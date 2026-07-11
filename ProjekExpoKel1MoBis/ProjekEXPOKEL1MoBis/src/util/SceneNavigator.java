package util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Membantu memindahkan scene pada sebuah Stage sekaligus memastikan
 * jendela SELALU dalam kondisi maximized setelah berpindah halaman
 * (Login <-> Registrasi, logout dari dashboard manapun, masuk ke
 * dashboard manapun), tanpa perlu pengguna meng-klik minimize/restore
 * secara manual terlebih dahulu.
 *
 * Catatan teknis: pada beberapa platform, memanggil stage.setMaximized(true)
 * ketika stage SUDAH dalam kondisi maximized tidak memicu perhitungan ulang
 * layout, sehingga scene baru tampil dengan ukuran kecil di pojok layar.
 * Trik "toggle" (false lalu true) di bawah ini memaksa JavaFX menghitung
 * ulang bounds jendela ke ukuran layar penuh setiap kali dipanggil.
 */
public final class SceneNavigator {

    private SceneNavigator() {
    }

    /** Pindah scene pada stage yang sudah ada, lalu maximize jendela. */
    public static void switchTo(Stage stage, Parent root, String title) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        if (title != null && !title.isEmpty()) {
            stage.setTitle(title);
        }

        stage.setMaximized(false);
        stage.setMaximized(true);
        stage.show();
    }
}
