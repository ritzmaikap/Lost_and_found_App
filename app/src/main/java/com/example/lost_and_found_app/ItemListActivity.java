package com.example.lost_and_found_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/*
    This activity displays lost and found adverts.
    Subtask 1 filters adverts based on the selected category.
*/
public class ItemListActivity extends AppCompatActivity {

    TextView txtFilterInfo;
    ListView listItems;
    DatabaseHelper databaseHelper;

    ArrayList<String> itemTitles;
    ArrayList<Integer> itemIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Linking XML views with Java variables
        txtFilterInfo = findViewById(R.id.txtFilterInfo);
        listItems = findViewById(R.id.listItems);

        // Creating database helper object
        databaseHelper = new DatabaseHelper(this);

        itemTitles = new ArrayList<>();
        itemIds = new ArrayList<>();

        // Getting selected category from MainActivity
        String categoryFilter = getIntent().getStringExtra("category_filter");

        if (categoryFilter == null) {
            categoryFilter = "All Categories";
        }

        txtFilterInfo.setText("Category: " + categoryFilter);

        // Loading items based on selected category
        loadItems(categoryFilter);

        // Open detail screen when an advert is clicked
        listItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            intent.putExtra("advert_id", itemIds.get(position));
            startActivity(intent);
        });
    }

    private void loadItems(String categoryFilter) {
        itemTitles.clear();
        itemIds.clear();

        Cursor cursor;

        // If All Categories is selected, all adverts are shown
        if (categoryFilter.equals("All Categories")) {
            cursor = databaseHelper.getAllAdverts();
        } else {
            cursor = databaseHelper.getAdvertsByCategory(categoryFilter);
        }

        // Reading adverts from database cursor
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));

                itemIds.add(id);
                itemTitles.add(postType + " " + name + " (" + category + ")\n" + timestamp);

            } while (cursor.moveToNext());

            cursor.close();
        }

        // Showing message if no item is found
        if (itemTitles.isEmpty()) {
            itemTitles.add("No items found for this category");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itemTitles
        );

        listItems.setAdapter(adapter);
    }
}