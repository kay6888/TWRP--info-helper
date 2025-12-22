# Sample Device Tree for TWRP

This is an example device tree showing the basic structure and files needed for building TWRP recovery.

## File Descriptions

### AndroidProducts.mk

Defines which product makefiles are available for this device. This tells the build system about `omni_sample.mk`.

**Key Points**:
- `PRODUCT_MAKEFILES` lists available product configurations
- `COMMON_LUNCH_CHOICES` defines lunch options
- Replace `sample` with your device codename

### BoardConfig.mk

The most important file - contains all hardware-specific configuration.

**Sections**:

1. **Platform Configuration**
   - `TARGET_BOARD_PLATFORM`: Chipset platform (e.g., msm8996, exynos8890, mt6765)
   - `TARGET_BOOTLOADER_BOARD_NAME`: Bootloader board name

2. **Architecture Settings**
   - ARM64 devices need both 64-bit and 32-bit configuration
   - ARM devices only need 32-bit configuration
   - Must match your device exactly

3. **Kernel Configuration**
   - Prebuilt kernel path OR kernel source configuration
   - Kernel command line (get from boot.img)
   - Base address and offsets (get from boot.img using abootimg)
   - Page size (usually 2048 or 4096)

4. **Partition Sizes**
   - Get from `adb shell cat /proc/partitions`
   - Calculate: blocks × 1024 = bytes
   - Must be accurate for successful builds

5. **TWRP Specific Flags**
   - Theme selection
   - Brightness control
   - Storage configuration
   - Encryption support
   - Features to include/exclude

**How to Customize**:
1. Extract boot.img from your device
2. Use `abootimg -i boot.img` to get kernel parameters
3. Update all kernel-related values
4. Get partition sizes from `/proc/partitions`
5. Set appropriate TWRP theme for your screen
6. Enable/disable features as needed

### device.mk

Contains device-specific product configuration.

**Key Elements**:
- Files to copy to recovery (kernel, fstab, timezone data)
- Product packages to include
- Device properties
- Recovery-specific packages

**Customization**:
- Update device/manufacturer/codename paths
- Add any additional files needed for recovery
- Include necessary libraries

### omni_sample.mk

Product configuration file that ties everything together.

**Purpose**:
- Inherits TWRP/Omni common configuration
- Inherits device-specific configuration
- Sets device identification values

**Customization**:
- Replace `sample` with your device codename
- Update brand, model, manufacturer
- Set build fingerprint (optional but recommended)

### recovery.fstab

Defines how TWRP mounts and manages partitions.

**Critical Sections**:

1. **Boot/Recovery Partitions**
   - Raw EMMC partitions
   - Must have `flashimg=1` flag to allow IMG flashing

2. **System/Vendor Partitions**
   - Both filesystem mount and image flash entries
   - Essential for ROM installation

3. **Data Partition**
   - Encryption flags are crucial
   - Use `encryptable=footer` for FDE (Android 6-)
   - Use `fileencryption=ice` for FBE (Android 7+)

4. **Storage**
   - External SD and USB OTG for backup storage
   - Must have `storage` and `removable` flags

**Finding Partition Paths**:
```bash
# Run on device
adb shell ls -la /dev/block/platform/*/by-name/
# or
adb shell ls -la /dev/block/bootdevice/by-name/
```

**Customization**:
- Update all device paths to match your device
- Add/remove partitions as needed
- Set correct encryption type
- Verify external storage paths

## How to Use This Example

### Step 1: Copy and Rename

```bash
# Create your device tree directory
mkdir -p device/[manufacturer]/[codename]

# Copy these files
cp examples/device_tree_sample/* device/[manufacturer]/[codename]/
```

### Step 2: Update File Contents

In all files, replace:
- `sample` → your device codename
- `Sample` → your manufacturer/brand
- `device/sample/sample` → `device/[manufacturer]/[codename]`

### Step 3: Gather Device Information

```bash
# Get device info
adb shell getprop ro.product.device          # codename
adb shell getprop ro.product.manufacturer    # manufacturer
adb shell getprop ro.board.platform          # platform
adb shell getprop ro.product.cpu.abi         # architecture

# Extract boot image
adb shell su -c "dd if=/dev/block/bootdevice/by-name/boot of=/sdcard/boot.img"
adb pull /sdcard/boot.img

# Analyze boot image
abootimg -i boot.img
```

### Step 4: Update BoardConfig.mk

Update these critical values:
- `TARGET_BOARD_PLATFORM`
- Architecture settings
- Kernel base and offsets (from abootimg)
- Kernel command line (from abootimg)
- Partition sizes (from `/proc/partitions`)
- TWRP theme (based on screen resolution)

### Step 5: Update recovery.fstab

```bash
# Get partition paths
adb shell ls -la /dev/block/bootdevice/by-name/

# Update fstab with correct paths
# Set correct encryption type
# Verify external storage paths
```

### Step 6: Add Prebuilt Kernel

```bash
# Create prebuilt directory
mkdir -p device/[manufacturer]/[codename]/prebuilt

# Extract kernel from boot.img
abootimg -x boot.img
cp zImage device/[manufacturer]/[codename]/prebuilt/kernel
```

### Step 7: Test Build

```bash
# Initialize build environment
cd ~/twrp
source build/envsetup.sh

# Lunch your device
lunch omni_[codename]-eng

# Build
mka recoveryimage
```

## Common Customizations

### Different Architecture (ARM instead of ARM64)

Remove 2nd architecture section and update:
```makefile
TARGET_ARCH := arm
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_CPU_VARIANT := cortex-a53
```

### Building Kernel from Source

Replace prebuilt kernel lines with:
```makefile
TARGET_KERNEL_SOURCE := kernel/[manufacturer]/[codename]
TARGET_KERNEL_CONFIG := [codename]_defconfig
TARGET_KERNEL_ARCH := arm64
TARGET_KERNEL_HEADER_ARCH := arm64
```

### System-as-Root (Android 10+)

Add to BoardConfig.mk:
```makefile
BOARD_BUILD_SYSTEM_ROOT_IMAGE := true
BOARD_SUPPRESS_SECURE_ERASE := true
```

Update recovery.fstab:
```
/system_root    ext4    /dev/block/bootdevice/by-name/system    flags=display="System"
```

### A/B Partition Scheme

Add to fstab entries:
```makefile
# In BoardConfig.mk
AB_OTA_UPDATER := true

# In recovery.fstab, add to partitions:
flags=slotselect
```

## Validation Checklist

Before building, verify:

- [ ] All `sample` references replaced with your codename
- [ ] Platform matches your device chipset
- [ ] Architecture is correct (arm vs arm64)
- [ ] Kernel base address matches boot.img
- [ ] All kernel offsets match boot.img
- [ ] Kernel command line matches boot.img
- [ ] Partition sizes calculated correctly
- [ ] All partition paths exist on device
- [ ] TWRP theme matches screen resolution
- [ ] Encryption type is correct
- [ ] Prebuilt kernel exists at specified path
- [ ] recovery.fstab paths are correct

## Troubleshooting

### Build Fails

- Check all paths are correct
- Verify prebuilt kernel exists
- Run `make clean` and try again

### TWRP Won't Boot

- Verify kernel base and offsets
- Check kernel command line
- Ensure partition sizes are correct

### Touch Doesn't Work

- Try different TW_THEME
- Verify screen resolution in wm size

### Can't Mount Partitions

- Check recovery.fstab paths
- Verify encryption flags
- Test partition paths with adb

## Additional Resources

- [TWRP Building Guide](../../docs/TWRP_BUILDING_GUIDE.md)
- [Device Info Extraction](../../docs/DEVICE_INFO_EXTRACTION.md)
- [Kernel Info Guide](../../docs/KERNEL_INFO.md)
- [Recovery Fstab Guide](../../docs/RECOVERY_FSTAB.md)
- [Troubleshooting Guide](../../docs/TROUBLESHOOTING.md)

## Real Device Tree Examples

Search GitHub for similar devices:
```
android_device_[manufacturer]_[codename]
```

Examples:
- https://github.com/TeamWin/android_device_samsung_herolte
- https://github.com/TeamWin/android_device_xiaomi_lavender
- https://github.com/TeamWin/android_device_oneplus_fajita

---

**Remember**: This is a template. Every device is unique - you must customize all values to match YOUR specific device!
