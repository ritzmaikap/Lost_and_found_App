package com.example.lost_and_found_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/*
    MapsActivity shows lost and found items on Google Maps.

    Features added:
    1. Back button
    2. Search location box
    3. Current user location
    4. Radius-based item display

    Important:
    Google Maps inside Android does not automatically give the same search bar
    as the real Google Maps app. So, we create our own search box using Geocoder.
*/
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap googleMap;

    DatabaseStore databaseStore;
    FusedLocationProviderClient fusedLocationClient;

    Button btnBack, btnSearchLocation;
    EditText editSearchLocation;

    double radiusKm = 10.0;

    Location currentUserLocation;

    ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadUserLocationAndItems();
                } else {
                    Toast.makeText(this, "Location permission is required for radius search", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Linking Java variables with XML views
        btnBack = findViewById(R.id.btnBack);
        btnSearchLocation = findViewById(R.id.btnSearchLocation);
        editSearchLocation = findViewById(R.id.editSearchLocation);

        databaseStore = new DatabaseStore(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Getting radius value from MainActivity
        radiusKm = getIntent().getDoubleExtra("radius_km", 10.0);

        // Back button closes this screen
        btnBack.setOnClickListener(v -> finish());

        // Search button moves the map camera to the searched location
        btnSearchLocation.setOnClickListener(v -> searchLocationOnMap());

        // Loading Google Map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // This method runs when Google Map is ready to use
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loadUserLocationAndItems();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void loadUserLocationAndItems() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(userLocation -> {
            if (userLocation == null) {
                Toast.makeText(this, "Unable to get current location. Open Maps once or try again.", Toast.LENGTH_LONG).show();
                return;
            }

            currentUserLocation = userLocation;

            LatLng userLatLng = new LatLng(
                    userLocation.getLatitude(),
                    userLocation.getLongitude()
            );

            // Marker for user's current location
            googleMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title("You are here"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13));

            // Show only items inside selected radius
            showItemsWithinRadius(userLocation);
        });
    }

    private void showItemsWithinRadius(Location userLocation) {
        Cursor cursor = null;
        int shownCount = 0;

        try {
            cursor = databaseStore.getAllAdverts();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String locationText = cursor.getString(cursor.getColumnIndexOrThrow("location"));

                    double itemLatitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                    double itemLongitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                    Location itemLocation = new Location("");
                    itemLocation.setLatitude(itemLatitude);
                    itemLocation.setLongitude(itemLongitude);

                    // distanceTo gives metres, so divide by 1000 to convert into km
                    double distanceKm = userLocation.distanceTo(itemLocation) / 1000.0;

                    // Only show item if it is inside selected radius
                    if (distanceKm <= radiusKm) {
                        LatLng itemLatLng = new LatLng(itemLatitude, itemLongitude);

                        googleMap.addMarker(new MarkerOptions()
                                .position(itemLatLng)
                                .title(postType + " " + name)
                                .snippet(category + " • " + locationText));

                        shownCount++;
                    }

                } while (cursor.moveToNext());
            }

            Toast.makeText(
                    this,
                    shownCount + " item(s) found within " + radiusKm + " km",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error loading map items: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void searchLocationOnMap() {
        String searchText = editSearchLocation.getText().toString().trim();

        if (searchText.isEmpty()) {
            Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            // This converts typed location name into latitude and longitude
            List<Address> addresses = geocoder.getFromLocationName(searchText, 1);

            if (addresses == null || addresses.isEmpty()) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Address address = addresses.get(0);

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();

            LatLng searchedLatLng = new LatLng(latitude, longitude);

            // Add marker on searched location
            googleMap.addMarker(new MarkerOptions()
                    .position(searchedLatLng)
                    .title(searchText));

            // Move camera to searched location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLng, 14));

        } catch (Exception e) {
            Toast.makeText(this, "Search failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}