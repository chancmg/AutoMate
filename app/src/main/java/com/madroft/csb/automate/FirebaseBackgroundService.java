package com.madroft.csb.automate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by chan on 07-01-2017.
 */

public class FirebaseBackgroundService extends Service {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    SharedPreferences pref;
    SharedPreferences.Editor editpref;
    long fuel = 0;
    private ValueEventListener handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        user = mAuth.getCurrentUser();
        pref = getApplicationContext().getSharedPreferences(user.getUid(), MODE_PRIVATE);
        editpref = pref.edit();
        String currentvehi = String.valueOf(pref.getInt("currentrb", -1) + 1);
        mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(currentvehi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Error Check","service datalistener called");
                maindata data = dataSnapshot.getValue(maindata.class);
                if (data == null) {
                    return;
                } else {
                    fuel = data.fuel;

                    if (fuel < 3) {
                        postNotif(String.valueOf(fuel));
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void postNotif(String notifString) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_motorcycle_white_18dp)
                        .setContentTitle("AutoMate")
                        .setContentText("Fuel is Low-"+notifString+" Liters");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }
}
