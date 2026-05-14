package com.example.lost_and_found_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
    This activity is used to create a lost or found advert.

    New geo feature:
    - User can type a location manually.
    - User can also click GET CURRENT LOCATION.
    - Latitude and longitude are saved with the advert.
*/
public class CreateAdvertActivity extends AppCompatActivity {

    RadioButton radioLost, radioFound;
    Spinner spinnerCategory;
    EditText editName, editPhone, editDescription, editDate, editLocation;
    Button btnSave, btnChooseImage, btnGetCurrentLocation;
    ImageView imgSelectedItem;

    DatabaseStore databaseStore;
    FusedLocationProviderClient fusedLocationClient;

    String selectedImageUri = "";

    // These variables store the selected location coordinates.
    double selectedLatitude = 0.0;
    double selectedLongitude = 0.0;

    // This checks whether a valid location has been selected.
    boolean locationSelected = false;

    ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri.toString();
                    imgSelectedItem.setImageURI(uri);

                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                }
            });

    ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission is needed", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

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
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        imgSelectedItem = findViewById(R.id.imgSelectedItem);

        databaseStore = new DatabaseStore(this);

        // This helps us get the phone's current location.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String[] categories = {
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

        spinnerCategory.setAdapter(adapter);

        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnGetCurrentLocation.setOnClickListener(v -> checkLocationPermission());

        btnSave.setOnClickListener(v -> saveAdvert());
    }

    private void checkLocationPermission() {
        // Runtime permission is needed for current location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                selectedLatitude = location.getLatitude();
                selectedLongitude = location.getLongitude();
                locationSelected = true;

                // Convert latitude and longitude into readable address.
                String addressText = getAddressFromCoordinates(selectedLatitude, selectedLongitude);
                editLocation.setText(addressText);

                Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to get current location. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latitude + ", " + longitude;
    }

    private boolean convertTypedLocationToCoordinates(String locationText) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocationName(locationText, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                selectedLatitude = address.getLatitude();
                selectedLongitude = address.getLongitude();
                locationSelected = true;

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void saveAdvert() {
        String postType = radioLost.isChecked() ? "Lost" : "Found";
        String category = spinnerCategory.getSelectedItem().toString();
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty()
                || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri.isEmpty()) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // If user typed the location manually, convert it into latitude and longitude.
        if (!locationSelected) {
            boolean converted = convertTypedLocationToCoordinates(location);

            if (!converted) {
                Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String timestamp = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        boolean inserted = databaseStore.insertAdvert(
                postType,
                category,
                name,
                phone,
                description,
                date,
                location,
                selectedLatitude,
                selectedLongitude,
                selectedImageUri,
                timestamp
        );

        if (inserted) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }
}