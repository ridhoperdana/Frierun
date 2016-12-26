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
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.maps.model.Marker;
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
import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.adapter.CustomAdapterFriendList;
import net.ridhoperdana.Frierun.pojo_class.User;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendOnlineListActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private int status=1;
    private ArrayList<Marker> listMarker;
    private Marker m;
    private CustomAdapterFriendList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        setTitle("Frierun - Teman Online");
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
            email = auth.getCurrentUser().getEmail();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
//        getLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myListener();
        recyclerView = (RecyclerView)findViewById(R.id.rv);
    }

    private void loadFriend()
    {
        final HashMap<String, Marker> marker = new HashMap<>();
        final Marker[] mk = {null};
        listMarker = new ArrayList<>();
        final ArrayList<User> listFriend = new ArrayList<>();

        final ArrayList<User> list = new ArrayList<User>();
        final User[] daftarTeman = new User[1];
        final User[] user = new User[1];
        final DatabaseReference myRef = database.getReference().child("User");
        Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("masuk", "data ada");
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    user[0] = childSnapshot.getValue(User.class);
                    list.add(user[0]);
                }
                Log.d("masuk", dataSnapshot.getKey());
                final DatabaseReference myRefTeman = database.getReference().child("Teman").child(user[0].getUid());
                myRefTeman.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            Log.d("nama teman: ", childSnapshot.getValue().toString().split("\\{")[1].split("\\}")[0].split("=")[1]);
                            Query queryTeman = myRef.orderByChild("email").equalTo(childSnapshot.getValue().toString()
                                    .split("\\{")[1].split("\\}")[0].split("=")[1]);
                            queryTeman.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        daftarTeman[0] = childSnapshot.getValue(User.class);
                                        Log.d("nama teman add: ", daftarTeman[0].getNama());
                                        MarkerOptions a = new MarkerOptions();
                                        String value = daftarTeman[0].getUid();
                                        if(childSnapshot.hasChild("Lokasi_sekarang"))
                                        {
                                            Log.d("masuk aktif", "jos");

                                            if(!listFriend.contains(daftarTeman[0]))
                                            {
                                                listFriend.add(daftarTeman[0]);
                                            }
                                            if(marker.containsKey(value))
                                            {
                                                Log.d("masuk remove", "sukses");
                                                marker.get(value).setPosition(new LatLng(Double.parseDouble(daftarTeman[0].getLokasi_sekarang().get("lat")),
                                                        Double.parseDouble(daftarTeman[0].getLokasi_sekarang().
                                                                get("longt"))));
//                                                mk[0] = marker.get(value);
                                            }
                                            else
                                            {
                                                mk[0] = mMap.addMarker(a.
                                                        position(new LatLng(Double.parseDouble(daftarTeman[0].getLokasi_sekarang().get("lat")),
                                                                Double.parseDouble(daftarTeman[0].getLokasi_sekarang().
                                                                        get("longt")))).title(daftarTeman[0].getNama()).icon(BitmapDescriptorFactory.
                                                        fromBitmap(getMarkerBitmapFromView(Uri.parse(daftarTeman[0].getUrlFoto())))));
                                                Log.d("posisi marker", "tidak null");
                                            }
                                            if(!marker.containsKey(daftarTeman[0].getUid()))
                                            {
                                                marker.put(daftarTeman[0].getUid(), mk[0]);
                                                Log.d("marker teman: " + daftarTeman[0].getNama(), "ditambahkan");
                                            }
                                        }
                                        else
                                        {
                                            listFriend.remove(daftarTeman[0]);
//                                            if(marker.containsKey(value) && !childSnapshot.hasChild("Lokasi_sekarang"))
//                                            {
//                                                Log.d("masuk", "remove marker");
//                                                marker.get(value).remove();
//                                            }
                                        }
                                    }
                                    adapter = new CustomAdapterFriendList(listFriend, getApplicationContext());
                                    recyclerView.setAdapter(adapter);
                                    linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
        LatLng its = null;
        try {
            its = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d("masuk", "onconnected");
            loadFriend();
            mMap.addMarker(new MarkerOptions().position(its).title("Marker in ITS").
                    icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(auth.getCurrentUser().getPhotoUrl()))));
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

    private void goCari() throws IOException {
        goToLokasi(lat, lng, 15);
    }

    private View.OnClickListener klik = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.tombol_refresh) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lokasBaru = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasBaru, 15));
            }
        }
    };

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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
