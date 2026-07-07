package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Pesanan;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KelolaPesananController {

    @FXML private TableView<Pesanan> tblDaftarPesanan; // sesuaikan fx:id tabel di KelolaPesanan.fxml
    @FXML private TableColumn<Pesanan, String> colId, colPelanggan, colMenu, colTglKirim, colAlamat, colStatus, colPembayaran;
    @FXML private TableColumn<Pesanan, Integer> colQty;

    private final ObservableList<Pesanan> semuaPesanan = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
      if (tblDaftarPesanan != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("idPesanan"));
            colPelanggan.setCellValueFactory(new PropertyValueFactory<>("usernamePelanggan"));
            colMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
            colQty.setCellValueFactory(new PropertyValueFactory<>("jumlahPorsi"));
            colTglKirim.setCellValueFactory(new PropertyValueFactory<>("tanggalPengiriman"));
            colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamatPengiriman"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPesanan"));

            // 2. Jalankan pembacaan file XML
            bacaSemuaXML();

            // 3. Pasangkan list data ke tabel UI
            tblDaftarPesanan.setItems(semuaPesanan);
        } else {
            System.out.println("Peringatan: fx:id tblDaftarPesanan tidak cocok atau belum tersambung!");
        }
    }
    private void bacaSemuaXML() {
        try {
            File xmlFile = new File("src/data/pesanan.xml");
            if (!xmlFile.exists()) return;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("order");
            semuaPesanan.clear();

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nNode;

                    Pesanan p = new Pesanan(
                        el.getAttribute("id"),
                        el.getElementsByTagName("username").item(0).getTextContent(),
                        el.getElementsByTagName("menu").item(0).getTextContent(),
                        Integer.parseInt(el.getElementsByTagName("porsi").item(0).getTextContent()),
                        el.getElementsByTagName("tanggal").item(0).getTextContent(),
                        el.getElementsByTagName("alamat").item(0).getTextContent(),
                        "", 
                        el.getElementsByTagName("status_pesanan").item(0).getTextContent(),
                        el.getElementsByTagName("status_bayar").item(0).getTextContent(),
                        Double.parseDouble(el.getElementsByTagName("total").item(0).getTextContent())
                    );
                    semuaPesanan.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}