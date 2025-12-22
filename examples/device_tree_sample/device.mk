LOCAL_PATH := device/sample/sample

# Inherit from the common configuration
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Time Zone data for recovery
PRODUCT_COPY_FILES += \
    bionic/libc/zoneinfo/tzdata:recovery/root/system/usr/share/zoneinfo/tzdata

# Prebuilt Kernel
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/prebuilt/kernel:kernel

# Recovery fstab
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/recovery.fstab:recovery/root/etc/recovery.fstab

# Properties
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.keystore=mdfpp \
    ro.hardware.gatekeeper=mdfpp

# Packages to include in recovery
PRODUCT_PACKAGES += \
    charger_res_images \
    charger

# Additional libraries for recovery
PRODUCT_PACKAGES += \
    libion \
    libxml2

# Device information
PRODUCT_DEVICE := sample
PRODUCT_NAME := omni_sample
PRODUCT_BRAND := Sample
PRODUCT_MODEL := Sample Device
PRODUCT_MANUFACTURER := Sample
