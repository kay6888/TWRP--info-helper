package com.hovatek.twrpinfo;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MAX_DUMPSYS_LINES = 5;
    private static final String[] TOUCH_DRIVER_VENDORS = {
        "touch", "synaptics", "focaltech", "goodix", "atmel", "cypress", "ft", "gt", "touchscreen", "ts"
    };
    
    private TextView infoTextView;
    private Button collectButton;
    private Button saveButton;
    private SearchView searchView;
    private LinearLayout paypalButton;
    private LinearLayout cashappButton;
    private String deviceInfo = "";
    private String fullDeviceInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = findViewById(R.id.infoTextView);
        collectButton = findViewById(R.id.collectButton);
        saveButton = findViewById(R.id.saveButton);
        searchView = findViewById(R.id.searchView);
        paypalButton = findViewById(R.id.paypalButton);
        cashappButton = findViewById(R.id.cashappButton);

        setupSearch();
        setupDonationButtons();

        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectDeviceInfo();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceInfo.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please collect device info first", Toast.LENGTH_SHORT).show();
                } else {
                    checkPermissionAndSave();
                }
            }
        });

        // Auto-collect on start
        collectDeviceInfo();
    }

    private void setupDonationButtons() {
        paypalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(getString(R.string.paypal_email));
            }
        });

        cashappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(getString(R.string.cashapp_id));
            }
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Payment Info", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.donation_copied), Toast.LENGTH_SHORT).show();
    }

    private boolean isRootAvailable() {
        String[] paths = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        };
        
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRootGranted() {
        // Note: This method does NOT request root permission to avoid disruptive dialogs.
        // It only checks if root commands can be executed without user interaction.
        // If su requires a popup, this will timeout and return false.
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su -c id");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();
            reader.close();
            
            // Use a short timeout to avoid waiting for user interaction
            boolean completed;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                completed = process.waitFor(1, TimeUnit.SECONDS);
            } else {
                // For older Android versions, simulate timeout behavior
                final Process finalProcess = process;
                final boolean[] processCompleted = {false};
                
                Thread timeoutThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000); // 1 second timeout
                            synchronized (processCompleted) {
                                if (!processCompleted[0]) {
                                    finalProcess.destroy();
                                }
                            }
                        } catch (InterruptedException e) {
                            // Timeout thread interrupted, process probably completed
                        }
                    }
                });
                timeoutThread.start();
                
                process.waitFor();
                synchronized (processCompleted) {
                    processCompleted[0] = true;
                }
                timeoutThread.interrupt();
                completed = true;
            }
            
            // Only return true if command completed without user interaction and shows root
            return completed && output != null && output.toLowerCase(Locale.ROOT).contains("uid=0");
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private String getSELinuxStatus() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String status = reader.readLine();
            reader.close();
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.waitFor(2, TimeUnit.SECONDS);
            } else {
                process.waitFor();
            }
            
            return status != null ? status : "Unknown";
        } catch (Exception e) {
            return "Unable to determine";
        }
    }

    private String getBootloaderStatus() {
        // Check specific bootloader verification files
        String verifiedBootFile = readFile("/sys/class/sec/sec_debug/boot_verifiedboot");
        if (verifiedBootFile != null && !verifiedBootFile.isEmpty()) {
            String content = verifiedBootFile.trim().toLowerCase(Locale.ROOT);
            if (content.equals("1") || content.contains("locked")) {
                return "Locked (verified boot enabled)";
            } else if (content.equals("0") || content.contains("unlocked")) {
                return "Unlocked (verified boot disabled)";
            }
        }
        
        // Check OEM secure boot setting
        String oemSecBootFile = readFile("/sys/devices/soc0/oem_sec_boot_enabled");
        if (oemSecBootFile != null && !oemSecBootFile.isEmpty()) {
            String content = oemSecBootFile.trim();
            if (content.equals("1")) {
                return "Locked (OEM secure boot enabled)";
            } else if (content.equals("0")) {
                return "Unlocked (OEM secure boot disabled)";
            }
        }
        
        // Try getprop for bootloader lock status
        String lockStateProp = executeCommand("getprop ro.boot.flash.locked");
        if (lockStateProp != null && !lockStateProp.isEmpty()) {
            String value = lockStateProp.trim();
            if (value.equals("1")) {
                return "Locked (via ro.boot.flash.locked)";
            } else if (value.equals("0")) {
                return "Unlocked (via ro.boot.flash.locked)";
            }
        }
        
        // Check alternative property
        String verifiedBootStateProp = executeCommand("getprop ro.boot.verifiedbootstate");
        if (verifiedBootStateProp != null && !verifiedBootStateProp.isEmpty()) {
            String value = verifiedBootStateProp.trim().toLowerCase(Locale.ROOT);
            if (value.equals("green")) {
                return "Locked (verified boot green)";
            } else if (value.equals("orange") || value.equals("yellow") || value.equals("red")) {
                return "Unlocked (verified boot " + value + ")";
            }
        }
        
        return "Unable to determine (device may not expose this information)";
    }

    private String getSystemPartitionMountStatus() {
        try {
            Process process = Runtime.getRuntime().exec("mount");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("/system")) {
                    if (line.contains(" ro,") || line.contains(" ro ")) {
                        reader.close();
                        return "Read-only (ro)";
                    } else if (line.contains(" rw,") || line.contains(" rw ")) {
                        reader.close();
                        return "Read-write (rw)";
                    }
                }
            }
            reader.close();
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.waitFor(2, TimeUnit.SECONDS);
            } else {
                process.waitFor();
            }
            
            return "Unable to determine";
        } catch (Exception e) {
            return "Unable to determine";
        }
    }

    private void collectDeviceInfo() {
        StringBuilder info = new StringBuilder();
        
        info.append("=== TWRP BUILDER DEVICE INFORMATION ===\n\n");
        
        // Basic Device Information
        info.append("--- BASIC DEVICE INFO ---\n");
        info.append("Device Codename: ").append(Build.DEVICE).append("\n");
        info.append("Product Name: ").append(Build.PRODUCT).append("\n");
        info.append("Brand: ").append(Build.BRAND).append("\n");
        info.append("Model: ").append(Build.MODEL).append("\n");
        info.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        info.append("Board: ").append(Build.BOARD).append("\n");
        info.append("Hardware: ").append(Build.HARDWARE).append("\n\n");
        
        // Android Version Information
        info.append("--- ANDROID VERSION INFO ---\n");
        info.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        info.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        info.append("Build ID: ").append(Build.ID).append("\n");
        info.append("Build Type: ").append(Build.TYPE).append("\n");
        info.append("Build Tags: ").append(Build.TAGS).append("\n");
        info.append("Incremental: ").append(Build.VERSION.INCREMENTAL).append("\n\n");
        
        // Architecture Information
        info.append("--- ARCHITECTURE INFO ---\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            info.append("Supported ABIs: ");
            for (String abi : Build.SUPPORTED_ABIS) {
                info.append(abi).append(" ");
            }
            info.append("\n");
            if (Build.SUPPORTED_ABIS.length > 0) {
                info.append("Primary ABI: ").append(Build.SUPPORTED_ABIS[0]).append("\n");
            }
        } else {
            info.append("CPU ABI: ").append(Build.CPU_ABI).append("\n");
            info.append("CPU ABI2: ").append(Build.CPU_ABI2).append("\n");
        }
        info.append("Architecture: ").append(System.getProperty("os.arch")).append("\n\n");
        
        // Screen Information
        info.append("--- SCREEN INFO ---\n");
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        info.append("Screen Resolution: ").append(metrics.widthPixels).append("x").append(metrics.heightPixels).append("\n");
        info.append("Screen Density: ").append(metrics.densityDpi).append(" dpi\n");
        info.append("Density Scale: ").append(metrics.density).append("\n\n");
        
        // Kernel Information
        info.append("--- KERNEL INFO ---\n");
        info.append("Kernel Version: ").append(System.getProperty("os.version")).append("\n");
        String kernelVersion = getKernelVersion();
        if (kernelVersion != null && !kernelVersion.isEmpty()) {
            info.append("Detailed Kernel: ").append(kernelVersion).append("\n");
        }
        info.append("\n");
        
        // Root & Security Status Information
        info.append("--- ROOT & SECURITY STATUS ---\n");
        boolean rootAvailable = isRootAvailable();
        boolean rootGranted = isRootGranted();
        String selinuxStatus = getSELinuxStatus();
        String bootloaderStatus = getBootloaderStatus();
        String systemMountStatus = getSystemPartitionMountStatus();
        
        info.append("Root Available: ").append(rootAvailable ? "Yes (su binary detected)" : "No").append("\n");
        info.append("Root Permission: ").append(rootGranted ? "Granted (automatic check)" : "Not granted/denied").append("\n");
        info.append("Note: Root permission check is non-intrusive and won't prompt for access\n");
        info.append("SELinux Status: ").append(selinuxStatus).append("\n");
        info.append("Bootloader Status: ").append(bootloaderStatus).append("\n");
        info.append("System Partition: ").append(systemMountStatus).append("\n");
        info.append("SafetyNet Status: Check with SafetyNet app (not detectable programmatically)\n");
        info.append("\n");
        
        // Touch Driver Information
        info.append("--- TOUCH DRIVER INFO ---\n");
        String touchDriver = getTouchDriverInfo();
        if (touchDriver != null && !touchDriver.isEmpty()) {
            info.append(touchDriver);
        } else {
            info.append("Touch driver information not available.\n");
            info.append("Note: Root access may be required to detect touch driver.\n");
        }
        info.append("\n");
        
        // Build Fingerprint
        info.append("--- BUILD FINGERPRINT ---\n");
        info.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n");
        info.append("Display: ").append(Build.DISPLAY).append("\n\n");
        
        // Additional Properties
        info.append("--- ADDITIONAL PROPERTIES ---\n");
        info.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
        info.append("Radio Version: ").append(Build.getRadioVersion()).append("\n");
        info.append("Host: ").append(Build.HOST).append("\n");
        info.append("User: ").append(Build.USER).append("\n");
        info.append("Time: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(Build.TIME))).append("\n\n");
        
        // TWRP Specific Requirements
        info.append("=== TWRP BUILDER REQUIREMENTS ===\n\n");
        info.append("Image Type Required: recovery.img OR boot.img OR boot.img + vendor_boot.img\n");
        info.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        info.append("Screen Resolution: ").append(metrics.widthPixels).append("x").append(metrics.heightPixels).append("\n");
        info.append("Device Codename: ").append(Build.DEVICE).append("\n");
        String primaryAbi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.SUPPORTED_ABIS.length > 0 
            ? Build.SUPPORTED_ABIS[0] 
            : Build.CPU_ABI;
        info.append("Architecture: ").append(primaryAbi).append("\n\n");
        
        info.append("=== INSTRUCTIONS ===\n");
        info.append("1. Visit: https://www.hovatek.com/twrpbuilder/\n");
        info.append("2. Select image type (recovery.img, boot.img, or both)\n");
        info.append("3. Enter Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        info.append("4. Enter Screen Resolution: ").append(metrics.widthPixels).append("x").append(metrics.heightPixels).append("\n");
        info.append("5. Upload your device's recovery/boot image files\n");
        info.append("6. Optional: Add touch driver info if touch doesn't work\n");
        info.append("7. Build and download your custom TWRP recovery\n\n");
        
        info.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date())).append("\n");
        
        deviceInfo = info.toString();
        fullDeviceInfo = deviceInfo;
        infoTextView.setText(deviceInfo);
        saveButton.setEnabled(true);
        Toast.makeText(this, "Device information collected!", Toast.LENGTH_SHORT).show();
    }

    private String getKernelVersion() {
        try {
            Process process = Runtime.getRuntime().exec("uname -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            return line;
        } catch (IOException e) {
            return null;
        }
    }

    private void checkPermissionAndSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ - scoped storage, no permission needed for Downloads
                saveDeviceInfo();
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission for Android 6-10
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                saveDeviceInfo();
            }
        } else {
            saveDeviceInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveDeviceInfo();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save file.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveDeviceInfo() {
        try {
            String codename = Build.DEVICE;
            String fileName = "twrp-builder-" + codename + ".txt";
            
            File downloadsDir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - use public Downloads directory
                downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            } else {
                // Android 9 and below
                downloadsDir = new File(Environment.getExternalStorageDirectory(), "Download");
            }
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            File file = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(deviceInfo.getBytes());
            fos.close();
            
            Toast.makeText(this, "Saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDeviceInfo(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDeviceInfo(newText);
                return true;
            }
        });
    }

    private void filterDeviceInfo(String query) {
        if (fullDeviceInfo == null || fullDeviceInfo.isEmpty()) {
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            // Show full device info when search is cleared
            infoTextView.setText(fullDeviceInfo);
            return;
        }

        // Filter lines that contain the search query (case-insensitive)
        String[] lines = fullDeviceInfo.split("\n");
        StringBuilder filteredInfo = new StringBuilder();
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        boolean foundMatch = false;

        for (String line : lines) {
            if (line.toLowerCase(Locale.ROOT).contains(lowerQuery)) {
                filteredInfo.append(line).append("\n");
                foundMatch = true;
            }
        }

        if (foundMatch) {
            infoTextView.setText(filteredInfo.toString());
        } else {
            infoTextView.setText("No results found for: \"" + query + "\"");
        }
    }

    private String getTouchDriverInfo() {
        StringBuilder touchInfo = new StringBuilder();
        boolean found = false;

        // Try multiple paths to find touch driver information
        String[] touchPaths = {
            "/proc/touchscreen/ts_information",
            "/sys/class/touchscreen/device/name",
            "/sys/class/input/input0/name",
            "/sys/class/input/input1/name",
            "/sys/class/input/input2/name",
            "/sys/class/input/input3/name",
            "/sys/devices/virtual/input/input0/name",
            "/sys/devices/virtual/input/input1/name",
            "/sys/devices/virtual/input/input2/name",
            "/sys/devices/virtual/input/input3/name"
        };

        for (String path : touchPaths) {
            String content = readFile(path);
            if (content != null && !content.isEmpty()) {
                // Check if it looks like touch-related info
                if (isTouchDriverContent(content)) {
                    touchInfo.append("Source: ").append(path).append("\n");
                    touchInfo.append(content).append("\n");
                    found = true;
                }
            }
        }

        // Try /proc/bus/input/devices
        String inputDevices = readFile("/proc/bus/input/devices");
        if (inputDevices != null && !inputDevices.isEmpty()) {
            String[] lines = inputDevices.split("\n");
            StringBuilder touchSection = new StringBuilder();
            boolean inTouchSection = false;

            for (String line : lines) {
                if (line.toLowerCase(Locale.ROOT).contains("name=") && isTouchDriverContent(line)) {
                    inTouchSection = true;
                    touchSection.append(line).append("\n");
                } else if (inTouchSection) {
                    if (line.trim().isEmpty()) {
                        inTouchSection = false;
                    } else {
                        touchSection.append(line).append("\n");
                    }
                }
            }

            if (touchSection.length() > 0) {
                touchInfo.append("Source: /proc/bus/input/devices\n");
                touchInfo.append(touchSection.toString()).append("\n");
                found = true;
            }
        }

        // Try dumpsys input command
        String dumpsysOutput = executeCommand("dumpsys input");
        if (dumpsysOutput != null && !dumpsysOutput.isEmpty()) {
            String[] lines = dumpsysOutput.split("\n");
            StringBuilder touchDumpsys = new StringBuilder();
            boolean inTouchSection = false;
            int lineCount = 0;

            for (String line : lines) {
                String lowerLine = line.toLowerCase(Locale.ROOT);
                if (lowerLine.contains("touch") && (lowerLine.contains("device") || lowerLine.contains("input"))) {
                    inTouchSection = true;
                    touchDumpsys.append(line).append("\n");
                    lineCount = 0;
                } else if (inTouchSection) {
                    if (lineCount < MAX_DUMPSYS_LINES && !line.trim().isEmpty()) {
                        touchDumpsys.append(line).append("\n");
                        lineCount++;
                    } else if (line.trim().isEmpty() || lineCount >= MAX_DUMPSYS_LINES) {
                        inTouchSection = false;
                    }
                }
            }

            if (touchDumpsys.length() > 0) {
                touchInfo.append("Source: dumpsys input\n");
                touchInfo.append(touchDumpsys.toString()).append("\n");
                found = true;
            }
        }

        return found ? touchInfo.toString() : null;
    }

    private boolean isTouchDriverContent(String content) {
        String lowerContent = content.toLowerCase(Locale.ROOT);
        for (String vendor : TOUCH_DRIVER_VENDORS) {
            if (lowerContent.contains(vendor)) {
                return true;
            }
        }
        return false;
    }

    private String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(path);
            if (!file.exists() || !file.canRead()) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString().trim();
        } catch (Exception e) {
            // File doesn't exist or can't be read
            return null;
        }
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // Wait for process with timeout to prevent hanging
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                process.waitFor(5, TimeUnit.SECONDS);
            } else {
                process.waitFor();
            }
            return output.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
