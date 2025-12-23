# Hovatek Online TWRP Builder Guide

## Table of Contents
- [Introduction](#introduction)
- [What is Hovatek TWRP Builder](#what-is-hovatek-twrp-builder)
- [Prerequisites](#prerequisites)
- [Preparing Your Device Information](#preparing-your-device-information)
- [Using the Hovatek Builder](#using-the-hovatek-builder)
- [Understanding the Form Fields](#understanding-the-form-fields)
- [Uploading Files](#uploading-files)
- [Build Process](#build-process)
- [Common Errors and Solutions](#common-errors-and-solutions)
- [Tips for Success](#tips-for-success)

## Introduction

Hovatek Online TWRP Builder is a web-based service that builds custom TWRP recovery images for Android devices without requiring you to set up a local build environment. This guide walks you through using the service effectively.

## What is Hovatek TWRP Builder

**Website**: https://www.hovatek.com/twrpbuilder/

Hovatek TWRP Builder is:
- ✅ Online service (no local build environment needed)
- ✅ Free to use
- ✅ Supports most Android devices
- ✅ Faster than local builds
- ✅ Maintained and updated regularly

**Advantages**:
- No need for powerful computer
- No 100GB+ disk space requirement
- No complicated build environment setup
- Faster build times
- Expert support from Hovatek community

**Limitations**:
- Requires accurate device information
- May not support all devices
- Depends on web service availability
- Less control than local builds

## Prerequisites

Before using Hovatek Builder, gather:

1. **Device Information** - See [DEVICE_INFO_EXTRACTION.md](DEVICE_INFO_EXTRACTION.md)
2. **Boot/Recovery Image** - Stock boot.img or recovery.img
3. **Device Tree** (optional but recommended) - For advanced builds

### Essential Information Checklist

- [ ] Device codename
- [ ] Device brand/manufacturer
- [ ] Device model
- [ ] Android version
- [ ] Architecture (arm, arm64, etc.)
- [ ] Platform/chipset
- [ ] Screen resolution
- [ ] Partition sizes
- [ ] Kernel command line
- [ ] Kernel base address and offsets

## Preparing Your Device Information

### Step 1: Use TWRP Device Info Collector App

If you have the TWRP Device Info Collector app (from this repository):

1. Install and run the app on your device
2. Save the output file
3. Review the file - it contains most information you need
4. Note the key values for the web form

### Step 2: Manual Collection via ADB

```bash
# Run the extraction script
./scripts/extract_device_info.sh

# Or manually gather info
adb shell getprop ro.product.device        # Codename
adb shell getprop ro.product.manufacturer  # Manufacturer
adb shell getprop ro.product.model         # Model
adb shell getprop ro.product.cpu.abi       # Architecture
adb shell getprop ro.board.platform        # Platform
adb shell getprop ro.build.version.release # Android version
adb shell wm size                          # Screen resolution
```

### Step 3: Extract Boot Image

See [KERNEL_INFO.md](KERNEL_INFO.md) for detailed instructions.

```bash
# Find and extract boot partition
adb shell su -c "dd if=/dev/block/bootdevice/by-name/boot of=/sdcard/boot.img"
adb pull /sdcard/boot.img

# Analyze boot image
abootimg -i boot.img
```

## Using the Hovatek Builder

### Access the Service

1. Navigate to: https://www.hovatek.com/twrpbuilder/
2. Read the instructions on the page
3. Have all your information ready

### Account Setup (If Required)

Some versions may require registration:
1. Create account (if needed)
2. Verify email
3. Log in

## Understanding the Form Fields

### Basic Device Information

#### Device Codename
```
Field: Device Codename / Device Name
Value: Your device's codename (e.g., "hero", "klte", "lavender")
Where to find: adb shell getprop ro.product.device
Example: hero
```

#### Manufacturer
```
Field: Manufacturer
Value: Device manufacturer
Where to find: adb shell getprop ro.product.manufacturer
Example: Samsung, Xiaomi, OnePlus
```

#### Device Model
```
Field: Device Model
Value: Human-readable model name
Where to find: adb shell getprop ro.product.model
Example: Galaxy S7, Redmi Note 7, OnePlus 6
```

### System Information

#### Android Version
```
Field: Android Version
Value: Major Android version number
Where to find: adb shell getprop ro.build.version.release
Example: 9, 10, 11, 12, 13
```

#### Architecture
```
Field: Architecture / CPU Type
Value: CPU architecture
Where to find: adb shell getprop ro.product.cpu.abi
Options:
  - arm64-v8a (most modern devices)
  - armeabi-v7a (older 32-bit devices)
  - x86_64 (rare)
  - x86 (very rare)
```

#### Platform
```
Field: Platform / Chipset
Value: SoC platform identifier
Where to find: adb shell getprop ro.board.platform
Examples:
  - msm8996 (Snapdragon 820)
  - msm8998 (Snapdragon 835)
  - sm8150 (Snapdragon 855)
  - exynos9810 (Samsung Exynos)
  - mt6765 (MediaTek)
```

### Display Information

#### Screen Resolution
```
Field: Screen Resolution
Value: Width x Height in pixels
Where to find: adb shell wm size
Format: 1080x1920, 1440x2560, 1080x2340
Example: 1080x2340

Note: Enter physical resolution, not scaled resolution
```

### Kernel Configuration

#### Kernel Base Address
```
Field: Kernel Base
Value: Memory address where kernel loads
Where to find: abootimg -i boot.img | grep "kernel addr"
Example: 0x80000000
Common: 0x80000000, 0x10000000, 0x40000000
```

#### Page Size
```
Field: Page Size
Value: Kernel page size in bytes
Where to find: abootimg -i boot.img | grep "page size"
Example: 2048 or 4096
Common: 2048 (older), 4096 (newer)
```

#### Kernel Command Line
```
Field: Kernel Command Line / Kernel cmdline
Value: Boot parameters passed to kernel
Where to find: abootimg -i boot.img | grep "cmdline"
Example: console=ttyMSM0,115200,n8 androidboot.hardware=qcom androidboot.selinux=permissive

Tip: Copy exact command line from stock boot.img
```

#### Kernel Offsets
```
Fields: Kernel Offset, Ramdisk Offset, Tags Offset, Second Offset
Where to find: abootimg -i boot.img

Examples:
  Kernel Offset: 0x00008000
  Ramdisk Offset: 0x01000000
  Tags Offset: 0x00000100
  Second Offset: 0x00f00000
```

### Recovery Configuration

#### Recovery Partition Size
```
Field: Recovery Partition Size
Value: Size in bytes
Where to find: adb shell cat /proc/partitions (look for recovery)
Example: 67108864 (64MB)

How to calculate:
  Blocks × 1024 = Size in bytes
  65536 × 1024 = 67108864 bytes
```

#### TWRP Theme
```
Field: TWRP Theme
Value: UI theme based on screen size/orientation
Options:
  - portrait_hdpi (1080p portrait)
  - portrait_mdpi (720p portrait)
  - landscape_hdpi (1080p landscape)
  - landscape_mdpi (720p landscape)
  - watch_mdpi (smartwatches)

Choose based on:
  - Screen resolution (720p=mdpi, 1080p+=hdpi)
  - Orientation (most phones are portrait)
```

### Storage Configuration

#### Has SD Card Slot
```
Field: Has SD Card / External SD Card
Value: Yes/No
Physical microSD card slot present?
```

#### Internal Storage Path
```
Field: Internal Storage Path
Value: Mount point for internal storage
Default: /data/media/0
Alternative: /data/media (some devices)
```

### Encryption

#### Encryption Type
```
Field: Encryption / Supports Encryption
Options:
  - None (no encryption)
  - FDE (Full Disk Encryption - Android 4.4-6.0)
  - FBE (File-Based Encryption - Android 7+)

Where to check:
  Settings → Security → Encryption status
  Or: adb shell getprop ro.crypto.type
```

## Uploading Files

### Boot/Recovery Image

```
Field: Boot Image / Recovery Image Upload
File: boot.img or recovery.img from your device

Requirements:
  - Must be from your exact device
  - Must match your current firmware
  - File size usually 10-60MB
  - Format: .img file
```

### Device Tree (Advanced)

```
Field: Device Tree Upload
File: device_tree.zip

If providing custom device tree:
  1. Create device tree structure
  2. Include all required files
  3. Compress as .zip
  4. Upload

See: examples/device_tree_sample/ for structure
```

## Build Process

### Submitting Your Build

1. **Fill all required fields**
   - Double-check all values
   - Ensure accuracy

2. **Upload necessary files**
   - boot.img or recovery.img
   - device tree (if applicable)

3. **Submit build request**
   - Click "Build" or "Submit"
   - Note your build ID

4. **Wait for build**
   - Builds typically take 10-30 minutes
   - Check email for notifications
   - Or refresh build status page

### Build Status

Monitor your build:
- **Queued**: Waiting to start
- **Building**: Currently compiling
- **Success**: Build completed
- **Failed**: Build had errors

### Downloading Your Recovery

When build succeeds:
1. Download recovery.img
2. Download md5sum (for verification)
3. Save both files

Verify download:
```bash
md5sum recovery.img
# Compare with provided md5sum
```

## Common Errors and Solutions

### "Build Failed - Invalid Kernel Parameters"

**Cause**: Incorrect kernel base, offsets, or cmdline

**Solution**:
1. Re-extract boot.img
2. Use abootimg or AIK to get exact values
3. Verify all offsets
4. Resubmit with correct values

### "Build Failed - Partition Size Mismatch"

**Cause**: Recovery partition size too large or incorrect

**Solution**:
1. Check actual partition size: `adb shell cat /proc/partitions`
2. Calculate correctly: blocks × 1024
3. Ensure recovery won't exceed partition size

### "Build Failed - Invalid Architecture"

**Cause**: Wrong CPU architecture specified

**Solution**:
1. Verify: `adb shell getprop ro.product.cpu.abi`
2. Match exactly (arm64-v8a, not just arm64)

### "Build Failed - Missing Dependencies"

**Cause**: Incomplete device information or files

**Solution**:
1. Ensure boot.img is uploaded
2. Check all required fields are filled
3. Verify file isn't corrupted

### "Recovery Boots But Touch Doesn't Work"

**Cause**: Wrong TWRP theme or display configuration

**Solution**:
1. Try different theme (hdpi ↔ mdpi)
2. Verify screen resolution is correct
3. May need custom device tree with touch drivers

### "Recovery Boots But No Partitions Mount"

**Cause**: Incorrect fstab or partition paths

**Solution**:
1. Provide custom device tree with correct recovery.fstab
2. Check partition paths match your device
3. See [RECOVERY_FSTAB.md](RECOVERY_FSTAB.md)

### "Recovery Boots But Can't Decrypt Data"

**Cause**: Missing encryption support or wrong encryption type

**Solution**:
1. Specify correct encryption type (FDE vs FBE)
2. May need custom build with crypto libraries
3. Check if device uses metadata encryption (Android 10+)

## Tips for Success

### Before Submitting

✅ **Verify All Information**
- Cross-reference multiple sources
- Use tools like abootimg for accuracy
- Check similar devices if unsure

✅ **Use Stock Boot Image**
- Don't use modified boot.img
- Use boot.img from stock firmware
- Ensure it's from your current firmware version

✅ **Double-Check Values**
- Codename is critical - must be exact
- Architecture must match exactly
- Kernel parameters must be precise

### During Build

✅ **Be Patient**
- Builds can take 10-30+ minutes
- Don't submit multiple builds simultaneously
- Wait for completion notification

✅ **Check Email**
- Build notifications sent to registered email
- Check spam folder
- Save build ID for reference

### After Build

✅ **Test Safely**
- Backup your data first
- Test in fastboot boot mode before flashing:
  ```bash
  fastboot boot recovery.img
  ```
- If it works, then flash permanently

✅ **Verify Basic Functions**
- Touch screen works
- Can mount partitions
- Display brightness works
- MTP file transfer works

✅ **Report Issues**
- If build fails, note error messages
- Share device info with Hovatek support
- Check Hovatek forums for similar issues

## Alternative Options

If Hovatek Builder doesn't work:

1. **Try Local Build** - See [TWRP_BUILDING_GUIDE.md](TWRP_BUILDING_GUIDE.md)
2. **Use PBRP Builder** - PitchBlack Recovery Builder
3. **Request on XDA** - Ask in device forums
4. **Hire Developer** - Commission custom build

## Getting Help

### Hovatek Resources
- **Website**: https://www.hovatek.com/twrpbuilder/
- **Forum**: https://www.hovatek.com/forum/
- **Guides**: Hovatek has detailed tutorials

### Community Resources
- **XDA Forums**: Device-specific help
- **Telegram Groups**: TWRP build groups
- **Reddit**: r/AndroidQuestions, r/TWRP

### Providing Information When Asking for Help

Include:
1. Device model and codename
2. Android version
3. What you tried
4. Error messages (exact text)
5. Boot.img analysis output
6. Screenshots if applicable

## Complete Example Submission

```
Device Codename: hero
Manufacturer: Samsung
Model: Galaxy S7
Android Version: 8.0
Architecture: arm64-v8a
Platform: exynos8890
Screen Resolution: 1440x2560

Kernel Base: 0x10000000
Page Size: 2048
Kernel Offset: 0x00008000
Ramdisk Offset: 0x01000000
Tags Offset: 0x00000100
Second Offset: 0x00f00000

Kernel Cmdline: console=null androidboot.hardware=samsungexynos8890 androidboot.selinux=permissive

TWRP Theme: portrait_hdpi
Has SD Card: Yes
Encryption: FDE

Files:
- boot.img (uploaded)
```

## Resources

- [Hovatek TWRP Builder](https://www.hovatek.com/twrpbuilder/)
- [DEVICE_INFO_EXTRACTION.md](DEVICE_INFO_EXTRACTION.md)
- [KERNEL_INFO.md](KERNEL_INFO.md)
- [RECOVERY_FSTAB.md](RECOVERY_FSTAB.md)
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

**Remember**: Accurate device information is the key to successful TWRP builds. Take time to verify everything!
