package net.ridhoperdana.Frierun.pojo_class;

import java.util.ArrayList;

/**
 * Created by RIDHO on 12/25/2016.
 */

public class RiwayatLokasi {
    ArrayList<Tanggal> tanggals;

    public ArrayList<Tanggal> getTanggals() {
        return tanggals;
    }

    public void setTanggals(ArrayList<Tanggal> tanggals) {
        this.tanggals = tanggals;
    }
}

class Tanggal
{
    ArrayList<Jam> jams;

    public ArrayList<Jam> getJams() {
        return jams;
    }

    public void setJams(ArrayList<Jam> jams) {
        this.jams = jams;
    }
}

class Jam
{
    private String lat, longt;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongt() {
        return longt;
    }

    public void setLongt(String longt) {
        this.longt = longt;
    }
}
