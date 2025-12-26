package worker.buku;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BukuApiClient;
import model.Buku;
import view.BukuFrame;

public class DeleteBukuWorker extends SwingWorker<Void, Void> {

    private final BukuFrame frame;
    private final BukuApiClient bukuApiClient;
    private final Buku buku;

    public DeleteBukuWorker(
            BukuFrame frame,
            BukuApiClient bukuApiClient,
            Buku buku
    ) {
        this.frame = frame;
        this.bukuApiClient = bukuApiClient;
        this.buku = buku;

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Deleting buku record...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        bukuApiClient.delete(buku.getId());
        return null;
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);

        try {
            get();
            frame.getProgressBar().setString("Buku deleted successfully");
            JOptionPane.showMessageDialog(
                    frame,
                    "Data buku berhasil dihapus.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to delete buku");
            JOptionPane.showMessageDialog(
                    frame,
                    "Gagal menghapus data buku:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
