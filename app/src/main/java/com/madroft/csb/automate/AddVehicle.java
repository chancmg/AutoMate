package com.madroft.csb.automate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.madroft.csb.automate.MainActivity.username;

/**
 * Created by chan on 03-01-2017.
 */

public class AddVehicle extends AppCompatActivity {

    private EditText inputmodel, inputyear, inputmilage,inputlicense;
    private Button btnaddvehicle;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private Vehicledetails vdetails;
    SharedPreferences pref;
    SharedPreferences.Editor editpref;
    int Count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        pref = getApplicationContext().getSharedPreferences(username, MODE_PRIVATE);
        editpref = pref.edit();

        btnaddvehicle = (Button) findViewById(R.id.addvehicle);

        inputmodel = (EditText) findViewById(R.id.vehicleModel);
        inputyear = (EditText) findViewById(R.id.vehicleyear);
        inputmilage = (EditText) findViewById(R.id.vehiclemilage);
        inputlicense = (EditText) findViewById(R.id.vehiclelicense);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnaddvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model=inputmodel.getText().toString().trim();
                String milage=inputmilage.getText().toString().trim();
                String year=inputyear.getText().toString().trim();
                String license=inputlicense.getText().toString().trim();

                if (TextUtils.isEmpty(model)) {
                    Toast.makeText(getApplicationContext(), "Enter Model!", Toast.LENGTH_SHORT).show();
                    return;
                }    if (TextUtils.isEmpty(milage)) {
                    Toast.makeText(getApplicationContext(), "Enter Milage!", Toast.LENGTH_SHORT).show();
                    return;
                }    if (TextUtils.isEmpty(year)) {
                    Toast.makeText(getApplicationContext(), "Enter year!", Toast.LENGTH_SHORT).show();
                    return;
                }    if (TextUtils.isEmpty(license)) {
                    Toast.makeText(getApplicationContext(), "Enter license!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);


                vdetails=new Vehicledetails(model,milage,year,license);
                //getting current user
                FirebaseUser user=mAuth.getCurrentUser();
                Count=pref.getInt("Vehiclecount",0);
               /* mFirebaseDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            User user=dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.e("TEst", "User data is null!");
                            return;
                        }

                       Count=user.Vehiclecount;
                        Log.e("Count", String.valueOf(Count));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });*/

                mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(String.valueOf(Count+1)).setValue(vdetails);
                mFirebaseDatabase.child(user.getUid()).child("Vehiclecount").setValue(Count+1);
                editpref.putInt("Vehiclecount",Count+1);
                editpref.commit();
                progressBar.setVisibility(View.GONE);
                mFirebaseDatabase.child(user.getUid()).child("Vehicle").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(getApplicationContext(),"Added-vehicle:"+String.valueOf(Count+1),Toast.LENGTH_SHORT).show();
                        inputmodel.setText("");
                        inputmilage.setText("");
                        inputlicense.setText("");
                        inputyear.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });
    }
}
