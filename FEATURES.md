# TWRP Device Info Collector - Feature Documentation

## Overview

The TWRP Device Info Collector is a comprehensive Android application designed to automatically gather all necessary device information required for building custom TWRP (Team Win Recovery Project) recovery images using the [Hovatek Online TWRP Builder](https://www.hovatek.com/twrpbuilder/).

## Key Features

### 1. Automatic Device Information Collection

The app automatically collects the following information when launched:

#### Basic Device Information
- **Device Codename**: Internal device name (e.g., "OnePlus6", "beryllium")
- **Product Name**: Product designation
- **Brand**: Device brand (e.g., Samsung, OnePlus, Xiaomi)
- **Model**: User-facing model name (e.g., "Galaxy S20", "Pixel 6")
- **Manufacturer**: Device manufacturer
- **Board**: Hardware board name
- **Hardware**: Hardware platform identifier

#### Android Version Details
- **Android Version**: User-facing version (e.g., 13, 12, 11)
- **API Level**: SDK version number
- **Build ID**: Specific build identifier
- **Build Type**: Debug, user, or userdebug
- **Build Tags**: Build tags and flags
- **Incremental**: Incremental build version

#### Architecture Information
- **Supported ABIs**: All supported CPU architectures
- **Primary ABI**: Main CPU architecture (arm64-v8a, armeabi-v7a, x86, x86_64)
- **System Architecture**: OS architecture details

#### Screen Information (Critical for TWRP)
- **Screen Resolution**: Exact pixel dimensions (e.g., 1080x2400)
- **Screen Density**: DPI value
- **Density Scale**: Scale factor

#### Kernel Information
- **Kernel Version**: Linux kernel version
- **Detailed Kernel Info**: Full kernel string with build date

#### Build Fingerprint
- **Complete Build Fingerprint**: Unique device build identifier
- **Display String**: Human-readable build description

#### Additional Properties
- **Bootloader Version**: Bootloader version string
- **Radio Version**: Baseband/radio firmware version
- **Build Host**: Server where build was compiled
- **Build User**: User who compiled the build
- **Build Time**: Timestamp of when build was created

### 2. TWRP Builder Integration

The app formats all information specifically for use with Hovatek TWRP Builder:

- **Required Fields Highlighted**: Image type, Android version, screen resolution
- **Device Codename**: Clearly displayed for file naming
- **Architecture**: For selecting correct binary
- **Step-by-Step Instructions**: Included in the output file

### 3. File Export Functionality

#### Save Location
- **Path**: `sdcard/Download/twrp-builder-{codename}.txt`
- **Format**: Plain text file, easily viewable on any device
- **Naming**: Automatically named with device codename

#### File Content Structure
```
=== TWRP BUILDER DEVICE INFORMATION ===
[Basic device info section]
[Android version section]
[Architecture section]
[Screen information section]
[Kernel information section]
[Build fingerprint section]
[Additional properties section]

=== TWRP BUILDER REQUIREMENTS ===
[Key required fields for TWRP Builder]

=== INSTRUCTIONS ===
[Step-by-step guide for using the info]
```

### 4. User Interface

#### Clean, Intuitive Design
- **Title Banner**: Clear app identification
- **Description**: Explains the app's purpose
- **Action Buttons**:
  - "Collect Info": Re-collect device information
  - "Save to File": Export to text file

#### Information Display
- **Scrollable Text View**: View all collected information
- **Monospace Font**: Easy to read technical details
- **Selectable Text**: Copy specific values if needed

#### Status Messages
- **Toast Notifications**: Immediate feedback on actions
- **Success Messages**: Confirmation of save location
- **Error Handling**: Clear error messages if save fails

### 5. Permissions Handling

#### Smart Permission Management
- **Android 11+**: Uses scoped storage (no permissions needed)
- **Android 6-10**: Requests WRITE_EXTERNAL_STORAGE when needed
- **Android 5 and below**: Automatic permission grant

#### User-Friendly Permission Flow
- Permissions requested only when saving
- Clear explanation of why permission is needed
- Graceful handling of permission denial

### 6. Compatibility

#### Wide Device Support
- **Minimum Android Version**: 5.0 (Lollipop, API 21)
- **Target Android Version**: 13 (API 33)
- **Maximum Compatibility**: Works on Android 5.0 through 14+

#### Architecture Support
- ARM (32-bit)
- ARM64 (64-bit)
- x86 (32-bit)
- x86_64 (64-bit)

### 7. Data Accuracy

#### Direct System API Access
- Uses official Android Build class
- Reads from system properties
- Executes system commands for kernel info
- Real-time display metrics reading

#### No Root Required
- All information gathered using standard Android APIs
- No special permissions beyond storage
- Works on locked bootloader devices

### 8. Privacy & Security

#### Local Data Only
- All information stays on device
- No network requests
- No data transmission to external servers
- User controls when to save

#### Open Source
- Complete source code available
- Transparent data collection
- Auditable by users

## Use Cases

### Primary Use Case: TWRP Recovery Building
1. User wants to build TWRP for their device
2. Install this app to gather required information
3. Save the information to a file
4. Use the data when filling out Hovatek TWRP Builder form
5. Upload appropriate image files and build TWRP

### Secondary Use Cases

#### Device Information Reference
- Keep a record of device specifications
- Compare information across different ROMs
- Technical support and troubleshooting
- Custom ROM development

#### Warranty and RMA
- Document device information before warranty claims
- Record original device state
- Verify device authenticity

## Technical Implementation

### Technologies Used
- **Language**: Java
- **UI Framework**: Android SDK (AndroidX)
- **Build System**: Gradle
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 33 (Android 13)

### Key Classes and APIs
- `android.os.Build`: Device and build information
- `android.view.Display`: Screen metrics
- `android.util.DisplayMetrics`: Display properties
- `java.io.File`: File operations
- `android.os.Environment`: Storage directories

### Data Collection Methods
1. **Static Fields**: `Build.MODEL`, `Build.BRAND`, etc.
2. **System Properties**: `System.getProperty()`
3. **Runtime Commands**: `Runtime.exec("uname -a")`
4. **Display Manager**: `WindowManager.getDefaultDisplay()`

## Future Enhancement Possibilities

### Potential Features
- [ ] Share information via email/messaging
- [ ] Compare with other devices
- [ ] Export to JSON format
- [ ] QR code generation for quick info transfer
- [ ] Automatic TWRP Builder form filling (via web integration)
- [ ] Device compatibility checker
- [ ] Recovery image validator
- [ ] Multi-language support

### Advanced Features
- [ ] Root detection and additional info if rooted
- [ ] Partition information (if available)
- [ ] Device tree information
- [ ] SELinux status
- [ ] Treble support detection

## Comparison with Manual Collection

### Manual Method (Without App)
- Requires ADB setup on computer
- Multiple commands needed:
  ```
  adb shell getprop ro.product.device
  adb shell getprop ro.product.model
  adb shell getprop ro.build.version.release
  adb shell wm size
  adb shell getprop ro.product.cpu.abi
  ```
- Must format information manually
- Easy to miss required fields
- Time-consuming

### With This App
- ✅ One-tap information collection
- ✅ All fields automatically gathered
- ✅ Pre-formatted for TWRP Builder
- ✅ Saved locally on device
- ✅ No computer required
- ✅ No technical knowledge needed

## Benefits

1. **Time-Saving**: Collect all info in seconds vs. minutes of manual work
2. **Accuracy**: Eliminates human error in transcription
3. **Completeness**: Ensures no required fields are missed
4. **Convenience**: No need for computer or ADB
5. **Accessibility**: Anyone can use it, no technical expertise required
6. **Reliability**: Direct API access ensures correct information
7. **Portability**: Information saved on device, accessible anywhere

## Why This Matters for TWRP Building

### Critical Information
The Hovatek TWRP Builder needs specific information to:

1. **Screen Resolution**: Configure touch drivers correctly
2. **Device Codename**: Identify correct device configuration
3. **Android Version**: Determine TWRP version compatibility
4. **Architecture**: Select correct binary compilation target
5. **Kernel Info**: Ensure kernel compatibility

### Common Mistakes Prevented
- Wrong screen resolution → touch doesn't work in TWRP
- Wrong codename → build fails or wrong device targeted
- Wrong architecture → TWRP won't boot
- Missing information → incomplete build

This app eliminates these issues by providing 100% accurate information directly from the device.

## Conclusion

The TWRP Device Info Collector is a comprehensive, user-friendly solution for gathering all necessary device information for TWRP recovery building. It eliminates the complexity and potential errors of manual information collection, making the TWRP building process accessible to users of all technical levels.
