package com.example.tal.minesweeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Tal on 1/8/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Score.db";
    public static final String TABLE_NAME = "Player_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "LONGITUDE";
    public static final String COL_4 = "LATITUDE";
    public static final String COL_5 = "ADDRESS";
    public static final String COL_6 = "TIME";
    public static final String KEY_GAME_LEVEL = "GAMELEVEL";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 12);
    }

    @Override//creating a data base that have some coloms
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , NAME TEXT , LATITUDE TEXT , LONGITUDE TEXT , ADDRESS TEXT , TIME INTEGER , GAMELEVEL INTEGER)");

    }

    @Override//if we want to upgrade the database and delete the old one
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    //inserting new player data to my data base
    public boolean insertData(String name , Double longitude , Double latitude , String address, int time , int gameLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2 , name);
        contentValues.put(COL_3 , longitude);
        contentValues.put(COL_4 , latitude);
        contentValues.put(COL_5 , address);
        contentValues.put(COL_6 , time);
        contentValues.put(KEY_GAME_LEVEL , gameLevel);
        long result = db.insert(TABLE_NAME , null , contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //making a cursor that will select by desc order the level of the current game level
    public Cursor getAllData(int mGameDifficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME + " where " + KEY_GAME_LEVEL + " = " + mGameDifficulty + " order by " + COL_6 + " desc", null);

        return res;
    }
    //making a cursor that will select by asc order the level of the current game level
    public Cursor getAllDataByOrder(int mGameDifficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME + " where " + KEY_GAME_LEVEL + " = " + mGameDifficulty + " order by " + COL_6 + " asc", null);

        return res;
    }

    // changing a specific data to another one if we need to
    public boolean updateData(int id , String name , Double longitude , Double latitude  , String address, int time , int gameLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String stringId = id +"";
        contentValues.put(COL_2 , name);
        contentValues.put(COL_3 , longitude);
        contentValues.put(COL_4 , latitude);
        contentValues.put(COL_5 , address);
        contentValues.put(COL_6 , time);
        contentValues.put(KEY_GAME_LEVEL , gameLevel);
        db.update(TABLE_NAME , contentValues , "ID = ?" ,new String[]{stringId});
        return true;
    }

    //deleting a player
    public Integer deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME , COL_1 + " = " +id , null);
    }

    //deleting all the things in the table
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME , null , null);
    }
}
