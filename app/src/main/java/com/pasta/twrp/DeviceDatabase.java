package com.pasta.twrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeviceDatabase {
    
    private static final List<DeviceInfo> devices = new ArrayList<>();
    private static final Map<String, DeviceInfo> codeNameIndex = new HashMap<>();
    private static final Map<String, DeviceInfo> modelIndex = new HashMap<>();
    
    static {
        initializeDatabase();
    }
    
    private static void initializeDatabase() {
        // Google Pixel devices
        addDevice(new DeviceInfo("oriole", "Google", "Pixel 6", "Google")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Google Tensor")
            .withReleaseDate("October 2021"));
            
        addDevice(new DeviceInfo("raven", "Google", "Pixel 6 Pro", "Google")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1440x3120")
            .withSoC("Google Tensor")
            .withReleaseDate("October 2021"));
            
        addDevice(new DeviceInfo("bluejay", "Google", "Pixel 6a", "Google")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Google Tensor")
            .withReleaseDate("July 2022"));
            
        addDevice(new DeviceInfo("panther", "Google", "Pixel 7", "Google")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Google Tensor G2")
            .withReleaseDate("October 2022"));
            
        addDevice(new DeviceInfo("cheetah", "Google", "Pixel 7 Pro", "Google")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1440x3120")
            .withSoC("Google Tensor G2")
            .withReleaseDate("October 2022"));
            
        addDevice(new DeviceInfo("lynx", "Google", "Pixel 7a", "Google")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Google Tensor G2")
            .withReleaseDate("May 2023"));
            
        addDevice(new DeviceInfo("shiba", "Google", "Pixel 8", "Google")
            .withAndroidVersion("14")
            .withScreenResolution("1080x2400")
            .withSoC("Google Tensor G3")
            .withReleaseDate("October 2023"));
            
        addDevice(new DeviceInfo("husky", "Google", "Pixel 8 Pro", "Google")
            .withAndroidVersion("14")
            .withScreenResolution("1344x2992")
            .withSoC("Google Tensor G3")
            .withReleaseDate("October 2023"));
            
        addDevice(new DeviceInfo("bonito", "Google", "Pixel 3a XL", "Google")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1080x2160")
            .withSoC("Snapdragon 670")
            .withReleaseDate("May 2019"));
            
        addDevice(new DeviceInfo("sargo", "Google", "Pixel 3a", "Google")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1080x2220")
            .withSoC("Snapdragon 670")
            .withReleaseDate("May 2019"));
            
        addDevice(new DeviceInfo("coral", "Google", "Pixel 4 XL", "Google")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1440x3040")
            .withSoC("Snapdragon 855")
            .withReleaseDate("October 2019"));
            
        addDevice(new DeviceInfo("flame", "Google", "Pixel 4", "Google")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1080x2280")
            .withSoC("Snapdragon 855")
            .withReleaseDate("October 2019"));
            
        addDevice(new DeviceInfo("sunfish", "Google", "Pixel 4a", "Google")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 730G")
            .withReleaseDate("August 2020"));
            
        addDevice(new DeviceInfo("bramble", "Google", "Pixel 4a 5G", "Google")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 765G")
            .withReleaseDate("September 2020"));
            
        addDevice(new DeviceInfo("redfin", "Google", "Pixel 5", "Google")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 765G")
            .withReleaseDate("October 2020"));
            
        addDevice(new DeviceInfo("barbet", "Google", "Pixel 5a", "Google")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 765G")
            .withReleaseDate("August 2021"));
        
        // Samsung Galaxy S series
        addDevice(new DeviceInfo("beyond0lte", "Samsung", "Galaxy S10e", "Samsung")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1080x2280")
            .withSoC("Snapdragon 855 / Exynos 9820")
            .withReleaseDate("March 2019"));
            
        addDevice(new DeviceInfo("beyond1lte", "Samsung", "Galaxy S10", "Samsung")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1440x3040")
            .withSoC("Snapdragon 855 / Exynos 9820")
            .withReleaseDate("March 2019"));
            
        addDevice(new DeviceInfo("beyond2lte", "Samsung", "Galaxy S10+", "Samsung")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1440x3040")
            .withSoC("Snapdragon 855 / Exynos 9820")
            .withReleaseDate("March 2019"));
            
        addDevice(new DeviceInfo("x1s", "Samsung", "Galaxy S20", "Samsung")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1440x3200")
            .withSoC("Snapdragon 865 / Exynos 990")
            .withReleaseDate("March 2020"));
            
        addDevice(new DeviceInfo("y2s", "Samsung", "Galaxy S20+", "Samsung")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1440x3200")
            .withSoC("Snapdragon 865 / Exynos 990")
            .withReleaseDate("March 2020"));
            
        addDevice(new DeviceInfo("z3s", "Samsung", "Galaxy S20 Ultra", "Samsung")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1440x3200")
            .withSoC("Snapdragon 865 / Exynos 990")
            .withReleaseDate("March 2020"));
            
        addDevice(new DeviceInfo("o1s", "Samsung", "Galaxy S21", "Samsung")
            .withAndroidVersion("11 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 888 / Exynos 2100")
            .withReleaseDate("January 2021"));
            
        addDevice(new DeviceInfo("t2s", "Samsung", "Galaxy S21+", "Samsung")
            .withAndroidVersion("11 - 14")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 888 / Exynos 2100")
            .withReleaseDate("January 2021"));
            
        addDevice(new DeviceInfo("p3s", "Samsung", "Galaxy S21 Ultra", "Samsung")
            .withAndroidVersion("11 - 14")
            .withScreenResolution("1440x3200")
            .withSoC("Snapdragon 888 / Exynos 2100")
            .withReleaseDate("January 2021"));
            
        addDevice(new DeviceInfo("r0s", "Samsung", "Galaxy S22", "Samsung")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 8 Gen 1 / Exynos 2200")
            .withReleaseDate("February 2022"));
            
        addDevice(new DeviceInfo("g0s", "Samsung", "Galaxy S22+", "Samsung")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 8 Gen 1 / Exynos 2200")
            .withReleaseDate("February 2022"));
            
        addDevice(new DeviceInfo("b0s", "Samsung", "Galaxy S22 Ultra", "Samsung")
            .withAndroidVersion("12 - 14")
            .withScreenResolution("1440x3088")
            .withSoC("Snapdragon 8 Gen 1 / Exynos 2200")
            .withReleaseDate("February 2022"));
            
        addDevice(new DeviceInfo("dm1q", "Samsung", "Galaxy S23", "Samsung")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 8 Gen 2")
            .withReleaseDate("February 2023"));
            
        addDevice(new DeviceInfo("dm2q", "Samsung", "Galaxy S23+", "Samsung")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 8 Gen 2")
            .withReleaseDate("February 2023"));
            
        addDevice(new DeviceInfo("dm3q", "Samsung", "Galaxy S23 Ultra", "Samsung")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1440x3088")
            .withSoC("Snapdragon 8 Gen 2")
            .withReleaseDate("February 2023"));
            
        addDevice(new DeviceInfo("e1s", "Samsung", "Galaxy S23 FE", "Samsung")
            .withAndroidVersion("13 - 14")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 8 Gen 1 / Exynos 2200")
            .withReleaseDate("October 2023"));
        
        // OnePlus devices
        addDevice(new DeviceInfo("enchilada", "OnePlus", "OnePlus 6", "OnePlus")
            .withAndroidVersion("8.1 - 11")
            .withScreenResolution("1080x2280")
            .withSoC("Snapdragon 845")
            .withReleaseDate("May 2018"));
            
        addDevice(new DeviceInfo("fajita", "OnePlus", "OnePlus 6T", "OnePlus")
            .withAndroidVersion("9 - 11")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 845")
            .withReleaseDate("October 2018"));
            
        addDevice(new DeviceInfo("guacamoleb", "OnePlus", "OnePlus 7", "OnePlus")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 855")
            .withReleaseDate("May 2019"));
            
        addDevice(new DeviceInfo("guacamole", "OnePlus", "OnePlus 7 Pro", "OnePlus")
            .withAndroidVersion("9 - 12")
            .withScreenResolution("1440x3120")
            .withSoC("Snapdragon 855")
            .withReleaseDate("May 2019"));
            
        addDevice(new DeviceInfo("hotdogb", "OnePlus", "OnePlus 7T", "OnePlus")
            .withAndroidVersion("10 - 12")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 855+")
            .withReleaseDate("September 2019"));
            
        addDevice(new DeviceInfo("hotdog", "OnePlus", "OnePlus 7T Pro", "OnePlus")
            .withAndroidVersion("10 - 12")
            .withScreenResolution("1440x3120")
            .withSoC("Snapdragon 855+")
            .withReleaseDate("October 2019"));
            
        addDevice(new DeviceInfo("instantnoodle", "OnePlus", "OnePlus 8", "OnePlus")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 865")
            .withReleaseDate("April 2020"));
            
        addDevice(new DeviceInfo("instantnoodlep", "OnePlus", "OnePlus 8 Pro", "OnePlus")
            .withAndroidVersion("10 - 13")
            .withScreenResolution("1440x3168")
            .withSoC("Snapdragon 865")
            .withReleaseDate("April 2020"));
            
        addDevice(new DeviceInfo("kebab", "OnePlus", "OnePlus 8T", "OnePlus")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 865")
            .withReleaseDate("October 2020"));
            
        addDevice(new DeviceInfo("lemonade", "OnePlus", "OnePlus 9", "OnePlus")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 888")
            .withReleaseDate("March 2021"));
            
        addDevice(new DeviceInfo("lemonadep", "OnePlus", "OnePlus 9 Pro", "OnePlus")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1440x3216")
            .withSoC("Snapdragon 888")
            .withReleaseDate("March 2021"));
        
        // Xiaomi devices
        addDevice(new DeviceInfo("dipper", "Xiaomi", "Mi 8", "Xiaomi")
            .withAndroidVersion("8.1 - 10")
            .withScreenResolution("1080x2248")
            .withSoC("Snapdragon 845")
            .withReleaseDate("May 2018"));
            
        addDevice(new DeviceInfo("polaris", "Xiaomi", "Mi Mix 2S", "Xiaomi")
            .withAndroidVersion("8.0 - 10")
            .withScreenResolution("1080x2160")
            .withSoC("Snapdragon 845")
            .withReleaseDate("March 2018"));
            
        addDevice(new DeviceInfo("beryllium", "Xiaomi", "Poco F1", "Xiaomi")
            .withAndroidVersion("8.1 - 10")
            .withScreenResolution("1080x2246")
            .withSoC("Snapdragon 845")
            .withReleaseDate("August 2018"));
            
        addDevice(new DeviceInfo("cepheus", "Xiaomi", "Mi 9", "Xiaomi")
            .withAndroidVersion("9 - 11")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 855")
            .withReleaseDate("February 2019"));
            
        addDevice(new DeviceInfo("raphael", "Xiaomi", "Mi 9T Pro / K20 Pro", "Xiaomi")
            .withAndroidVersion("9 - 11")
            .withScreenResolution("1080x2340")
            .withSoC("Snapdragon 855")
            .withReleaseDate("May 2019"));
            
        addDevice(new DeviceInfo("lmi", "Xiaomi", "Poco F2 Pro / K30 Pro", "Xiaomi")
            .withAndroidVersion("10 - 12")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 865")
            .withReleaseDate("March 2020"));
            
        addDevice(new DeviceInfo("alioth", "Xiaomi", "Poco F3 / Mi 11X / K40", "Xiaomi")
            .withAndroidVersion("11 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 870")
            .withReleaseDate("March 2021"));
            
        addDevice(new DeviceInfo("munch", "Xiaomi", "Poco F4 / K40S", "Xiaomi")
            .withAndroidVersion("12 - 13")
            .withScreenResolution("1080x2400")
            .withSoC("Snapdragon 870")
            .withReleaseDate("April 2022"));
    }
    
    private static void addDevice(DeviceInfo device) {
        devices.add(device);
        codeNameIndex.put(device.codename.toLowerCase(Locale.ROOT), device);
        modelIndex.put(device.model.toLowerCase(Locale.ROOT), device);
    }
    
    public static DeviceInfo searchDevice(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        
        String normalizedQuery = query.toLowerCase(Locale.ROOT).trim();
        
        // Direct codename match
        if (codeNameIndex.containsKey(normalizedQuery)) {
            return codeNameIndex.get(normalizedQuery);
        }
        
        // Direct model match
        if (modelIndex.containsKey(normalizedQuery)) {
            return modelIndex.get(normalizedQuery);
        }
        
        // Partial model match
        for (DeviceInfo device : devices) {
            String modelLower = device.model.toLowerCase(Locale.ROOT);
            if (modelLower.contains(normalizedQuery) || normalizedQuery.contains(modelLower)) {
                return device;
            }
        }
        
        // Partial codename match
        for (DeviceInfo device : devices) {
            String codenameLower = device.codename.toLowerCase(Locale.ROOT);
            if (codenameLower.contains(normalizedQuery) || normalizedQuery.contains(codenameLower)) {
                return device;
            }
        }
        
        // Brand match - return first device of that brand
        for (DeviceInfo device : devices) {
            String brandLower = device.brand.toLowerCase(Locale.ROOT);
            if (brandLower.contains(normalizedQuery) || normalizedQuery.contains(brandLower)) {
                return device;
            }
        }
        
        return null;
    }
}
