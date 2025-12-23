# Quick Reference Cheat Sheet

## ADB Commands

### Device Connection
```bash
adb devices                           # List connected devices
adb kill-server                       # Stop ADB server
adb start-server                      # Start ADB server
adb connect [ip]:5555                 # Connect via WiFi
```

### Device Information
```bash
adb shell getprop ro.product.device              # Codename
adb shell getprop ro.product.manufacturer        # Manufacturer
adb shell getprop ro.product.brand               # Brand
adb shell getprop ro.product.model               # Model
adb shell getprop ro.board.platform              # Platform
adb shell getprop ro.product.cpu.abi             # Architecture
adb shell getprop ro.build.version.release       # Android version
adb shell getprop ro.build.version.sdk           # API level
adb shell wm size                                # Screen resolution
adb shell cat /proc/version                      # Kernel version
adb shell cat /proc/partitions                   # Partitions
adb shell cat /proc/cmdline                      # Kernel cmdline
```

### File Operations
```bash
adb push local_file /sdcard/              # Upload to device
adb pull /sdcard/file local_path          # Download from device
adb shell ls -la /path/                   # List directory
adb shell cat /path/file                  # View file content
```

### Boot Operations
```bash
adb reboot                               # Reboot to system
adb reboot recovery                      # Reboot to recovery
adb reboot bootloader                    # Reboot to fastboot
adb reboot download                      # Reboot to download (Samsung)
```

## Fastboot Commands

```bash
fastboot devices                         # List devices in fastboot
fastboot getvar all                      # Get all variables
fastboot flash recovery recovery.img    # Flash recovery
fastboot flash boot boot.img            # Flash boot
fastboot boot recovery.img              # Boot without flashing
fastboot erase cache                    # Erase cache
fastboot reboot                         # Reboot
fastboot reboot recovery                # Reboot to recovery
```

## BoardConfig.mk Essentials

### Platform
```makefile
TARGET_BOARD_PLATFORM := msm8996
TARGET_BOOTLOADER_BOARD_NAME := MSM8996
TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true
```

### Architecture (ARM64)
```makefile
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_VARIANT := generic

TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
TARGET_2ND_CPU_VARIANT := cortex-a53
```

### Architecture (ARM)
```makefile
TARGET_ARCH := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_VARIANT := cortex-a53
```

### Kernel Configuration
```makefile
TARGET_PREBUILT_KERNEL := device/brand/codename/prebuilt/kernel

BOARD_KERNEL_BASE := 0x80000000
BOARD_KERNEL_PAGESIZE := 2048
BOARD_KERNEL_OFFSET := 0x00008000
BOARD_RAMDISK_OFFSET := 0x01000000
BOARD_SECOND_OFFSET := 0x00f00000
BOARD_TAGS_OFFSET := 0x00000100

BOARD_KERNEL_CMDLINE := console=null androidboot.hardware=qcom
BOARD_MKBOOTIMG_ARGS := --base $(BOARD_KERNEL_BASE) --pagesize $(BOARD_KERNEL_PAGESIZE)
```

### Partitions
```makefile
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3221225472
BOARD_USERDATAIMAGE_PARTITION_SIZE := 27258650624
BOARD_CACHEIMAGE_PARTITION_SIZE := 268435456
BOARD_FLASH_BLOCK_SIZE := 131072
```

### File Systems
```makefile
BOARD_HAS_LARGE_FILESYSTEM := true
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := true
```

### TWRP Flags
```makefile
TW_THEME := portrait_hdpi
RECOVERY_SDCARD_ON_DATA := true
TW_INCLUDE_CRYPTO := true
TW_INCLUDE_CRYPTO_FBE := true
TW_BRIGHTNESS_PATH := "/sys/class/backlight/panel/brightness"
TW_MAX_BRIGHTNESS := 255
TW_DEFAULT_BRIGHTNESS := 150
TW_EXCLUDE_DEFAULT_USB_INIT := true
TW_INCLUDE_NTFS_3G := true
TW_INTERNAL_STORAGE_PATH := "/data/media/0"
TW_INTERNAL_STORAGE_MOUNT_POINT := "data"
TW_EXTERNAL_STORAGE_PATH := "/external_sd"
TW_EXTERNAL_STORAGE_MOUNT_POINT := "external_sd"
```

## recovery.fstab Format

```
# mount point    fstype    device                              flags
/boot            emmc      /dev/block/bootdevice/by-name/boot  flags=display="Boot";backup=1
/recovery        emmc      /dev/block/bootdevice/by-name/recovery
/system          ext4      /dev/block/bootdevice/by-name/system
/data            ext4      /dev/block/bootdevice/by-name/userdata  flags=encryptable=footer
/cache           ext4      /dev/block/bootdevice/by-name/cache
/external_sd     auto      /dev/block/mmcblk1p1   /dev/block/mmcblk1  flags=storage;removable
/usb_otg         auto      /dev/block/sda1        /dev/block/sda      flags=storage;removable
```

### Common Flags
- `backup=1` - Include in backups
- `display="Name"` - Display name in TWRP
- `storage` - Use for storage
- `removable` - Removable storage
- `flashimg=1` - Allow IMG flashing
- `encryptable=footer` - FDE encryption
- `fileencryption=ice` - FBE encryption
- `wipeingui` - Show in wipe menu

## Partition Naming Conventions

### Qualcomm Devices
```
/dev/block/bootdevice/by-name/boot
/dev/block/bootdevice/by-name/recovery
/dev/block/bootdevice/by-name/system
/dev/block/bootdevice/by-name/userdata
```

### Samsung Devices
```
/dev/block/platform/*/by-name/BOOT
/dev/block/platform/*/by-name/RECOVERY
/dev/block/platform/*/by-name/SYSTEM
/dev/block/platform/*/by-name/USERDATA
```

### MediaTek Devices
```
/dev/block/platform/bootdevice/by-name/boot
/dev/block/platform/bootdevice/by-name/recovery
/dev/block/platform/bootdevice/by-name/system
/dev/block/platform/bootdevice/by-name/userdata
```

## Common Platform Values

| Chipset | Platform Value |
|---------|---------------|
| Snapdragon 660 | msm8956 |
| Snapdragon 710 | sdm710 |
| Snapdragon 820 | msm8996 |
| Snapdragon 835 | msm8998 |
| Snapdragon 845 | sdm845 |
| Snapdragon 855 | sm8150 |
| Snapdragon 865 | kona |
| Samsung Exynos 7 | exynos5 |
| Samsung Exynos 8 | exynos8 |
| Samsung Exynos 9 | exynos9 |
| MediaTek Helio P22 | mt6762 |
| MediaTek Helio P60 | mt6771 |
| MediaTek Helio G85 | mt6785 |

## TWRP Theme Selection

| Resolution | Orientation | Theme |
|-----------|-------------|-------|
| 720p | Portrait | `portrait_mdpi` |
| 1080p+ | Portrait | `portrait_hdpi` |
| 720p | Landscape | `landscape_mdpi` |
| 1080p+ | Landscape | `landscape_hdpi` |
| Smartwatch | Any | `watch_mdpi` |

## Partition Size Calculation

```
From /proc/partitions:
Blocks × 1024 = Bytes

Example:
65536 blocks
65536 × 1024 = 67108864 bytes (64MB)

Use in BoardConfig.mk:
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
```

## Build Commands

### Initialize TWRP Source
```bash
mkdir ~/twrp && cd ~/twrp
repo init -u https://github.com/minimal-manifest-twrp/platform_manifest_twrp_omni.git -b twrp-9.0
repo sync -j$(nproc --all)
```

### Build TWRP
```bash
cd ~/twrp
source build/envsetup.sh
lunch omni_[codename]-eng
mka recoveryimage
# or
make -j$(nproc --all) recoveryimage
```

### Clean Build
```bash
make clean
make clobber  # Deep clean
```

## Boot Image Tools

### abootimg
```bash
# Extract info
abootimg -i boot.img

# Extract components
abootimg -x boot.img

# Create boot image
abootimg --create new.img -f bootimg.cfg -k zImage -r initrd.img
```

### Android Image Kitchen
```bash
# Unpack
./unpackimg.sh boot.img

# Repack
./repackimg.sh

# Cleanup
./cleanup.sh
```

## Common Issues Quick Fix

| Issue | Quick Fix |
|-------|-----------|
| Black screen | Check BOARD_KERNEL_BASE and offsets |
| Touch not working | Try different TW_THEME |
| Can't mount data | Add encryption flags |
| Partitions missing | Check recovery.fstab paths |
| Build fails | Run `make clean` and rebuild |
| Recovery too large | Add TW_EXCLUDE_* flags |
| MTP not working | Set TW_EXCLUDE_MTP := false |
| Brightness issue | Set TW_BRIGHTNESS_PATH |

## File Structure Quick View

```
device/manufacturer/codename/
├── AndroidProducts.mk
├── BoardConfig.mk
├── device.mk
├── omni_codename.mk
├── recovery.fstab
└── prebuilt/
    └── kernel
```

## Quick Troubleshooting Checklist

- [ ] Device codename is correct
- [ ] Architecture matches device (arm64-v8a, etc.)
- [ ] Platform is correct
- [ ] Kernel base and offsets from boot.img
- [ ] Partition sizes calculated correctly
- [ ] recovery.fstab has correct paths
- [ ] TW_THEME matches screen resolution
- [ ] All required files exist
- [ ] Paths in device.mk are correct

## Useful One-Liners

```bash
# Complete device info dump
adb shell getprop > props.txt && \
adb shell cat /proc/partitions > partitions.txt && \
adb shell cat /proc/version > kernel.txt && \
adb shell wm size > resolution.txt

# Pull boot image (with root)
adb shell "su -c 'dd if=/dev/block/bootdevice/by-name/boot of=/sdcard/boot.img'" && \
adb pull /sdcard/boot.img

# Find all partition paths
adb shell ls -la /dev/block/platform/*/by-name/ > partition_paths.txt

# Complete boot.img analysis
abootimg -i boot.img > boot_info.txt
```

## Hex ↔ Decimal Quick Reference

| Hex | Decimal |
|-----|---------|
| 0x00008000 | 32768 |
| 0x00000100 | 256 |
| 0x00f00000 | 15728640 |
| 0x01000000 | 16777216 |
| 0x02000000 | 33554432 |
| 0x10000000 | 268435456 |
| 0x40000000 | 1073741824 |
| 0x80000000 | 2147483648 |

## Size Conversions

| Bytes | KB | MB | GB |
|-------|----|----|-----|
| 67108864 | 65536 | 64 | 0.0625 |
| 268435456 | 262144 | 256 | 0.25 |
| 536870912 | 524288 | 512 | 0.5 |
| 1073741824 | 1048576 | 1024 | 1 |
| 2147483648 | 2097152 | 2048 | 2 |
| 3221225472 | 3145728 | 3072 | 3 |

## Important Links

- **TWRP**: https://twrp.me/
- **Hovatek Builder**: https://www.hovatek.com/twrpbuilder/
- **XDA Forums**: https://forum.xda-developers.com/
- **Android Source**: https://source.android.com/
- **GitHub TWRP**: https://github.com/TeamWin

---

**Print this page for quick reference while building TWRP!**
