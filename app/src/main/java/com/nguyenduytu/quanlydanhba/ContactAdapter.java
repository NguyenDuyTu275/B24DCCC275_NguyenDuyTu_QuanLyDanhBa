package com.nguyenduytu.quanlydanhba;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Adapter hiển thị danh sách liên hệ lên RecyclerView (mỗi liên hệ là một thẻ). */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    /** Sự kiện khi người dùng nhấn vào một liên hệ trong danh sách. */
    public interface OnContactClickListener {
        void onContactClick(int position);
    }

    // Màu nền ảnh đại diện, chọn theo chữ cái đầu của tên: A, B, C... lần lượt từng màu
    private static final int[] AVATAR_COLORS = {
            R.color.avatar_red, R.color.avatar_blue, R.color.avatar_gold,
            R.color.avatar_purple, R.color.avatar_teal, R.color.avatar_steel
    };

    private final List<Contact> contacts;
    private final OnContactClickListener listener;

    public ContactAdapter(List<Contact> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        bindAvatar(holder.tvAvatar, contact);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getFormattedPhone());
        holder.tvGroup.setText(contact.getGroup());
        holder.ivFavorite.setVisibility(contact.isFavorite() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            // Lấy vị trí hiện tại của dòng (có thể đã đổi sau khi thêm / xóa)
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onContactClick(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    /** Ảnh đại diện: chữ cái đầu của tên trên nền tròn có màu. Dùng chung cho màn hình chi tiết. */
    public static void bindAvatar(TextView tvAvatar, Contact contact) {
        String initial = contact.getInitial();
        int index = Math.abs(initial.charAt(0) - 'A') % AVATAR_COLORS.length;
        int color = ContextCompat.getColor(tvAvatar.getContext(), AVATAR_COLORS[index]);
        tvAvatar.setText(initial);
        tvAvatar.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAvatar;
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvGroup;
        final ImageView ivFavorite;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvGroup = itemView.findViewById(R.id.tvGroup);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}
