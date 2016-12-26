package net.ridhoperdana.Frierun.pojo_class;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RIDHO on 12/18/2016.
 */

public class User {
    private String nama, email, urlFoto;
    private RiwayatLokasi riwayat;
    private int statusOnline;
    private HashMap<String, String> Lokasi_sekarang;
    private String uid;

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o == null || o.getClass() != getClass()) {
            result = false;
        } else {
            User employee = (User) o;
            if (this.nama.equals(employee.getNama()) && this.email.equals(employee.getEmail()) && this.urlFoto == employee.getUrlFoto()) {
                result = true;
            }
        }
        return result;
    }

    public HashMap<String, String> getLokasi_sekarang() {
        return Lokasi_sekarang;
    }

    public void setLokasi_sekarang(HashMap<String, String> lokasi_sekarang) {
        Lokasi_sekarang = lokasi_sekarang;
    }

    public String getUid() {
        return uid;
    }

    public RiwayatLokasi getRiwayat() {
        return riwayat;
    }

    public void setRiwayat(RiwayatLokasi riwayat) {
        this.riwayat = riwayat;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatusOnline() {
        return statusOnline;
    }

    public void setStatusOnline(int statusOnline) {
        this.statusOnline = statusOnline;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
