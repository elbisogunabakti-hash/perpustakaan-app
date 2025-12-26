package worker.buku;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BukuApiClient;
import model.Buku;
import view.BukuFrame;

public class UpdateBukuWorker extends SwingWorker<Void, Void> {

    private final BukuFrame frame;
    private final BukuApiClient bukuApiClient;
    private final Buku buku;

    public UpdateBukuWorker(
            BukuFrame frame,
            BukuApiClient bukuApiClient,
            Buku buku
    ) {
        this.frame = frame;
        this.bukuApiClient = bukuApiClient;
        this.buku = buku;

        // LOG: Worker dibuat
        System.out.println("[WORKER] UpdateBukuWorker created for buku ID: " + buku.getId());
        System.out.println("[WORKER] Buku details - Judul: " + buku.getJudul() + 
                          ", Penulis: " + buku.getPenulis());
        
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Updating buku data...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        // LOG: Worker mulai eksekusi
        System.out.println("[WORKER] UpdateBukuWorker.doInBackground() started");
        System.out.println("[WORKER] Calling bukuApiClient.update() for ID: " + buku.getId());
        
        bukuApiClient.update(buku);
        
        // LOG: Update berhasil
        System.out.println("[WORKER] bukuApiClient.update() completed successfully");
        return null;
    }

    @Override
    protected void done() {
        // LOG: Worker selesai
        System.out.println("[WORKER] UpdateBukuWorker.done() called");
        
        frame.getProgressBar().setIndeterminate(false);
        try {
            get(); // Ambil hasil/exception dari doInBackground
            
            // LOG: Success
            System.out.println("[WORKER] UpdateBukuWorker - Update successful for ID: " + buku.getId());
            
            frame.getProgressBar().setString("Buku updated successfully");
            JOptionPane.showMessageDialog(
                    frame,
                    "Data buku berhasil diperbarui.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            // LOG: Error
            System.err.println("[WORKER ERROR] UpdateBukuWorker failed for ID: " + buku.getId());
            System.err.println("[WORKER ERROR] Message: " + e.getMessage());
            e.printStackTrace();
            
            frame.getProgressBar().setString("Failed to update buku");
            JOptionPane.showMessageDialog(
                    frame,
                    "Gagal memperbarui data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        
        // LOG: Worker selesai
        System.out.println("[WORKER] UpdateBukuWorker finished");
    }
}