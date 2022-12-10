package com.example.myapplication;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper db;
    public static final String DATABASE_NAME = "fishapp.db";
    public static final String CONTACTS_TABLE_NAME = "reports";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_LOCATION = "location";
    public static final String CONTACTS_COLUMN_NUMFISH = "numfish";
    public static final String CONTACTS_COLUMN_TIDELEVEL = "tidelevel";
    public static final String CONTACTS_COLUMN_ISEBB = "isebb";
    public static final String CONTACTS_COLUMN_TIMEOFDAY = "timeofday";
    public static final String CONTACTS_COLUMN_TIMEFISHED = "timefished";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table reports " +
                        "(id integer primary key, location text,numfish integer,tidelevel real,isebb integer, timeofday integer, timefished real)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertReport (String location, int numFish, float tideLevel, boolean isEbb, int timeOfDay, float timeFished) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("location", location);
        contentValues.put("numfish", numFish);
        contentValues.put("tidelevel", tideLevel);
        if( isEbb )
            contentValues.put("isEbb", 1);
        else
            contentValues.put("isEbb", 0);

        contentValues.put("timeofday", timeOfDay);
        contentValues.put("timefished", timeFished);
        db.insert("reports", null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public ArrayList<Report> getAllReports() {
        ArrayList<Report> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from reports", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String location = res.getString(res.getColumnIndexOrThrow(CONTACTS_COLUMN_LOCATION));
            int numFish = res.getInt(res.getColumnIndexOrThrow(CONTACTS_COLUMN_NUMFISH));

            float tideLevel = res.getFloat(res.getColumnIndexOrThrow(CONTACTS_COLUMN_TIDELEVEL));
            int ebbFlood = res.getInt(res.getColumnIndexOrThrow(CONTACTS_COLUMN_ISEBB));
            boolean isEbb = false;
            if( ebbFlood == 1 )
                isEbb = true;

            int timeOfDay = res.getInt(res.getColumnIndexOrThrow(CONTACTS_COLUMN_TIMEOFDAY));
            float timeFished = res.getFloat(res.getColumnIndexOrThrow(CONTACTS_COLUMN_TIMEFISHED));

            array_list.add(new Report(location, numFish, tideLevel, isEbb, timeOfDay, timeFished));
            res.moveToNext();
        }
        return array_list;
    }
}
