package com.vocabloom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.vocabloom.app.R;
import com.vocabloom.app.data.CloudSync;
import com.vocabloom.app.data.CloudProgressStore;
import com.google.firebase.firestore.ListenerRegistration;
import com.vocabloom.app.data.SampleData;
import com.vocabloom.app.model.Word;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlashcardActivity extends AppCompatActivity {
    private final List<Word> words = new ArrayList<>();
    private int index;
    private boolean flipped, review, busy, favorite;
    private TextToSpeech textToSpeech;
    private String topic;
    private ListenerRegistration progressListener;
    private boolean sessionInitialized;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!CloudSync.signedIn(this)) { startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); return; }
        setContentView(R.layout.activity_flashcard);
        String singleWordId = getIntent().getStringExtra("wordId");
        review = getIntent().getBooleanExtra("review", false) || singleWordId != null; topic = getIntent().getStringExtra("topic");
        if (singleWordId != null) { Word word = SampleData.byId(singleWordId); if (word != null) words.add(word); }
        else if (review) {
            progressListener = CloudProgressStore.listen(this, snapshot -> {
                if (sessionInitialized) return;
                if (progressListener != null) { progressListener.remove(); progressListener = null; }
                for (String id : snapshot.dueWordIds()) { Word word = SampleData.byId(id); if (word != null) words.add(word); }
                initializeSession();
            });
            return;
        } else words.addAll(SampleData.byTopic(topic == null ? SampleData.TOPICS[0] : topic));
        initializeSession();
    }

    private void initializeSession() {
        if (sessionInitialized) return;
        sessionInitialized = true;
        if (words.isEmpty()) { Toast.makeText(this, review ? "Chưa có từ đến hạn ôn." : "Bộ từ này chưa có dữ liệu.", Toast.LENGTH_SHORT).show(); finish(); return; }
        textToSpeech = new TextToSpeech(this, status -> { if (status == TextToSpeech.SUCCESS) textToSpeech.setLanguage(Locale.US); });
        loadFavorite(words.get(0));
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.card).setOnClickListener(v -> flip());
        findViewById(R.id.cardAudio).setOnClickListener(v -> speak());
        findViewById(R.id.cardStar).setOnClickListener(v -> toggleFavorite());
        findViewById(R.id.forgot).setOnClickListener(v -> rate("FORGOT"));
        findViewById(R.id.hard).setOnClickListener(v -> rate("HARD"));
        findViewById(R.id.remember).setOnClickListener(v -> rate("REMEMBER"));
        findViewById(R.id.forgot).setEnabled(false); findViewById(R.id.hard).setEnabled(false); findViewById(R.id.remember).setEnabled(false);
        show();
    }

    private void flip() { if (busy) return; flipped = !flipped; show(); }
    private void show() {
        Word word = words.get(index);
        String label = review ? "Ôn tập hôm nay" : "Chủ đề: " + (topic == null ? SampleData.TOPICS[0] : topic);
        ((TextView) findViewById(R.id.sessionLabel)).setText(label);
        ((TextView) findViewById(R.id.counter)).setText((index + 1) + " / " + words.size() + " từ");
        ((TextView) findViewById(R.id.word)).setText(word.english);
        ((TextView) findViewById(R.id.phonetic)).setText(cleanPhonetic(word.phonetic));
        ((TextView) findViewById(R.id.meaning)).setText(word.meaning);
        ((TextView) findViewById(R.id.example)).setText("“" + word.example + "”");
        ((TextView) findViewById(R.id.partOfSpeech)).setText("Từ vựng • Vocabulary");
        ((TextView) findViewById(R.id.flipHint)).setText(flipped ? "☞ Chạm để xem từ" : "☞ Chạm để lật");
        ((TextView) findViewById(R.id.synonyms)).setText(flipped ? "Ghi nhớ • Nghĩa tiếng Việt" : "Gợi ý ghi nhớ • Chạm để xem nghĩa");
        findViewById(R.id.meaning).setVisibility(flipped ? View.VISIBLE : View.GONE);
        findViewById(R.id.example).setVisibility(flipped ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.cardStar)).setText(favorite ? "★" : "☆");
        ProgressBar bar = findViewById(R.id.sessionProgress); bar.setMax(words.size()); bar.setProgress(index);
        findViewById(R.id.forgot).setEnabled(flipped && !busy); findViewById(R.id.hard).setEnabled(flipped && !busy); findViewById(R.id.remember).setEnabled(flipped && !busy);
    }
    private String cleanPhonetic(String input) {
        if (input == null) return "";
        return input.replace("Ëˆ", "ˈ").replace("Ë", "ː").replace("É™", "ə").replace("Éª", "ɪ").replace("Êƒ", "ʃ").replace("É›", "ɛ").replace("É¡", "ɡ").replace("áº¨", "ʌ").replace("Å“", "œ").replace("É”", "ɔ").replace("Âː", "ː").replace("Â", "");
    }
    private void speak() { if (textToSpeech != null) textToSpeech.speak(words.get(index).english, TextToSpeech.QUEUE_FLUSH, null, "flashcard-word"); }
    private void toggleFavorite() {
        favorite = !favorite;
        Word word = words.get(index); boolean value = favorite;
        favoriteCache().edit().putBoolean(word.id, value).apply();
        CloudSync.markFavoriteChanged(this);
        CloudSync.setFavorite(this, word.id, value);
        show(); Toast.makeText(this, favorite ? "Đã lưu từ yêu thích" : "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
    }
    private void loadFavorite(Word word) {
        boolean legacy = favoriteCache().getBoolean(word.id, false);
        favorite = legacy; show();
        CloudSync.getFavorite(this, word.id, legacy, value -> {
            if (!isFinishing() && words.get(index).id.equals(word.id)) {
                favorite = value;
                show();
            }
        });
    }

    private android.content.SharedPreferences favoriteCache() {
        String uid = CloudSync.uid(this);
        return getSharedPreferences("vocabloom_favorites_" + (uid == null ? "guest" : uid), MODE_PRIVATE);
    }

    private void rate(String rating) {
        if (!flipped || busy) return; busy = true; Word word = words.get(index);
        CloudSync.rateWord(this, word.id, rating);
        if (index == words.size() - 1) { startActivity(new Intent(this, SessionResultActivity.class).putExtra("count", words.size()).putExtra("review", review)); finish(); return; }
        index++; flipped = false; busy = false;
        loadFavorite(words.get(index));
    }
    @Override protected void onDestroy() { if (progressListener != null) progressListener.remove(); if (textToSpeech != null) { textToSpeech.stop(); textToSpeech.shutdown(); } super.onDestroy(); }
}
