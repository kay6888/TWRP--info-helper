# TWRP Info Helper - Architecture Diagram

## Application Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         TWRP Info Helper                         │
│                         Android Application                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                          MainActivity                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Toolbar: [Title] [Settings Icon]                         │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  Donation Card (PayPal | CashApp)                         │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  Tab Layout: [ My Device ] [ Search ]                     │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  Search UI (visible when Search tab selected)             │  │
│  │  ┌─────────────────────────────┐ ┌──────────────────┐    │  │
│  │  │ SearchView: "Enter device..." │ │ Search Online │    │  │
│  │  └─────────────────────────────┘ └──────────────────┘    │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  Filter UI (visible when My Device tab selected)          │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │ SearchView: "Filter device info..."              │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  [Collect Info Button] [Save to File Button]              │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  Device Information TextView (Scrollable)           │ │  │
│  │  │  - Device specs OR Search results                   │ │  │
│  │  │  - Monospace font, selectable text                  │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ├──────────────┐
                            │              │
                            ▼              ▼
        ┌─────────────────────────┐  ┌─────────────────────────┐
        │   SettingsActivity      │  │   Helper Classes        │
        ├─────────────────────────┤  ├─────────────────────────┤
        │ ○ Light Mode            │  │ FileSaveHelper          │
        │ ○ Dark Mode             │  │ - saveWithMediaStore()  │
        │ ● System Default        │  │ - saveLegacy()          │
        │                         │  │                         │
        │ [Apply immediately]     │  │ DeviceSearchService     │
        │                         │  │ - searchDevice()        │
        └─────────────────────────┘  │ - cache results         │
                                     │ - ExecutorService       │
                                     └─────────────────────────┘
```

## Data Flow

### Theme Management
```
User selects theme
    ↓
SettingsActivity
    ↓
SharedPreferences.edit()
    ↓
Save theme_mode (0=Light, 1=Dark, 2=System)
    ↓
AppCompatDelegate.setDefaultNightMode()
    ↓
UI updates immediately
```

### File Saving (Android 10+)
```
User taps "Save to File"
    ↓
MainActivity.checkPermissionAndSave()
    ↓
FileSaveHelper.saveDeviceInfo()
    ↓
Build.VERSION.SDK_INT >= Q?
    ├─ Yes → saveWithMediaStore()
    │           ↓
    │       MediaStore.insert()
    │           ↓
    │       Write to OutputStream
    │           ↓
    │       Verify file
    └─ No → saveLegacy()
                ↓
            File.write()
                ↓
            Verify file
    ↓
Callback → Show success/error toast
```

### Device Search
```
User enters query and taps "Search Online"
    ↓
MainActivity.performOnlineSearch()
    ↓
Show progress indicator
    ↓
DeviceSearchService.searchDevice()
    ↓
Check network connectivity
    ↓
Check cache
    ├─ Found → Return cached result
    └─ Not found → Execute background task
                        ↓
                    ExecutorService.execute()
                        ↓
                    createMockDeviceInfo()
                    (or real API call)
                        ↓
                    Cache result
                        ↓
                    Handler.post() to main thread
    ↓
Hide progress indicator
    ↓
Update TextView with results
```

## Component Relationships

```
┌─────────────────────────────────────────────────────────────┐
│                      MainActivity                            │
│  (Orchestrates all features)                                 │
└───┬─────────────┬─────────────┬──────────────┬──────────────┘
    │             │             │              │
    │             │             │              │
    ▼             ▼             ▼              ▼
┌─────────┐  ┌─────────┐  ┌──────────┐  ┌──────────────┐
│Settings │  │FileSave │  │Device    │  │System APIs   │
│Activity │  │Helper   │  │Search    │  │- Build       │
│         │  │         │  │Service   │  │- Display     │
│Theme    │  │MediaStr │  │          │  │- Window      │
│Toggle   │  │-ore API │  │Executor  │  │- Environment │
└─────────┘  └─────────┘  └──────────┘  └──────────────┘
     │            │             │              │
     └────────────┴─────────────┴──────────────┘
                      │
                      ▼
            ┌──────────────────┐
            │ SharedPreferences│
            │ - theme_mode     │
            └──────────────────┘
```

## Permission Flow

```
AndroidManifest.xml
    │
    ├─ INTERNET (for web search)
    ├─ ACCESS_NETWORK_STATE (check connectivity)
    ├─ WRITE_EXTERNAL_STORAGE (maxSdkVersion=28)
    └─ READ_EXTERNAL_STORAGE (maxSdkVersion=32)
        ↓
MainActivity onCreate()
    ↓
User action triggers save
    ↓
checkPermissionAndSave()
    ↓
SDK < 29? → Request runtime permission
SDK >= 29? → No permission needed (scoped storage)
    ↓
Save using appropriate method
```

## Threading Model

```
Main Thread (UI Thread)
    │
    ├─ MainActivity
    ├─ SettingsActivity
    ├─ UI Updates
    └─ Event Handlers
        │
        └─ Triggers background work
                ↓
        ┌─────────────────┐
        │ ExecutorService │ (Background Thread)
        │                 │
        │ - Device search │
        │ - Mock data     │
        │ - Real API call │
        └─────────────────┘
                ↓
        ┌─────────────────┐
        │ Handler.post()  │
        │ (Back to Main)  │
        └─────────────────┘
                ↓
        Update UI with results
```

## Resource Organization

```
res/
├── layout/
│   ├── activity_main.xml (Enhanced with tabs)
│   └── activity_settings.xml (New)
├── menu/
│   └── main_menu.xml (New)
├── values/
│   ├── strings.xml (Enhanced)
│   ├── colors.xml (Enhanced)
│   └── themes.xml (New)
└── drawable/
    └── (existing icons)
```

## Class Hierarchy

```
AppCompatActivity
    │
    ├── MainActivity
    │   ├── Theme management
    │   ├── Tab switching
    │   ├── Device info collection
    │   ├── Search coordination
    │   └── File saving coordination
    │
    └── SettingsActivity
        └── Theme preference management

Helper Classes (Utility)
    │
    ├── FileSaveHelper
    │   ├── SaveCallback interface
    │   ├── saveWithMediaStore()
    │   └── saveLegacy()
    │
    └── DeviceSearchService
        ├── SearchCallback interface
        ├── searchDevice()
        └── SearchTask (Runnable)
```

## Feature Integration Points

### Tab System
```
TabLayout (Material Design)
    │
    ├─ Tab 0: "My Device"
    │   ├─ Shows: filterView
    │   ├─ Hides: searchLayout
    │   ├─ Enables: collectButton
    │   └─ Displays: fullDeviceInfo
    │
    └─ Tab 1: "Search"
        ├─ Shows: searchLayout
        ├─ Hides: filterView
        ├─ Disables: collectButton
        └─ Displays: searchedDeviceInfo
```

### Theme System
```
User preference (SharedPreferences)
    │
    ├─ THEME_LIGHT (0)
    │   └─ AppCompatDelegate.MODE_NIGHT_NO
    │
    ├─ THEME_DARK (1)
    │   └─ AppCompatDelegate.MODE_NIGHT_YES
    │
    └─ THEME_SYSTEM (2)
        └─ AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
```

## Summary

This architecture provides:
- ✅ Clean separation of concerns
- ✅ Modern Android patterns (ExecutorService, Handler)
- ✅ Material Design components
- ✅ Proper threading model
- ✅ Scoped storage compatibility
- ✅ Backward compatibility to API 21
- ✅ Extensible for real API integration
