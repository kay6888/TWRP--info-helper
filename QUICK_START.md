# Quick Start Guide - For Non-Technical Users

## What Is This?

This is an Android app that collects information about your phone. You need this information to build a custom TWRP recovery for your device using the Hovatek Online TWRP Builder.

---

## 🎯 Simple 3-Step Process

### Step 1: Get the APK File

You need to build the APK file first. Choose ONE of these options:

**Option A: Have Someone Build It For You (EASIEST)**
- Share this GitHub link with a friend who knows Android development
- They can build the APK and send it to you
- Skip to Step 2

**Option B: Use Android Studio (If You Want To Learn)**
1. Download Android Studio: https://developer.android.com/studio
2. Install Android Studio (follow their installation guide)
3. Open Android Studio
4. Click "Get from VCS" or "Open"
5. Enter this repository URL or select the downloaded folder
6. Wait for Gradle to sync (this downloads needed files - takes 5-10 minutes first time)
7. Click "Build" menu → "Build Bundle(s) / APK(s)" → "Build APK(s)"
8. Wait for build to complete
9. Click "locate" in the success message
10. You'll find `app-debug.apk` in the folder

---

### Step 2: Install the App on Your Phone

1. Copy the `app-debug.apk` file to your phone (via USB, email, cloud storage, etc.)
2. On your phone, tap the APK file
3. If you see "Install from Unknown Sources" warning:
   - Go to Settings → Security → Allow installation from this source
   - Go back and tap the APK again
4. Tap "Install"
5. Wait for installation to complete
6. Tap "Open" or find "TWRP Info Collector" in your app drawer

---

### Step 3: Use the App

1. Open "TWRP Info Collector" app
2. The app automatically collects your phone's information (you'll see it on screen)
3. Tap "Save to File" button
4. You'll see a message showing where the file was saved
5. Open your file manager and go to the "Download" folder
6. Find a file named `twrp-builder-[something].txt` (the [something] is your phone's codename)
7. Open this file to view your device information

---

## 📱 Using the Information

Now that you have your device information file:

1. Open your web browser
2. Go to: https://www.hovatek.com/twrpbuilder/
3. Open your saved text file
4. Fill in the TWRP Builder form using the information from your file:
   - Look for "Android Version: X" in your file - enter this
   - Look for "Screen Resolution: WIDTHxHEIGHT" - enter this
   - Look for "Device Codename: XXXXX" - this is your device name
5. Upload your recovery/boot image files as instructed
6. Follow the website's instructions to build TWRP

---

## ❓ Common Questions

**Q: Do I need to root my phone?**  
A: No! The app works on non-rooted phones.

**Q: Will this app harm my phone?**  
A: No. It only reads information, doesn't modify anything.

**Q: Why do I need to build the APK first?**  
A: APK files are large and can't be stored in text-only GitHub repositories. Building is simple with the right tools.

**Q: What if I can't build the APK?**  
A: Ask someone who has Android Studio, or post in Android development forums. Share this repository link with them.

**Q: Where exactly is the file saved?**  
A: In your phone's Download folder (same place where browser downloads go). Look for a file named `twrp-builder-` followed by your device name.

**Q: Do I need internet to use the app?**  
A: No. Once installed, the app works completely offline.

**Q: What information does the app collect?**  
A: Only technical specifications like Android version, screen size, processor type, etc. No personal data.

---

## 🆘 Troubleshooting

**Problem: Can't install the APK**  
Solution: Enable "Install from Unknown Sources" in Settings → Security

**Problem: App crashes when opening**  
Solution: Your phone should be Android 5.0 or newer. Check your Android version in Settings → About Phone

**Problem: Can't find the saved file**  
Solution: 
- Open any file manager app
- Look in "Internal Storage" → "Download" folder
- File name starts with "twrp-builder-"

**Problem: Permission denied when saving**  
Solution: Go to Settings → Apps → TWRP Info Collector → Permissions → Allow Storage

---

## 💡 Tips

1. **Take a screenshot** of the app screen before saving - good backup!
2. **Keep the text file** - you might need it again later
3. **Share with friends** - if they also need TWRP info
4. **Read FEATURES.md** - to understand what information is collected

---

## 📚 More Help

- **Detailed instructions**: See `BUILD_INSTRUCTIONS.md`
- **Technical details**: See `FEATURES.md`
- **Example output**: See `SAMPLE_OUTPUT.txt`
- **Full documentation**: See `README.md`

---

## What Is TWRP?

TWRP (Team Win Recovery Project) is a custom recovery tool for Android devices that lets you:
- Install custom ROMs
- Create backups
- Root your device
- Install modifications

The Hovatek TWRP Builder helps you create TWRP for your specific phone model.

**Website**: https://www.hovatek.com/twrpbuilder/

---

## Need More Help?

1. Check all the documentation files in this repository
2. Visit Hovatek forum: https://www.hovatek.com/forum/
3. Search for "Android Studio APK build tutorial" online
4. Ask in Android development communities

---

**Remember**: Building the APK requires a computer and Android Studio. Everything else can be done on your phone!

Good luck! 🚀
