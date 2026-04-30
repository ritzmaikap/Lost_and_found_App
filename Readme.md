# Lost and Found Android App

## Overview

The Lost and Found Android App is a mobile application developed using **Java**, **XML**, and **SQLite** in Android Studio. The purpose of this app is to help users create, view, filter, and remove lost or found item adverts.

The app follows the given assessment wireframe structure and includes additional functionality such as category filtering, required image upload, automatic posting timestamp, item detail view, navigation buttons, and error handling.

---

## Features

### 1. Create a New Advert

Users can create a lost or found item post by entering:

- Post type: Lost or Found
- Category
- Item name
- Phone number
- Description
- Date
- Location
- Item image

The app validates the form before saving, so empty adverts cannot be submitted.

---

### 2. Category Search / Filter

Users can filter lost and found posts by category.

Supported categories include:

- All Categories
- Electronics
- Pets
- Wallets
- Keys
- Bags
- Other

The selected category is passed from the main screen to the item list screen using an Android `Intent`.

---

### 3. Required Image Upload

Each lost or found post requires an image before it can be saved.

The image is selected from the device gallery and displayed as a preview before saving. The image URI is stored in the SQLite database.

---

### 4. Automatic Date and Time Stamp

Each post automatically stores the date and time when it is created.

This allows users to see how recent a listing is. The app can display posting time as:

- Just now
- Minutes ago
- Hours ago
- Days ago

---

### 5. View All Lost and Found Items

The item list screen displays saved adverts in a simple list format.

Each item shows:

- Lost or Found status
- Item name
- Category
- Posting time

Clicking an item opens the detail screen.

---

### 6. Item Detail Screen and Remove Option

When an item is clicked, the app opens a detail screen showing:

- Uploaded image
- Item name
- Category
- Posting time
- Phone number
- Description
- Date
- Location

The user can remove the advert after the item has been returned to its owner.

A confirmation dialog is shown before deletion to prevent accidental removal.

---

## Technologies Used

- Java
- XML
- Android Studio
- SQLite
- Android Intent Navigation
- Activity Result API
- Android ListView
- Toast Messages
- AlertDialog

---

## App Screens

### Main Screen

The main screen contains:

- `CREATE A NEW ADVERT` button
- Category filter spinner
- `SHOW ALL LOST & FOUND ITEMS` button

This screen is handled by:

```text
activity_main.xml
MainActivity.java
