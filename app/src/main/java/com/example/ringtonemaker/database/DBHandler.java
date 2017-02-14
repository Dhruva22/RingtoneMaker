package com.example.ringtonemaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.ringtonemaker.model.Ringtones;

import java.util.ArrayList;

/**
 * Created by Mishtiii on 08-02-2017.
 */

public class DBHandler extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "RingtoneMaker";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "MyRingtones";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_NAME = "Ringtone_Name";
    private static final String COLUMN_SOURCE = "Source_Path";

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating a database
    public static DBHandler dbHandler;
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String CREATE_APPLICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_SOURCE + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(CREATE_APPLICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXIXTS " + TABLE_NAME);
        onCreate(db);
    }

    //Inserting Row in a database
    public void addRingtone(Ringtones ringtones)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, ringtones.getRingtone_name()); // Name: Label of Ringtone
        values.put(COLUMN_SOURCE, ringtones.getRingtone_source());//Source: Source of Ringtone

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        Log.d("DBHelper", "Inserting data");
        db.close(); // Closing database connection
    }

    public void deleteRingtone(Ringtones ringtones)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, ringtones.getRingtone_name()); // Name: Label of Ringtone

        db.delete(TABLE_NAME,"id = ?", new String[] {Integer.toString(ringtones.getId())});

        db.close();
    }

    //Displaying all the record inserted in database
    public ArrayList<Ringtones> getAllRingtones()
    {
        ArrayList<Ringtones> lst_ringtones = new ArrayList<Ringtones>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to ArrayList
        if (cursor.moveToFirst())
        {
            do {
                Ringtones ringtones = new Ringtones();
                ringtones.setId(Integer.parseInt(cursor.getString(0)));
                ringtones.setRingtone_name(cursor.getString(1));
                ringtones.setRingtone_source(cursor.getString(2));

                // Adding Ringtones to ArrayList
                lst_ringtones.add(ringtones);

            } while (cursor.moveToNext());
        }
        return lst_ringtones;
    }

}
