# Feature Backend Policy

Each feature is assigned one primary implementation backend after source and target-ROM analysis.

| Backend | Appropriate use | Not appropriate for |
|---|---|---|
| LSPosed adapter | Runtime hooks into verified SystemUI/framework methods | Direct kernel-node writes |
| App backend | Profiles, backup, previews, configuration and diagnostics UI | Pretending to change system behavior without a backend |
| Root helper | Verified sysfs, boot-time and narrowly scoped privileged operations | Replacing all framework hooks |
| ROM integration | New framework resources/APIs/privileged permissions that cannot be safely injected | Avoidable changes to the ROM source |

## Initial classification (provisional)

| Singularity feature area | Candidate backend | Required validation |
|---|---|---|
| Battery bar / status bar clock | Runtime hook or ROM integration | ProjectInfinity SystemUI class and method signatures |
| Dynamic Bar / lyrics | Runtime hook plus app configuration | SystemUI media/notification pipeline and API surface |
| Lockscreen clock / media art | Runtime hook or ROM integration | Keyguard implementation and resources |
| UDFPS animation | ROM/framework integration or unsupported | Confirm under-display sensor hardware and HAL integration |
| Shake gestures | App/service with sensor access | Identify actual sensor inventory and permissions |
| Sensor block | App/service or framework integration | Confirm sensor list, sensor privacy API and supported controls |
| Edge light | Runtime hook or ROM integration | Notification/heads-up pipeline and display cutout geometry |
| Charging animation | Runtime hook or ROM integration | Charging UI implementation and resource ownership |
| Smart Pixels | Runtime hook or ROM integration | Panel technology and supported pixel control path |
| Idle manager | App/service or ROM integration | Android 16 power/idle APIs and policy conflicts |
| App spoofing | Separate security-sensitive analysis | Integrity, app compatibility and policy boundaries |

This table is not a claim that these features are already implemented or supported. Every row requires source-level dependency tracing and target-device tests before a UI control is enabled.
