package model;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class User {
    private String username;
    private String password; // Dikembalikan lagi biar strukturnya lengkap seperti awal
    private String role;

   
private static final String FILE_PATH;

static {
    String currentDir = System.getProperty("user.dir");
    // Jika terminal aktif di folder luar, tambahkan folder kapital ke jalurnya
    if (currentDir.endsWith("projek-Expo-Kelompok-1-MoBis")) {
        FILE_PATH = "ProjekExpoKel1MoBis/ProjekEXPOKEL1MoBis/data/users.xml";
    } else if (currentDir.endsWith("ProjekExpoKel1MoBis")) {
        FILE_PATH = "ProjekEXPOKEL1MoBis/data/users.xml";
    } else {
        FILE_PATH = "data/users.xml";
    }
}

    // Constructor lengkap     
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter 
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // --- FITUR SAVE KE XML (REGISTRASI) ---
    public static boolean laksanakanRegistrasi(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        try {
            File xmlFile = new File(FILE_PATH);
            File folderData = xmlFile.getParentFile();
            if (folderData != null && !folderData.exists()) {
                folderData.mkdirs();
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            if (xmlFile.exists()) {
                doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
                
                // Validasi ASD/RPL: Cek duplikasi username
                NodeList nList = doc.getElementsByTagName("user");
                for (int i = 0; i < nList.getLength(); i++) {
                    Element element = (Element) nList.item(i);
                    if (element.getElementsByTagName("username").item(0).getTextContent().equals(username)) {
                        return false; 
                    }
                }
            } else {
                // Jika file XML baru pertama kali dibuat, jalankan fungsi pembuat data awal (Admin & Owner)
                doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("users");
                doc.appendChild(rootElement);
                
                // Suntik data default Admin dan Owner biar langsung siap pakai
                tambahUserKeDoc(doc, "admin1", "admin123", "Admin");
                tambahUserKeDoc(doc, "owner1", "owner123", "Owner");
            }

            // Tambah elemen pelanggan baru hasil ketikan dari menu registrasi
            tambahUserKeDoc(doc, username, password, "Pelanggan");

            // Tulis/Save kembali ke file XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- FITUR BACA DARI XML (LOGIN) ---
    public static User laksanakanLogin(String username, String password) {
        try {
            File xmlFile = new File(FILE_PATH);
            
            // JIKA FILE XML BELUM ADA (Belum ada yang registrasi), kita buatkan dulu biar akun default admin/owner langsung aktif
            if (!xmlFile.exists()) {
                laksanakanRegistrasi("dummyUserHarusDihapus", "dummy123");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");

            // Algoritma Pencarian (ASD): Mencari data di dalam nodelist XML
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String u = element.getElementsByTagName("username").item(0).getTextContent();
                String p = element.getElementsByTagName("password").item(0).getTextContent();
                String r = element.getElementsByTagName("role").item(0).getTextContent();

                if (u.equals(username) && p.equals(password)) {
                    return new User(u, p, r); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // Helper Method internal untuk menyederhanakan penulisan struktur elemen XML
    private static void tambahUserKeDoc(Document doc, String username, String password, String role) {
        Element root = doc.getDocumentElement();
        Element newUser = doc.createElement("user");

        Element uname = doc.createElement("username");
        uname.appendChild(doc.createTextNode(username));
        newUser.appendChild(uname);

        Element upass = doc.createElement("password");
        upass.appendChild(doc.createTextNode(password));
        newUser.appendChild(upass);

        Element urole = doc.createElement("role");
        urole.appendChild(doc.createTextNode(role));
        newUser.appendChild(urole);

        root.appendChild(newUser);
    }

    // --- FITUR CEK USERNAME (EDIT PROFIL) ---
    public static boolean cekUsernameAda(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                return false;
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String u = element.getElementsByTagName("username").item(0).getTextContent();
                if (u.equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- FITUR UPDATE PROFIL (EDIT PROFIL) ---
    public static boolean updateProfil(String oldUsername, String newUsername, String newPassword) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return false;
        }

        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                return false;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");
            boolean userFound = false;

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String u = element.getElementsByTagName("username").item(0).getTextContent();

                if (u.equals(oldUsername)) {
                    // Update username
                    element.getElementsByTagName("username").item(0).setTextContent(newUsername.trim());
                    
                    // Update password if new password is not empty
                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        element.getElementsByTagName("password").item(0).setTextContent(newPassword.trim());
                    }
                    
                    userFound = true;
                    break;
                }
            }

            if (userFound) {
                // Save changes back to XML
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(xmlFile);
                transformer.transform(source, result);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- FITUR HAPUS AKUN (EDIT PROFIL) ---
    public static boolean hapusAkun(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                return false;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");
            boolean userFound = false;

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String u = element.getElementsByTagName("username").item(0).getTextContent();

                if (u.equals(username)) {
                    // Hapus node <user> ini sepenuhnya dari XML
                    element.getParentNode().removeChild(element);
                    userFound = true;
                    break;
                }
            }

            if (userFound) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(xmlFile);
                transformer.transform(source, result);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
