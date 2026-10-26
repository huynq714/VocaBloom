package com.vocabloom.app.data;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Cloud source for per-word learning state. Firestore's Android cache serves offline reads/writes. */
public final class CloudProgressStore {
    public interface ProgressListener { void onProgress(ProgressSnapshot progress); }
    private static final int BATCH_LIMIT = 450;
    private CloudProgressStore() { }

    public static ListenerRegistration listen(Context context, ProgressListener listener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() == null ? null : FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) return null;
        Context appContext = context.getApplicationContext();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final boolean[] migrationStarted = {false};
        final boolean[] favoritesMigrationStarted = {false};
        final QuerySnapshot[] progressResult = {null}, logResult = {null};
        Runnable publish = () -> {
            if (progressResult[0] != null && logResult[0] != null) {
                ProgressSnapshot snapshot = ProgressSnapshot.fromFirestore(progressResult[0], logResult[0]);
                LocalProgress local = new LocalProgress(appContext, uid);
                listener.onProgress(local.hasMigrationMarker(uid) ? snapshot : ProgressSnapshot.mergeLegacy(local, snapshot));
            }
        };
        ListenerRegistration progressListener = db.collection("userProgress").whereEqualTo("userId", uid).addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) return;
            progressResult[0] = snapshot; publish.run();
            // Never infer that the cloud is empty from a cache-only snapshot; wait for server confirmation.
            if (!snapshot.getMetadata().isFromCache()) {
                if (!favoritesMigrationStarted[0]) { favoritesMigrationStarted[0] = true; CloudSync.migrateFavorites(appContext, uid); }
                if (!migrationStarted[0]) { migrationStarted[0] = true; migrateLegacy(appContext, uid, snapshot); }
            }
        });
        ListenerRegistration logListener = db.collection("reviewLogs").whereEqualTo("userId", uid).addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) return; logResult[0] = snapshot; publish.run();
        });
        return () -> { progressListener.remove(); logListener.remove(); };
    }

    private static void migrateLegacy(Context context, String uid, QuerySnapshot cloudSnapshot) {
        FirebaseFirestore.getInstance().collection("reviewLogs").whereEqualTo("userId", uid).get(Source.SERVER)
                .addOnSuccessListener(logSnapshot -> applyLegacyMigration(context, uid, cloudSnapshot, logSnapshot));
    }

    private static void applyLegacyMigration(Context context, String uid, QuerySnapshot cloudSnapshot, QuerySnapshot cloudLogs) {
        LocalProgress local = new LocalProgress(context, uid);
        if (local.hasMigrationMarker(uid)) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, DocumentSnapshot> remote = new HashMap<>();
        for (DocumentSnapshot doc : cloudSnapshot.getDocuments()) {
            String wordId = doc.getString("wordId");
            if (wordId != null) remote.put(wordId, doc);
        }
        List<WriteOp> writes = new ArrayList<>();
        for (String wordId : local.trackedWordIds()) {
            long localReviewed = local.reviewedAt(wordId);
            if (localReviewed <= 0) continue;
            DocumentSnapshot cloudDoc = remote.get(wordId);
            Date cloudReviewed = cloudDoc == null ? null : cloudDoc.getDate("lastReviewedAt");
            if (cloudDoc == null || cloudReviewed == null || localReviewed > cloudReviewed.getTime()) {
                Map<String, Object> data = local.progressDoc(wordId);
                data.put("userId", uid);
                writes.add(batch -> batch.set(db.collection("userProgress").document(uid + "_" + wordId), data, SetOptions.merge()));
            }
        }
        JSONArray logs = local.logs();
        Set<String> matchedCloudLogs = new HashSet<>();
        for (int i = 0; i < logs.length(); i++) {
            JSONObject entry = logs.optJSONObject(i);
            if (entry == null) continue;
            if (alreadySynced(entry, cloudLogs, matchedCloudLogs)) continue;
            final int index = i;
            Map<String, Object> log = new HashMap<>();
            log.put("userId", uid);
            log.put("wordId", entry.optString("wordId", ""));
            log.put("rating", entry.optString("rating", ""));
            log.put("reviewedAt", new Date(entry.optLong("reviewedAtMillis", 0)));
            log.put("levelBefore", entry.optInt("levelBefore", 0));
            log.put("levelAfter", entry.optInt("levelAfter", 0));
            log.put("migrationSource", "local_v1");
            writes.add(batch -> batch.set(db.collection("reviewLogs").document("migration_" + uid + "_" + index), log, SetOptions.merge()));
        }
        commitChunk(context, uid, writes, 0);
    }

    private static boolean alreadySynced(JSONObject local, QuerySnapshot cloudLogs, Set<String> consumed) {
        String wordId = local.optString("wordId", ""), rating = local.optString("rating", "");
        long localTime = local.optLong("reviewedAtMillis", 0);
        for (DocumentSnapshot doc : cloudLogs.getDocuments()) {
            if (consumed.contains(doc.getId())) continue;
            if (!wordId.equals(doc.getString("wordId")) || !rating.equals(doc.getString("rating"))) continue;
            Date cloudTime = doc.getDate("reviewedAt");
            if (cloudTime != null && Math.abs(cloudTime.getTime() - localTime) < 10_000L) { consumed.add(doc.getId()); return true; }
        }
        return false;
    }

    private interface WriteOp { void add(WriteBatch batch); }
    private static void commitChunk(Context context, String uid, List<WriteOp> writes, int offset) {
        if (offset >= writes.size()) {
            LocalProgress local = new LocalProgress(context, uid); local.setMigrationMarker(uid); local.clearLegacyLearningData();
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        int end = Math.min(offset + BATCH_LIMIT, writes.size());
        for (int i = offset; i < end; i++) writes.get(i).add(batch);
        batch.commit().addOnSuccessListener(ignored -> commitChunk(context, uid, writes, end));
        // On failure keep the migration marker unset; next online session retries the idempotent writes.
    }
}
