package com.madroft.csb.automate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chan on 02-01-2017.
 */

public class Vehicleadapter extends RecyclerView.Adapter<Vehicleadapter.MyViewHolder> {

    private Context mContext;
    private List<Vehicle> albumList;
    ValueEventListener refremove;
    private RadioButton lastchecked=null;
    DatabaseReference ref;
    FirebaseUser user;
    SharedPreferences pref;
    SharedPreferences.Editor editpref;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView overflow;
        public RadioButton radio;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            radio=(RadioButton)view.findViewById(R.id.radio);
        }
    }


    public Vehicleadapter(Context mContext, List<Vehicle> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

         pref= mContext.getSharedPreferences(MainActivity.username, MODE_PRIVATE);
         editpref=pref.edit();
        Vehicle album = albumList.get(position);
        holder.title.setText(album.getName());
        if(position==pref.getInt("currentrb",-1))
        {
            holder.radio.setChecked(true);
            lastchecked=holder.radio;
        }
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,position);
            }
        });

        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton checkedrb=holder.radio;
                editpref.putInt("currentrb",position);
                editpref.commit();


                if(lastchecked!=null && lastchecked!=checkedrb)
                {
                    lastchecked.setChecked(false);
                }

                lastchecked=checkedrb;
            }
        });


    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view,int Pos) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_vehicle, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(Pos));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
int pos;
        public MyMenuItemClickListener(int p) {
            this.pos=p;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    startseditactiviy(pos);
                  //  ref.child(user.getUid()).child("Vehicle").child(String.valueOf(pos+1)).removeEventListener(refremove);
                    Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_delete:
                   // deletevehicle(pos);
                    Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private void startseditactiviy(int pos)
    {
        Log.e("Error Check","From starteditactivity funtion");

        String key;


        key=String.valueOf(pos+1);
        ref= FirebaseDatabase.getInstance().getReference("users");
        user= FirebaseAuth.getInstance().getCurrentUser();
        Log.e("why this?",key);
        ref.child(user.getUid()).child("Vehicle").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vehicledetails v=dataSnapshot.getValue(Vehicledetails.class);
                Log.e("or this?",dataSnapshot.getKey());
                String mod=v.model;
                String mil=v.milage;
                String year=v.year;
                String lic=v.licenseno;
                int key=Integer.valueOf(dataSnapshot.getKey());

                Bundle b=new Bundle();
                b.putString("m",mod);
                b.putString("mi",mil);
                b.putString("y",year);
                b.putString("l",lic);
                b.putInt("key",key);

                Intent i=new Intent(mContext,EditVehicle.class);
                i.putExtras(b);
                mContext.startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
            }
        });


    }


   /* private void deletevehicle(int pos)
    {
        AlertDialog.Builder alertDialog;
        final String key;
        key=String.valueOf(pos+1);
        alertDialog=new AlertDialog.Builder(mContext);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Do You want to Delete?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                ref= FirebaseDatabase.getInstance().getReference("users");
                user= FirebaseAuth.getInstance().getCurrentUser();
                int count=pref.getInt("Vehiclecount",0);
                ref.child(user.getUid()).child("Vehiclecount").setValue(count-1);
                editpref.putInt("Vehiclecount",count-1);
                ref.child(user.getUid()).child("Vehicle").child(key).removeValue();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();


    }*/
}

