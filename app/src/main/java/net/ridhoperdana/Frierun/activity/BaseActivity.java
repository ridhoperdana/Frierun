package net.ridhoperdana.Frierun.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import net.ridhoperdana.Frierun.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout fullView;
    private Toolbar toolbar;
    private int selectedNavItemId;
    public TextView nama;
    private ImageView profilpict;
    private FirebaseAuth auth;
    private View navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navHeader = navigationView.getHeaderView(0);
        nama = (TextView)navHeader.findViewById(R.id.name);
        profilpict = (ImageView)navHeader.findViewById(R.id.img_profile);
        auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getDisplayName();
        nama.setText(email);
        Picasso.with(this).load(auth.getCurrentUser().getPhotoUrl()).into(profilpict);
//        profilpict.setImageURI(auth.getCurrentUser().getPhotoUrl());
        Log.d("email: ", email);
        setSupportActionBar(toolbar);
        setUpNavView();
    }

    public void SignOut()
    {
        auth.signOut();

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fullView.closeDrawer(GravityCompat.START);
        selectedNavItemId = item.getItemId();

        return onOptionsItemSelected(item);
    }

    protected void setUpNavView()
    {
        navigationView.setNavigationItemSelectedListener(this);

        if( useDrawerToggle()) { // use the hamburger menu
            drawerToggle = new ActionBarDrawerToggle(this, fullView, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);

            fullView.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
//        else if(useToolbar() && getSupportActionBar() != null) {
//            // Use home/back button instead
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(getResources()
//                    .getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
//        }
    }

    protected boolean useToolbar()
    {
        return true;
    }

    protected boolean useDrawerToggle()
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if(id==R.id.tombolRiwayat)
//        {
//            startActivity(new Intent(this, RiwayatActivity.class));
//        }
        if(id==R.id.tombollogout)
        {
            SignOut();
        }
        else if(id==R.id.tombolTrack)
        {
            startActivity(new Intent(this, TrackLocationActivity.class));
        }
//        else if(id==R.id.tombolBeranda)
//        {
//            startActivity(new Intent(this, MapsActivity.class));
//        }
        else if(id==R.id.tombolDaftarTemanOnline)
        {
            startActivity(new Intent(this, FriendOnlineListActivity.class));
        }
        else if(id==R.id.tombolDaftarTeman)
        {
            startActivity(new Intent(this, FriendListActivity.class));
        }
        else if(id==R.id.tombolGymTerdekat)
        {
            startActivity(new Intent(this, NearbyGymActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
