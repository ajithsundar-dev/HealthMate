# Manual verification guide

These procedures describe expected behavior for the current app. Actual device results and screenshots have not been recorded in this guide. Complete the checks and record observations before marking a case as passed.

## Environment and test data

Use a test installation or emulator with no records for today's date. Keep unrelated personal records out of the evidence. Record the device model, Android/API version, app build, local date/time zone and test date.

The app runs on API 24 or higher; Database Inspector requires API 26 or higher. The initial sample records are dated 25-31 October 2026. If your test date overlaps those dates, account for those rows before comparing totals.

Let **D** be today's date on the test device. Prepare:

| Entry | Date | Steps | Calories (kcal) | Water (ml) |
| --- | --- | --- | --- | --- |
| A | D | 3000 | 150 | 800 |
| B | D | 2000 | 100 | 700 |

Run T01-T06 in order. After T02, the expected totals are 5,000 steps, 250 kcal and 1,500 ml. T03 changes only A's steps to 3,500; after deleting B in T06, the totals should be 3,500 steps, 150 kcal and 800 ml.

## Cases to execute

| ID | Procedure | Expected observation | Actual result |
| --- | --- | --- | --- |
| T01 | Launch the app when D has no entries; open Dashboard and Records. | Dashboard shows zero totals and its empty-day message. The seven seeded October records remain available in Records on a fresh database. | Not recorded |
| T02 | Add A using the date picker and Save. Add B for the same date. Return to Dashboard. | Both rows appear in Records; totals are 5000 / 250 / 1500. Each successful save gives feedback. | Not recorded |
| T03 | Edit A. Change steps to 3500, keeping its other fields unchanged; tap Update. | A retains its identity, displays the edited values and contributes to totals of 5500 / 250 / 1500. | Not recorded |
| T04 | Search Records for `2026-10-28`, then `2026-10`, then a date absent from the database. Clear search afterwards. | The day query shows the matching sample, the month query shows October rows, an absent date shows no matches, and clearing restores all rows. | Not recorded |
| T05 | Request deletion of B and cancel the confirmation dialog. | B remains and today's totals stay at 5500 / 250 / 1500. | Not recorded |
| T06 | Request deletion of B again and confirm. | B disappears; totals become 3500 / 150 / 800. | Not recorded |
| T07 | Open Add, leave Steps empty, fill the other fields and tap Save. Repeat with Calories and Water empty separately. | The relevant required-field error appears and no new row is stored. Cancel the invalid form. | Not recorded |
| T08 | On separate forms try Steps 100001, Calories 20001 and Water 20001. Check allowed boundary values and try entering a decimal or minus sign. | Values above the limits cannot be saved; zero and the exact upper limits are accepted. Fields reject non-digit input. Remove any valid boundary-test rows before comparing totals again. | Not recorded |
| T09 | Enter a distinctive unsaved date and three values; rotate the device. Then save once, including a check with rapid repeated taps. | Rotation retains the input. Saving stores one intended row; repeated taps do not create duplicate saves. Identify and remove this test row afterwards. | Not recorded |
| T10 | Close and reopen the app without clearing its storage or uninstalling it. Search for A and inspect Dashboard. | The edited A remains, deleted B remains absent and the corresponding totals persist. | Not recorded |

If other rows exist for D, calculate totals from those rows as well. Do not label a discrepancy as a defect until the starting data and search filter have been checked.

## Capturing evidence

Capture the empty dashboard before adding A and B. Capture the populated add form before saving; then capture the dashboard totals and records list after T02. For editing, capture the pre-update form and the saved result. Also capture a matching search, an empty search result, required-field and upper-limit errors, the deletion dialog and the list after confirmation.

Record the visible result, expected result, pass/fail decision, screenshot filename and any defect or retest reference. Crop for readability while retaining the app controls needed to interpret the result. Use your actual device date and values in the report.

## Interpreting the checks

A signature check establishes APK signing validity; it does not execute these cases. A build result likewise does not establish correct CRUD behavior. Keep those forms of evidence separate.

The empty-database behavior also needs care: deleting all records and later recreating the ViewModel can restore the sample data. This follows the current seeding rule. Future checks could examine midnight refresh, database failures, accessibility and schema migrations; those checks are not recorded as completed here.
