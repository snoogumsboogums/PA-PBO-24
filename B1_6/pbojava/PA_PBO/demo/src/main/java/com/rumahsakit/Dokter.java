package com.rumahsakit;

public class Dokter extends Poli{
    private int gaji;
    private String kodeDokter;

    public Dokter(String namaDokter, String spesialis, int gaji, String kodeDokter) {
        super(namaDokter, spesialis);
        this.gaji = gaji;
        this.kodeDokter = kodeDokter;
    }

    public int getGaji() {
        return gaji;
    }

    public void setGaji(int gaji) {
        this.gaji = gaji;
    }

    public String getKodeDokter() {
        return kodeDokter;
    }

    public void setKodeDokter(String kodeDokter) {
        this.kodeDokter = kodeDokter;
    }

    public void tampilDetail(){
        System.out.println("---------------------------------------------------");
        System.out.println("Kode Dokter : " + this.kodeDokter);
        System.out.println("Nama Dokter: "+ this.namaDokter);
        System.out.println("Spesialis Dokter : " + this.spesialis);
        System.out.println("Gaji Dokter : " + this.gaji);
        System.out.println("---------------------------------------------------");
    }

    public void tampilDokter(){
        System.out.println("---------------------------------------------------");
        System.out.println("Kode Dokter : " + this.kodeDokter);
        System.out.println("Nama Dokter: "+ this.namaDokter);
        System.out.println("---------------------------------------------------");
    }


    // overload
    public void tampilDetail(boolean isDetail){
        if (isDetail){
            tampilDetail();
        } else {
            super.tampil();
        }
    }
}