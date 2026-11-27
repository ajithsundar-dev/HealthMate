# HealthMate

An offline Android health tracker built with Kotlin, Jetpack Compose, and Room. Users manually record their daily steps, calories burned, and water intake.

## Features

- Add health records using a calendar date picker.
- View records with the newest date first.
- Edit and delete existing records, with confirmation before deletion.
- Search using a full date or part of a date, such as `2026-10`.
- View today's total steps, calories, and water intake on the dashboard.
- Validate required fields, whole-number input, and value limits.
- Keep form values when the device rotates.
- Store records locally across app restarts.
- Support light and dark themes.

## Technology

| Component | Project configuration |
| --- | --- |
| Language and UI | Kotlin; Jetpack Compose; Material 3 |
| Architecture | ViewModel, repository, and DAO layers |
| Database | Room 2.8.5 over SQLite |
| State | StateFlow and lifecycle-aware collection |
| Navigation | Navigation Compose 2.9.5 |
| Android SDK | Compile/target API 37; minimum API 24 |
| Build tools | Android Gradle Plugin 9.4.1; Gradle 9.6.0 |
| Java daemon toolchain | Java 25, configured in `gradle/gradle-daemon-jvm.properties` |
| Kotlin Compose plugin | 2.2.10 |
| Kotlin Symbol Processing | 2.3.14 |

Exact dependency declarations are in `app/build.gradle.kts` and `gradle/libs.versions.toml`.

## Source files

All Kotlin paths below are relative to `app/src/main/java/com/ajith/healthmate/`.

| File | Responsibility |
| --- | --- |
| [MainActivity.kt](app/src/main/java/com/ajith/healthmate/MainActivity.kt) | App entry point, navigation, bottom bar, and feedback messages |
| [HealthRecord.kt](app/src/main/java/com/ajith/healthmate/HealthRecord.kt) | Room entity for the `health_records` table |
| [HealthRecordDao.kt](app/src/main/java/com/ajith/healthmate/HealthRecordDao.kt) | Database queries and CRUD operations |
| [HealthDatabase.kt](app/src/main/java/com/ajith/healthmate/HealthDatabase.kt) | Shared Room database instance |
| [HealthRepository.kt](app/src/main/java/com/ajith/healthmate/HealthRepository.kt) | Data access for the ViewModel |
| [DummyData.kt](app/src/main/java/com/ajith/healthmate/DummyData.kt) | Sample records inserted when the table is empty |
| [viewmodel/HealthViewModel.kt](app/src/main/java/com/ajith/healthmate/viewmodel/HealthViewModel.kt) | Search state, today's totals, save state, and record actions |
| [ui/screens/](app/src/main/java/com/ajith/healthmate/ui/screens) | Dashboard, records list, and add/edit form |
| [ui/theme/](app/src/main/java/com/ajith/healthmate/ui/theme) | App colors, typography, and themes |

The data classes declare the package `com.ajith.healthmate.data` and currently reside directly in the main Kotlin source folder.

## Database

Database name: `health_database`; table: `health_records`.

| Column | Kotlin type | Meaning |
| --- | --- | --- |
| `id` | `Int` | Auto-generated primary key |
| `date` | `String` | Date in `yyyy-MM-dd` format |
| `steps` | `Int` | Steps walked |
| `calories` | `Int` | Calories burned in kcal |
| `water` | `Int` | Water intake in millilitres |

Multiple entries can share a date. The dashboard adds together entries for today. Seven sample records dated 25-31 October 2026 are inserted when the table is empty; those records appear in the Records screen. Today's dashboard shows totals only for today's date.

## Open and run

1. Clone or download this repository.
2. Open its root folder in Android Studio.
3. Install Android SDK Platform 37 using the SDK Manager and let Gradle sync complete.
4. Allow Android Studio/Gradle to provision the configured Java daemon toolchain if needed.
5. Select an Android device or emulator running API 24 or higher and run the `app` module.

Android Studio creates the local SDK configuration for the current computer. `local.properties` is excluded from version control.

To build a debug APK from the project root:

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew assembleDebug
```

The generated APK is normally located at `app/build/outputs/apk/debug/app-debug.apk`.

For a signed release APK, use Android Studio's **Build > Generate Signed Bundle / APK** wizard, select **APK**, choose your own signing key, and select the **release** build variant. Signing keys and passwords are managed locally.

## Manual verification

| Check | Expected result |
| --- | --- |
| Add a valid entry for today | Entry appears in Records and contributes to today's totals |
| Edit an entry | Updated values appear in the list and relevant dashboard totals |
| Search by day or month | Only matching dates appear |
| Cancel deletion | Record remains |
| Confirm deletion | Record is removed and totals update |
| Save empty numeric fields | Required-field errors appear |
| Enter steps above 100000 | Validation prevents saving |
| Enter calories or water above 20000 | Validation prevents saving |
| Rotate while filling the form | Entered values remain |
| Close and reopen the app | Saved records remain available |

This checklist describes expected behavior; it does not record completed test results.

## Developer guides

- [Architecture and state flow](docs/ARCHITECTURE.md): component responsibilities, save behavior and current boundaries.
- [Database schema and queries](docs/DATABASE.md): stored fields, search, sample data and Database Inspector.
- [Manual verification procedures](docs/TESTING.md): test data, expected results and space to record device observations.
- [Release APK verification](docs/RELEASE.md): signing steps and a dated check of the local release artifact.

## Author

Ajithsundar Manoharan
