package net.ridhoperdana.Frierun.interface_retrofit;

import net.ridhoperdana.Frierun.pojo_class.Address;
import net.ridhoperdana.Frierun.pojo_class.Tempat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by RIDHO on 12/17/2016.
 */

public interface GetPlace {
    @GET
    Call<Address> getAutoCompleteAddress(@Url String url);

    @GET
    Call<Tempat> getGymNearby(@Url String url);
}
