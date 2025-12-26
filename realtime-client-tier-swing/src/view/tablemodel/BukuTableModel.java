package view.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Buku;

public class BukuTableModel extends AbstractTableModel {

    private List<Buku> bukuList = new ArrayList<>();

    private final String[] columnNames = {
        "ID",
        "Judul",
        "Penulis",
        "Penerbit",
        "Tahun Terbit"
    };

    public void setBukuList(List<Buku> bukuList) {
        this.bukuList = bukuList;
        fireTableDataChanged();
    }

    public Buku getBukuAt(int rowIndex) {
        return bukuList.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return bukuList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Buku buku = bukuList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> buku.getId();
            case 1 -> buku.getJudul();
            case 2 -> buku.getPenulis();
            case 3 -> buku.getPenerbit();
            case 4 -> buku.getTahunTerbit();
            default -> null;
        };
    }
}
