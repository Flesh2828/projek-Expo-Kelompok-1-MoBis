package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Optional;
import model.User;
import util.SceneNavigator;

public class EditProfilController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtKonfirmasiPassword;
    @FXML private Label lblStatus;
    
    private AnchorPane paneKontenTengah;
    private String usernameSession;
    private Text txtNamaUser;
    private PelangganDashboardController dashboardController;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    public void setDashboardController(PelangganDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setUsernameSession(String username) {
        this.usernameSession = username;
        loadDataUser();
    }

    public void setTxtNamaUser(Text txtNamaUser) {
        this.txtNamaUser = txtNamaUser;
    }

    private void loadDataUser() {
        if (usernameSession == null || usernameSession.isEmpty()) {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Session tidak ditemukan!");
            return;
        }

        if (User.cekUsernameAda(usernameSession)) {
            txtUsername.setText(usernameSession);
            lblStatus.setStyle("-fx-text-fill: #2E7D32;");
            lblStatus.setText("✅ Data profil ditemukan!");
        } else {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ User tidak ditemukan!");
        }
    }

    @FXML
    private void handleSimpanProfil(ActionEvent event) {
        String usernameBaru = txtUsername.getText().trim();
        String passwordBaru = txtPassword.getText().trim();
        String konfirmasiPassword = txtKonfirmasiPassword.getText().trim();

        if (usernameBaru.isEmpty()) {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Username wajib diisi!");
            return;
        }

        if (!passwordBaru.isEmpty() && !passwordBaru.equals(konfirmasiPassword)) {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Password dan konfirmasi tidak sama!");
            return;
        }

        // Cek jika username baru sudah terdaftar oleh user lain
        if (!usernameBaru.equalsIgnoreCase(usernameSession) && User.cekUsernameAda(usernameBaru)) {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Username sudah terdaftar!");
            return;
        }

        boolean sukses = User.updateProfil(usernameSession, usernameBaru, passwordBaru);

        if (sukses) {
            lblStatus.setStyle("-fx-text-fill: #2E7D32;");
            lblStatus.setText("✅ Profil berhasil diperbarui!");
            txtPassword.clear();
            txtKonfirmasiPassword.clear();
            
            System.out.println("✅✅✅ SAVE BERHASIL! ✅✅✅");

            // Tampilkan dialog informasi
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Profil Berhasil Diubah");
            alert.setHeaderText(null);
            alert.setContentText("Profil Anda telah diperbarui. Silakan login kembali dengan username/password baru.");
            alert.showAndWait();

            // Arahkan kembali ke halaman login
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                SceneNavigator.switchTo(stage, root, "SINARING - Login");
            } catch (Exception e) {
                e.printStackTrace();
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ Gagal kembali ke halaman Login!");
            }
            
        } else {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Gagal memperbarui profil!");
            System.err.println("❌ SAVE GAGAL!");
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        if (dashboardController != null) {
            // Cukup ganti konten di panel yang sudah ada (sidebar & header
            // dashboard tidak ikut di-load ulang), jadi tidak dobel.
            dashboardController.kembaliKeDaftarMenu();
            return;
        }

        // Fallback jika dashboardController tidak ter-set (mis. diakses dari
        // luar konteks dashboard): baru load ulang shell dashboard seutuhnya.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PelangganDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            PelangganDashboardController controller = loader.getController();
            controller.setUsernameSession(usernameSession);

            SceneNavigator.switchTo(stage, root, "SINARING - Dashboard Pelanggan");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== HAPUS AKUN =====
    @FXML
    private void handleHapusAkun(ActionEvent event) {
        if (usernameSession == null || usernameSession.isEmpty()) {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Session tidak ditemukan!");
            return;
        }

        Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Konfirmasi dulu sebelum menghapus, karena aksi ini permanen
        Alert konfirmasi = new Alert(AlertType.CONFIRMATION);
        konfirmasi.initOwner(ownerStage);
        konfirmasi.setTitle("Konfirmasi Hapus Akun");
        konfirmasi.setHeaderText(null);
        konfirmasi.setContentText("Apakah Anda yakin ingin menghapus akun \"" + usernameSession
                + "\"? Tindakan ini tidak dapat dibatalkan dan seluruh data akun akan hilang.");
        konfirmasi.getDialogPane().getScene().getWindow().centerOnScreen();

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (!hasil.isPresent() || hasil.get() != ButtonType.OK) {
            return;
        }

        boolean sukses = User.hapusAkun(usernameSession);

        if (sukses) {
            Alert info = new Alert(AlertType.INFORMATION);
            info.initOwner(ownerStage);
            info.setTitle("Akun Terhapus");
            info.setHeaderText(null);
            info.setContentText("Akun Anda telah berhasil dihapus. Anda akan diarahkan ke halaman Login.");
            info.getDialogPane().getScene().getWindow().centerOnScreen();
            info.showAndWait();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
                SceneNavigator.switchTo(ownerStage, root, "SINARING - Login");
            } catch (Exception e) {
                e.printStackTrace();
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ Gagal kembali ke halaman Login!");
            }
        } else {
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Gagal menghapus akun!");
        }
    }
}