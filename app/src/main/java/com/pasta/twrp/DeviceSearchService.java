package com.pasta.twrp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.os.Build;
import android.net.NetworkCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceSearchService {

    private static final String TAG = "DeviceSearchService";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // Make cache final since it's not reassigned
    private static final Map<String, String> cache = new HashMap<>();

    public interface SearchCallback {
        void onSearchComplete(String deviceInfo);
        void onSearchError(String error);
    }

    public static void searchDevice(Context context, String query, SearchCallback callback) {
        if (!isNetworkAvailable(context)) {
            callback.onSearchError("No network connection available");
            return;
        }

        // Check cache first
        String cacheKey = query.toLowerCase().trim();
        if (cache.containsKey(cacheKey)) {
            callback.onSearchComplete(cache.get(cacheKey));
            return;
        }

        // Perform search in background
        executorService.execute(new SearchTask(query, callback));
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        // Use NetworkCapabilities on newer APIs and fall back to deprecated API for older ones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            );
        } else {
            return isNetworkConnectedLegacy(connectivityManager);
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean isNetworkConnectedLegacy(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static class SearchTask implements Runnable {
        private final String query;
        private final SearchCallback callback;

        SearchTask(String query, SearchCallback callback) {
            this.query = query;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                // Create mock device info for demonstration
                // In a real implementation, you would fetch from an actual API
                final String deviceInfo = createMockDeviceInfo(query);
                
                // Cache the result
                cache.put(query.toLowerCase().trim(), deviceInfo);
                
                // Post result to main thread
                mainHandler.post(() -> callback.onSearchComplete(deviceInfo));
            } catch (Exception e) {
                final String error = e.getMessage();
                Log.e(TAG, "Search error", e);
                
                // Post error to main thread
                mainHandler.post(() -> callback.onSearchError(error != null ? error : "Unknown error occurred"));
            }
        }

        private String createMockDeviceInfo(String query) {
            String normalizedQuery = query.toLowerCase().trim();
            DeviceInfo device = DeviceDatabase.searchDevice(normalizedQuery);
            
            StringBuilder info = new StringBuilder();
            info.append("=== DEVICE SEARCH RESULTS ===\n\n");
            info.append("Search Query: ").append(query).append("\n\n");
            
            if (device != null) {
                info.append("--- DEVICE FOUND ---\n\n");
                
                // Basic Device Information
                info.append("=== BASIC DEVICE INFO ===\n");
                info.append("Device Codename: ").append(device.codename).append("\n");
                info.append("Brand: ").append(device.brand).append("\n");
                info.append("Model: ").append(device.model).append("\n");
                info.append("Manufacturer: ").append(device.manufacturer).append("\n");
                if (device.releaseDate != null) {
                    info.append("Release Date: ").append(device.releaseDate).append("\n");
                }
                info.append("\n");
                
                // Hardware Specifications
                info.append("=== HARDWARE SPECS ===\n");
                if (device.soc != null) {
                    info.append("SoC/Processor: ").append(device.soc).append("\n");
                }
                if (device.architecture != null) {
                    info.append("CPU Architecture: ").append(device.architecture).append("\n");
                }
                if (device.screenResolution != null) {
                    info.append("Screen Resolution: ").append(device.screenResolution).append("\n");
                    
                    // Calculate DPI category
                    String[] res = device.screenResolution.split("x");
                    if (res.length == 2) {
                        try {
                            int width = Integer.parseInt(res[0]);
                            String dpiCategory;
                            if (width >= 1440) {
                                dpiCategory = "xxxhdpi (640 dpi)";
                            } else if (width >= 1080) {
                                dpiCategory = "xxhdpi (480 dpi)";
                            } else if (width >= 720) {
                                dpiCategory = "xhdpi (320 dpi)";
                            } else {
                                dpiCategory = "hdpi (240 dpi)";
                            }
                            info.append("DPI Category: ").append(dpiCategory).append("\n");
                        } catch (NumberFormatException e) {
                            // Skip DPI calculation if parsing fails
                        }
                    }
                }
                info.append("\n");
                
                // Software Information
                info.append("=== SOFTWARE INFO ===\n");
                if (device.androidVersion != null) {
                    info.append("Android Version: ").append(device.androidVersion).append("\n");
                    
                    // Extract first version for API level
                    String firstVersion = device.androidVersion.split("-")[0].trim();
                    int apiLevel = getApiLevelFromVersion(firstVersion);
                    if (apiLevel > 0) {
                        info.append("API Level: ").append(apiLevel).append("+\n");
                    }
                }
                info.append("TWRP Compatible: Yes (for most variants)\n");
                info.append("\n");
                
                // Partition Information (estimated based on Android version)
                info.append("=== PARTITION LAYOUT ===\n");
                if (device.androidVersion != null) {
                    String firstVer = device.androidVersion.split("-")[0].trim();
                    int mainVersion = getMainAndroidVersion(firstVer);
                    
                    if (mainVersion >= 13) {
                        info.append("Partition Scheme: A/B (Seamless updates)\n");
                        info.append("Dynamic Partitions: Yes (Super partition)\n");
                        info.append("Required Images: boot.img + vendor_boot.img\n");
                        info.append("System-as-root: Yes\n");
                    } else if (mainVersion >= 10) {
                        info.append("Partition Scheme: A/B or A-only (variant dependent)\n");
                        info.append("Dynamic Partitions: Likely (check with fastboot)\n");
                        info.append("Required Images: boot.img or recovery.img\n");
                        info.append("System-as-root: Yes\n");
                    } else {
                        info.append("Partition Scheme: A-only (traditional)\n");
                        info.append("Dynamic Partitions: No\n");
                        info.append("Required Images: recovery.img\n");
                        info.append("System-as-root: Check variant\n");
                    }
                }
                info.append("\n");
                
                // TWRP Builder Requirements
                info.append("=== TWRP BUILDER REQUIREMENTS ===\n");
                info.append("Visit: https://www.hovatek.com/twrpbuilder/\n\n");
                
                info.append("Step 1: Select Image Type\n");
                if (device.androidVersion != null) {
                    int mainVersion = getMainAndroidVersion(device.androidVersion.split("-")[0].trim());
                    if (mainVersion >= 13) {
                        info.append("  → boot.img + vendor_boot.img (Android 13+)\n");
                    } else if (mainVersion >= 10) {
                        info.append("  → boot.img (for A/B devices) OR recovery.img (for A-only)\n");
                    } else {
                        info.append("  → recovery.img (most common for older devices)\n");
                    }
                }
                info.append("\n");
                
                info.append("Step 2: Android Version\n");
                if (device.androidVersion != null) {
                    info.append("  → ").append(device.androidVersion.split("-")[0].trim()).append("\n");
                }
                info.append("\n");
                
                info.append("Step 3: Screen Resolution\n");
                if (device.screenResolution != null) {
                    info.append("  → ").append(device.screenResolution).append("\n");
                }
                info.append("\n");
                
                info.append("Step 4: Extract Boot/Recovery Image\n");
                info.append("  Methods to extract:\n");
                info.append("  • From stock firmware (recommended)\n");
                info.append("  • Using ADB: adb pull /dev/block/bootdevice/by-name/boot boot.img\n");
                info.append("  • Using ADB: adb pull /dev/block/bootdevice/by-name/recovery recovery.img\n");
                info.append("  • From payload.bin (Android 9+)\n");
                info.append("\n");
                
                info.append("Step 5: Optional Touch Driver Info\n");
                info.append("  If touch doesn't work after TWRP build:\n");
                info.append("  • Check /proc/touchscreen/ on device\n");
                info.append("  • Check /sys/class/input/input*/name\n");
                info.append("  • Search XDA forums for ").append(device.codename).append(" touch driver\n");
                info.append("\n");
                
                // Additional Resources
                info.append("=== ADDITIONAL RESOURCES ===\n");
                info.append("XDA Forums: Search for '").append(device.codename).append(" twrp'\n");
                info.append("GitHub: Search for '").append(device.codename).append(" device tree'\n");
                info.append("Official TWRP: https://twrp.me/Devices/\n");
                info.append("\n");
                
                // Important Notes
                info.append("=== IMPORTANT NOTES ===\n");
                info.append("• Always backup your data before flashing TWRP\n");
                info.append("• Unlock bootloader before flashing (if locked)\n");
                info.append("• Some variants may have different partition layouts\n");
                info.append("• Check device variant/model number carefully\n");
                info.append("• Carrier-locked devices may have different specs\n");
                info.append("\n");
                
                // Fastboot Commands Reference
                info.append("=== FASTBOOT COMMANDS REFERENCE ===\n");
                info.append("Flash TWRP:\n");
                info.append("  fastboot flash recovery twrp.img\n");
                info.append("OR (for A/B devices):\n");
                info.append("  fastboot flash boot twrp.img\n");
                info.append("\nBoot TWRP temporarily (testing):\n");
                info.append("  fastboot boot twrp.img\n");
                info.append("\nCheck partition layout:\n");
                info.append("  fastboot getvar all\n");
                info.append("\n");
                
            } else {
                info.append("--- DEVICE NOT FOUND ---\n");
                info.append("No device found matching: ").append(query).append("\n\n");
                
                info.append("=== SEARCH TIPS ===\n");
                info.append("Try searching by:\n");
                info.append("• Device codename (e.g., 'oriole', 'beyond1lte', 'enchilada')\n");
                info.append("• Model number (e.g., 'SM-G973F', 'A2215', 'OnePlus 6')\n");
                info.append("• Full model name (e.g., 'Galaxy S10', 'Pixel 6', 'Mi 9')\n");
                info.append("• Brand name (e.g., 'Samsung', 'Google', 'OnePlus')\n\n");
                
                info.append("=== POPULAR DEVICES IN DATABASE ===\n");
                info.append("Google Pixel:\n");
                info.append("  • Pixel 3a, 3a XL, 4, 4 XL, 4a, 4a 5G, 5, 5a\n");
                info.append("  • Pixel 6, 6 Pro, 6a, 7, 7 Pro, 7a, 8, 8 Pro\n\n");
                
                info.append("Samsung Galaxy S:\n");
                info.append("  • S10e, S10, S10+, S20, S20+, S20 Ultra\n");
                info.append("  • S21, S21+, S21 Ultra, S22, S22+, S22 Ultra\n");
                info.append("  • S23, S23+, S23 Ultra, S23 FE\n\n");
                
                info.append("OnePlus:\n");
                info.append("  • OnePlus 6, 6T, 7, 7 Pro, 7T, 7T Pro\n");
                info.append("  • OnePlus 8, 8 Pro, 8T, 9, 9 Pro\n\n");
                
                info.append("Xiaomi/Poco:\n");
                info.append("  • Mi 8, Mi Mix 2S, Mi 9, Mi 9T Pro\n");
                info.append("  • Poco F1, F2 Pro, F3, F4\n\n");
                
                info.append("=== MANUAL DEVICE INFO COLLECTION ===\n");
                info.append("If your device is not in database:\n");
                info.append("1. Switch to 'My Device' tab\n");
                info.append("2. Tap 'Collect Info' button\n");
                info.append("3. Get specs directly from your device\n\n");
            }
            
            return info.toString();
        }
        
        private int getApiLevelFromVersion(String version) {
            try {
                int ver = Integer.parseInt(version.replaceAll("[^0-9]", ""));
                switch (ver) {
                    case 14: return 34;
                    case 13: return 33;
                    case 12: return 31;
                    case 11: return 30;
                    case 10: return 29;
                    case 9: return 28;
                    case 8: return 26;
                    default: return ver + 15;
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        private int getMainAndroidVersion(String version) {
            try {
                String numOnly = version.replaceAll("[^0-9]", "");
                if (numOnly.length() > 0) {
                    return Integer.parseInt(numOnly.substring(0, Math.min(2, numOnly.length())));
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
            return 0;
        }
    }
}
