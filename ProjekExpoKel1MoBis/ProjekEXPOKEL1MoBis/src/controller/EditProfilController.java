package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class EditProfilController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtKonfirmasiPassword;
    @FXML private Label lblStatus;
    
    private AnchorPane paneKontenTengah;
    private String usernameSession;
    private Text txtNamaUser;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    public void setUsernameSession(String username) {
        this.usernameSession = username;
        loadDataUser();
    }

    public void setTxtNamaUser(Text txtNamaUser) {
        this.txtNamaUser = txtNamaUser;
    }

    // ===== CARI FILE users.xml DI BEBERAPA LOKASI =====
    private File getFile() {
        // COBA SEMUA KEMUNGKINAN PATH
        String[] paths = {
            "src/users.xml",
            "./src/users.xml",
            "data/users.xml",
            "./data/users.xml",
            "users.xml",
            System.getProperty("user.dir") + "/src/users.xml",
            System.getProperty("user.dir") + "/data/users.xml"
        };
        
        for (String path : paths) {
            File file = new File(path);
            System.out.println("🔍 CEK: " + file.getAbsolutePath() + " -> " + file.exists());
            if (file.exists()) {
                System.out.println("✅ KETEMU DI: " + file.getAbsolutePath());
                return file;
            }
        }
        
        // JIKA TIDAK KETEMU, BUAT FILE BARU DI src/users.xml
        File newFile = new File("src/users.xml");
        System.out.println("📝 BUAT FILE BARU DI: " + newFile.getAbsolutePath());
        try {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            String defaultXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<users>\n</users>";
            Files.write(Paths.get(newFile.getAbsolutePath()), defaultXml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFile;
    }

    private void loadDataUser() {
        if (usernameSession == null || usernameSession.isEmpty()) {
            lblStatus.setText("❌ Session tidak ditemukan!");
            return;
        }

        try {
            File file = getFile();
            if (!file.exists()) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ File users.xml tidak ditemukan!");
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
            
            String searchTag = "<username>" + usernameSession + "</username>";
            if (content.contains(searchTag)) {
                txtUsername.setText(usernameSession);
                lblStatus.setStyle("-fx-text-fill: #2E7D32;");
                lblStatus.setText("✅ Data profil ditemukan!");
            } else {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ User tidak ditemukan!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("❌ Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSimpanProfil(ActionEvent event) {
        String usernameBaru = txtUsername.getText().trim();
        String passwordBaru = txtPassword.getText().trim();
        String konfirmasiPassword = txtKonfirmasiPassword.getText().trim();

        System.out.println("========================================");
        System.out.println("📝 MENYIMPAN PROFIL");
        System.out.println("   Username session: " + usernameSession);
        System.out.println("   Username baru: " + usernameBaru);
        System.out.println("   Password baru: " + (passwordBaru.isEmpty() ? "(tidak diubah)" : "*****"));
        System.out.println("========================================");

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

        try {
            File file = getFile();
            if (!file.exists()) {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ File users.xml tidak ditemukan!");
                return;
            }

            System.out.println("💾 SAVE KE: " + file.getAbsolutePath());

            // BACA ISI FILE
            String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
            System.out.println("📖 ISI FILE:\n" + content);
            
            // CARI USER LAMA
            String oldUserBlock = "";
            String newUserBlock = "";
            
            // SPLIT BERDASARKAN </user>
            String[] users = content.split("</user>");
            
            for (String user : users) {
                if (user.trim().isEmpty()) continue;
                user = user.trim() + "</user>";
                
                if (user.contains("<username>" + usernameSession + "</username>")) {
                    oldUserBlock = user;
                    newUserBlock = user;
                    
                    // GANTI USERNAME
                    newUserBlock = newUserBlock.replace(
                        "<username>" + usernameSession + "</username>",
                        "<username>" + usernameBaru + "</username>"
                    );
                    
                    // GANTI PASSWORD
                    if (!passwordBaru.isEmpty()) {
                        int startPass = newUserBlock.indexOf("<password>");
                        int endPass = newUserBlock.indexOf("</password>");
                        if (startPass != -1 && endPass != -1) {
                            String before = newUserBlock.substring(0, startPass + 10);
                            String after = newUserBlock.substring(endPass);
                            newUserBlock = before + passwordBaru + after;
                        }
                    }
                    
                    System.out.println("📝 USER DITEMUKAN!");
                    System.out.println("   OLD: " + oldUserBlock);
                    System.out.println("   NEW: " + newUserBlock);
                    break;
                }
            }
            
            if (!oldUserBlock.isEmpty() && !newUserBlock.isEmpty()) {
                // GANTI DI KONTEN
                String newContent = content.replace(oldUserBlock, newUserBlock);
                
                // TULIS KEMBALI
                Files.write(Paths.get(file.getAbsolutePath()), newContent.getBytes(StandardCharsets.UTF_8));
                
                // UPDATE SIDEBAR
                if (txtNamaUser != null) {
                    txtNamaUser.setText(usernameBaru);
                }
                usernameSession = usernameBaru;
                
                lblStatus.setStyle("-fx-text-fill: #2E7D32;");
                lblStatus.setText("✅ Profil berhasil diperbarui!");
                txtPassword.clear();
                txtKonfirmasiPassword.clear();
                
                System.out.println("✅✅✅ SAVE BERHASIL! ✅✅✅");
                System.out.println("   Username baru: " + usernameBaru);
                System.out.println("   File: " + file.getAbsolutePath());
                
            } else {
                lblStatus.setStyle("-fx-text-fill: red;");
                lblStatus.setText("❌ User tidak ditemukan!");
                System.err.println("❌ SAVE GAGAL! User '" + usernameSession + "' tidak ditemukan!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("❌ Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PelangganDashboard.fxml"));
            Parent root = loader.load();
            
            PelangganDashboardController controller = loader.getController();
            controller.setPaneKontenTengah(paneKontenTengah);
            controller.setUsernameSession(usernameSession);
            
            paneKontenTengah.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}