# TWRP Device Info Collector

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Ready-success.svg)]()
[![Security](https://img.shields.io/badge/Security-Passed-success.svg)](SECURITY_SUMMARY.md)

An Android application that automatically collects all device information needed for [Hovatek Online TWRP Builder](https://www.hovatek.com/twrpbuilder/).

## 🌟 Quick Links

### App Documentation
- 📖 **[Quick Start Guide](QUICK_START.md)** - For non-technical users
- 🔨 **[Build Instructions](BUILD_INSTRUCTIONS.md)** - Detailed build guide
- 📋 **[Features](FEATURES.md)** - Complete feature list
- 🔒 **[Security](SECURITY_SUMMARY.md)** - Security audit results
- 📄 **[Sample Output](SAMPLE_OUTPUT.txt)** - Example output file

### TWRP Building Documentation
- 📚 **[Complete TWRP Building Guide](docs/TWRP_BUILDING_GUIDE.md)** - Comprehensive guide
- 🔍 **[Device Info Extraction](docs/DEVICE_INFO_EXTRACTION.md)** - Extract device information
- ⚙️ **[Kernel Information Guide](docs/KERNEL_INFO.md)** - Kernel extraction and analysis
- 📝 **[Recovery Fstab Guide](docs/RECOVERY_FSTAB.md)** - Partition configuration
- 🌐 **[Hovatek Builder Guide](docs/HOVATEK_BUILDER_GUIDE.md)** - Using online TWRP builder
- 🔧 **[Troubleshooting Guide](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- 🛠️ **[Tools and Resources](docs/TOOLS_AND_RESOURCES.md)** - Essential tools
- ⚡ **[Quick Reference](docs/QUICK_REFERENCE.md)** - Command cheat sheet

## Features

- 📱 Automatically collects comprehensive device information
- 💾 Saves data to `sdcard/Download/twrp-builder-{codename}.txt`
- 🔐 **NEW**: Enhanced root and security status detection
- 🔍 Gathers all required information for TWRP Builder:
  - Device codename, brand, model, manufacturer
  - Android version and API level
  - Screen resolution
  - CPU architecture (ABI)
  - Kernel version
  - Build fingerprint
  - Hardware information
  - **Root access status**
  - **SELinux status**
  - **Bootloader lock status**
  - **System partition mount status**
  - And much more!

## What is this for?

This app helps you gather all the technical information about your Android device that is required when using the Hovatek Online TWRP Builder to create a custom TWRP recovery for your device.

## 📖 TWRP Building Resources

This repository now includes comprehensive documentation for building TWRP recovery:

### 📚 Complete Documentation Suite
- **[TWRP Building Guide](docs/TWRP_BUILDING_GUIDE.md)** - Complete guide to building TWRP from scratch
- **[Device Info Extraction](docs/DEVICE_INFO_EXTRACTION.md)** - How to extract all necessary device information
- **[Kernel Information](docs/KERNEL_INFO.md)** - Extract and analyze boot images and kernels
- **[Recovery Fstab](docs/RECOVERY_FSTAB.md)** - Configure partition mounting for TWRP
- **[Hovatek Builder Guide](docs/HOVATEK_BUILDER_GUIDE.md)** - Step-by-step guide for using the online builder
- **[Troubleshooting](docs/TROUBLESHOOTING.md)** - Solutions to common build and boot issues
- **[Tools & Resources](docs/TOOLS_AND_RESOURCES.md)** - Essential tools and community resources
- **[Quick Reference](docs/QUICK_REFERENCE.md)** - Command cheat sheet and quick lookup

### 🛠️ Automation Tools
- **[Device Info Script](scripts/extract_device_info.sh)** - Automated ADB-based device information extraction
- Run `./scripts/extract_device_info.sh` to automatically gather all device info via ADB

### 📂 Example Device Tree
- **[Sample Device Tree](examples/device_tree_sample/)** - Complete example with all required files
- Includes: `BoardConfig.mk`, `device.mk`, `recovery.fstab`, and more
- Fully commented to explain every configuration option

### 🚀 Getting Started with TWRP Building

1. **Collect Device Information**:
   - Use this app on your device, OR
   - Run `./scripts/extract_device_info.sh` with ADB

2. **Learn the Basics**:
   - Read [TWRP Building Guide](docs/TWRP_BUILDING_GUIDE.md)
   - Review [Device Info Extraction](docs/DEVICE_INFO_EXTRACTION.md)

3. **Choose Your Approach**:
   - **Online Building**: Follow [Hovatek Builder Guide](docs/HOVATEK_BUILDER_GUIDE.md)
   - **Local Building**: Follow the complete [TWRP Building Guide](docs/TWRP_BUILDING_GUIDE.md)

4. **Use Example Files**:
   - Copy and customize [examples/device_tree_sample/](examples/device_tree_sample/)
   - See included README for detailed instructions

## How to Use

1. Install the APK on your Android device
2. Open the "TWRP Info Collector" app
3. The app will automatically collect your device information
4. Tap "Save to File" to save the information
5. Find the file at: `sdcard/Download/twrp-builder-{your-device-codename}.txt`
6. Use this information when building TWRP at https://www.hovatek.com/twrpbuilder/

## Building the APK

### Prerequisites

- Android SDK (API level 33 or higher)
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

# Or build the release APK (unsigned)
./gradlew assembleRelease

# The APK will be located at:
# app/build/outputs/apk/debug/app-debug.apk
# or
# app/build/outputs/apk/release/app-release-unsigned.apk
```

#### Option 2: Using Android Studio

1. Open Android Studio
2. Click "Open an Existing Project"
3. Navigate to and select the cloned repository folder
4. Wait for Gradle to sync
5. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
6. The APK will be in `app/build/outputs/apk/`

### Building a Signed Release APK

To create a production-ready signed APK:

#### Step 1: Generate a Keystore

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

This will prompt you for:
- Keystore password
- Key password
- Your name and organization details

**Important**: Keep your keystore file and passwords safe! You'll need them for all future updates.

#### Step 2: Configure the Keystore

1. Place your `release-keystore.jks` file in the `app/` directory
2. Choose one of the following configuration methods:

**Option A: Using gradle.properties (Recommended)**

Create or edit `gradle.properties` in the project root and add:

```properties
KEYSTORE_FILE=release-keystore.jks
KEYSTORE_PASSWORD=your-store-password
KEY_ALIAS=your-key-alias
KEY_PASSWORD=your-key-password
```

Add `gradle.properties` to `.gitignore` to prevent committing credentials!

**Option B: Using Environment Variables**

Set the following environment variables before building:

```bash
export KEYSTORE_FILE=release-keystore.jks
export KEYSTORE_PASSWORD=your-store-password
export KEY_ALIAS=your-key-alias
export KEY_PASSWORD=your-key-password
```

The build.gradle is already configured to use these environment variables or gradle.properties automatically.

**Option C: Direct Modification (Not Recommended)**

If you prefer, you can directly modify `app/build.gradle`, but be careful not to commit real credentials:

```gradle
signingConfigs {
    release {
        storeFile file("release-keystore.jks")
        storePassword "your-store-password"
        keyAlias "your-key-alias"
        keyPassword "your-key-password"
    }
}
```

**Security Warning**: Never commit real keystore credentials to source control! Always use gradle.properties (with .gitignore) or environment variables for production builds.

#### Step 3: Enable Signing in Build Configuration

In `app/build.gradle`, uncomment the signing configuration line:

```gradle
buildTypes {
    release {
        signingConfig signingConfigs.release  // Uncomment this line
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

#### Step 4: Build the Signed Release APK

```bash
./gradlew assembleRelease
```

The signed APK will be at: `app/build/outputs/apk/release/app-release.apk`

#### ProGuard Configuration

The release build uses ProGuard to:
- Shrink the APK size by removing unused code
- Obfuscate code for security
- Optimize bytecode for better performance

ProGuard rules are configured in `app/proguard-rules.pro`. The default configuration:
- Keeps all app classes and methods
- Preserves AndroidX and Material Design components
- Removes debug logging statements
- Optimizes the final APK

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
- **Touch Driver Info**: Touch driver detection from multiple sources
- **Root & Security Status** *(NEW)*:
  - Root availability (su binary detection)
  - Root access granted status
  - SELinux status (Enforcing/Permissive)
  - Bootloader lock status
  - System partition mount status (read-only/read-write)
- **Build Info**: Fingerprint, Display, Bootloader, Radio Version
- **Additional Properties**: Host, User, Build Time

### Root Detection Details

The app performs comprehensive root and security checks:

1. **Root Available**: Checks for `su` binary in common system paths
2. **Root Access Granted**: Attempts to execute a root command to verify actual root access
3. **SELinux Status**: Reads SELinux enforcement status from system files
4. **Bootloader Status**: Detects if the bootloader is locked or unlocked via system properties
5. **System Partition Status**: Checks if the system partition is mounted as read-only or read-write

These checks help TWRP builders understand the security state of the device, which can be important for recovery development.

## Permissions

The app requires storage permissions to save the device information file:

- `WRITE_EXTERNAL_STORAGE` (Android 9 and below)
- Scoped storage access (Android 10+)

## Compatibility

- Minimum Android Version: 5.0 (Lollipop, API 21)
- Target Android Version: 13 (API 33)
- Supports all Android architectures (ARM, ARM64, x86, x86_64)

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
