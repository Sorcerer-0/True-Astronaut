# Lunaris Singularity 16.2 Quick Settings Port Plan

## Authoritative source

The source of truth is the Lunaris-AOSP organization, not Iconify or XMorphClock.

- Settings app: https://github.com/Lunaris-AOSP/packages_apps_Singularity/tree/16.2
- Framework/SystemUI: https://github.com/Lunaris-AOSP/frameworks_base/tree/16.2

Iconify and XMorphClock are implementation-pattern references only. Their feature sets must not be substituted for Lunaris.

## Confirmed Settings UI inventory

From `packages_apps_Singularity/res/xml/lunaris_settings_quick_settings.xml` (64 keys):

### QS misc
- qs_mist_enabled
- status_bar_custom_header
- enable_lockscreen_quick_settings
- qs_split_shade
- qs_bounce_effect
- qs_fling_haptic
- qs_split_notification
- qs_header_clock_style
- qs_media_art_scrim_enabled
- qs_media_art_dim_amount

### QS widget
- qs_widget_panel
- qs_widget_utility_panel
- qs_widget_utility_tile_rounded
- qs_widget_ios_music
- qs_widget_slider_corner
- qs_widget_panel_order

### QS layout
- shade_scrim_alpha
- notification_scrim_alpha
- qs_panel_style
- qs_tile_icon_shape
- qs_tile_animation_style
- qs_tile_label_hide
- qs_tile_style_minimal
- qs_tile_style_minimal_invert
- qs_tile_shape
- qs_use_modified_tile_spacing
- qs_layout_columns_rows
- qs_tile_haptic

### QS scrim
- single_qs_tone_enabled
- dual_target_tile_style
- qs_tile_alternate_color
- notification_bg_alternate_color

### QS scrim colors
- custom_shade_color_enabled
- custom_shade_color
- custom_notif_scrim_color_enabled
- custom_notif_scrim_color

### Brightness slider
- qs_brightness_slider_style
- qs_show_brightness_slider
- qs_brightness_slider_shape
- qs_brightness_slider_position
- qs_brightness_slider_haptic
- qs_show_auto_brightness

### Volume slider
- qs_volume_slider_style
- qs_show_volume_slider
- qs_volume_slider_shape
- qs_volume_slider_position
- qs_volume_slider_haptic
- qs_show_ringer_button

### Footer
- empty_shade_messages
- qs_show_data_usage
- qs_show_data_usage_tile
- qs_footer_show_edit
- qs_footer_show_power_menu
- qs_footer_show_settings

### Media player
- qs_show_media_player
- qs_compact_media_player_mode
- com.android.system.qs.qsmedia

## Additional layout keys

From `res/xml/qs_layout_settings.xml`:
- qs_tiles_columns (2..6, default 4)
- qs_tiles_columns_landscape (2..6, default 4)
- qs_tiles_rows (2..5, default 4)
- qs_tiles_rows_landscape (1..3, default 2)
- qqs_tiles_rows (1..3, default 2)
- qqs_tiles_rows_landscape (currently commented out in XML)
- qs_classic_tile_icon_size (40..96, default 64)
- qs_classic_tile_row_spacing (0..32, default 8)

From `res/xml/qs_header_image_settings.xml`:
- status_bar_custom_header
- qs_header_provider
- daylight_header_pack
- custom_header_browse
- file_header_select
- status_bar_custom_header_height (100..200, default 142)
- status_bar_custom_header_shadow (0..200, default 0)

## Confirmed SystemUI-level Lunaris implementation

The Lunaris 16.2 `frameworks_base` branch contains native code under
`packages/SystemUI/src/com/android/systemui/qs/panels/` including:

- AxQsSettingsRepository.kt
- LunarisQsGradientSettings.kt
- LunarisQsMediaStyle.kt
- LunarisQsSliderStyle.kt
- LunarisQsSpacing.kt
- LunarisQsWidget.kt and widget service/host classes
- LunarisQsPhotoFrame.kt and photo editor/provider/storage
- tile folders, tile animations, media-player variants and slider implementations

Also present:
- `core/java/com/android/internal/util/lunaris/QsHeaderImageGeometry.java`
- `core/java/com/android/internal/widget/QsHeaderImageView.java`

The native Settings repository observes settings and exposes state flows for panel mode,
tile layout, order, spans, control positions, widget/photo configuration, gradient,
spacing, slider styles, media style, grid columns/rows, etc. This is stronger evidence
that many QS capabilities are SystemUI-integrated rather than isolated settings keys.

## Porting constraints

1. Do not label a settings UI toggle as ported unless its runtime SystemUI behaviour is implemented.
2. For every feature, map: settings key + type + default + valid range + observer + SystemUI consumer + side effects.
3. Determine whether the target ProjectInfinity-X SystemUI contains a native equivalent before hooking.
4. Prefer overriding/extending an existing native view/controller. Preserve original state and provide restore/disable behaviour.
5. Use per-feature adapters and fail-safe hooks; never install guessed method signatures.
6. The app must write through a defined privileged bridge. Do not rely on ordinary Settings.System reads of ROM-hidden keys.
7. Avoid restarting SystemUI automatically during early tests; provide explicit apply/restart controls.
8. Record features unavailable on this target build rather than displaying nonfunctional controls.

## Next implementation phase

Build the Lunaris QS key catalogue and settings UI as data-driven controls in True Astronaut,
then implement SystemUI consumers incrementally beginning with low-risk scalar settings
(layout/spacing/slider style), followed by complex panel/media/widgets/header-image behaviour.
