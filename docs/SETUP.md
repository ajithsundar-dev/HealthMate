# Setup and build

This guide records the repository's current build configuration. The wrapper and Gradle files are the source of truth for version changes.

## Requirements

| Setting | Project value | Declared in |
| --- | --- | --- |
| Compile and target SDK | API 37 | [app/build.gradle.kts](../app/build.gradle.kts) |
| Minimum Android version | API 24 | [app/build.gradle.kts](../app/build.gradle.kts) |
| Gradle wrapper | 9.6.0 | [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties) |
| Android Gradle Plugin | 9.4.1 | [libs.versions.toml](../gradle/libs.versions.toml) |
| Gradle daemon JVM | Java 25 | [gradle-daemon-jvm.properties](../gradle/gradle-daemon-jvm.properties) |
| Java source/target compatibility | Java 11 | [app/build.gradle.kts](../app/build.gradle.kts) |

The daemon JVM and Java source compatibility serve different purposes. The project configures a Java 25 runtime for Gradle while retaining Java 11 source/target compatibility.

## Open in Android Studio

1. Clone the repository, or download and extract its ZIP.
2. Open the root folder containing `settings.gradle.kts`.
3. Use SDK Manager to install Android SDK Platform 37.
4. Let Gradle sync resolve the declared plugins and dependencies. Allow the configured daemon toolchain to be provisioned if prompted.
5. Select a device or emulator running API 24 or higher, select the `app` configuration and press Run.

The initial SDK, toolchain and dependency downloads require an internet connection. Android Studio configures the SDK location locally; keep `local.properties` out of version control.

## Command-line debug build

Run the wrapper from the repository root.

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew assembleDebug
```

The expected debug APK path is `app/build/outputs/apk/debug/app-debug.apk`. Confirm that the build succeeds before using the generated file. A build command shown here is an instruction, not a recorded build result.

For signing and checking a release APK, follow the [release guide](RELEASE.md).

## First launch

The app inserts seven sample records dated 25-31 October 2026 when the database is empty during ViewModel initialization. Open **Records** to see them. Dashboard totals only include entries matching the device's current local date, so a zero summary can be expected even when older sample rows exist.

## Common setup issues

| Symptom | Check |
| --- | --- |
| SDK location or platform missing | Open SDK Manager, install the configured platform and let Android Studio create the local SDK configuration. |
| Wrapper command not found | Run it from the root folder; PowerShell requires the `.\` prefix. |
| Dependency or toolchain download fails | Check the first Gradle error and network access, then retry sync after resolving the cause. |
| Database Inspector shows no process | Run the debug app, keep it open and select its process on a device using API 26 or higher. |
| Today's dashboard shows zero | Check the dates in Records; the sample rows use fixed October dates. |

Continue with the [manual verification guide](TESTING.md) to check app behavior.
