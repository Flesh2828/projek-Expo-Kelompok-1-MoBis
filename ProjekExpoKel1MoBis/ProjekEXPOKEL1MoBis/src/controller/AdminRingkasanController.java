package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Pesanan;
import java.util.List;

public class AdminRingkasanController {
    @FXML private Label lblTotalPesanan;
    @FXML private Label lblPesananHariIni;
    @FXML private Label lblPendapatan;

    @FXML private TableView<Pesanan> tablePesananTerbaru;
    @FXML private TableColumn<Pesanan, String> colId;
    @FXML private TableColumn<Pesanan, String> colPelanggan;
    @FXML private TableColumn<Pesanan, String> colMenu;
    @FXML private TableColumn<Pesanan, String> colQty;
    @FXML private TableColumn<Pesanan, String> colTglKirim;
    @FXML private TableColumn<Pesanan, String> colStatus;
    
    @FXML private VBox boxAlertKapasitas;
    @FXML private ProgressBar progressKapasitas;

    private final ObservableList<Pesanan> dataPesanan = FXCollections.observableArrayList();
     @FXML
    public void initialize() {
        // 1. Hubungkan kolom TableView dengan property yang ada di model Pesanan.java
        colId.setCellValueFactory(new PropertyValueFactory<>("idPesanan"));
        colPelanggan.setCellValueFactory(new PropertyValueFactory<>("usernamePelanggan")); // Nanti bisa disesuaikan property-nya di model
        colMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("jumlahPorsi"));
        colTglKirim.setCellValueFactory(new PropertyValueFactory<>("tanggalPengiriman"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPesanan"));

        // 2. Ambil data gabungan langsung dari data/pesanan.xml untuk dimasukkan ke tabel ringkasan
        dataPesanan.clear();
        muatDataPesananTerbaru();
        javafx.application.Platform.runLater(() ->{
        if (tablePesananTerbaru != null) {
            tablePesananTerbaru.setItems(dataPesanan);
            tablePesananTerbaru.refresh(); // Paksa UI merefresh baris data
        }
    });
     }
    private void muatDataPesananTerbaru() {
        int totalPorsiHariIni = 0;

        try {
            // PENTING: pakai Pesanan.getAllPesanan() supaya path yang dibaca SAMA PERSIS
            // dengan path yang dipakai saat pelanggan menyimpan pesanan baru (model/Pesanan.java).
            // Sebelumnya controller ini punya path hardcoded sendiri yang salah ketik/salah folder
            // ("ProjekExpoKelompok1MoBis/ProjekEXPOKELOMPOK1MoBis/data/pesanan.xml") sehingga
            // file.exists() selalu false dan tabel ringkasan selalu kosong.
            List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

            // Batasi maksimal 5-7 pesanan terbaru untuk ringkasan dashboard
            int jumlahData = Math.min(semuaPesanan.size(), 7);
            for (int i = 0; i < semuaPesanan.size(); i++) {
                Pesanan p = semuaPesanan.get(i);

                // Masukkan ke list tabel ringkasan
                if (i < jumlahData) {
                    dataPesanan.add(p);
                    System.out.println("LOG SINARING -> Berhasil membaca order: " + p.getIdPesanan() + " oleh " + p.getUsernamePelanggan());
                }

                // Hitung akumulasi porsi untuk indikator bar kapasitas produksi
                totalPorsiHariIni += p.getJumlahPorsi();
            }

            tablePesananTerbaru.setItems(dataPesanan);
            
            // 3. Logika atur Progress Bar Kapasitas Maksimal Produksi (Contoh batas: 500 porsi)
            double persentaseKapasitas = (double) totalPorsiHariIni / 500.0;
            if (progressKapasitas != null) {
                progressKapasitas.setProgress(Math.min(persentaseKapasitas, 1.0));
            }
            
            // Sembunyikan alert kapasitas kalau porsi pesanan masih di bawah aman (misal < 50 porsi)
            if (totalPorsiHariIni < 50 && boxAlertKapasitas != null) {
                boxAlertKapasitas.setVisible(false);
                boxAlertKapasitas.setManaged(false);
            }
               } catch (Exception e) {
            System.out.println("Gagal membaca data ringkasan admin: " + e.getMessage());
            e.printStackTrace();
        
        }
    }
}