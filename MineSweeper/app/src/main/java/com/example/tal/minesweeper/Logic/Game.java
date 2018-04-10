package com.example.tal.minesweeper.Logic;

public class Game {
    private Board mBoard;
    private boolean mIsWon = false;
    private boolean mIsLost = false;
    private boolean mFlagOn = false;
    private boolean mIsFullMines = false;
    private int mGameDifficulty;
    final static int EASY = 0;
    final static int MEDIUM = 1;
    final static int HARD = 2;
    private final int[] mEasyBoard = { 8, 8};
    private final int[] mMediumBoard = { 10, 10 };
    private final int[] mHardBoard = { 12, 15 };

    public Game(int difficulty) {
        mGameDifficulty = difficulty;
        switch (mGameDifficulty) {
            case EASY:
                mBoard = new Board(mEasyBoard[0], mEasyBoard[1]);
                break;
            case MEDIUM:
                mBoard = new Board(mMediumBoard[0], mMediumBoard[1]);
                break;
            case HARD:
                mBoard = new Board(mHardBoard[0], mHardBoard[1]);
        }
    }

    public Board getBoard() {
        return mBoard;
    }

    public boolean getIsWon() {
        return mIsWon;
    }

    public boolean getIsLost() {
        return mIsLost;
    }

    public boolean getIsFullMines(){return mIsFullMines;}

    // play the game  //
    public void playTile(int position) {
        if(mIsLost || mIsWon){
            return;
        }
        int row = position/mBoard.getRowColSize();
        int col = position%mBoard.getRowColSize();

        if (mBoard.checkTileExists(row, col)) {
            Tile tile = mBoard.getTile(row, col);
            if (!tile.isPressed()) {
                if (mFlagOn) {
                    tile.setFlag();
                    if (tile.isFlagged()) {
                        mBoard.addFlag();
                    } else {
                        mBoard.removeFlag();
                    }
                    return;
                } else {
                    if(tile.isFlagged()){
                        return;
                    }
                    else if (tile.isMine()) {
                        mIsLost = true;
                        tile.press();
                        return;
                    } else {
                        recursiveFlush(row, col);
                        if (mBoard.getNumOfUnpressedTiles() == mBoard.getNumOfMines()) {
                            mIsWon = true;
                        }
                    }
                }
            }
        }
    }
    // this function pressing more tiles if there number is 0 and if they are near the one we pressed already //
    private void recursiveFlush(int row, int col) {
        Tile tile = mBoard.getTile(row, col);
        if (tile.isMine() || tile.isPressed() || tile.isFlagged()) {
            return;
        }
        mBoard.pressTile(row, col);
        if (tile.getmNumber() > 0) {
            return;
        }
        for  (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if(i==row && j == col){
                    continue;
                }
                if (mBoard.checkTileExists(i, j)) {
                    recursiveFlush(i, j);
                }
            }
        }
    }
    public void unpressTiles(){
        mBoard.unPressTile();
        if((mBoard.getRowColSize()*mBoard.getRowColSize()) == mBoard.getNumOfMines()){
            mIsFullMines = true;
        }
    }
    public void setFlag() {
        if (!mFlagOn) {
            mFlagOn = true;
        } else {
            mFlagOn = false;
        }
    }
}
