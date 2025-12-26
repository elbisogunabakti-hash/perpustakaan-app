package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import api.BukuApiClient;
import api.WebSocketClientHandler;
import model.Buku;
import view.BukuDialog;
import view.BukuFrame;
import worker.buku.DeleteBukuWorker;
import worker.buku.LoadBukuWorker;
import worker.buku.SaveBukuWorker;
import worker.buku.UpdateBukuWorker;

public class BukuController {

    private final BukuFrame frame;
    private final BukuApiClient bukuApiClient = new BukuApiClient();

    private List<Buku> allBuku = new ArrayList<>();
    private List<Buku> displayedBuku = new ArrayList<>();

    private WebSocketClientHandler wsClient;

    public BukuController(BukuFrame frame) {
        System.out.println("[CONTROLLER] BukuController created");
        this.frame = frame;
        setupEventListeners();
        setupWebSocket();
        loadAllBuku();
    }

    /* ===================== WEBSOCKET ===================== */
    private void setupWebSocket() {
        System.out.println("[CONTROLLER] Setting up WebSocket...");
        try {
            URI uri = new URI("ws://localhost:3000");
            wsClient = new WebSocketClientHandler(uri, new Consumer<String>() {
                @Override
                public void accept(String message) {
                    System.out.println("[WEBSOCKET] Received message: " + message);
                    handleWebSocketMessage(message);
                }
            });
            wsClient.connect();
            System.out.println("[CONTROLLER] WebSocket connection initiated");
        } catch (URISyntaxException e) {
            System.err.println("[CONTROLLER ERROR] WebSocket URI error: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    frame,
                    "Failed to connect realtime server:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleWebSocketMessage(String message) {
        System.out.println("[WEBSOCKET] Handling message: " + message);
        // Untuk sekarang: setiap event → reload data
        SwingUtilities.invokeLater(() -> {
            System.out.println("[WEBSOCKET] Reloading data due to WebSocket update");
            loadAllBuku();
        });
    }

    /* ===================== EVENT LISTENER ===================== */
    private void setupEventListeners() {
        System.out.println("[CONTROLLER] Setting up event listeners");
        
        frame.getAddButton().addActionListener(e -> {
            System.out.println("[UI EVENT] Add button clicked");
            openBukuDialog(null);
        });
        
        frame.getRefreshButton().addActionListener(e -> {
            System.out.println("[UI EVENT] Refresh button clicked");
            loadAllBuku();
        });
        
        frame.getDeleteButton().addActionListener(e -> {
            System.out.println("[UI EVENT] Delete button clicked");
            deleteSelectedBuku();
        });

        frame.getBukuTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("[UI EVENT] Table double-click");
                    int row = frame.getBukuTable().getSelectedRow();
                    if (row >= 0) {
                        Buku buku = displayedBuku.get(row);
                        System.out.println("[UI EVENT] Editing buku ID: " + buku.getId() + 
                                          ", Judul: " + buku.getJudul());
                        openBukuDialog(buku);
                    }
                }
            }
        });

        frame.getSearchField().getDocument()
                .addDocumentListener(new DocumentListener() {

            @Override 
            public void insertUpdate(DocumentEvent e) { 
                System.out.println("[UI EVENT] Search field insert update");
                filter(); 
            }
            
            @Override 
            public void removeUpdate(DocumentEvent e) { 
                System.out.println("[UI EVENT] Search field remove update");
                filter(); 
            }
            
            @Override 
            public void changedUpdate(DocumentEvent e) { 
                filter(); 
            }

            private void filter() {
                String keyword = frame.getSearchField()
                        .getText().toLowerCase().trim();
                
                System.out.println("[UI EVENT] Filtering with keyword: '" + keyword + "'");
                System.out.println("[UI EVENT] Total data before filter: " + allBuku.size());

                displayedBuku = new ArrayList<>();
                for (Buku buku : allBuku) {
                    if (buku.getJudul().toLowerCase().contains(keyword)
                            || buku.getPenulis().toLowerCase().contains(keyword)
                            || (buku.getPenerbit() != null
                                && buku.getPenerbit().toLowerCase().contains(keyword))) {
                        displayedBuku.add(buku);
                    }
                }
                
                System.out.println("[UI EVENT] Total data after filter: " + displayedBuku.size());
                frame.getBukuTableModel().setBukuList(displayedBuku);
                updateTotalRecords();
            }
        });
    }

    /* ===================== DIALOG ===================== */
    private void openBukuDialog(Buku bukuToEdit) {
        if (bukuToEdit == null) {
            System.out.println("[CONTROLLER] Opening dialog for NEW buku");
        } else {
            System.out.println("[CONTROLLER] Opening dialog for EDIT buku ID: " + bukuToEdit.getId());
        }

        BukuDialog dialog = (bukuToEdit == null)
                ? new BukuDialog(frame)
                : new BukuDialog(frame, bukuToEdit);

        dialog.getSaveButton().addActionListener(e -> {
            Buku buku = dialog.getBuku();
            System.out.println("[CONTROLLER] Save button clicked in dialog");
            System.out.println("[CONTROLLER] Buku data - ID: " + buku.getId() + 
                             ", Judul: " + buku.getJudul());

            SwingWorker<Void, Void> worker;
            
            if (bukuToEdit == null) {
                System.out.println("[CONTROLLER] Creating SaveBukuWorker (CREATE operation)");
                worker = new SaveBukuWorker(frame, bukuApiClient, buku);
            } else {
                System.out.println("[CONTROLLER] Creating UpdateBukuWorker (UPDATE operation)");
                worker = new UpdateBukuWorker(frame, bukuApiClient, buku);
            }

            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    System.out.println("[CONTROLLER] Worker DONE, closing dialog and reloading data");
                    dialog.dispose();
                    loadAllBuku();
                }
            });

            System.out.println("[CONTROLLER] Executing worker...");
            worker.execute();
        });

        dialog.setVisible(true);
        System.out.println("[CONTROLLER] Dialog displayed");
    }

    /* ===================== DELETE ===================== */
    private void deleteSelectedBuku() {
        int row = frame.getBukuTable().getSelectedRow();
        if (row < 0) {
            System.out.println("[UI EVENT] Delete clicked but no row selected");
            JOptionPane.showMessageDialog(frame, "Pilih data buku dulu");
            return;
        }

        Buku buku = displayedBuku.get(row);
        System.out.println("[CONTROLLER] Delete selected - ID: " + buku.getId() + 
                         ", Judul: " + buku.getJudul());

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Hapus buku:\n" + buku.getJudul() + " ?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("[CONTROLLER] User confirmed delete, creating DeleteBukuWorker");
            DeleteBukuWorker worker = new DeleteBukuWorker(frame, bukuApiClient, buku);

            worker.addPropertyChangeListener(evt -> {
                if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    System.out.println("[CONTROLLER] Delete worker DONE, reloading data");
                    loadAllBuku();
                }
            });
            
            System.out.println("[CONTROLLER] Executing delete worker...");
            worker.execute();
        } else {
            System.out.println("[CONTROLLER] User cancelled delete");
        }
    }

    /* ===================== LOAD DATA ===================== */
    private void loadAllBuku() {
        System.out.println("[CONTROLLER] Loading all buku data...");
        frame.getProgressBar().setIndeterminate(true);
        frame.getProgressBar().setString("Loading buku data...");

        LoadBukuWorker worker = new LoadBukuWorker(frame, bukuApiClient);

        worker.addPropertyChangeListener(evt -> {
            if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                System.out.println("[CONTROLLER] LoadBukuWorker DONE");
                try {
                    allBuku = worker.get();
                    System.out.println("[CONTROLLER] Data loaded successfully: " + 
                                     allBuku.size() + " records");
                    
                    displayedBuku = new ArrayList<>(allBuku);
                    frame.getBukuTableModel().setBukuList(displayedBuku);
                    updateTotalRecords();
                    
                } catch (Exception e) {
                    System.err.println("[CONTROLLER ERROR] Failed to load data: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            frame,
                            "Gagal memuat data",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    frame.getProgressBar().setIndeterminate(false);
                    frame.getProgressBar().setString("Ready");
                    System.out.println("[CONTROLLER] Data loading completed");
                }
            }
        });
        
        System.out.println("[CONTROLLER] Executing LoadBukuWorker...");
        worker.execute();
    }

    private void updateTotalRecords() {
        int count = displayedBuku.size();
        frame.getTotalRecordsLabel().setText(count + " Records");
        System.out.println("[CONTROLLER] Updated total records: " + count);
    }
}