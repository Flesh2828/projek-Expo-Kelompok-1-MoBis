package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
        }
        tableKelolaPesanan.refresh();
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