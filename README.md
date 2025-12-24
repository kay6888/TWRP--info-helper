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


## How to Use

1. Install the APK on your Android device
2. Open the "TWRP Info Collector" app
3. The app will automatically collect your device information
4. Tap "Save to File" to save the information
5. Find the file at: `sdcard/Download/twrp-builder-{your-device-codename}.txt`
6. Use this information when building TWRP at https://www.hovatek.com/twrpbuilder/

Links
Reddit -https://www.reddit.com/r/TWRPinfohelper/comments/1puj0by/my_app_for_testing/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button

## 🔗 Related Links

- [Hovatek TWRP Builder](https://www.hovatek.com/twrpbuilder/)
- [TWRP Official Site](https://twrp.me/)
- [Android Developer Documentation](https://developer.android.com/)

---

**Made with ❤️ for the Android community**
