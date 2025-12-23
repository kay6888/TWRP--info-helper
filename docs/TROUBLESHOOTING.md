# TWRP Troubleshooting Guide

## Table of Contents
- [Build Issues](#build-issues)
- [Boot Issues](#boot-issues)
- [Partition and Mount Issues](#partition-and-mount-issues)
- [Display and Touch Issues](#display-and-touch-issues)
- [Storage Issues](#storage-issues)
- [Encryption and Decryption Issues](#encryption-and-decryption-issues)
- [Backup and Restore Issues](#backup-and-restore-issues)
- [Flashing Issues](#flashing-issues)

## Build Issues

### Build Fails with "Missing Dependencies"

**Symptoms**: Build stops with dependency errors

**Solutions**:
```bash
# Sync again
repo sync -j$(nproc --all) --force-sync

# Clean and rebuild
make clean
source build/envsetup.sh
lunch omni_[codename]-eng
mka recoveryimage
```

### Build Fails with "No Rule to Make Target"

**Symptoms**: Error about missing kernel or files

**Solutions**:
1. **Check prebuilt kernel exists**:
   ```bash
   ls -la device/manufacturer/codename/prebuilt/kernel
   ```

2. **Verify device.mk PRODUCT_COPY_FILES paths**
   - Ensure all source files exist
   - Check paths are correct

3. **Verify BoardConfig.mk paths**
   - TARGET_PREBUILT_KERNEL path is correct
   - All referenced files exist

### Build Succeeds but recovery.img Not Created

**Symptoms**: Build completes but no recovery.img

**Solutions**:
```bash
# Check output directory
ls -la out/target/product/[codename]/

# Look for recovery.img
find out/ -name "recovery.img"

# Check for errors in build log
```

### Out of Memory During Build

**Symptoms**: Build killed, "Killed" message

**Solutions**:
```bash
# Reduce parallel jobs
make -j4 recoveryimage  # Instead of -j$(nproc)

# Clear cache
ccache -C

# Free up RAM
# Close other applications
# Add swap space
```

### Wrong Architecture Error

**Symptoms**: "Target architecture mismatch"

**Solutions**:
1. Verify BoardConfig.mk architecture settings
2. Ensure TARGET_ARCH matches device
3. For 64-bit devices, set both TARGET_ARCH and TARGET_2ND_ARCH

## Boot Issues

### TWRP Won't Boot (Black Screen)

**Symptoms**: Device shows black screen after flashing TWRP

**Causes & Solutions**:

**1. Wrong kernel base address**
```makefile
# In BoardConfig.mk - verify this matches boot.img
BOARD_KERNEL_BASE := 0x80000000  # Check with abootimg
```

**2. Wrong kernel offsets**
```bash
# Extract boot.img and verify
abootimg -i boot.img
# Update BoardConfig.mk to match exactly
```

**3. Incompatible kernel**
```makefile
# Try using prebuilt kernel from stock boot.img
TARGET_PREBUILT_KERNEL := device/.../prebuilt/kernel
```

### TWRP Boots to Bootloader

**Symptoms**: Device restarts to bootloader/fastboot

**Solutions**:
1. **Wrong partition flashed**
   ```bash
   # Verify recovery partition name
   adb shell ls -la /dev/block/platform/*/by-name/ | grep recovery
   
   # Flash to correct partition
   fastboot flash recovery recovery.img
   ```

2. **Recovery image too large**
   ```bash
   # Check image size
   ls -lh recovery.img
   
   # Compare with partition size
   adb shell cat /proc/partitions | grep recovery
   
   # Reduce in BoardConfig.mk
   TW_EXCLUDE_DEFAULT_USB_INIT := true
   TW_EXCLUDE_ENCRYPTED_BACKUPS := true
   ```

### Boot Loop to Recovery

**Symptoms**: Device continuously boots into TWRP

**Solutions**:
1. **Wipe cache and restart**
   - In TWRP: Wipe → Advanced → Cache
   - Reboot → System

2. **Check boot partition**
   ```bash
   # Boot partition might be damaged
   # Reflash stock boot.img
   fastboot flash boot stock_boot.img
   ```

### TWRP Boots Then Crashes

**Symptoms**: TWRP starts then crashes/reboots

**Solutions**:
1. **Check logs**
   ```bash
   adb shell dmesg > dmesg.log
   adb pull /tmp/recovery.log
   ```

2. **Disable problematic features**
   ```makefile
   # In BoardConfig.mk
   TW_EXCLUDE_MTP := true
   TWRP_INCLUDE_LOGCAT := false
   ```

## Partition and Mount Issues

### Cannot Mount /system

**Symptoms**: "Failed to mount /system" error

**Solutions**:

**1. Wrong filesystem type**
```
# In recovery.fstab - try both
/system    ext4    /dev/block/.../system
# or
/system    auto    /dev/block/.../system
```

**2. Wrong partition path**
```bash
# Verify actual path
adb shell ls -la /dev/block/platform/*/by-name/

# Update recovery.fstab with correct path
```

**3. System-as-root (Android 10+)**
```
# Use system_root instead of system
/system_root    ext4    /dev/block/.../system    flags=display="System"
```

### Cannot Mount /data (Encrypted)

**Symptoms**: "Failed to decrypt data" or "Unable to mount data"

**Solutions**:

**1. Enable encryption support**
```makefile
# In BoardConfig.mk
TW_INCLUDE_CRYPTO := true
TW_INCLUDE_CRYPTO_FBE := true  # For Android 7+
TW_INCLUDE_FBE_METADATA_DECRYPT := true  # For Android 10+
```

**2. Add metadata partition (Android 10+)**
```
# In recovery.fstab
/metadata    ext4    /dev/block/.../metadata    flags=display="Metadata";wrappedkey
```

**3. Check encryption flags in fstab**
```
# For FDE (Android 6 and below)
/data    ext4    /dev/block/.../userdata    flags=encryptable=footer;length=-16384

# For FBE (Android 7+)
/data    ext4    /dev/block/.../userdata    flags=fileencryption=ice
```

### Partitions Not Listed in TWRP

**Symptoms**: Some partitions missing in Mount menu

**Solutions**:
1. **Add to recovery.fstab**
   ```
   /persist    ext4    /dev/block/.../persist    flags=display="Persist"
   /firmware   vfat    /dev/block/.../modem      flags=display="Firmware"
   ```

2. **Check device paths exist**
   ```bash
   adb shell ls -la /dev/block/platform/*/by-name/
   ```

### Wrong Partition Size Detected

**Symptoms**: TWRP shows wrong partition sizes

**Solutions**:
```makefile
# In BoardConfig.mk - verify sizes match
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3221225472
BOARD_USERDATAIMAGE_PARTITION_SIZE := 27258650624
BOARD_FLASH_BLOCK_SIZE := 131072
```

## Display and Touch Issues

### Touch Screen Not Working

**Symptoms**: Cannot interact with TWRP screen

**Solutions**:

**1. Wrong TWRP theme**
```makefile
# Try different themes in BoardConfig.mk
TW_THEME := portrait_hdpi
# Try: portrait_mdpi, landscape_hdpi, watch_mdpi
```

**2. Missing touch drivers**
- May need custom kernel with touch drivers
- Check if stock recovery works (touch driver exists)

**3. Wrong screen resolution**
```makefile
# In BoardConfig.mk - verify correct resolution
TW_SCREEN_WIDTH := 1080
TW_SCREEN_HEIGHT := 1920
```

### Display Rotated or Upside Down

**Symptoms**: TWRP displays rotated incorrectly

**Solutions**:
```makefile
# In BoardConfig.mk
BOARD_HAS_FLIPPED_SCREEN := true  # For upside-down
TW_ROTATION := 90  # Rotate 90 degrees
# or
TW_ROTATION := 270  # Rotate 270 degrees
```

### Brightness Too Low/High

**Symptoms**: Screen too dark or too bright

**Solutions**:
```makefile
# In BoardConfig.mk
TW_BRIGHTNESS_PATH := "/sys/class/backlight/panel/brightness"
TW_MAX_BRIGHTNESS := 255
TW_DEFAULT_BRIGHTNESS := 150

# Or try alternative paths:
# /sys/class/leds/lcd-backlight/brightness
# /sys/devices/platform/*/backlight/*/brightness
```

### Display Glitches or Artifacts

**Symptoms**: Visual corruption on screen

**Solutions**:
```makefile
# In BoardConfig.mk - try different graphics backends
TARGET_RECOVERY_PIXEL_FORMAT := "RGBX_8888"
# or
TARGET_RECOVERY_PIXEL_FORMAT := "RGBA_8888"
# or  
TARGET_RECOVERY_PIXEL_FORMAT := "RGB_565"
```

### Screen Timeout/Blank Issues

**Symptoms**: Screen turns off too quickly

**Solutions**:
```makefile
# In BoardConfig.mk
TW_NO_SCREEN_TIMEOUT := true
TW_NO_SCREEN_BLANK := true
```

## Storage Issues

### External SD Card Not Detected

**Symptoms**: MicroSD card not showing in TWRP

**Solutions**:

**1. Add to recovery.fstab**
```
/external_sd    auto    /dev/block/mmcblk1p1    /dev/block/mmcblk1    flags=display="MicroSD";storage;wipeingui;removable
```

**2. Try different device paths**
```bash
# Find SD card device
adb shell ls -la /dev/block/ | grep mmcblk

# Common paths:
/dev/block/mmcblk1p1
/dev/block/mmcblk0p1  # Some devices
```

### USB OTG Not Working

**Symptoms**: USB flash drive not detected

**Solutions**:

**1. Add to recovery.fstab**
```
/usb_otg    auto    /dev/block/sda1    /dev/block/sda    flags=display="USB OTG";storage;wipeingui;removable
```

**2. Enable USB OTG support**
```makefile
# In BoardConfig.mk
TW_HAS_USB_STORAGE := true
```

### MTP Not Working

**Symptoms**: Cannot transfer files via USB to PC

**Solutions**:
```makefile
# In BoardConfig.mk
TW_EXCLUDE_MTP := false  # Ensure MTP is enabled
TW_MTP_DEVICE := "/dev/mtp_usb"

# Add MTP dependencies to device.mk
PRODUCT_PACKAGES += \
    android.hardware.usb@1.0-service
```

### Internal Storage Shows 0MB

**Symptoms**: Data partition shows 0MB

**Solutions**:

**1. Check fstab configuration**
```
/data    ext4    /dev/block/.../userdata    flags=display="Data"
```

**2. Disable forced storage**
```makefile
# Remove if set:
# RECOVERY_SDCARD_ON_DATA := true
```

**3. Format data partition**
- Wipe → Format Data → yes
- **WARNING**: This erases all data!

## Encryption and Decryption Issues

### "Unable to Decrypt FBE Device"

**Symptoms**: Cannot decrypt File-Based Encryption

**Solutions**:
```makefile
# In BoardConfig.mk
TW_INCLUDE_CRYPTO := true
TW_INCLUDE_CRYPTO_FBE := true
TW_INCLUDE_FBE_METADATA_DECRYPT := true

# Add metadata partition to recovery.fstab
/metadata    ext4    /dev/block/.../metadata    flags=wrappedkey
```

### Password/PIN Not Accepted

**Symptoms**: Correct password rejected

**Solutions**:
1. **Try default password first**
   - Default: "default_password"
   - Or try blank password

2. **Disable encryption temporarily**
   ```bash
   # Format data (WARNING: Erases data!)
   # In TWRP: Wipe → Format Data
   ```

3. **Update to latest TWRP**
   - Newer versions have better decryption support

### "Decrypt Failed, Wrong Password"

**Symptoms**: Known correct password fails

**Solutions**:
1. **Wait and retry**
   - First attempt may fail
   - Try 2-3 times

2. **Check keyboard layout**
   - Ensure correct keyboard language
   - Some characters might not work

3. **Remove and re-encrypt**
   - Format data
   - Reinstall ROM
   - Re-encrypt

## Backup and Restore Issues

### Backup Fails - "Not Enough Free Space"

**Solutions**:
1. **Use external SD or USB OTG**
   - TWRP → Backup → Select Storage

2. **Select specific partitions**
   - Don't backup everything
   - Skip cache, dalvik

3. **Enable compression**
   - TWRP → Backup → Enable compression

### Restore Fails - "MD5 Mismatch"

**Solutions**:
1. **Disable MD5 verification**
   - TWRP → Settings → Zip signature verification → Off

2. **Re-download backup**
   - Backup may be corrupted

3. **Regenerate MD5**
   ```bash
   # Delete .md5 file
   # TWRP will regenerate
   ```

### Backup Very Slow

**Solutions**:
1. **Use external storage**
   - External SD is usually faster than internal

2. **Disable compression**
   - Faster but larger backup

3. **Check storage speed**
   - Slow SD card = slow backup

## Flashing Issues

### ZIP Flashing Fails

**Symptoms**: "Error installing zip file"

**Solutions**:
1. **Check ZIP signature**
   - TWRP → Settings → Zip signature verification → Off

2. **Verify ZIP is compatible**
   - Some ZIPs require specific TWRP versions
   - Check ZIP requirements

3. **Re-download ZIP**
   - File may be corrupted

### "Error 7" When Flashing

**Symptoms**: "Error 7: Installation aborted"

**Solutions**:
1. **Device mismatch**
   - ZIP is for different device
   - Verify device codename matches

2. **Edit updater-script**
   ```bash
   # Extract ZIP
   # Edit META-INF/com/google/android/updater-script
   # Remove assert lines (risky!)
   # Repackage ZIP
   ```

### IMG Flashing Fails

**Symptoms**: Cannot flash IMG files

**Solutions**:
1. **Enable flashimg flag**
   ```
   # In recovery.fstab
   /boot    emmc    /dev/block/.../boot    flags=flashimg=1
   ```

2. **Flash via fastboot instead**
   ```bash
   fastboot flash boot boot.img
   ```

## Debug and Logging

### Enable Detailed Logging

```makefile
# In BoardConfig.mk
TWRP_INCLUDE_LOGCAT := true
TARGET_USES_LOGD := true
```

### Access Logs

```bash
# While in TWRP
adb shell cat /tmp/recovery.log > recovery.log
adb shell dmesg > dmesg.log

# Kernel logs
adb shell cat /proc/last_kmsg > last_kmsg.log
```

### Common Log Locations

```
/tmp/recovery.log       # TWRP recovery log
/cache/recovery/log     # Recovery log backup
/proc/last_kmsg         # Kernel crash log
```

## General Troubleshooting Tips

### Always Try First

1. ✅ **Reboot TWRP**
   - Many issues resolve after reboot

2. ✅ **Check logs**
   - Logs tell you what's wrong

3. ✅ **Compare with working device trees**
   - Find similar device on GitHub
   - Check XDA forums

4. ✅ **Start simple**
   - Minimal BoardConfig.mk first
   - Add features gradually

5. ✅ **Test incrementally**
   - Change one thing at a time
   - Test after each change

### Getting Help

When asking for help, provide:
- Device model and codename
- Android version
- TWRP version or build source
- Exact error message
- What you've tried
- Logs (recovery.log, dmesg)
- BoardConfig.mk and recovery.fstab

## Resources

- [TWRP FAQ](https://twrp.me/faq/)
- [XDA TWRP Forums](https://forum.xda-developers.com/f/twrp-teamwin-recovery-project.564/)
- [TWRP GitHub Issues](https://github.com/TeamWin/Team-Win-Recovery-Project/issues)
- [TOOLS_AND_RESOURCES.md](TOOLS_AND_RESOURCES.md)

---

**Remember**: Most issues are configuration problems. Double-check all settings before assuming it's a code bug!
