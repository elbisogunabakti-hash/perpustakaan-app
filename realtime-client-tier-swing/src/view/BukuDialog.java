package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import model.Buku;
import net.miginfocom.swing.MigLayout;

public class BukuDialog extends JDialog {

    private final JTextField judulField = new JTextField(25);
    private final JTextField penulisField = new JTextField(25);
    private final JTextField penerbitField = new JTextField(25);
    private final JTextField tahunTerbitField = new JTextField(10);

    private final JButton saveButton = new JButton("Save");
    private final JButton cancelButton = new JButton("Cancel");

    private Buku buku;

    // ===================== ADD =====================
    public BukuDialog(JFrame owner) {
        super(owner, "Add New Buku", true);
        this.buku = new Buku();
        setupComponents();
    }

    // ===================== EDIT =====================
    public BukuDialog(JFrame owner, Buku bukuToEdit) {
        super(owner, "Edit Buku", true);
        this.buku = bukuToEdit;
        setupComponents();

        judulField.setText(bukuToEdit.getJudul());
        penulisField.setText(bukuToEdit.getPenulis());
        penerbitField.setText(bukuToEdit.getPenerbit());
        tahunTerbitField.setText(
                String.valueOf(bukuToEdit.getTahunTerbit())
        );
    }

    private void setupComponents() {
        setLayout(new MigLayout("fill, insets 30", "[right]20[grow]"));

        add(new JLabel("Judul Buku"), "");
        add(judulField, "growx, wrap");

        add(new JLabel("Penulis"), "");
        add(penulisField, "growx, wrap");

        add(new JLabel("Penerbit"), "");
        add(penerbitField, "growx, wrap");

        add(new JLabel("Tahun Terbit"), "");
        add(tahunTerbitField, "growx, wrap");

        saveButton.setBackground(
                UIManager.getColor("Button.default.background")
        );
        saveButton.setForeground(
                UIManager.getColor("Button.default.foreground")
        );
        saveButton.setFont(
                saveButton.getFont().deriveFont(Font.BOLD)
        );

        JPanel buttonPanel = new JPanel(
                new MigLayout("", "[]10[]")
        );
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, "span, right");

        pack();
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(getOwner());
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public Buku getBuku() {
        buku.setJudul(judulField.getText().trim());
        buku.setPenulis(penulisField.getText().trim());
        buku.setPenerbit(penerbitField.getText().trim());

        try {
            buku.setTahunTerbit(
                    Integer.parseInt(tahunTerbitField.getText().trim())
            );
        } catch (NumberFormatException e) {
            buku.setTahunTerbit(0); // default aman
        }

        return buku;
    }
}
