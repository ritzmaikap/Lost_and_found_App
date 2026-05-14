package com.example.lost_and_found_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

/*
    MainActivity is the home screen.

    New geo feature:
    - User can enter radius in km.
    - SHOW ON MAP opens MapsActivity.
    - MapsActivity shows only items within that radius.
*/
public class MainActivity extends AppCompatActivity {

    Button btnCreateAdvert, btnShowItems, btnShowOnMap;
    Spinner spinnerCategoryFilter;
    EditText editRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        btnShowItems = findViewById(R.id.btnShowItems);
        btnShowOnMap = findViewById(R.id.btnShowOnMap);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        editRadius = findViewById(R.id.editRadius);

        String[] categories = {
                "All Categories",
                "Electronics",
                "Pets",
                "Wallets",
                "Keys",
                "Bags",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        spinnerCategoryFilter.setAdapter(adapter);

        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(intent);
        });

        btnShowItems.setOnClickListener(v -> {
            String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();

            Intent intent = new Intent(MainActivity.this, ItemListingActivity.class);
            intent.putExtra("category_filter", selectedCategory);
            startActivity(intent);
        });

        btnShowOnMap.setOnClickListener(v -> {
            String radiusText = editRadius.getText().toString().trim();

            // Default radius is 10 km if user leaves it empty.
            double radiusKm = 10.0;

            if (!radiusText.isEmpty()) {
                radiusKm = Double.parseDouble(radiusText);
            }

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("radius_km", radiusKm);
            startActivity(intent);
        });
    }
}