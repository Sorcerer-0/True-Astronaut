# True Astronaut — current LSPosed implementation

Target: ProjectInfinity-X Android 16 QPR1 / Infinix Hot 30 (X6831)

## Implemented in this revision

- LSPosed entry point loads in the companion app and SystemUI processes.
- SystemUI hook targets \`com.android.systemui.statusbar.policy.Clock#updateClock\`.
- Optional live seconds display, controlled by \`Settings.System.true_astronaut_clock_seconds\`.
- Optional bold status-bar clock, controlled by \`Settings.System.true_astronaut_clock_bold\`.
- Companion app exposes both switches and writes the module-owned settings keys.
- App-process readable-key allowlist includes only the two module-owned clock keys and the existing explicitly listed InfinityX battery-bar keys.

## Install and enable

1. Build/install the companion app APK and the runtime LSPosed APK from the same CI run.
2. In LSPosed Manager, enable **True Astronaut Runtime**.
3. Add scope for **System UI** (\`com.android.systemui\`) and **True Astronaut** (\`dev.sorcerer.trueastronaut\`).
4. Reboot or restart SystemUI.
5. Open True Astronaut, grant the Android **Modify system settings** permission, then enable the clock options.

If the ROM uses a different clock class or method, the hook logs an unavailable message and leaves SystemUI unchanged.

## Scope and limitations

This is an initial, real LSPosed feature implementation, not a full Lunaris Singularity port. It does not replace InfinityX framework, Settings, SystemUI resources, or vendor files. Features that require new SystemUI layouts, framework services, privileged permissions, or ROM-side resource overlays cannot be reproduced by this clock hook. Each additional feature needs its own verified target class/API and device testing.

The CI workflow builds APKs; a successful APK build does not prove runtime compatibility on the X6831. Verify the clock on-device and collect LSPosed logs before expanding hooks.
