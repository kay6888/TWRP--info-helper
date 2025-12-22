# Platform
TARGET_BOARD_PLATFORM := msm8996
TARGET_BOOTLOADER_BOARD_NAME := MSM8996

# Bootloader
TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true

# Architecture
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_ABI2 :=
TARGET_CPU_VARIANT := generic

# Second architecture (32-bit support)
TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
TARGET_2ND_CPU_VARIANT := cortex-a53

# Kernel
TARGET_PREBUILT_KERNEL := device/sample/sample/prebuilt/kernel

# Kernel command line
BOARD_KERNEL_CMDLINE := console=null androidboot.hardware=qcom
BOARD_KERNEL_CMDLINE += msm_rtb.filter=0x237 ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += lpm_levels.sleep_disabled=1
BOARD_KERNEL_CMDLINE += androidboot.bootdevice=624000.ufshc
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive

# Kernel base address and offsets
# These values must match your device's boot.img
# Extract using: abootimg -i boot.img
BOARD_KERNEL_BASE := 0x80000000
BOARD_KERNEL_OFFSET := 0x00008000
BOARD_RAMDISK_OFFSET := 0x01000000
BOARD_SECOND_OFFSET := 0x00f00000
BOARD_TAGS_OFFSET := 0x00000100
BOARD_KERNEL_PAGESIZE := 2048

# Boot image arguments
BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE)
BOARD_MKBOOTIMG_ARGS += --pagesize $(BOARD_KERNEL_PAGESIZE)
BOARD_MKBOOTIMG_ARGS += --kernel_offset $(BOARD_KERNEL_OFFSET)
BOARD_MKBOOTIMG_ARGS += --ramdisk_offset $(BOARD_RAMDISK_OFFSET)
BOARD_MKBOOTIMG_ARGS += --second_offset $(BOARD_SECOND_OFFSET)
BOARD_MKBOOTIMG_ARGS += --tags_offset $(BOARD_TAGS_OFFSET)

# Partitions
# Get these values from: adb shell cat /proc/partitions
# Calculate: blocks × 1024 = size in bytes
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864        # 64MB
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864    # 64MB
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3221225472    # 3GB
BOARD_USERDATAIMAGE_PARTITION_SIZE := 27258650624 # ~25GB (varies)
BOARD_CACHEIMAGE_PARTITION_SIZE := 268435456      # 256MB
BOARD_FLASH_BLOCK_SIZE := 131072                  # 128KB

# File systems
BOARD_HAS_LARGE_FILESYSTEM := true
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := true

# System-as-root (for Android 10+)
# Uncomment if your device uses system-as-root
# BOARD_BUILD_SYSTEM_ROOT_IMAGE := true
# BOARD_SUPPRESS_SECURE_ERASE := true

# Recovery
TARGET_RECOVERY_FSTAB := device/sample/sample/recovery.fstab
TARGET_RECOVERY_PIXEL_FORMAT := "RGBX_8888"

# TWRP Configuration
TW_THEME := portrait_hdpi
# Other theme options: portrait_mdpi, landscape_hdpi, landscape_mdpi, watch_mdpi

# Screen settings
TW_BRIGHTNESS_PATH := "/sys/class/backlight/panel/brightness"
TW_MAX_BRIGHTNESS := 255
TW_DEFAULT_BRIGHTNESS := 150

# Input
TW_NO_SCREEN_TIMEOUT := true
TW_NO_SCREEN_BLANK := true

# Storage
RECOVERY_SDCARD_ON_DATA := true
TW_INTERNAL_STORAGE_PATH := "/data/media/0"
TW_INTERNAL_STORAGE_MOUNT_POINT := "data"
TW_EXTERNAL_STORAGE_PATH := "/external_sd"
TW_EXTERNAL_STORAGE_MOUNT_POINT := "external_sd"

# Crypto/Encryption
TW_INCLUDE_CRYPTO := true
# For Android 7+ with File-Based Encryption
TW_INCLUDE_CRYPTO_FBE := true
# For Android 10+ with metadata encryption
TW_INCLUDE_FBE_METADATA_DECRYPT := true

# MTP (Media Transfer Protocol)
TW_EXCLUDE_MTP := false
TW_MTP_DEVICE := "/dev/mtp_usb"

# Additional features
TW_EXCLUDE_SUPERSU := true
TW_USE_TOOLBOX := true
TW_INCLUDE_NTFS_3G := true
TW_INCLUDE_FUSE_EXFAT := true
TW_INCLUDE_FUSE_NTFS := true

# USB OTG
TW_HAS_USB_STORAGE := true

# Exclude features to reduce size (optional)
TW_EXCLUDE_DEFAULT_USB_INIT := true
TW_EXCLUDE_ENCRYPTED_BACKUPS := true
TW_EXCLUDE_TWRPAPP := true

# Languages
TW_EXTRA_LANGUAGES := true
TW_DEFAULT_LANGUAGE := en

# Debug
TWRP_INCLUDE_LOGCAT := true
TARGET_USES_LOGD := true

# SELinux (optional - for custom policies)
# BOARD_SEPOLICY_DIRS += device/sample/sample/sepolicy
