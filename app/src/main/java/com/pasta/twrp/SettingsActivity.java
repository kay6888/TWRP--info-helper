package com.pasta.twrp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TWRPInfoPrefs";
    private static final String THEME_KEY = "theme_mode";
    
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private RadioGroup themeRadioGroup;
    private RadioButton radioLight;
    private RadioButton radioDark;
    private RadioButton radioSystem;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme before setting content view
        applyTheme();
        
        setContentView(R.layout.activity_settings);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
        radioSystem = findViewById(R.id.radioSystem);

        // Load current theme setting
        int currentTheme = preferences.getInt(THEME_KEY, THEME_SYSTEM);
        setCheckedRadioButton(currentTheme);

        // Set up theme change listener
        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int themeMode;
                if (checkedId == R.id.radioLight) {
                    themeMode = THEME_LIGHT;
                } else if (checkedId == R.id.radioDark) {
                    themeMode = THEME_DARK;
                } else {
                    themeMode = THEME_SYSTEM;
                }
                
                // Save preference
                preferences.edit().putInt(THEME_KEY, themeMode).apply();
                
                // Apply theme
                applyThemeMode(themeMode);
            }
        });
    }

    private void setCheckedRadioButton(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                radioLight.setChecked(true);
                break;
            case THEME_DARK:
                radioDark.setChecked(true);
                break;
            case THEME_SYSTEM:
            default:
                radioSystem.setChecked(true);
                break;
        }
    }

    private void applyTheme() {
        int themeMode = preferences.getInt(THEME_KEY, THEME_SYSTEM);
        applyThemeMode(themeMode);
    }

    private void applyThemeMode(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
