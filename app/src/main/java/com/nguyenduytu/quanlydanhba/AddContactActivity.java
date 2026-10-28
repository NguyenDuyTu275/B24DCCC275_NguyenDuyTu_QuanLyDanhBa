package com.nguyenduytu.quanlydanhba;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

/** Màn hình 3: Nhập thông tin liên hệ mới và trả về MainActivity. */
public class AddContactActivity extends AppCompatActivity {

    /** Danh sách số điện thoại đã có (MainActivity gửi sang) để kiểm tra trùng. */
    public static final String EXTRA_EXISTING_PHONES = "existing_phones";
    /** Đối tượng Contact vừa tạo, gửi ngược về MainActivity. */
    public static final String EXTRA_CONTACT = "contact";

    private EditText etName;
    private EditText etPhone;
    private Spinner spGroup;
    private RadioGroup rgGender;
    private CheckBox cbFavorite;
    private EditText etNote;
    private ArrayList<String> existingPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_add_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Tính cả bàn phím để bàn phím không che mất form
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        existingPhones = getIntent().getStringArrayListExtra(EXTRA_EXISTING_PHONES);
        if (existingPhones == null) {
            existingPhones = new ArrayList<>();
        }

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        spGroup = findViewById(R.id.spGroup);
        rgGender = findViewById(R.id.rgGender);
        cbFavorite = findViewById(R.id.cbFavorite);
        etNote = findViewById(R.id.etNote);

        // Spinner nhóm lấy dữ liệu từ mảng contact_groups trong strings.xml
        ArrayAdapter<CharSequence> groupAdapter = ArrayAdapter.createFromResource(
                this, R.array.contact_groups, R.layout.item_spinner);
        groupAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spGroup.setAdapter(groupAdapter);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveContact());
    }

    /** Kiểm tra dữ liệu nhập, hợp lệ thì tạo Contact và gửi về màn hình danh sách. */
    private void saveContact() {
        String name = etName.getText().toString().trim().replaceAll("\\s+", " ");
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return;
        }
        if (!Contact.isValidPhone(phone)) {
            etPhone.setError(getString(R.string.error_phone_invalid));
            etPhone.requestFocus();
            return;
        }
        if (existingPhones.contains(phone)) {
            etPhone.setError(getString(R.string.error_phone_duplicate));
            etPhone.requestFocus();
            return;
        }

        RadioButton rbGender = findViewById(rgGender.getCheckedRadioButtonId());
        Contact contact = new Contact(
                name,
                phone,
                spGroup.getSelectedItem().toString(),
                rbGender.getText().toString(),
                cbFavorite.isChecked(),
                etNote.getText().toString().trim());

        // Trả đối tượng Contact về MainActivity qua Intent rồi đóng màn hình
        Intent result = new Intent();
        result.putExtra(EXTRA_CONTACT, contact);
        setResult(RESULT_OK, result);
        finish();
    }
}
