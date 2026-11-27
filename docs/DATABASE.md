# Database schema and queries

The app uses Room over SQLite. The database is named `health_database`, and its single entity maps to `health_records`. The database version is 1, with `exportSchema = false`.

Sources: [HealthRecord.kt](../app/src/main/java/com/ajith/healthmate/HealthRecord.kt), [HealthRecordDao.kt](../app/src/main/java/com/ajith/healthmate/HealthRecordDao.kt) and [HealthDatabase.kt](../app/src/main/java/com/ajith/healthmate/HealthDatabase.kt).

## Stored fields

| Column | Kotlin type | SQLite storage type | Meaning |
| --- | --- | --- | --- |
| `id` | `Int` | INTEGER | Auto-generated primary key; zero is the default for a new entity. |
| `date` | `String` | TEXT | Calendar date stored as `yyyy-MM-dd`. |
| `steps` | `Int` | INTEGER | Manually entered steps. |
| `calories` | `Int` | INTEGER | Manually entered calories burned, in kcal. |
| `water` | `Int` | INTEGER | Manually entered water intake, in ml. |

All entity fields are non-null. Dates are not unique: two rows may represent separate entries on the same day. The dashboard sums all rows for today's date. For example, two entries of 3,000 and 2,000 steps contribute 5,000 steps to that day's total.

The form permits steps from 0 to 100,000 and calories/water from 0 to 20,000. These are application validation rules rather than database CHECK constraints. The entity also does not validate the date format, so writes outside the form would need their own validation.

## Read queries

The DAO's full-list query orders records by descending date and then descending ID:

```sql
SELECT * FROM health_records ORDER BY date DESC, id DESC
```

For fixed-width dates in the form's format, text ordering follows calendar ordering. ID provides a consistent order for entries with the same date.

The search query is:

```sql
SELECT * FROM health_records
WHERE date LIKE '%' || :query || '%'
ORDER BY date DESC, id DESC
```

The ViewModel trims the input before querying. An empty query matches all dates; `2026-10` matches October 2026, and `2026-10-28` matches that day. Input is passed as a bound parameter. SQL LIKE characters `%` and `_` retain their wildcard meanings because the app does not escape them.

Other methods fetch one row by ID, observe rows for an exact date, and count records. Room annotations provide insert, bulk insert, update and delete operations.

## Initial records and schema changes

[DummyData.kt](../app/src/main/java/com/ajith/healthmate/DummyData.kt) inserts seven sample records dated 25-31 October 2026 when the table is empty during ViewModel initialization. These dates do not automatically become today's date. If every row is deleted, a later ViewModel creation can seed the table again.

There is no explicit migration registered for a future schema version. A schema change should therefore include a deliberate migration strategy and migration testing; changing the version alone does not provide one.

## Inspecting a debug database

Run the debug app on a device or emulator using API 26 or higher. In Android Studio, open **View > Tool Windows > App Inspection**, select the running app process, and open **Database Inspector**. Expand `health_database` and open `health_records`.

For evidence, capture the readable table pane with the database name and all five column headings visible. Sample rows demonstrate stored data; a screenshot alone does not demonstrate persistence across a restart.
