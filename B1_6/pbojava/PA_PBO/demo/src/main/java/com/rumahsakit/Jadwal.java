package com.rumahsakit;

public class Jadwal extends Poli {
    private int idJadwal;
    private String kodeDokter;
    private String tanggal;
    private String jamMulai;
    private String jamSelesai;
    private int kuota;

    // Updated constructor to include idJadwal
    public Jadwal(int idJadwal, String kodeDokter, String namaDokter, String spesialis, String tanggal, String jamMulai, String jamSelesai, int kuota) {
        super(namaDokter, spesialis);
        this.idJadwal = idJadwal;
        this.kodeDokter = kodeDokter;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kuota = kuota;
    }

    public int getIdJadwal() {
        return idJadwal;
    }

    public void setIdJadwal(int idJadwal) {
        this.idJadwal = idJadwal;
    }

    public String getKodeDokter() {
        return kodeDokter;
    }

    public void setKodeDokter(String kodeDokter) {
        this.kodeDokter = kodeDokter;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(String jamMulai) {
        this.jamMulai = jamMulai;
    }

    public String getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(String jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    @Override
    public void tampilDetail() {
        System.out.println("---------------------------------------------------");
        System.out.println("ID Jadwal : " + this.idJadwal);
        System.out.println("Kode Dokter : " + this.kodeDokter);
        System.out.println("Nama Dokter : " + this.namaDokter);
        System.out.println("Tanggal : " + this.tanggal);
        System.out.println("Jam : " + this.jamMulai + " - " + this.jamSelesai);
        System.out.println("Kuota : " + this.kuota);
        System.out.println("---------------------------------------------------");
    }

    public void tampilDetail(boolean isDetail) {
        if (isDetail) {
            tampilDetail();
        } else {
            super.tampil();
        }
    }
}