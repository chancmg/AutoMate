package com.madroft.csb.automate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authlistener;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private  LatLng cord;
    private FirebaseUser user;
    SharedPreferences pref;
    TextView tvfuel, tvmil;
    public static String username;
    SharedPreferences.Editor editpref;
    private GoogleMap mMap;
    String currentvehi, mil;
    Marker lastmarker;
    ValueEventListener mainlistener;
    long fuel = 0;
   double lat,lon;
    AlertDialog.Builder alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        startService(new Intent(this,FirebaseBackgroundService.class));
        // Get the map and register for the ready callback
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        user = mAuth.getCurrentUser();
        username = user.getUid();
        pref = getApplicationContext().getSharedPreferences(username, MODE_PRIVATE);
        editpref = pref.edit();
        tvfuel = (TextView) findViewById(R.id.fuel);
        tvmil = (TextView) findViewById(R.id.kilometer);
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Select or Add Vehicle");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        populateui();

        authlistener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                mAuth.signOut();
                return true;

            case R.id.vehicle:
                startActivity(new Intent(MainActivity.this, VehicleActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authlistener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authlistener != null) {
            mAuth.removeAuthStateListener(authlistener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(currentvehi).removeEventListener(mainlistener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Error Check","From main activity resume");
        populateui();
    }


    void populateui() {
        Log.e("Error Check","poulate function called");
        currentvehi = String.valueOf(pref.getInt("currentrb", -1) + 1);
        Log.e("RbID", currentvehi);
        Log.e("RbID", String.valueOf(pref.getInt("currentrb", -1)));
        mainlistener=mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(currentvehi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Error Check","mainactivity datalistener called");
                maindata data = dataSnapshot.getValue(maindata.class);

                if (data == null) {
                    alertDialog.show();
                } else {
                    fuel = data.fuel;
                    lat = data.lat;
                    lon = data.longi;
                    mil = data.milage;
                    tvfuel.setText(fuel + " Liters");
                    if (fuel < 3) {
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_motorcycle_white_18dp)
                                        .setContentTitle("AutoMate")
                                        .setContentText("Fuel is Low-Refuel your Vehicle");
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(0, builder.build());
                    }


                    float milage = fuel * Float.valueOf(mil);
                    tvmil.setText(String.valueOf(milage + " Km"));
                    cord=new LatLng(lat,lon);

                    if (mMap == null) {
                        return;
                    }


                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cord, 15f));

                    Marker markered;
                    MarkerOptions marker=new MarkerOptions()
                            .position(cord)
                            .title("YourVehicle")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    if(lastmarker!=null)
                    {
                        lastmarker.remove();
                    }

                    markered= mMap.addMarker(marker);
                    lastmarker=markered;
                    Log.e("LAT and LONG",String.valueOf(lat)+","+String.valueOf(lon));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                }
            });
        }
    }
}
