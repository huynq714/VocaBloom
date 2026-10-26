package com.vocabloom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.vocabloom.app.R;
import com.vocabloom.app.data.LocalProgress;
import com.vocabloom.app.data.CloudProgressStore;
import com.vocabloom.app.data.ProgressSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.vocabloom.app.data.SampleData;
import com.vocabloom.app.model.Word;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    private android.graphics.Typeface topicTypeface;
    private ProgressSnapshot cloudProgress;
    private ListenerRegistration progressListener;
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!com.vocabloom.app.data.CloudSync.signedIn(this)) { startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); return; }
        setContentView(R.layout.activity_statistics); Nav.setup(this, 2); findViewById(R.id.statsBack).setOnClickListener(v -> finish());
        progressListener = CloudProgressStore.listen(this, snapshot -> { cloudProgress = snapshot; render(snapshot); });
    }
    @Override public void onResume() { super.onResume(); render(cloudProgress == null ? ProgressSnapshot.fromLocal(new LocalProgress(this)) : cloudProgress); }
    @Override protected void onDestroy() { if (progressListener != null) progressListener.remove(); super.onDestroy(); }

    private void render(ProgressSnapshot progress) {
        List<Word> all = SampleData.all(); int total = all.size(), learned = progress.learnedCount();
        int percent = total == 0 ? 0 : Math.min(100, learned * 100 / total);
        ((TextView) findViewById(R.id.statWords)).setText(String.valueOf(learned));
        ((TextView) findViewById(R.id.statTotalLabel)).setText(" / " + total + " từ");
        ((TextView) findViewById(R.id.overallPercent)).setText(percent + "%");
        ProgressBar overall = findViewById(R.id.overallProgress); overall.setProgress(percent);
        ((TextView) findViewById(R.id.statWordsMetric)).setText(learned + " từ");
        ((TextView) findViewById(R.id.statStreakMetric)).setText(progress.streakDays() + " ngày");
        ((TextView) findViewById(R.id.statsStreak)).setText("🔥 " + progress.streakDays() + " ngày");
        int notLearned = 0;
        for (Word word : all) if (progress.level(word.id) == 0) notLearned++;
        ((TextView) findViewById(R.id.statTimeMetric)).setText(String.valueOf(notLearned));
        LinearLayout holder = findViewById(R.id.topicProgressList); holder.removeAllViews();
        for (int i = 0; i < SampleData.TOPICS.length; i++) {
            String topic = SampleData.TOPICS[i]; List<Word> words = SampleData.byTopic(topic); int count = 0;
            for (Word word : words) if (progress.level(word.id) > 0) count++;
            int ratio = words.isEmpty() ? 0 : count * 100 / words.size();
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(14), dp(12), dp(14), dp(12)); card.setBackgroundResource(R.drawable.stitch_card); card.setElevation(dp(1));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.bottomMargin = dp(10); holder.addView(card, cp);
            LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); card.addView(header, new LinearLayout.LayoutParams(-1, -2));
            TextView icon = text(topicIcon(topic), R.color.accent, 20, true); icon.setGravity(Gravity.CENTER); icon.setBackgroundResource(R.drawable.stitch_soft); header.addView(icon, new LinearLayout.LayoutParams(dp(38), dp(38)));
            LinearLayout names = new LinearLayout(this); names.setOrientation(LinearLayout.VERTICAL); names.setPadding(dp(10), 0, 0, 0); header.addView(names, new LinearLayout.LayoutParams(0, -2, 1));
            textInto(names, topic, R.color.ink, 16, true, TypefaceMode.SERIF);
            textInto(names, topicSubtitle(topic), R.color.muted, 12, false, TypefaceMode.DEFAULT);
            boolean complete = !words.isEmpty() && count == words.size();
            TextView ratioText = text(complete ? "\u2713 Ho\u00E0n th\u00E0nh" : ratio + "%", complete ? R.color.green : R.color.accent, 13, true); header.addView(ratioText);
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); bar.setMax(100); bar.setProgress(ratio); bar.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.accent))); bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.divider)));
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(8)); bp.topMargin = dp(10); card.addView(bar, bp);
            LinearLayout footer = new LinearLayout(this); footer.setGravity(Gravity.CENTER_VERTICAL); footer.setPadding(0, dp(5), 0, 0); card.addView(footer);
            TextView left = text(count + " từ đã học", R.color.muted, 11, false); footer.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
            footer.addView(text("Tổng: " + words.size() + " từ", R.color.muted, 11, false));
        }
        int completedTopics = 0;
        for (String topic : SampleData.TOPICS) {
            List<Word> topicWords = SampleData.byTopic(topic); boolean complete = !topicWords.isEmpty();
            for (Word word : topicWords) if (progress.level(word.id) == 0) { complete = false; break; }
            if (complete) completedTopics++;
        }
        ((TextView) findViewById(R.id.statTopicCount)).setText(completedTopics + " / " + SampleData.TOPICS.length + " ch\u1EE7 \u0111\u1EC1 ho\u00E0n th\u00E0nh");
        renderReviewAgain(progress);
    }
    private void renderReviewAgain(ProgressSnapshot progress) {
        LinearLayout holder = findViewById(R.id.reviewAgainList);
        holder.removeAllViews();
        List<Word> reviewWords = new ArrayList<>();
        for (Word word : SampleData.all()) {
            if (progress.hasReviewed(word.id) && progress.level(word.id) == 0) reviewWords.add(word);
        }
        ((TextView) findViewById(R.id.reviewAgainCount)).setText(reviewWords.size() + " t\u1EEB c\u1EA7n \u00F4n");
        if (reviewWords.isEmpty()) {
            TextView empty = text("Ch\u01B0a c\u00F3 t\u1EEB c\u1EA7n \u00F4n t\u1EADp l\u1EA1i. H\u00E3y ti\u1EBFp t\u1EE5c h\u1ECDc th\u00EAm.", R.color.muted, 13, false);
            empty.setPadding(dp(16), dp(14), dp(16), dp(14)); empty.setBackgroundResource(R.drawable.stitch_soft);
            holder.addView(empty, new LinearLayout.LayoutParams(-1, -2));
            return;
        }
        Map<String, List<Word>> byTopic = new LinkedHashMap<>();
        for (Word word : reviewWords) {
            List<Word> topicWords = byTopic.get(word.topicId);
            if (topicWords == null) { topicWords = new ArrayList<>(); byTopic.put(word.topicId, topicWords); }
            topicWords.add(word);
        }
        for (Map.Entry<String, List<Word>> entry : byTopic.entrySet()) {
            String topic = entry.getKey(); List<Word> words = entry.getValue();
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(14), dp(14), dp(14), dp(14)); card.setBackgroundResource(R.drawable.stitch_card); card.setElevation(dp(1));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2); cardParams.bottomMargin = dp(10); holder.addView(card, cardParams);
            LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setClickable(true); header.setFocusable(true); card.addView(header, new LinearLayout.LayoutParams(-1, -2));
            TextView icon = text(topicIcon(topic), R.color.accent, 19, true); icon.setGravity(Gravity.CENTER); icon.setBackgroundResource(R.drawable.stitch_soft); header.addView(icon, new LinearLayout.LayoutParams(dp(38), dp(38)));
            LinearLayout labels = new LinearLayout(this); labels.setOrientation(LinearLayout.VERTICAL); labels.setPadding(dp(10), 0, dp(6), 0); header.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
            textInto(labels, topic, R.color.ink, 16, true, TypefaceMode.SERIF);
            textInto(labels, words.size() + " t\u1EEB ch\u01B0a thu\u1ED9c", R.color.muted, 12, false, TypefaceMode.DEFAULT);
            TextView toggle = text("\u2304", R.color.accent, 22, true); toggle.setGravity(Gravity.CENTER); header.addView(toggle, new LinearLayout.LayoutParams(dp(32), dp(38)));
            LinearLayout wordList = new LinearLayout(this); wordList.setOrientation(LinearLayout.VERTICAL); wordList.setVisibility(View.GONE); card.addView(wordList, new LinearLayout.LayoutParams(-1, -2));
            for (Word word : words) {
                LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.VERTICAL); row.setPadding(dp(10), dp(10), dp(10), dp(10)); row.setBackgroundResource(R.drawable.stitch_soft);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2); rowParams.topMargin = dp(8); wordList.addView(row, rowParams);
                LinearLayout wordHeader = new LinearLayout(this); wordHeader.setGravity(Gravity.CENTER_VERTICAL); row.addView(wordHeader, new LinearLayout.LayoutParams(-1, -2));
                LinearLayout wordLabels = new LinearLayout(this); wordLabels.setOrientation(LinearLayout.VERTICAL); wordHeader.addView(wordLabels, new LinearLayout.LayoutParams(0, -2, 1));
                textInto(wordLabels, word.english, R.color.ink, 16, true, TypefaceMode.SERIF);
                textInto(wordLabels, word.phonetic, R.color.muted, 12, false, TypefaceMode.DEFAULT);
                TextView state = text("CH\u01AFA THU\u1ED8C", R.color.accent, 9, true); state.setPadding(dp(7), dp(5), dp(7), dp(5)); state.setBackgroundResource(R.drawable.stitch_pill); wordHeader.addView(state);
                TextView meaning = text(word.meaning, R.color.ink, 13, false); meaning.setPadding(0, dp(7), 0, dp(7)); row.addView(meaning);
                com.google.android.material.button.MaterialButton review = new com.google.android.material.button.MaterialButton(this);
                review.setText("\u00D4n t\u1EEB n\u00E0y     \u2192"); review.setTextSize(13); review.setAllCaps(false); review.setTextColor(getColor(R.color.surface));
                review.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.accent))); review.setCornerRadius(dp(10));
                row.addView(review, new LinearLayout.LayoutParams(-1, dp(42)));
                review.setOnClickListener(v -> startActivity(new Intent(this, FlashcardActivity.class).putExtra("wordId", word.id).putExtra("review", true)));
            }
            header.setOnClickListener(v -> { boolean opening = wordList.getVisibility() != View.VISIBLE; wordList.setVisibility(opening ? View.VISIBLE : View.GONE); toggle.setText(opening ? "\u2303" : "\u2304"); });
        }
    }

    private String topicIcon(String topic) {
        if (topic.equals("Daily Life")) return "☼";
        if (topic.equals("University Life")) return "⌂";
        if (topic.equals("Food & Drinks")) return "♨";
        if (topic.equals("IT")) return "⌘";
        return "♧";
    }
    private String topicSubtitle(String topic) {
        if (topic.equals("Daily Life")) return "Đời sống hằng ngày & giao tiếp";
        if (topic.equals("University Life")) return "Môi trường giảng đường đại học";
        if (topic.equals("Food & Drinks")) return "Ẩm thực, nguyên liệu và đồ uống";
        if (topic.equals("IT")) return "Công nghệ thông tin & phần mềm";
        return "Thiên nhiên, khí hậu & bảo tồn";
    }
    private enum TypefaceMode { DEFAULT, SERIF }
    private TextView text(String value, int color, int size, boolean bold) { TextView v = new TextView(this); v.setText(value); v.setTextColor(getColor(color)); v.setTextSize(size); if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD); return v; }
    private void textInto(LinearLayout parent, String value, int color, int size, boolean bold, TypefaceMode type) { TextView v = text(value, color, size, bold); if (type == TypefaceMode.SERIF) v.setTypeface(topicTypeface()); parent.addView(v); }
    private android.graphics.Typeface topicTypeface() {
        if (topicTypeface != null) return topicTypeface;
        try { topicTypeface = ResourcesCompat.getFont(this, R.font.literata_semibold); }
        catch (RuntimeException ignored) { }
        if (topicTypeface == null) topicTypeface = android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD);
        return topicTypeface;
    }
}


