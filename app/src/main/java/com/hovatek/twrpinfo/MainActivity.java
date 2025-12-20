package com.hovatek.twrpinfo;

import android.Manifest;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView infoTextView;
    private Button collectButton;
    private Button saveButton;
    private String deviceInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = findViewById(R.id.infoTextView);
        collectButton = findViewById(R.id.collectButton);
        saveButton = findViewById(R.id.saveButton);

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
}
