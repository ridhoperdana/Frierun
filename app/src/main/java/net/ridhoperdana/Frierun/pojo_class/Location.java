package net.ridhoperdana.Frierun.pojo_class;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by RIDHO on 12/26/2016.
 */

public class Location implements Serializable {

    @SerializedName("lat")
    protected Double lat;

    @SerializedName("lng")
    protected Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
