package com.rumahsakit;

public class Akun {
    private int id;
    private String username, password;

    public Akun(int id, String username, String pwd) {
        this.id = id;
        this.username = username;
        this.password = pwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
