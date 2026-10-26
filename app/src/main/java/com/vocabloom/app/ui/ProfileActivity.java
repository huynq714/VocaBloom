package com.vocabloom.app.ui;

import android.content.Intent;
import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabloom.app.R;
import com.vocabloom.app.data.CloudSync;
import com.vocabloom.app.data.LocalProgress;
import com.vocabloom.app.data.StudyReminderScheduler;

public class ProfileActivity extends AppCompatActivity {
    private int currentGoal;
    private FirebaseUser user;
    private boolean applyingReminderSettings;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 7402;
    private void toast(String message) { Toast.makeText(this, message, Toast.LENGTH_LONG).show(); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!CloudSync.signedIn(this)) { startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish(); return; }
        setContentView(R.layout.activity_profile); Nav.setup(this, 3);
        findViewById(R.id.profileBack).setOnClickListener(v -> finish());
        Switch reminder = findViewById(R.id.reminderToggle);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String reminderPrefsName = "study_reminder_" + (user == null ? "guest" : user.getUid());
        android.content.SharedPreferences reminderPrefs = getSharedPreferences(reminderPrefsName, MODE_PRIVATE);
        reminder.setChecked(reminderPrefs.getBoolean("study_reminder_enabled", false));
        findViewById(R.id.reminderTime).setAlpha(reminder.isChecked() ? 1f : 0.45f);
        int reminderHour = reminderPrefs.getInt("study_reminder_hour", 20), reminderMinute = reminderPrefs.getInt("study_reminder_minute", 30);
        updateReminderLabel(reminderHour, reminderMinute);
        loadReminderSettings(reminder, reminderPrefs, reminderHour, reminderMinute);
        findViewById(R.id.reminderTime).setOnClickListener(v -> new TimePickerDialog(this, (picker, hour, minute) -> {
            reminderPrefs.edit().putInt("study_reminder_hour", hour).putInt("study_reminder_minute", minute).apply();
            CloudSync.syncReminderSettings(this, reminder.isChecked(), hour, minute);
            updateReminderLabel(hour, minute);
            if (reminder.isChecked()) StudyReminderScheduler.schedule(this, hour, minute, (user == null ? "guest" : user.getUid()));
        }, reminderHour, reminderMinute, true).show());
        reminder.setOnCheckedChangeListener((button, checked) -> {
            if (applyingReminderSettings) return;
            reminderPrefs.edit().putBoolean("study_reminder_enabled", checked).apply();
            CloudSync.syncReminderSettings(this, checked, reminderPrefs.getInt("study_reminder_hour", 20), reminderPrefs.getInt("study_reminder_minute", 30));
            ((TextView) findViewById(R.id.reminderTime)).setAlpha(checked ? 1f : 0.45f);
            if (!checked) { StudyReminderScheduler.cancel(this, (user == null ? "guest" : user.getUid())); toast("Đã tắt nhắc học."); return; }
            if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
                return;
            }
            StudyReminderScheduler.schedule(this, reminderPrefs.getInt("study_reminder_hour", 20), reminderPrefs.getInt("study_reminder_minute", 30), (user == null ? "guest" : user.getUid()));
            toast("Đã bật thông báo nhắc học hằng ngày.");
        });
        LocalProgress progress = new LocalProgress(this); currentGoal = progress.getGoal();
        EditText goalInput = findViewById(R.id.goalInput); goalInput.setText(String.valueOf(currentGoal));
        TextView goalLabel = findViewById(R.id.goalValue); goalLabel.setText(currentGoal + " từ / ngày");
        goalInput.setOnFocusChangeListener((v, focused) -> { if (!focused) updateGoalLabel(goalInput, goalLabel); });
        findViewById(R.id.saveGoal).setOnClickListener(v -> saveGoal(progress, goalInput, goalLabel));
        findViewById(R.id.editName).setOnClickListener(v -> editName());
        findViewById(R.id.resetPassword).setOnClickListener(v -> resetPassword());
        findViewById(R.id.logoutButton).setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Đăng xuất?").setMessage("Bạn có muốn đăng xuất khỏi VocaBloom không?").setNegativeButton("Hủy", null).setPositiveButton("Đăng xuất", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut(); startActivity(new Intent(this, AuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)); finish();
        }).show());
        showUser(); loadCloudProfile(progress, goalInput, goalLabel);
    }

    private void loadReminderSettings(Switch reminder, android.content.SharedPreferences cache, int fallbackHour, int fallbackMinute) {
        if (user == null) return;
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
            if (isFinishing()) return;
            boolean enabled = doc.contains("studyReminderEnabled") ? Boolean.TRUE.equals(doc.getBoolean("studyReminderEnabled")) : cache.getBoolean("study_reminder_enabled", false);
            Long hourValue = doc.getLong("studyReminderHour"), minuteValue = doc.getLong("studyReminderMinute");
            int hour = hourValue == null ? fallbackHour : Math.max(0, Math.min(23, hourValue.intValue()));
            int minute = minuteValue == null ? fallbackMinute : Math.max(0, Math.min(59, minuteValue.intValue()));
            cache.edit().putBoolean("study_reminder_enabled", enabled).putInt("study_reminder_hour", hour).putInt("study_reminder_minute", minute).apply();
            applyingReminderSettings = true; reminder.setChecked(enabled); applyingReminderSettings = false;
            updateReminderLabel(hour, minute); findViewById(R.id.reminderTime).setAlpha(enabled ? 1f : 0.45f);
            if (enabled && (Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED))
                StudyReminderScheduler.schedule(this, hour, minute, user.getUid());
            else if (!enabled) StudyReminderScheduler.cancel(this, user.getUid());
            if (!doc.contains("studyReminderEnabled")) CloudSync.syncReminderSettings(this, enabled, hour, minute);
        });
    }

    private void saveGoal(LocalProgress progress, EditText input, TextView label) {
        if (!updateGoalLabel(input, label)) { input.setError("Nhập số từ 1 đến 100"); input.requestFocus(); return; }
        progress.setGoal(currentGoal);
        if (user == null) { toast("Không tìm thấy tài khoản."); return; }
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).update("dailyGoal", currentGoal)
            .addOnSuccessListener(ignored -> toast("\u0110\u00E3 l\u01B0u m\u1EE5c ti\u00EAu h\u1ECDc t\u1EADp."))
            .addOnFailureListener(ex -> toast("Đã lưu trên máy; chưa đồng bộ: " + ex.getLocalizedMessage()));
    }
    private void loadCloudProfile(LocalProgress progress, EditText goalInput, TextView goalLabel) {
        if (user == null) return;
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;
            String cloudName = doc.getString("fullName"); if (cloudName != null && !cloudName.trim().isEmpty()) updateLabels(cloudName.trim());
            Long cloudGoal = doc.getLong("dailyGoal");
            if (cloudGoal != null) {
                currentGoal = Math.max(1, Math.min(100, cloudGoal.intValue()));
                progress.setGoal(currentGoal);
                goalInput.setText(String.valueOf(currentGoal));
                goalLabel.setText(currentGoal + " từ / ngày");
            }
        });
    }

    private boolean updateGoalLabel(EditText input, TextView label) {
        String raw = input.getText().toString().trim();
        try {
            int value = Integer.parseInt(raw);
            if (value < 1 || value > 100) return false;
            currentGoal = value;
            label.setText(currentGoal + " từ / ngày");
            input.setError(null);
            return true;
        } catch (NumberFormatException ignored) { return false; }
    }

    private void updateReminderLabel(int hour, int minute) {
        ((TextView) findViewById(R.id.reminderTime)).setText(String.format(java.util.Locale.getDefault(), "◷  %02d:%02d mỗi tối · Chạm để đổi giờ", hour, minute));
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != NOTIFICATION_PERMISSION_REQUEST) return;
        Switch reminder = findViewById(R.id.reminderToggle);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            android.content.SharedPreferences prefs = getSharedPreferences("study_reminder_" + (user == null ? "guest" : user.getUid()), MODE_PRIVATE);
            StudyReminderScheduler.schedule(this, prefs.getInt("study_reminder_hour", 20), prefs.getInt("study_reminder_minute", 30), (user == null ? "guest" : user.getUid()));
            toast("Đã bật thông báo nhắc học hằng ngày.");
        } else {
            reminder.setChecked(false);
            toast("Cần cho phép thông báo để nhận nhắc học.");
        }
    }

    private void showUser() {
        if (user == null) return;
        String name = user.getDisplayName(); if (name == null || name.trim().isEmpty()) name = "Người học VocaBloom";
        updateLabels(name); ((TextView) findViewById(R.id.accountStatus)).setText(user.getEmail() == null ? "T\u00E0i kho\u1EA3n VocaBloom" : user.getEmail());
    }
    private void updateLabels(String name) {
        ((TextView) findViewById(R.id.profileName)).setText(name);
        ((TextView) findViewById(R.id.profileInitial)).setText(name.substring(0, 1).toUpperCase());
    }
    private void editName() {
        if (user == null) return;
        EditText input = new EditText(this); input.setSingleLine(true); input.setText(((TextView) findViewById(R.id.profileName)).getText());
        int padding = Math.round(24 * getResources().getDisplayMetrics().density); input.setPadding(padding, padding, padding, padding);
        new AlertDialog.Builder(this).setTitle("Chỉnh sửa tên hiển thị").setView(input).setNegativeButton("Hủy", null).setPositiveButton("Lưu", (dialog, which) -> {
            String name = input.getText().toString().trim(); if (name.isEmpty()) { toast("Tên không được để trống."); return; }
            user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build()).addOnSuccessListener(ignored ->
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).update("fullName", name)
                    .addOnSuccessListener(done -> { updateLabels(name); toast("Đã cập nhật hồ sơ."); })
                    .addOnFailureListener(ex -> { updateLabels(name); toast("\u0110\u00E3 đ\u1ED5i t\u00EAn đ\u0103ng nh\u1EADp nh\u01B0ng ch\u01B0a th\u1EC3 đ\u1ED3ng b\u1ED9 h\u1ED3 s\u01A1: " + ex.getLocalizedMessage()); })
            ).addOnFailureListener(ex -> toast("Không thể lưu tên: " + ex.getLocalizedMessage()));
        }).show();
    }
    private void resetPassword() {
        if (user == null || user.getEmail() == null) { toast("T\u00E0i kho\u1EA3n ch\u01B0a c\u00F3 email \u0111\u0103ng nh\u1EADp."); return; }
        android.view.View form = getLayoutInflater().inflate(R.layout.dialog_change_password, null, false);
        EditText current = form.findViewById(R.id.currentPasswordInput);
        EditText next = form.findViewById(R.id.newPasswordInput);
        EditText confirm = form.findViewById(R.id.confirmPasswordInput);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(form).create();
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(-1, -2);
            }
            form.findViewById(R.id.cancelPasswordChange).setOnClickListener(v -> dialog.dismiss());
            form.findViewById(R.id.submitPasswordChange).setOnClickListener(v -> {
                String oldValue = current.getText().toString();
                String newValue = next.getText().toString();
                if (oldValue.isEmpty()) { current.setError("Nh\u1EADp m\u1EADt kh\u1EA9u hi\u1EC7n t\u1EA1i"); return; }
                if (newValue.length() < 6) { next.setError("M\u1EADt kh\u1EA9u m\u1EDBi c\u1EA7n \u00EDt nh\u1EA5t 6 k\u00FD t\u1EF1"); return; }
                if (!newValue.equals(confirm.getText().toString())) { confirm.setError("M\u1EADt kh\u1EA9u nh\u1EADp l\u1EA1i ch\u01B0a kh\u1EDBp"); return; }
                form.findViewById(R.id.submitPasswordChange).setEnabled(false);
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), oldValue)).addOnSuccessListener(authResult ->
                    user.updatePassword(newValue).addOnSuccessListener(done -> { toast("\u0110\u00E3 \u0111\u1ED5i m\u1EADt kh\u1EA9u."); dialog.dismiss(); }).addOnFailureListener(ex -> {
                        form.findViewById(R.id.submitPasswordChange).setEnabled(true); toast("Kh\u00F4ng \u0111\u1ED5i \u0111\u01B0\u1EE3c m\u1EADt kh\u1EA9u: " + ex.getLocalizedMessage());
                    })
                ).addOnFailureListener(ex -> { form.findViewById(R.id.submitPasswordChange).setEnabled(true); current.setError("M\u1EADt kh\u1EA9u hi\u1EC7n t\u1EA1i kh\u00F4ng \u0111\u00FAng"); });
            });
        });
        dialog.show();
    }
}
