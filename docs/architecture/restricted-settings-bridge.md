# Restricted Settings Readable-Key Bridge

## Problem

ProjectInfinity-X exposes ROM settings such as `statusbar_battery_bar` through
`Settings.System`. On Android 12+ the framework rejects reads of hidden
Settings keys unless the key is marked readable or the caller is exempt.

## Implementation

The LSPosed module now loads in the True Astronaut app process
(`dev.sorcerer.trueastronaut`) and hooks
`android.provider.Settings$NameValueCache#getStringForUser(ContentResolver, String, int)`.

Before the framework readability check runs, the hook adds only the requested
key to that cache instance's `mReadableFields` set, and only if the key is in
the hard-coded allowlist in `RestrictedSettingsReadableHook.kt`.

Current allowlist:

- `statusbar_battery_bar`
- `statusbar_battery_bar_animate`
- `statusbar_battery_bar_enable_charging_color`
- `statusbar_battery_bar_blend_color`
- `statusbar_battery_bar_blend_color_reverse`
- `status_bar_clock_bold`
- `status_bar_clock_seconds`

This is intentionally narrower than overriding the framework's global
`isCallerExemptFromReadableRestriction()` check.

## LSPosed scope

Enable the module for:

- `dev.sorcerer.trueastronaut` — required for the Settings readable-key hook.
- `com.android.systemui` — retained as the future target for SystemUI feature hooks.

The hook currently performs no feature modification inside SystemUI.

## Write access

Reading and writing are separate permissions. The app still uses
`Settings.System.canWrite()` and the user's Modify system settings grant for
writes. The LSPosed readable-key hook does not grant write permission and does
not bypass SettingsProvider/AppOps write checks.

## Compatibility and validation

The hook depends on Android framework implementation details:
`Settings$NameValueCache`, `getStringForUser`, and `mReadableFields`.
It must be tested on the target ProjectInfinity-X Android 16 build. If these
internals change, the hook logs an installation failure and the app safely
treats restricted reads as unavailable instead of crashing.

A successful Gradle build only verifies compilation. Device testing is still
required to confirm the hook installs and values can be read and written.
