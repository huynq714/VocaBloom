package com.vocabloom.app.ui;
import android.content.Intent;import android.os.Bundle;import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;import com.vocabloom.app.R;
public class SessionResultActivity extends AppCompatActivity {
 @Override public void onCreate(Bundle b){super.onCreate(b);if(!com.vocabloom.app.data.CloudSync.signedIn(this)){startActivity(new android.content.Intent(this,AuthActivity.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK|android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();return;}setContentView(R.layout.activity_result);
  int count=getIntent().getIntExtra("count",0);((TextView)findViewById(R.id.doneCount)).setText(count+" thẻ từ đã hoàn thành");
  findViewById(R.id.goStats).setOnClickListener(v->{startActivity(new Intent(this,StatisticsActivity.class));finish();});
  findViewById(R.id.goHome).setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish();});
 }
}
