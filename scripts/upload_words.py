import csv
import firebase_admin
from firebase_admin import credentials, firestore
import uuid
import time
import os

# ==========================================
# CONFIGURATION
# ==========================================
# 1. Place your CSV file in this scripts folder and name it 'words.csv'
CSV_FILE_PATH = 'words.csv'

# 2. You must generate a Firebase Service Account Key (JSON file)
# Go to Firebase Console -> Project Settings -> Service Accounts -> Generate New Private Key
# Save the JSON file in this scripts folder and rename it to 'serviceAccountKey.json'
SERVICE_ACCOUNT_KEY_PATH = 'serviceAccountKey.json'

COLLECTION_NAME = 'words'
# ==========================================

def initialize_firebase():
    if not os.path.exists(SERVICE_ACCOUNT_KEY_PATH):
        print(f"ERROR: Cannot find {SERVICE_ACCOUNT_KEY_PATH}")
        print("Please download your Service Account Key from Firebase Console and place it in this folder.")
        exit(1)

    cred = credentials.Certificate(SERVICE_ACCOUNT_KEY_PATH)
    firebase_admin.initialize_app(cred)
    return firestore.client()

def upload_csv_to_firestore(db):
    if not os.path.exists(CSV_FILE_PATH):
        print(f"ERROR: Cannot find {CSV_FILE_PATH}")
        print("Please place your CSV file in the scripts folder.")
        exit(1)

    print(f"Reading {CSV_FILE_PATH}...")

    success_count = 0
    error_count = 0

    with open(CSV_FILE_PATH, mode='r', encoding='utf-8') as file:
        # Notice: Changed delimiter to ';' based on the provided CSV format
        csv_reader = csv.DictReader(file, delimiter=';')

        # Print detected columns to help debug
        print(f"Detected columns: {csv_reader.fieldnames}")

        for row in csv_reader:
            try:
                # Map CSV columns to Firestore document fields
                # Adjust these row['ColumnName'] keys to exactly match your CSV headers!
                word = row.get('word', '').strip()
                if not word:
                    continue # Skip empty rows

                # Generate a unique ID if one isn't provided in the CSV
                doc_id = row.get('id', '').strip()
                if not doc_id:
                    doc_id = f"word_{uuid.uuid4().hex[:12]}"

                # Construct the document data
                # Defaulting missing fields to safe values
                doc_data = {
                    'id': doc_id,
                    'word': word,
                    'definition': row.get('definition', '').strip(),
                    'partOfSpeech': row.get('partOfSpeech', '').strip().upper(),
                    'pronunciation': row.get('pronunciation', '').strip(),
                    'simpleMeaning': row.get('simpleMeaning', '').strip(),
                    'example': row.get('example', '').strip(),
                    'synonyms': row.get('synonyms', '').strip(), # Expected comma-separated string
                    'difficulty': row.get('difficulty', 'INTERMEDIATE').strip().upper(),
                    'category': row.get('category', 'General').strip(),
                    'audioUrl': row.get('audioUrl', '').strip(),
                    'active': True,
                    'version': 1,
                    'updatedAt': int(time.time() * 1000) # Current timestamp in milliseconds
                }

                # Upload to Firestore
                db.collection(COLLECTION_NAME).document(doc_id).set(doc_data)
                print(f"Uploaded: {word} ({doc_id})")
                success_count += 1

            except Exception as e:
                print(f"Error uploading row {row}: {e}")
                error_count += 1

    print("\n==========================================")
    print(f"Upload Complete!")
    print(f"Successfully uploaded: {success_count} words")
    print(f"Failed to upload: {error_count} words")
    print("==========================================")

if __name__ == '__main__':
    print("Initializing Firebase...")
    db = initialize_firebase()
    upload_csv_to_firestore(db)
