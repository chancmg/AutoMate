package com.madroft.csb.automate;

import android.content.Intent;
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

/**
 * Created by chan on 03-01-2017.
 */

public class EditVehicle extends AppCompatActivity {
    private EditText inputmodel, inputyear, inputmilage,inputlicense;
    private Button btnaddvehicle;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    int Count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_vehicle);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        btnaddvehicle = (Button) findViewById(R.id.addvehicle);

        Log.e("Error Check","From Create");
        inputmodel = (EditText) findViewById(R.id.vehicleModel);
        inputyear = (EditText) findViewById(R.id.vehicleyear);
        inputmilage = (EditText) findViewById(R.id.vehiclemilage);
        inputlicense = (EditText) findViewById(R.id.vehiclelicense);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent i=getIntent();
        Bundle b=i.getExtras();
        inputmodel.setText(b.getString("m"));
        inputmilage.setText(b.getString("mi"));
        inputlicense.setText(b.getString("l"));
        inputyear.setText(b.getString("y"));
        Count=b.getInt("key");

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



                //getting current user
                FirebaseUser user=mAuth.getCurrentUser();

                mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(String.valueOf(Count)).child("model").setValue(model);
                mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(String.valueOf(Count)).child("milage").setValue(milage);
                mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(String.valueOf(Count)).child("year").setValue(year);
                mFirebaseDatabase.child(user.getUid()).child("Vehicle").child(String.valueOf(Count)).child("licenseno").setValue(license);
                progressBar.setVisibility(View.GONE);
                Log.e("Error Check","From editclick");
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Error Check","From Resume");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
