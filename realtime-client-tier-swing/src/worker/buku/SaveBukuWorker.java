package worker.buku;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BukuApiClient;
import model.Buku;
import view.BukuFrame;

public class SaveBukuWorker extends SwingWorker<Void, Void> {

    private final BukuFrame frame;
    private final BukuApiClient bukuApiClient;
    private final Buku buku;

    public SaveBukuWorker(
            BukuFrame frame,
            BukuApiClient bukuApiClient,
            Buku buku
    ) {
        this.frame = frame;
        this.bukuApiClient = bukuApiClient;
        this.buku = buku;

        // Log
        System.out.println("[WORKER] SaveBukuWorker created");
        System.out.println("[WORKER] Buku data - Judul: " + buku.getJudul() + 
                          ", ID: " + buku.getId());
        
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Saving new buku...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        System.out.println("[WORKER] SaveBukuWorker.doInBackground() started");
        System.out.println("[WORKER] Calling bukuApiClient.create()");
        
        bukuApiClient.create(buku);
        
        System.out.println("[WORKER] bukuApiClient.create() completed successfully");
        return null;
    }

    @Override
    protected void done() {
        System.out.println("[WORKER] SaveBukuWorker.done() called");
        
        frame.getProgressBar().setIndeterminate(false);
        try {
            get(); // catch exception dari doInBackground
            System.out.println("[WORKER] SaveBukuWorker - Success!");
            
            frame.getProgressBar().setString("Buku saved successfully");
            JOptionPane.showMessageDialog(
                    frame,
                    "Data buku berhasil disimpan.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            System.err.println("[WORKER ERROR] SaveBukuWorker failed: " + e.getMessage());
            e.printStackTrace();
            
            frame.getProgressBar().setString("Failed to save buku");
            JOptionPane.showMessageDialog(
                    frame,
                    "Gagal menyimpan data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        System.out.println("[WORKER] SaveBukuWorker finished");
    }
}