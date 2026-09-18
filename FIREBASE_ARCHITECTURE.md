# Firebase Vocabulary Architecture

This document describes the architectural integration of Firebase as the cloud master source for Word Drop vocabulary.

## 1. Flow Overview: `Firebase → Repository → Room`

```
 ┌───────────────────────────────────────────────┐
 │              Firebase Firestore               │
 │     (Cloud Master Vocabulary Source)          │
 └───────────────────────┬───────────────────────┘
                         │ (updatedAt > lastSyncedAt)
                         ▼
 ┌───────────────────────────────────────────────┐
 │          VocabularyRemoteDataSource           │
 │    (FirebaseVocabularyDataSource Impl)        │
 └───────────────────────┬───────────────────────┘
                         │
                         ▼
 ┌───────────────────────────────────────────────┐
 │                WordRepository                 │
 │     (Validation, Active/Inactive Filtering,   │
 │      & User Progress Preservation)            │
 └───────────┬───────────────────────────┬───────┘
             │                           │
             ▼                           ▼
 ┌───────────────────────┐   ┌───────────────────────┐
 │     Room Database     │   │    Dictionary API     │
 │    (Offline Cache &   │   │  (Optional Enrichment │
 │   Runtime Source)     │   │      / Import)        │
 └───────────┬───────────┘   └───────────────────────┘
             │
             ▼
 ┌───────────────────────┐
 │  UI & Notifications   │
 └───────────────────────┘
```

### Key Architectural Principles
- **Room as Runtime Source**: App UI, ViewModels, and Notification Manager interact strictly with Room (`WordDao`). Notifications never make direct calls to Firebase.
- **Repository Abstraction**: All remote sync logic is encapsulated inside `WordRepositoryImpl` via `VocabularyRemoteDataSource`.
- **Incremental Synchronization**: Only changed or new records (`updatedAt > lastSyncedAt`) are downloaded from Firebase, saving bandwidth and battery.
- **User Progress Preservation**: When a word's definition or fields are updated remotely, local user progress state (`isLearned`, `timesShown`, `lastShownAt`) is preserved during the Room merge.

---

## 2. Firebase Database Schema

Collection path: `words/{wordId}`

| Field | Type | Description |
| :--- | :--- | :--- |
| `word` | String | Word title (e.g. "Ethereal") |
| `pronunciation` | String? | IPA pronunciation (e.g. "/ɪˈθɪə.ri.əl/") |
| `partOfSpeech` | String? | "NOUN", "VERB", "ADJECTIVE", etc. |
| `definition` | String | Full definition of the word |
| `simpleMeaning` | String? | Beginner-friendly short explanation |
| `example` | String? | Usage sentence |
| `synonyms` | List\<String\> or String | Synonyms list or comma-separated string |
| `difficulty` | String | "BEGINNER", "INTERMEDIATE", or "ADVANCED" |
| `category` | String? | Topic category (e.g. "Academic", "Literary") |
| `audioUrl` | String? | URL for pronunciation audio |
| `active` | Boolean | `true` if active, `false` if deactivated/deleted |
| `version` | Long | Schema/content revision number |
| `updatedAt` | Long | Epoch timestamp in milliseconds |

---

## 3. Firestore Security Rules

Client applications should have **read-only** access to the `words` collection. Content updates must be performed using the Firebase Admin SDK or Console.

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /words/{wordId} {
      allow read: if true;
      allow write: if false;
    }
  }
}
```

---

## 4. Error Handling & Offline Reliability

1. **Missing Config Safe Guard**: If `google-services.json` is missing or Firebase is uninitialized, `FirebaseVocabularyDataSource` catches exceptions gracefully and returns an empty sync list without crashing.
2. **Local Initial Seed**: If Room is completely empty (e.g., fresh install offline), `WordRepositoryImpl` seeds local database from `InitialSeedData` first before attempting remote sync.
