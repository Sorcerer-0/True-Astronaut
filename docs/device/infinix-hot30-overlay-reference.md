# Infinix Hot 30 device overlay reference

## Upstream reference
- Repository: https://github.com/Doze-off/vendor_hardware_overlay
- Branch: `pie`
- Device path: `Infinix/Hot30`
- Observed upstream tree commit: `0e31c9ed54182f96154bd380e66fbf84e837a05c`
- Runtime overlay package: `treble-overlay-infinix-hot30`
- Overlay package: `me.phh.treble.overlay.infinix.hot30`
- Target package: `android`
- Device match: `ro.product.device=Infinix-X6831`
- Overlay priority: `423`
- Overlay is static and installed to `$(TARGET_OUT_PRODUCT)/overlay`.

## Source files reviewed
- `Infinix/Hot30/Android.mk`
- `Infinix/Hot30/AndroidManifest.xml`
- `Infinix/Hot30/res/values/config.xml`
- `Infinix/Hot30/res/xml/power_profile.xml`

## Device-specific resources observed
- Tethering interface regexes and network attributes.
- Navigation bar enabled.
- Main display cutout filling disabled; cutout shape supplied.
- Navigation bar dimensions: 40 dp (height, landscape height, width).
- Quick QS offset: 36 dp.
- Status bar heights: 36 dp portrait / 28 dp landscape.
- Hotswap capability, audio-jack input event, color transform and IMS feature availability flags.
- Battery shutdown temperature resource value 599.
- Power profile includes 5000 mAh battery capacity, CPU cluster frequency/current arrays, Wi-Fi, Bluetooth, GPS, camera, modem and display estimates.

## Porting constraints
This is a legacy Pie-era Treble resource overlay and is reference data only. Do not blindly transplant it into Android 16 QPR1. Resource names, target package, runtime overlay policy, partition placement and resource definitions must be checked against the target Android 16 branch. In particular, never carry device power values forward unless confirmed against the actual X6831 kernel/HAL readings.

This overlay is not an implementation of Lunaris Singularity features. It only supplies device configuration resources.
