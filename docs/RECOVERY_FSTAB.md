# Recovery Fstab Configuration Guide

## Table of Contents
- [Introduction](#introduction)
- [Fstab Format](#fstab-format)
- [Mount Points](#mount-points)
- [File System Types](#file-system-types)
- [Mount Flags](#mount-flags)
- [Examples](#examples)
- [Extracting Fstab](#extracting-fstab)
- [Common Configurations](#common-configurations)

## Introduction

The `recovery.fstab` file defines how TWRP mounts and manages device partitions. A properly configured fstab is critical for TWRP to function correctly - it controls backup/restore, mounting, and file operations.

## Fstab Format

### TWRP Fstab v1 Format

```
# Mount Point    fstype    device                             [device2] [flags]
/boot            emmc      /dev/block/bootdevice/by-name/boot
/system          ext4      /dev/block/bootdevice/by-name/system
/data            ext4      /dev/block/bootdevice/by-name/userdata    flags=encryptable=footer
/cache           ext4      /dev/block/bootdevice/by-name/cache
/recovery        emmc      /dev/block/bootdevice/by-name/recovery
```

### TWRP Fstab v2 Format (Recommended)

```
# mount point    fstype    device                                           device2          flags
/boot            emmc      /dev/block/bootdevice/by-name/boot                                flags=display="Boot";backup=1;flashimg=1
/recovery        emmc      /dev/block/bootdevice/by-name/recovery                           flags=display="Recovery";backup=1;flashimg=1
/system          ext4      /dev/block/bootdevice/by-name/system                             flags=display="System"
/system_image    emmc      /dev/block/bootdevice/by-name/system                             flags=display="System Image";backup=1;flashimg=1
/vendor          ext4      /dev/block/bootdevice/by-name/vendor                             flags=display="Vendor"
/vendor_image    emmc      /dev/block/bootdevice/by-name/vendor                             flags=display="Vendor Image";backup=1;flashimg=1
/data            ext4      /dev/block/bootdevice/by-name/userdata                           flags=encryptable=footer;length=-16384
/cache           ext4      /dev/block/bootdevice/by-name/cache                              flags=display="Cache"
/persist         ext4      /dev/block/bootdevice/by-name/persist                            flags=display="Persist"
/firmware        vfat      /dev/block/bootdevice/by-name/modem                              flags=display="Firmware";mounttodecrypt;fsflags=ro
/misc            emmc      /dev/block/bootdevice/by-name/misc                               flags=display="Misc"
/efs1            emmc      /dev/block/bootdevice/by-name/modemst1                           flags=backup=1;display="EFS1"
/efs2            emmc      /dev/block/bootdevice/by-name/modemst2                           flags=backup=1;display="EFS2"

# Removable storage
/external_sd     vfat      /dev/block/mmcblk1p1                         /dev/block/mmcblk1  flags=display="MicroSD Card";storage;wipeingui;removable
/usb_otg         vfat      /dev/block/sda1                              /dev/block/sda      flags=display="USB OTG";storage;wipeingui;removable
```

## Mount Points

### Essential Mount Points

| Mount Point | Description | Backup? | Required? |
|------------|-------------|---------|-----------|
| /boot | Kernel and ramdisk | Yes | Yes |
| /recovery | Recovery partition | Yes | Yes |
| /system | Android system | Yes | Yes |
| /vendor | Vendor files (Android 8+) | Yes | Yes (8+) |
| /data | User data | Yes | Yes |
| /cache | System cache | No | Yes |
| /misc | Miscellaneous partition | No | Optional |
| /persist | Persistent data | Yes | Optional |

### Additional Mount Points

| Mount Point | Description | Notes |
|------------|-------------|-------|
| /firmware | Modem/firmware partition | Often read-only |
| /modem | Modem firmware | May be same as firmware |
| /efs | IMEI and network data | **Critical - always backup!** |
| /metadata | Encryption metadata (Android 10+) | Required for FBE |
| /dtbo | Device tree overlay | Android 9+ |
| /odm | OEM files | Android 9+ |
| /product | Product-specific files | Android 10+ |

### Removable Storage

| Mount Point | Description |
|------------|-------------|
| /external_sd | MicroSD card slot |
| /usb_otg | USB OTG storage |
| /sdcard | Internal SD (usually symlink to /data/media) |

## File System Types

### Common File System Types

- **ext4**: Standard Linux filesystem (most Android partitions)
- **f2fs**: Flash-Friendly File System (modern devices)
- **vfat**: FAT32 (SD cards, some firmware partitions)
- **emmc**: Raw eMMC partition (boot, recovery, raw images)
- **exfat**: Extended FAT (large SD cards)
- **ntfs**: NTFS filesystem (USB drives)
- **auto**: Auto-detect filesystem type

### Choosing the Right Type

```
# Boot/Recovery - always emmc (raw image)
/boot            emmc      /dev/block/.../boot

# System partitions - ext4 or f2fs
/system          ext4      /dev/block/.../system
# or
/data            f2fs      /dev/block/.../userdata

# Firmware - usually vfat
/firmware        vfat      /dev/block/.../modem

# External storage - vfat or auto
/external_sd     auto      /dev/block/mmcblk1p1
```

## Mount Flags

### Essential Flags

#### Display Name
```
flags=display="Friendly Name"
```
Shows a user-friendly name in TWRP.

#### Backup
```
flags=backup=1
```
Include in TWRP backups.

#### Storage
```
flags=storage
```
Use as storage location for backups/files.

#### Flash Image
```
flags=flashimg=1
```
Allow flashing IMG files to this partition.

#### Wipe in GUI
```
flags=wipeingui
```
Show wipe option in TWRP GUI.

#### Removable
```
flags=removable
```
Indicate removable storage (SD card, USB).

### Encryption Flags

#### Footer Encryption (Full Disk Encryption)
```
flags=encryptable=footer
```
For devices using FDE (Android 4.4-6.0).

#### File-Based Encryption (FBE)
```
flags=encryptable=footer;length=-16384
# or
flags=fileencryption=ice
# or
flags=fileencryption=aes-256-xts
```
For devices using FBE (Android 7+).

#### Metadata Encryption
```
# /metadata partition for Android 10+
/metadata        ext4      /dev/block/.../metadata          flags=display="Metadata";wrappedkey
```

### Advanced Flags

#### Mount to Decrypt
```
flags=mounttodecrypt
```
Mount during decryption process.

#### FS Flags
```
flags=fsflags=ro
flags=fsflags=noatime,nosuid,nodev
```
Pass specific mount flags to the filesystem.

#### Symlink
```
flags=symlink=/system/vendor
```
Create symlink to another location.

#### Subpartition Of
```
flags=subpartitionof=/data
```
Indicate logical relationship to another partition.

#### Can Be Mounted/Wiped
```
flags=canbewiped
flags=cannotbemounted
```
Control mount/wipe capabilities.

#### Length
```
flags=length=-16384
```
Reduce partition size (for encryption footer).

### Storage Flags

```
flags=storage;wipeingui;removable
flags=settingsstorage
flags=usermrf
```

## Examples

### Modern Qualcomm Device (Android 10+)

```fstab
# mount point    fstype    device                                    flags
/boot            emmc      /dev/block/bootdevice/by-name/boot        flags=display="Boot";backup=1;flashimg=1
/dtbo            emmc      /dev/block/bootdevice/by-name/dtbo        flags=display="DTBO";backup=1;flashimg=1
/recovery        emmc      /dev/block/bootdevice/by-name/recovery    flags=display="Recovery";backup=1;flashimg=1

/system_root     ext4      /dev/block/bootdevice/by-name/system      flags=display="System";backup=1;wipeingui
/system_image    emmc      /dev/block/bootdevice/by-name/system      flags=display="System Image";backup=1;flashimg=1

/vendor          ext4      /dev/block/bootdevice/by-name/vendor      flags=display="Vendor";backup=1;wipeingui
/vendor_image    emmc      /dev/block/bootdevice/by-name/vendor      flags=display="Vendor Image";backup=1;flashimg=1

/product         ext4      /dev/block/bootdevice/by-name/product     flags=display="Product";backup=1;wipeingui
/product_image   emmc      /dev/block/bootdevice/by-name/product     flags=display="Product Image";backup=1;flashimg=1

/data            f2fs      /dev/block/bootdevice/by-name/userdata    flags=fileencryption=ice;wrappedkey;keydirectory=/metadata/vold/metadata_encryption
/cache           ext4      /dev/block/bootdevice/by-name/cache       flags=display="Cache"
/metadata        ext4      /dev/block/bootdevice/by-name/metadata    flags=display="Metadata";wrappedkey

/persist         ext4      /dev/block/bootdevice/by-name/persist     flags=display="Persist";backup=1
/firmware        vfat      /dev/block/bootdevice/by-name/modem       flags=display="Firmware";mounttodecrypt;fsflags=ro
/misc            emmc      /dev/block/bootdevice/by-name/misc
/efs1            emmc      /dev/block/bootdevice/by-name/modemst1    flags=backup=1;display="EFS"
/efs2            emmc      /dev/block/bootdevice/by-name/modemst2    flags=backup=1;subpartitionof=/efs1

/external_sd     auto      /dev/block/mmcblk1p1  /dev/block/mmcblk1  flags=display="MicroSD";storage;wipeingui;removable
/usb_otg         auto      /dev/block/sda1       /dev/block/sda      flags=display="USB OTG";storage;wipeingui;removable
```

### Older Device (Android 7-8)

```fstab
/boot            emmc      /dev/block/platform/soc/7824900.sdhci/by-name/boot
/recovery        emmc      /dev/block/platform/soc/7824900.sdhci/by-name/recovery
/system          ext4      /dev/block/platform/soc/7824900.sdhci/by-name/system
/system_image    emmc      /dev/block/platform/soc/7824900.sdhci/by-name/system      flags=backup=1;flashimg=1
/data            ext4      /dev/block/platform/soc/7824900.sdhci/by-name/userdata    flags=encryptable=footer;length=-16384
/cache           ext4      /dev/block/platform/soc/7824900.sdhci/by-name/cache
/persist         ext4      /dev/block/platform/soc/7824900.sdhci/by-name/persist     flags=display="Persist"
/firmware        vfat      /dev/block/platform/soc/7824900.sdhci/by-name/modem       flags=display="Firmware"
/misc            emmc      /dev/block/platform/soc/7824900.sdhci/by-name/misc

/external_sd     vfat      /dev/block/mmcblk1p1  /dev/block/mmcblk1                  flags=display="MicroSD";storage;wipeingui;removable
/usb-otg         vfat      /dev/block/sda1       /dev/block/sda                      flags=display="USB OTG";storage;wipeingui;removable
```

### MediaTek Device

```fstab
/boot            emmc      /dev/block/platform/bootdevice/by-name/boot           flags=display="Boot"
/recovery        emmc      /dev/block/platform/bootdevice/by-name/recovery       flags=display="Recovery";backup=1
/system          ext4      /dev/block/platform/bootdevice/by-name/system         flags=display="System"
/vendor          ext4      /dev/block/platform/bootdevice/by-name/vendor         flags=display="Vendor"
/data            ext4      /dev/block/platform/bootdevice/by-name/userdata       flags=encryptable=footer
/cache           ext4      /dev/block/platform/bootdevice/by-name/cache
/nvram           emmc      /dev/block/platform/bootdevice/by-name/nvram          flags=display="NVRAM";backup=1;flashimg=1
/persist         ext4      /dev/block/platform/bootdevice/by-name/persist        flags=display="Persist"
/protect_f       ext4      /dev/block/platform/bootdevice/by-name/protect1       flags=display="Protect"
/protect_s       ext4      /dev/block/platform/bootdevice/by-name/protect2
/lk              emmc      /dev/block/platform/bootdevice/by-name/lk             flags=display="Bootloader";backup=1;flashimg=1

/external_sd     auto      /dev/block/mmcblk1p1  /dev/block/mmcblk1              flags=display="MicroSD";storage;wipeingui;removable
/usbotg          auto      /dev/block/sda1       /dev/block/sda                  flags=display="USB OTG";storage;wipeingui;removable
```

## Extracting Fstab

### From Stock Recovery

```bash
# Boot to recovery
adb reboot recovery

# Extract fstab
adb pull /etc/recovery.fstab
adb pull /etc/twrp.fstab
```

### From Boot/Recovery Image

```bash
# Extract boot/recovery image
unpackbootimg -i recovery.img
# or
abootimg -x recovery.img

# Extract ramdisk
mkdir ramdisk
cd ramdisk
gunzip -c ../recovery.img-ramdisk.gz | cpio -i

# Find fstab
cat etc/recovery.fstab
cat etc/twrp.fstab
```

### From Running System

```bash
# System fstab (not exactly recovery fstab but helpful)
adb shell cat /system/etc/fstab*
adb shell cat /vendor/etc/fstab*

# Mount points
adb shell cat /proc/mounts
```

### From Device Tree

Check existing device trees for similar devices:
```bash
# Search GitHub
https://github.com/TeamWin/android_device_*

# Or XDA forums for your device
```

## Common Configurations

### System-as-root (Android 10+)

```
/system_root     ext4      /dev/block/bootdevice/by-name/system      flags=display="System"
# Don't mount /system separately - it's in /system_root
```

### A/B Partitioning

```
/boot            emmc      /dev/block/bootdevice/by-name/boot        flags=slotselect
/system          ext4      /dev/block/bootdevice/by-name/system      flags=slotselect
/vendor          ext4      /dev/block/bootdevice/by-name/vendor      flags=slotselect
```

### Dynamic Partitions (Android 10+)

```
/super           emmc      /dev/block/bootdevice/by-name/super       flags=display="Super";backup=1
```

## Troubleshooting

### Partition Not Found

Check actual device path:
```bash
adb shell ls -la /dev/block/platform/*/by-name/
```

### Unable to Mount

- Verify filesystem type (ext4 vs f2fs)
- Check encryption flags
- Ensure partition exists

### Storage Issues

- Verify `storage` flag on correct partitions
- Check removable storage paths
- Test with `adb shell mount`

## Testing Your Fstab

1. Boot TWRP with your fstab
2. Check Mount menu - all partitions should be listed
3. Try mounting/unmounting each partition
4. Test backup/restore
5. Check external storage detection

## Resources

- [TWRP Fstab Guide](https://github.com/TeamWin/Team-Win-Recovery-Project/blob/android-9.0/partitions.hpp)
- [Android Partitions](https://source.android.com/docs/core/architecture/partitions)
- Example device trees on GitHub

---

**Important**: Always backup EFS/persist partitions - they contain unique device data that cannot be recovered if lost!
