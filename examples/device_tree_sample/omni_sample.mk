# Release name
PRODUCT_RELEASE_NAME := Sample Device

# Inherit from the common TWRP/Omni product configuration
$(call inherit-product, vendor/omni/config/common.mk)

# Inherit from device configuration
$(call inherit-product, device/sample/sample/device.mk)

# Device identifier
# These values are used to identify your device in TWRP and must match your actual device
PRODUCT_DEVICE := sample
PRODUCT_NAME := omni_sample
PRODUCT_BRAND := Sample
PRODUCT_MODEL := Sample Device
PRODUCT_MANUFACTURER := Sample

# Build fingerprint (optional - get from: adb shell getprop ro.build.fingerprint)
PRODUCT_BUILD_PROP_OVERRIDES += \
    PRODUCT_NAME=sample \
    BUILD_FINGERPRINT=Sample/sample/sample:9/PKQ1.123456.001/123456789:user/release-keys \
    PRIVATE_BUILD_DESC="sample-user 9 PKQ1.123456.001 123456789 release-keys"
