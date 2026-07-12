package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import model.Pesanan;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;

public class PembayaranController {

    // Judul kartu tagihan ("Tagihan Belum Lunas" / "Semua Tagihan Lunas")
    @FXML
    private Text txtJudulTagihan;

    // Wadah daftar pesanan yang belum lunas, diisi dinamis lewat kode
    @FXML
    private VBox vboxDaftarTagihan;

    // Total keseluruhan tagihan yang belum lunas
    @FXML
    private Text txtSisaTagihan;

    @FXML
    private Button btnKonfirmasiBayar;

    // ===== DEKLARASIKAN VARIABEL INI =====
    private AnchorPane paneKontenTengah;
    private String usernameSession = "Pelanggan Aktif";
    // ====================================

    // ===== SETTER UNTUK paneKontenTengah =====
    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }
    // ========================================

    // ===== SETTER UNTUK usernameSession =====
    public void setUsernameSession(String username) {
        this.usernameSession = username;
        // Loader memanggil initialize() SEBELUM username asli di-set,
        // jadi begitu username-nya sudah benar, tagihan wajib dimuat ulang.
        muatTagihanBelumLunas();
    }
    // ========================================

    @FXML
    public void initialize() {
        muatTagihanBelumLunas();
    }

    // ===== PERBAIKAN: Ambil tagihan belum lunas SECARA REAL-TIME dari data
    //      pesanan.xml (sumber yang sama dengan Riwayat Pesanan & Dashboard Admin),
    //      bukan teks contoh yang statis. =====
    private void muatTagihanBelumLunas() {
        if (vboxDaftarTagihan == null || txtSisaTagihan == null || txtJudulTagihan == null) return;

        vboxDaftarTagihan.getChildren().clear();

        List<Pesanan> riwayat = Pesanan.getRiwayatByUser(usernameSession);

        double totalBelumLunas = 0;
        int jumlahBelumLunas = 0;

        for (Pesanan p : riwayat) {
            if (!"Lunas".equalsIgnoreCase(p.getStatusPembayaran())) {
                totalBelumLunas += p.getTotalHarga();
                jumlahBelumLunas++;

                Text baris = new Text(
                        p.getIdPesanan() + " - " + p.getNamaMenu() + " (" + p.getJumlahPorsi() + " porsi) — Rp "
                                + String.format("%,.0f", p.getTotalHarga())
                );
                baris.setStyle("-fx-font-size: 18; -fx-fill: #666666;");
                vboxDaftarTagihan.getChildren().add(baris);
            }
        }

        if (jumlahBelumLunas == 0) {
            txtJudulTagihan.setText("Semua Tagihan Lunas");
            txtJudulTagihan.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: #2E7D32;");

            Text infoLunas = new Text("Tidak ada tagihan yang perlu dibayar saat ini.");
            infoLunas.setStyle("-fx-font-size: 18; -fx-fill: #666666;");
            vboxDaftarTagihan.getChildren().add(infoLunas);

            txtSisaTagihan.setText("Rp 0");
            txtSisaTagihan.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: #2E7D32;");

            if (btnKonfirmasiBayar != null) btnKonfirmasiBayar.setDisable(true);
        } else {
            txtJudulTagihan.setText("Tagihan Belum Lunas (" + jumlahBelumLunas + ")");
            txtJudulTagihan.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: red;");

            txtSisaTagihan.setText("Total: Rp " + String.format("%,.0f", totalBelumLunas));
            txtSisaTagihan.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: red;");

            if (btnKonfirmasiBayar != null) btnKonfirmasiBayar.setDisable(false);
        }
    }

    @FXML
    private void handleSalinRekening(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            VBox parent = (VBox) btn.getParent();
            
            if (parent.getChildren().size() > 1) {
                Node node = parent.getChildren().get(1);
                if (node instanceof Text) {
                    Text rekeningText = (Text) node;
                    String rekening = rekeningText.getText();
                    
                    if (rekening != null && !rekening.isEmpty()) {
                        StringSelection selection = new StringSelection(rekening);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                        
                        btn.setText("✔ Disalin");
                        btn.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: #2E7D32; -fx-font-size: 11;");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKonfirmasiBayar(ActionEvent event) {
        try {
            // Load halaman KonfirmasiPembayaran
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/KonfirmasiPembayaran.fxml"));
            Parent konfirmasiView = loader.load();
            
            // Set paneKontenTengah & username ke KonfirmasiPembayaranController
            KonfirmasiPembayaranController konfirmasiController = loader.getController();
            konfirmasiController.setPaneKontenTengah(this.paneKontenTengah);
            konfirmasiController.setUsernameSession(this.usernameSession);
            
            // Ganti konten di paneKontenTengah
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(konfirmasiView);
                
                AnchorPane.setTopAnchor(konfirmasiView, 0.0);
                AnchorPane.setBottomAnchor(konfirmasiView, 0.0);
                AnchorPane.setLeftAnchor(konfirmasiView, 0.0);
                AnchorPane.setRightAnchor(konfirmasiView, 0.0);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
