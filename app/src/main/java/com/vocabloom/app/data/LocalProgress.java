package com.vocabloom.app.data;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;
/** Functional offline practice storage; no Internet required. */
public final class LocalProgress {
 private final SharedPreferences p;
 private static final long DAY=86400000L;
 public LocalProgress(Context c){this(c,activeUid());}
 public LocalProgress(Context c,String uid){p=c.getSharedPreferences("vocabloom_progress_v3_"+(uid==null?"guest":uid),Context.MODE_PRIVATE);}
 private static String activeUid(){try {com.google.firebase.auth.FirebaseAuth a=com.google.firebase.auth.FirebaseAuth.getInstance();if(a.getCurrentUser()!=null)return a.getCurrentUser().getUid();}catch(Exception ignored){}return "guest";}
 public int getGoal(){return p.getInt("daily_goal",10);}
 public void setGoal(int goal){p.edit().putInt("daily_goal",goal).apply();}
 public int learnedCount(){int n=0;for(String k:p.getAll().keySet())if(k.startsWith("level_")&&p.getInt(k,0)>0)n++;return n;}
 public int masteredCount(){int n=0;for(String k:p.getAll().keySet())if(k.startsWith("level_")&&p.getInt(k,0)>=5)n++;return n;}
 public int dueCount(){return dueWordIds().size();}
 public List<String> dueWordIds(){List<String> ids=new ArrayList<>();for(String k:p.getAll().keySet())if(k.startsWith("due_")&&p.getLong(k,Long.MAX_VALUE)<=System.currentTimeMillis())ids.add(k.substring(4)); Collections.sort(ids);return ids;}
 public int dailyCount(){return p.getInt("daily_"+today(),0);}
 public int reviewCount(){return p.getInt("total_reviews",0);}
 public int countRating(String r){return p.getInt("rating_"+r,0);}
 public int streakDays(){
  Set<String> studied=new HashSet<>();
  try{JSONArray entries=logs();SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd",Locale.US);for(int i=0;i<entries.length();i++){JSONObject entry=entries.optJSONObject(i);if(entry!=null){long time=entry.optLong("reviewedAtMillis",0);if(time>0)studied.add(format.format(new Date(time)));}}}catch(Exception ignored){}
  Calendar day=Calendar.getInstance();String today=new SimpleDateFormat("yyyyMMdd",Locale.US).format(day.getTime());if(!studied.contains(today))day.add(Calendar.DAY_OF_YEAR,-1);
  int streak=0;while(studied.contains(new SimpleDateFormat("yyyyMMdd",Locale.US).format(day.getTime()))){streak++;day.add(Calendar.DAY_OF_YEAR,-1);}return streak;
 }
 public int level(String id){return p.getInt("level_"+id,0);}
 public boolean hasReviewed(String id){return p.contains("reviewed_"+id);}
 public long due(String id){return p.getLong("due_"+id,0L);}
 public long reviewedAt(String id){return p.getLong("reviewed_"+id,0L);}
 public List<String> trackedWordIds(){List<String> ids=new ArrayList<>();for(String k:p.getAll().keySet())if(k.startsWith("level_"))ids.add(k.substring(6));Collections.sort(ids);return ids;}
 public boolean hasMigrationMarker(String uid){return p.getBoolean("firestore_migration_v1_"+uid,false);}
 public void setMigrationMarker(String uid){p.edit().putBoolean("firestore_migration_v1_"+uid,true).apply();}
 public void clearLegacyLearningData(){SharedPreferences.Editor edit=p.edit();for(String k:p.getAll().keySet())if(k.equals("logs")||k.equals("total_reviews")||k.startsWith("level_")||k.startsWith("due_")||k.startsWith("reviewed_")||k.startsWith("daily_")||k.startsWith("rating_"))edit.remove(k);edit.apply();}
 private String today(){return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date());}
 public synchronized void rate(String id,String rating){
   int old=level(id);int level=rating.equals("FORGOT")?0:rating.equals("HARD")?old:Math.min(5,old+1);
   long delay=rating.equals("FORGOT")?600000L:rating.equals("HARD")?DAY:new long[]{DAY,DAY,3*DAY,7*DAY,14*DAY,30*DAY}[level];
   long now=System.currentTimeMillis();
   p.edit().putInt("level_"+id,level).putLong("due_"+id,now+delay).putInt("total_reviews",reviewCount()+1)
    .putInt("rating_"+rating,countRating(rating)+1).putInt("daily_"+today(),dailyCount()+1)
    .putLong("reviewed_"+id,now).apply();
   // Append-only log in private storage, for offline statistics and backup.
   try { JSONArray logs=new JSONArray(p.getString("logs","[]"));JSONObject log=new JSONObject();log.put("wordId",id);log.put("rating",rating);log.put("levelBefore",old);log.put("levelAfter",level);log.put("reviewedAtMillis",now);logs.put(log);p.edit().putString("logs",logs.toString()).apply();} catch(Exception ignored){}
 }
 public JSONArray logs(){try{return new JSONArray(p.getString("logs","[]"));}catch(Exception e){return new JSONArray();}}
 public Map<String,Object> progressDoc(String id){Map<String,Object> doc=new HashMap<>();doc.put("wordId",id);doc.put("level",level(id));doc.put("nextReviewAt",new Date(due(id)));doc.put("lastReviewedAt",new Date(p.getLong("reviewed_"+id,System.currentTimeMillis())));return doc;}
}
