package com.example.tal.minesweeper.Logic;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tal on 1/14/2017.
 */

public class Record implements Comparable<Record>{
    private int mGameTime;
    private LatLng mLatLng;
    private String mName;
    private int id;
    private String address;

    public Record(String name,LatLng latLng,String address,int gameTime , int id){
        this.mGameTime = gameTime;
        this.mLatLng = latLng;
        this.mName = name;
        this.id = id;
        this.address = address;
    }

    public int getmGameTime() {
        return mGameTime;
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public String getmName() {
        return mName;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {return address;}

    @Override
    public int compareTo(Record o) {
        if (o == null){
            return 1;
        }
        if(o.equals(this)){
            return 0;
        }else if( o.mGameTime > this.mGameTime){
            return -1;
        }else if(o.mGameTime < this.mGameTime){
            return 1;
        }else {
            if (this.id > o.id) {
                return 1;
            } else if (this.id < o.id) {
                return -1;
            }
        }
        return 0;
    }
}
