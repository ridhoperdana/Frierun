package net.ridhoperdana.Frierun;

import android.util.Log;

import net.ridhoperdana.Frierun.interface_retrofit.GetPlace;
import net.ridhoperdana.Frierun.pojo_class.Address;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by RIDHO on 12/17/2016.
 */

public class GetAutoCompleteAddress {
    Address request;
    public ArrayList<String> result;
    private StringBuilder url;
    private String lat, longt;

    public GetAutoCompleteAddress(String lat, String longt) {
        this.lat = lat;
        this.longt = longt;
    }

    public ArrayList<String> AutoComplete(String input) throws IOException {
        Log.d("masuk get auto", "address");
        Log.d("lat:" + lat, "Long: " + longt);
        Address tempat;
        url = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        try {
            url.append("input=" + URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url.append("&types=" + "address");
        url.append("&language=" + "id");
        url.append("&location=" + lat + "," + longt);
        url.append("&radius=" + 20000);
        url.append("&key=" + "AIzaSyBVuRYeAWRZhzeF9c51pOUfAC93iP7FgBE");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ridhoperdana.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetPlace service = retrofit.create(GetPlace.class);
        Call<Address> call = service.getAutoCompleteAddress(url.toString());

        Log.d("url: ", url.toString());

        tempat = call.execute().body();
        Log.d("tempat:", tempat.getPredictions().get(0).getDescription());
        result = new ArrayList<>(tempat.getPredictions().size());
        for(int i=0; i<tempat.getPredictions().size(); i++)
        {
            result.add(tempat.getPredictions().get(i).getDescription());
        }
        return result;
    }
}
