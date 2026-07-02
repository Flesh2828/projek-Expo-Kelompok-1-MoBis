package model;

public class User {
    private String username;
    private String password;
    private String role; // "Pelanggan", "Admin", atau "Owner"

    // Constructor untuk membuat objek User baru (saat registrasi)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- LOGIKA BISNIS (Fungsi Inti) ---

    // Fungsi simulasi untuk mengecek login (Koki mengecek bahan/data)
    public static User laksanakanLogin(String username, String password) {
        // Contoh data bawaan (hardcoded) untuk simulasi proyekmu
        if (username.equals("hafidz") && password.equals("123")) {
            return new User("hafidz", "123", "Pelanggan");
        } else if (username.equals("admin1") && password.equals("admin123")) {
            return new User("admin1", "admin123", "Admin");
        } else if (username.equals("owner1") && password.equals("owner123")) {
            return new User("owner1", "owner123", "Owner");
        }
        return null; // Jika user tidak ditemukan
    }

    // Fungsi simulasi untuk mendaftarkan pelanggan baru
    public static boolean laksanakanRegistrasi(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false; // Validasi gagal jika kosong
        }
        // Di sini nanti tempat kode untuk menyimpan data ke Database (MySQL)
        System.out.println("Akun baru pelanggan berhasil disimpan ke database: " + username);
        return true;
    }

    // --- Getter dan Setter ---
    public String getUsername() { return username; }
    public String getRole() { return role; }
}