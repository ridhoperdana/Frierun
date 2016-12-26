package net.ridhoperdana.Frierun.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.interface_retrofit.GetPlace;
import net.ridhoperdana.Frierun.pojo_class.Results;
import net.ridhoperdana.Frierun.pojo_class.Tempat;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyGymActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location mLastLocation;
    FirebaseDatabase database;
    protected GoogleApiClient mGoogleApiClient;
    private Double lat, lng;
    private FirebaseAuth auth;
    private AutoCompleteTextView inputGeocoder;
    private Tempat places;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_gym);
        setTitle("Frierun - Gym Terdekat");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        Log.d("masuk", "onconnected nearby");

        if (auth.getCurrentUser() != null) {
            // User is logged in
            email = auth.getCurrentUser().getEmail();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
        Log.d("masuk", "onconnected nearby");
        LatLng its = null;
        try {
            its = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("masuk", "onconnected nearby");
            mMap.addMarker(new MarkerOptions().position(its).title("Lokasi Saya").
                    icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(auth.getCurrentUser().getPhotoUrl()))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(its, 13));
            nearbyGym();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal dapat lokasi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void getLocation()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("lokasi permision nearby", "belum");
            return;
        }
        try{
            mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("Lokasi masuk nearby", "try");
            if (mLastLocation == null){
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Log.d("Lokasi masuk nearby: ","network");
                if(mLastLocation==null)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("Lokasi masuk nearby: ","gps");
                }
                else
                {
                    Log.d("Lokasi netowk nearby: ","tidak null");
                }
            }
            else if (mLastLocation != null)
                Log.d("Location nearby: ","Lat = "+ mLastLocation.getLatitude() + " Lng");
        }catch (Exception e)
        {
            Log.d("Gagal lokasi terbaru", e.toString());
        }
    }

    private class myListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            goToLokasi(location.getLatitude(), location.getLongitude(), 15);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private void goToLokasi(Double lat, Double lng, float z) {
        LatLng lokasBaru = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(lokasBaru).title("Marker in " + lat + ", " + lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasBaru, z));
    }

    private void nearbyGym()
    {
        places = new Tempat();
        final ArrayList<Results> tampung = new ArrayList<>();
        StringBuilder urlbaru = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        urlbaru.append("location=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        urlbaru.append("&radius=" + 5000);
        urlbaru.append("&type=" + "gym");
        urlbaru.append("&rankBy=" + "prominence");
        urlbaru.append("&key=" + "AIzaSyBVuRYeAWRZhzeF9c51pOUfAC93iP7FgBE");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ridhoperdana.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetPlace service = retrofit.create(GetPlace.class);
        Call<Tempat> call = service.getGymNearby(urlbaru.toString());
        call.enqueue(new Callback<Tempat>() {
            @Override
            public void onResponse(Call<Tempat> call, Response<Tempat> response) {
                places = response.body();
                for(int i = 0; i< places.getResults().size(); i++)
                {
                    Log.d("response", "nearby");
//                    tampung.add(places.getResults().get(i));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(places.getResults().get(i).getGeometry().getLocation().getLat(),
                            places.getResults().get(i).getGeometry().getLocation().getLng())).
                            title(places.getResults().get(i).getName()));
//                    Log.d("List Nama Restaurant->", tampung.get(i).getName());
                }
            }

            @Override
            public void onFailure(Call<Tempat> call, Throwable t) {

            }
        });
    }

    private Bitmap getMarkerBitmapFromView(Uri url) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        Picasso.with(this).load(url).into(markerImageView);
//        markerImageView.setImageURI(foto);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
