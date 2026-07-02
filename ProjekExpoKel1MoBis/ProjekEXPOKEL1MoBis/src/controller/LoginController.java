package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import model.User;

public class LoginController {

    // Komponen ini harus sama ID-nya dengan fx:id yang ada di file FXML
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblPesanError;

    /**
     * Fitur Aksi Tombol Masuk (Login Button)
     * Fungsi ini akan berjalan otomatis saat tombol "Masuk" diklik.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String inputUser = txtUsername.getText();
        String inputPass = txtPassword.getText();

        // 1. Validasi Input Kosong (Kebutuhan RPL)
        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            lblPesanError.setText("Username dan Password tidak boleh kosong!");
            return;
        }

        // 2. Memanggil logika bisnis dari MODEL untuk memvalidasi user
        User userAktif = User.laksanakanLogin(inputUser, inputPass);

        // 3. Cek apakah user ditemukan
        if (userAktif != null) {
            lblPesanError.setStyle("-fx-text-fill: green;");
            lblPesanError.setText("Login Berhasil! Mengalihkan...");

            // 4. Pengalihan Halaman (Scene Switching) Berdasarkan Role/Peran
            String namaFxmlDashboard = "";
            String judulWindow = "";

            if (userAktif.getRole().equals("Pelanggan")) {
                namaFxmlDashboard = "/View/PelangganMain.fxml"; // Sesuaikan huruf besar/kecil packagemu
                judulWindow = "SINARING - Dashboard Pelanggan";
            } else if (userAktif.getRole().equals("Admin")) {
                namaFxmlDashboard = "/View/AdminMain.fxml";
                judulWindow = "SINARING - Dashboard Admin";
            } else if (userAktif.getRole().equals("Owner")) {
                namaFxmlDashboard = "/View/OwnerMain.fxml";
                judulWindow = "SINARING - Dashboard Owner";
            }

            // Eksekusi perpindahan halaman window
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(namaFxmlDashboard));
                Parent root = loader.load();
                
                // Mengambil stage/window yang sedang aktif saat ini
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                // Mengganti scene di window tersebut ke dashboard baru
                Scene scene = new Scene(root);
                stage.setTitle(judulWindow);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                lblPesanError.setStyle("-fx-text-fill: red;");
                lblPesanError.setText("Gagal memuat halaman dashboard: " + namaFxmlDashboard);
                e.printStackTrace();
            }

        } else {
            // Jika akun tidak terdaftar atau password keliru
            lblPesanError.setStyle("-fx-text-fill: red;");
            lblPesanError.setText("Username atau Password salah!");
        }
    }

    /**
     * Fitur Aksi Hyperlink untuk Pindah ke Halaman Registrasi
     */
    @FXML
    public void keHalamanRegistrasi(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/Registrasi.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("SINARING - Daftar Pelanggan Baru");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}