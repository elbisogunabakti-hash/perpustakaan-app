package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Buku;

public class BukuApiClient {

    private static final String BASE_URL =
            "http://localhost/realtime-application-buku-tier-php/public/index.php?url=api/buku";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // ===================== GET ALL =====================
    public List<Buku> findAll() throws Exception {
        System.out.println("[API] GET ALL - Mengambil data buku dari: " + BASE_URL);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[API] GET ALL - Response status: " + response.statusCode());

        ApiResponse<List<Buku>> apiResp = gson.fromJson(
                response.body(),
                new TypeToken<ApiResponse<List<Buku>>>() {}.getType()
        );

        if (!apiResp.success) {
            System.err.println("[API ERROR] GET ALL - " + apiResp.message);
            throw new Exception(apiResp.message);
        }

        System.out.println("[API] GET ALL - Berhasil, data: " + 
                          (apiResp.data != null ? apiResp.data.size() : 0) + " records");
        return apiResp.data;
    }

    // ===================== CREATE =====================
    public void create(Buku b) throws Exception {
        System.out.println("[API] CREATE - Menambah buku baru:");
        System.out.println("  Judul: " + b.getJudul());
        System.out.println("  Penulis: " + b.getPenulis());
        System.out.println("  Penerbit: " + b.getPenerbit());
        System.out.println("  Tahun Terbit: " + b.getTahunTerbit());

        Map<String, Object> body = new HashMap<>();
        body.put("judul", b.getJudul());
        body.put("penulis", b.getPenulis());
        body.put("penerbit", b.getPenerbit());
        body.put("tahun_terbit", b.getTahunTerbit());

        String json = gson.toJson(body);
        System.out.println("  JSON Request: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[API] CREATE - Response status: " + response.statusCode());
        System.out.println("[API] CREATE - Response body: " + response.body());

        handleResponse(response);
        System.out.println("[API] CREATE - Berhasil disimpan");
    }

    // ===================== UPDATE =====================
    public void update(Buku b) throws Exception {
        System.out.println("[API] UPDATE - Update buku ID: " + b.getId());
        System.out.println("  Data baru:");
        System.out.println("    Judul: " + b.getJudul());
        System.out.println("    Penulis: " + b.getPenulis());
        System.out.println("    Penerbit: " + b.getPenerbit());
        System.out.println("    Tahun Terbit: " + b.getTahunTerbit());

        Map<String, Object> body = new HashMap<>();
        body.put("judul", b.getJudul());
        body.put("penulis", b.getPenulis());
        body.put("penerbit", b.getPenerbit());
        body.put("tahun_terbit", b.getTahunTerbit());

        String json = gson.toJson(body);
        System.out.println("  JSON Request: " + json);
        
        String url = BASE_URL + "/" + b.getId();
        System.out.println("  URL: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[API] UPDATE - Response status: " + response.statusCode());
        System.out.println("[API] UPDATE - Response body: " + response.body());

        handleResponse(response);
        System.out.println("[API] UPDATE - Berhasil diupdate");
    }

    // ===================== DELETE =====================
    public void delete(int id) throws Exception {
        System.out.println("[API] DELETE - Hapus buku ID: " + id);
        
        String url = BASE_URL + "/" + id;
        System.out.println("  URL: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("[API] DELETE - Response status: " + response.statusCode());
        System.out.println("[API] DELETE - Response body: " + response.body());

        handleResponse(response);
        System.out.println("[API] DELETE - Berhasil dihapus");
    }

    // ===================== API RESPONSE =====================
    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }

    // ===================== RESPONSE HANDLER =====================
    private void handleResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String errorMsg = "HTTP " + response.statusCode() + ": " +
                            extractErrorMessage(response.body());
            System.err.println("[API ERROR] " + errorMsg);
            throw new RuntimeException(errorMsg);
        }

        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);

        if (!apiResp.success) {
            System.err.println("[API ERROR] " + apiResp.message);
            throw new Exception(apiResp.message);
        } else {
            System.out.println("[API SUCCESS] " + apiResp.message);
        }
    }

    private String extractErrorMessage(String body) {
        try {
            ApiResponse<?> resp = gson.fromJson(body, ApiResponse.class);
            return resp.message != null
                    ? resp.message
                    : "Unknown server error";
        } catch (Exception e) {
            return "Server returned invalid response: " + body;
        }
    }
}