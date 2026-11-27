# Release APK and signing verification

This guide covers producing a signed APK and verifying its signature. Build output, signature verification and functional testing answer different questions; keep their evidence identifiable.

## Produce the artifact

1. Open the project in Android Studio and allow Gradle sync to finish.
2. Select **Build > Generate Signed Bundle / APK**, choose **APK**, and select the app module.
3. Choose your own local signing key and the **release** variant. Complete the build and locate the generated APK.
4. Record the actual output filename and path. The inspected artifact for the record below was `app/release/app-release.apk`.

Keep the signing key, keystore and passwords private. The repository's ignore rules exclude local signing material and generated APK files.

## Verify the signature and file identity

Use `apksigner` from an installed Android SDK Build Tools directory. With that directory available on PATH, run:

```powershell
$releaseApk = ".\app\release\app-release.apk"
apksigner verify --verbose $releaseApk
$verificationExitCode = $LASTEXITCODE
$verificationExitCode
Get-Item -LiteralPath $releaseApk | Select-Object Name, Length
Get-FileHash -LiteralPath $releaseApk -Algorithm SHA256
```

Adjust the path to the actual output. Capture the filename, the `Verifies` output and a zero exit code. No signing password is needed for this verification command.

## Observed local verification

The following check was run against the local release artifact on **27 November 2026 at 04:56 UTC (10:26 Sri Lanka time)**, using Android SDK Build Tools 36.0.0 and OpenJDK 21 to execute `apksigner.jar`.

| Item | Observed value |
| --- | --- |
| Filename | `app-release.apk` |
| Size | 1,774,648 bytes |
| Verification result | `Verifies` |
| Process exit code | `0` |
| APK Signature Scheme v2 | `true` |
| v1, v3, v3.1 and v4 verification flags | `false` |
| SourceStamp verification flag | `false` |
| Number of signers | `1` |
| SHA-256 | `48ed66cf985a77ae0e339029b70c9761d0b9e2063a6a8f4969b9845dd79bec37` |

The overall command succeeded with v2 verification. The other false flags do not override that successful result.

This record applies to the local file inspected at that time. It does not establish that an externally hosted download contains identical bytes, that the APK was reproducibly built from a particular source commit, or that all app features pass their tests.

After rebuilding, verify the new file and record its new hash. Before submission, download the distributed copy and compare its SHA-256 with the intended artifact. Use the [manual test guide](TESTING.md) to record device behavior separately.
