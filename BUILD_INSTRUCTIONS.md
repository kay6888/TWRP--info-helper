# Build Instructions for TWRP Device Info Collector

This document provides detailed instructions for building the TWRP Device Info Collector APK.

## Prerequisites

Before building, ensure you have the following installed:

1. **Java Development Kit (JDK) 8 or higher**
   - Check: `java -version`
   - Download: https://adoptium.net/

2. **Android SDK**
   - You can install via Android Studio or command-line tools
   - Required SDK version: API 33 (Android 13)

3. **Git** (for cloning the repository)
   - Check: `git --version`

## Quick Build (Recommended)

### Method 1: Using Gradle Wrapper

The easiest way to build the project:

```bash
# Clone the repository
git clone https://github.com/kay6888/Hovatek--Online--TWRP--Builder-help.git
cd Hovatek--Online--TWRP--Builder-help

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# The APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

For Windows:
```cmd
gradlew.bat assembleDebug
```

### Method 2: Using Android Studio

1. Download and install [Android Studio](https://developer.android.com/studio)
2. Open Android Studio
3. Select "Open an Existing Project"
4. Navigate to the cloned repository directory
5. Wait for Gradle sync to complete
6. Select "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
7. Find the APK in `app/build/outputs/apk/debug/`

## Build Variants

### Debug Build (Development)
```bash
./gradlew assembleDebug
```
- Output: `app/build/outputs/apk/debug/app-debug.apk`
- This APK can be installed directly on any device for testing

### Release Build (Production)

```bash
./gradlew assembleRelease
```
- Output: `app/build/outputs/apk/release/app-release-unsigned.apk`
- This APK needs to be signed before distribution

## Signing the Release APK (Optional)

For distributing to users, you should sign the release APK:

### Generate a Keystore

```bash
keytool -genkey -v -keystore twrp-info-release.keystore -alias twrp-info -keyalg RSA -keysize 2048 -validity 10000
```

### Sign the APK

```bash
# Using jarsigner
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore twrp-info-release.keystore app/build/outputs/apk/release/app-release-unsigned.apk twrp-info

# Using apksigner (recommended)
apksigner sign --ks twrp-info-release.keystore --out app-release-signed.apk app/build/outputs/apk/release/app-release-unsigned.apk
```

### Verify the signature

```bash
apksigner verify app-release-signed.apk
```

## Installing the APK

### Using ADB (Android Debug Bridge)

```bash
# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Or force reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Manual Installation

1. Transfer the APK to your Android device
2. Enable "Install from Unknown Sources" in Settings
3. Use a file manager to open the APK
4. Follow the installation prompts

## Troubleshooting

### Problem: "SDK location not found"

**Solution**: Create a `local.properties` file in the project root:

```properties
sdk.dir=/path/to/your/Android/sdk
```

For example:
- Windows: `sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
- Mac: `sdk.dir=/Users/YourName/Library/Android/sdk`
- Linux: `sdk.dir=/home/YourName/Android/Sdk`

### Problem: "Java version mismatch"

**Solution**: Ensure you're using JDK 8 or higher:

```bash
java -version
# Should show 1.8.x or higher
```

### Problem: Gradle build fails

**Solution**: Try cleaning and rebuilding:

```bash
./gradlew clean
./gradlew assembleDebug
```

### Problem: "Failed to resolve dependencies"

**Solution**: Check your internet connection and try:

```bash
./gradlew build --refresh-dependencies
```

## Build Configuration

The project uses the following configuration:

- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **Target SDK**: API 33 (Android 13)
- **Compile SDK**: API 33
- **Build Tools**: Automatically selected by Gradle
- **Gradle Version**: 7.5
- **Android Gradle Plugin**: 7.4.2

## Project Structure

```
Hovatek--Online--TWRP--Builder-help/
├── app/
│   ├── build.gradle                 # App-level build configuration
│   ├── proguard-rules.pro          # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  # App manifest
│           ├── java/com/hovatek/twrpinfo/
│           │   └── MainActivity.java # Main activity
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml
│               ├── values/
│               │   ├── strings.xml
│               │   └── colors.xml
│               └── drawable/
│                   └── ic_launcher_*.xml
├── build.gradle                     # Project-level build config
├── settings.gradle                  # Project settings
├── gradle.properties               # Gradle properties
└── gradlew                         # Gradle wrapper script
```

## Customization

To customize the app:

1. **App Name**: Edit `app/src/main/res/values/strings.xml`
2. **Package Name**: Change in `app/build.gradle` and refactor in Android Studio
3. **Colors**: Edit `app/src/main/res/values/colors.xml`
4. **Icon**: Replace launcher icons in `res/mipmap-*` folders

## Testing

### Run on Emulator

1. Open Android Studio
2. Create/start an Android Virtual Device (AVD)
3. Click "Run" or press Shift+F10

### Run on Physical Device

1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect device via USB
4. Run: `./gradlew installDebug`

## Clean Build

To perform a clean build:

```bash
./gradlew clean assembleDebug
```

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Gradle Build Documentation](https://docs.gradle.org/)
- [Hovatek TWRP Builder](https://www.hovatek.com/twrpbuilder/)

## Support

For build issues, please open an issue on the GitHub repository with:
- Your OS and version
- Java/JDK version
- Android SDK version
- Full error message/log
