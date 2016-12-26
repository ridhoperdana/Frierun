package net.ridhoperdana.Frierun.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.pojo_class.Riwayat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TrackLocationActivity extends BaseActivity {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Location mLastLocation;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);
        setTitle("Frierun - Lari");
        verifyLocationPermissions(this);

        Button tombolStart = (Button)findViewById(R.id.tombol_start_running);
        Button tombolStop = (Button)findViewById(R.id.tombol_stop_running);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        locationListener = new myListener();
        tombolStart.setOnClickListener(klik);
        tombolStop.setOnClickListener(klik);
    }

    protected View.OnClickListener klik = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.tombol_start_running)
            {
                aktifkanGPS(true);
            }
            else if(v.getId()==R.id.tombol_stop_running)
            {
                aktifkanGPS(false);
            }
        }
    };

    private class myListener implements LocationListener {

        @Override
        public void onLocationChanged(final Location location) {
            final DatabaseReference myRef = database.getReference().child("User");
            final Riwayat riwayat = new Riwayat();

            riwayat.setLat(String.valueOf(location.getLatitude()));
            riwayat.setLongt(String.valueOf(location.getLongitude()));
            Toast.makeText(getApplicationContext(), "lat: " + riwayat.getLat(), Toast.LENGTH_SHORT).show();

            Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        Log.d("child:", childSnapshot.getKey());
                        String keys = myRef.child(childSnapshot.getKey()).push().getKey();
                        myRef.child(childSnapshot.getKey()).child("Riwayat").
                                child(getDateOnly(getDate())).
                                child(getTimeOnly(getDate())).setValue(riwayat);
                        myRef.child(childSnapshot.getKey()).child("Lokasi_sekarang").setValue(riwayat);
                        myRef.child(childSnapshot.getKey()).child("statusOnline").setValue(1);
                        Toast.makeText(getApplicationContext(), "lat: " + riwayat.getLat(), Toast.LENGTH_SHORT).show();
                        Log.d("masuk insert", "benar");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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

    private void aktifkanGPS(boolean status) {
//        verifyLocationPermissions(this);
        final DatabaseReference myRef = database.getReference().child("User");

        Query query = myRef.orderByChild("email").equalTo(auth.getCurrentUser().getEmail());
        if (status) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        Log.d("child:", childSnapshot.getKey());
//                        String keys = myRef.child(childSnapshot.getKey()).push().getKey();
                        myRef.child(childSnapshot.getKey()).child("statusOnline").setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Toast.makeText(this, "GPS AKTIF WAKTU: " + 2000 + "JARAK: " + 2, Toast.LENGTH_SHORT).show();
        } else {
            locationManager.removeUpdates(locationListener);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        Log.d("child:", childSnapshot.getKey());
//                        String keys = myRef.child(childSnapshot.getKey()).push().getKey();
                        myRef.child(childSnapshot.getKey()).child("statusOnline").setValue(0);
                        myRef.child(childSnapshot.getKey()).child("Lokasi_sekarang").removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Toast.makeText(this, "GPS TIDAK AKTIF", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
