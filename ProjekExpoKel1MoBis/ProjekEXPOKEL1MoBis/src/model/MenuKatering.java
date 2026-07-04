package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MenuKatering {
    private String idMenu;
    private String namaMenu;
    private String deskripsi;
    private double harga;
    private String status; // "Aktif" atau "Tidak Aktif"

    private static final String FILE_PATH = "data/menu.xml";

    public MenuKatering(String idMenu, String namaMenu, String deskripsi, double harga, String status) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.status = status;
    }

    // Getters & Setters
    public String getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public String getDeskripsi() { return deskripsi; }
    public double getHarga() { return harga; }
    public String getStatus() { return status; }

    // --- AMBIL SEMUA DATA MENU DARI XML ---
    public static List<MenuKatering> ambilSemuaMenu() {
        List<MenuKatering> list = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return list;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("menu");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                list.add(new MenuKatering(
                    el.getAttribute("id"),
                    el.getElementsByTagName("nama").item(0).getTextContent(),
                    el.getElementsByTagName("deskripsi").item(0).getTextContent(),
                    Double.parseDouble(el.getElementsByTagName("harga").item(0).getTextContent()),
                    el.getElementsByTagName("status").item(0).getTextContent()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}