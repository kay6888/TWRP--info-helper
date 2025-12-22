# Scripts Directory

This directory contains automation scripts to help with TWRP building and device information extraction.

## Available Scripts

### extract_device_info.sh

Comprehensive device information extraction script that gathers all necessary data via ADB.

**Purpose**: Automatically collect all device information needed for TWRP building.

**Requirements**:
- ADB installed and in PATH
- Android device with USB debugging enabled
- Device connected via USB and authorized

**Usage**:

```bash
# Make executable (first time only)
chmod +x scripts/extract_device_info.sh

# Run the script
./scripts/extract_device_info.sh

# Output will be saved to: device_info_[codename].txt
```

**Information Collected**:
- Device codename, brand, model, manufacturer
- Android version and API level
- CPU architecture and ABI
- Platform/chipset information
- Kernel version and command line
- Screen resolution and density
- Partition layout and sizes
- Block device paths
- Build fingerprint
- SELinux status
- Fstab information (if accessible)

**Output**:
The script creates a comprehensive text file named `device_info_[codename].txt` containing all extracted information, formatted for easy reading and reference when building TWRP.

**Next Steps After Running**:
1. Review the generated text file
2. Extract boot.img from your device
3. Analyze boot.img using `abootimg -i boot.img`
4. Use the information to configure your device tree
5. Follow [TWRP_BUILDING_GUIDE.md](../docs/TWRP_BUILDING_GUIDE.md)

**Troubleshooting**:

If script fails to connect:
```bash
# Check ADB connection
adb devices

# Kill and restart ADB server
adb kill-server
adb start-server

# Check device authorization
adb devices
# Should show: [serial]    device
```

If some information shows "N/A":
- Some properties may not be available on all devices
- Try running with root access if available
- Some system files may require root to access

## Installing ADB

### Ubuntu/Debian
```bash
sudo apt update
sudo apt install adb
```

### macOS
```bash
brew install android-platform-tools
```

### Windows
Download Android SDK Platform Tools from:
https://developer.android.com/studio/releases/platform-tools

## Using the Scripts

### Basic Workflow

1. **Enable USB Debugging on your device**:
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"

2. **Connect device and authorize**:
   ```bash
   adb devices
   # Accept authorization prompt on device
   ```

3. **Run extraction script**:
   ```bash
   ./scripts/extract_device_info.sh
   ```

4. **Review output**:
   ```bash
   cat device_info_*.txt
   ```

5. **Use information for TWRP building**:
   - See [TWRP_BUILDING_GUIDE.md](../docs/TWRP_BUILDING_GUIDE.md)
   - See [HOVATEK_BUILDER_GUIDE.md](../docs/HOVATEK_BUILDER_GUIDE.md)

## Contributing

Have an idea for a useful script? Contributions are welcome!

Scripts that would be useful:
- Boot image analyzer (wrapper for abootimg/AIK)
- Device tree generator (from extracted info)
- Partition size calculator
- Fstab generator
- Build environment checker

## Resources

- [ADB Documentation](https://developer.android.com/studio/command-line/adb)
- [DEVICE_INFO_EXTRACTION.md](../docs/DEVICE_INFO_EXTRACTION.md)
- [TOOLS_AND_RESOURCES.md](../docs/TOOLS_AND_RESOURCES.md)

---

**Note**: Always verify extracted information manually before using it for TWRP builds. The scripts are helpers, not replacements for understanding your device!
