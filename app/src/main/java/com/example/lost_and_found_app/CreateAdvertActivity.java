package com.example.lost_and_found_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/*
    This activity allows the user to create a lost or found advert.
    Subtask 1 added category selection.
    Subtask 2 requires the user to upload an image before saving the advert.
*/
public class CreateAdvertActivity extends AppCompatActivity {

    RadioButton radioLost, radioFound;
    Spinner spinnerCategory;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Button btnSave, btnChooseImage;
    ImageView imgSelectedItem;

    DatabaseHelper databaseHelper;

    // Stores selected image path as text in SQLite
    String selectedImageUri = "";

    // Image picker launcher for selecting image from phone gallery
    ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri.toString();
                    imgSelectedItem.setImageURI(uri);

                    // VERY IMPORTANT: persist permission
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                }
            });

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
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imgSelectedItem = findViewById(R.id.imgSelectedItem);

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

        // Opens phone gallery to choose item image
        btnChooseImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

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

        // Subtask 2 validation: image is compulsory
        if (selectedImageUri.isEmpty()) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto timestamp generation (system time)
        String timestamp = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        // Inserting advert into SQLite database with image path
        boolean inserted = databaseHelper.insertAdvert(
                postType,
                category,
                name,
                phone,
                description,
                date,
                location,
                selectedImageUri,
                timestamp   // NEW
        );

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }
}