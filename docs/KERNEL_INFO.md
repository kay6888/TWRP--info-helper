# Kernel Information Guide

## Table of Contents
- [Introduction](#introduction)
- [Why Kernel Information Matters](#why-kernel-information-matters)
- [Extracting Boot Image](#extracting-boot-image)
- [Analyzing Boot Image](#analyzing-boot-image)
- [Kernel Parameters](#kernel-parameters)
- [Prebuilt Kernel vs Source](#prebuilt-kernel-vs-source)
- [Tools](#tools)

## Introduction

The kernel is the core of the Android operating system. For TWRP to work correctly, you need accurate kernel configuration information including command line parameters, base address, offsets, and page size.

## Why Kernel Information Matters

TWRP needs kernel information to:
- Build recovery images with correct boot parameters
- Set proper memory addresses
- Configure kernel command line
- Ensure compatibility with bootloader
- Enable proper hardware initialization

## Extracting Boot Image

### Method 1: Using ADB (Requires Root)

```bash
# Find boot partition
adb shell su -c "ls -la /dev/block/platform/*/by-name/ | grep boot"

# Example output:
# lrwxrwxrwx 1 root root 16 1970-01-01 00:00 boot -> /dev/block/sde11

# Pull boot image
adb shell
su
dd if=/dev/block/sde11 of=/sdcard/boot.img
exit
exit
adb pull /sdcard/boot.img
```

### Method 2: Using Fastboot

```bash
# Boot to fastboot mode
adb reboot bootloader

# Some devices allow boot image extraction
fastboot getvar all > device_info.txt
fastboot boot twrp.img  # Boot temporary TWRP

# Then in TWRP
dd if=/dev/block/bootdevice/by-name/boot of=/sdcard/boot.img
```

### Method 3: From Firmware/ROM Package

```bash
# Many ROMs include boot.img in the ZIP
unzip firmware.zip
# Look for boot.img
```

### Method 4: Using Termux (No Root on Some Devices)

```bash
# Install Termux from F-Droid
# In Termux:
cat /proc/cmdline  # Shows kernel command line
cat /proc/version  # Shows kernel version
```

## Analyzing Boot Image

### Tool 1: abootimg

```bash
# Install abootimg
sudo apt install abootimg

# Extract boot image information
abootimg -i boot.img

# Example output:
# Android Boot Image Info:
# * file name = boot.img
# * image size = 67108864 bytes (64.00 MB)
# * page size = 2048 bytes
# * kernel size = 23456789 bytes (22.37 MB)
# * ramdisk size = 1234567 bytes (1.18 MB)
# * second stage size = 0 bytes
# * device tree size = 234567 bytes (229.07 KB)
# * kernel addr = 0x00008000
# * ramdisk addr = 0x01000000
# * second stage addr = 0x00f00000
# * tags addr = 0x00000100
# * page size = 2048
# * dt size = 234567
# * unused = 0
# * name = 
# * cmdline = console=ttyMSM0,115200,n8 androidboot.console=ttyMSM0...

# Extract kernel
abootimg -x boot.img
# Creates: bootimg.cfg, zImage, initrd.img
```

### Tool 2: Android Image Kitchen (AIK)

```bash
# Download Android Image Kitchen
wget https://github.com/osm0sis/Android-Image-Kitchen/archive/refs/heads/master.zip
unzip master.zip
cd Android-Image-Kitchen-master

# Unpack boot image
./unpackimg.sh boot.img

# View information
cat split_img/boot.img-board
cat split_img/boot.img-base
cat split_img/boot.img-pagesize
cat split_img/boot.img-cmdline
cat split_img/boot.img-kernel_offset
cat split_img/boot.img-ramdisk_offset
cat split_img/boot.img-second_offset
cat split_img/boot.img-tags_offset
cat split_img/boot.img-dtb_offset  # If present

# Repack (after modifications)
./repackimg.sh
```

### Tool 3: unmkbootimg

```bash
# Install unmkbootimg
git clone https://github.com/xiaolu/unmkbootimg
cd unmkbootimg
make
sudo make install

# Extract boot info
unmkbootimg -i boot.img

# Example output:
# unmkbootimg version 1.2 - Mikael Q Kuisma <kuisma@ping.se>
# File boot.img
# Type: Android bootimg
# Kernel size: 23456789 bytes
# Kernel address: 0x80008000
# Ramdisk size: 1234567 bytes  
# Ramdisk address: 0x81000000
# Secondary size: 0 bytes
# Secondary address: 0x80f00000
# Kernel tags address: 0x80000100
# Flash page size: 2048 bytes
# Board name: ""
# Command line: console=ttyMSM0,115200,n8 androidboot.console=ttyMSM0...
# Extracting kernel to file zImage ...
# Extracting root filesystem to file initramfs.cpio.gz ...
```

### Tool 4: Magisk Manager

If Magisk is installed:
1. Open Magisk Manager
2. Tap on Installed → Boot image location
3. Copy boot.img

## Kernel Parameters

### Base Address

The kernel base address is where the kernel is loaded in memory.

```bash
# Common base addresses:
# 0x80000000  - Most Qualcomm devices
# 0x10000000  - Some Samsung Exynos
# 0x40000000  - Some MediaTek
# 0x00000000  - Rare cases
```

Finding base address:
```bash
# From abootimg
abootimg -i boot.img | grep "kernel addr"
# Output: kernel addr = 0x00008000

# Actual base is usually this minus kernel offset
# If kernel addr = 0x80008000 and offset = 0x00008000
# Then base = 0x80000000
```

### Kernel Offset

```bash
# Common offsets:
BOARD_KERNEL_OFFSET := 0x00008000     # Most common
BOARD_KERNEL_OFFSET := 0x00080000     # Some devices
```

### Ramdisk Offset

```bash
# Common ramdisk offsets:
BOARD_RAMDISK_OFFSET := 0x01000000    # Very common
BOARD_RAMDISK_OFFSET := 0x02000000    # Some devices
BOARD_RAMDISK_OFFSET := 0x11000000    # Samsung Exynos
```

### Second Stage Offset

```bash
# Usually:
BOARD_SECOND_OFFSET := 0x00f00000
# Or not used (0x00000000)
```

### Tags Offset

```bash
# Usually:
BOARD_TAGS_OFFSET := 0x00000100
# Or:
BOARD_TAGS_OFFSET := 0x01e00000
```

### DTB Offset (Device Tree Blob)

```bash
# For devices with separate DTB:
BOARD_DTB_OFFSET := 0x01f00000
# Or included in kernel
```

### Page Size

```bash
# Common page sizes:
BOARD_KERNEL_PAGESIZE := 2048    # Most common
BOARD_KERNEL_PAGESIZE := 4096    # Newer devices
```

### Kernel Command Line

The kernel command line contains boot parameters:

```bash
# Example from Android device:
BOARD_KERNEL_CMDLINE := console=ttyMSM0,115200,n8
BOARD_KERNEL_CMDLINE += androidboot.console=ttyMSM0
BOARD_KERNEL_CMDLINE += androidboot.hardware=qcom
BOARD_KERNEL_CMDLINE += msm_rtb.filter=0x237
BOARD_KERNEL_CMDLINE += ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += androidboot.bootdevice=7464900.sdhci
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive
```

Common parameters:
- `console=` - Serial console for debugging
- `androidboot.hardware=` - Hardware platform name
- `androidboot.selinux=` - SELinux mode (enforcing/permissive)
- `androidboot.bootdevice=` - Boot device path
- `lpm_levels.sleep_disabled=1` - Disable sleep
- `buildvariant=` - Build variant (user/userdebug/eng)

## Calculating Values for BoardConfig.mk

### Example 1: From abootimg Output

```bash
$ abootimg -i boot.img
# kernel addr = 0x80008000
# ramdisk addr = 0x81000000
# second stage addr = 0x80f00000
# tags addr = 0x80000100
# page size = 2048
# cmdline = console=ttyMSM0,115200,n8 androidboot.hardware=qcom
```

Convert to BoardConfig.mk:
```makefile
# Base = kernel addr - 0x00008000
BOARD_KERNEL_BASE := 0x80000000

# Offsets
BOARD_KERNEL_OFFSET := 0x00008000
BOARD_RAMDISK_OFFSET := 0x01000000   # ramdisk addr - base
BOARD_SECOND_OFFSET := 0x00f00000    # second addr - base  
BOARD_TAGS_OFFSET := 0x00000100      # tags addr - base

# Page size
BOARD_KERNEL_PAGESIZE := 2048

# Command line
BOARD_KERNEL_CMDLINE := console=ttyMSM0,115200,n8 androidboot.hardware=qcom
```

### Example 2: From AIK Output

```bash
$ cat split_img/boot.img-base
0x80000000

$ cat split_img/boot.img-kernel_offset
0x00008000

$ cat split_img/boot.img-ramdisk_offset
0x01000000

$ cat split_img/boot.img-pagesize
2048

$ cat split_img/boot.img-cmdline
console=ttyHSL0,115200,n8 androidboot.hardware=qcom
```

Directly use these values in BoardConfig.mk.

## Prebuilt Kernel vs Source

### Using Prebuilt Kernel (Easier)

Extract kernel from boot.img:

```bash
# Using abootimg
abootimg -x boot.img
cp zImage device/manufacturer/codename/prebuilt/kernel

# Or using AIK
./unpackimg.sh boot.img
cp split_img/boot.img-zImage device/manufacturer/codename/prebuilt/kernel
```

In BoardConfig.mk:
```makefile
TARGET_PREBUILT_KERNEL := device/manufacturer/codename/prebuilt/kernel
```

In device.mk:
```makefile
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/prebuilt/kernel:kernel
```

### Building Kernel from Source (Advanced)

```makefile
# In BoardConfig.mk
TARGET_KERNEL_SOURCE := kernel/manufacturer/codename
TARGET_KERNEL_CONFIG := codename_defconfig
TARGET_KERNEL_ARCH := arm64
TARGET_KERNEL_HEADER_ARCH := arm64
```

Requires:
- Kernel source code
- Kernel configuration file (defconfig)
- Proper toolchain
- More build time

## Complete BoardConfig.mk Kernel Section

### Example Configuration

```makefile
# Kernel
TARGET_PREBUILT_KERNEL := device/samsung/hero/prebuilt/kernel

# Or for building from source:
# TARGET_KERNEL_SOURCE := kernel/samsung/hero
# TARGET_KERNEL_CONFIG := hero_defconfig

# Kernel Command Line
BOARD_KERNEL_CMDLINE := console=null androidboot.hardware=qcom
BOARD_KERNEL_CMDLINE += msm_rtb.filter=0x3F ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += androidboot.bootdevice=624000.ufshc
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive

# Kernel Base and Offsets
BOARD_KERNEL_BASE := 0x80000000
BOARD_KERNEL_OFFSET := 0x00008000
BOARD_RAMDISK_OFFSET := 0x02000000
BOARD_SECOND_OFFSET := 0x00f00000
BOARD_TAGS_OFFSET := 0x01e00000
BOARD_KERNEL_PAGESIZE := 4096

# DTB
BOARD_KERNEL_SEPARATED_DTBO := true
BOARD_INCLUDE_DTB_IN_BOOTIMG := true
BOARD_PREBUILT_DTBOIMAGE := device/samsung/hero/prebuilt/dtbo.img
BOARD_PREBUILT_DTBIMAGE_DIR := device/samsung/hero/prebuilt/dtb

# Boot Image Arguments
BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE)
BOARD_MKBOOTIMG_ARGS += --pagesize $(BOARD_KERNEL_PAGESIZE)
BOARD_MKBOOTIMG_ARGS += --kernel_offset $(BOARD_KERNEL_OFFSET)
BOARD_MKBOOTIMG_ARGS += --ramdisk_offset $(BOARD_RAMDISK_OFFSET)
BOARD_MKBOOTIMG_ARGS += --second_offset $(BOARD_SECOND_OFFSET)
BOARD_MKBOOTIMG_ARGS += --tags_offset $(BOARD_TAGS_OFFSET)
BOARD_MKBOOTIMG_ARGS += --header_version 2
```

## Tools

### Required Tools

```bash
# abootimg
sudo apt install abootimg

# Android Image Kitchen
wget https://github.com/osm0sis/Android-Image-Kitchen/archive/refs/heads/master.zip

# unmkbootimg
git clone https://github.com/xiaolu/unmkbootimg
```

### Optional Tools

- **bootimg-tools**: Alternative boot image tools
- **mkbootimg**: Create boot images
- **unpackbootimg**: Unpack boot images
- **Kernel Toolkit**: GUI tools for Windows

## Common Issues

### Wrong Base Address

**Symptoms**: Device won't boot, bootloop
**Solution**: Extract boot.img again, verify addresses

### Wrong Page Size

**Symptoms**: Build fails or boot fails
**Solution**: Confirm page size from boot.img

### Missing DTB

**Symptoms**: Hardware doesn't work properly
**Solution**: Extract and include DTB/DTBO

### Wrong Command Line

**Symptoms**: Hardware issues, SELinux denials
**Solution**: Use exact cmdline from stock boot.img

## Quick Reference

### Get All Kernel Info (One Command)

```bash
# Using abootimg
abootimg -i boot.img > kernel_info.txt

# Using AIK
./unpackimg.sh boot.img
cat split_img/* > kernel_info.txt
```

### Verify Your Configuration

```bash
# After building TWRP
abootimg -i out/target/product/codename/recovery.img

# Compare with stock
abootimg -i stock_boot.img
```

## Resources

- [Android Boot Image Format](https://source.android.com/docs/core/architecture/bootloader/boot-image-header)
- [Android Image Kitchen](https://github.com/osm0sis/Android-Image-Kitchen)
- [XDA Boot Image Tools](https://forum.xda-developers.com/)

---

**Tip**: Always keep a backup of your original boot.img - you'll need it for recovery if something goes wrong!
