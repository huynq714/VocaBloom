package com.vocabloom.app.data;
import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Source;
import com.google.android.gms.tasks.Task;
import java.util.*;
/** Firestore access for signed-in user data; SDK persistence provides the offline cache. */
public final class CloudSync {
 private CloudSync(){}
 public static boolean isConfigured(Context c){return FirebaseApp.initializeApp(c)!=null;}
 public static boolean signedIn(Context c){return isConfigured(c)&&FirebaseAuth.getInstance().getCurrentUser()!=null;}
 public static String uid(Context c){return signedIn(c)?FirebaseAuth.getInstance().getCurrentUser().getUid():null;}
 public static void syncUser(Context c,int goal){String user=uid(c);if(user==null)return;
  Map<String,Object> data=new HashMap<>();data.put("userId",user);data.put("dailyGoal",goal);data.put("updatedAt",new Date());
  FirebaseFirestore.getInstance().collection("users").document(user).set(data,SetOptions.merge());}
 public static void syncRating(Context c,String wordId,String rating){String user=uid(c);if(user==null)return;
  FirebaseFirestore db=FirebaseFirestore.getInstance();LocalProgress local=new LocalProgress(c);
  Map<String,Object> progress=local.progressDoc(wordId);progress.put("userId",user);
  Map<String,Object> log=new HashMap<>();log.put("userId",user);log.put("wordId",wordId);log.put("rating",rating);log.put("reviewedAt",new Date());log.put("levelAfter",local.level(wordId));
  WriteBatch batch=db.batch();batch.set(db.collection("userProgress").document(user+"_"+wordId),progress,SetOptions.merge());batch.set(db.collection("reviewLogs").document(),log);
  batch.commit().addOnSuccessListener(ignored->{if(local.hasMigrationMarker(user))local.clearLegacyLearningData();});
 }
 public static void rateWord(Context c,String wordId,String rating){String user=uid(c);if(user==null)return;
  FirebaseFirestore db=FirebaseFirestore.getInstance();DocumentReference ref=db.collection("userProgress").document(user+"_"+wordId);
  ref.get(Source.DEFAULT).addOnSuccessListener(current->{
   int old=current.contains("level")&&current.get("level") instanceof Number?((Number)current.get("level")).intValue():0;
   int next="FORGOT".equals(rating)?0:"HARD".equals(rating)?old:Math.min(5,old+1);
   long now=System.currentTimeMillis();long delay="FORGOT".equals(rating)?600000L:"HARD".equals(rating)?86400000L:new long[]{86400000L,86400000L,259200000L,604800000L,1209600000L,2592000000L}[next];
   Map<String,Object> progress=new HashMap<>();progress.put("userId",user);progress.put("wordId",wordId);progress.put("level",next);progress.put("nextReviewAt",new Date(now+delay));progress.put("lastReviewedAt",new Date(now));
   Map<String,Object> log=new HashMap<>();log.put("userId",user);log.put("wordId",wordId);log.put("rating",rating);log.put("reviewedAt",new Date(now));log.put("levelBefore",old);log.put("levelAfter",next);
   WriteBatch batch=db.batch();batch.set(ref,progress,SetOptions.merge());batch.set(db.collection("reviewLogs").document(),log);batch.commit();
  }).addOnFailureListener(error->{LocalProgress fallback=new LocalProgress(c);fallback.rate(wordId,rating);syncRating(c,wordId,rating);});
 }
 public static void setFavorite(Context c,String wordId,boolean favorite){String user=uid(c);if(user==null)return;
  DocumentReference ref=FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites").document(wordId);
  if(favorite){Map<String,Object> data=new HashMap<>();data.put("wordId",wordId);data.put("updatedAt",new Date());ref.set(data,SetOptions.merge());}else ref.delete();
 }
 public static void markFavoriteChanged(Context c){String user=uid(c);if(user==null)return;android.content.SharedPreferences state=c.getSharedPreferences("cloud_migrations",Context.MODE_PRIVATE);String key="favorite_changes_v1_"+user;state.edit().putLong(key,state.getLong(key,0)+1).apply();}
 public static void getFavorite(Context c,String wordId,boolean legacyFavorite,java.util.function.Consumer<Boolean> callback){String user=uid(c);if(user==null){callback.accept(false);return;}
  DocumentReference ref=FirebaseFirestore.getInstance().collection("users").document(user).collection("favorites").document(wordId);
  ref.get().addOnSuccessListener(doc->{boolean favorite=doc.exists();if(!doc.exists()&&legacyFavorite){setFavorite(c,wordId,true);favorite=true;}callback.accept(favorite);}).addOnFailureListener(error->callback.accept(legacyFavorite));
 }
 public static void migrateFavorites(Context c,String user){
  android.content.SharedPreferences scoped=c.getSharedPreferences("vocabloom_favorites_"+user,Context.MODE_PRIVATE);
  android.content.SharedPreferences state=c.getSharedPreferences("cloud_migrations",Context.MODE_PRIVATE);
  String marker="favorites_v1_"+user;if(state.getBoolean(marker,false))return;
  String changeKey="favorite_changes_v1_"+user;long changeVersion=state.getLong(changeKey,0);
  FirebaseFirestore db=FirebaseFirestore.getInstance();db.collection("users").document(user).collection("favorites").get(Source.SERVER).addOnSuccessListener(remote->{
   if(state.getLong(changeKey,0)!=changeVersion)return;
   Set<String> existing=new HashSet<>();for(com.google.firebase.firestore.DocumentSnapshot doc:remote.getDocuments())existing.add(doc.getId());
   Map<String,Boolean> desired=new HashMap<>();for(Map.Entry<String,?> entry:scoped.getAll().entrySet())if(entry.getValue() instanceof Boolean)desired.put(entry.getKey(),(Boolean)entry.getValue());
   WriteBatch batch=db.batch();int writes=0;
   for(Map.Entry<String,Boolean> entry:desired.entrySet()){String wordId=entry.getKey();DocumentReference ref=db.collection("users").document(user).collection("favorites").document(wordId);if(entry.getValue()&&!existing.contains(wordId)){Map<String,Object> item=new HashMap<>();item.put("wordId",wordId);item.put("updatedAt",new Date());batch.set(ref,item,SetOptions.merge());writes++;}else if(!entry.getValue()&&existing.contains(wordId)){batch.delete(ref);writes++;}}
   if(writes==0){scoped.edit().clear().apply();state.edit().putBoolean(marker,true).apply();}
   else batch.commit().addOnSuccessListener(done->{scoped.edit().clear().apply();state.edit().putBoolean(marker,true).apply();});
  });
 }
 public static void syncReminderSettings(Context c,boolean enabled,int hour,int minute){String user=uid(c);if(user==null)return;
  Map<String,Object> settings=new HashMap<>();settings.put("studyReminderEnabled",enabled);settings.put("studyReminderHour",hour);settings.put("studyReminderMinute",minute);settings.put("reminderUpdatedAt",new Date());
  FirebaseFirestore.getInstance().collection("users").document(user).set(settings,SetOptions.merge());
 }
 public static Task<com.google.firebase.firestore.DocumentSnapshot> loadUser(String uid){return FirebaseFirestore.getInstance().collection("users").document(uid).get(Source.DEFAULT);}
}
