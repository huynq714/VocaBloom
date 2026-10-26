package com.vocabloom.app.ui;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.widget.TextView;
import com.vocabloom.app.R;
public final class Nav {
 private Nav(){}
 public static void setup(Activity a,int selected){
  int[] ids={R.id.navHome,R.id.navTopics,R.id.navStats,R.id.navProfile};
  int[] icons={R.drawable.ic_nav_home,R.drawable.ic_nav_book,R.drawable.ic_nav_stats,R.drawable.ic_nav_profile};
  String[] labels={"Trang chủ","Học từ","Tiến độ","Hồ sơ"};
  Class<?>[] pages={MainActivity.class,TopicsActivity.class,StatisticsActivity.class,ProfileActivity.class};
  for(int i=0;i<ids.length;i++){final int pos=i;TextView v=a.findViewById(ids[i]);if(v==null)continue;
   v.setTextColor(a.getColor(i==selected?R.color.on_primary_fixed:R.color.muted));
   v.setTypeface(null,i==selected?Typeface.BOLD:Typeface.NORMAL);
   v.setBackgroundResource(i==selected?R.drawable.stitch_pill:android.R.color.transparent);
   v.setText(labels[i]);
   android.graphics.drawable.Drawable icon=a.getDrawable(icons[i]);
   if(icon!=null){icon=icon.mutate();icon.setTint(a.getColor(i==selected?R.color.on_primary_fixed:R.color.muted));v.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);}
   v.setCompoundDrawablePadding(a.getResources().getDimensionPixelSize(R.dimen.nav_icon_gap));
   v.setPadding(a.getResources().getDimensionPixelSize(R.dimen.nav_hpad),i==selected?3:0,a.getResources().getDimensionPixelSize(R.dimen.nav_hpad),i==selected?3:0);
   v.setOnClickListener(view->{if(pos==selected)return;Intent intent=new Intent(a,pages[pos]);intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);a.startActivity(intent);a.overridePendingTransition(0,0);});
  }
 }
}
