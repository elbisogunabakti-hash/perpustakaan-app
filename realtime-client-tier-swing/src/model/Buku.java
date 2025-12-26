package model;

import com.google.gson.annotations.SerializedName;

public class Buku {
    private int id;
    private String judul;
    private String penulis;
    private String penerbit;
    
    @SerializedName("tahun_terbit")
    private int tahunTerbit;
    
    // Constructor default
    public Buku() {
        this.tahunTerbit = 2024;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(int tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }
}
