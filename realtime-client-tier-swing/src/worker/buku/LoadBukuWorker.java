package worker.buku;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import api.BukuApiClient;
import model.Buku;
import view.BukuFrame;

public class LoadBukuWorker extends SwingWorker<List<Buku>, Void> {

    private final BukuFrame frame;
    private final BukuApiClient bukuApiClient;

    public LoadBukuWorker(
            BukuFrame frame,
            BukuApiClient bukuApiClient
    ) {
        this.frame = frame;
        this.bukuApiClient = bukuApiClient;

        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading buku data...");
    }

    @Override
    protected List<Buku> doInBackground() throws Exception {
        return bukuApiClient.findAll();
    }

    @Override
    protected void done() {
        frame.getProgressBar().setIndeterminate(false);
        try {
            List<Buku> result = get();

            // Set data ke table
            frame.getBukuTableModel().setBukuList(result);

            frame.getProgressBar()
                 .setString(result.size() + " records loaded");

            frame.getTotalRecordsLabel()
                 .setText(result.size() + " Records");

        } catch (Exception e) {
            frame.getProgressBar().setString("Failed to load data");
            JOptionPane.showMessageDialog(
                    frame,
                    "Error loading data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
