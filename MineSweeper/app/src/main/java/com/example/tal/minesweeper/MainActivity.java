package com.example.tal.minesweeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.example.tal.minesweeper.Logic.Record;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity{
    private final static int EASY =0;
    private final static int MEDIUM =1;
    private final static int HARD =2;
    private int gameLevel;
    private ImageButton tableBt;
    private ImageButton mapBt;
    private RadioGroup GameLevel_rg;
    private RadioButton radio_b;
    private ImageButton StartButton;
    private SharedPreferences myPref;
    private FragmentTable table;
    private FragmentMap map;
    private static HashSet<Record> easyRecordsSet;
    private static HashSet<Record> mediumRecordsSet;
    private static HashSet<Record> hardRecordsSet;
    private static ArrayList<Record> easyRecords;
    private static ArrayList<Record> mediumRecords;
    private static ArrayList<Record> hardRecords;
    // DataBase members
    private DatabaseHelper dataBase;
    private final int SCORE_COLUMN = 5;
    private final int ADDRESS_COLUMN = 4;
    private final int LONGITUDE_COLUMN = 3;
    private final int LATITUDE_COLUMN = 2;
    private final int NAME_COLUMN = 1;
    private final int ID_COLUMN = 0;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myPref = getPreferences(Context.MODE_PRIVATE);
        gameSound();//start the game sound
        createDataBase();
        storeRecordsAndGetAddresses();
        requestPermissions();
        openTable();
        openMap();
        startRadioButtons();
        startGame();
    }
    public void openTable() {

        tableBt = (ImageButton)findViewById(R.id.tableButton);

        tableBt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager fragnebtManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragnebtManager.beginTransaction();

                table = new FragmentTable();
                fragmentTransaction.add(R.id.fragment_container, table);
                fragmentTransaction.commit();
            }
        });
    }
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessage(getString(R.string.request_header), getString(R.string.location_request));
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
        }
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.INTERNET)) {
                showMessage(getString(R.string.request_header), getString(R.string.internet_request));
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.INTERNET},
                    PackageManager.PERMISSION_GRANTED);
        }
    }
    // for debug
    private void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void openMap() {
        mapBt = (ImageButton)findViewById(R.id.mapButton);

        mapBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragnebtManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragnebtManager.beginTransaction();

                map = new FragmentMap();

                fragmentTransaction.add(R.id.fragment_container, map);
                fragmentTransaction.commit();
            }
        });
    }

    // making a radio group and select the diffeclty of the game //
    private void startRadioButtons() {
        int i = myPref.getInt(getString(R.string.button_pressed),0); // saving in i the last selected difficulty
        if(i == EASY){ // compare i to each difficulty to find its value and bring the right radio button
            radio_b = (RadioButton) findViewById(R.id.radioButton);
            gameLevel = EASY;
        }else if(MEDIUM == i){
            radio_b = (RadioButton) findViewById(R.id.radioButton2);
            gameLevel = MEDIUM;
        }else{
            radio_b = (RadioButton) findViewById(R.id.radioButton3);
            gameLevel = HARD;
        }
        radio_b.setChecked(true); // check the radio button last picked by the user
        GameLevel_rg = (RadioGroup) findViewById(R.id.rg_level); //we are making a new radio group

        GameLevel_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group , int checkedId) { // we are listening to the radio group on changing radio bottons
                radio_b = (RadioButton) group.findViewById(checkedId);
                SharedPreferences.Editor editor = myPref.edit();

                switch (radio_b.getId()) {
                    case R.id.radioButton:
                        editor.putInt(getString(R.string.button_pressed),EASY);
                        gameLevel = EASY;
                        break;

                    case R.id.radioButton2:
                        editor.putInt(getString(R.string.button_pressed),MEDIUM);
                        gameLevel = MEDIUM;
                        break;

                    case R.id.radioButton3:
                        editor.putInt(getString(R.string.button_pressed),HARD);
                        gameLevel = HARD;
                        break;
                }
                editor.apply();
            }
        });
    }

    // starting the game by pressing the start botton //
    public void startGame() {
        StartButton = (ImageButton) findViewById(R.id.imageButton_Start);   //we are making a new Button
        StartButton.setOnClickListener(new View.OnClickListener() {         //when we are pressing the button this will happen
            @Override
            public void onClick(View view) {
                stopPlaying();
                startGameActivity();
            }
        });
    }

    public void startGameActivity() {
        Intent GameActivity = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.game_level_key), gameLevel);
        GameActivity.putExtra(getString(R.string.bundle_key) , bundle);
        startActivity(GameActivity);
        finish();
    }
    private void storeRecordsAndGetAddresses(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                easyRecordsSet = new HashSet<>();
                Cursor cursor = dataBase.getAllDataByOrder(EASY);
                while (cursor.moveToNext()) {
                    easyRecordsSet.add(new Record(cursor.getString(NAME_COLUMN)
                            ,new LatLng(cursor.getDouble(LATITUDE_COLUMN)
                            , cursor.getDouble(LONGITUDE_COLUMN))
                            ,cursor.getString(ADDRESS_COLUMN)
                            ,cursor.getInt(SCORE_COLUMN)
                            ,cursor.getInt(ID_COLUMN)));
                }
                easyRecords = new ArrayList<>(easyRecordsSet);
                Collections.sort(easyRecords);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediumRecordsSet = new HashSet<>();
                Cursor cursor = dataBase.getAllDataByOrder(MEDIUM);
                while (cursor.moveToNext()) {
                    mediumRecordsSet.add(new Record(cursor.getString(NAME_COLUMN)
                            ,new LatLng(cursor.getDouble(LATITUDE_COLUMN)
                            , cursor.getDouble(LONGITUDE_COLUMN))
                            ,cursor.getString(ADDRESS_COLUMN)
                            ,cursor.getInt(SCORE_COLUMN)
                            ,cursor.getInt(ID_COLUMN)));
                }
                mediumRecords = new ArrayList<>(mediumRecordsSet);
                Collections.sort(mediumRecords);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hardRecordsSet = new HashSet<>();
                Cursor cursor = dataBase.getAllDataByOrder(HARD);
                while (cursor.moveToNext()) {
                    hardRecordsSet.add(new Record(cursor.getString(NAME_COLUMN)
                            ,new LatLng(cursor.getDouble(LATITUDE_COLUMN)
                            , cursor.getDouble(LONGITUDE_COLUMN))
                            ,cursor.getString(ADDRESS_COLUMN)
                            ,cursor.getInt(SCORE_COLUMN)
                            ,cursor.getInt(ID_COLUMN)));
                }
                hardRecords = new ArrayList<>(hardRecordsSet);
                Collections.sort(hardRecords);
            }
        }).start();
    }

    private void gameSound() {
        mp = MediaPlayer.create(MainActivity.this, R.raw.startgame);
        mp.start();
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
    private void createDataBase() {
        dataBase = new DatabaseHelper(getBaseContext());
    }

    public static ArrayList<Record> getEasyRecords() {
        return easyRecords;
    }

    public static ArrayList<Record> getMediumRecords() {
        return mediumRecords;
    }

    public static ArrayList<Record> getHardRecords() {
        return hardRecords;
    }
}
