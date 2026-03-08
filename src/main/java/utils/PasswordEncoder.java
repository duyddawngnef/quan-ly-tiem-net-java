package utils;

/**
 * So sánh mật khẩu plaintext — không mã hóa
 */
public class PasswordEncoder {

    public static String encode(String rawPassword) {
        // Không mã hóa, trả về nguyên bản
        return rawPassword;
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;
        return rawPassword.equals(storedPassword);
    }
}