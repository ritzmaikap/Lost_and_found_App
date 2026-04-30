package com.example.lost_and_found_app;

import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
    This screen appears after clicking an item from the list.
    It shows the selected advert details and allows removing the advert.
*/
public class ItemDetailActivity extends AppCompatActivity {

    TextView txtItemDetails;
    ImageView imgItemDetail;
    Button btnRemove, btnBackToList;

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
        btnBackToList = findViewById(R.id.btnBackToList);

        databaseHelper = new DatabaseHelper(this);

        // Getting selected advert id from ItemListActivity
        advertId = getIntent().getIntExtra("advert_id", -1);

        if (advertId == -1) {
            Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAdvertDetails();

        // Back navigation to list screen
        btnBackToList.setOnClickListener(v -> finish());

        // Confirm before deleting item
        btnRemove.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadAdvertDetails() {
        Cursor cursor = null;

        try {
            cursor = databaseHelper.getAdvertById(advertId);

            if (cursor == null || !cursor.moveToFirst()) {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

            // Display uploaded item image
            if (imageUri != null && !imageUri.trim().isEmpty()) {
                try {
                    if (imageUri != null && !imageUri.trim().isEmpty()) {
                        Uri uri = Uri.parse(imageUri);
                        imgItemDetail.setImageURI(uri);
                    } else {
                        imgItemDetail.setImageResource(android.R.color.darker_gray);
                    }
                } catch (Exception e) {
                    imgItemDetail.setImageResource(android.R.color.darker_gray);
                    Toast.makeText(this, "Image could not be loaded", Toast.LENGTH_SHORT).show();
                }
            }

            String details =
                    postType + " " + name + "\n\n" +
                            formatTime(timestamp) + "\n\n" +
                            "At " + location + "\n\n" +
                            "Category: " + category + "\n\n" +
                            "Phone: " + phone + "\n\n" +
                            "Description: " + description + "\n\n" +
                            "Date: " + date;

            txtItemDetails.setText(details);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading item: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Remove item")
                .setMessage("Remove this advert from the lost and found list?")
                .setPositiveButton("Remove", (dialog, which) -> removeAdvert())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeAdvert() {
        try {
            boolean deleted = databaseHelper.deleteAdvert(advertId);

            if (deleted) {
                Toast.makeText(this, "Advert removed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error removing item: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String formatTime(String timestamp) {
        try {
            if (timestamp == null || timestamp.isEmpty()) {
                return "Unknown time";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postedDate = sdf.parse(timestamp);
            Date currentDate = new Date();

            if (postedDate == null) {
                return timestamp;
            }

            long difference = currentDate.getTime() - postedDate.getTime();

            long minutes = difference / (1000 * 60);
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + " days ago";
            } else if (hours > 0) {
                return hours + " hours ago";
            } else if (minutes > 0) {
                return minutes + " minutes ago";
            } else {
                return "Just now";
            }

        } catch (Exception e) {
            return timestamp;
        }
    }
}