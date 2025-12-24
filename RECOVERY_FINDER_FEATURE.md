# Recovery Finder Feature

## 🔍 What's New

The TWRP Info Helper app now includes a **Custom Recovery Finder** feature that helps you locate unofficial TWRP, OrangeFox, and PBRP recoveries for your device!

## 🌟 Features

- **Automatic Device Detection** - Uses your current device's codename and manufacturer
- **Multi-Source Search** - Searches GitHub and SourceForge repositories
- **Recovery Type Filtering** - Choose TWRP, OrangeFox, PBRP, or All
- **Beautiful Material Design UI** - Modern card-based layout
- **Direct Downloads** - Open recovery pages directly in your browser
- **Star Ratings** - See popular repositories with GitHub stars
- **Smart Detection** - Automatically identifies recovery type from names and descriptions

## 📱 How to Use

### Method 1: From Menu
1. Open **TWRP Info Helper** app
2. Tap the **🔍 Search icon** in the top menu
3. Select recovery type from dropdown
4. Tap **Search Recoveries**
5. Browse results and tap **View/Download** on any recovery

### Method 2: From Notification
- Device info is automatically detected from your current device

## 🎯 What It Searches

### GitHub
- Repository names and descriptions
- Release tags and assets
- Device-specific builds
- Popular TWRP/OrangeFox forks

### SourceForge
- Project names and summaries
- Device-specific uploads
- Recovery distributions

## 📊 Search Results Include

- **Recovery Name** - Project or file name
- **Type** - TWRP, OrangeFox, PBRP, or Unknown
- **Source** - GitHub or SourceForge  
- **Description** - Project details
- **Stars** - GitHub popularity (if applicable)
- **Direct Link** - Open in browser for downloads

## 🔒 Safety Tips

⚠️ **IMPORTANT**: The app finds UNOFFICIAL/UNCERTIFIED recoveries. Always:

- ✅ **Verify file integrity** - Check MD5/SHA checksums
- ✅ **Read installation guides** - Follow device-specific instructions
- ✅ **Backup your data** - Use TWRP to create a full backup first
- ✅ **Check compatibility** - Ensure recovery matches your exact device model
- ✅ **Use trusted sources** - Prefer repositories with many stars/downloads
- ❌ **Don't flash blindly** - Research before installing

## 🛠️ Technical Details

### API Integration
- **GitHub API v3** - Repository and code search
- **SourceForge REST API** - Project search
- **No API Key Required** - Works out of the box

### Device Detection
```java
String codename = Build.DEVICE;        // e.g., "miatoll"
String manufacturer = Build.MANUFACTURER; // e.g., "Xiaomi"
```

### Search Algorithm
1. Queries multiple APIs with device codename
2. Filters results by recovery type keywords
3. Detects recovery type from names/descriptions
4. Removes duplicates
5. Sorts by source and popularity

## 🎨 UI Components

### Main Screen
- Device info display
- Recovery type selector (Spinner)
- Search button
- Progress indicator

### Results Screen
- Scrollable card list
- Each card shows:
  - Recovery name (bold, colored)
  - Type badge (colored)
  - Source label
  - Description (3 lines max)
  - Star count (if GitHub)
  - View/Download button

### Color Scheme
- **Primary**: `#FF6200EE` (Purple)
- **Success**: `#4CAF50` (Green badges)
- **Warning**: `#FFB300` (Yellow stars)
- **Background**: `#F5F5F5` (Light gray)

## 📦 Dependencies

All dependencies are already included in the main app:

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.9.0'
implementation 'androidx.cardview.widget.CardView' // Included in material
```

## 🔧 Customization

### Add New Sources

Edit `RecoveryFinderActivity.java`:

```java
private List<RecoveryResult> searchNewSource(String recoveryType) {
    List<RecoveryResult> results = new ArrayList<>();
    
    // Your API calls here
    
    return results;
}
```

Then add to `doInBackground`:
```java
results.addAll(searchNewSource(recoveryType));
```

### Modify Recovery Types

Edit the spinner array in `setupRecoveryTypeSpinner()`:

```java
String[] recoveryTypes = {"All Recoveries", "TWRP", "OrangeFox", "PBRP", "SHRP"};
```

## 🐛 Troubleshooting

### No Results Found
- **Check device codename**: Some devices have multiple codenames
- **Try manufacturer search**: Search by brand name
- **Build yourself**: Use TWRP Builder with collected info

### Network Errors
- **Check internet**: Ensure WiFi/data is enabled
- **Firewall**: Some networks block GitHub API
- **Rate limiting**: GitHub limits to 60 requests/hour without auth

### App Crashes
- **Check logs**: Use `adb logcat` for debugging
- **Update app**: Ensure you have the latest version
- **Report issues**: Open GitHub issue with details

## 📈 Future Enhancements

Planned features:
- [ ] XDA Developers forum search
- [ ] Telegram channel integration
- [ ] Download manager integration
- [ ] MD5 checksum verification
- [ ] Installation guide links
- [ ] Device database caching
- [ ] Offline mode (cached results)
- [ ] Share recovery links
- [ ] Bookmark favorites
- [ ] Direct APK downloads

## 🤝 Contributing

Want to add a new source or improve the search?

1. Fork the repository
2. Edit `RecoveryFinderActivity.java`
3. Add your search method
4. Test thoroughly
5. Submit a Pull Request

## 📝 License

This feature is part of TWRP Info Helper and uses the same MIT License.

## 🙏 Credits

- **GitHub API** - Repository search
- **SourceForge API** - Project search
- **Material Design** - UI components
- **Android Community** - Testing and feedback

---

**Made with ❤️ for the Android modding community**

Found a bug? Have a suggestion? [Open an issue](https://github.com/kay6888/TWRP--info-helper/issues)!
