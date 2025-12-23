# Tools and Resources

## Table of Contents
- [Essential Tools](#essential-tools)
- [Build Environment Tools](#build-environment-tools)
- [Device Information Tools](#device-information-tools)
- [Boot Image Tools](#boot-image-tools)
- [Development Tools](#development-tools)
- [Online Resources](#online-resources)
- [Community Resources](#community-resources)

## Essential Tools

### ADB and Fastboot

**Android Debug Bridge (ADB)** and **Fastboot** are essential for device interaction.

**Installation**:

```bash
# Ubuntu/Debian
sudo apt install adb fastboot

# Mac (using Homebrew)
brew install android-platform-tools

# Windows
# Download from: https://developer.android.com/studio/releases/platform-tools
```

**Basic Commands**:
```bash
# ADB
adb devices                    # List connected devices
adb shell                      # Open shell on device
adb push file /sdcard/        # Upload file to device
adb pull /sdcard/file         # Download file from device
adb reboot recovery           # Reboot to recovery
adb logcat                    # View device logs

# Fastboot
fastboot devices              # List devices in fastboot mode
fastboot flash recovery recovery.img  # Flash recovery
fastboot boot recovery.img    # Boot recovery without flashing
fastboot getvar all          # Get device variables
fastboot reboot              # Reboot device
```

**Resources**:
- [Official ADB Documentation](https://developer.android.com/studio/command-line/adb)
- [Download Platform Tools](https://developer.android.com/studio/releases/platform-tools)

### Git

Version control system for managing code.

**Installation**:
```bash
# Ubuntu/Debian
sudo apt install git

# Mac
brew install git

# Windows
# Download from: https://git-scm.com/
```

**Basic Commands**:
```bash
git clone [url]              # Clone repository
git add .                    # Stage changes
git commit -m "message"      # Commit changes
git push                     # Push to remote
git pull                     # Pull from remote
```

**Resources**:
- [Git Documentation](https://git-scm.com/doc)
- [GitHub Learning](https://skills.github.com/)

### Repo Tool

Google's tool for managing multiple Git repositories.

**Installation**:
```bash
mkdir ~/bin
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
export PATH=~/bin:$PATH
```

**Basic Commands**:
```bash
repo init -u [manifest-url] -b [branch]
repo sync -j$(nproc --all)
repo forall -c "git checkout [branch]"
```

**Resources**:
- [Repo Documentation](https://source.android.com/setup/develop/repo)

## Build Environment Tools

### Android SDK

Software Development Kit for Android.

**Installation**:
```bash
# Via Android Studio (recommended)
# Download from: https://developer.android.com/studio

# Or command-line tools only
wget https://dl.google.com/android/repository/commandlinetools-linux-latest.zip
unzip commandlinetools-linux-latest.zip
```

**Resources**:
- [Android Studio Download](https://developer.android.com/studio)
- [SDK Tools Documentation](https://developer.android.com/studio/command-line)

### Build Dependencies (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install -y bc bison build-essential ccache curl flex \
    g++-multilib gcc-multilib git gnupg gperf imagemagick lib32ncurses5-dev \
    lib32readline-dev lib32z1-dev liblz4-tool libncurses5 libncurses5-dev \
    libsdl1.2-dev libssl-dev libxml2 libxml2-utils lzop pngcrush \
    rsync schedtool squashfs-tools xsltproc zip zlib1g-dev \
    openjdk-8-jdk python python3 python-pip python3-pip
```

### Java Development Kit (JDK)

```bash
# JDK 8 (for Android 9 and below)
sudo apt install openjdk-8-jdk

# JDK 11 (for Android 10+)
sudo apt install openjdk-11-jdk

# Set default
sudo update-alternatives --config java
sudo update-alternatives --config javac
```

## Device Information Tools

### Terminal-Based Tools

#### AIDA64 (Android App)

**Platform**: Android  
**Cost**: Free (with ads)  
**Features**:
- Comprehensive hardware/software info
- CPU, GPU, memory details
- Sensor information
- Battery stats

**Download**: [Google Play Store](https://play.google.com/store/apps/details?id=com.finalwire.aida64)

#### CPU-Z (Android App)

**Platform**: Android  
**Cost**: Free  
**Features**:
- CPU information
- Device details
- System information
- Battery status

**Download**: [Google Play Store](https://play.google.com/store/apps/details?id=com.cpuid.cpu_z)

#### DevCheck (Android App)

**Platform**: Android  
**Cost**: Free  
**Features**:
- Hardware information
- Dashboard view
- Real-time monitoring
- Export capabilities

**Download**: [Google Play Store](https://play.google.com/store/apps/details?id=flar2.devcheck)

#### Termux (Android Terminal)

**Platform**: Android  
**Cost**: Free  
**Features**:
- Full Linux terminal on Android
- Package management
- Run scripts and commands
- No root required

**Download**: [F-Droid](https://f-droid.org/packages/com.termux/)

**Usage**:
```bash
# In Termux
getprop                      # View all properties
cat /proc/version           # Kernel version
cat /proc/cpuinfo          # CPU info
cat /proc/partitions       # Partition list
```

## Boot Image Tools

### abootimg

Extract and analyze boot images.

**Installation**:
```bash
sudo apt install abootimg
```

**Usage**:
```bash
# Extract info
abootimg -i boot.img

# Extract components
abootimg -x boot.img

# Create boot image
abootimg --create newboot.img -f bootimg.cfg -k zImage -r initrd.img
```

**Resources**:
- [abootimg on GitHub](https://github.com/ggrandou/abootimg)

### Android Image Kitchen (AIK)

Comprehensive boot/recovery image tool.

**Installation**:
```bash
wget https://github.com/osm0sis/Android-Image-Kitchen/archive/refs/heads/master.zip
unzip master.zip
cd Android-Image-Kitchen-master
```

**Usage**:
```bash
# Unpack
./unpackimg.sh boot.img

# Modify files in ramdisk/ and split_img/

# Repack
./repackimg.sh

# Cleanup
./cleanup.sh
```

**Resources**:
- [AIK on GitHub](https://github.com/osm0sis/Android-Image-Kitchen)
- [XDA Thread](https://forum.xda-developers.com/t/tool-android-image-kitchen-unpack-repack-kernel-ramdisk-win-android-linux-mac.2073775/)

### unmkbootimg

Alternative boot image unpacker.

**Installation**:
```bash
git clone https://github.com/xiaolu/unmkbootimg
cd unmkbootimg
make
sudo make install
```

**Usage**:
```bash
unmkbootimg -i boot.img
```

### mkbootimg / unpackbootimg

AOSP boot image tools.

**Installation**:
```bash
git clone https://android.googlesource.com/platform/system/tools/mkbootimg
cd mkbootimg
python setup.py install
```

**Usage**:
```bash
# Unpack
unpackbootimg -i boot.img

# Pack
mkbootimg --kernel zImage --ramdisk ramdisk.gz --base 0x80000000 \
    --pagesize 2048 -o newboot.img
```

## Development Tools

### Android Studio

Full-featured IDE for Android development.

**Download**: [https://developer.android.com/studio](https://developer.android.com/studio)

**Features**:
- Code editing with IntelliSense
- Build automation
- Debugging tools
- AVD Manager (emulators)
- SDK Manager

### Visual Studio Code

Lightweight code editor.

**Download**: [https://code.visualstudio.com/](https://code.visualstudio.com/)

**Useful Extensions**:
- C/C++
- Android
- Makefile Tools
- Git integration

### Sublime Text / Vim / Nano

Terminal-based editors.

```bash
# Nano (simplest)
sudo apt install nano

# Vim (powerful)
sudo apt install vim

# Sublime Text
wget -qO - https://download.sublimetext.com/sublimehq-pub.gpg | sudo apt-key add -
sudo apt install sublime-text
```

## Online Resources

### Official Documentation

- **TWRP Official Site**: https://twrp.me/
- **TWRP Device List**: https://twrp.me/Devices/
- **TWRP FAQ**: https://twrp.me/faq/
- **Android Source**: https://source.android.com/
- **Android Developer Docs**: https://developer.android.com/

### TWRP Source Code

- **TWRP GitHub**: https://github.com/TeamWin/Team-Win-Recovery-Project
- **Minimal TWRP Manifest**: https://github.com/minimal-manifest-twrp
- **TWRP Device Trees**: https://github.com/TeamWin

### Build Guides

- **TWRP Official Guide**: https://github.com/TeamWin/Team-Win-Recovery-Project
- **XDA University**: https://www.xda-developers.com/xda-university/
- **LineageOS Wiki**: https://wiki.lineageos.org/devices/

### Online Builders

- **Hovatek TWRP Builder**: https://www.hovatek.com/twrpbuilder/
- **TeamWin Builder**: Check TWRP official site for availability

### Device Trees Repository

- **GitHub Search**: Search "android_device_[manufacturer]_[codename]"
- **Example**: https://github.com/search?q=android_device_samsung

### Tools Downloads

- **Android SDK Platform Tools**: https://developer.android.com/studio/releases/platform-tools
- **Android Image Kitchen**: https://github.com/osm0sis/Android-Image-Kitchen
- **Magisk**: https://github.com/topjohnwu/Magisk

## Community Resources

### Forums

#### XDA Developers
**URL**: https://forum.xda-developers.com/

**Key Sections**:
- Device-specific forums
- TWRP General: https://forum.xda-developers.com/f/twrp-teamwin-recovery-project.564/
- Android Software Development

**Usage**: Search for your device to find guides and help

#### Hovatek Forum
**URL**: https://www.hovatek.com/forum/

**Focus**:
- TWRP building
- Android customization
- Device support

#### Reddit

- **r/AndroidQuestions**: https://reddit.com/r/AndroidQuestions
- **r/TWRP**: Device recovery discussions
- **r/Android**: General Android news and help
- **r/LineageOS**: Custom ROM building

### Telegram Groups

Search Telegram for:
- "TWRP [your device]"
- "Android Development"
- "[Device manufacturer] Development"

### Discord Servers

- LineageOS Discord
- Android Building Communities
- Device-specific servers

### YouTube Channels

Search for:
- "TWRP building tutorial"
- "Android device tree"
- "Compile TWRP"
- "[Your device] TWRP"

## Utilities and Scripts

### Device Info Extraction Script

This repository includes:
```bash
scripts/extract_device_info.sh
```

See [DEVICE_INFO_EXTRACTION.md](DEVICE_INFO_EXTRACTION.md)

### Partition Size Calculator

```python
#!/usr/bin/env python3
# Calculate partition size from blocks
blocks = int(input("Enter blocks from /proc/partitions: "))
size_bytes = blocks * 1024
size_mb = size_bytes / 1048576
print(f"Size in bytes: {size_bytes}")
print(f"Size in MB: {size_mb:.2f}")
print(f"For BoardConfig.mk: {size_bytes}")
```

### Hex to Decimal Converter

```python
#!/usr/bin/env python3
# Convert hex addresses to decimal
hex_value = input("Enter hex value (e.g., 0x80000000): ")
decimal_value = int(hex_value, 16)
print(f"Decimal: {decimal_value}")
print(f"Hex: {hex_value}")
```

## Quick Command Reference

### Device Information
```bash
adb shell getprop > all_props.txt
adb shell cat /proc/partitions > partitions.txt
adb shell cat /proc/version > kernel_version.txt
adb shell ls -la /dev/block/platform/*/by-name/ > block_devices.txt
```

### Boot Image Analysis
```bash
abootimg -i boot.img > boot_info.txt
./unpackimg.sh boot.img
cat split_img/* > boot_details.txt
```

### Build Commands
```bash
repo init -u https://github.com/minimal-manifest-twrp/platform_manifest_twrp_omni.git -b twrp-9.0
repo sync -j$(nproc --all)
source build/envsetup.sh
lunch omni_[codename]-eng
mka recoveryimage
```

## Recommended Tool Set

### Beginner
- ✅ ADB/Fastboot
- ✅ TWRP Device Info Collector (this app)
- ✅ Hovatek Online TWRP Builder
- ✅ Android Image Kitchen

### Intermediate
- ✅ All beginner tools
- ✅ Git and Repo
- ✅ abootimg
- ✅ Text editor (VS Code/Sublime)
- ✅ Build environment setup

### Advanced
- ✅ All intermediate tools
- ✅ Android Studio
- ✅ Custom kernel building tools
- ✅ SELinux policy tools
- ✅ Debuggers (GDB, LLDB)

## Learning Path

1. **Start Here**:
   - Understand what TWRP is
   - Extract device information
   - Use Hovatek Online Builder

2. **Next Steps**:
   - Learn about device trees
   - Understand partition layout
   - Study similar device trees

3. **Advanced**:
   - Set up build environment
   - Build TWRP locally
   - Customize and debug

## Getting Help

When seeking help, have ready:
- Device information (use provided app)
- Boot image analysis (abootimg output)
- Error logs
- What you've tried
- Your device tree files

Where to ask:
1. XDA device forum
2. Hovatek forum
3. Reddit communities
4. Telegram groups

## Contributing

Help improve this documentation:
- Report errors or outdated info
- Add missing tools
- Share useful resources
- Contribute scripts

---

**Remember**: The right tools make the job easier. Start with basics and expand your toolkit as you learn!
