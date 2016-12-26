package net.ridhoperdana.Frierun.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.adapter.CustomAdapterFriendList;
import net.ridhoperdana.Frierun.pojo_class.User;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListActivity extends BaseActivity {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String email;
    private CustomAdapterFriendList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list2);
        setTitle("Frierun - Daftar Teman");
        Button tombolTambahTeman = (Button)findViewById(R.id.tombol_tambah_teman);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
            email = auth.getCurrentUser().getEmail();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        loadFriend();
        tombolTambahTeman.setOnClickListener(klik);
    }

    private View.OnClickListener klik = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.tombol_tambah_teman)
            {
                EditText inputEmailTeman = (EditText)findViewById(R.id.input_tambah_teman);
                final String email =  inputEmailTeman.getText().toString();
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
                                child(keys).child("email").setValue(email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    };

    private void loadFriend()
    {
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
                                        if(!listFriend.contains(daftarTeman[0]))
                                        {
                                            listFriend.add(daftarTeman[0]);
//                                            adapter.notifyDataSetChanged();
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
}
