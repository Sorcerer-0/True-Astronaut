# Runtime adapter implementation

The initial `runtime-lsposed` module implements the framework-neutral `RuntimeAdapter` SPI using the legacy Xposed API surface. Its entry point is registered through `assets/xposed_init` and only probes/logs the SystemUI process.

No feature hook is installed yet. This is intentional: target SystemUI class/method signatures must be extracted from the exact ProjectInfinity-X build before hooking. The app UI currently reports the adapter as awaiting verification; a real app-to-module handshake has not yet been implemented.

## Build

Open the repository root in Android Studio with JDK 17 and Android SDK Platform 36 installed. Sync Gradle, then build:

```powershell
gradle :app:assembleDebug :runtime-lsposed:assembleDebug
```

The Xposed API dependency is compile-only and is not bundled into the module. Successful compilation does not prove that the installed LSPosed-compatible runtime supports Android 16 on this device.

## Next verification

1. Install the generated runtime adapter APK/module only on a recoverable test setup.
2. Enable it in the installed compatible framework and scope it to SystemUI.
3. Reboot and inspect Xposed/LSPosed logs for the `TrueAstronaut: SystemUI loaded` marker.
4. Confirm SystemUI remains stable.
5. Only then implement feature hooks against verified class/method signatures.
