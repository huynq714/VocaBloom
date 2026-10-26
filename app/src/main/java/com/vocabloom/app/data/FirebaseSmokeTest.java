package com.vocabloom.app.data;
import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;
/** User-owned Firestore write/read test, compatible with supplied Security Rules. */
public final class FirebaseSmokeTest {
 public interface Callback {void done(String message);}
 private FirebaseSmokeTest(){}
 public static void run(Context context,Callback callback){
  if(FirebaseApp.initializeApp(context)==null){callback.done("Chưa có google-services.json trong thư mục app.");return;}
  FirebaseAuth auth=FirebaseAuth.getInstance();
  if(auth.getCurrentUser()==null){callback.done("Bạn cần đăng nhập email trước khi thử Firebase.");return;}
  test(auth.getCurrentUser().getUid(),callback);
 }
 private static void test(String uid,Callback cb){
  FirebaseFirestore db=FirebaseFirestore.getInstance();Map<String,Object> m=new HashMap<>();m.put("userId",uid);m.put("dailyGoal",10);m.put("updatedAt",new Date());
  db.collection("users").document(uid).set(m,com.google.firebase.firestore.SetOptions.merge()).addOnSuccessListener(v->
   db.collection("users").document(uid).get().addOnSuccessListener(s->cb.done(s.exists()?"Firebase kết nối thành công. UID: "+uid.substring(0,Math.min(8,uid.length()))+"...":"Đọc document không thành công."))
   .addOnFailureListener(e->cb.done("Lỗi đọc: "+e.getMessage())))
   .addOnFailureListener(e->cb.done("Lỗi ghi: "+e.getMessage()));
 }
}
