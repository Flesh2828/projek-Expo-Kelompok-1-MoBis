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
import util.SceneNavigator;

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

        User userAktif = User.laksanakanLogin(inputUser, inputPass);

        if (userAktif != null) {
            try {
                String fxmlPath = "";
                String judulWindow = "";

                if (userAktif.getRole().equalsIgnoreCase("Pelanggan")){
                    fxmlPath = "/view/PelangganDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Pelanggan";
                    
                    // ===== KIRIM USERNAME KE DASHBOARD =====
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();
                    
                    PelangganDashboardController controller = loader.getController();
                    controller.setUsernameSession(inputUser); // <-- INI PENTING!
                    
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    SceneNavigator.switchTo(stage, root, judulWindow);
                    return;
                    
                } else if (userAktif.getRole().equalsIgnoreCase("Owner")){
                    fxmlPath = "/view/OwnerDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Owner";
                } else if (userAktif.getRole().equalsIgnoreCase("Admin")){
                    fxmlPath = "/view/AdminDashboard.fxml";
                    judulWindow = "SINARING - Dashboard Admin";
                }
                
                Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                SceneNavigator.switchTo(stage, root, judulWindow);
                
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
            Parent root = FXMLLoader.load(getClass().getResource("/view/Registrasi.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.switchTo(stage, root, "SINARING - Registrasi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}