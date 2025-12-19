# IMPORTANT: Building This Project

## Internet Access Required

This Android project requires internet access to download build dependencies when you first build it.

The following dependencies will be downloaded automatically by Gradle:
- Android Gradle Plugin (7.4.2)
- AndroidX libraries
- Material Design Components
- Other Android SDK components

## How to Build

### Prerequisites
1. Install Android Studio: https://developer.android.com/studio
2. Install Android SDK (API level 33)
3. Ensure you have internet connectivity

### Build Steps

**Option 1: Using Android Studio (Recommended)**
1. Open Android Studio
2. Click "Open Project"
3. Select this directory
4. Wait for Gradle sync (this will download dependencies - requires internet)
5. Click Build > Build Bundle(s) / APK(s) > Build APK(s)
6. APK will be in `app/build/outputs/apk/debug/`

**Option 2: Using Command Line**
```bash
# On Linux/Mac:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```

The first build will take longer as it downloads all dependencies.

## What This App Does

This app collects comprehensive device information for TWRP Builder:
- Device codename, brand, model
- Android version and API level
- Screen resolution (required for TWRP)
- CPU architecture
- Kernel version
- Build fingerprint
- And much more!

The information is saved to: `sdcard/Download/twrp-builder-{codename}.txt`

## Using the App

1. Install the APK on your Android device
2. Open "TWRP Info Collector"
3. Information is automatically collected
4. Tap "Save to File" to save
5. Use the file when building TWRP at: https://www.hovatek.com/twrpbuilder/

## Project Structure

This is a complete Android application with:
- ✅ MainActivity.java - Full device info collection logic
- ✅ UI Layout - User-friendly interface
- ✅ Permissions handling - For file saving
- ✅ Build configuration - Ready to compile
- ✅ Documentation - Complete instructions

All source code is included and ready to build!
