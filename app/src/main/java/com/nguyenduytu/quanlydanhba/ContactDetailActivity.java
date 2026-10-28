package com.nguyenduytu.quanlydanhba;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Màn hình 4: Chi tiết liên hệ: gọi điện, đổi trạng thái yêu thích, xóa liên hệ. */
public class ContactDetailActivity extends AppCompatActivity {

    /** Liên hệ được chọn (MainActivity gửi sang). */
    public static final String EXTRA_CONTACT = "contact";
    /** Vị trí của liên hệ trong ArrayList, gửi đi và trả về để MainActivity biết dòng nào thay đổi. */
    public static final String EXTRA_POSITION = "position";
    /** Trạng thái yêu thích mới (trả về MainActivity). */
    public static final String EXTRA_FAVORITE = "favorite";
    /** true nếu người dùng đã xóa liên hệ (trả về MainActivity). */
    public static final String EXTRA_DELETED = "deleted";

    private Contact contact;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_contact_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Nhận dữ liệu liên hệ được gửi từ MainActivity qua Intent
        contact = IntentCompat.getSerializableExtra(getIntent(), EXTRA_CONTACT, Contact.class);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);
        if (contact == null) {
            finish();
            return;
        }
        showContact();

        CheckBox cbFavorite = findViewById(R.id.cbFavorite);
        cbFavorite.setChecked(contact.isFavorite());
        // Đặt sẵn kết quả mỗi lần đổi trạng thái: dù quay lại bằng nút Quay lại, mũi tên
        // hay nút Back của máy thì MainActivity vẫn cập nhật danh sách và thống kê
        cbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent result = new Intent();
            result.putExtra(EXTRA_POSITION, position);
            result.putExtra(EXTRA_FAVORITE, isChecked);
            setResult(RESULT_OK, result);
        });

        findViewById(R.id.btnCall).setOnClickListener(v -> callContact());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    private void showContact() {
        ContactAdapter.bindAvatar(findViewById(R.id.tvAvatar), contact);
        ((TextView) findViewById(R.id.tvName)).setText(contact.getName());
        ((TextView) findViewById(R.id.tvPhone)).setText(contact.getFormattedPhone());
        ((TextView) findViewById(R.id.tvGroup)).setText(contact.getGroup());
        ((TextView) findViewById(R.id.tvGender)).setText(contact.getGender());

        TextView tvNote = findViewById(R.id.tvNote);
        if (contact.getNote().isEmpty()) {
            tvNote.setText(R.string.no_note);
            tvNote.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
        } else {
            tvNote.setText(contact.getNote());
        }
    }

    /** Intent ngầm định ACTION_DIAL: mở màn hình quay số có sẵn số điện thoại (không cần quyền CALL_PHONE). */
    private void callContact() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone()));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_dialer, Toast.LENGTH_SHORT).show();
        }
    }

    /** Hỏi lại trước khi xóa; đồng ý thì báo MainActivity xóa khỏi ArrayList và quay lại danh sách. */
    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, contact.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_POSITION, position);
                    result.putExtra(EXTRA_DELETED, true);
                    setResult(RESULT_OK, result);
                    finish();
                })
                .show();
    }
}
