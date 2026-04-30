package com.example.lost_and_found_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

/*
    MainActivity works as the home screen.
    It follows the first wireframe screen:
    - Create a new advert
    - Show all lost and found items
    Subtask 1 adds a category filter before showing the list.
*/
public class MainActivity extends AppCompatActivity {

    Button btnCreateAdvert, btnShowItems;
    Spinner spinnerCategoryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Linking Java variables with XML views
        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        btnShowItems = findViewById(R.id.btnShowItems);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);

        // Category list used for filtering lost/found adverts
        String[] categories = {
                "All Categories",
                "Electronics",
                "Pets",
                "Wallets",
                "Keys",
                "Bags",
                "Other"
        };

        // Setting category values inside the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        spinnerCategoryFilter.setAdapter(adapter);

        // Open the create advert screen
        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        // Open the item list screen with selected category filter
        btnShowItems.setOnClickListener(v -> {
            String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();

            Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
            intent.putExtra("category_filter", selectedCategory);
            startActivity(intent);
        });
    }
}