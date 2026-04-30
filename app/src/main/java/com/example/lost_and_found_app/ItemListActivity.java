package com.example.lost_and_found_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
    This screen shows all lost and found adverts.
    When an item is clicked, it opens the detail screen with remove option.
*/
public class ItemListActivity extends AppCompatActivity {

    TextView txtFilterInfo;
    ListView listItems;
    Button btnBackToHome;

    DatabaseHelper databaseHelper;

    ArrayList<String> itemTitles;
    ArrayList<Integer> itemIds;

    boolean hasRealItems = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Linking XML views with Java variables
        txtFilterInfo = findViewById(R.id.txtFilterInfo);
        listItems = findViewById(R.id.listItems);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        databaseHelper = new DatabaseHelper(this);

        itemTitles = new ArrayList<>();
        itemIds = new ArrayList<>();

        // Getting selected category filter from MainActivity
        String categoryFilter = getIntent().getStringExtra("category_filter");

        if (categoryFilter == null || categoryFilter.trim().isEmpty()) {
            categoryFilter = "All Categories";
        }

        txtFilterInfo.setText("Category: " + categoryFilter);

        loadItems(categoryFilter);

        // Back navigation to home screen
        btnBackToHome.setOnClickListener(v -> finish());

        // Opens detail screen when a real item is clicked
        listItems.setOnItemClickListener((parent, view, position, id) -> {
            if (!hasRealItems) {
                Toast.makeText(this, "No item available to open", Toast.LENGTH_SHORT).show();
                return;
            }

            if (position < 0 || position >= itemIds.size()) {
                Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            intent.putExtra("advert_id", itemIds.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh list after returning from detail screen
        String categoryFilter = getIntent().getStringExtra("category_filter");

        if (categoryFilter == null || categoryFilter.trim().isEmpty()) {
            categoryFilter = "All Categories";
        }

        loadItems(categoryFilter);
    }

    private void loadItems(String categoryFilter) {
        itemTitles.clear();
        itemIds.clear();
        hasRealItems = false;

        Cursor cursor = null;

        try {
            if (categoryFilter.equals("All Categories")) {
                cursor = databaseHelper.getAllAdverts();
            } else {
                cursor = databaseHelper.getAdvertsByCategory(categoryFilter);
            }

            if (cursor != null && cursor.moveToFirst()) {
                hasRealItems = true;

                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

                    itemIds.add(id);

                    // This follows the wireframe style: Lost Key ... / Found AirPods ...
                    itemTitles.add(postType + " " + name + " ...\n"
                            + category + " • " + formatTime(timestamp));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error loading items: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (itemTitles.isEmpty()) {
            itemTitles.add("No lost or found items available");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itemTitles
        );

        listItems.setAdapter(adapter);
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