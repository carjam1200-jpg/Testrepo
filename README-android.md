# Android native To-Do app

This branch adds a minimal native Android app (Kotlin) that implements the To-Do list using Room for local persistence.

How to build
1. Install Android Studio (recommended).
2. Open this repository's `android-native` branch in Android Studio (File -> Open -> select the repo folder).
3. Let Android Studio sync Gradle and download dependencies.
4. Run on an emulator or device (Build -> Run).

Notes
- The app package is `com.carjam.todo`.
- Data is stored in a Room database (`todos.db`) private to the app.
- The UI supports adding tasks, toggling complete, deleting, and clearing completed tasks.

If you want, I can:
- Build an APK for you and attach it here.
- Add editing of tasks.
- Add multi-window/tab sync or backup/export to JSON.
