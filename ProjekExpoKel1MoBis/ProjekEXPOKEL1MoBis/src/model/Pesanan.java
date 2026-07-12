package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Pesanan {
    private String idPesanan;
    private String usernamePelanggan;
    private String namaMenu;
    private int jumlahPorsi;
    private String tanggalPengiriman;
    private String alamatPengiriman;
    private String catatanKhusus;
    private String statusPesanan;
    private String statusPembayaran;
    private double totalHarga;

    private static final String FILE_PATH;

    static {
        String currentDir = System.getProperty("user.dir");
        if (currentDir.endsWith("projek-Expo-Kelompok-1-MoBis")) {
            FILE_PATH = "ProjekExpoKel1MoBis/ProjekEXPOKEL1MoBis/data/pesanan.xml";
        } else if (currentDir.endsWith("ProjekExpoKel1MoBis")) {
            FILE_PATH = "ProjekEXPOKEL1MoBis/data/pesanan.xml";
        } else {
            FILE_PATH = "data/pesanan.xml";
        }
    }

    public Pesanan(String idPesanan, String usernamePelanggan, String namaMenu, int jumlahPorsi, 
                   String tanggalPengiriman, String alamatPengiriman, String catatanKhusus, 
                   String statusPesanan, String statusPembayaran, double totalHarga) {
        this.idPesanan = idPesanan;
        this.usernamePelanggan = usernamePelanggan;
        this.namaMenu = namaMenu;
        this.jumlahPorsi = jumlahPorsi;
        this.tanggalPengiriman = tanggalPengiriman;
        this.alamatPengiriman = alamatPengiriman;
        this.catatanKhusus = catatanKhusus;
        this.statusPesanan = statusPesanan;
        this.statusPembayaran = statusPembayaran;
        this.totalHarga = totalHarga;
    }

    // Getter path file (dipakai controller lain biar tidak hardcode path sendiri-sendiri)
    public static String getFilePath() { return FILE_PATH; }

    // Getters
    public String getIdPesanan() { return idPesanan; }
    public String getUsernamePelanggan() { return usernamePelanggan; }
    public String getNamaMenu() { return namaMenu; }
    public int getJumlahPorsi() { return jumlahPorsi; }
    public String getTanggalPengiriman() { return tanggalPengiriman; }
    public String getAlamatPengiriman() { return alamatPengiriman; }
    public String getCatatanKhusus() { return catatanKhusus; }
    public String getStatusPesanan() { return statusPesanan; }
    public String getStatusPembayaran() { return statusPembayaran; }
    public double getTotalHarga() { return totalHarga; }

    // --- SIMPAN PESANAN BARU KE XML ---
    public static boolean simpanPesananBaru(Pesanan pesanan) {
        try {
            File xmlFile = new File(FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            if (xmlFile.exists()) {
                doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
            } else {
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("orders");
                doc.appendChild(rootElement);
            }

            Element root = doc.getDocumentElement();
            Element newOrder = doc.createElement("order");

            newOrder.setAttribute("id", pesanan.idPesanan);
            
            tambahChild(doc, newOrder, "username", pesanan.usernamePelanggan);
            tambahChild(doc, newOrder, "menu", pesanan.namaMenu);
            tambahChild(doc, newOrder, "porsi", String.valueOf(pesanan.jumlahPorsi));
            tambahChild(doc, newOrder, "tanggal", pesanan.tanggalPengiriman);
            tambahChild(doc, newOrder, "alamat", pesanan.alamatPengiriman);
            tambahChild(doc, newOrder, "catatan", pesanan.catatanKhusus);
            tambahChild(doc, newOrder, "status_pesanan", pesanan.statusPesanan);
            tambahChild(doc, newOrder, "status_bayar", pesanan.statusPembayaran);
            tambahChild(doc, newOrder, "total", String.valueOf(pesanan.totalHarga));

            root.appendChild(newOrder);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- AMBIL RIWAYAT BERDASARKAN USERNAME ---
    public static List<Pesanan> getRiwayatByUser(String username) {
        List<Pesanan> list = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return list;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("order");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getElementsByTagName("username").item(0).getTextContent().equals(username)) {
                    list.add(new Pesanan(
                        el.getAttribute("id"),
                        username,
                        el.getElementsByTagName("menu").item(0).getTextContent(),
                        Integer.parseInt(el.getElementsByTagName("porsi").item(0).getTextContent()),
                        el.getElementsByTagName("tanggal").item(0).getTextContent(),
                        el.getElementsByTagName("alamat").item(0).getTextContent(),
                        el.getElementsByTagName("catatan").item(0).getTextContent(),
                        el.getElementsByTagName("status_pesanan").item(0).getTextContent(),
                        el.getElementsByTagName("status_bayar").item(0).getTextContent(),
                        Double.parseDouble(el.getElementsByTagName("total").item(0).getTextContent())
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- AMBIL SEMUA PESANAN ---
    public static List<Pesanan> getAllPesanan() {
        List<Pesanan> list = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return list;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("order");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                list.add(new Pesanan(
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
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- UPDATE STATUS PESANAN (DIPAKAI ADMIN, PAKAI FILE_PATH YANG SAMA) ---
    public static boolean updateStatusPesanan(String idPesanan, String statusBaru) {
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return false;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("order");

            boolean ditemukan = false;
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(idPesanan)) {
                    el.getElementsByTagName("status_pesanan").item(0).setTextContent(statusBaru);
                    ditemukan = true;
                    break;
                }
            }
            if (!ditemukan) return false;

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- UPDATE STATUS PEMBAYARAN (VERIFIKASI PEMBAYARAN OLEH ADMIN) ---
    public static boolean updateStatusPembayaran(String idPesanan, String statusBaru) {
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return false;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("order");

            boolean ditemukan = false;
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(idPesanan)) {
                    el.getElementsByTagName("status_bayar").item(0).setTextContent(statusBaru);
                    ditemukan = true;
                    break;
                }
            }
            if (!ditemukan) return false;

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- HAPUS PESANAN (DIPAKAI ADMIN DI KELOLA PESANAN) ---
    public static boolean hapusPesanan(String idPesanan) {
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return false;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("order");

            boolean ditemukan = false;
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(idPesanan)) {
                    el.getParentNode().removeChild(el);
                    ditemukan = true;
                    break;
                }
            }
            if (!ditemukan) return false;

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void tambahChild(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.appendChild(doc.createTextNode(value));
        parent.appendChild(child);
    }
}