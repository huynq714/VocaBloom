package com.vocabloom.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vocabloom.app.R;
import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private boolean register;
    private FirebaseAuth auth;
    private TextInputEditText name, email, password, confirmPassword;
    private View confirmPasswordLayout;
    private View nameLayout;
    private ProgressBar spinner;
    private TextView title, subtitle, error;
    private Button submit, toggle;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_auth);
        name = findViewById(R.id.nameInput); email = findViewById(R.id.emailInput); password = findViewById(R.id.passwordInput); confirmPassword = findViewById(R.id.confirmPasswordInput); confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        nameLayout = findViewById(R.id.nameLayout); spinner = findViewById(R.id.authLoading); title = findViewById(R.id.authHeading); subtitle = findViewById(R.id.authSubtitle);
        error = findViewById(R.id.authError); submit = findViewById(R.id.authSubmit); toggle = findViewById(R.id.toggleAuth);
        findViewById(R.id.forgotPassword).setOnClickListener(v -> resetPassword());
        if (FirebaseApp.initializeApp(this) == null) { submit.setEnabled(false); toggle.setEnabled(false); showError("Chưa cấu hình Firebase. Hãy thêm cấu hình Firebase của ứng dụng và bật Email/Password trong Authentication."); return; }
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) { openHome(); return; }
        toggle.setOnClickListener(v -> { register = !register; refresh(); });
        submit.setOnClickListener(v -> submitForm()); refresh();
    }

    private void refresh() {
        nameLayout.setVisibility(register ? View.VISIBLE : View.GONE); confirmPasswordLayout.setVisibility(register ? View.VISIBLE : View.GONE); title.setVisibility(register ? View.VISIBLE : View.GONE);
        title.setText(register ? "Tạo tài khoản" : "Đăng nhập");
        subtitle.setText(register ? "Tạo tài khoản để lưu quá trình học của riêng bạn." : "Tiếp tục hành trình làm chủ vốn từ của bạn.");
        submit.setText(register ? "Đăng ký" : "Đăng nhập     →"); toggle.setText(register ? "Đã có tài khoản? Đăng nhập" : "Chưa có tài khoản? Đăng ký"); error.setVisibility(View.GONE);
    }

    private String value(TextInputEditText field) { return field.getText() == null ? "" : field.getText().toString().trim(); }
    private void submitForm() {
        String e = value(email), p = value(password), n = value(name), confirmation = value(confirmPassword);
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { showError("Vui lòng nhập email hợp lệ."); return; }
        if (p.length() < 6) { showError("Mật khẩu cần tối thiểu 6 ký tự."); return; }
        if (register && TextUtils.isEmpty(n)) { showError("Vui lòng nhập tên hiển thị."); return; }
        if (register && !p.equals(confirmation)) { showError("M\u1EADt kh\u1EA9u x\u00E1c nh\u1EADn kh\u00F4ng kh\u1EDBp."); confirmPassword.requestFocus(); return; }
        busy(true); error.setVisibility(View.GONE);
        if (register) {
            auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) { failure(task.getException()); return; }
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) { busy(false); showError("Không lấy được tài khoản vừa tạo."); return; }
                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(n).build()).addOnCompleteListener(ignored -> {
                    Map<String, Object> profile = new HashMap<>(); profile.put("userId", user.getUid()); profile.put("fullName", n); profile.put("email", e); profile.put("dailyGoal", 10); profile.put("createdAt", FieldValue.serverTimestamp()); profile.put("studyReminderEnabled", false); profile.put("studyReminderHour", 20); profile.put("studyReminderMinute", 30);
                    FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(profile)
                        .addOnSuccessListener(v -> openHome()).addOnFailureListener(ex -> { busy(false); showError("Tài khoản đã tạo nhưng chưa lưu được hồ sơ: " + ex.getLocalizedMessage()); });
                });
            });
        } else auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(task -> { if (task.isSuccessful()) openHome(); else failure(task.getException()); });
    }

    private void resetPassword() {
        if (auth == null) { showError("Firebase chưa sẵn sàng."); return; }
        String e = value(email);
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { showError("Nhập email hợp lệ để nhận liên kết đặt lại mật khẩu."); return; }
        auth.sendPasswordResetEmail(e).addOnSuccessListener(v -> Toast.makeText(this, "Đã gửi liên kết đặt lại mật khẩu.", Toast.LENGTH_LONG).show()).addOnFailureListener(ex -> showError("Không gửi được email: " + ex.getLocalizedMessage()));
    }
    private void failure(Exception ex) { busy(false); showError("Không thể xác thực: " + (ex == null ? "Vui lòng thử lại." : ex.getLocalizedMessage())); }
    private void showError(String message) { error.setText(message); error.setVisibility(View.VISIBLE); }
    private void busy(boolean value) { spinner.setVisibility(value ? View.VISIBLE : View.GONE); submit.setEnabled(!value); toggle.setEnabled(!value); }
    private void openHome() { Intent i = new Intent(this, MainActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(i); finish(); }
}
