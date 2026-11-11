# Full Lunaris Singularity 16.2 Port

## Scope

- Target platform: ProjectInfinity-X Android 16 QPR1 (API 36), Infinix Hot 30 (X6831).
- Upstream feature source: Lunaris Singularity branch `16.2`. This source branch is not the target ROM branch.

This is a full feature port, not a Quick Settings-only port. The imported upstream source baseline is:

- Repository: `Lunaris-AOSP/packages_apps_Singularity`
- Branch: `16.2`
- Imported tree: `src/`, `res/`, and `assets/`
- Imported files verified: 180 source files, 426 resource files, and 4 assets (610 total).

The source inventory is tracked in `core-api/src/main/kotlin/dev/sorcerer/trueastronaut/api/SingularityFeatureCatalog.kt`. Every catalog entry points to an upstream XML resource and, where identified, its implementation class.

## Critical integration finding

The upstream Settings code is not a standalone Android application library. For example:

- `LunarisSettings` extends `com.android.settings.dashboard.DashboardFragment`.
- Feature fragments import `com.android.settings.R`, Settings preference/controller classes, Android internal APIs, and LineageOS settings providers.
- Shared feature code imports `com.android.internal.util.lunaris.*`, whose implementation lives in the Lunaris `frameworks_base` tree.
- Dynamic Bar / Dynamic Island UI and runtime behavior also have SystemUI-side implementation under `frameworks_base/packages/SystemUI`.

Therefore, copying these classes into the existing Gradle companion app or wrapping them in LSPosed would not produce a complete or correct port. The source must be integrated into the Android platform build, with matching Settings, framework, SystemUI, resources, permissions, and ROM configuration changes.

## Required integration layers

| Layer | Responsibility |
|---|---|
| `packages/apps/Settings` | Singularity dashboard, preference screens, controllers, dialogs, Settings resources |
| `frameworks/base` | Internal APIs, settings contracts, framework resources, services, policy and utility code |
| `packages/SystemUI` | QS, status bar, Dynamic Bar/Island, lockscreen, notification and charging UI behavior |
| `packages/SettingsLib` and provider modules | Preference/UI support and settings-provider integration |
| Device / vendor configuration | Hardware capability checks, overlays, permissions, init/property integration where required |
| True Astronaut companion app | Feature control surface, profiles, diagnostics, backup/restore and compatibility reporting |
| LSPosed adapter | Only narrowly scoped runtime extensions where platform integration is unavailable and hooks are verified |

## Porting rules

1. Keep every upstream feature in scope. Do not silently drop non-QS areas.
2. Port source and its complete resource/dependency chain, not just preference XML or UI.
3. Trace each feature into Settings, framework, SystemUI, SettingsLib, provider, overlay and device configuration before implementation.
4. Prefer native ROM integration for framework and SystemUI features. Do not emulate framework features with fragile hooks when the ROM source can be changed.
5. Do not expose a control as functional until its backend is implemented and its target runtime has been tested.
6. Gate hardware-specific functions using detected capabilities. Do not assume UDFPS, panel pixel control, keyboard/button backlight, or particular sensors exist on X6831.
7. Preserve upstream licensing headers and attribution in every copied or adapted file.
8. Keep changes isolated by feature area and build/test each integration stage before proceeding.

## Implementation phases

### Phase 0 — Source and target baseline
- [x] Import the complete Singularity `src/`, `res/`, and `assets/` tree.
- [x] Verify the GitHub Actions import workflow and imported file counts.
- [x] Add a source-backed catalog covering all Settings screens and feature groups.
- [x] Pin ProjectInfinity-X manifest branch `16-QPR1` and vendor baseline commit `f20a3edc17f69c04d33ad9c5015343bcf9e4ec46`.
- [x] Inspect the actual ProjectInfinity-X manifest branch `16-QPR1`.
- [x] Verify that the `frameworks/base` and `packages/apps/Settings` entries in `default.xml` are commented out; the manifest does not declare the claimed Codeberg `LFS-dump` remote for these paths.
- [ ] Identify the actual target source repositories/revisions supplying `frameworks/base`, Settings, SystemUI and SettingsLib for the user's InfinityX GSI build. Do not assume the Codeberg repository names or SHAs.
- [x] Inspect the supplied X6831 Treble overlay reference (`Doze-off/vendor_hardware_overlay`, `Infinix/Hot30`).
- [x] Record QPR1 source map and clarify target `packages/SystemUI` is in the manifest-declared `frameworks/base` project, not the separate shared SystemUI libraries repository.
- [ ] Confirm the X6831-specific device tree/kernel/vendor baseline used for the current GSI build.
- [ ] Compare the target Settings, frameworks/base, SystemUI and SettingsLib trees with Lunaris 16.2. The current companion-app repository does not contain these target platform trees, and the manifest inspection did not establish their source.

### Phase 1 — Platform dependency map
- [ ] Enumerate every Lunaris Settings source/resource reference and its target framework symbol.
- [ ] Enumerate every Lunaris framework addition and its SystemUI/Settings consumer.
- [ ] Produce an ordered patch series with conflict notes against the exact target ROM revisions.
- [ ] Identify proprietary/vendor and device-specific requirements separately from common platform code.

### Phase 2 — Settings integration
- [ ] Integrate the complete Singularity Settings entry and all preference screens.
- [ ] Integrate controllers, custom preferences, utilities, strings, arrays, drawables, layouts, fonts and assets.
- [ ] Add required SettingsLib and provider dependencies.
- [ ] Build the Settings target.

### Phase 3 — Framework and SystemUI integration
- [ ] Port required framework APIs, services, resources and settings contracts.
- [ ] Port all SystemUI implementations for QS, status bar, Dynamic Bar/Island, lockscreen, notifications, charging and related UI.
- [ ] Add required resource overlays, permissions and configuration.
- [ ] Build framework and SystemUI targets.

### Phase 4 — Device adaptation
- [ ] Audit X6831 sensor, fingerprint, display, refresh-rate, backlight, power and vendor interfaces.
- [ ] Add only verified device-specific adapters and overlays.
- [ ] Mark unavailable hardware features as unsupported/limited without breaking other features.

### Phase 5 — Validation and release
- [ ] Build a complete ROM image.
- [ ] Boot-test and collect logcat/kernel logs.
- [ ] Test each feature group independently, then test interactions and persistence.
- [ ] Document known limitations and recovery procedures.

## Current status

The upstream source import and catalog are complete. The imported tree is verified against the current upstream `16.2` revision by CI. **No claim is made that the platform integration, ROM build, or runtime feature port is complete.** The ProjectInfinity-X QPR1 manifest/vendor/SystemUI refs are identified, and the legacy Hot 30 overlay has been inventoried. Remaining blockers: the actual target platform source repositories and exact revisions for `frameworks/base`, Settings/SystemUI and SettingsLib, plus the device-tree/kernel revisions used for the current GSI. The earlier Codeberg LFS-dump attribution was not substantiated by the inspected QPR1 manifest and has been removed.
