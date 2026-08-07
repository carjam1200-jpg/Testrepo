# Aero Android Theme

Aero Android Theme is an original Android theme controller built around the Shizuku API.

## What it does

- Uses an original Aero-inspired dark UI.
- Detects whether Shizuku is running.
- Requests Shizuku permission.
- Uses the Shizuku shell identity to request Android's `uimode` dark/light setting.
- Does not copy code from another theme project.

## Build

Open the `aero-android-theme-shizuku` directory in Android Studio and let Gradle sync. Build the `app` module to produce an APK.

## Notes

Shizuku must be installed and running before the privileged controls can work. Android may restrict or ignore system UI commands depending on the device/ROM.

Shizuku API documentation: https://github.com/RikkaApps/Shizuku-API
