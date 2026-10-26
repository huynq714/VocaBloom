package com.vocabloom.app.data;

import com.vocabloom.app.data.SampleData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Immutable view of a user's Firestore progress and review history. */
public final class ProgressSnapshot {
    private final Map<String, Map<String, Object>> progress;
    private final List<Map<String, Object>> logs;

    public ProgressSnapshot(Map<String, Map<String, Object>> progress, List<Map<String, Object>> logs) {
        this.progress = progress == null ? new HashMap<>() : progress;
        this.logs = logs == null ? new ArrayList<>() : logs;
    }

    public static ProgressSnapshot fromLocal(LocalProgress local) {
        Map<String, Map<String, Object>> progress = new HashMap<>();
        for (String id : local.trackedWordIds()) {
            Map<String, Object> item = new HashMap<>(); item.put("wordId", id); item.put("level", local.level(id));
            item.put("nextReviewAt", new Date(local.due(id))); item.put("lastReviewedAt", new Date(local.reviewedAt(id)));
            progress.put(id, item);
        }
        List<Map<String, Object>> logs = new ArrayList<>();
        org.json.JSONArray entries = local.logs();
        for (int i=0;i<entries.length();i++) {
            org.json.JSONObject e=entries.optJSONObject(i); if(e==null) continue;
            Map<String,Object> item=new HashMap<>(); item.put("wordId",e.optString("wordId")); item.put("rating",e.optString("rating"));
            item.put("levelBefore",e.optInt("levelBefore")); item.put("levelAfter",e.optInt("levelAfter")); item.put("reviewedAt",new Date(e.optLong("reviewedAtMillis",0))); logs.add(item);
        }
        return new ProgressSnapshot(progress, logs);
    }

    public int level(String id) { Object value = progress.containsKey(id) ? progress.get(id).get("level") : null; return value instanceof Number ? ((Number)value).intValue() : 0; }
    public boolean hasReviewed(String id) { Object date=progress.containsKey(id)?progress.get(id).get("lastReviewedAt"):null; if(date instanceof Date && ((Date)date).getTime()>0)return true; for(Map<String,Object> log:logs)if(id.equals(log.get("wordId")))return true; return false; }
    public int learnedCount() { int count=0; for(Map<String,Object> item:progress.values()){Object v=item.get("level");if(v instanceof Number&&((Number)v).intValue()>0)count++;} return count; }
    public List<String> dueWordIds() { List<String> ids=new ArrayList<>();long now=System.currentTimeMillis();for(Map.Entry<String,Map<String,Object>> e:progress.entrySet()){Object due=e.getValue().get("nextReviewAt");if(due instanceof Date&&((Date)due).getTime()<=now)ids.add(e.getKey());}java.util.Collections.sort(ids);return ids; }
    public int dailyCount() { String today=day(System.currentTimeMillis());int n=0;for(Map<String,Object> log:logs){Object d=log.get("reviewedAt");if(d instanceof Date&&day(((Date)d).getTime()).equals(today))n++;}return n; }
    public int streakDays() { Set<String> days=new HashSet<>();for(Map<String,Object> log:logs){Object d=log.get("reviewedAt");if(d instanceof Date)days.add(day(((Date)d).getTime()));}java.util.Calendar c=java.util.Calendar.getInstance();String today=day(c.getTimeInMillis());if(!days.contains(today))c.add(java.util.Calendar.DAY_OF_YEAR,-1);int n=0;while(days.contains(day(c.getTimeInMillis()))){n++;c.add(java.util.Calendar.DAY_OF_YEAR,-1);}return n; }
    private String day(long millis){return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date(millis));}
    public static ProgressSnapshot fromFirestore(QuerySnapshot progressSnapshot, QuerySnapshot logSnapshot) {
        Map<String,Map<String,Object>> progress=new HashMap<>();List<Map<String,Object>> logs=new ArrayList<>();
        if(progressSnapshot!=null)for(DocumentSnapshot doc:progressSnapshot.getDocuments()){
            String id=doc.getString("wordId");if(id!=null){Map<String,Object> item=doc.getData()==null?new HashMap<>():new HashMap<>(doc.getData());item.put("nextReviewAt",doc.getDate("nextReviewAt"));item.put("lastReviewedAt",doc.getDate("lastReviewedAt"));progress.put(id,item);}
        }
        if(logSnapshot!=null)for(DocumentSnapshot doc:logSnapshot.getDocuments())if(doc.getData()!=null){Map<String,Object> item=new HashMap<>(doc.getData());item.put("reviewedAt",doc.getDate("reviewedAt"));logs.add(item);}
        return new ProgressSnapshot(progress,logs);
    }
    public static ProgressSnapshot mergeLegacy(LocalProgress local, ProgressSnapshot cloud) {
        Map<String,Map<String,Object>> merged=new HashMap<>(cloud.progress);
        for(String id:local.trackedWordIds()){
            long localTime=local.reviewedAt(id);if(localTime<=0)continue;
            Map<String,Object> existing=merged.get(id);Object last=existing==null?null:existing.get("lastReviewedAt");long cloudTime=last instanceof Date?((Date)last).getTime():0;
            if(existing==null||localTime>cloudTime){Map<String,Object> item=local.progressDoc(id);merged.put(id,item);}
        }
        List<Map<String,Object>> mergedLogs=new ArrayList<>(cloud.logs);
        org.json.JSONArray localLogs=local.logs();
        for(int i=0;i<localLogs.length();i++){
            org.json.JSONObject e=localLogs.optJSONObject(i);if(e==null)continue;long time=e.optLong("reviewedAtMillis",0);String word=e.optString("wordId"),rating=e.optString("rating");boolean duplicate=false;
            for(Map<String,Object> remote:cloud.logs){Object date=remote.get("reviewedAt");if(word.equals(remote.get("wordId"))&&rating.equals(remote.get("rating"))&&date instanceof Date&&Math.abs(((Date)date).getTime()-time)<10000L){duplicate=true;break;}}
            if(!duplicate){Map<String,Object> item=new HashMap<>();item.put("wordId",word);item.put("rating",rating);item.put("levelBefore",e.optInt("levelBefore"));item.put("levelAfter",e.optInt("levelAfter"));item.put("reviewedAt",new Date(time));mergedLogs.add(item);}
        }
        return new ProgressSnapshot(merged,mergedLogs);
    }
    public Map<String,Map<String,Object>> progressMap(){return progress;}
    public List<Map<String,Object>> logs(){return logs;}
}
