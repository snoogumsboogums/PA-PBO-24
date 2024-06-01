package com.rumahsakit;

public abstract class Poli implements interfaces{
    protected String namaDokter;
    protected String spesialis;

    public Poli(String namaDokter, String spesialis) {
        this.namaDokter = namaDokter;
        this.spesialis = spesialis;
    
    }

    // override interface methods
    public void tampil(){
        System.out.println ("Nama: "+ this.namaDokter);
        System.out.println ("Spesialis: "+ this.spesialis);

    }
    
    public String getNamaDokter() {
        return namaDokter;
    }

    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public void setSpesialis(String spesialis) {
        this.spesialis = spesialis;
    }

    // abstract method
    public abstract void tampilDetail();

}