package com.example.lost_and_found_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    DatabaseStore manages the SQLite database for the Lost and Found app.

    New geo feature:
    - latitude and longitude are now saved for every advert.
    - This helps us show each lost/found item on Google Maps.
    - It also helps us calculate radius-based search.
*/
public class DatabaseStore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LostFound.db";

    // Version increased because latitude and longitude columns are added.
    // Important: increasing version recreates the database during development.
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_NAME = "adverts";

    public DatabaseStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method creates the adverts table.
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
                "latitude REAL, " +       // New column for map latitude
                "longitude REAL, " +      // New column for map longitude
                "image_uri TEXT, " +
                "timestamp TEXT)";

        db.execSQL(createTable);
    }

    // During development, the old table is removed and recreated when version changes.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // This method inserts a new advert into SQLite.
    // latitude and longitude are also stored so the item can be shown on map.
    public boolean insertAdvert(String postType, String category, String name,
                                String phone, String description,
                                String date, String location,
                                double latitude, double longitude,
                                String imageUri, String timestamp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("post_type", postType);
        values.put("category", category);
        values.put("name", name);
        values.put("phone", phone);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("image_uri", imageUri);
        values.put("timestamp", timestamp);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }

    // This gets all adverts for list screen and map screen.
    public Cursor getAllAdverts() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC",
                null
        );
    }

    // This gets adverts by category.
    public Cursor getAdvertsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE category = ? ORDER BY id DESC",
                new String[]{category}
        );
    }

    // This gets one advert by id.
    public Cursor getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // This deletes one advert.
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