package api;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class WebSocketClientHandler extends WebSocketClient {

    // Handler untuk pesan JSON dari server
    private final Consumer<String> messageHandler;

    public WebSocketClientHandler(
            URI serverUri,
            Consumer<String> messageHandler
    ) {
        super(serverUri);
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("📡 WebSocket Perpustakaan terhubung");
    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.isBlank()) {
            System.err.println("⚠️ Pesan WebSocket kosong");
            return;
        }
        // Teruskan pesan (event buku) ke controller
        messageHandler.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket terputus: " + reason);

        // Auto reconnect
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("🔄 Mencoba reconnect WebSocket...");
                reconnect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("⚠️ WebSocket error: " + ex.getMessage());
    }
}
