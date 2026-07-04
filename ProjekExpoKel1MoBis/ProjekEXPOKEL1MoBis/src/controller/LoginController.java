package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblPesanError;

    @FXML
    public void handleLogin(ActionEvent event) {
        String inputUser = txtUsername.getText();
        String inputPass = txtPassword.getText();

        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            lblPesanError.setText("Username dan Password wajib diisi!");
            return;
        }

        // Membaca data dari XML melalui model baru yang lengkap
        User userAktif = User.laksanakanLogin(inputUser, inputPass);

        if (userAktif != null) {
            try {
                // Sesuai prinsip RPL: Arahkan scene berdasarkan Role akun 
                String fxmlPath = ""; // Default sementara gabungan
                String judulWindow = "" + userAktif.getRole();

                // Catatan Kelompok: Jika nanti file FXML tiap aktor sudah dipisah, tinggal aktifkan ini:
                /*
                if (userAktif.getRole().equals("Admin")) fxmlPath = "/View/AdminMain.fxml";
                else if (userAktif.getRole().equals("Owner")) fxmlPath = "/View/OwnerMain.fxml";
                */
                if (userAktif.getRole().equalsIgnoreCase("Pelanggan")){
                    fxmlPath = "/view/PelangganDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Pelanggan";
                } else if (userAktif.getRole().equalsIgnoreCase("Owner")){
                    fxmlPath = "/view/OwnerDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Owner";
                } else if (userAktif.getRole().equalsIgnoreCase("Admin")){
                    fxmlPath = "/view/AdminDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Admin";
                }
                Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(judulWindow);
                stage.setScene(new Scene(root, 600, 400)); 
                stage.show();
            } catch (Exception e) {
                lblPesanError.setText("Gagal memuat halaman dashboard!");
                e.printStackTrace();
            }
        } else {
            lblPesanError.setText("Akun salah atau belum terdaftar! Silakan registrasi.");
        }
    }

    @FXML
    public void keHalamanRegistrasi(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/Registrasi.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("SINARING - Registrasi");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}