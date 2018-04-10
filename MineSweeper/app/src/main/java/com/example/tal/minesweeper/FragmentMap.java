package com.example.tal.minesweeper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.tal.minesweeper.Logic.Record;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;



public class FragmentMap extends DialogFragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    private View rootView;
    private Button button;
    private boolean wait = true;
    private boolean isBound = false;
    private ServiceConnection mConnection;
    private LocationService.LocalBinder mBinder;
    private Location mCurrentLocation;
    private static ArrayList<Record> easyRecords = new ArrayList<>();
    private static ArrayList<Record> mediumRecords = new ArrayList<>();
    private static ArrayList<Record> hardRecords = new ArrayList<>();
    private GoogleMap map;
    private LatLng latLng;

    public FragmentMap() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        button = (Button) rootView.findViewById(R.id.exitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wait) {
                    dismiss();
                }
            }
        });
        initService();
        MapView mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wait = false;
                    Thread.sleep(4000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            putFlags();
                            rootView.findViewById(R.id.progressBarMap).setVisibility(View.GONE);
                            wait = true;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onStop() {
        if (isBound) {
            getActivity().unbindService(mConnection);
            isBound = false;
        }
        super.onStop();
    }

    private void initService() {
        Intent intent = new Intent(getActivity(), LocationService.class);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("Service Connection", "bound to service");
                mBinder = (LocationService.LocalBinder) service;
                isBound = true;
                mCurrentLocation = mBinder.getLocation();
                if (mCurrentLocation != null)
                    Log.d("Location in Map: ", mCurrentLocation.toString());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void putFlags() {
        getAddressesAndRecords();
        if (map != null) {
            Log.d("Map is good", "it isnt null");
            for (int i = 0; i < easyRecords.size(); i++) {
                Record record = easyRecords.get(i);
                LatLng latiLongi = mBinder.getLocationFromAddress(record.getAddress());
                createMarker(latiLongi,record);
            }
            for (int i = 0; i < mediumRecords.size(); i++) {
                Record record = mediumRecords.get(i);
                LatLng latiLongi = mBinder.getLocationFromAddress(record.getAddress());
                createMarker(latiLongi,record);
            }
            for (int i = 0; i < hardRecords.size(); i++) {
                Record record = hardRecords.get(i);
                LatLng latiLongi = mBinder.getLocationFromAddress(record.getAddress());
                createMarker(latiLongi,record);
            }
        }
    }
    private void getAddressesAndRecords() {
        easyRecords = MainActivity.getEasyRecords();
        mediumRecords = MainActivity.getMediumRecords();
        hardRecords = MainActivity.getHardRecords();
    }

    private void createMarker(LatLng latiLongi ,Record record) {
        map.addMarker(new MarkerOptions()
                .position(latiLongi)
                .title(record.getAddress())
                .snippet(" Name: " + record.getmName() + " Time: " + record.getmGameTime())
                .visible(true));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(marker.getPosition(),(map.getMaxZoomLevel()+map.getMinZoomLevel())/4)));
        marker.showInfoWindow();
        return true;
    }
}
