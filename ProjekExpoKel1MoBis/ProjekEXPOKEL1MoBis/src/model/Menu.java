package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Menu {
    private String id;
    private String namaMenu;
    private String deskripsi;
    private double harga;
    private String status;

    private static final String FILE_PATH;

    static {
        String currentDir = System.getProperty("user.dir");
        if (currentDir.endsWith("projek-Expo-Kelompok-1-MoBis")) {
            FILE_PATH = "ProjekExpoKel1MoBis/ProjekEXPOKEL1MoBis/data/menu.xml";
        } else if (currentDir.endsWith("ProjekExpoKel1MoBis")) {
            FILE_PATH = "ProjekEXPOKEL1MoBis/data/menu.xml";
        } else {
            FILE_PATH = "data/menu.xml";
        }
    }

    public static String getFilePath() { return FILE_PATH; }

    public Menu(String id, String namaMenu, String deskripsi, double harga, String status) {
        this.id = id;
        this.namaMenu = namaMenu;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.status = status;
    }

    // GETTER
    public String getId() { return id; }
    public String getNamaMenu() { return namaMenu; }
    public String getDeskripsi() { return deskripsi; }
    public double getHarga() { return harga; }
    public String getStatus() { return status; }

    // LOAD DARI XML
    public static List<Menu> getAllMenu() {
        List<Menu> list = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                // Kalo file gak ada, return kosong (nanti diisi dummy di controller)
                return list;
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("menu");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                list.add(new Menu(
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