# Google AdMob Configuration Guide

Wordzip is integrated with Google AdMob SDK (`com.google.android.gms:play-services-ads`).

## Active Production Configuration

Wordzip is configured with production Google AdMob IDs:

| Component | Active Production ID | File Location |
| :--- | :--- | :--- |
| **AdMob App ID** | `ca-app-pub-5770944272187781~8690847497` | [AndroidManifest.xml](file:///D:/word_drop/app/src/main/AndroidManifest.xml) |
| **Banner Ad Unit ID** | `ca-app-pub-5770944272187781/1438897644` | [BannerAd.kt](file:///D:/word_drop/app/src/main/java/com/diws/worddrop/ui/components/BannerAd.kt) |
| **Rewarded Ad Unit ID** | `ca-app-pub-5770944272187781/3366669945` | [DeveloperInfoSheet.kt](file:///D:/word_drop/app/src/main/java/com/diws/worddrop/ui/components/DeveloperInfoSheet.kt) |

---

## Steps for Production Release (Google Play Store)

Before publishing Wordzip to Google Play, update the Test IDs with your real AdMob production IDs:

### Step 1: Create an AdMob Account & App
1. Go to [Google AdMob Console](https://admob.google.com/).
2. Click **Apps** -> **Add App** -> Select **Android**.
3. Copy your **AdMob App ID** (Format: `ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX`).

### Step 2: Update App ID in `AndroidManifest.xml`
Open [AndroidManifest.xml](file:///D:/word_drop/app/src/main/AndroidManifest.xml) and replace the value tag:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-YOUR_REAL_ADMOB_APP_ID_HERE" />
```

### Step 3: Create Banner Ad Unit
1. In AdMob Console, click **Ad units** -> **Add Ad Unit** -> Select **Banner**.
2. Copy your **Banner Ad Unit ID** (Format: `ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX`).

### Step 4: Update Banner Ad Unit ID in `BannerAd.kt`
Open [BannerAd.kt](file:///D:/word_drop/app/src/main/java/com/diws/worddrop/ui/components/BannerAd.kt) and update the default parameter:

```kotlin
@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-YOUR_REAL_BANNER_AD_UNIT_ID_HERE"
)
```

### Step 5: Create Rewarded Ad Unit & Update `DeveloperInfoSheet.kt`
1. In AdMob Console, click **Ad units** -> **Add Ad Unit** -> Select **Rewarded**.
2. Copy your **Rewarded Ad Unit ID** (Format: `ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX`).
3. Open [DeveloperInfoSheet.kt](file:///D:/word_drop/app/src/main/java/com/diws/worddrop/ui/components/DeveloperInfoSheet.kt) and update the parameter:

```kotlin
@Composable
fun DeveloperInfoSheet(
    ...
    rewardAdUnitId: String = "ca-app-pub-YOUR_REAL_REWARDED_AD_UNIT_ID_HERE"
)
```

