package com.rumahsakit;

public class Janji {
    private String namaDokter;
    private String namaPasien;
    private String tanggal;
    private String jam;
    private String keluhanPasien;
    private String resep;
    private int harga;
    private String status;

    public Janji(String namaDokter, String namaPasien, String tanggal, String jam, String keluhanPasien, String resep,
            int harga, String status) {
        this.namaDokter = namaDokter;
        this.namaPasien = namaPasien;
        this.tanggal = tanggal;
        this.jam = jam;
        this.keluhanPasien = keluhanPasien;
        this.resep = resep;
        this.harga = harga;
        this.status = status;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getResep() {
        return resep;
    }

    public void setResep(String resep) {
        this.resep = resep;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama() {
        return namaPasien;
    }

    public void setNama(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public String getKeluhan() {
        return keluhanPasien;
    }

    public void setKeluhan(String keluhanPasien) {
        this.keluhanPasien = keluhanPasien;
    }

    public void tampilDetail() {
        System.out.println("---------------------------------------------------");
        System.out.println("Nama Pasien: " + this.namaPasien);
        System.out.println("Nama Dokter: " + this.namaDokter);
        System.out.println("Tanggal: " + this.tanggal);
        System.out.println("Jam: " + this.jam);
        System.out.println("Keluhan: " + this.keluhanPasien);
        if (resep.isEmpty() && harga == 0) {
            System.out.println("Resep: Mohon tunggu");
            System.out.println("Total harga: Mohon tunggu");
            System.out.println("Status: " + status);
        } else {
            System.out.println("Resep: " + resep);
            System.out.println("Total harga: " + harga);
            System.out.println("Status: " + status);
        }
        System.out.println("---------------------------------------------------");
    }
}