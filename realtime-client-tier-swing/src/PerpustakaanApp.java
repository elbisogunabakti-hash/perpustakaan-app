import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;

import controller.BukuController;
import view.BukuFrame;

public class PerpustakaanApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
        } catch (Exception ex) {
            System.err.println("Gagal mengatur tema FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            BukuFrame frame = new BukuFrame();
            new BukuController(frame);
            frame.setVisible(true);
        });
    }
}
    