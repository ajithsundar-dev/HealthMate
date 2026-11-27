# HealthMate

An offline Android app for tracking steps, calories burned and water intake through manual daily entries.

**Kotlin · Jetpack Compose · Material 3 · Room / SQLite**

## Features

- Add, edit and delete health records, with confirmation before deletion.
- Search by date or month and view today's combined totals.
- Validate numeric input and retain form values during rotation.
- Store entries locally and support light and dark themes.

## Quick start

1. Clone this repository and open its root folder in Android Studio.
2. Install **Android SDK Platform 37** and let Gradle sync complete.
3. Run the `app` configuration on a device or emulator using **API 24 or higher**.

To build a debug APK from the project root in Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`.

See the [setup guide](docs/SETUP.md) for toolchain requirements, other platforms and troubleshooting.

## Documentation

| Guide | Contents |
| --- | --- |
| [Architecture](docs/ARCHITECTURE.md) | Components, state flow and save behavior |
| [Database](docs/DATABASE.md) | Room schema, queries and sample data |
| [Testing](docs/TESTING.md) | Manual procedures and expected results |
| [Release](docs/RELEASE.md) | APK signing and recorded signature verification |
| [Contributing](CONTRIBUTING.md) | Development workflow and validation |

## Sample data

Seven sample entries dated **25-31 October 2026** are inserted when the database is empty. Find them under **Records**; today's dashboard only counts entries matching the device's current date.

Created by **Ajithsundar Manoharan**.
