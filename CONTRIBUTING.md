# Contributing

HealthMate is a Kotlin Android coursework project. Keep changes focused and describe the behavior they add or correct.

## Working on a change

1. Start from the current `main` branch and create a branch for the change.
2. Follow the [setup guide](docs/SETUP.md) before building or running the app.
3. Keep UI changes in the Compose screens, state and actions in the ViewModel, and data access in the repository/DAO layers.
4. Update relevant documentation when setup instructions or visible behavior change.
5. Open a pull request describing the change and its validation.

Data files currently declare `com.ajith.healthmate.data` while residing directly in the base Kotlin source folder. Use the existing package declarations when adding imports.

## Validation

For a code change, run the relevant build and checks from the repository root. Windows PowerShell examples:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

Record which commands actually ran and their results. Unit tests and lint do not replace device checks for navigation, form behavior or Room integration. Use the relevant cases in [TESTING.md](docs/TESTING.md), recording the device/API level and observed results.

For documentation changes, check relative links, filenames, commands and consistency with the current source. A documentation-only change does not need an Android build unless it changes build instructions that require validation.

## Commit messages

Use one clear message for each coherent change. Describe completed work, such as `Fix date search after clearing input` or `Clarify database setup instructions`. These examples are naming guidance, not a record of implemented changes.

Use the actual commit date. Do not describe a proposed feature or an unexecuted test as completed work.

## Reporting a problem

Include:

- Device/emulator model, Android/API version and app build.
- Starting data and exact steps to reproduce the problem.
- Expected behavior and the actual result.
- Relevant logs or readable screenshots.
- Whether the problem still occurs after repeating the steps.

Keep signing keys, passwords and machine-specific configuration out of commits. Crop personal information from public screenshots.
