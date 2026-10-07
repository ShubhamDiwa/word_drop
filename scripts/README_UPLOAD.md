# Firebase Bulk Upload Script

This folder contains a Python script to quickly upload hundreds of words from a CSV file directly into your Firebase Firestore database.

## Prerequisites

1. **Install Python**: Make sure Python is installed on your computer.
2. **Install Firebase Admin SDK**: Open your terminal/command prompt and run:
   ```bash
   pip install firebase-admin
   ```

## Setup Instructions

### 1. Prepare Your CSV File
Create or copy your CSV file into this `scripts` folder and name it **`words.csv`**. 
The CSV file MUST have a header row. The script currently expects headers similar to this:
`word,definition,partOfSpeech,pronunciation,simpleMeaning,example,synonyms,difficulty,category,audioUrl`

*(If your CSV headers are named differently, open `upload_words.py` in a text editor and change the `row.get('headerName')` keys to match your CSV).*

### 2. Get Your Firebase Service Account Key
To allow a Python script to write to your database, you need a secret admin key from Google:
1. Go to your [Firebase Console](https://console.firebase.google.com/).
2. Click the **Gear icon (Project settings)** -> **Service accounts** tab.
3. Make sure **Firebase Admin SDK** is selected, and click **Generate new private key**.
4. Download the `.json` file.
5. Move that downloaded `.json` file into this `scripts` folder.
6. Rename the file to **`serviceAccountKey.json`**.

### 3. Run the Script Directly from Android Studio

You do **NOT** need to publish a new app release to Google Play Store to add new words:

1. Open [`scripts/words.csv`](file:///d:/word_drop/scripts/words.csv) in Android Studio.
2. Add your new word rows.
3. Open the **Terminal** tab at the bottom of Android Studio.
4. Run:
   ```bash
   python scripts/upload_words.py
   ```
5. All live production users will automatically sync the new words upon opening the app!