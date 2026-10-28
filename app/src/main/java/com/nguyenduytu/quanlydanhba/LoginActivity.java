package com.nguyenduytu.quanlydanhba;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Màn hình 1: Đăng nhập bằng tài khoản cố định khai báo trong code. */
public class LoginActivity extends AppCompatActivity {

    // Tài khoản cố định (hard-code) theo yêu cầu đề bài
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123456";

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Chừa chỗ cho thanh trạng thái, thanh điều hướng và bàn phím
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);

        findViewById(R.id.btnLogin).setOnClickListener(v -> login());
        // Nhấn nút "Xong" trên bàn phím ở ô mật khẩu cũng là đăng nhập
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Không cho đăng nhập khi bỏ trống tên đăng nhập hoặc mật khẩu
        if (username.isEmpty() || password.isEmpty()) {
            showError(R.string.error_login_empty);
            return;
        }
        // So sánh với tài khoản cố định, sai thì ở lại màn hình đăng nhập
        if (!username.equals(USERNAME) || !password.equals(PASSWORD)) {
            showError(R.string.error_login_wrong);
            return;
        }

        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        // Đóng màn hình đăng nhập để nút Back ở danh sách không quay lại đây
        finish();
    }

    private void showError(@StringRes int message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
