package com.pasta.twrp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MAX_DUMPSYS_LINES = 5;
    private static final String[] TOUCH_DRIVER_VENDORS = {
        "touch", "synaptics", "focaltech", "goodix", "atmel", "cypress", "ft", "gt", "touchscreen", "ts"
    };
    private static final String PREFS_NAME = "TWRPInfoPrefs";
    private static final String THEME_KEY = "theme_mode";
    
    private TextView infoTextView;
    private Button collectButton;
    private Button saveButton;
    private Button searchButton;
    private Button donationButton;
    private SearchView onlineSearchView;
    private SearchView filterView;
    private LinearLayout searchLayout;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private String deviceInfo = "";
    private String fullDeviceInfo = "";
    private String searchedDeviceInfo = "";
    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before calling super.onCreate()
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = findViewById(R.id.infoTextView);
        collectButton = findViewById(R.id.collectButton);
        saveButton = findViewById(R.id.saveButton);
        searchButton = findViewById(R.id.searchButton);
        donationButton = findViewById(R.id.donationButton);
        onlineSearchView = findViewById(R.id.searchView);
        filterView = findViewById(R.id.filterView);
        searchLayout = findViewById(R.id.searchLayout);
        tabLayout = findViewById(R.id.tabLayout);
        progressBar = findViewById(R.id.progressBar);

        setupTabs();
        setupSearch();
        setupDonationButton();

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
        
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOnlineSearch();
            }
        });

        // Auto-collect on start
        collectDeviceInfo();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_find_recovery) {
            // Launch Recovery Finder with device info
            Intent intent = new Intent(this, RecoveryFinderActivity.class);
            intent.putExtra("codename", Build.DEVICE);
            intent.putExtra("manufacturer", Build.MANUFACTURER);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeMode = preferences.getInt(THEME_KEY, SettingsActivity.THEME_SYSTEM);
        
        switch (themeMode) {
            case SettingsActivity.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case SettingsActivity.THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case SettingsActivity.THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isSearchMode = tab.getPosition() == 1;
                if (isSearchMode) {
                    // Search tab selected
                    searchLayout.setVisibility(View.VISIBLE);
                    filterView.setVisibility(View.GONE);
                    collectButton.setEnabled(false);
                    if (!searchedDeviceInfo.isEmpty()) {
                        infoTextView.setText(searchedDeviceInfo);
                    } else {
                        infoTextView.setText("Enter a device model or codename and tap 'Search Online' to find device information.");
                    }
                } else {
                    // My Device tab selected
                    searchLayout.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                    collectButton.setEnabled(true);
                    infoTextView.setText(fullDeviceInfo);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void performOnlineSearch() {
        String query = onlineSearchView.getQuery().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a device model or codename", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        infoTextView.setText(getString(R.string.searching));
        
        DeviceSearchService.searchDevice(this, query, new DeviceSearchService.SearchCallback() {
            @Override
            public void onSearchComplete(String deviceInfo) {
                progressBar.setVisibility(View.GONE);
                searchedDeviceInfo = deviceInfo;
                infoTextView.setText(deviceInfo);
            }

            @Override
            public void onSearchError(String error) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = getString(R.string.search_error) + ": " + error;
                infoTextView.setText(errorMsg);
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
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
        
        // Root & Security Status
        info.append("--- ROOT & SECURITY STATUS ---\n");
        info.append("Root Available: ").append(isRootAvailable() ? "Yes" : "No").append("\n");
        info.append("Root Access Granted: ").append(isRootGranted() ? "Yes" : "No").append("\n");
        info.append("SELinux Status: ").append(getSELinuxStatus()).append("\n");
        info.append("Bootloader Status: ").append(getBootloaderStatus()).append("\n");
        info.append("System Partition: ").append(getSystemPartitionStatus()).append("\n");
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
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.save_verifying, Toast.LENGTH_SHORT).show();
        
        FileSaveHelper.saveDeviceInfo(this, deviceInfo, Build.DEVICE, new FileSaveHelper.SaveCallback() {
            @Override
            public void onSaveSuccess(String filePath, long fileSize) {
                progressBar.setVisibility(View.GONE);
                String successMsg = getString(R.string.save_success, filePath) + "\n" + 
                                  getString(R.string.save_verified, String.valueOf(fileSize));
                Toast.makeText(MainActivity.this, successMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveError(String error) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = getString(R.string.save_error, error);
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearch() {
        filterView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        
        onlineSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performOnlineSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupDonationButton() {
        donationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDonationDialog();
            }
        });
    }
    
    private void showDonationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_donation, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        
        Button paypalBtn = dialogView.findViewById(R.id.dialogPaypalButton);
        Button cashappBtn = dialogView.findViewById(R.id.dialogCashappButton);
        
        paypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPayPal();
                dialog.dismiss();
            }
        });
        
        cashappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCashApp();
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    private void openPayPal() {
        String paypalEmail = getString(R.string.paypal_email);
        
        try {
            // Try to open PayPal app with payment link
            Intent paypalIntent = new Intent(Intent.ACTION_VIEW);
            paypalIntent.setData(android.net.Uri.parse("https://www.paypal.me/" + paypalEmail.replace("@gmail.com", "")));
            startActivity(paypalIntent);
        } catch (Exception e) {
            // Fallback to web browser with PayPal URL
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(android.net.Uri.parse("https://www.paypal.com/paypalme/" + paypalEmail.split("@")[0]));
                startActivity(browserIntent);
            } catch (Exception ex) {
                // If all else fails, copy to clipboard
                copyToClipboard(paypalEmail, "PayPal Email");
                Toast.makeText(this, "PayPal not available. Email copied to clipboard.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void openCashApp() {
        String cashappHandle = getString(R.string.cashapp_handle);
        
        try {
            // Try to open Cash App with cashtag
            Intent cashappIntent = new Intent(Intent.ACTION_VIEW);
            cashappIntent.setData(android.net.Uri.parse("https://cash.app/" + cashappHandle));
            startActivity(cashappIntent);
        } catch (Exception e) {
            // If Cash App isn't installed, copy to clipboard
            copyToClipboard(cashappHandle, "CashApp Handle");
            Toast.makeText(this, "Cash App not available. Handle copied to clipboard.", Toast.LENGTH_LONG).show();
        }
    }

    private void copyToClipboard(String text, String label) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, text + " " + getString(R.string.donation_copied), Toast.LENGTH_SHORT).show();
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
                process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
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

    // Root detection methods
    // NOTE: These methods perform passive detection only. They do not modify the system
    // or grant root access. The isRootGranted() method attempts to execute a harmless
    // command to verify if root access would be available, but does not request or use
    // elevated privileges for any actual operations.
    
    private boolean isRootAvailable() {
        // Check for su binary in common paths
        String[] suPaths = {
            "/system/bin/su",
            "/system/xbin/su",
            "/system/sbin/su",
            "/sbin/su",
            "/vendor/bin/su",
            "/su/bin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        };
        
        for (String path : suPaths) {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRootGranted() {
        // This method attempts to execute a harmless 'id' command with su to check
        // if root access would be granted. It does not modify anything on the system.
        // If root is available, the user may see a root permission prompt from their
        // root management app (SuperSU, Magisk, etc.)
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su -c id");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.readLine();
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                } else {
                    process.waitFor();
                }
                
                return output != null && output.toLowerCase(Locale.ROOT).contains("uid=0");
            }
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
            // Try reading SELinux status file
            String selinuxFile = readFile("/sys/fs/selinux/enforce");
            if (selinuxFile != null && !selinuxFile.isEmpty()) {
                return selinuxFile.trim().equals("1") ? "Enforcing" : "Permissive";
            }
            
            // Try getenforce command
            String result = executeCommand("getenforce");
            if (result != null && !result.isEmpty()) {
                return result.trim();
            }
            
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getBootloaderStatus() {
        try {
            // Check via getprop
            String locked = executeCommand("getprop ro.boot.verifiedbootstate");
            if (locked != null && !locked.isEmpty()) {
                locked = locked.trim();
                if (locked.equalsIgnoreCase("green")) {
                    return "Locked";
                } else if (locked.equalsIgnoreCase("orange") || locked.equalsIgnoreCase("yellow")) {
                    return "Unlocked";
                }
            }
            
            // Alternative property
            String bootloaderLocked = executeCommand("getprop ro.boot.flash.locked");
            if (bootloaderLocked != null && !bootloaderLocked.isEmpty()) {
                bootloaderLocked = bootloaderLocked.trim();
                return bootloaderLocked.equals("1") ? "Locked" : "Unlocked";
            }
            
            // Check if OEM unlocking is allowed
            String oemUnlock = executeCommand("getprop sys.oem_unlock_allowed");
            if (oemUnlock != null && !oemUnlock.isEmpty()) {
                oemUnlock = oemUnlock.trim();
                return oemUnlock.equals("1") ? "Unlockable/Unlocked" : "Locked";
            }
            
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getSystemPartitionStatus() {
        try {
            // Check /proc/mounts for system partition
            String mounts = readFile("/proc/mounts");
            if (mounts != null && !mounts.isEmpty()) {
                String[] lines = mounts.split("\n");
                for (String line : lines) {
                    if (line.contains("/system ") || line.contains(" /system ")) {
                        if (line.contains("ro,") || line.contains(" ro ")) {
                            return "Read-Only (ro)";
                        } else if (line.contains("rw,") || line.contains(" rw ")) {
                            return "Read-Write (rw)";
                        }
                    }
                }
            }
            
            // Alternative: Try mount command
            String mountOutput = executeCommand("mount");
            if (mountOutput != null && !mountOutput.isEmpty()) {
                String[] lines = mountOutput.split("\n");
                for (String line : lines) {
                    if (line.contains("/system")) {
                        if (line.contains(" ro,") || line.contains(" ro ")) {
                            return "Read-Only (ro)";
                        } else if (line.contains(" rw,") || line.contains(" rw ")) {
                            return "Read-Write (rw)";
                        }
                    }
                }
            }
            
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
