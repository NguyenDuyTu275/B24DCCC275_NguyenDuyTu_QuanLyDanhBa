package com.nguyenduytu.quanlydanhba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Kiểm thử các hàm xử lý dữ liệu của lớp Contact (chạy trên máy tính, không cần điện thoại). */
public class ContactTest {

    private static Contact contact(String name, String phone) {
        return new Contact(name, phone, "Bạn bè", "Nam", false, "");
    }

    @Test
    public void initial_isFirstLetterOfGivenName() {
        assertEquals("A", contact("Nguyễn Văn An", "0901234567").getInitial());
        assertEquals("B", contact("Trần Thị Bình", "0912345678").getInitial());
        assertEquals("C", contact("Lê Minh Châu", "0987654321").getInitial());
        assertEquals("Đ", contact("  lê văn   đức ", "0987654321").getInitial());
        assertEquals("T", contact("Tú", "0987654321").getInitial());
    }

    @Test
    public void validPhone_has10DigitsAndStartsWith0() {
        assertTrue(Contact.isValidPhone("0901234567"));
        assertTrue(Contact.isValidPhone("0388888888"));
    }

    @Test
    public void invalidPhone_isRejected() {
        assertFalse(Contact.isValidPhone(""));
        assertFalse(Contact.isValidPhone(null));
        assertFalse(Contact.isValidPhone("090123456"));    // 9 chữ số
        assertFalse(Contact.isValidPhone("09012345678"));  // 11 chữ số
        assertFalse(Contact.isValidPhone("1901234567"));   // không bắt đầu bằng 0
        assertFalse(Contact.isValidPhone("09012a4567"));   // có chữ cái
        assertFalse(Contact.isValidPhone("0901 23456"));   // có khoảng trắng
    }

    @Test
    public void formattedPhone_isGrouped4_3_3() {
        assertEquals("0901 234 567", contact("An", "0901234567").getFormattedPhone());
        assertEquals("12345", contact("An", "12345").getFormattedPhone());
    }
}
