package com.madroft.csb.automate;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by chan on 31-12-2016.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public int Vehiclecount;

    public User(){}

    public User(String name,String Email,int vehiclecount)
    {
        this.username=name;
        this.email=Email;
        this.Vehiclecount=vehiclecount;
    }



}
