package com.nguyenduytu.quanlydanhba;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.BundleCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/** Màn hình 2: Danh sách liên hệ, khối thống kê và nút Thêm liên hệ. */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CONTACTS = "contacts";

    // Danh sách liên hệ chỉ lưu trong bộ nhớ khi ứng dụng đang chạy
    private ArrayList<Contact> contacts;
    private ContactAdapter adapter;
    private RecyclerView rvContacts;
    private TextView tvTotal;
    private TextView tvFavorite;
    private TextView tvHint;

    // Nhận đối tượng Contact mới do màn hình Thêm liên hệ trả về
    private final ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() != RESULT_OK || data == null) {
                    return;
                }
                Contact contact = IntentCompat.getSerializableExtra(data, AddContactActivity.EXTRA_CONTACT, Contact.class);
                if (contact == null) {
                    return;
                }
                contacts.add(contact);
                adapter.notifyItemInserted(contacts.size() - 1);
                rvContacts.scrollToPosition(contacts.size() - 1);
                updateStats();
                Toast.makeText(this, getString(R.string.toast_added, contact.getName()), Toast.LENGTH_SHORT).show();
            });

    // Nhận kết quả từ màn hình Chi tiết: liên hệ bị xóa hoặc đã đổi trạng thái yêu thích
    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() != RESULT_OK || data == null) {
                    return;
                }
                int position = data.getIntExtra(ContactDetailActivity.EXTRA_POSITION, -1);
                if (position < 0 || position >= contacts.size()) {
                    return;
                }

                if (data.getBooleanExtra(ContactDetailActivity.EXTRA_DELETED, false)) {
                    Contact removed = contacts.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, getString(R.string.toast_deleted, removed.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    boolean favorite = data.getBooleanExtra(ContactDetailActivity.EXTRA_FAVORITE, false);
                    contacts.get(position).setFavorite(favorite);
                    adapter.notifyItemChanged(position);
                }
                updateStats();
            });

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Xoay màn hình thì lấy lại danh sách đã lưu, lần đầu mở thì nạp dữ liệu mẫu
        if (savedInstanceState != null) {
            contacts = BundleCompat.getSerializable(savedInstanceState, KEY_CONTACTS, ArrayList.class);
        }
        if (contacts == null) {
            contacts = createSampleContacts();
        }

        tvTotal = findViewById(R.id.tvTotal);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvHint = findViewById(R.id.tvHint);

        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(contacts, this::openContactDetail);
        rvContacts.setAdapter(adapter);

        findViewById(R.id.btnAddContact).setOnClickListener(v -> openAddContact());
        updateStats();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CONTACTS, contacts);
    }

    /** Dữ liệu mẫu ban đầu theo đề bài. */
    private ArrayList<Contact> createSampleContacts() {
        ArrayList<Contact> list = new ArrayList<>();
        list.add(new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", false,
                "Bạn cùng lớp đại học"));
        list.add(new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", true,
                "Chị gái, hay gọi buổi tối"));
        list.add(new Contact("Lê Minh Châu", "0987654321", "Công việc", "Nam", false,
                "Đồng nghiệp phòng kỹ thuật"));
        return list;
    }

    private void openAddContact() {
        // Gửi kèm các số điện thoại đã có để màn hình Thêm kiểm tra trùng
        ArrayList<String> phones = new ArrayList<>();
        for (Contact contact : contacts) {
            phones.add(contact.getPhone());
        }
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putStringArrayListExtra(AddContactActivity.EXTRA_EXISTING_PHONES, phones);
        addContactLauncher.launch(intent);
    }

    private void openContactDetail(int position) {
        // Gửi cả đối tượng Contact và vị trí của nó trong ArrayList sang màn hình chi tiết
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contacts.get(position));
        intent.putExtra(ContactDetailActivity.EXTRA_POSITION, position);
        detailLauncher.launch(intent);
    }

    /** Đếm tổng số liên hệ và số liên hệ yêu thích rồi hiển thị lên khối thống kê. */
    private void updateStats() {
        int favoriteCount = 0;
        for (Contact contact : contacts) {
            if (contact.isFavorite()) {
                favoriteCount++;
            }
        }
        tvTotal.setText(String.valueOf(contacts.size()));
        tvFavorite.setText(String.valueOf(favoriteCount));
        tvHint.setText(contacts.isEmpty() ? R.string.hint_empty_list : R.string.hint_tap_contact);
    }
}
