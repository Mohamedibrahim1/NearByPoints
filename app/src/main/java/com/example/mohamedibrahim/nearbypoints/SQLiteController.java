package com.example.mohamedibrahim.nearbypoints;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteController extends SQLiteOpenHelper {

    public SQLiteController(Context context) {
        super(context, "androidsqlite.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE Places ( PlaceId INTEGER PRIMARY KEY, PlaceName TEXT, PlaceAddress TEXT, PlaceIconURL TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS Students";
        database.execSQL(query);
        onCreate(database);
    }

    public void insertPlace(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PlaceName", queryValues.get("PlaceName"));
        values.put("PlaceAddress", queryValues.get("PlaceAddress"));
        values.put("PlaceIconURL", queryValues.get("PlaceIconURL"));
        database.insert("Places", null, values);
        database.close();
    }


    public void deletePlace(String placeName) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM  Places where PlaceName='" + placeName + "'";
        database.execSQL(deleteQuery);
    }


    public Cursor searchPlace(String placeName) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM Places where PlaceName='" + placeName + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public List<PlaceItem> getAllPlaces() {
        String selectQuery = "SELECT * FROM Places";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<PlaceItem> rowItems = new ArrayList<PlaceItem>();

        if (cursor.moveToFirst()) {
            do {
                PlaceItem item = new PlaceItem(null, cursor.getString(1), cursor.getString(2), true, cursor.getString(3));
                rowItems.add(item);
            } while (cursor.moveToNext());
        }
        return rowItems;
    }
}