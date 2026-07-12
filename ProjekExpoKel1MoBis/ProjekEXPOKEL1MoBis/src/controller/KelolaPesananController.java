package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.*;
import javafx.scene.layout.VBox;
import model.Pesanan;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class KelolaPesananController {
    @FXML private TableView<Pesanan> tableKelolaPesanan;
    @FXML private TableColumn<Pesanan, String> colId, colPelanggan, colMenu, colTglKirim, colAlamat, colStatus, colPembayaran;
    @FXML private TableColumn<Pesanan, Integer> colQty;
    @FXML private Label lblJumlahPesanan;

    @FXML private Button btnFilterSemua;
    @FXML private Button btnFilterKonfirmasi;
    @FXML private Button btnFilterDimasak;
    @FXML private Button btnFilterDikirim;

    // ===== Fitur Antrean Pesanan (Queue / FIFO) =====
    @FXML private ListView<Pesanan> listAntrean;
    @FXML private Label lblJumlahAntrean;
    @FXML private Button btnSelesaikanAntrean;

    // Status yang dianggap "menunggu diproses" dan masuk ke antrean
    private static final String STATUS_ANTREAN = "Dikonfirmasi";
    // Status hasil setelah pesanan di-dequeue dari antrean (diproses)
    private static final String STATUS_SETELAH_ANTREAN = "Dimasak";
    // Status akhir pesanan setelah selesai dimasak/diproses
    private static final String STATUS_SELESAI = "Selesai";

    // Struktur data antrean: FIFO — pesanan yang paling awal masuk (Head)
    // ada di depan Queue dan akan diambil lebih dulu lewat poll().
    private final Queue<Pesanan> antreanPesanan = new LinkedList<>();

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

        if (listAntrean != null) {
            listAntrean.setOrientation(Orientation.HORIZONTAL);
            listAntrean.setCellFactory(lv -> antreanCell());
        }

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

        // Setiap kali data dimuat ulang, antrean (queue) ikut dibangun ulang
        // supaya selalu sinkron dengan status pesanan yang terbaru di XML.
        bangunAntreanPesanan();

    } catch (Exception e) { 
        System.out.println("LOG SINARING -> CRASH UTAMA PADA SAAT MEMBACA XML PESANAN:");
        e.printStackTrace(); 
    }
}

    /**
     * Membangun ulang antrean (queue) dari listMasterPesanan.
     * Urutan mengikuti urutan data pada XML, yaitu urutan pesanan itu di-input
     * (enqueue) pertama kali oleh pelanggan, sehingga pesanan yang paling awal
     * masuk otomatis berada di posisi Head/depan antrean (FIFO).
     * Hanya pesanan berstatus "Dikonfirmasi" yang dianggap menunggu di antrean;
     * begitu status berubah (mis. jadi "Dimasak"), pesanan itu otomatis keluar
     * dari antrean pada pembangunan ulang berikutnya.
     */
    private void bangunAntreanPesanan() {
        antreanPesanan.clear();
        for (Pesanan p : listMasterPesanan) {
            if (p.getStatusPesanan().equalsIgnoreCase(STATUS_ANTREAN)) {
                antreanPesanan.offer(p); // enqueue di posisi paling belakang (Tail)
            }
        }
        tampilkanAntreanPesanan();
    }

    /** Menampilkan isi antrean saat ini ke ListView, beserta info jumlahnya. */
    private void tampilkanAntreanPesanan() {
        if (listAntrean != null) {
            listAntrean.setItems(FXCollections.observableArrayList(antreanPesanan));
        }
        if (lblJumlahAntrean != null) {
            lblJumlahAntrean.setText(antreanPesanan.size() + " dalam antrean");
        }
        if (btnSelesaikanAntrean != null) {
            btnSelesaikanAntrean.setDisable(antreanPesanan.isEmpty());
        }
    }

    /**
     * Membuat ListCell untuk menampilkan satu baris antrean, lengkap dengan
     * nomor urut dan penanda visual khusus untuk pesanan di posisi Head (terdepan).
     */
    private ListCell<Pesanan> antreanCell() {
        return new ListCell<Pesanan>() {
            @Override
            protected void updateItem(Pesanan p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                boolean isHead = getIndex() == 0;

                Label lblRank = new Label(isHead ? "HEAD" : "#" + (getIndex() + 1));
                lblRank.getStyleClass().add(isHead ? "antrean-rank-head" : "antrean-rank");

                Label lblId = new Label(p.getIdPesanan());
                lblId.getStyleClass().add("antrean-id");

                Label lblPelanggan = new Label(p.getUsernamePelanggan());
                lblPelanggan.getStyleClass().add("antrean-detail");
                lblPelanggan.setMaxWidth(150.0);

                Label lblMenu = new Label(p.getNamaMenu());
                lblMenu.getStyleClass().add("antrean-detail");
                lblMenu.setMaxWidth(150.0);

                VBox kolomTeks = new VBox(2.0, lblId, lblPelanggan, lblMenu);

                VBox kartu = new VBox(6.0, lblRank, kolomTeks);
                kartu.setAlignment(Pos.TOP_LEFT);
                kartu.setPrefWidth(160.0);
                kartu.setMinWidth(160.0);
                kartu.setPrefHeight(150.0);
                kartu.getStyleClass().add(isHead ? "antrean-item-head" : "antrean-item");

                setGraphic(kartu);
                setText(null);
            }
        };
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

    /**
     * Tombol "Selesaikan Antrean Terdepan".
     * Melakukan dequeue (poll) terhadap antrean: pesanan yang paling awal
     * masuk (Head) dikeluarkan dari antrean, lalu status pesanan itu otomatis
     * diubah dari "Dikonfirmasi" menjadi "Dimasak" dan disimpan ke XML.
     */
    @FXML
    void handleSelesaikanAntreanTerdepan() {
        Pesanan head = antreanPesanan.poll(); // dequeue: ambil & keluarkan elemen paling depan

        if (head == null) {
            tampilkanAlert("Antrean pesanan kosong, tidak ada pesanan yang menunggu diproses.", Alert.AlertType.INFORMATION, "Antrean Pesanan");
            return;
        }

        boolean sukses = Pesanan.updateStatusPesanan(head.getIdPesanan(), STATUS_SETELAH_ANTREAN);
        if (!sukses) {
            System.out.println("LOG SINARING -> Gagal memproses antrean untuk id=" + head.getIdPesanan());
            tampilkanAlert("Gagal memperbarui status pesanan " + head.getIdPesanan() + ".", Alert.AlertType.ERROR, "Antrean Pesanan");
            // Data belum berubah di XML, jadi muat ulang supaya antrean & tabel tetap konsisten.
            loadDataDariXML();
            applyFilter();
            return;
        }

        // Muat ulang data dari XML: tabel & antrean akan otomatis sinkron kembali,
        // dan pesanan yang baru diproses tidak lagi muncul di antrean karena
        // statusnya sudah bukan "Dikonfirmasi".
        loadDataDariXML();
        applyFilter();

        tampilkanAlert("Pesanan " + head.getIdPesanan() + " dikeluarkan dari antrean dan statusnya kini \"" + STATUS_SETELAH_ANTREAN + "\".", Alert.AlertType.INFORMATION, "Antrean Pesanan");
    }

    /**
     * Tombol "Tandai Selesai".
     * Mengubah status pesanan yang dipilih di tabel dari "Dimasak" menjadi
     * "Selesai" (pesanan sudah selesai diproses/dikonfirmasi dan dimasak).
     */
    @FXML
    void handleTandaiSelesai() {
        Pesanan selected = tableKelolaPesanan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tampilkanAlert("Pilih pesanan yang ingin ditandai selesai terlebih dahulu.", Alert.AlertType.WARNING, "Tandai Selesai");
            return;
        }

        if (selected.getStatusPesanan().equalsIgnoreCase(STATUS_SELESAI)) {
            tampilkanAlert("Pesanan " + selected.getIdPesanan() + " sudah berstatus \"Selesai\".", Alert.AlertType.INFORMATION, "Tandai Selesai");
            return;
        }

        if (!selected.getStatusPesanan().equalsIgnoreCase(STATUS_SETELAH_ANTREAN)) {
            tampilkanAlert("Pesanan harus berstatus \"" + STATUS_SETELAH_ANTREAN + "\" terlebih dahulu (sudah dikonfirmasi & dimasak) sebelum bisa ditandai selesai.", Alert.AlertType.WARNING, "Tandai Selesai");
            return;
        }

        boolean sukses = Pesanan.updateStatusPesanan(selected.getIdPesanan(), STATUS_SELESAI);
        if (sukses) {
            loadDataDariXML();
            applyFilter();
            tampilkanAlert("Pesanan " + selected.getIdPesanan() + " berhasil ditandai selesai.", Alert.AlertType.INFORMATION, "Tandai Selesai");
        } else {
            tampilkanAlert("Gagal menandai pesanan " + selected.getIdPesanan() + " sebagai selesai.", Alert.AlertType.ERROR, "Tandai Selesai");
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
        tampilkanAlert(pesan, tipe, "Verifikasi Pembayaran");
    }

    private void tampilkanAlert(String pesan, Alert.AlertType tipe, String judul) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
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