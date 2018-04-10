package com.example.tal.minesweeper;


import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private final IBinder mBinder = new LocalBinder();
    private String mAddress;

    public LocationService() {
        super();
    }

    public Location getLocation(){
        return mCurrentLocation;
    }
    public String getmAddress(){return mAddress;}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void initLocation(){
        Log.v("Connection: ", "connected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                &&ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Connection: ", "has permission");

            mLocationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.v("Connection error: ", "GPS not enabled");
            }else{
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            }
            if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.v("Connection error: ", "Network not enabled");
            }else{
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(criteria,true),2000,0,this);

            if (mCurrentLocation == null)
                mCurrentLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria,true));
            if (mCurrentLocation == null) {
                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } if(mCurrentLocation == null){
                mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else{
                Log.v("Location in Service: ", mCurrentLocation.toString());
            }
        }
    }
    public void getAddress(){
        if(mCurrentLocation != null){
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> address = geocoder.getFromLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),1);
                mAddress = address.get(0).getAddressLine(0)+" , "+address.get(0).getLocality();
                Log.d("Address",mAddress + "end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LatLng getLocationFromAddress(String stAddress) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> address;
        try {
            address = geocoder.getFromLocationName(stAddress,1);
            return new LatLng(address.get(0).getLatitude(),address.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class LocalBinder extends Binder {
        String getAddress(){
            LocationService.this.getAddress();
            return LocationService.this.getmAddress();
        }
        LatLng getLocationFromAddress(String address){
            return LocationService.this.getLocationFromAddress(address);
        }
        Location getLocation() {
            initLocation();
            if(mCurrentLocation == null){
                Log.v("Location: ", "Null");
            }
            return LocationService.this.getLocation();
        }
    }

}
