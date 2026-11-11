# True Astronaut

True Astronaut is a device-targeted Singularity feature port for the Infinix Hot 30 (X6831) running ProjectInfinity-X Android 16.

## Project target

- Device: Infinix Hot 30 (X6831 / Infinix-X6831)
- ROM: ProjectInfinity-X Android 16 (API 36)
- Kernel reported: 4.19.191-g5048da07dcbc-dirty
- Feature reference: Lunaris Singularity 16.2

## Product architecture

The project is designed as a companion Android app plus an LSPosed module, with a small optional root helper for operations that require root or direct kernel/vendor access.

- **True Astronaut app:** feature dashboard, settings, profiles, backup/restore, compatibility status, diagnostics, and module management.
- **LSPosed module:** in-memory hooks for compatible SystemUI and Android framework behavior. Hook only the processes and methods needed by each feature.
- **Root helper (optional):** narrowly scoped privileged operations such as verified sysfs controls, boot-time setup, and device diagnostics. It must not duplicate functionality that can be safely implemented by the LSPosed module.

Magisk is an installation/root provider, not the primary feature implementation architecture. The root helper may be packaged as a small Magisk-compatible component when required.

## Feature implementation policy

Every Singularity feature must be inventoried and classified individually:

1. LSPosed hook
2. Companion-app-only functionality
3. Root helper / sysfs / kernel operation
4. ROM/framework integration required
5. Unsupported on this device or ROM

A settings toggle must never be shown as working unless a backend exists and its capability has been detected. Hardware- or framework-dependent features must be gated with an explicit unsupported/limited state.

## Device audit notes

The supplied audit reports Android 16 / API 36, MediaTek MT6768, Magisk Kitsune v30.71, and kernel 4.19.191. It reports ProjectInfinity fingerprint property `ro.infinity.fingerprint=Project_Infinity//gsi/20.09.2026`, and the refresh-rate manager service `com.infinity.display.IRefreshRateManagerService`.

The audit's reported fingerprint strings still identify the underlying Infinix Android 12 vendor build. These do not mean Android 12 is running; they are vendor/base fingerprints. The sensor inventory section is empty, so sensor-dependent features must not be assumed available. Display/backlight nodes and biometric service presence must be probed and validated before use; service presence alone does not prove a particular fingerprint sensor type or UDFPS support.

## Compatibility and safety

- Target Android 16 only unless a future target is explicitly added.
- Detect the actual framework/ROM implementation before installing a hook.
- Scope hooks narrowly; fail closed when a class, method, field, service, or kernel node is absent.
- Never blindly write undocumented sysfs nodes or vendor properties.
- Never weaken SELinux globally to make a feature work.
- Provide a recovery path and per-feature disable switches.
- Test feature hooks individually before combining them.
- Keep app UI state synchronized with actual backend state, not just the requested toggle value.

## Source baseline

- [Lunaris Singularity 16.2](https://github.com/Lunaris-AOSP/packages_apps_Singularity/tree/16.2)
- [Lunaris Android 16 manifest](https://github.com/Lunaris-AOSP/android/tree/16.2)
- [LSPosed](https://github.com/LSPosed/LSPosed)

## Compatibility layer status

A framework-neutral Kotlin API foundation now exists under `core-api/`. It defines runtime adapter, hook request, feature backend, capability/status contracts, a feature registry, and runtime selection. Architecture and provisional feature-backend policy are documented under `docs/architecture/`.

The repository now contains a Gradle multi-module Android project with `:app`, `:core-api`, and `:runtime-lsposed`. The companion app has an initial Jetpack Compose dashboard. The LSPosed adapter is packaged as a separate APK and has a legacy Xposed API entry point with a SystemUI runtime probe. Framework-specific code is isolated behind the framework-neutral runtime SPI.

A GitHub Actions workflow builds the companion app and adapter APK. The app-to-module configuration/status handshake is not implemented yet. Dashboard cards are UI scaffolding and are explicitly marked backend-pending. No functional Singularity feature hook or release is claimed. The build must pass CI, and runtime compatibility must be verified on ProjectInfinity-X Android 16 / X6831 before enabling feature hooks.
