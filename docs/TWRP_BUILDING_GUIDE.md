# TWRP Building Guide

## Table of Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Understanding TWRP](#understanding-twrp)
- [Device Tree Structure](#device-tree-structure)
- [Required Files](#required-files)
- [BoardConfig.mk Configuration](#boardconfigmk-configuration)
- [Device.mk Configuration](#devicemk-configuration)
- [Building Process](#building-process)
- [Testing Your Build](#testing-your-build)

## Introduction

TWRP (Team Win Recovery Project) is a custom recovery image for Android devices. Building TWRP requires proper device-specific configuration files organized in a device tree. This guide covers everything you need to know to build TWRP for your device.

## Prerequisites

Before building TWRP, ensure you have:

1. **Device Information** - See [DEVICE_INFO_EXTRACTION.md](DEVICE_INFO_EXTRACTION.md)
2. **Build Environment** - Linux system (Ubuntu recommended) with:
   - 100GB+ free disk space
   - 16GB+ RAM (8GB minimum)
   - Fast internet connection
3. **Tools**:
   - Git
   - ADB and Fastboot
   - Android build tools
   - Text editor

## Understanding TWRP

TWRP is a custom recovery that allows you to:
- Install custom ROMs and modifications
- Create full device backups (NANDroid)
- Wipe/format partitions
- Mount storage for file management
- Flash ZIP files
- Advanced file operations

## Device Tree Structure

A proper TWRP device tree should be organized as follows:

```
device/[manufacturer]/[codename]/
├── AndroidProducts.mk          # Product definition
├── BoardConfig.mk              # Board/hardware configuration
├── device.mk                   # Device-specific settings
├── omni_[codename].mk         # Omni/TWRP product config
├── recovery.fstab             # Partition mount configuration
├── recovery/                  # Recovery-specific files
│   └── root/                  # Recovery ramdisk additions
├── prebuilt/                  # Prebuilt files (optional)
│   └── kernel                 # Prebuilt kernel (if not building from source)
└── README.md                  # Documentation
```

### Example Directory Structure

For a Samsung Galaxy S7 (herolte):
```
device/samsung/herolte/
├── AndroidProducts.mk
├── BoardConfig.mk
├── device.mk
├── omni_herolte.mk
├── recovery.fstab
└── prebuilt/
    └── kernel
```

## Required Files

### 1. AndroidProducts.mk

This file defines which product makefiles to include:

```makefile
PRODUCT_MAKEFILES := \
    $(LOCAL_DIR)/omni_[codename].mk

COMMON_LUNCH_CHOICES := \
    omni_[codename]-eng
```

**Purpose**: Tells the build system which product configurations are available.

### 2. BoardConfig.mk

The most important file containing all hardware-specific configurations. See [BoardConfig.mk Configuration](#boardconfigmk-configuration) section below.

### 3. device.mk

Contains device-specific product configuration. See [Device.mk Configuration](#devicemk-configuration) section below.

### 4. omni_[codename].mk

Product configuration for TWRP/Omni:

```makefile
# Release name
PRODUCT_RELEASE_NAME := [device_model]

# Inherit from common TWRP product configuration
$(call inherit-product, vendor/omni/config/common.mk)

# Inherit from device configuration
$(call inherit-product, device/[manufacturer]/[codename]/device.mk)

# Device identifier
PRODUCT_DEVICE := [codename]
PRODUCT_NAME := omni_[codename]
PRODUCT_BRAND := [brand]
PRODUCT_MODEL := [model]
PRODUCT_MANUFACTURER := [manufacturer]
```

### 5. recovery.fstab

Defines partition mount points and properties. See [RECOVERY_FSTAB.md](RECOVERY_FSTAB.md) for details.

## BoardConfig.mk Configuration

This is the heart of your device tree. Here are all essential flags:

### Basic Device Information

```makefile
# Platform/Board
TARGET_BOARD_PLATFORM := [platform]           # e.g., msm8996, exynos5, mt6765
TARGET_BOOTLOADER_BOARD_NAME := [board_name]  # e.g., MSM8996, universal8890

# No Bootloader/Radioimage (for most devices)
TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true
```

### Architecture Settings

For ARM64 devices:
```makefile
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_ABI2 :=
TARGET_CPU_VARIANT := generic

# 2nd Architecture (32-bit support on 64-bit devices)
TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
TARGET_2ND_CPU_VARIANT := generic
```

For ARM devices:
```makefile
TARGET_ARCH := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_VARIANT := generic
```

### Kernel Configuration

```makefile
# Kernel Image
TARGET_PREBUILT_KERNEL := device/[manufacturer]/[codename]/prebuilt/kernel
# OR if building from source:
# TARGET_KERNEL_SOURCE := kernel/[manufacturer]/[codename]
# TARGET_KERNEL_CONFIG := [codename]_defconfig

# Kernel Command Line
BOARD_KERNEL_CMDLINE := console=null androidboot.hardware=[hardware]
# Add your device-specific cmdline parameters

# Kernel Base Address
BOARD_KERNEL_BASE := 0x80000000        # Find using abootimg or unpackbootimg

# Kernel Offsets
BOARD_KERNEL_OFFSET := 0x00008000
BOARD_RAMDISK_OFFSET := 0x01000000
BOARD_SECOND_OFFSET := 0x00f00000
BOARD_TAGS_OFFSET := 0x00000100
BOARD_KERNEL_PAGESIZE := 2048          # Usually 2048 or 4096

# DTB/DTBO (Device Tree)
BOARD_KERNEL_SEPARATED_DTBO := true    # If device uses separate DTBO
BOARD_INCLUDE_DTB_IN_BOOTIMG := true   # If DTB should be in boot image

# Boot Image Arguments
BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE)
BOARD_MKBOOTIMG_ARGS += --pagesize $(BOARD_KERNEL_PAGESIZE)
BOARD_MKBOOTIMG_ARGS += --kernel_offset $(BOARD_KERNEL_OFFSET)
BOARD_MKBOOTIMG_ARGS += --ramdisk_offset $(BOARD_RAMDISK_OFFSET)
BOARD_MKBOOTIMG_ARGS += --second_offset $(BOARD_SECOND_OFFSET)
BOARD_MKBOOTIMG_ARGS += --tags_offset $(BOARD_TAGS_OFFSET)
```

### Partition Sizes

```makefile
# Get partition sizes using: cat /proc/partitions
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864        # 64MB in bytes
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864    # 64MB in bytes
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3221225472    # 3GB in bytes
BOARD_USERDATAIMAGE_PARTITION_SIZE := 27258650624 # Varies by device
BOARD_CACHEIMAGE_PARTITION_SIZE := 268435456      # 256MB in bytes
BOARD_FLASH_BLOCK_SIZE := 131072                  # Block size (usually 131072)
```

### File System Types

```makefile
BOARD_HAS_LARGE_FILESYSTEM := true
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := true    # If device supports F2FS

# System-as-root (Android 10+)
BOARD_BUILD_SYSTEM_ROOT_IMAGE := true
BOARD_SUPPRESS_SECURE_ERASE := true
```

### TWRP-Specific Flags

```makefile
# TWRP Configuration
TW_THEME := portrait_hdpi                      # or portrait_mdpi, landscape_hdpi, etc.
RECOVERY_SDCARD_ON_DATA := true                # If no physical SD card
TW_INCLUDE_CRYPTO := true                      # For encrypted devices
TW_INCLUDE_CRYPTO_FBE := true                  # For File-Based Encryption (Android 7+)
TW_INCLUDE_FBE_METADATA_DECRYPT := true        # For metadata encryption

# Screen Settings
TW_BRIGHTNESS_PATH := "/sys/class/backlight/panel/brightness"
TW_MAX_BRIGHTNESS := 255
TW_DEFAULT_BRIGHTNESS := 150

# Input
TW_NO_SCREEN_TIMEOUT := true                   # Disable screen timeout
TW_NO_SCREEN_BLANK := true                     # Prevent screen blanking

# Storage
TW_INTERNAL_STORAGE_PATH := "/data/media/0"
TW_INTERNAL_STORAGE_MOUNT_POINT := "data"
TW_EXTERNAL_STORAGE_PATH := "/external_sd"
TW_EXTERNAL_STORAGE_MOUNT_POINT := "external_sd"

# MTP (Media Transfer Protocol)
TW_EXCLUDE_MTP := false                        # Include MTP support
TW_MTP_DEVICE := "/dev/mtp_usb"

# Additional Features
TW_EXCLUDE_SUPERSU := true                     # Exclude SuperSU
TW_USE_TOOLBOX := true                         # Use toolbox instead of busybox
TW_INCLUDE_NTFS_3G := true                     # NTFS filesystem support
TW_INCLUDE_FUSE_EXFAT := true                  # exFAT filesystem support
TW_INCLUDE_FUSE_NTFS := true                   # NTFS filesystem support

# USB Storage
TW_HAS_USB_STORAGE := true                     # Device has USB OTG
TW_NO_USB_STORAGE := false

# Custom Features
TW_CUSTOM_CPU_TEMP_PATH := "/sys/class/thermal/thermal_zone0/temp"
TW_CUSTOM_BATTERY_PATH := "/sys/class/power_supply/battery"

# Exclude unnecessary features (reduces size)
TW_EXCLUDE_DEFAULT_USB_INIT := true
TW_EXCLUDE_ENCRYPTED_BACKUPS := true
TW_EXCLUDE_TWRPAPP := true

# Language
TW_EXTRA_LANGUAGES := true                     # Include additional languages
TW_DEFAULT_LANGUAGE := en                      # Default to English
```

### Additional Recovery Features

```makefile
# Debugging
TWRP_INCLUDE_LOGCAT := true
TARGET_USES_LOGD := true

# SELinux
BOARD_SEPOLICY_DIRS += device/[manufacturer]/[codename]/sepolicy
```

## Device.mk Configuration

The `device.mk` file specifies packages and files to include:

```makefile
LOCAL_PATH := device/[manufacturer]/[codename]

# Inherit from common configuration
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Time Zone data
PRODUCT_COPY_FILES += \
    bionic/libc/zoneinfo/tzdata:recovery/root/system/usr/share/zoneinfo/tzdata

# Prebuilt Kernel
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/prebuilt/kernel:kernel

# Recovery fstab
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/recovery.fstab:recovery/root/etc/recovery.fstab

# Properties
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.keystore=mdfpp \
    ro.hardware.gatekeeper=mdfpp

# Packages to include
PRODUCT_PACKAGES += \
    charger_res_images \
    charger

# Additional libraries
PRODUCT_PACKAGES += \
    libion \
    libxml2
```

## Building Process

### Step 1: Set Up Build Environment

```bash
# Install required packages (Ubuntu/Debian)
sudo apt update
sudo apt install -y bc bison build-essential ccache curl flex \
    g++-multilib gcc-multilib git gnupg gperf imagemagick lib32ncurses5-dev \
    lib32readline-dev lib32z1-dev liblz4-tool libncurses5 libncurses5-dev \
    libsdl1.2-dev libssl-dev libxml2 libxml2-utils lzop pngcrush \
    rsync schedtool squashfs-tools xsltproc zip zlib1g-dev
```

### Step 2: Initialize TWRP Source

```bash
# Create working directory
mkdir ~/twrp
cd ~/twrp

# Initialize repo
repo init -u https://github.com/minimal-manifest-twrp/platform_manifest_twrp_omni.git -b twrp-9.0

# Sync source (this will take a while)
repo sync -j$(nproc --all)
```

### Step 3: Add Device Tree

```bash
# Clone or copy your device tree
cd ~/twrp
mkdir -p device/[manufacturer]/[codename]
# Copy your device tree files here

# Or clone from repository
git clone [your-device-tree-repo] device/[manufacturer]/[codename]
```

### Step 4: Build TWRP

```bash
# Set up environment
cd ~/twrp
source build/envsetup.sh

# Select your device
lunch omni_[codename]-eng

# Build recovery
mka recoveryimage

# Or use make
make -j$(nproc --all) recoveryimage
```

### Step 5: Find Your Build

The recovery image will be located at:
```
out/target/product/[codename]/recovery.img
```

## Testing Your Build

### Flashing via Fastboot

```bash
# Boot into fastboot mode
adb reboot bootloader

# Flash recovery
fastboot flash recovery recovery.img

# Reboot to recovery
fastboot reboot recovery
```

### Flashing via ADB (if you have root)

```bash
# Find recovery partition
adb shell
su
ls -la /dev/block/platform/*/by-name/ | grep recovery

# Flash (example - adjust for your device)
adb push recovery.img /sdcard/
adb shell
su
dd if=/sdcard/recovery.img of=/dev/block/[recovery-partition]
```

### Testing Checklist

- [ ] Device boots into recovery
- [ ] Touchscreen works properly
- [ ] All partitions are visible and mountable
- [ ] MTP file transfer works
- [ ] Backup/restore functions work
- [ ] Encryption/decryption works (if applicable)
- [ ] Display brightness controls work
- [ ] All buttons function correctly

## Common Issues

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for detailed solutions.

## Next Steps

1. Review [DEVICE_INFO_EXTRACTION.md](DEVICE_INFO_EXTRACTION.md) to gather device info
2. Check [RECOVERY_FSTAB.md](RECOVERY_FSTAB.md) for fstab configuration
3. Read [KERNEL_INFO.md](KERNEL_INFO.md) for kernel extraction
4. See [HOVATEK_BUILDER_GUIDE.md](HOVATEK_BUILDER_GUIDE.md) for online builder usage

## Resources

- [Official TWRP Documentation](https://twrp.me/)
- [XDA Developers Forum](https://forum.xda-developers.com/)
- [TWRP Device Tree Examples](https://github.com/TeamWin)
- [TOOLS_AND_RESOURCES.md](TOOLS_AND_RESOURCES.md)

## Contributing

Found an error or want to improve this guide? Contributions are welcome!

---

**Remember**: Building TWRP requires patience and attention to detail. Double-check all configurations before building!
