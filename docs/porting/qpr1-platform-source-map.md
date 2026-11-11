# ProjectInfinity-X Android 16 QPR1 source map for Lunaris Singularity

## Target baseline
- Manifest: https://github.com/ProjectInfinity-X/manifest/tree/16-QPR1
- Manifest commit: `91a7f7a4a8ff33ad0460c93541e9a0b5e4ecf8f8`
- Manifest snippet: `snippets/infinity.xml`
- Vendor tree: https://github.com/ProjectInfinity-X/vendor_infinity/tree/16-QPR1
- Vendor commit: `f20a3edc17f69c04d33ad9c5015343bcf9e4ec46`
- Shared SystemUI libraries: https://github.com/ProjectInfinity-X/frameworks_libs_systemui/tree/16-QPR1
- Shared SystemUI libraries commit: `9ae1abbda48666f38de85b595ab908a18947767f`
- Lunaris feature source: https://github.com/Lunaris-AOSP/packages_apps_Singularity/tree/16.2
- Lunaris framework source: https://github.com/Lunaris-AOSP/frameworks_base/tree/16.2
- Lunaris framework commit: `9e8848e3b1992aba2621839e0cd4d30bf68087ba`

## Manifest facts
The ProjectInfinity-X QPR1 manifest snippet explicitly declares:
- `frameworks/base` from the LFS-dump Codeberg remote at revision `16-QPR1`.
- `packages/apps/Settings` from the LFS-dump Codeberg remote at revision `16-QPR1`.
- `frameworks/libs/systemui` from ProjectInfinity-X at revision `16-QPR1`.
- `vendor/infinity` from ProjectInfinity-X at revision `16-QPR1`.

Therefore the GitHub org's vendor tree alone is not the correct patch target for Settings, framework and SystemUI features.

## QPR1 shared SystemUI library note

The pinned ProjectInfinity-X `frameworks/libs/systemui` repository is an AOSP shared UI library (animationlib, ambientlib, aconfig, etc.); it is not the `packages/SystemUI` implementation tree. The `packages/SystemUI` feature implementations still reside under the manifest-declared `frameworks/base` project. Do not treat `frameworks_libs_systemui` as the SystemUI patch target.

## Lunaris framework-level additions identified
The Lunaris framework tree contains custom support for the QS header image and lockscreen widgets, including:
- `core/java/com/android/internal/util/lunaris/QsHeaderImageGeometry.java`
- `core/java/com/android/internal/widget/QsHeaderImageView.java`
- `core/java/com/android/internal/widget/LockScreenWidgetCallback.java`
- `core/java/com/android/internal/widget/LockScreenWidgetInterface.java`

Other feature work is spread across framework core/resources, SystemUI, Settings, SystemUI shared libraries and ROM-specific providers. The framework Git tree is too large for a single recursive GitHub tree response (GitHub marks it truncated); inspect relevant subtrees individually before generating patches.

## Patch strategy
1. Pin exact LFS-dump SHAs for `frameworks/base` and `packages/apps/Settings` using their Codeberg branches.
2. Compare Lunaris 16.2 with the actual ProjectInfinity-X 16-QPR1 files at the exact path level.
3. Port Settings screens only together with their matching SystemUI/framework/provider APIs.
4. Port QS, status bar, lockscreen, power menu, notifications, sound, themes and miscellaneous controls as independent dependency groups.
5. Keep device-specific overlay values separate from ROM feature patches.
6. Build affected platform modules and then run full ROM integration builds; UI visibility alone is not evidence of a working feature.

## Current completeness
This document is a source map and dependency audit starter, not a claim that the full Lunaris feature set has been ported.


## QPR1 comparison status (source-tree inspection)
The Lunaris framework source exposes a non-trivial QS implementation surface under `packages/SystemUI/src/com/android/systemui/qs/panels/`:
- `data/`, `domain/`, `shared/`, `ui/` and `widgets/` subtrees.
- Custom widget Binder interfaces and host/service classes under `widgets/`.
- Additional custom QS repository/settings classes are in this platform tree and must be compared with the target `frameworks/base/packages/SystemUI` at the exact pinned LFS revision.

The ProjectInfinity-X `frameworks/libs/systemui` repository is separate shared UI libraries and does not contain `packages/SystemUI`. The correct target for these changes is the `frameworks/base` LFS project.

The current GitHub connector cannot access the Codeberg LFS remote. Therefore no truthful file-by-file target diff or build-ready framework patch can be prepared from the public GitHub refs alone. Do not copy Lunaris's complete `packages/SystemUI` directory wholesale: it contains a large Android platform tree and depends on AOSP base revisions, Dagger bindings, resource IDs, SettingsProvider contracts and feature-specific APIs.
