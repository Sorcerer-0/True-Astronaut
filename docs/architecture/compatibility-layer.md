# Runtime Compatibility Layer

## Goal

Keep Singularity feature implementations independent from LSPosed/Xposed APIs and from any future compatible hook runtime. The first runtime adapter may target LSPosed, but the feature layer must compile against `core-api` only.

## Layers

```text
True Astronaut Android app
        |
        | feature configuration / status contract
        v
Feature API + Feature Registry  <--- framework-neutral
        |
        +---- Feature backends
        |       +---- Status bar features
        |       +---- Lockscreen features
        |       +---- Gestures and sensors
        |       +---- Themes and charging
        |
        v
Runtime SPI (RuntimeAdapter / HookRequest)
        |
        +---- LSPosed adapter (isolated module)
        +---- Future compatible adapter
        |
        v
Android framework / SystemUI
```

The root helper is a separate optional backend for verified sysfs, boot-time, or privileged operations. It is not a hook runtime and must not be used as a substitute for framework hooks.

## Contracts

- `RuntimeAdapter`: probes a runtime and installs/uninstalls hook requests.
- `HookRequest`: framework-neutral description of the process, class, method, and owning feature.
- `FeatureBackend`: probes device capability, reads actual status, and applies a configuration.
- `FeatureRegistry`: dispatches feature operations and refuses to report success when no backend/capability exists.
- `RuntimeSelector`: selects an available adapter; feature implementations never select or import a framework directly.

## Required rules

1. No LSPosed/Xposed classes in `core-api` or feature modules.
2. Keep framework-specific entry points, callback wrappers, and API dependencies inside the adapter module.
3. Hook only explicitly required processes and methods.
4. Verify target class/method/signature before installing a hook. Missing targets return Unsupported/Failed and must not crash SystemUI.
5. Hook installation must be idempotent.
6. Feature UI must show backend-reported status, not merely the requested preference value.
7. Capability probes must verify actual hardware/service/node behavior; property presence alone is insufficient.
8. Every feature must have an independent disable path and documented verification test.
9. Do not weaken SELinux globally or write undocumented sysfs nodes.
10. Runtime selection must fail closed if no tested adapter is available.

## Android 16 / X6831 notes

The supplied audit identifies Android 16/API 36, MT6768, ProjectInfinity-X and a service named `com.infinity.display.IRefreshRateManagerService`. It does not prove that any particular hook runtime is installed or compatible. The sensor inventory was empty. Fingerprint service presence does not establish UDFPS hardware.

Therefore:
- Runtime adapter availability is detected at runtime; no Android 16 support is assumed.
- UDFPS-only features remain unavailable until sensor type and display integration are confirmed.
- Sensor-dependent features remain capability-gated.
- Refresh-rate integration must validate the service API and supported modes before exposing controls.

## Current implementation status

The contracts in `core-api` are framework-neutral Kotlin source. They are an architecture foundation, not yet a complete Android Gradle project or a functional LSPosed adapter. The next implementation step is to add the Android app/module build, then implement and device-test the LSPosed adapter behind this SPI.
