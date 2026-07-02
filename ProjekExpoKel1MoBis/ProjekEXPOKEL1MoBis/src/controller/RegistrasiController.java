package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

public class RegistrasiController {

    @FXML private TextField txtRegUsername;
    @FXML private PasswordField txtRegPassword;
    @FXML private Label lblPesanStatus;

    @FXML
    public void handleRegistrasi() {
        String userBaru = txtRegUsername.getText();
        String passBaru = txtRegPassword.getText();

        // CONTROLLER menyuruh MODEL untuk mengeksekusi pendaftaran data baru
        boolean sukses = User.laksanakanRegistrasi(userBaru, passBaru);

        if (sukses) {
            lblPesanStatus.setStyle("-fx-text-fill: green;");
            lblPesanStatus.setText("Akun Pelanggan Berhasil Dibuat!");
        } else {
            lblPesanStatus.setStyle("-fx-text-fill: red;");
            lblPesanStatus.setText("Gagal! Username/Password tidak boleh kosong.");
        }
    }

    @FXML
    public void keHalamanLogin() {
        System.out.println("Pindah scene ke Login.fxml");
        // Kode FXMLLoader untuk balik ke layar login
    }
}