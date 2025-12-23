# Device Information Extraction Guide

## Table of Contents
- [Introduction](#introduction)
- [Required Information](#required-information)
- [Using ADB Commands](#using-adb-commands)
- [Alternative Methods](#alternative-methods)
- [Automated Extraction](#automated-extraction)
- [Understanding the Output](#understanding-the-output)

## Introduction

Before building TWRP, you need to collect comprehensive information about your Android device. This guide shows you multiple methods to extract all necessary device information.

## Required Information

You need to gather the following information:

| Information | Purpose | How to Get |
|------------|---------|------------|
| Device Codename | Unique device identifier | `ro.product.device` |
| Manufacturer | Device manufacturer | `ro.product.manufacturer` |
| Brand | Device brand | `ro.product.brand` |
| Model | Device model name | `ro.product.model` |
| Architecture | CPU architecture (arm, arm64, etc.) | `ro.product.cpu.abi` |
| Platform | Chipset/SoC platform | `ro.board.platform` |
| Android Version | OS version | `ro.build.version.release` |
| API Level | Android API level | `ro.build.version.sdk` |
| Kernel Version | Linux kernel version | `/proc/version` |
| Bootloader Version | Bootloader version | `ro.bootloader` |
| Screen Resolution | Display resolution | `wm size` |
| Partition Layout | Storage partitions | `/proc/partitions` |

## Using ADB Commands

### Prerequisites

1. **Enable USB Debugging**:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times to enable Developer Options
   - Go to Settings → Developer Options
   - Enable "USB Debugging"

2. **Install ADB**:
   ```bash
   # Ubuntu/Debian
   sudo apt install adb
   
   # Mac (using Homebrew)
   brew install android-platform-tools
   
   # Windows - Download from Android SDK Platform Tools
   ```

3. **Connect Device**:
   ```bash
   # Verify connection
   adb devices
   
   # Should show: [device-serial-number]    device
   ```

### Essential Commands

#### Basic Device Information

```bash
# Device Codename (most important!)
adb shell getprop ro.product.device

# Manufacturer
adb shell getprop ro.product.manufacturer

# Brand
adb shell getprop ro.product.brand

# Model
adb shell getprop ro.product.model

# Board name
adb shell getprop ro.product.board
```

#### Architecture and Platform

```bash
# Primary ABI (arm64-v8a, armeabi-v7a, etc.)
adb shell getprop ro.product.cpu.abi

# Secondary ABI (if any)
adb shell getprop ro.product.cpu.abi2

# All supported ABIs
adb shell getprop ro.product.cpu.abilist

# Platform/Chipset (e.g., msm8996, exynos9810)
adb shell getprop ro.board.platform

# Hardware name
adb shell getprop ro.hardware
```

#### Android Version Information

```bash
# Android version (e.g., 11, 12)
adb shell getprop ro.build.version.release

# API Level (e.g., 30, 31)
adb shell getprop ro.build.version.sdk

# Build ID
adb shell getprop ro.build.id

# Build fingerprint (full build identifier)
adb shell getprop ro.build.fingerprint
```

#### Kernel Information

```bash
# Kernel version
adb shell cat /proc/version

# Kernel command line (important for TWRP)
adb shell cat /proc/cmdline

# CPU information
adb shell cat /proc/cpuinfo
```

#### Display Information

```bash
# Screen resolution
adb shell wm size

# Screen density
adb shell wm density

# Display info (alternative)
adb shell dumpsys display | grep mBaseDisplayInfo
```

#### Partition Information

```bash
# List all partitions
adb shell cat /proc/partitions

# List block devices with names
adb shell ls -la /dev/block/platform/*/by-name/

# Or (depends on device)
adb shell ls -la /dev/block/bootdevice/by-name/

# Get partition sizes
adb shell df -h

# Detailed partition info
adb shell cat /proc/mounts
```

#### Fstab Information

```bash
# Recovery fstab (if available)
adb shell cat /etc/recovery.fstab

# TWRP fstab (if TWRP already installed)
adb shell cat /etc/twrp.fstab

# System fstab
adb shell cat /system/etc/fstab*

# Vendor fstab (Android 8+)
adb shell cat /vendor/etc/fstab*
```

#### Bootloader and Radio

```bash
# Bootloader version
adb shell getprop ro.bootloader

# Radio/baseband version
adb shell getprop ro.baseband

# Serial number (may not work on newer Android versions)
adb shell getprop ro.serialno
```

### Complete Property Dump

Get ALL system properties:

```bash
# Save all properties to a file
adb shell getprop > device_properties.txt

# Or view in terminal
adb shell getprop
```

### Kernel and Boot Image Extraction

#### Extract Boot Image

```bash
# Find boot partition
adb shell ls -la /dev/block/platform/*/by-name/ | grep boot

# Pull boot image (requires root)
adb shell
su
dd if=/dev/block/[boot-partition] of=/sdcard/boot.img
exit
exit
adb pull /sdcard/boot.img

# Or using Termux app without root (on some devices)
cat /proc/self/mounts | grep boot
```

#### Extract Recovery Image

```bash
# Find recovery partition
adb shell ls -la /dev/block/platform/*/by-name/ | grep recovery

# Pull recovery image (requires root)
adb shell
su
dd if=/dev/block/[recovery-partition] of=/sdcard/recovery.img
exit
exit
adb pull /sdcard/recovery.img
```

## Alternative Methods

### Method 1: Using Device Info Apps

Install apps from Play Store:
- **AIDA64** - Comprehensive device information
- **CPU-Z** - CPU and device details
- **DevCheck** - Hardware and system information
- **Termux** - Terminal emulator for Android

### Method 2: Using Termux (No PC Required)

Install Termux from F-Droid or Play Store:

```bash
# In Termux app
getprop ro.product.device
getprop ro.board.platform
cat /proc/version
cat /proc/partitions
```

### Method 3: Recovery Mode

Boot into existing recovery:

```bash
# From ADB
adb reboot recovery

# Then from recovery menu or console
cat /proc/partitions
cat /etc/recovery.fstab
```

### Method 4: Fastboot Mode

Boot into fastboot:

```bash
adb reboot bootloader

# Then on PC
fastboot getvar all
fastboot oem device-info
```

## Automated Extraction

### Using the Provided Script

We provide a comprehensive script that automates all ADB extractions:

```bash
# Make script executable
chmod +x scripts/extract_device_info.sh

# Run the script
./scripts/extract_device_info.sh

# Output will be saved to: device_info_[codename].txt
```

See [scripts/README.md](../scripts/README.md) for details.

### Manual Automation Script

Create your own quick extraction script:

```bash
#!/bin/bash

echo "=== TWRP Device Information ==="
echo ""

echo "Device Codename: $(adb shell getprop ro.product.device)"
echo "Manufacturer: $(adb shell getprop ro.product.manufacturer)"
echo "Brand: $(adb shell getprop ro.product.brand)"
echo "Model: $(adb shell getprop ro.product.model)"
echo "Board: $(adb shell getprop ro.product.board)"
echo ""

echo "Android Version: $(adb shell getprop ro.build.version.release)"
echo "API Level: $(adb shell getprop ro.build.version.sdk)"
echo ""

echo "Architecture: $(adb shell getprop ro.product.cpu.abi)"
echo "Platform: $(adb shell getprop ro.board.platform)"
echo "Hardware: $(adb shell getprop ro.hardware)"
echo ""

echo "Kernel Version:"
adb shell cat /proc/version
echo ""

echo "Bootloader: $(adb shell getprop ro.bootloader)"
echo "Baseband: $(adb shell getprop ro.baseband)"
echo ""

echo "Screen Resolution:"
adb shell wm size
echo ""

echo "=== Partitions ==="
adb shell cat /proc/partitions
```

## Understanding the Output

### Interpreting Architecture

- **arm64-v8a**: 64-bit ARM (most modern devices)
- **armeabi-v7a**: 32-bit ARM (older devices)
- **x86_64**: 64-bit x86 (rare, some tablets)
- **x86**: 32-bit x86 (very rare)

### Interpreting Platform

Common platforms:
- **msm8996, msm8998, sm8150**: Qualcomm Snapdragon
- **exynos7885, exynos9810**: Samsung Exynos
- **mt6765, mt6771, mt6785**: MediaTek
- **kirin970, kirin980**: Huawei Kirin

### Partition Naming

Common partition names:
- **boot**: Kernel and ramdisk
- **recovery**: Recovery image
- **system**: Android system
- **vendor**: Vendor files (Android 8+)
- **userdata**: User data
- **cache**: System cache
- **persist**: Persistent data

### Calculating Partition Sizes

From `/proc/partitions`, blocks are usually 1024 bytes:

```
Blocks: 524288
Size in bytes: 524288 * 1024 = 536870912 bytes
Size in MB: 536870912 / 1048576 = 512 MB
```

Use this for `BOARD_*_PARTITION_SIZE` in BoardConfig.mk.

## Quick Reference Commands

### One-Line Device Summary

```bash
adb shell "echo Codename: \$(getprop ro.product.device); echo Model: \$(getprop ro.product.model); echo Android: \$(getprop ro.build.version.release); echo Arch: \$(getprop ro.product.cpu.abi); echo Platform: \$(getprop ro.board.platform)"
```

### Save Everything to File

```bash
{
  echo "=== DEVICE INFORMATION ==="
  echo "Codename: $(adb shell getprop ro.product.device)"
  echo "Model: $(adb shell getprop ro.product.model)"
  echo "Manufacturer: $(adb shell getprop ro.product.manufacturer)"
  echo "Architecture: $(adb shell getprop ro.product.cpu.abi)"
  echo "Platform: $(adb shell getprop ro.board.platform)"
  echo "Android: $(adb shell getprop ro.build.version.release)"
  echo ""
  echo "=== ALL PROPERTIES ==="
  adb shell getprop
  echo ""
  echo "=== PARTITIONS ==="
  adb shell cat /proc/partitions
  echo ""
  echo "=== KERNEL ==="
  adb shell cat /proc/version
} > complete_device_info.txt
```

## Troubleshooting

### "Device Unauthorized"

```bash
# Revoke and re-authorize
adb kill-server
adb start-server
adb devices
# Accept authorization prompt on device
```

### "Permission Denied" for /proc or /sys Files

```bash
# Try with su (requires root)
adb shell su -c "cat /proc/partitions"

# Or use app like Termux with root
```

### No ADB Access

Use alternative methods:
- Install device info app
- Use Termux app
- Boot to recovery mode
- Check fastboot mode

## Next Steps

After collecting device information:

1. Document findings in a text file
2. Compare with similar devices on XDA
3. Proceed to [TWRP_BUILDING_GUIDE.md](TWRP_BUILDING_GUIDE.md)
4. Configure your device tree files

## Resources

- [ADB Documentation](https://developer.android.com/studio/command-line/adb)
- [Android Build Properties](https://source.android.com/docs/core/architecture)
- [XDA Forums](https://forum.xda-developers.com/)

---

**Tip**: Always save all device information before modifying your device. You might need it for recovery!
