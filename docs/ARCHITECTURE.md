# Application architecture

This guide describes the application source imported in [commit cb7503b](https://github.com/ajithsundar-dev/HealthMate/commit/cb7503b1b82aa7a3af039e8b09eae8de9400276a).

## Responsibilities

Paths below are relative to `app/src/main/java/com/ajith/healthmate/`.

| Component | Responsibility |
| --- | --- |
| [MainActivity.kt](../app/src/main/java/com/ajith/healthmate/MainActivity.kt) | Creates the database, repository and ViewModel factory; hosts navigation and Snackbar feedback. |
| [ui/screens/](../app/src/main/java/com/ajith/healthmate/ui/screens) | Displays the dashboard, searchable records and add/edit form. |
| [HealthViewModel.kt](../app/src/main/java/com/ajith/healthmate/viewmodel/HealthViewModel.kt) | Exposes observable state and launches record operations. |
| [HealthRepository.kt](../app/src/main/java/com/ajith/healthmate/HealthRepository.kt) | Delegates data access to the DAO. |
| [HealthRecordDao.kt](../app/src/main/java/com/ajith/healthmate/HealthRecordDao.kt) | Defines Room queries, insertion, update and deletion. |
| [HealthDatabase.kt](../app/src/main/java/com/ajith/healthmate/HealthDatabase.kt) | Provides a synchronized singleton Room database using the application context. |

The data files declare the package `com.ajith.healthmate.data`, although their current physical location is the base source folder.

## State and operations

One activity-scoped `HealthViewModel` is shared by the navigation destinations. The screens collect its StateFlows with `collectAsStateWithLifecycle`. Search text selects the latest date query through `flatMapLatest`; database emissions then update the records list. The dashboard independently observes all records and sums rows whose date matches the current local date.

For a save, the form validates its fields and passes a `HealthRecord` to the ViewModel. A coroutine calls the repository and DAO. A successful operation sets `saveCompleted`; the navigation host observes that state, returns from the form and consumes it. Snackbar messages use a separate SharedFlow. The list and dashboard refresh from Room's observable queries.

The ViewModel guards saves while an earlier save is pending or awaiting consumption. The form also disables its controls during saving. Its date and text fields use `rememberSaveable`, keyed by the edited record ID, to retain input across activity recreation such as rotation.

## Current boundaries

- Storage is local to the app; there is no account system or server synchronization.
- Today's date is evaluated when the records flow emits. There is no dedicated midnight refresh timer.
- Read failures emit a message and an empty list or zero summary. These fallback values do not, by themselves, prove that the database is empty.
- Form validation enforces numeric limits; the database entity does not declare equivalent SQL CHECK constraints.
- Update and delete methods do not report affected-row counts.

See the [database guide](DATABASE.md) for query behavior and the [manual test guide](TESTING.md) for checks to run on a device.
