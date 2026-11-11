package dev.sorcerer.trueastronaut.api

/**
 * Source-of-truth inventory for the complete Lunaris Singularity 16.2 Settings surface.
 *
 * These entries describe imported upstream resources. They are NOT proof that a feature
 * backend has been ported or is operational on the target ROM/device.
 */
enum class SingularityArea {
    BUTTONS,
    LOCKSCREEN,
    MISCELLANEOUS,
    NOTIFICATIONS,
    QUICK_SETTINGS,
    SOUND,
    STATUS_BAR,
    THEMES,
    SUPPORTING
}

data class SingularitySourceFeature(
    val id: String,
    val title: String,
    val area: SingularityArea,
    val sourceXml: String,
    val sourceClasses: List<String> = emptyList()
) {
    val sourceResourcePath: String
        get() = "vendor/lunaris/packages_apps_Singularity/res/xml/$sourceXml"
}

object SingularityFeatureCatalog {
    const val SOURCE_BRANCH = "16.2"
    const val SOURCE_REPOSITORY = "Lunaris-AOSP/packages_apps_Singularity"

    /**
     * Every entry is tied to an XML resource present in the imported upstream tree.
     * The catalog intentionally includes every settings screen, not only Quick Settings.
     */
    val all: List<SingularitySourceFeature> = listOf(
        // Buttons and power menu
        feature("buttons", "Buttons", SingularityArea.BUTTONS, "lunaris_settings_button.xml",
            "org/lunaris/settings/fragments/buttons/Buttons.java"),
        feature("button-backlight", "Button backlight brightness", SingularityArea.BUTTONS,
            "lunaris_settings_button.xml", "org/lunaris/settings/fragments/buttons/ButtonBacklightBrightness.java"),
        feature("power-menu", "Power menu", SingularityArea.BUTTONS, "power_menu.xml",
            "org/lunaris/settings/fragments/buttons/PowerMenuActions.java"),
        feature("power-menu-actions", "Power menu actions", SingularityArea.BUTTONS,
            "power_menu_actions.xml", "org/lunaris/settings/fragments/buttons/PowerMenuActions.java"),
        feature("power-off-verify", "Power-off verification", SingularityArea.BUTTONS,
            "power_off_verify.xml", "org/lunaris/settings/fragments/buttons/PowerOffVerify.kt"),

        // Lockscreen
        feature("lockscreen", "Lockscreen", SingularityArea.LOCKSCREEN,
            "lunaris_settings_lock_screen.xml", "org/lunaris/settings/fragments/lockscreen/LockScreen.java"),
        feature("clock-styles", "Lockscreen clock styles", SingularityArea.LOCKSCREEN,
            "clock_styles.xml", "org/lunaris/settings/fragments/lockscreen/ClockPickerFragment.kt"),
        feature("clock-preview", "Lockscreen clock preview", SingularityArea.LOCKSCREEN,
            "lockscreen_clock_preview_settings.xml", "org/lunaris/settings/fragments/lockscreen/CustomClockPreview.java"),
        feature("lockscreen-widgets", "Lockscreen widgets", SingularityArea.LOCKSCREEN,
            "rising_settings_lockscreen_widgets.xml", "org/lunaris/settings/fragments/lockscreen/LockScreenWidgets.java"),
        feature("media-art", "Lockscreen media art", SingularityArea.LOCKSCREEN,
            "media_art_settings.xml", "org/lunaris/settings/fragments/lockscreen/MediaArtSettings.java"),
        feature("now-playing", "Now Playing", SingularityArea.LOCKSCREEN,
            "nowplaying_settings.xml", "org/lunaris/settings/fragments/lockscreen/NowplayingSettings.kt"),
        feature("pulse", "Pulse visualizer", SingularityArea.LOCKSCREEN,
            "pulse_settings.xml", "org/lunaris/settings/fragments/lockscreen/PulseSettings.java"),
        feature("udfps-customization", "UDFPS customization", SingularityArea.LOCKSCREEN,
            "lunaris_settings_udfps_custom.xml", "org/lunaris/settings/fragments/lockscreen/UdfpsCust.java"),
        feature("wallpaper-depth", "Wallpaper depth effect", SingularityArea.LOCKSCREEN,
            "wallpaper_depth.xml", "org/lunaris/settings/fragments/lockscreen/WallpaperDepth.java"),
        feature("lockscreen-fonts", "Lockscreen fonts", SingularityArea.LOCKSCREEN,
            "clock_styles.xml", "org/lunaris/settings/fragments/lockscreen/font/FontManager.java"),

        // Miscellaneous and spoofing
        feature("miscellaneous", "Miscellaneous", SingularityArea.MISCELLANEOUS,
            "lunaris_settings_miscellaneous.xml", "org/lunaris/settings/fragments/miscellaneous/Miscellaneous.java"),
        feature("app-spoofing", "Advanced app spoofing", SingularityArea.MISCELLANEOUS,
            "spoofing.xml", "org/lunaris/settings/fragments/miscellaneous/AdvancedAppSpoofSettings.kt"),
        feature("play-integrity", "Play Integrity settings", SingularityArea.MISCELLANEOUS,
            "play_integrity_fix.xml", "org/lunaris/settings/fragments/miscellaneous/PlayIntegrityFix.kt"),
        feature("tricky-store", "Tricky Store", SingularityArea.MISCELLANEOUS,
            "tricky_store.xml", "org/lunaris/settings/fragments/miscellaneous/TrickyStore.kt"),
        feature("sensor-block", "Sensor block", SingularityArea.MISCELLANEOUS,
            "sensor_block_settings.xml", "org/lunaris/settings/fragments/miscellaneous/SensorBlock.java"),
        feature("shake-gestures", "Shake gestures", SingularityArea.MISCELLANEOUS,
            "shake_settings_gestures.xml", "org/lunaris/settings/fragments/miscellaneous/ShakeGestures.java"),
        feature("quick-switch", "Quick switch", SingularityArea.MISCELLANEOUS,
            "quick_switch.xml", "org/lunaris/settings/fragments/miscellaneous/QuickSwitch.java"),

        // Notifications
        feature("notifications", "Notifications", SingularityArea.NOTIFICATIONS,
            "lunaris_settings_notifications.xml", "org/lunaris/settings/fragments/notifications/Notifications.java"),
        feature("notification-light", "Notification light", SingularityArea.NOTIFICATIONS,
            "notification_light_settings.xml", "org/lunaris/settings/fragments/notifications/notificationlight/NotificationLightSettings.java"),
        feature("battery-light", "Battery light", SingularityArea.NOTIFICATIONS,
            "battery_light_settings.xml", "org/lunaris/settings/fragments/notifications/notificationlight/BatteryLightSettings.java"),
        feature("essential-notifications", "Essential notifications", SingularityArea.NOTIFICATIONS,
            "lunaris_settings_notifications.xml", "org/lunaris/settings/fragments/notifications/EssentialNotificationsSettings.kt"),

        // Quick Settings
        feature("quick-settings", "Quick Settings", SingularityArea.QUICK_SETTINGS,
            "lunaris_settings_quick_settings.xml", "org/lunaris/settings/fragments/quicksettings/QuickSettings.java"),
        feature("qs-layout", "Quick Settings layout", SingularityArea.QUICK_SETTINGS,
            "qs_layout_settings.xml", "org/lunaris/settings/fragments/quicksettings/LayoutSettings.java"),
        feature("qs-header-image", "Quick Settings header image", SingularityArea.QUICK_SETTINGS,
            "qs_header_image_settings.xml", "org/lunaris/settings/fragments/quicksettings/QsHeaderImageSettings.java"),

        // Sound
        feature("sound", "Sound", SingularityArea.SOUND, "lunaris_settings_sound.xml",
            "org/lunaris/settings/fragments/sound/Sound.java"),

        // Status bar and Dynamic Bar
        feature("status-bar", "Status bar", SingularityArea.STATUS_BAR,
            "lunaris_settings_status_bar.xml", "org/lunaris/settings/fragments/statusbar/StatusBar.java"),
        feature("battery-bar", "Battery bar", SingularityArea.STATUS_BAR,
            "battery_bar.xml", "org/lunaris/settings/fragments/statusbar/BatteryBar.java"),
        feature("status-bar-clock", "Status bar clock", SingularityArea.STATUS_BAR,
            "status_bar_clock.xml", "org/lunaris/settings/fragments/statusbar/Clock.java"),
        feature("clock-chip", "Clock chip", SingularityArea.STATUS_BAR,
            "clock_chip_settings.xml", "org/lunaris/settings/fragments/statusbar/ClockChipSettings.java"),
        feature("status-bar-lyrics", "Status bar lyrics", SingularityArea.STATUS_BAR,
            "status_bar_lyric_settings.xml", "org/lunaris/settings/fragments/statusbar/StatusbarLyricSettings.java"),
        feature("cutout-progress", "Cutout progress", SingularityArea.STATUS_BAR,
            "cutout_progress_settings.xml", "org/lunaris/settings/fragments/statusbar/CutoutProgressSettingsFragment.kt"),
        feature("dynamic-bar", "Dynamic Bar", SingularityArea.STATUS_BAR,
            "dynamic_bar.xml", "org/lunaris/settings/fragments/statusbar/DynamicBar.kt"),
        feature("dynamic-island", "Dynamic Island", SingularityArea.STATUS_BAR,
            "dynamic_island_settings.xml", "org/lunaris/settings/fragments/statusbar/DynamicIslandSettings.kt"),
        feature("lunaris-bar-cards", "Dynamic Bar cards and editor", SingularityArea.STATUS_BAR,
            "lunaris_bar_cards.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarCardsSettings.kt"),

        // Themes, display, AOD, wallpaper and fonts
        feature("themes", "Themes", SingularityArea.THEMES,
            "lunaris_settings_themes.xml", "org/lunaris/settings/fragments/themes/Themes.java"),
        feature("charging-animation", "Charging animation", SingularityArea.THEMES,
            "charging_animation_settings.xml", "org/lunaris/settings/fragments/themes/ChargingAnimationSettings.kt"),
        feature("doze", "Doze and always-on display", SingularityArea.THEMES,
            "doze_settings.xml", "org/lunaris/settings/fragments/themes/DozeSettings.java"),
        feature("aod-schedule", "Always-on display schedule", SingularityArea.THEMES,
            "always_on_display_schedule.xml", "org/lunaris/settings/fragments/themes/doze/AODSchedule.java"),
        feature("edge-light", "Edge lighting", SingularityArea.THEMES,
            "edge_light_settings.xml", "org/lunaris/settings/fragments/themes/EdgeLightSettings.kt"),
        feature("gradient", "Gradient effects", SingularityArea.THEMES,
            "gradient_settings.xml", "org/lunaris/settings/fragments/themes/GradientSettings.kt"),
        feature("smart-pixels", "Smart Pixels", SingularityArea.THEMES,
            "smart_pixels.xml", "org/lunaris/settings/fragments/themes/SmartPixels.java"),
        feature("video-wallpaper", "Video wallpaper", SingularityArea.THEMES,
            "video_wallpaper_settings.xml", "org/lunaris/settings/fragments/themes/VideoWallpaperFragment.java"),
        feature("wallpaper", "Wallpaper", SingularityArea.THEMES,
            "wallpaper_settings.xml", "org/lunaris/settings/fragments/themes/Wallpaper.java"),

        // Additional upstream screens and Dynamic Bar editor sub-screens
        feature("lock-glymps", "Lock Glymps", SingularityArea.LOCKSCREEN,
            "lock_glymps_settings.xml", "org/lunaris/settings/fragments/themes/LockGlympsSettings.java"),
        feature("dynamic-bar-guide", "Dynamic Bar guide", SingularityArea.STATUS_BAR,
            "dynamic_bar_guide_prefs.xml", "org/lunaris/settings/fragments/statusbar/DynamicBarGuide.kt"),
        feature("dynamic-bar-appearance", "Dynamic Bar appearance", SingularityArea.STATUS_BAR,
            "lunaris_bar_appearance.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-card-editor", "Dynamic Bar card editor", SingularityArea.STATUS_BAR,
            "lunaris_bar_card.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarCardSettings.kt"),
        feature("dynamic-bar-colors", "Dynamic Bar colors", SingularityArea.STATUS_BAR,
            "lunaris_bar_colors.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarCardSettings.kt"),
        feature("dynamic-bar-feature-style", "Dynamic Bar feature styles", SingularityArea.STATUS_BAR,
            "lunaris_bar_feature_style.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-features", "Dynamic Bar features", SingularityArea.STATUS_BAR,
            "lunaris_bar_features.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-gestures", "Dynamic Bar gestures", SingularityArea.STATUS_BAR,
            "lunaris_bar_gestures.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-media", "Dynamic Bar media", SingularityArea.STATUS_BAR,
            "lunaris_bar_media.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-motion", "Dynamic Bar motion", SingularityArea.STATUS_BAR,
            "lunaris_bar_motion.xml", "org/lunaris/settings/fragments/statusbar/LunarisBarOptions.kt"),
        feature("dynamic-bar-preference-templates", "Dynamic Bar preference templates", SingularityArea.SUPPORTING,
            "lunaris_bar_preference_templates.xml"),
        feature("app-hide-list", "App hide list", SingularityArea.MISCELLANEOUS,
            "hide_applist_prefs.xml", "org/lunaris/settings/fragments/miscellaneous/UserSelectedAppSpoofSettings.kt"),
        feature("haptics", "Haptics settings", SingularityArea.SOUND,
            "haptics_settings.xml"),

        // Source-wide preference infrastructure and shared resources
        feature("settings-preferences", "Settings preference infrastructure", SingularityArea.SUPPORTING,
            "lunaris_settings.xml", "org/lunaris/settings/preferences/GlobalSettingsStore.java",
            "org/lunaris/settings/preferences/SystemSettingsStore.java",
            "org/lunaris/settings/preferences/SecureSettingsStore.java"),
        feature("color-pickers", "Color picker infrastructure", SingularityArea.SUPPORTING,
            "lunaris_bar_colors.xml", "org/lunaris/settings/preferences/colorpicker/ColorPickerPreference.java"),
        feature("font-management", "Font management", SingularityArea.SUPPORTING,
            "clock_styles.xml", "org/lunaris/settings/fragments/themes/fonts/FontManager.java"),
        feature("wallpaper-processing", "Wallpaper processing and subject extraction", SingularityArea.SUPPORTING,
            "wallpaper_depth.xml", "org/lunaris/settings/utils/PortraitSegmenter.kt",
            "org/lunaris/settings/utils/WallpaperSubjectExtractorService.kt")
    )

    private fun feature(
        id: String,
        title: String,
        area: SingularityArea,
        xml: String,
        vararg classes: String
    ) = SingularitySourceFeature(
        id = id,
        title = title,
        area = area,
        sourceXml = xml,
        sourceClasses = classes.toList()
    )

    fun byArea(area: SingularityArea): List<SingularitySourceFeature> =
        all.filter { it.area == area }

    fun find(id: String): SingularitySourceFeature? =
        all.firstOrNull { it.id == id }
}
