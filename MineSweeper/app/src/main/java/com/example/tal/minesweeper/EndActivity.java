package com.example.tal.minesweeper;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;



public class EndActivity extends FragmentActivity{
    private boolean mGameState;
    private int mGameTime;
    private int mGameDifficulty;
    private int[] anArray = new int[2];
    private final static int MAX_RECORDS = 10;
    private ImageButton menuButton;
    private ImageButton restartButton;
    private ImageButton submitButton;
    private EditText editText;
    private RelativeLayout relative;
    private Location mLastLocation;
    private ServiceConnection mConnection;
    private LocationService.LocalBinder mBinder;
    private boolean isBound = false;
    ////////////////////////
    private Cursor res;
    private DatabaseHelper myDb;
    private boolean needToDelete = false;
    private String mAddress;
    ////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        initService();

        extractDataFromBundle(); //getting my data from other activity
        myDb = new DatabaseHelper(EndActivity.this);//getting my data base
        res = myDb.getAllData(mGameDifficulty);//getting the items i want into my cursor

        //myDb.deleteAll(); //if i want to delete my table i will use this method
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        restartButton = (ImageButton) findViewById(R.id.restartButton);
        submitButton = (ImageButton) findViewById(R.id.submitButton);
        editText = (EditText) findViewById(R.id.playerNameText);

        if (mGameState) {
            relative = (RelativeLayout) findViewById(R.id.activity_end);
            relative.setBackgroundResource(R.drawable.winscreen);
            checkingIfWeNeedToDeleteAPlayer();//checks if we need to delete a player
        } else {
            relative = (RelativeLayout) findViewById(R.id.activity_end);
            relative.setBackgroundResource(R.drawable.losescreen);
        }
        setButtonListeners();
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(EndActivity.this, LocationService.class);
        EndActivity.this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (isBound) {
            this.unbindService(mConnection);
            isBound = false;
        }
        super.onStop();
    }
    private void initService(){
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("Service Connection", "bound to service");
                mBinder = (LocationService.LocalBinder)service;
                isBound = true;
                mLastLocation = mBinder.getLocation();
                if(mLastLocation != null)
                    Log.d("Location in Activity:", mLastLocation.toString());
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

    }
    //taking a look if there are more then 10 players in the game
    private void checkingIfWeNeedToDeleteAPlayer() {
        getBiggestIdAndTime();
        if (res.getCount() < MAX_RECORDS) {
            editText.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE); //starting the subbmit button
            needToDelete = false;
        } else if (anArray[1] > mGameTime) {
            editText.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            needToDelete = true;
        }
    }

    private void setButtonListeners() {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainMenu();
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restart();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() { // the button that save my data in the SQLite
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                if (name.length() > 8 || name.length() <= 0) {
                    makeNiceCrispyToastYumYum(getString(R.string.invalid_name_message));
                    return;
                } else {
                    mLastLocation = mBinder.getLocation();
                    mAddress = mBinder.getAddress();
                    //saving the data in the SQLite
                    if(needToDelete){
                        myDb.deleteData(anArray[0]);//deleting the player
                    }
                    if (mLastLocation != null && myDb.insertData(name, mLastLocation.getLatitude(), mLastLocation.getLongitude(),mAddress, mGameTime, mGameDifficulty))
                        makeNiceCrispyToastYumYum(getString(R.string.data_inserted));
                    else
                        makeNiceCrispyToastYumYum(getString(R.string.data_not_inserted));
                    mainMenu();
                }
            }
        });
    }

    private void makeNiceCrispyToastYumYum(String text) {
        Toast.makeText(EndActivity.this, text, Toast.LENGTH_LONG).show();
    }

    //this function returns the player that has the worst highScore
    private void getBiggestIdAndTime() {
        res = myDb.getAllData(mGameDifficulty);

        res.moveToNext();
        if (res.isAfterLast()) {
            return;
        }
        anArray[0] = res.getInt(0);
        anArray[1] = res.getInt(5);
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_key));

        mGameState = bundle.getBoolean(getString(R.string.game_end_key));
        mGameDifficulty = bundle.getInt(getString(R.string.game_level_key));
        mGameTime = bundle.getInt(getString(R.string.game_time_key));
    }

    private void restart() {
        Intent it = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.game_level_key), mGameDifficulty);
        it.putExtra(getString(R.string.bundle_key), bundle);
        startActivity(it);
        finish();
    }

    private void mainMenu() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }



}
