package com.example.tal.minesweeper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tal.minesweeper.Logic.Board;
import com.example.tal.minesweeper.Logic.Game;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements GameRotationService.IRotationListener {
    private int count = 0 ;
    private int numOfUseCounter;
    private Game mGame;
    private GridView mGrid;
    private int mGameLevel;
    private TextView timer_v , numberOfFlags_v;
    private Switch switch1;
    private Timer timer;
    private Toast toast;
    private  MediaPlayer mp;
    private  MediaPlayer mp2;
    private  MediaPlayer mp3;

    private GameRotationService.LocalBinder mBinder;
    private boolean isBound = false;
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        extractDataFromBundle();
        initService();
        gameSound();
        mGame = new Game(mGameLevel); //making a new game
        startGrid();
        toast = new Toast(getBaseContext());
    }
    @Override
    protected void onResume() {
        Intent intent = new Intent(this, GameRotationService.class);
        this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (isBound){
            unbindService(mConnection);
            isBound = false;
        }
        super.onPause();
    }

    private void initService() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("Service Connection", "bound to Rotation service");
                mBinder = (GameRotationService.LocalBinder) service;
                isBound = true;
                mBinder.registerListener(GameActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
    }

    private void startGrid(){
        mGrid = (GridView) findViewById(R.id.gridview); //getting ower gridView
        mGrid.setAdapter(new TileAdapter(this ,mGame.getBoard())); //setting a new addapter
        mGrid.setNumColumns(mGame.getBoard().getRowColSize()); //setting number of columns
        switchUse();

        // when we are clicking on a Tile
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGame.playTile(position);

                numberOfFlags();//showing the number of flags
                timerStart();//starting the timer

                ((TileAdapter)mGrid.getAdapter()).notifyDataSetChanged();

                if (mGame.getIsWon() || mGame.getIsLost()) {
                    endGame();
                }
            }
        });

        //placing some flags on the tiles or removing them
        mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Board board = mGame.getBoard();


                timerStart();//starting the timer
                if(!board.getTileByPosition(i).isPressed()){
                    board.getTileByPosition(i).setFlag();
                    if(board.getTileByPosition(i).isFlagged()){
                        mGame.getBoard().addFlag();
                    } else{
                        mGame.getBoard().removeFlag();
                    }
                }
                numberOfFlags();//showing the number of flags
                ((TileAdapter)mGrid.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });
    }

    //this function making ower swich to pot flags on tiles
    private void switchUse() {
        switch1 = (Switch) findViewById(R.id.switchFlags);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGame.setFlag();
            }
        });
    }

    //this function showoing on the map the number of flags that we have
    private void numberOfFlags() {
        int i = mGame.getBoard().getNumOfMines()-mGame.getBoard().getNumOfFlags();
        numberOfFlags_v = (TextView) findViewById(R.id.flagsNumber);
        numberOfFlags_v.setText(""+i);
    }

    // Timer that counts the seconds from the start of pressing the first Tile //
    private void timerStart() {
        if(numOfUseCounter == 0) {
            numOfUseCounter = 1;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            timer_v = (TextView) findViewById(R.id.Timer);
                            timer_v.setText(String.format("%02d:%02d", count / 60, count % 60));
                            count++;
                        }
                    });
                }
            }, 1000, 1000);
        }
    }

    //getting the data from main activity
    private void extractDataFromBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_key));
        mGameLevel = bundle.getInt(getString(R.string.game_level_key));
    }

    //when the game is ended we are putting new items in the bundle to the EndActivity
    public void endGame(){
        stopPlaying();//stop playing the music
        timer.cancel();//stoping the timer
        final Intent EndActivity = new Intent(mGrid.getContext(),EndActivity.class);
        Bundle bundle = new Bundle();

        if(mGame.getIsWon()){//we want to tell the end activity that we won the game
            //((TileAdapter) mGrid.getAdapter()).setWinAnimationByPosition();
            winAnimationStarter();
            winingSound();
            bundle.putBoolean(getString(R.string.game_end_key), true);
        }
        else{//we want to tell the end activity that we lost the game
            mGame.getBoard().showAllTiles();
            ((TileAdapter) mGrid.getAdapter()).notifyDataSetChanged();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startAnimation();
                                lossingSound();
                                //this function starting ower losing animation
                                loseAnimationStarter();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            bundle.putBoolean(getString(R.string.game_end_key), false);
        }
        bundle.putInt(getString(R.string.game_time_key),count);
        bundle.putInt(getString(R.string.game_level_key),mGameLevel);
        EndActivity.putExtra(getString(R.string.bundle_key) , bundle);

        //this thread is running and making the game wait befor starting the EndActivity
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);

                    startActivity(EndActivity);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //this function starting ower losing animation
    public void loseAnimationStarter() {
        int i = 0;
        for(int row = 0 ; row <mGame.getBoard().getRowColSize() ; row++){
            for(int col = 0 ; col < mGame.getBoard().getRowColSize() ; col++){
                ((TileAdapter) mGrid.getAdapter())
                        .setLoseAnimationByPosition(((TileAdapter) mGrid.getAdapter())
                                .getTileArrayList().get(i) , row, col);
                if(i <((TileAdapter) mGrid.getAdapter())
                        .getTileArrayList().size()-1 ){
                    i++;
                }
            }
        }
    }
    public void winAnimationStarter() {
        int i = 0;
        for(int row = 0 ; row <mGame.getBoard().getRowColSize() ; row++){
            for(int col = 0 ; col < mGame.getBoard().getRowColSize() ; col++){
                ((TileAdapter) mGrid.getAdapter())
                        .setWinAnimationByPosition(((TileAdapter) mGrid.getAdapter())
                                .getTileArrayList().get(i) , row, col);
                if(i <((TileAdapter) mGrid.getAdapter())
                        .getTileArrayList().size()-1 ){
                    i++;
                }
            }
        }
    }

    public void startAnimation() {
        ImageView frame = (ImageView) findViewById(R.id.loseSprite);
        frame.setBackgroundResource(R.drawable.frame);

        AnimationDrawable animationDrawable = (AnimationDrawable) frame.getBackground();

        if(animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        else {
            animationDrawable.stop();
            animationDrawable.start();
        }
    }
    @Override
    public void onRotation() {
        if(mGame.getIsWon()){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGame.unpressTiles();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!mGame.getIsFullMines()) {
                            toast.makeText(getApplicationContext()
                                    , getString(R.string.game_warning_message)
                                    , Toast.LENGTH_SHORT).show();
                            numberOfFlags();
                            ((TileAdapter) mGrid.getAdapter()).notifyDataSetChanged();
                        } else {
                            toast.cancel();
                            endGame();
                        }
                    }
                });
            }
        }).start();
    }

    private void lossingSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp2 = MediaPlayer.create(GameActivity.this ,R.raw.fart);
                mp2.start();
            }
        }).start();
    }
    private void gameSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp = MediaPlayer.create(GameActivity.this, R.raw.gamesound);
                mp.start();
            }
        }).start();
    }
    private void winingSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp3 = MediaPlayer.create(GameActivity.this ,R.raw.winingsound);
                mp3.start();
            }
        }).start();
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}