package com.vocabloom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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
import java.util.Locale;

public class TopicsActivity extends AppCompatActivity {
    private int selected = -1;
    private String query = "";
    private final List<View> topicCards = new ArrayList<>();
    private final List<TextView> filterChips = new ArrayList<>();
    private Typeface cardTitleTypeface;
    private ListenerRegistration progressListener;
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!com.vocabloom.app.data.CloudSync.signedIn(this)) {
            startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); return;
        }
        setContentView(R.layout.activity_topics); Nav.setup(this, 1);
        findViewById(R.id.topicsBack).setOnClickListener(v -> finish());
        LocalProgress localProgress = new LocalProgress(this);
        ((TextView) findViewById(R.id.topicGoalLabel)).setText("Mục tiêu ngày: " + localProgress.getGoal() + " từ");
        ((TextView) findViewById(R.id.topicsStreak)).setText("🔥 " + localProgress.streakDays());
        ((TextView) findViewById(R.id.topicCount)).setText(SampleData.TOPICS.length + " chủ đề");
        buildFilters();
        EditText search = findViewById(R.id.topicSearch);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { query = s.toString().trim().toLowerCase(Locale.ROOT); filterCards(); }
            public void afterTextChanged(Editable e) { }
        });
        buildCards(ProgressSnapshot.fromLocal(localProgress));
        progressListener = CloudProgressStore.listen(this, snapshot -> { buildCards(snapshot); renderCloudHeader(snapshot); });
        loadCloudGoal();
    }
    @Override protected void onDestroy() { if (progressListener != null) progressListener.remove(); super.onDestroy(); }

    private void renderCloudHeader(ProgressSnapshot progress) {
        ((TextView) findViewById(R.id.topicsStreak)).setText("ðŸ”¥ " + progress.streakDays());
    }
    private void loadCloudGoal() {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String uid = user == null ? null : user.getUid();
        if (uid == null) return;
        com.vocabloom.app.data.CloudSync.loadUser(uid).addOnSuccessListener(doc -> {
            Long goal = doc.getLong("dailyGoal");
            if (goal != null) ((TextView) findViewById(R.id.topicGoalLabel)).setText("Má»¥c tiÃªu ngÃ y: " + goal + " tá»«");
        });
    }

    private void buildFilters() {
        LinearLayout container = findViewById(R.id.filterContainer);
        container.removeAllViews(); filterChips.clear();
        addFilter(container, "Tất cả", -1);
        for (int i = 0; i < SampleData.TOPICS.length; i++) addFilter(container, SampleData.TOPICS[i], i);
        styleFilters();
    }

    private void addFilter(LinearLayout container, String title, int topicIndex) {
        TextView chip = new TextView(this); chip.setText(title); chip.setGravity(Gravity.CENTER); chip.setTextSize(12);
        boolean all = topicIndex < 0;
        chip.setPadding(dp(all ? 16 : 14), 0, dp(all ? 16 : 14), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, dp(36));
        if (!filterChips.isEmpty()) params.leftMargin = dp(8);
        container.addView(chip, params); filterChips.add(chip);
        chip.setOnClickListener(v -> { selected = topicIndex; styleFilters(); filterCards(); });
    }

    private void styleFilters() {
        for (int i = 0; i < filterChips.size(); i++) {
            TextView chip = filterChips.get(i);
            boolean active = i == 0 ? selected < 0 : selected == i - 1;
            chip.setBackgroundResource(active ? R.drawable.stitch_accent : R.drawable.stitch_input);
            chip.setTextColor(getColor(active ? R.color.surface : R.color.muted));
            chip.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void buildCards(ProgressSnapshot progress) {
        LinearLayout list = findViewById(R.id.topicList); list.removeAllViews(); topicCards.clear();
        List<String> order = new ArrayList<>();
        if (SampleData.TOPICS.length > 2) order.add(SampleData.TOPICS[2]);
        if (SampleData.TOPICS.length > 1 && !order.contains(SampleData.TOPICS[1])) order.add(SampleData.TOPICS[1]);
        if (SampleData.TOPICS.length > 0 && !order.contains(SampleData.TOPICS[0])) order.add(SampleData.TOPICS[0]);
        for (String topic : SampleData.TOPICS) if (!order.contains(topic)) order.add(topic);
        for (int i = 0; i < order.size(); i++) {
            String topic = order.get(i); List<Word> words = SampleData.byTopic(topic);
            int learned = 0; for (Word word : words) if (progress.level(word.id) > 0) learned++;
            int percent = words.isEmpty() ? 0 : learned * 100 / words.size();
            LinearLayout card = new LinearLayout(this); card.setTag(topic); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(20), dp(18), dp(20), dp(18)); card.setBackgroundResource(R.drawable.stitch_card); card.setElevation(dp(2));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2); cardParams.bottomMargin = dp(12); list.addView(card, cardParams); topicCards.add(card);
            LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.TOP); card.addView(top, new LinearLayout.LayoutParams(-1, -2));
            LinearLayout info = new LinearLayout(this); info.setOrientation(LinearLayout.VERTICAL); top.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
            boolean complete = !words.isEmpty() && learned == words.size();
            String badgeText = complete ? "\u2713 HO\u00C0N TH\u00C0NH" : "\u266C  CH\u1EE6 \u0110\u1EC0 " + String.format(Locale.ROOT, "%02d", i + 1);
            TextView badge = label(badgeText, complete ? R.color.green : R.color.on_primary_fixed, 10, true); badge.setPadding(dp(9), dp(4), dp(9), dp(4)); badge.setBackgroundResource(complete ? R.drawable.stitch_soft : R.drawable.stitch_pill); info.addView(badge);
            TextView title = label(topic, R.color.ink, 20, true); title.setTypeface(cardTitleTypeface()); title.setPadding(0, dp(8), dp(6), 0); info.addView(title);
            TextView icon = label(topicIcon(topic), R.color.accent, 24, true); icon.setGravity(Gravity.CENTER); icon.setBackgroundResource(R.drawable.stitch_soft); top.addView(icon, new LinearLayout.LayoutParams(dp(52), dp(52)));
            LinearLayout stats = new LinearLayout(this); stats.setGravity(Gravity.CENTER_VERTICAL); stats.setPadding(0, dp(14), 0, dp(7)); card.addView(stats);
            TextView caption = label("Tiến độ tổng thể", R.color.muted, 12, false); stats.addView(caption, new LinearLayout.LayoutParams(0, -2, 1));
            TextView count = label(learned + " / " + words.size() + " từ (" + percent + "%)", R.color.accent, 12, true); stats.addView(count);
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); bar.setMax(100); bar.setProgress(percent); bar.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.accent))); bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.divider))); card.addView(bar, new LinearLayout.LayoutParams(-1, dp(8)));
            com.google.android.material.button.MaterialButton action = new com.google.android.material.button.MaterialButton(this); action.setText("Tiếp tục học     ▷"); action.setTextColor(getColor(R.color.surface)); action.setTextSize(14); action.setAllCaps(false); action.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.accent))); action.setCornerRadius(dp(14));
            LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, dp(52)); ap.topMargin = dp(16); card.addView(action, ap);
            action.setOnClickListener(v -> openTopic(topic)); card.setOnClickListener(v -> openTopic(topic));
        }
        filterCards();
    }

    private String topicIcon(String topic) {
        if (topic.equals("Daily Life")) return "☼";
        if (topic.equals("University Life")) return "⌂";
        if (topic.equals("Food & Drinks")) return "♨";
        if (topic.equals("IT")) return "⌘";
        return "♧";
    }

    private Typeface cardTitleTypeface() {
        if (cardTitleTypeface != null) return cardTitleTypeface;
        try { cardTitleTypeface = ResourcesCompat.getFont(this, R.font.literata_semibold); }
        catch (RuntimeException ignored) { }
        if (cardTitleTypeface == null) cardTitleTypeface = Typeface.create("serif", Typeface.BOLD);
        return cardTitleTypeface;
    }

    private TextView label(String value, int color, int size, boolean bold) { TextView t = new TextView(this); t.setText(value); t.setTextColor(getColor(color)); t.setTextSize(size); if (bold) t.setTypeface(null, Typeface.BOLD); return t; }

    private void filterCards() {
        for (View view : topicCards) {
            String topic = (String) view.getTag();
            boolean category = selected < 0 || topic.equals(SampleData.TOPICS[selected]);
            boolean matches = topic.toLowerCase(Locale.ROOT).contains(query);
            if (!matches) for (Word word : SampleData.byTopic(topic)) if (word.english.toLowerCase(Locale.ROOT).contains(query) || word.meaning.toLowerCase(Locale.ROOT).contains(query)) { matches = true; break; }
            view.setVisibility(category && matches ? View.VISIBLE : View.GONE);
        }
    }

    private void openTopic(String topic) { startActivity(new Intent(this, FlashcardActivity.class).putExtra("topic", topic)); }
}
