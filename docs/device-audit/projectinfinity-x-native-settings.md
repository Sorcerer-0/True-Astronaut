# ProjectInfinity-X native settings backend

## Verified namespace

On the supplied X6831 / ProjectInfinity-X Android 16 device:
- settings get system statusbar_battery_bar returned 0.
- settings get secure statusbar_battery_bar returned null.
- settings list system returned the Battery Bar and clock keys.
- settings list secure returned no matching keys.

This verifies Settings.System as the observed namespace for these keys on this build.

## Values observed

Battery Bar:
- statusbar_battery_bar=0
- statusbar_battery_bar_animate=1
- statusbar_battery_bar_battery_low_color=-16777216
- statusbar_battery_bar_blend_color=1
- statusbar_battery_bar_blend_color_reverse=0
- statusbar_battery_bar_charging_color=-16777216
- statusbar_battery_bar_color=-16777216
- statusbar_battery_bar_enable_charging_color=1
- statusbar_battery_bar_style=0
- statusbar_battery_bar_thickness=null

Status Bar Clock:
- status_bar_clock_bold=1
- status_bar_clock_date_display=2
- status_bar_clock_date_format=EEEdd
- status_bar_clock_date_position=0
- status_bar_clock_date_style=0
- status_bar_clock_period_separator=0
- status_bar_clock_seconds=1

## App implementation

The companion app uses Settings.System.canWrite(), getString(), and putInt(). It requests Android's Modify system settings special access; the user must grant it in Android Settings.

Only boolean keys with observed 0/1 values are exposed as switches. Style, thickness, date display/position/style, date format and ARGB color keys are not exposed until their accepted values and semantics are verified from the ROM implementation. Each write is read back and compared before the UI reports success.

This backend does not require LSPosed or root. It configures the ROM's existing SystemUI implementation. LSPosed remains reserved for features that have no native ROM backend.

## Safety notes

- No Settings.Secure writes are performed.
- No undocumented enum values are generated.
- No SystemUI process restart is requested automatically.
