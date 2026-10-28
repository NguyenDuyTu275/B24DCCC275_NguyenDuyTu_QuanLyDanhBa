package com.nguyenduytu.quanlydanhba;

import java.io.Serializable;
import java.util.Locale;

/**
 * Lớp Contact biểu diễn một liên hệ trong danh bạ.
 * <p>
 * Thuộc tính: họ tên, số điện thoại, nhóm, giới tính, yêu thích, ghi chú.
 * <p>
 * Lớp cài đặt Serializable để gửi được cả đối tượng giữa các màn hình qua Intent (putExtra).
 */
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String phone;
    private String group;
    private String gender;
    private boolean favorite;
    private String note;

    public Contact(String name, String phone, String group, String gender, boolean favorite, String note) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.gender = gender;
        this.favorite = favorite;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Chữ cái đầu của tên (từ cuối cùng trong họ tên) để làm ảnh đại diện.
     * Ví dụ: "Nguyễn Văn An" -> "A", "Trần Thị Bình" -> "B".
     */
    public String getInitial() {
        String[] words = name.trim().split("\\s+");
        String lastWord = words[words.length - 1];
        if (lastWord.isEmpty()) {
            return "?";
        }
        return lastWord.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    /** Số điện thoại tách nhóm cho dễ đọc: "0901234567" -> "0901 234 567". */
    public String getFormattedPhone() {
        if (phone.length() != 10) {
            return phone;
        }
        return phone.substring(0, 4) + " " + phone.substring(4, 7) + " " + phone.substring(7);
    }

    /** Số điện thoại hợp lệ: gồm đúng 10 chữ số và bắt đầu bằng số 0. */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("0\\d{9}");
    }
}
