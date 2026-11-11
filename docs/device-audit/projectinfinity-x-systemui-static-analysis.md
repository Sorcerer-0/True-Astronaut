# SystemUI APK static inspection — ProjectInfinity-X / X6831

Source artifact: user-supplied `SystemUI.apk` (51,341,479 bytes; SHA-256 `e73d97af78dc7e68c01c06406d0f082c065fb21174d8a181f3111cbfc08144a4`). This note records static DEX/resource-table observations only. It does not claim runtime behavior or identify the exact settings provider until verified on-device.

## Confirmed DEX layout

The APK contains three DEX files: `classes.dex`, `classes2.dex`, and `classes3.dex`.

## Battery Bar: native ROM implementation exists

The APK contains these classes:

- `com.android.systemui.infinity.batterybar.BatteryBar`
- `com.android.systemui.infinity.batterybar.BatteryBarController`

This is important: a Battery Bar implementation is already compiled into this SystemUI. True Astronaut should first expose and configure the ROM implementation rather than insert a second custom bar or hook the battery pipeline unnecessarily.

### BatteryBar methods

| Method | DEX signature |
|---|---|
| constructor | `(int, Context, boolean, boolean) -> void` |
| `isRunning` | `() -> boolean` |
| `onAttachedToWindow` | `() -> void` |
| `onDetachedFromWindow` | `() -> void` |
| `onTuningChanged` | `(String, String) -> void` |
| `setProgress$1` | `(int) -> void` |
| `start` | `() -> void` |
| `stop` | `() -> void` |

### BatteryBarController methods

| Method | DEX signature |
|---|---|
| constructor | `(Context, AttributeSet) -> void` |
| `addBars` | `() -> void` |
| `configThickness` | `() -> void` |
| `onAttachedToWindow` | `() -> void` |
| `onConfigurationChanged` | `(Configuration) -> void` |
| `onDetachedFromWindow` | `() -> void` |
| `onTuningChanged` | `(String, String) -> void` |

Controller fields include `mBatteryLevel`, `mBatteryCharging`, `mLocation`, `mStyle`, `mThickness`, and `isVertical`. The bar class has progress, charging, color, blend, location, and animation state.

### Battery Bar setting keys present in the APK

The DEX string pool contains the following keys:

- `system:statusbar_battery_bar`
- `system:statusbar_battery_bar_style`
- `system:statusbar_battery_bar_thickness`
- `system:statusbar_battery_bar_color`
- `system:statusbar_battery_bar_animate`
- `system:statusbar_battery_bar_enable_charging_color`
- `system:statusbar_battery_bar_charging_color`
- `system:statusbar_battery_bar_battery_low_color`
- `system:statusbar_battery_bar_blend_color`
- `system:statusbar_battery_bar_blend_color_reverse`

The `system:` prefix appears in the key strings, but this alone does not prove whether the runtime provider is `Settings.System`, `Settings.Secure`, or a ROM tuner abstraction. Verify the exact namespace and value encoding before writing settings.

## Status Bar Clock: ROM customization is already present

Class: `com.android.systemui.statusbar.policy.Clock`

Relevant methods confirmed from DEX method IDs:

- `getSmallTime(): CharSequence`
- `updateClock(boolean): void`
- `updateShowSeconds(): void`
- `onTuningChanged(String, String): void`
- `applyBoldClockStyle(): void`
- `onAttachedToWindow(): void`
- `onDetachedFromWindow(): void`
- `onConfigurationChanged(Configuration): void`
- `onDarkChanged(ArrayList, float, int): void`

The class contains ROM-specific state fields for clock date display/format/position/style, seconds, AM/PM style, bold style, visibility, and clock formatting. The APK string pool includes these keys:

- `system:status_bar_clock_bold`
- `system:status_bar_clock_seconds`
- `system:status_bar_clock_date_display`
- `system:status_bar_clock_date_format`
- `system:status_bar_clock_date_position`
- `system:status_bar_clock_date_style`
- `system:status_bar_clock_period_separator`

This suggests the ROM already has a configurable status-bar clock. Prefer exposing these existing controls in True Astronaut; do not replace the Clock view or hook `updateClock` until necessary.

## Standard SystemUI classes also present

- `com.android.systemui.battery.BatteryMeterView`
- `com.android.systemui.battery.BatteryMeterViewController`
- `com.android.systemui.statusbar.phone.PhoneStatusBarView`
- `com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment`

`BatteryMeterView` is the battery icon/percentage view; it is not the native Battery Bar class. Keep those separate in feature mapping.

## Recommended implementation order

1. Add a ROM-native backend for Battery Bar and Status Bar Clock.
2. Confirm settings namespace, valid enum values, defaults, and whether a SystemUI refresh/restart is required.
3. Make the companion app show the native feature's actual state and expose only validated options.
4. Keep LSPosed hooks as a fallback for features that have no native ROM backend.

## On-device verification required

Run these commands from Windows PowerShell while the target ROM is booted:

```powershell
adb shell settings get system statusbar_battery_bar
adb shell settings get secure statusbar_battery_bar
adb shell settings list system | Select-String "statusbar_battery_bar|status_bar_clock"
adb shell settings list secure | Select-String "statusbar_battery_bar|status_bar_clock"
```

These are read-only checks. Do not write values yet. The namespace that returns a value, plus source-level call-site inspection, will determine the correct backend contract.

## Limits of this inspection

- DEX string/method/field tables and APK resource entry names were inspected.
- This is not a complete decompilation of method bytecode.
- A string in the APK does not by itself prove a setting is active, user-facing, or backed by a particular provider.
- No hook should be installed based only on class/method names; verify runtime signatures and behavior on the target build.
