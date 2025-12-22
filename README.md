# TWRP Device Info Collector

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Ready-success.svg)]()
[![Security](https://img.shields.io/badge/Security-Passed-success.svg)](SECURITY_SUMMARY.md)

An Android application that automatically collects all device information needed for [Hovatek Online TWRP Builder](https://www.hovatek.com/twrpbuilder/).

## 🌟 Quick Links

- 📖 **[Quick Start Guide](QUICK_START.md)** - For non-technical users
- 🔨 **[Build Instructions](BUILD_INSTRUCTIONS.md)** - Detailed build guide
- 📋 **[Features](FEATURES.md)** - Complete feature list
- 🔒 **[Security](SECURITY_SUMMARY.md)** - Security audit results
- 📄 **[Sample Output](SAMPLE_OUTPUT.txt)** - Example output file

## Features

- 📱 Automatically collects comprehensive device information
- 💾 Saves data to `sdcard/Download/twrp-builder-{codename}.txt`
- 🔍 Gathers all required information for TWRP Builder:
  - Device codename, brand, model, manufacturer
  - Android version and API level
  - Screen resolution
  - CPU architecture (ABI)
  - Kernel version
  - Build fingerprint
  - Hardware information
  - **Root & Security Status** (NEW in v2.0):
    - Root access detection
    - Root permission status
    - SELinux status
    - Bootloader lock status
    - System partition mount status
  - And much more!
- 💝 **Support Development** (NEW in v2.0):
  - Easy donation options via PayPal and CashApp
  - One-tap copy of payment information

## What is this for?

This app helps you gather all the technical information about your Android device that is required when using the Hovatek Online TWRP Builder to create a custom TWRP recovery for your device.

## How to Use

1. Install the APK on your Android device
2. Open the "TWRP Info Collector" app
3. The app will automatically collect your device information
4. Tap "Save to File" to save the information
5. Find the file at: `sdcard/Download/twrp-builder-{your-device-codename}.txt`
6. Use this information when building TWRP at https://www.hovatek.com/twrpbuilder/

## Building the APK

### Prerequisites

- Android SDK (API level 34 or higher)
- Java Development Kit (JDK) 8 or higher
- Gradle 7.4.2 or higher

### Build Instructions

#### Option 1: Using Gradle (Command Line)

```bash
# Clone the repository
git clone https://github.com/kay6888/Hovatek--Online--TWRP--Builder-help.git
cd Hovatek--Online--TWRP--Builder-help

# Build the debug APK
./gradlew assembleDebug

# Or build a signed release APK
./gradlew assembleRelease

# The APK will be located at:
# app/build/outputs/apk/debug/app-debug.apk
# or
# app/build/outputs/apk/release/app-release.apk
```

#### Option 2: Using Android Studio

1. Open Android Studio
2. Click "Open an Existing Project"
3. Navigate to and select the cloned repository folder
4. Wait for Gradle to sync
5. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
6. The APK will be in `app/build/outputs/apk/`

### Building a Signed Release APK

The app is configured to build signed releases. By default, it uses the Android debug keystore for convenience. To create a production-signed APK:

1. **Generate a release keystore**:
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **Place the keystore file** in the `app/` directory

3. **Update `app/build.gradle`** signing configuration:
   ```gradle
   signingConfigs {
       release {
           storeFile file("my-release-key.jks")
           storePassword "your-store-password"
           keyAlias "my-key-alias"
           keyPassword "your-key-password"
       }
   }
   ```

4. **Build the release APK**:
   ```bash
   ./gradlew assembleRelease
   ```

5. The signed APK will be at: `app/build/outputs/apk/release/app-release.apk`

**Security Note**: Never commit your keystore file or passwords to version control. Consider using environment variables or `gradle.properties` for sensitive information.

### Installing the APK

1. Transfer the APK to your Android device
2. Enable "Install from Unknown Sources" in your device settings
3. Open the APK file and install it

## Information Collected

The app collects the following information:

- **Basic Device Info**: Codename, Product Name, Brand, Model, Manufacturer, Board, Hardware
- **Android Version Info**: Android Version, API Level, Build ID, Build Type, Tags
- **Architecture Info**: Supported ABIs, Primary ABI, CPU Architecture
- **Screen Info**: Resolution, Density, Density Scale
- **Kernel Info**: Kernel Version, Detailed Kernel Information
- **Root & Security Status** (NEW in v2.0):
  - Root availability (su binary detection)
  - Root permission status
  - SELinux status (enforcing/permissive)
  - Bootloader lock status
  - System partition mount status (ro/rw)
  - SafetyNet status note
- **Build Info**: Fingerprint, Display, Bootloader, Radio Version
- **Additional Properties**: Host, User, Build Time
- **Touch Driver Info**: Detected touch driver information (if available)

## Permissions

The app requires storage permissions to save the device information file:

- `WRITE_EXTERNAL_STORAGE` (Android 9 and below)
- Scoped storage access (Android 10+)

## Compatibility

- Minimum Android Version: 5.0 (Lollipop, API 21)
- Target Android Version: 14 (API 34)
- Supports all Android architectures (ARM, ARM64, x86, x86_64)

## 💝 Support Development

If you find this app helpful, please consider supporting the developer:

- **PayPal**: kaynikko88@gmail.com
- **CashApp**: $Nikko6888

Tap on the donation options in the app to easily copy the payment information!

## Output File Format

The app saves all information in a plain text file with clear sections:

```
=== TWRP BUILDER DEVICE INFORMATION ===

--- BASIC DEVICE INFO ---
Device Codename: [your device codename]
Brand: [your device brand]
Model: [your device model]
...

=== TWRP BUILDER REQUIREMENTS ===
Android Version: [version]
Screen Resolution: [width]x[height]
Device Codename: [codename]
...

=== INSTRUCTIONS ===
[Step-by-step guide for using Hovatek TWRP Builder]
```

## About TWRP Builder

TWRP (Team Win Recovery Project) is a custom recovery image for Android devices. The Hovatek Online TWRP Builder is a service that helps you build a custom TWRP recovery for your specific device model.

Visit: https://www.hovatek.com/twrpbuilder/

## 📊 Project Status

✅ **Complete and Production-Ready**

- Total Files: 44
- Documentation: 50KB+ across 9 guides
- Security: 0 vulnerabilities (CodeQL verified)
- Code Quality: Reviewed and validated
- Compatibility: Android 5.0+ (API 21-33+)

## 🤝 Contributing

Contributions are welcome! Feel free to submit issues or pull requests.

### How to Contribute
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 💬 Support

- **App Issues**: Open an issue on GitHub
- **TWRP Builder**: Visit [Hovatek Forum](https://www.hovatek.com/forum/)
- **Documentation**: Check the guides in this repository

## ⚠️ Disclaimer

This app only collects and displays device information. It does not root your device, modify system files, or make any changes to your device. Use the collected information at your own risk when building custom recovery images.

## 📱 Screenshots

The app features a clean, Material Design interface with:
- Auto-collection of device information on launch
- Scrollable information display
- One-tap save functionality
- Clear success/error messages

## 🔗 Related Links

- [Hovatek TWRP Builder](https://www.hovatek.com/twrpbuilder/)
- [TWRP Official Site](https://twrp.me/)
- [Android Developer Documentation](https://developer.android.com/)

---

**Made with ❤️ for the Android community**
