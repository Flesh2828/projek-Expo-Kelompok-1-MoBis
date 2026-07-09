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
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;

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
        String xmlPath = "ProjekExpoKelompok1MoBis/ProjekEXPOKELOMPOK1MoBis/data/pesanan.xml";

        try {
            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("order");

                // Batasi maksimal 5-7 pesanan terbaru untuk ringkasan dashboard
                int jumlahData = Math.min(nList.getLength(), 7);
                for (int i = 0; i < nList.getLength(); i++) {
                    org.w3c.dom.Element el = (org.w3c.dom.Element) nList.item(i);
                    
                    String id = el.getAttribute("id");
                    String user = el.getElementsByTagName("username").item(0).getTextContent();
                    String menu = el.getElementsByTagName("menu").item(0).getTextContent();
                    int porsi = Integer.parseInt(el.getElementsByTagName("porsi").item(0).getTextContent());
                    String tanggal = el.getElementsByTagName("tanggal").item(0).getTextContent();
                    String statPesanan = el.getElementsByTagName("status_pesanan").item(0).getTextContent();
                    String statBayar = el.getElementsByTagName("status_bayar").item(0).getTextContent();
                    double total = Double.parseDouble(el.getElementsByTagName("total").item(0).getTextContent());

                    Pesanan p = new Pesanan(id, user, menu, porsi, tanggal, "", "", statPesanan, statBayar, total);
                    
                    // Masukkan ke list tabel ringkasan
                    if (i < jumlahData) {
                        dataPesanan.add(p);
                        System.out.println("LOG SINARING -> Berhasil membaca order: " + id + " oleh " + user);
                    }
                    
                    // Hitung akumulasi porsi untuk indikator bar kapasitas produksi
                    totalPorsiHariIni += porsi;
                }
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
