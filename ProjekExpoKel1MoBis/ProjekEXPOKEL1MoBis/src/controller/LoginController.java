package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblPesanError;
    
    @FXML
    public void handleLogin() {
        String inputUser = txtUsername.getText();
        String inputPass = txtPassword.getText();

        // CONTROLLER memanggil MODEL untuk mengecek data asli
        User userAktif = User.laksanakanLogin(inputUser, inputPass);

        if (userAktif != null) {
            lblPesanError.setText("Login Berhasil sebagai: " + userAktif.getRole());
            
            // Logika RPL/PBO: Mengarahkan ke dashboard yang sesuai peran (role)
            if (userAktif.getRole().equals("Pelanggan")) {
                // kode pindah ke PelangganMain.fxml
            } else if (userAktif.getRole().equals("Admin")) {
                // kode pindah ke AdminMain.fxml
            } else if (userAktif.getRole().equals("Owner")) {
                // kode pindah ke OwnerMain.fxml
            }
        } else {
            // Jika Koki (Model) bilang data tidak ada, pelayan (Controller) pasang muka sedih di UI
            lblPesanError.setText("Username atau Password salah!");
        }
    }

    @FXML
    public void keHalamanRegistrasi() {
        System.out.println("Pindah scene ke Registrasi.fxml");
        // Di sini diisi kode FXMLLoader biasa untuk tumpuk/ganti scene halaman
    }
}