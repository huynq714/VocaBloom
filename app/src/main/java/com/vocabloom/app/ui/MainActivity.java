package com.vocabloom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabloom.app.R;
import com.vocabloom.app.data.CloudSync;
import com.vocabloom.app.data.LocalProgress;
import com.vocabloom.app.data.CloudProgressStore;
import com.vocabloom.app.data.ProgressSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.vocabloom.app.data.SampleData;
import com.vocabloom.app.model.Word;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private String displayName = "";
    private ProgressSnapshot cloudProgress;
    private ListenerRegistration progressListener;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!CloudSync.signedIn(this)) {
            startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Nav.setup(this, 0);
        FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null) displayName = user.getDisplayName().trim();
        updateHeader();
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) textToSpeech.setLanguage(Locale.US);
        });
        findViewById(R.id.topicsButton).setOnClickListener(v -> startActivity(new Intent(this, TopicsActivity.class)));
        findViewById(R.id.changeDeck).setOnClickListener(v -> startActivity(new Intent(this, TopicsActivity.class)));
        findViewById(R.id.profileShortcut).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.nextLesson).setOnClickListener(v -> openDailyLife());
        findViewById(R.id.deckTitle).setOnClickListener(v -> openDailyLife());
        findViewById(R.id.dailyWordAudio).setOnClickListener(v -> {
            if (textToSpeech != null) textToSpeech.speak(((TextView) findViewById(R.id.dailyWord)).getText(), TextToSpeech.QUEUE_FLUSH, null, "daily-word");
        });
        findViewById(R.id.notificationButton).setOnClickListener(v -> Toast.makeText(this, "Bạn không có thông báo mới.", Toast.LENGTH_SHORT).show());
        loadCloudProfile();
        cloudProgress = ProgressSnapshot.fromLocal(new LocalProgress(this));
        progressListener = CloudProgressStore.listen(this, snapshot -> { cloudProgress = snapshot; refreshProgress(snapshot); });
    }

    private void updateHeader() {
        ((TextView) findViewById(R.id.homeGreeting)).setText(displayName.isEmpty() ? "Chào buổi sáng!" : "Chào buổi sáng, " + displayName + "!");
        ((TextView) findViewById(R.id.profileShortcut)).setText(displayName.isEmpty() ? "M" : displayName.substring(0, 1).toUpperCase(Locale.getDefault()));
        ((TextView) findViewById(R.id.homeDate)).setText(new SimpleDateFormat("EEEE, d MMMM", new Locale("vi", "VN")).format(new Date()));
    }

    private void loadCloudProfile() {
        FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;
            String name = doc.getString("fullName");
            if (name != null && !name.trim().isEmpty()) { displayName = name.trim(); updateHeader(); }
            Long goal = doc.getLong("dailyGoal");
            if (goal != null) new LocalProgress(this).setGoal(Math.max(1, Math.min(100, goal.intValue())));
            refreshProgress(cloudProgress == null ? ProgressSnapshot.fromLocal(new LocalProgress(this)) : cloudProgress);
        });
    }

    private void openDailyLife() {
        startActivity(new Intent(this, FlashcardActivity.class).putExtra("topic", SampleData.TOPICS[2]));
    }

    @Override protected void onResume() { super.onResume(); refreshProgress(cloudProgress == null ? ProgressSnapshot.fromLocal(new LocalProgress(this)) : cloudProgress); }

    private void refreshProgress(ProgressSnapshot progress) {
        ((TextView) findViewById(R.id.streakBadge)).setText("🔥 " + progress.streakDays() + " ngày");
        int daily = progress.dailyCount(), goal = new LocalProgress(this).getGoal();
        ((TextView) findViewById(R.id.dailyLearned)).setText(String.valueOf(Math.min(goal, daily)));
        ((TextView) findViewById(R.id.dailyCount)).setText("/ " + goal + " từ mới");
        ((TextView) findViewById(R.id.dailyPercent)).setText(Math.min(100, daily * 100 / Math.max(1, goal)) + "%");
        ((TextView) findViewById(R.id.dailySubtitle)).setText(daily >= goal ? "Tuyệt vời! Bạn đã hoàn thành mục tiêu hôm nay." : "Mỗi lần học là một bước tiến nhỏ.");
        ProgressBar dailyBar = findViewById(R.id.dailyProgress);
        dailyBar.setMax(goal);
        dailyBar.setProgress(Math.min(daily, goal));

        List<Word> words = SampleData.byTopic(SampleData.TOPICS[2]);
        int learned = 0;
        for (Word word : words) if (progress.level(word.id) > 0) learned++;
        int percent = words.isEmpty() ? 0 : learned * 100 / words.size();
        ProgressBar deckBar = findViewById(R.id.deckProgress);
        deckBar.setMax(100);
        deckBar.setProgress(percent);
        ((TextView) findViewById(R.id.deckProgressText)).setText(percent + "% (" + learned + " / " + words.size() + " từ)");

        Word word = SampleData.byId("w021");
        if (word != null) {
            ((TextView) findViewById(R.id.dailyWord)).setText("Resilience");
            ((TextView) findViewById(R.id.dailyWordPhonetic)).setText("/rɪˈzɪl.jəns/");
            ((TextView) findViewById(R.id.dailyWordMeaning)).setText("Khả năng phục hồi, kiên cường vượt qua nghịch cảnh.");
            ((TextView) findViewById(R.id.dailyWordPart)).setText("danh từ");
        }
    }

    @Override protected void onDestroy() {
        if (progressListener != null) progressListener.remove();
        if (textToSpeech != null) { textToSpeech.stop(); textToSpeech.shutdown(); }
        super.onDestroy();
    }
}
