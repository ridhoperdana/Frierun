package net.ridhoperdana.Frierun.pojo_class;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by RIDHO on 12/26/2016.
 */

public class Geometry implements Serializable {
    @SerializedName("location")
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
