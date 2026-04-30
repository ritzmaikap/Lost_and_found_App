package com.example.lost_and_found_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
    This activity allows the user to create a lost or found advert.
    Subtask 1 adds category selection so adverts can be filtered later.
*/
public class CreateAdvertActivity extends AppCompatActivity {

    RadioButton radioLost, radioFound;
    Spinner spinnerCategory;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Button btnSave;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        // Linking XML views with Java variables
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        editDate = findViewById(R.id.editDate);
        editLocation = findViewById(R.id.editLocation);
        btnSave = findViewById(R.id.btnSave);

        // Creating database helper object
        databaseHelper = new DatabaseHelper(this);

        // Category list for adverts
        String[] categories = {
                "Electronics",
                "Pets",
                "Wallets",
                "Keys",
                "Bags",
                "Other"
        };

        // Setting category spinner values
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        spinnerCategory.setAdapter(adapter);

        // Save advert when button is clicked
        btnSave.setOnClickListener(v -> saveAdvert());
    }

    private void saveAdvert() {
        // Getting values from form fields
        String postType = radioLost.isChecked() ? "Lost" : "Found";
        String category = spinnerCategory.getSelectedItem().toString();
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        // Simple validation to avoid empty adverts
        if (name.isEmpty() || phone.isEmpty() || description.isEmpty()
                || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inserting advert into SQLite database
        boolean inserted = databaseHelper.insertAdvert(
                postType,
                category,
                name,
                phone,
                description,
                date,
                location
        );

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }
}