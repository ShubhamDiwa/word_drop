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

### 3. Run the Script
Open your terminal/command prompt, navigate to this `scripts` folder, and run:

```bash
cd D:\word_drop\scripts
python upload_words.py
```

The script will read your CSV, generate unique IDs (if you didn't provide them), add a current timestamp (`updatedAt`), and push them directly to your `words` collection in Firestore!