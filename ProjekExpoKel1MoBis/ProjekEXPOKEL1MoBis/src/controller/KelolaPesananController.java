package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.*;
import model.Pesanan;
import java.io.File;
import java.util.List;

public class KelolaPesananController {
    @FXML private TableView<Pesanan> tableKelolaPesanan;
    @FXML private TableColumn<Pesanan, String> colId, colPelanggan, colMenu, colTglKirim, colAlamat, colStatus, colPembayaran;
    @FXML private TableColumn<Pesanan, Integer> colQty;
    @FXML private Label lblJumlahPesanan;

    @FXML private Button btnFilterSemua;
    @FXML private Button btnFilterKonfirmasi;
    @FXML private Button btnFilterDimasak;
    @FXML private Button btnFilterDikirim;

    private ObservableList<Pesanan> listMasterPesanan = FXCollections.observableArrayList();
    private String currentFilter = "Semua";

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdPesanan()));
        colPelanggan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsernamePelanggan()));
        colMenu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNamaMenu()));
        colQty.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getJumlahPorsi()));
        colTglKirim.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggalPengiriman()));
        colAlamat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAlamatPengiriman()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatusPesanan()));
        colPembayaran.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatusPembayaran()));

        colStatus.setCellFactory(col -> badgeCell(this::gayaStatus));
        colPembayaran.setCellFactory(col -> badgeCell(this::gayaPembayaran));

        if (btnFilterSemua != null) btnFilterSemua.setOnAction(e -> handleFilterSemua());
        if (btnFilterKonfirmasi != null) btnFilterKonfirmasi.setOnAction(e -> handleFilterKonfirmasi());
        if (btnFilterDimasak != null) btnFilterDimasak.setOnAction(e -> handleFilterDimasak());
        if (btnFilterDikirim != null) btnFilterDikirim.setOnAction(e -> handleFilterDikirim());

        loadDataDariXML();
    }

 private void loadDataDariXML() {
    listMasterPesanan.clear();
    try {
        // PENTING: pakai Pesanan.getAllPesanan() supaya path yang dibaca SAMA PERSIS
        // dengan path yang dipakai saat pelanggan menyimpan pesanan baru (model/Pesanan.java).
        // Sebelumnya controller ini punya path hardcoded sendiri ("/data/pesanan.xml", path absolut)
        // yang tidak pernah cocok dengan file tempat data pelanggan benar-benar tersimpan.
        System.out.println("LOG SINARING -> Mencoba membaca XML di jalur: " + new File(Pesanan.getFilePath()).getAbsolutePath());

        List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();
        System.out.println("LOG SINARING -> Jumlah order yang terdeteksi di XML: " + semuaPesanan.size());

        listMasterPesanan.addAll(semuaPesanan);

        // PENTING: Set items ke table visual
        tableKelolaPesanan.setItems(listMasterPesanan);
        tableKelolaPesanan.refresh();
        perbaruiJumlahPesanan(listMasterPesanan.size());
        System.out.println("LOG SINARING -> Berhasil memasukkan data ke TableView Kelola Pesanan!");

    } catch (Exception e) { 
        System.out.println("LOG SINARING -> CRASH UTAMA PADA SAAT MEMBACA XML PESANAN:");
        e.printStackTrace(); 
    }
}
    private void applyFilter() {
        if (currentFilter.equals("Semua")) {
            tableKelolaPesanan.setItems(listMasterPesanan);
        } else {
            ObservableList<Pesanan> filtered = FXCollections.observableArrayList();
            for (Pesanan p : listMasterPesanan) {
                if (p.getStatusPesanan().equalsIgnoreCase(currentFilter)) filtered.add(p);
            }
            tableKelolaPesanan.setItems(filtered);
            perbaruiJumlahPesanan(filtered.size());
        }
        tableKelolaPesanan.refresh();
    }

    private void perbaruiJumlahPesanan(int jumlah) {
        if (lblJumlahPesanan != null) {
            lblJumlahPesanan.setText(jumlah + " pesanan");
        }
    }

    /**
     * Membuat TableCell generik berbentuk badge/pill berwarna.
     * gayaFn menerima teks status/pembayaran dan mengembalikan style CSS inline untuk badge.
     */
    private TableCell<Pesanan, String> badgeCell(java.util.function.Function<String, String> gayaFn) {
        return new TableCell<Pesanan, String>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("badge-pill");
                setAlignment(Pos.CENTER_LEFT);
            }
            @Override
            protected void updateItem(String nilai, boolean empty) {
                super.updateItem(nilai, empty);
                if (empty || nilai == null || nilai.isEmpty()) {
                    setGraphic(null);
                } else {
                    badge.setText(nilai);
                    badge.setStyle(gayaFn.apply(nilai));
                    setGraphic(badge);
                }
            }
        };
    }

    /** Warna badge untuk kolom PEMBAYARAN, mengikuti palet pada desain (hijau/kuning/merah). */
    private String gayaPembayaran(String status) {
        String s = status.trim().toLowerCase();
        // PENTING: cek "belum" LEBIH DULU sebelum "lunas", karena data asli
        // memakai teks "Belum Lunas" yang juga mengandung kata "lunas" —
        // urutan sebelumnya membuat status ini salah kena warna hijau.
        if (s.contains("belum")) return "-fx-background-color:#FBDCDF; -fx-text-fill:#C0392B;";
        if (s.contains("dp")) return "-fx-background-color:#FDF0CE; -fx-text-fill:#B8860B;";
        if (s.contains("lunas")) return "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;";
        return "-fx-background-color:#ECECEC; -fx-text-fill:#6B6B6B;";
    }

    /** Warna badge untuk kolom STATUS pesanan (netral abu, senada dengan desain kartu). */
    private String gayaStatus(String status) {
        String s = status.trim().toLowerCase();
        if (s.contains("dikirim")) return "-fx-background-color:#E4E1FB; -fx-text-fill:#5B4FCF;";
        if (s.contains("dimasak")) return "-fx-background-color:#FDEBD3; -fx-text-fill:#C87A0D;";
        if (s.contains("dikonfirmasi")) return "-fx-background-color:#DCEBFB; -fx-text-fill:#2E71B8;";
        if (s.contains("selesai")) return "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;";
        return "-fx-background-color:#ECECEC; -fx-text-fill:#6B6B6B;";
    }

    @FXML void handleFilterSemua() { currentFilter = "Semua"; applyFilter(); }
    @FXML void handleFilterKonfirmasi() { currentFilter = "Dikonfirmasi"; applyFilter(); }
    @FXML void handleFilterDimasak() { currentFilter = "Dimasak"; applyFilter(); }
    @FXML void handleFilterDikirim() { currentFilter = "Dikirim"; applyFilter(); }

    @FXML
    void handleProsesStatus() {
        Pesanan selected = tableKelolaPesanan.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String statusSekarang = selected.getStatusPesanan();
        String statusBaru = statusSekarang;

        if (statusSekarang.equalsIgnoreCase("Dikonfirmasi")) statusBaru = "Dimasak";
        else if (statusSekarang.equalsIgnoreCase("Dimasak")) statusBaru = "Dikirim";
        
        updateXMLStatus(selected.getIdPesanan(), statusBaru);
        loadDataDariXML();
    }

    private void updateXMLStatus(String id, String statusBaru) {
        // Pakai method di model Pesanan supaya path yang ditulis juga konsisten
        boolean sukses = Pesanan.updateStatusPesanan(id, statusBaru);
        if (!sukses) {
            System.out.println("LOG SINARING -> Gagal update status pesanan id=" + id);
        }
    }

    @FXML
    void handleVerifikasiPembayaran() {
        Pesanan selected = tableKelolaPesanan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tampilkanAlert("Pilih pesanan yang ingin diverifikasi pembayarannya terlebih dahulu.", Alert.AlertType.WARNING);
            return;
        }
        if (selected.getStatusPembayaran().equalsIgnoreCase("Lunas")) {
            tampilkanAlert("Pembayaran pesanan " + selected.getIdPesanan() + " sudah terverifikasi (Lunas).", Alert.AlertType.INFORMATION);
            return;
        }

        boolean sukses = Pesanan.updateStatusPembayaran(selected.getIdPesanan(), "Lunas");
        if (sukses) {
            loadDataDariXML();
            applyFilter();
            tampilkanAlert("Pembayaran pesanan " + selected.getIdPesanan() + " berhasil diverifikasi.", Alert.AlertType.INFORMATION);
        } else {
            tampilkanAlert("Gagal memverifikasi pembayaran pesanan " + selected.getIdPesanan() + ".", Alert.AlertType.ERROR);
        }
    }

    private void tampilkanAlert(String pesan, Alert.AlertType tipe) {
        Alert alert = new Alert(tipe);
        alert.setTitle("Verifikasi Pembayaran");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    @FXML
    void handleVerifikasiWA() {
        Pesanan selected = tableKelolaPesanan.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        // Membuka Browser Otomatis Menuju API WhatsApp Web kirim invoice resmi
        String pesan = "Halo " + selected.getUsernamePelanggan() + ", Pesanan " + selected.getIdPesanan() + " senilai Rp " + selected.getTotalHarga() + " telah diverifikasi sah oleh admin SINARING. Terima kasih!";
        String url = "https://api.whatsapp.com/send?text=" + pesan.replace(" ", "%20");
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) { e.printStackTrace(); }
    }
}