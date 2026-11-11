# Lunaris Singularity 16.2 → ProjectInfinity-X 16-QPR1 Native Integration Map

## Target/source pins

- Target manifest: `ProjectInfinity-X/manifest`, branch `16-QPR1`
- Manifest commit observed: `91a7f7a4a8ff33ad0460c93541e9a0b5e4ecf8f8`
- Target vendor: `ProjectInfinity-X/vendor_infinity`, branch `16-QPR1`
- Vendor commit observed: `f20a3edc17f69c04d33ad9c5015343bcf9e4ec46`
- Target SystemUI shared libraries: `ProjectInfinity-X/frameworks_libs_systemui`, branch `16-QPR1`
- Shared libraries commit observed: `9ae1abbda48666f38de85b595ab908a18947767f`
- Target `frameworks/base`: manifest-declared Codeberg remote `https://codeberg.org/LFS-dump/frameworks_base.git`, branch `16-QPR1`
- Target Settings: manifest-declared Codeberg remote `https://codeberg.org/LFS-dump/packages_apps_Settings.git`, branch `16-QPR1`
- Feature source: `Lunaris-AOSP/frameworks_base` and `Lunaris-AOSP/packages_apps_Singularity`, branch `16.2`

The ProjectInfinity-X manifest's `snippets/infinity.xml` explicitly maps `frameworks/base` and `packages/apps/Settings` to the LFS-dump remote and maps `frameworks/libs/systemui` to the ProjectInfinity-X GitHub repository. Do not substitute AOSP or LineageOS refs for these ROM refs.

## Confirmed upstream implementation roots

The Lunaris `frameworks_base:16.2` tree contains feature implementation under:

- `core/java/com/android/internal/util/lunaris/`
- `core/res/res/values/lunaris_*.xml`
- `packages/SystemUI/src/com/android/systemui/lunaris/`
- `packages/SystemUI/src/com/android/systemui/qs/panels/`
- `packages/SystemUI/src/com/android/systemui/shade/`
- `packages/SystemUI/src/com/android/systemui/media/`
- `packages/SystemUI/src/com/android/systemui/lockscreenwidgets/`
- `packages/SystemUI/clockmaker/src/org/lunaris/clockmaker/`
- `packages/SystemUI/res/values/lunaris_*.xml`
- `packages/SettingsLib/SpaPrivileged/res/values/lunaris_strings.xml`
- `packages/SettingsProvider/res/values/lunaris_colors.xml`

The Singularity Settings app is a consumer of these APIs and setting keys; it is not the runtime implementation for most SystemUI features.

## Feature-to-platform dependency matrix

| Singularity category | Settings source entry points | Native runtime integration required |
|---|---|---|
| Buttons / power menu | `fragments/buttons/Buttons.java`, `PowerMenuActions.java`, `PowerOffVerify.kt` | `frameworks/base` global actions, power policy/services, and/or SystemUI global-actions UI; hardware-backed button light requires a verified device interface |
| Lock screen | `fragments/lockscreen/LockScreen.java`, `LockScreenWidgets.java`, `MediaArtSettings.java`, `NowplayingSettings.kt`, `PulseSettings.java`, `WallpaperDepth.java` | SystemUI keyguard, lockscreen rendering, media pipeline and widget host; framework keyguard/settings contracts |
| Clock styles / fonts | `ClockPickerFragment.kt`, `ClockStyles.kt`, `ClockUtils.kt`, font picker/manager classes | `packages/SystemUI/clockmaker`, keyguard clock host, framework resources and font assets |
| UDFPS | `UdfpsCust.java`, `UdfpsAnimation.kt`, `UdfpsIconPicker.kt` | Framework biometric/UDFPS APIs, SystemUI fingerprint overlay, device HAL/overlay. Must remain disabled on devices without under-display fingerprint hardware |
| Miscellaneous | `AdvancedAppSpoofSettings.kt`, `IdleManagerSettings.kt`, `QuickSwitch.java`, `SensorBlock.java`, `ShakeGestures.java`, spoofing / Pixel Props / integrity controllers | Per-feature framework service, ROM property, sensor or separate privileged app/module; integrity and spoofing features need security review and explicit backend support |
| Notifications | `Notifications.java`, `EssentialNotificationsSettings.kt`, notification/battery light preference classes | SystemUI notification pipeline, Doze and notification manager; physical LED features require a real LED interface |
| Quick Settings | `QuickSettings.java`, `LayoutSettings.java`, `QsHeaderImageSettings.java` | SystemUI QS panel, tile layout, header image view, brightness/flashlight sliders, tile/media/widget rendering and SettingsProvider persistence |
| Sound | `Sound.java` | AudioService, volume dialog/SystemUI, vibrator service and supported settings keys |
| Status bar / Dynamic Bar | `StatusBar.java`, `Clock.java`, `BatteryBar.java`, `DynamicBar.kt`, `DynamicIslandSettings.kt`, `StatusbarLyricSettings.java` | SystemUI status-bar window, notification/media/call/screen-recording pipeline, cutout layout and battery/clock rendering |
| Themes / Doze / display | `Themes.java`, `DozeSettings.java`, `EdgeLightSettings.kt`, `GradientSettings.kt`, `SmartPixels.java`, `ChargingAnimationSettings.kt` | Framework theme resources, SystemUI/Doze/AOD/charging surfaces, WallpaperManager and display/panel capability checks |
| Wallpaper | `Wallpaper.java`, video wallpaper service/view-model, depth and subject extraction classes | WallpaperManager, wallpaper picker/service, SystemUI lockscreen renderer and any required extraction service |

## QS-specific confirmed source files

The Lunaris framework source has concrete native QS implementations, not just preference definitions:

- Models/settings: `LunarisQsGradientSettings.kt`, `LunarisQsMediaStyle.kt`, `LunarisQsSliderStyle.kt`, `LunarisQsSpacing.kt`, `LunarisQsPhotoFrame.kt`, `LunarisQsTileFolderController.kt`, `LunarisQsWidget.kt`
- UI: `LunarisQsGradient.kt`, `LunarisQsPhotoCard.kt`, `LunarisQsTileFolder.kt`, `LunarisQsTileFolderScreen.kt`, `LunarisQsWidgetCard.kt`, `LunarisFlashlightSlider.kt`, `LunarisSliderLongPress.kt`
- Data/repository: `LunarisQsPhotoStorage.kt`, `LunarisQsPhotoProvider.kt`, `LunarisQsWidgetStore.kt`
- Activities/services/AIDL: `LunarisQsPhotoActivity.kt`, `LunarisQsWidgetActivity.kt`, `LunarisQsWidgetPicker.kt`, `LunarisQsWidgetService.kt`, `ILunarisQsWidgetHost.aidl`, `ILunarisQsWidgetListener.aidl`
- Shade integration: `LunarisQsPanelSwipeController.java`

All of these must be traced into the ProjectInfinity-X QPR1 SystemUI base before porting. Copying only the app preference XML would leave the controls without consumers.

## X6831 hardware gating

The target device is Infinix Hot 30 X6831 / MT6768. Treat device-dependent features as conditional:

- UDFPS-specific animations/icons: unsupported unless a real under-display fingerprint sensor is detected. A side-mounted fingerprint reader is not UDFPS.
- Notification/battery LED and button backlight: do not expose as working without a verified kernel/HAL node and read-back.
- Smart Pixels: do not enable without panel support.
- Sensor gestures, pickup/proximity doze and refresh-rate control: verify actual sensor list, display modes and framework service before enabling.
- Generic QS, clock, status-bar layout, media, software animation and wallpaper features can be evaluated independently of those hardware checks.

## Patch order

1. Resolve and pin exact target commits for Codeberg `frameworks_base` and `packages_apps_Settings` (automated by the new CI job).
2. Resolve the complete target source manifest with `repo manifest -r`; save the locked manifest in the port branch for reproducibility.
3. Diff target `frameworks/base`, Settings and shared SystemUI libraries against Lunaris 16.2; classify each upstream change as shared, ROM-specific, conflict, or hardware-gated.
4. Port Settings contracts and provider keys first, then framework APIs/resources/services, then SystemUI consumers, then Singularity Settings UI.
5. Build Settings/SystemUI/framework targets; only then build the full ROM/GSI source image.
6. Device-test each feature group on X6831 and mark status using the inventory's `Inventoried → Mapped → Implemented → Built → Device-tested → Complete` vocabulary.

## Status

- [x] Confirmed ProjectInfinity-X QPR1 manifest and its project remotes.
- [x] Confirmed Lunaris 16.2 native implementation roots and major feature classes.
- [x] Added CI job to resolve exact target GitHub and Codeberg branch SHAs.
- [ ] Obtain resolved Codeberg SHAs from CI; the first public CI probe returned `repository not found` for `LFS-dump/frameworks_base`, so the remote may be private or the repo path may need maintainer confirmation.
- [ ] Obtain the exact locked manifest used for the current GSI build (`repo manifest -r`) and the accessible Codeberg source URLs or exported source snapshots.
- [ ] Apply native platform patches and run platform build/tests.

No feature is marked implemented or device-tested by this inventory alone.
