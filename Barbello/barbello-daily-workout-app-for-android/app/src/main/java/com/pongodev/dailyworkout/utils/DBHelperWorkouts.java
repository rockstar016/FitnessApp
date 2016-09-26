
package com.pongodev.dailyworkout.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Design and developed by pongodev.com
 *
 * DBHelperWorkouts is created to handle program database operation that used within application.
 * Created using SQLiteOpenHelper.
 */
public class DBHelperWorkouts extends SQLiteOpenHelper {

    // Path of database when app installed on device
    private static String DB_PATH = Utils.ARG_DATABASE_PATH;

    // Create database name and version
    private final static String DB_NAME = "db_workouts";
    public final static int DB_VERSION = 1;
    public static SQLiteDatabase db;
    private final Context context;

    // Create table names and fields
    private final String TABLE_WORKOUTS     = "tbl_workouts";
    private final String CATEGORY_ID        = "category_id";
    private final String WORKOUTS_ID        = "workout_id";
    private final String WORKOUTS_NAME      = "name";
    private final String WORKOUTS_IMAGE     = "image";
    private final String WORKOUTS_TIME      = "time";
    private final String WORKOUTS_STEPS     = "steps";

    private final String TABLE_CATEGORIES   = "tbl_categories";
    private final String CATEGORY_NAME      = "category_name";
    private final String CATEGORY_IMAGE     = "category_image";

    private final String TABLE_IMAGES = "tbl_images";

    public DBHelperWorkouts(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // Method to create database
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;

        // If database exist delete database and copy the new one
        if(dbExist){
            deleteDataBase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }else{
            db_Read = this.getReadableDatabase();
            db_Read.close();

            Log.d("db_Read", "db_Read: "+db_Read.getPath());
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");

            }
        }

    }

    // Method to delete database
    private void deleteDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        dbFile.delete();
    }

    // Method to check database on path
    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // Method to copy database from app to db path
    private void copyDataBase() throws IOException{

        InputStream myInput = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        Log.d("outFileName", outFileName);
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    // Method to open database and read it
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    // Method to open database and read it
    public void openDataBase1() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    // Method to get all category data from database
    public ArrayList<ArrayList<Object>> getAllCategories(){
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<>();

        Cursor cursor;

        try{
            cursor = db.query(
                    TABLE_CATEGORIES,
                    new String[]{CATEGORY_ID, CATEGORY_NAME, CATEGORY_IMAGE},
                    null, null, null, null, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<>();
                    long id = countWorkouts(cursor.getLong(0));

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(id);

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB getAllCategories", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }


    // Method to get total workout in workout category
    public int countWorkouts(long id) {
        Cursor dataCount = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUTS +" WHERE "+CATEGORY_ID +" = "+id , null);
        dataCount.moveToFirst();
        int count = dataCount.getInt(0);
        dataCount.close();
        return count;
    }

    // Method to get all workouts data by category from database
    public ArrayList<ArrayList<Object>> getAllWorkoutsByCategory(String selectedID){
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<>();

        Cursor cursor;

        try{
           cursor = db.query(
                    TABLE_WORKOUTS,
                    new String[]{WORKOUTS_ID, WORKOUTS_NAME, WORKOUTS_IMAGE, WORKOUTS_TIME,
                            WORKOUTS_STEPS, CATEGORY_ID},
                    CATEGORY_ID +" = "+selectedID, null, null, null, null);
            //String selectQuery = "SELECT  * FROM " + TABLE_WORKOUTS;
           // cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<>();
                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("WorkoutListByCategory", e.toString());
            e.printStackTrace();
        }


        return dataArrays;
    }

    // Method to get workout detail from database
    public ArrayList<Object> getWorkoutDetail(String selectedID){

        ArrayList<Object> rowArray = new ArrayList<>();
        Cursor cursor;

        try{
            cursor = db.query(
                    TABLE_WORKOUTS,
                    new String[] {WORKOUTS_ID, WORKOUTS_NAME, WORKOUTS_IMAGE, WORKOUTS_TIME, WORKOUTS_STEPS},
                    WORKOUTS_ID + " = " + selectedID,
                    null, null, null, null, null);

            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    rowArray.add(cursor.getLong(0));
                    rowArray.add(cursor.getString(1));
                    rowArray.add(cursor.getString(2));
                    rowArray.add(cursor.getString(3));
                    rowArray.add(cursor.getString(4));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
        }
        catch (SQLException e)
        {
            Log.e("DB getDetail", e.toString());
            e.printStackTrace();
        }

        return rowArray;
    }


    // Method to get all workout images data from database
    public ArrayList<ArrayList<Object>> getImages(String workoutID){
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<>();

        Cursor cursor;

        try{
            cursor = db.query(
                    TABLE_IMAGES,
                    new String[]{WORKOUTS_IMAGE},
                    WORKOUTS_ID +" = "+workoutID, null, null, null, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<>();

                    dataList.add(cursor.getString(0));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB getImages", e.toString());
            e.printStackTrace();
        }


        return dataArrays;
    }

    public ArrayList<ArrayList<Object>> getAll_Time()
    {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WORKOUTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ArrayList<Object> dataList = new ArrayList<>();
                dataList.add(cursor.getString(4));
                dataArrays.add(dataList);
            } while (cursor.moveToNext());
        }
        cursor.moveToFirst();   cursor.close();
        // return contact list
        return dataArrays;
    }

    public void update_time_data(String id, String _str)
    {
        String selectQuery = "UPDATE tbl_workouts SET time = '" + _str + "' WHERE category_id = " + id+";";

     //   db = this.getWritableDatabase();

       // db.rawQuery(selectQuery, null);
       // db.execSQL(selectQuery);

        ContentValues value = new ContentValues();
        value.put(WORKOUTS_TIME, _str);
        //value.put("category_id", 90);
        //value.put(WORKOUTS_NAME, "xxxxx");
        db.update(TABLE_WORKOUTS, value, "workout_id=1", null );

        //db.insert(TABLE_WORKOUTS, null, value);
    }
}