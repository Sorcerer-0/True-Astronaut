# Lunaris Singularity 16.2 — Full Porting Inventory

## Scope and source of truth

This is a full-feature port, not a QS-only port and not a visual recreation. Every feature present in the Lunaris Singularity 16.2 source tree is in scope for True Astronaut on ProjectInfinity-X Android 16 QPR1 (API 36) / Infinix X6831.

- Settings source: https://github.com/Lunaris-AOSP/packages_apps_Singularity/tree/16.2
- Framework/SystemUI source: https://github.com/Lunaris-AOSP/frameworks_base/tree/16.2
- Target app/module: https://github.com/Sorcerer-0/True-Astronaut
- Target branch: port/lunaris-singularity-16.2

The same AOSP lineage is a strong compatibility advantage, but it does not make ROM-specific classes, resources, services, overlays, setting providers, vendor HALs, or privileged permissions identical. Port each feature's complete path: UI -> persisted config -> privileged bridge -> framework/SystemUI/service -> observable runtime state.

## Feature inventory from source paths

The following source areas are confirmed in the Singularity 16.2 tree. Each is in scope.

| Area | Source files / subfeatures | Target integration |
|---|---|---|
| Root settings/navigation | LunarisSettings.java, top-level preference controller | Compose navigation and feature registry |
| Buttons & power menu | Buttons.java, ButtonBacklightBrightness.java, PowerMenuActions.java, PowerOffVerify.kt | Settings bridge + SystemUI/PowerManager hooks; hardware checks for backlight |
| Lockscreen | LockScreen.java, LockScreenWidgets.java, MediaArtSettings.java, NowplayingSettings.kt, PulseSettings.java, WallpaperDepth.java | SystemUI/keyguard hooks and app-side configuration |
| Lockscreen clocks | ClockPickerFragment.kt, ClockStyles.kt, ClockUtils.kt, preview/color picker and clock layout resources | Port all clock styles, previews, font/color selection and keyguard renderer integration |
| UDFPS | UdfpsAnimation.kt, UdfpsCust.java, UdfpsIconPicker.kt, UDFPSUtils.java | Framework/SystemUI integration; hardware/HAL capability probe is mandatory |
| Lockscreen fonts | font manager, picker, preview and bundled font resources | App UI + supported keyguard font API/resource integration |
| Miscellaneous | Miscellaneous.java, AdvancedAppSpoofSettings.kt, IdleManagerSettings.kt, QuickSwitch.java, SensorBlock.java, ShakeGestures.java, spoofing, Pixel Props, Play Integrity Fix, TrickyStore, Duck RKP, Tensor targets | Separate backend per function; security-sensitive spoofing/integrity items require isolated review |
| Notifications | Notifications.java, EssentialNotificationsSettings.kt, battery/notification light preference and dialog classes | NotificationManager/SystemUI/Doze integration, LED hardware capability checks |
| Quick Settings | QuickSettings.java, LayoutSettings.java, QsHeaderImageSettings.java | Full QS panel/tile/header implementation in SystemUI, not just settings UI |
| Sound | Sound.java | AudioService, vibrator and supported system settings bridge |
| Status bar | StatusBar.java, BatteryBar.java, Clock.java, ClockChipSettings.java, DynamicBar.kt, DynamicIslandSettings.kt, LunarisBar classes, StatusbarLyricSettings.java, cutout progress settings | SystemUI status bar/notification/media pipeline hooks and custom views |
| Themes & display | Themes.java, DozeSettings.java, EdgeLightSettings.kt, GradientSettings.kt, LockGlympsSettings.java, SmartPixels.java, charging animation, display cutout fullscreen, video wallpaper | SystemUI/Keyguard/Wallpaper/Doze integration and panel capability checks |
| Doze | AOD schedule, receiver, service, pickup/proximity/tilt sensor classes | Doze service and sensor integration; lifecycle and battery validation |
| Wallpaper | Wallpaper.java, video wallpaper service/view model, depth and subject extraction | WallpaperManager + service integration |
| Shared preference framework | Global/System/Secure/Lineage settings stores; system property preferences; overlay switch; custom seekbar/list/color picker | Typed settings schema and permission-aware bridge |
| Shared utilities | device/system/telephony/media/image/color/restart/UDFPS utilities | Reimplement/adapt only required utilities; no blind copy of platform-specific assumptions |

## Resource inventory

All source resource groups are in scope where used by a feature:

- res/xml/: Singularity home/navigation XML and feature preference definitions, including buttons/power menu, lockscreen, UDFPS, miscellaneous, notifications, QS layout/header, sound, status bar, Dynamic Bar, Doze, edge light, gradient, fonts, wallpaper, charging animation, Smart Pixels, spoofing and Tricky Store.
- res/layout/: custom preference widgets, dialogs, previews, all lockscreen clock layouts, Dynamic Bar guide/editor, header image preview, font and wallpaper previews.
- res/drawable/ and res/drawable-night/: feature icons, shapes, previews, artwork, custom preference backgrounds and night variants.
- res/font/: bundled clock fonts and related font metadata.
- res/raw/ and res/raw-night/: animation assets including power-off verification.
- res/values/, res/values-night/, locale values: arrays, attrs, colors, dimensions, plurals, strings, styles and configuration values.
- assets/: feature assets, to be inventoried and licensed/attributed before redistribution.

Resource IDs and XML preference declarations are not runtime implementations. Each key must be mapped to its consumer and verified on the target ROM.

## Privileged access plan

Do not treat "privilege" as one permission. Select the narrowest mechanism that actually satisfies each API:

1. Normal Android permissions / special app access: request runtime permissions and user-granted special access (for example Modify system settings) where applicable.
2. LSPosed runtime hooks: use for verified in-process framework/SystemUI behavior where target class and method signatures are confirmed. Hooks must be narrow, idempotent, independently disableable, and fail closed.
3. Root helper (Magisk/su): use only for operations that genuinely require root, such as verified sysfs controls or controlled system-file operations. Check command exit status and actual read-back; never write guessed nodes.
4. Shizuku / shell identity: use only for APIs available to the granted shell identity. Shizuku does not grant platform signature permissions and is not a universal substitute for root.
5. ROM-side integration: where a feature requires signature|privileged permissions, framework resources, SystemUI code, service APIs, overlays, or privapp allowlists, implement it in the matching InfinityX source and rebuild the ROM. If the target ROM source is not available, keep the feature backend isolated and document the exact ROM-side patch required.
6. Platform signing: only a ROM build signed with the platform key can legitimately receive platform signature permissions. Do not attempt to extract keys or impersonate the platform signer.
7. SELinux: add narrowly scoped policy only when a verified denial demonstrates it is required. Never globally permissive SELinux as a shortcut.

A privilege request is not proof of effective access. Every backend must verify capability, apply the setting, read back actual state, and expose a reason when unavailable.

## Porting execution order

### Phase 0 — source and target audit
- Finish recursive source inventory, settings-key extraction, permission/API mapping and asset/license inventory.
- Compare each Singularity consumer with the corresponding ProjectInfinity-X Android 16 QPR1 implementation.
- Inspect the X6831 sensor, display, backlight, biometric and vendor service capabilities before enabling hardware-dependent features.

### Phase 1 — foundation
- Typed feature catalogue with stable IDs, defaults, ranges and capability requirements.
- Privileged bridge abstraction with app, LSPosed, root and ROM-side adapters.
- App/module configuration and status handshake.
- Per-feature logging, rollback/disable switch and diagnostics.

### Phase 2 — settings-backed low-risk features
- Port controls whose target ROM already has a native consumer.
- Confirm persistence and live effect before marking each feature implemented.

### Phase 3 — SystemUI / framework features
- Status bar, QS, Dynamic Bar, notifications, lockscreen, media, Doze, power menu and charging behavior.
- Port consumer-side code and required resources, not only preference screens.
- Keep each feature in an isolated hook/backend.

### Phase 4 — device/hardware-dependent features
- UDFPS, button backlight, notification light, Smart Pixels, sensor gestures and refresh-rate/display controls.
- Enable only after target hardware, HAL, kernel node and service behavior are verified.

### Phase 5 — validation
- Build app and module.
- Install on the X6831 test device; verify clean boot, SystemUI stability, settings persistence, process restart behavior, battery/thermal impact and rollback.
- Test every feature individually, then in combinations.

## Status vocabulary

- Inventoried: source files and settings declarations located.
- Mapped: source behavior and target consumer/API identified.
- Implemented: code exists in True Astronaut or required ROM patch.
- Built: relevant APK/ROM target compiles.
- Device-tested: behavior verified on ProjectInfinity-X Android 16 QPR1 (API 36) / X6831.
- Complete: all required states, persistence, failure handling and rollback verified.

Never call a feature "ported" merely because its screen or toggle exists.

## Current status

This commit establishes the complete porting scope and integration policy. It does not claim that Singularity features have already been copied, compiled, or device-tested. Existing QS inventory work remains in lunaris-singularity-16.2-qs-inventory.md and must be reconciled into this full catalogue.
