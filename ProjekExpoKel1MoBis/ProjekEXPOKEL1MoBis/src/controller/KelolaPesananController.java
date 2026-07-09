package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.*;
import model.Pesanan;
import java.io.File;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class KelolaPesananController {
    @FXML private TableView<Pesanan> tableKelolaPesanan;
    @FXML private TableColumn<Pesanan, String> colId, colPelanggan, colMenu, colTglKirim, colAlamat, colStatus, colPembayaran;
    @FXML private TableColumn<Pesanan, Integer> colQty;

    private ObservableList<Pesanan> listMasterPesanan = FXCollections.observableArrayList();
    private String currentFilter = "Semua";
    private final String xmlPath = "ProjekEXPOKEL1MoBis/src/data/pesanan.xml"; // Sesuaikan folder root laptopmu

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

        loadDataDariXML();
    }

    private void loadDataDariXML() {
        listMasterPesanan.clear();
        try {
            File file = new File(xmlPath);
            if (!file.exists()) return;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = doc.getElementsByTagName("order");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                Pesanan p = new Pesanan(
                    el.getAttribute("id"),
                    el.getElementsByTagName("username").item(0).getTextContent(),
                    el.getElementsByTagName("menu").item(0).getTextContent(),
                    Integer.parseInt(el.getElementsByTagName("porsi").item(0).getTextContent()),
                    el.getElementsByTagName("tanggal").item(0).getTextContent(),
                    el.getElementsByTagName("alamat").item(0).getTextContent(),
                    el.getElementsByTagName("catatan").item(0).getTextContent(),
                    el.getElementsByTagName("status_pesanan").item(0).getTextContent(),
                    el.getElementsByTagName("status_bayar").item(0).getTextContent(),
                    Double.parseDouble(el.getElementsByTagName("total").item(0).getTextContent())
                );
                listMasterPesanan.add(p);
            }
            applyFilter();
        } catch (Exception e) { e.printStackTrace(); }
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
        try {
            File file = new File(xmlPath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = doc.getElementsByTagName("order");
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(id)) {
                    el.getElementsByTagName("status_pesanan").item(0).setTextContent(statusBaru);
                    break;
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) { e.printStackTrace(); }
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