# True Astronaut

True Astronaut is a device-targeted full-feature Lunaris Singularity porting project for the Infinix Hot 30 (X6831) running ProjectInfinity-X Android 16 QPR1.

## Project target

- Device: Infinix Hot 30 (X6831 / Infinix-X6831)
- ROM: ProjectInfinity-X Android 16 QPR1 (API 36)
- Kernel reported: 4.19.191-g5048da07dcbc-dirty
- Feature reference: Lunaris Singularity 16.2
- Source branch and target Android release are distinct: Lunaris 16.2 is the feature source; Android 16 QPR1 is the target platform.

## Product architecture

The project is designed as a companion Android app plus an LSPosed module, with a small optional root helper for operations that require root or direct kernel/vendor access. Native ROM-side integration remains required for features that depend on Settings, framework, SystemUI, privileged permissions, services, overlays, or platform resources.

- **True Astronaut app:** feature dashboard, settings, profiles, backup/restore, compatibility status, diagnostics, and module management.
- **LSPosed module:** in-memory hooks for compatible SystemUI and Android framework behavior. Hook only the processes and methods needed by each feature.
- **Root helper (optional):** narrowly scoped privileged operations such as verified sysfs controls, boot-time setup, and device diagnostics. It must not duplicate functionality that can be safely implemented by the LSPosed module.
- **ROM integration:** native Settings, framework, SystemUI, SettingsLib, provider, overlay, permission, and device-tree changes where the upstream implementation requires them.

Magisk is an installation/root provider, not the primary feature implementation architecture. The root helper may be packaged as a small Magisk-compatible component when required.

## Feature implementation policy

Every Singularity feature must be inventoried and classified individually:

1. Native ROM / Settings / framework / SystemUI integration
2. LSPosed hook, only where native integration is unavailable and runtime signatures are verified
3. Companion-app-only functionality
4. Root helper / sysfs / kernel operation
5. Unsupported on this device or ROM

A settings toggle must never be shown as working unless a backend exists and its capability has been detected. Hardware- or framework-dependent features must be gated with an explicit unsupported/limited state.

## Device audit notes

The supplied audit reports Android 16 / API 36, MediaTek MT6768, Magisk Kitsune v30.71, and kernel 4.19.191. The target release for this port is Android 16 QPR1 (API 36). It reports ProjectInfinity fingerprint property `ro.infinity.fingerprint=Project_Infinity//gsi/20.09.2026`, and the refresh-rate manager service `com.infinity.display.IRefreshRateManagerService`.

The audit's reported fingerprint strings still identify the underlying Infinix Android 12 vendor build. These do not mean Android 12 is running; they are vendor/base fingerprints. The sensor inventory section is empty, so sensor-dependent features must not be assumed available. Display/backlight nodes and biometric service presence must be probed and validated before use; service presence alone does not prove a particular fingerprint sensor type or UDFPS support.

## Compatibility and safety

- Target Android 16 QPR1 only unless a future target is explicitly added.
- Detect the actual framework/ROM implementation before installing a hook.
- Scope hooks narrowly; fail closed when a class, method, field, service, or kernel node is absent.
- Never blindly write undocumented sysfs nodes or vendor properties.
- Never weaken SELinux globally to make a feature work.
- Provide a recovery path and per-feature disable switches.
- Test feature hooks individually before combining them.
- Keep app UI state synchronized with actual backend state, not just the requested toggle value.

## Source baseline

- Target ROM manifest: [ProjectInfinity-X/manifest, branch 16-QPR1](https://github.com/ProjectInfinity-X/manifest/tree/16-QPR1)
- Target ROM manifest commit: `91a7f7a4a8ff33ad0460c93541e9a0b5e4ecf8f8`
- Target vendor repository: [ProjectInfinity-X/vendor_infinity, branch 16-QPR1](https://github.com/ProjectInfinity-X/vendor_infinity/tree/16-QPR1)
- Target vendor commit: `f20a3edc17f69c04d33ad9c5015343bcf9e4ec46`
- Target SystemUI shared libraries: [ProjectInfinity-X/frameworks_libs_systemui, branch 16-QPR1](https://github.com/ProjectInfinity-X/frameworks_libs_systemui/tree/16-QPR1)
- Target SystemUI shared libraries commit: `9ae1abbda48666f38de85b595ab908a18947767f`
- Target manifest snippet declares `frameworks/base` and `packages/apps/Settings` from the LFS-dump Codeberg remote on revision `16-QPR1`; exact upstream LFS repository SHAs still need to be pinned.
- Feature source: [Lunaris Singularity 16.2](https://github.com/Lunaris-AOSP/packages_apps_Singularity/tree/16.2)
- [Project Infinity X manifest instructions](https://github.com/ProjectInfinity-X/manifest/tree/16-QPR1)
- [LSPosed](https://github.com/LSPosed/LSPosed)

## Compatibility layer status

A framework-neutral Kotlin API foundation now exists under `core-api/`. It defines runtime adapter, hook request, feature backend, capability/status contracts, a feature registry, and runtime selection. Architecture and provisional feature-backend policy are documented under `docs/architecture/`.

The repository now contains a Gradle multi-module Android project with `:app`, `:core-api`, and `:runtime-lsposed`. The companion app has an initial Jetpack Compose dashboard. The LSPosed adapter is packaged as a separate APK and has a legacy Xposed API entry point with a SystemUI runtime probe. Framework-specific code is isolated behind the framework-neutral runtime SPI.

The upstream Singularity Settings source import is complete: 180 source files, 426 resource files, and 4 assets (610 total), with no missing or extra imported files at the current source revision. CI now verifies the imported tree against upstream before building the APKs. Static analysis of the supplied ProjectInfinity-X SystemUI APK is recorded in [the device audit](docs/device-audit/projectinfinity-x-systemui-static-analysis.md); it confirms native Battery Bar and Status Bar Clock implementations and their setting-key strings.

The app-to-module configuration/status handshake is not implemented yet. Dashboard cards are UI scaffolding and are explicitly marked backend-pending. No functional Singularity feature hook, ROM-side platform patch, or device-tested release is claimed. The ProjectInfinity-X `16-QPR1` manifest, vendor, and SystemUI shared-library GitHub refs are now pinned. Its manifest points `frameworks/base` and `packages/apps/Settings` to Codeberg `LFS-dump`; public CI receives `repository not found` for those two URLs, so those exact source trees/SHAs and the locked manifest used for the current GSI build are still needed before native patches can safely be prepared.


## Device reference

- [Infinix Hot 30 Treble overlay audit](docs/device/infinix-hot30-overlay-reference.md)
- [QPR1 platform source map](docs/porting/qpr1-platform-source-map.md)
