///*MainActivity.java*/
//package com.example.lost_and_found_app;
//
//import android.app.AlertDialog;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
///*
//    MainActivity works as the home screen.
//    It follows the first wireframe screen:
//    - Create a new advert
//    - Show all lost and found items
//    Subtask 1 adds a category filter before showing the list.
//*/
//public class MainActivity extends AppCompatActivity {
//
//    Button btnCreateAdvert, btnShowItems;
//    Spinner spinnerCategoryFilter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Linking Java variables with XML views
//        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
//        btnShowItems = findViewById(R.id.btnShowItems);
//        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
//
//        // Category list used for filtering lost/found adverts
//        String[] categories = {
//                "All Categories",
//                "Electronics",
//                "Pets",
//                "Wallets",
//                "Keys",
//                "Bags",
//                "Other"
//        };
//
//        // Setting category values inside the spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                categories
//        );
//
//        spinnerCategoryFilter.setAdapter(adapter);
//
//        // Open the create advert screen
//        btnCreateAdvert.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, CreateAdvertActivity.class);
//            startActivity(intent);
//        });
//
//        // Open the item list screen with selected category filter
//        btnShowItems.setOnClickListener(v -> {
//            String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();
//
//            Intent intent = new Intent(MainActivity.this, ItemListingActivity.class);
//            intent.putExtra("category_filter", selectedCategory);
//            startActivity(intent);
//        });
//    }
//}
//
///*CreateAdvertActivity.java*/
//package com.example.lost_and_found_app;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.Spinner;
//import android.widget.Toast;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
///*
//    This activity allows the user to create a lost or found advert.
//    Subtask 1 added category selection.
//    Subtask 2 requires the user to upload an image before saving the advert.
//*/
//public class CreateAdvertActivity extends AppCompatActivity {
//
//    RadioButton radioLost, radioFound;
//    Spinner spinnerCategory;
//    EditText editName, editPhone, editDescription, editDate, editLocation;
//    Button btnSave, btnChooseImage;
//    ImageView imgSelectedItem;
//
//    DatabaseStore databaseStore;
//
//    // Stores selected image path as text in SQLite
//    String selectedImageUri = "";
//
//    // Image picker launcher for selecting image from phone gallery
//    ActivityResultLauncher<String> imagePickerLauncher =
//            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
//                if (uri != null) {
//                    selectedImageUri = uri.toString();
//                    imgSelectedItem.setImageURI(uri);
//
//                    // VERY IMPORTANT: persist permission
//                    getContentResolver().takePersistableUriPermission(
//                            uri,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    );
//                }
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_advert);
//
//        // Linking XML views with Java variables
//        radioLost = findViewById(R.id.radioLost);
//        radioFound = findViewById(R.id.radioFound);
//        spinnerCategory = findViewById(R.id.spinnerCategory);
//        editName = findViewById(R.id.editName);
//        editPhone = findViewById(R.id.editPhone);
//        editDescription = findViewById(R.id.editDescription);
//        editDate = findViewById(R.id.editDate);
//        editLocation = findViewById(R.id.editLocation);
//        btnSave = findViewById(R.id.btnSave);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//        imgSelectedItem = findViewById(R.id.imgSelectedItem);
//
//        // Creating database helper object
//        databaseStore = new DatabaseStore(this);
//
//        // Category list for adverts
//        String[] categories = {
//                "Electronics",
//                "Pets",
//                "Wallets",
//                "Keys",
//                "Bags",
//                "Other"
//        };
//
//        // Setting category spinner values
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                categories
//        );
//
//        spinnerCategory.setAdapter(adapter);
//
//        // Opens phone gallery to choose item image
//        btnChooseImage.setOnClickListener(v -> {
//            imagePickerLauncher.launch("image/*");
//        });
//
//        // Save advert when button is clicked
//        btnSave.setOnClickListener(v -> saveAdvert());
//    }
//
//    private void saveAdvert() {
//        // Getting values from form fields
//        String postType = radioLost.isChecked() ? "Lost" : "Found";
//        String category = spinnerCategory.getSelectedItem().toString();
//        String name = editName.getText().toString().trim();
//        String phone = editPhone.getText().toString().trim();
//        String description = editDescription.getText().toString().trim();
//        String date = editDate.getText().toString().trim();
//        String location = editLocation.getText().toString().trim();
//
//        // Simple validation to avoid empty adverts
//        if (name.isEmpty() || phone.isEmpty() || description.isEmpty()
//                || date.isEmpty() || location.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Subtask 2 validation: image is compulsory
//        if (selectedImageUri.isEmpty()) {
//            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Subtask 3
//        // Auto timestamp generation (system time)
//        String timestamp = new SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss",
//                Locale.getDefault()
//        ).format(new Date());
//
//        // Inserting advert into SQLite database with image path
//        boolean inserted = databaseStore.insertAdvert(
//                postType,
//                category,
//                name,
//                phone,
//                description,
//                date,
//                location,
//                selectedImageUri,
//                timestamp   // NEW
//        );
//
//        if (inserted) {
//            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
//
///*DatabaseStore.java*/
//package com.example.lost_and_found_app;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
///*
//    DatabaseStore manages the SQLite database.
//    Subtask 1 added category filtering.
//    Subtask 2 adds image storage using image URI text.
//*/
//public class DatabaseStore extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "LostFound.db";
//
//    // Version increased because image_uri column is added
//    private static final int DATABASE_VERSION = 3;
//
//    private static final String TABLE_NAME = "adverts";
//
//    public DatabaseStore(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    // Creating the adverts table
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "post_type TEXT, " +
//                "category TEXT, " +
//                "name TEXT, " +
//                "phone TEXT, " +
//                "description TEXT, " +
//                "date TEXT, " + // existing (user input)
//                "location TEXT, " +
//                "image_uri TEXT, " +
//                "timestamp TEXT)";   // NEW
//
//        db.execSQL(createTable);
//    }
//
//    // Recreating table if database version changes
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    // Insert a new lost/found advert with image URI
//    public boolean insertAdvert(String postType, String category, String name,
//                                String phone, String description,
//                                String date, String location,
//                                String imageUri, String timestamp) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put("post_type", postType);
//        values.put("category", category);
//        values.put("name", name);
//        values.put("phone", phone);
//        values.put("description", description);
//        values.put("date", date);
//        values.put("location", location);
//        values.put("image_uri", imageUri);
//        values.put("timestamp", timestamp); // NEW
//
//        long result = db.insert(TABLE_NAME, null, values);
//
//        return result != -1;
//    }
//
//    // Get all adverts without category filter
//    public Cursor getAllAdverts() {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        return db.rawQuery(
//                "SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC",
//                null
//        );
//    }
//
//    // Get adverts filtered by category
//    public Cursor getAdvertsByCategory(String category) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        return db.rawQuery(
//                "SELECT * FROM " + TABLE_NAME + " WHERE category = ? ORDER BY id DESC",
//                new String[]{category}
//        );
//    }
//
//    // Get a single advert using id
//    public Cursor getAdvertById(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        return db.rawQuery(
//                "SELECT * FROM " + TABLE_NAME + " WHERE id = ?",
//                new String[]{String.valueOf(id)}
//        );
//    }
//
//    // Delete advert after item is returned to owner
//    public boolean deleteAdvert(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        int result = db.delete(
//                TABLE_NAME,
//                "id = ?",
//                new String[]{String.valueOf(id)}
//        );
//
//        return result > 0;
//    }
//}
//
///*ItemDetailActivity.java*/
//package com.example.lost_and_found_app;
//
//import android.app.AlertDialog;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///*
//    This screen appears after clicking an item from the list.
//    It shows the selected advert details and allows removing the advert.
//*/
//public class ItemDetailActivity extends AppCompatActivity {
//
//    TextView txtItemDetails;
//    ImageView imgItemDetail;
//    Button btnRemove, btnBackToList;
//
//    DatabaseStore databaseStore;
//    int advertId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_item_detail);
//
//        // Linking XML views with Java variables
//        txtItemDetails = findViewById(R.id.txtItemDetails);
//        imgItemDetail = findViewById(R.id.imgItemDetail);
//        btnRemove = findViewById(R.id.btnRemove);
//        btnBackToList = findViewById(R.id.btnBackToList);
//
//        databaseStore = new DatabaseStore(this);
//
//        // Getting selected advert id from ItemListingActivity
//        advertId = getIntent().getIntExtra("advert_id", -1);
//
//        if (advertId == -1) {
//            Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        loadAdvertDetails();
//
//        // Back navigation to list screen
//        btnBackToList.setOnClickListener(v -> finish());
//
//        // Confirm before deleting item
//        btnRemove.setOnClickListener(v -> showDeleteConfirmation());
//    }
//
//    private void loadAdvertDetails() {
//        Cursor cursor = null;
//
//        try {
//            cursor = databaseStore.getAdvertById(advertId);
//
//            if (cursor == null || !cursor.moveToFirst()) {
//                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            }
//
//            String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
//            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
//            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
//            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
//            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
//            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
//
//            // Display uploaded item image
//            if (imageUri != null && !imageUri.trim().isEmpty()) {
//                try {
//                    if (imageUri != null && !imageUri.trim().isEmpty()) {
//                        Uri uri = Uri.parse(imageUri);
//                        imgItemDetail.setImageURI(uri);
//                    } else {
//                        imgItemDetail.setImageResource(android.R.color.darker_gray);
//                    }
//                } catch (Exception e) {
//                    imgItemDetail.setImageResource(android.R.color.darker_gray);
//                    Toast.makeText(this, "Image could not be loaded", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            String details =
//                    postType + " " + name + "\n\n" +
//                            formatTime(timestamp) + "\n\n" +
//                            "At " + location + "\n\n" +
//                            "Category: " + category + "\n\n" +
//                            "Phone: " + phone + "\n\n" +
//                            "Description: " + description + "\n\n" +
//                            "Date: " + date;
//
//            txtItemDetails.setText(details);
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Error loading item: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            finish();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//
//    private void showDeleteConfirmation() {
//        new AlertDialog.Builder(this)
//                .setTitle("Remove item")
//                .setMessage("Remove this advert from the lost and found list?")
//                .setPositiveButton("Remove", (dialog, which) -> removeAdvert())
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    private void removeAdvert() {
//        try {
//            boolean deleted = databaseStore.deleteAdvert(advertId);
//
//            if (deleted) {
//                Toast.makeText(this, "Advert removed successfully", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Error removing item: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private String formatTime(String timestamp) {
//        try {
//            if (timestamp == null || timestamp.isEmpty()) {
//                return "Unknown time";
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            Date postedDate = sdf.parse(timestamp);
//            Date currentDate = new Date();
//
//            if (postedDate == null) {
//                return timestamp;
//            }
//
//            long difference = currentDate.getTime() - postedDate.getTime();
//
//            long minutes = difference / (1000 * 60);
//            long hours = minutes / 60;
//            long days = hours / 24;
//
//            if (days > 0) {
//                return days + " days ago";
//            } else if (hours > 0) {
//                return hours + " hours ago";
//            } else if (minutes > 0) {
//                return minutes + " minutes ago";
//            } else {
//                return "Just now";
//            }
//
//        } catch (Exception e) {
//            return timestamp;
//        }
//    }
//}
//
///*ItemListingActivity.java*/
//package com.example.lost_and_found_app;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
///*
//    This screen shows all lost and found adverts.
//    When an item is clicked, it opens the detail screen with remove option.
//*/
//public class ItemListingActivity extends AppCompatActivity {
//
//    TextView txtFilterInfo;
//    ListView listItems;
//    Button btnBackToHome;
//
//    DatabaseStore databaseStore;
//
//    ArrayList<String> itemTitles;
//    ArrayList<Integer> itemIds;
//
//    boolean hasRealItems = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_item_listing);
//
//        // Linking XML views with Java variables
//        txtFilterInfo = findViewById(R.id.txtFilterInfo);
//        listItems = findViewById(R.id.listItems);
//        btnBackToHome = findViewById(R.id.btnBackToHome);
//
//        databaseStore = new DatabaseStore(this);
//
//        itemTitles = new ArrayList<>();
//        itemIds = new ArrayList<>();
//
//        // Getting selected category filter from MainActivity
//        String categoryFilter = getIntent().getStringExtra("category_filter");
//
//        if (categoryFilter == null || categoryFilter.trim().isEmpty()) {
//            categoryFilter = "All Categories";
//        }
//
//        txtFilterInfo.setText("Category: " + categoryFilter);
//
//        loadItems(categoryFilter);
//
//        // Back navigation to home screen
//        btnBackToHome.setOnClickListener(v -> finish());
//
//        // Opens detail screen when a real item is clicked
//        listItems.setOnItemClickListener((parent, view, position, id) -> {
//            if (!hasRealItems) {
//                Toast.makeText(this, "No item available to open", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (position < 0 || position >= itemIds.size()) {
//                Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Intent intent = new Intent(ItemListingActivity.this, ItemDetailActivity.class);
//            intent.putExtra("advert_id", itemIds.get(position));
//            startActivity(intent);
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Refresh list after returning from detail screen
//        String categoryFilter = getIntent().getStringExtra("category_filter");
//
//        if (categoryFilter == null || categoryFilter.trim().isEmpty()) {
//            categoryFilter = "All Categories";
//        }
//
//        loadItems(categoryFilter);
//    }
//
//    private void loadItems(String categoryFilter) {
//        itemTitles.clear();
//        itemIds.clear();
//        hasRealItems = false;
//
//        Cursor cursor = null;
//
//        try {
//            if (categoryFilter.equals("All Categories")) {
//                cursor = databaseStore.getAllAdverts();
//            } else {
//                cursor = databaseStore.getAdvertsByCategory(categoryFilter);
//            }
//
//            if (cursor != null && cursor.moveToFirst()) {
//                hasRealItems = true;
//
//                do {
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//                    String postType = cursor.getString(cursor.getColumnIndexOrThrow("post_type"));
//                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
//                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
//
//                    itemIds.add(id);
//
//                    // This follows the wireframe style: Lost Key ... / Found AirPods ...
//                    itemTitles.add(postType + " " + name + " ...\n"
//                            + category + " • " + formatTime(timestamp));
//
//                } while (cursor.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Error loading items: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//
//        if (itemTitles.isEmpty()) {
//            itemTitles.add("No lost or found items available");
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                itemTitles
//        );
//
//        listItems.setAdapter(adapter);
//    }
//
//    private String formatTime(String timestamp) {
//        try {
//            if (timestamp == null || timestamp.isEmpty()) {
//                return "Unknown time";
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            Date postedDate = sdf.parse(timestamp);
//            Date currentDate = new Date();
//
//            if (postedDate == null) {
//                return timestamp;
//            }
//
//            long difference = currentDate.getTime() - postedDate.getTime();
//
//            long minutes = difference / (1000 * 60);
//            long hours = minutes / 60;
//            long days = hours / 24;
//
//            if (days > 0) {
//                return days + " days ago";
//            } else if (hours > 0) {
//                return hours + " hours ago";
//            } else if (minutes > 0) {
//                return minutes + " minutes ago";
//            } else {
//                return "Just now";
//            }
//
//        } catch (Exception e) {
//            return timestamp;
//        }
//    }
//}