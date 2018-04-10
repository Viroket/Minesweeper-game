package com.example.tal.minesweeper.Logic;


public class Tile {
    private boolean mIsPressed = false;
    private boolean mIsFlagged = false;
    private boolean mIsMine = false;
    private int mNumber;

    public Tile() {
        mIsMine = true;
        mNumber = -1;
    }

    public Tile(int mNumber) {
        this.mNumber = mNumber;
    }

    public void turnToMine(){
        mNumber = -1;
        mIsMine = true;
    }

    public void raiseNumber(){mNumber++;}

    public boolean isMine() {
        return mIsMine;
    }

    public int getmNumber() {
        return mNumber;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void unPress() { mIsPressed = false; }

    public void press() {
        mIsPressed = true;
    }

    public boolean isFlagged() { // taking a look at a flag
        return mIsFlagged;
    }

    public void setFlag() { //set flag to false or true
        if (!mIsFlagged) {
            mIsFlagged = true;
        } else {
            mIsFlagged = false;
        }
    }

    public String toString() {
        if (mIsMine) {
            return "*";
        }
        else if (mIsFlagged) {
            return "!";
        }
        else if (mNumber == 0) {
            return " ";
        }
        return  mNumber + "";
    }
}
