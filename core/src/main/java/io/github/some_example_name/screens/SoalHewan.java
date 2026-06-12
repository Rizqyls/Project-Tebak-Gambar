package io.github.some_example_name.screens;

public class SoalHewan {
    public int id;
    public String namaFileGambar;
    public String kunciJawaban;
    public boolean isTerjawab;

    public SoalHewan(int id, String namaFileGambar, String kunciJawaban) {
        this.id = id;
        this.namaFileGambar = namaFileGambar;
        this.kunciJawaban = kunciJawaban;
        this.isTerjawab = false;
    }
}