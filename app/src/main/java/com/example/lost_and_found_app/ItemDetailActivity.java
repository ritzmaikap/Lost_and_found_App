package com.example.lost_and_found_app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
    This activity shows one selected advert in detail.
    Subtask 2 displays the uploaded image for the advert.
    The remove button deletes the advert from SQLite.
*/
public class ItemDetailActivity extends AppCompatActivity {

    TextView txtItemDetails;
    ImageView imgItemDetail;
    Button btnRemove;

    DatabaseHelper databaseHelper;
    int advertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Linking XML views with Java variables
        txtItemDetails = findViewById(R.id.txtItemDetails);
        imgItemDetail = findViewById(R.id.imgItemDetail);
        btnRemove = findViewById(R.id.btnRemove);

        // Creating database helper object
        databaseHelper = new DatabaseHelper(this);

        // Getting selected advert id from list screen
        advertId = getIntent().getIntExtra("advert_id", -1);

        loadAdvertDetails();

        // Remove selected advert from database
        btnRemove.setOnClickListener(v -> {
            boolean deleted = databaseHelper.deleteAdvert(advertId);

            if (deleted) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAdvertDetails() {
        Cursor cursor = databaseHelper.getAdvertById(advertId);

        if (cursor != null && cursor.moveToFirst()) {
            String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

            // Displaying uploaded image
            if (imageUri != null && !imageUri.isEmpty()) {
                imgItemDetail.setImageURI(Uri.parse(imageUri));
            }

            String details =
                    postType + " " + name + "\n\n" +
                            "Category: " + category + "\n\n" +
                            "Phone: " + phone + "\n\n" +
                            "Description: " + description + "\n\n" +
                            "Date: " + date + "\n\n" +
                            "At " + location;

            txtItemDetails.setText(details);

            cursor.close();
        }
    }
}