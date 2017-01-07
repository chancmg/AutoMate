package com.madroft.csb.automate;

/**
 * Created by chan on 06-01-2017.
 */

public class maindata
{
    public int fuel;    double lat,longi;
        String milage;

    maindata()
    {}
    public maindata(int f,double l,double lon,String m)
    {
        this.fuel=f;
        this.lat=l;
        this.longi=lon;
        this.milage=m;
    }
}
