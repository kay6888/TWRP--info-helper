# Reddit Posts for TWRP Info Helper

This file contains various Reddit posts optimized for different subreddits.

---

## POST 1: For r/Android (Main Community)

**Title:** [DEV] I made a free app that auto-collects device info for building TWRP recovery

**Body:**

Hey r/Android! 👋

I built a free tool that might interest some of you - especially if you've ever tried building custom TWRP recovery.

## What is it?

**TWRP Device Info Collector** - An open-source Android app that automatically gathers ALL the device specifications you need for building TWRP using online builders like Hovatek.

## Why did I make this?

Manually collecting device info for TWRP building is tedious. You need codename, screen resolution, CPU architecture, kernel version, build fingerprint... the list goes on. This app does it all in one tap.

## Key Features:

✅ One-tap device info collection  
✅ Exports to text file in Downloads  
✅ Detects 30+ device specifications  
✅ Shows root/SELinux/bootloader status  
✅ **No ads, no tracking, 100% free**  
✅ Works completely offline  
✅ Open source (MIT license)  

## Download:

📥 [GitHub Releases](https://github.com/kay6888/TWRP--info-helper/releases/latest)  
💻 [Source Code](https://github.com/kay6888/TWRP--info-helper)

## Privacy:

All data stays on YOUR device. No internet required. No analytics. No sketchy permissions. Just a simple tool that does its job.

## Supports:

Android 5.0 (Lollipop) through Android 14+

---

Would love to hear your feedback! Let me know if you try it out.

---

## POST 2: For r/androiddev (Developer Community)

**Title:** Open Source Tool: TWRP Device Info Collector - Made with vanilla Android SDK

**Body:**

Hey fellow Android devs! 👨‍💻

I recently published an open-source utility app and thought I'd share it here for feedback and contributions.

**Project:** TWRP Device Info Collector  
**License:** MIT  
**Tech Stack:** Pure Android SDK (no fancy frameworks)  
**Min SDK:** 21 | **Target SDK:** 34

## What it does:

Collects comprehensive device information using Android Build APIs, Display metrics, System properties, and Runtime data - then exports to a formatted text file.

Primary use case: Gathering device specs for TWRP recovery building.

## Technical Highlights:

- Uses `Build` class for hardware/software info
- `DisplayMetrics` for screen specifications  
- Reflection-free property access
- Permission-aware storage (handles SDK 29+ scoped storage)
- ProGuard/R8 optimized release builds
- Signed APK with proper keystore management

## Code Quality:

✅ Clean architecture (Activity → Logic → File I/O)  
✅ No external dependencies (except AndroidX basics)  
✅ Proper error handling  
✅ Material Design 3 UI  
✅ Dark mode support

## Why I'm sharing:

1. Could use code review from experienced devs
2. Open to contributions (especially UI improvements)
3. Might help someone learning Android development
4. Built with best practices - good reference for beginners

## Links:

📦 [GitHub Repository](https://github.com/kay6888/TWRP--info-helper)  
📥 [Latest Release APK](https://github.com/kay6888/TWRP--info-helper/releases/latest)  
📖 [Documentation](https://github.com/kay6888/TWRP--info-helper#readme)

## Looking for:

- Code reviews
- Feature suggestions  
- UI/UX improvements
- Bug reports
- Contributors!

**Current version:** 2.2-beta

Feel free to fork, contribute, or use it as a learning resource. All feedback welcome! 🚀

---

## POST 3: For r/twrp (TWRP Community)

**Title:** Made an app to auto-collect device info for Hovatek TWRP Builder

**Body:**

Hey TWRP community! 🛠️

Tired of manually gathering all your device specs for TWRP building? I made a tool to help!

## TWRP Device Info Collector

Free Android app that **automatically collects ALL device information** needed for online TWRP builders (Hovatek, etc.)

### What it collects:

📱 Device codename, brand, model, manufacturer  
🤖 Android version, API level, build fingerprint  
📐 Screen resolution & density (critical for TWRP UI!)  
⚙️ CPU architecture (ABI)  
🔧 Kernel version  
🔐 Root status, SELinux mode, bootloader lock status  
💾 Partition info & mount points  
📊 Memory & storage details  

### Output:

Clean text file saved to Downloads: `twrp-builder-{codename}.txt`

Copy-paste ready for Hovatek builder! ✅

### Download:

📥 **Direct APK:** [GitHub Releases](https://github.com/kay6888/TWRP--info-helper/releases/latest)  
💻 **Source Code:** [GitHub Repo](https://github.com/kay6888/TWRP--info-helper)

### Privacy:

- ✅ 100% offline
- ✅ No tracking
- ✅ No ads
- ✅ Open source

### Compatible with:

- Android 5.0+ (API 21 through 34)
- All architectures (ARM, ARM64, x86)
- Phones & tablets

---

**Tested with Hovatek TWRP Builder** ✅

Hope this saves you some time! Let me know if you have suggestions for improvements.

Building TWRP just got easier! 🎉

---

## POST 4: For r/LineageOS (Custom ROM Community)

**Title:** Tool: Automatically collect device specs for TWRP/ROM development

**Body:**

Hey LineageOS community! 👋

For those of you building TWRP or working on device trees, I made a utility that might help.

## TWRP Device Info Collector

Open-source app that auto-collects device specifications needed for:
- Building TWRP recovery
- Creating device trees
- ROM development
- Device testing

### Key Info It Gathers:

- Full build fingerprint
- Device codename & hardware identifiers
- Kernel version & architecture
- Partition layouts
- Screen specs (for recovery UI)
- Security status (root, SELinux, bootloader)

### Why It's Useful:

Instead of running multiple ADB commands or checking various settings, just tap once and get a complete device profile exported to a text file.

### Download:

GitHub: https://github.com/kay6888/TWRP--info-helper/releases

### Tech Details:

- Supports Android 5.0 → 14+
- Zero telemetry
- MIT licensed
- No dependencies beyond AndroidX

---

Feedback welcome! If you use it for LineageOS development, let me know how it works out.

---

## POST 5: For r/androidapps (App Discovery)

**Title:** TWRP Device Info Collector - Know your device inside out [FREE][OPEN SOURCE]

**Body:**

**App Name:** TWRP Device Info Collector  
**Price:** FREE (No ads, no IAPs)  
**Privacy:** No tracking, no internet required  
**License:** Open Source (MIT)

## What is it?

An app that shows you EVERYTHING about your Android device in one place.

## What makes it special?

Unlike other device info apps, this one is specifically designed to give you the **exact information needed for building custom TWRP recovery**, but it's useful for anyone who wants to know their device specs!

## What you'll see:

📱 Device codename, model, brand  
🤖 Android version & API level  
📐 Exact screen resolution & DPI  
⚙️ CPU architecture details  
🔧 Kernel version  
🔐 Security status (root, SELinux, bootloader)  
💾 Storage & memory info  
📊 Build fingerprint  
...and 20+ more specs!

## Why choose this over other device info apps?

✅ **Export to file** - Save all info as text  
✅ **Actually useful** - Info formatted for TWRP building  
✅ **Completely private** - No internet, no tracking  
✅ **No bloat** - Just 3.3 MB  
✅ **Open source** - See exactly what it does  
✅ **Free forever** - No premium upsells  

## Perfect for:

- Android enthusiasts
- Developers
- TWRP builders
- Anyone curious about their device
- Troubleshooting support requests

## Download:

📥 [GitHub Releases](https://github.com/kay6888/TWRP--info-helper/releases/latest)  
💻 [Source Code](https://github.com/kay6888/TWRP--info-helper)

## Compatibility:

Android 5.0 (Lollipop) through Android 14+

---

Give it a try and let me know what you think! Suggestions welcome. 🚀

---

## BONUS: XDA Developers Forum Post

**Thread Title:** [APP][5.0+][OPEN SOURCE] TWRP Device Info Collector - Auto-gather device specs

**Post Body:**

# TWRP Device Info Collector

**Automatically collect all device information for building TWRP recovery**

---

## 📱 ABOUT

TWRP Device Info Collector is a free, open-source Android application that automatically gathers comprehensive device specifications needed for building custom TWRP recovery using online builders like Hovatek.

---

## ✨ FEATURES

• **One-tap information collection** - No manual input needed  
• **30+ device specifications** - Everything TWRP builders need  
• **Export to text file** - Saved to Downloads folder  
• **Root status detection** - Checks su binaries  
• **SELinux mode detection** - Security configuration  
• **Bootloader status** - Lock/unlock state  
• **Screen specifications** - Critical for TWRP UI scaling  
• **Kernel information** - Version and build details  
• **CPU architecture** - ABI compatibility info  
• **Partition information** - System paths and mount points  

---

## 📥 DOWNLOAD

**Latest Version:** v2.2-beta  
**Release Date:** December 24, 2025

**Direct Download (APK):**  
https://github.com/kay6888/TWRP--info-helper/releases/download/v2.2-beta/twrp-info-helper-v2.2-beta.apk

**Source Code:**  
https://github.com/kay6888/TWRP--info-helper

---

## 🔧 REQUIREMENTS

• **Android Version:** 5.0 (Lollipop) or higher  
• **Permissions:** Storage (for saving output file)  
• **Root:** Not required (but detects if present)  
• **Architecture:** ARM, ARM64, x86, x86_64  

---

## 📋 INSTALLATION

1. Download APK from link above
2. Enable "Unknown Sources" in Settings → Security
3. Install APK
4. Launch "TWRP Info Collector"
5. Tap to collect device info
6. Save to Downloads folder

---

## 🎯 USE CASES

✅ Building TWRP with Hovatek Online Builder  
✅ Creating device trees for custom ROMs  
✅ Documenting device specifications  
✅ Troubleshooting hardware/software issues  
✅ Learning about Android system internals  

---

## 🔒 PRIVACY & SECURITY

✅ **No internet connection required**  
✅ **No analytics or tracking**  
✅ **No ads**  
✅ **All data stays on device**  
✅ **Open source - verify the code yourself**  
✅ **MIT License**  

---

## 📸 SCREENSHOTS

[To be added - capture from running app]

---

## 🛠️ DEVELOPMENT

**Tech Stack:**
- Pure Android SDK
- Material Design 3
- AndroidX libraries
- ProGuard/R8 optimized

**Build from source:**
```bash
git clone https://github.com/kay6888/TWRP--info-helper
cd TWRP--info-helper
./gradlew assembleRelease
```

---

## 📜 CHANGELOG

**v2.2-beta (Current)**
- Initial public beta release
- Enhanced root detection
- SELinux status detection
- Bootloader lock status
- System partition mount detection
- Material Design 3 UI
- Dark mode support
- Android 14 support

---

## 🤝 CONTRIBUTIONS

Contributions welcome! The project is open source.

**GitHub:** https://github.com/kay6888/TWRP--info-helper  
**Issues:** https://github.com/kay6888/TWRP--info-helper/issues  
**License:** MIT

---

## 💬 SUPPORT

Found a bug? Have a suggestion? Create an issue on GitHub!

**Response time:** Usually within 24 hours

---

## 🔗 RELATED LINKS

• [Hovatek TWRP Builder](https://www.hovatek.com/twrpbuilder/)  
• [Official TWRP](https://twrp.me/)  
• [XDA TWRP Forum](https://forum.xda-developers.com/c/team-win-recovery-project-twrp.204/)  

---

**If this app helped you build TWRP, consider leaving feedback or starring the GitHub repo! ⭐**

---

**Tested Devices:**
[Add your device to the list if it works!]

---

*Made with ❤️ for the Android community*
