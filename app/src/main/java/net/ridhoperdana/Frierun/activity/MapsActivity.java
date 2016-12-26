package net.ridhoperdana.Frierun.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.ridhoperdana.Frierun.Direction;
import net.ridhoperdana.Frierun.adapter.PlaceAdapter;
import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.pojo_class.Riwayat;
import net.ridhoperdana.Frierun.pojo_class.User;

import org.w3c.dom.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location mLastLocation;
    FirebaseDatabase database;
    protected GoogleApiClient mGoogleApiClient;
    private Double lat, lng;
    private FirebaseAuth auth;
    private String email;
    private String clubkey;
    private AutoCompleteTextView inputGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyLocationPermissions(this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
            email = auth.getCurrentUser().getEmail();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        Button buttonCari = (Button) findViewById(R.id.geocoderButton);
        Button buttonGo = (Button) findViewById(R.id.buttonGo);
        Button tombolRefresh = (Button) findViewById(R.id.tombol_refresh);
        inputGeocoder = (AutoCompleteTextView) findViewById(R.id.input_cari);
        Button tombolTambahTeman = (Button) findViewById(R.id.tombol_tambah_teman);



//        getLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonGo.setOnClickListener(klik);
        buttonCari.setOnClickListener(klik);
        tombolTambahTeman.setOnClickListener(klik);
        tombolRefresh.setOnClickListener(klik);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myListener();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
        LatLng its = null;
        try {
            inputGeocoder.setAdapter(new PlaceAdapter(this, R.layout.autocomplete, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude())));
            inputGeocoder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String description = (String) parent.getItemAtPosition(position);
                    Geocoder g = new Geocoder(getBaseContext());
                    List<Address> daftar;
                    try {
                        daftar = g.getFromLocationName(description, 5);
                        Address alamat = daftar.get(0);
                        String namaAlamat = alamat.getAddressLine(0);
                        lat = alamat.getLatitude();
                        lng = alamat.getLongitude();
                        sembunyikanKeyboard(view);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            its = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("masuk", "onconnected");

            final DatabaseReference myRef = database.getReference().child("User");
            Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        String key = myRef.push().getKey();
                        User pengguna = new User();
                        pengguna.setNama(auth.getCurrentUser().getDisplayName());
                        pengguna.setUrlFoto(String.valueOf(auth.getCurrentUser().getPhotoUrl()));
                        pengguna.setEmail(auth.getCurrentUser().getEmail());
                        pengguna.setUid(key);
                        myRef.child(key).setValue(pengguna);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mMap.addMarker(new MarkerOptions().position(its).title("Marker in ITS").icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(its, 15));
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

    private class myListener implements LocationListener {
        private EditText lat, lngt;

        @Override
        public void onLocationChanged(Location location) {
            lat = (EditText) findViewById(R.id.inputLatitude);
            lngt = (EditText) findViewById(R.id.inputLongitude);

            lat.setText(String.valueOf(location.getLatitude()));
            lngt.setText(String.valueOf(location.getLongitude()));

            goToPeta();
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

    private static void verifyLocationPermissions(Activity activity) {
        int permission, permission2;
        String[] PERMISSIONS_LOCATION = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        // Check if we have write permission
        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }

    private void goToPeta() {
        EditText inputLongitude = (EditText) findViewById(R.id.inputLongitude);
        EditText inputLatitude = (EditText) findViewById(R.id.inputLatitude);
        String tampungLongitude = inputLongitude.getText().toString();
        String tampungLatitude = inputLatitude.getText().toString();
        Double DoubleLongitude = Double.parseDouble(tampungLongitude);
        Double DoubleLatitude = Double.parseDouble(tampungLatitude);
        goToLokasi(DoubleLatitude, DoubleLongitude, 15);
    }

    private void goToLokasi(Double lat, Double lng, float z) {
        LatLng lokasBaru = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(lokasBaru).title("Marker in " + lat + ", " + lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasBaru, z));
        final DatabaseReference myRef = database.getReference().child("User");
        final Riwayat riwayat = new Riwayat();

        riwayat.setLat(String.valueOf(lat));
        riwayat.setLongt(String.valueOf(lng));

        Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Log.d("child:", childSnapshot.getKey());
                    String keys = myRef.child(childSnapshot.getKey()).push().getKey();
                    myRef.child(childSnapshot.getKey()).child("Riwayat").
                            child(getDateOnly(getDate())).
                            child(getTimeOnly(getDate())).setValue(riwayat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        route(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), lokasBaru, "driving");
    }

    protected void route(LatLng sourcePosition, LatLng destPosition, String mode) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    Direction md = new Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(getApplicationContext().getResources().getColor(R.color.colorPrimary));

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = mMap.addPolyline(rectLine);
//                    md.getDurationText(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };

        new Direction(handler, sourcePosition, destPosition, Direction.MODE_DRIVING).execute();
    }

    private void cariLatLong(String alamat1, String alamat2) throws IOException {
        Geocoder g = new Geocoder(getBaseContext());
        Geocoder g2 = new Geocoder(getBaseContext());
        List<android.location.Address> daftar = g.getFromLocationName(alamat1, 1);
        List<android.location.Address> daftar2 = g2.getFromLocationName(alamat2, 1);
        Address alamat = daftar.get(0);
        Address alamat_2 = daftar2.get(0);
        hitungJarak(alamat.getLatitude(), alamat.getLongitude(), alamat_2.getLatitude(), alamat_2.getLongitude());
    }

    private void goCari() throws IOException {
        goToLokasi(lat, lng, 15);
    }

    private View.OnClickListener klik = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.buttonGo) {
                sembunyikanKeyboard(view);
                goToPeta();
            } else if (view.getId() == R.id.geocoderButton) {
                try {
                    sembunyikanKeyboard(view);
                    goCari();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (view.getId() == R.id.tombol_tambah_teman) {
                final EditText inputTambahTeman = (EditText) findViewById(R.id.input_tambah_teman);
                final DatabaseReference myRef = database.getReference().child("User");
                final String[] key = new String[1];

                Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Log.d("child:", childSnapshot.getKey());
                            key[0] = childSnapshot.getKey();
                        }
                        final DatabaseReference myRefTeman = database.getReference().child("Teman");
                        String keys = myRef.child(key[0]).push().getKey();
                        myRefTeman.child(key[0]).
                                child(keys).child("email").setValue(inputTambahTeman.getText().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else if (view.getId() == R.id.tombol_refresh) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    };

    public String getDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.d("date: ", formattedDate);
        return formattedDate;
    }

    public String getTimeOnly(String value)
    {
        String[] hasil = value.split(" ");
        String date = hasil[1];
        return date;
    }

    public String getDateOnly(String value)
    {
        String[] hasil = value.split(" ");
        String date = hasil[0];
        return date;
    }

    private void sembunyikanKeyboard(View view) {
        InputMethodManager a = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(view.getWindowToken(), 20);
    }

    private void hitungJarak(Double latAsal, Double lngAsal, Double latTujuan, Double lngTujuan) {
        Location asal = new Location("asal");
        Location tujuan = new Location("tujuan");
        tujuan.setLatitude(latTujuan);
        tujuan.setLongitude(lngTujuan);
        asal.setLatitude(latAsal);
        asal.setLongitude(lngAsal);
        float jarak = (float) asal.distanceTo(tujuan) / 1000;
        String hasilJarak = String.valueOf(jarak);
        Toast.makeText(getBaseContext(), "Jarak: " + hasilJarak, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.petaNormal)
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if (item.getItemId() == R.id.petaHybrid)
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if (item.getItemId() == R.id.petaTerrain)
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        else if (item.getItemId() == R.id.petaSatellite)
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        return super.onOptionsItemSelected(item);
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
            Log.d("lokasi permision", "belum");
            return;
        }
        try{
            mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("Lokasi masuk", "try");
            if (mLastLocation == null){
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Log.d("Lokasi masuk : ","network");
                if(mLastLocation==null)
                {
                    try{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }catch (Exception e)
                    {
                        Log.d("error lokasi: ", e.toString());
                    }

                    Log.d("Lokasi masuk : ","gps");
                }
                else
                {
                    Log.d("Lokasi netowk : ","tidak null");
                }
            }
            else if (mLastLocation != null)
                Log.d("Location : ","Lat = "+ mLastLocation.getLatitude() + " Lng");
        }catch (Exception e)
        {
            Log.d("Gagal lokasi terbaru", e.toString());
        }
    }

    private Bitmap getMarkerBitmapFromView() {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        Picasso.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(markerImageView);
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
