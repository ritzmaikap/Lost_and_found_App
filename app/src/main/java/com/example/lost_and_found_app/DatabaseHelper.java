package com.example.lost_and_found_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    DatabaseHelper manages the SQLite database.
    Subtask 1 added category filtering.
    Subtask 2 adds image storage using image URI text.
*/
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostFound.db";

    // Version increased because image_uri column is added
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "adverts";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating the adverts table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "post_type TEXT, " +
                "category TEXT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "image_uri TEXT)";

        db.execSQL(createTable);
    }

    // Recreating table if database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new lost/found advert with image URI
    public boolean insertAdvert(String postType, String category, String name,
                                String phone, String description,
                                String date, String location,
                                String imageUri) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("post_type", postType);
        values.put("category", category);
        values.put("name", name);
        values.put("phone", phone);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);
        values.put("image_uri", imageUri);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }

    // Get all adverts without category filter
    public Cursor getAllAdverts() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC",
                null
        );
    }

    // Get adverts filtered by category
    public Cursor getAdvertsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE category = ? ORDER BY id DESC",
                new String[]{category}
        );
    }

    // Get a single advert using id
    public Cursor getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Delete advert after item is returned to owner
    public boolean deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}