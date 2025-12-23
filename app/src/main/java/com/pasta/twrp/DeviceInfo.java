package com.pasta.twrp;

public class DeviceInfo {
    public String codename;
    public String brand;
    public String model;
    public String manufacturer;
    public String androidVersion;
    public String screenResolution;
    public String architecture;
    public String soc;
    public String releaseDate;

    public DeviceInfo(String codename, String brand, String model, String manufacturer) {
        this.codename = codename;
        this.brand = brand;
        this.model = model;
        this.manufacturer = manufacturer;
        this.architecture = "arm64-v8a";
    }

    public DeviceInfo withAndroidVersion(String version) {
        this.androidVersion = version;
        return this;
    }

    public DeviceInfo withScreenResolution(String resolution) {
        this.screenResolution = resolution;
        return this;
    }

    public DeviceInfo withArchitecture(String arch) {
        this.architecture = arch;
        return this;
    }

    public DeviceInfo withSoC(String soc) {
        this.soc = soc;
        return this;
    }

    public DeviceInfo withReleaseDate(String date) {
        this.releaseDate = date;
        return this;
    }
}
