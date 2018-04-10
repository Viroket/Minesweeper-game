package com.example.tal.minesweeper;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.example.tal.minesweeper.Logic.Board;

import java.util.ArrayList;
import java.util.Random;

public class TileAdapter extends BaseAdapter {
    private Board mBoard;
    private Context mContext;
    private int[] colors = {Color.GREEN,Color.YELLOW,Color.BLUE,Color.MAGENTA,Color.RED};
    private TileView tileView;
    private ArrayList<TileView> allViews = new ArrayList<TileView>();

    public ArrayList<TileView> getTileArrayList() {
        return allViews;
    }

    public TileAdapter(Context context, Board board) {
        mBoard = board;
        mContext = context;
    }
    @Override
    public int getCount() {
        return mBoard.getRowColSize()*mBoard.getRowColSize();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tileView = (TileView)convertView;
        if(tileView == null) {
            tileView = new TileView(mContext);
            allViews.add(tileView);
           // Log.v("Tile Adapter","creating new view for index "+position);
        } else {
           // Log.e("Tile Adapter","RECYCLING view for index "+position);
        }
        if(mBoard.getTileByPosition(position).isPressed()) { //if the Tile is pressed we will print on its number

            if(mBoard.getTileByPosition(position).isMine()){
                //tileView.setBackgroundColor(Color.RED);
                tileView.setBackgroundResource(R.drawable.poobutton2);//if we lost the game
            }else{
                tileView.text.setText(mBoard.getTileByPosition(position).toString());
                for(int i = 1 ; i<9 ; i++){
                    if(mBoard.getTileByPosition(position).getmNumber()>5){
                        tileView.text.setTextColor(colors[colors.length-1]);
                        continue;
                    }
                    if(mBoard.getTileByPosition(position).getmNumber() == i){
                        tileView.text.setTextColor(colors[i-1]);
                    }
                }
                tileView.setBackgroundColor(Color.GRAY);
            }
        }
        else if(mBoard.getTileByPosition(position).isFlagged()) {//if the tile is pressed by a flag we will put "!" on it

            tileView.setBackgroundResource(R.drawable.bagofpoo2);
        }
        else {
            tileView.setBackgroundColor(Color.TRANSPARENT);
            tileView.text.setText("");
            tileView.setBackgroundResource(R.drawable.emptybutton2);
        }
        return tileView;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position ) {
        return mBoard.getTileByPosition(0);
    }

    public void setLoseAnimationByPosition(final TileView view, int row, int col) {

        int finalPosition = 3500;
        int startPosition = 200;

        Random random = new Random();
        int addPosition = random.nextInt(finalPosition)+startPosition;

        int currentX = view.getHeight()-100;
        int currentY = view.getWidth()-100;

        onStartAnimationLose(view, currentX, currentY, addPosition, 0 ,row ,col);
    }


    private void onStartAnimationLose(final TileView tile, int currentX, int currentY, int newX, int newY, int row, int col) {
        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(currentY, newY);
        ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(currentX, newX);

        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                tile.setTranslationX(value);
            }
        });

        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                tile.setTranslationY(value);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(valueAnimatorX);
        animatorSet.play(valueAnimatorY);

        animatorSet.setDuration(3500);
        animatorSet.start();

    }
    public void setWinAnimationByPosition(final TileView view, int row, int col) {
        int finalPosition = 3500;
        int startPosition = 200;


        Random random = new Random(); // random number
        int add = random.nextInt(finalPosition)+startPosition;

        int midWidth = (mBoard.getRowColSize()) / 2;
        int midHight = (mBoard.getRowColSize()) / 2;

        int currentX = view.getHeight()-100;
        int currentY = view.getWidth()-100;
        int newX = 0;
        int newY = 0;

        if (row >= midHight && col >= midWidth) {
            newX = -add;
            newY = +add;
        }

        else {
            newX = -add;
            newY = -add;
        }


        onStartAnimationWin(view, currentX, currentY, newX, newY);
    }


    private void onStartAnimationWin(final TileView tile, int currentX, int currentY, int newX, int newY) {

        ValueAnimator valueAnimatorX = ValueAnimator.ofFloat(currentY, newY);
        ValueAnimator valueAnimatorY = ValueAnimator.ofFloat(currentX, newX);


        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                tile.setTranslationX(value);
            }
        });

        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                tile.setTranslationY(value);
            }
        });

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(tile, "rotation", 0, 380f);
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(valueAnimatorX).with(rotationAnimator);
        animatorSet.play(valueAnimatorY).with(rotationAnimator);

        animatorSet.setDuration(3500);

        animatorSet.start();

    }
}