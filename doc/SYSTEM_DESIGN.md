# RapidRecall system design

## Overview

RapidRecall is a single-activity Android application built with Kotlin and Jetpack Compose. It uses
a small Model–View–ViewModel structure: composables render immutable snapshots, the
`RapidRecallViewModel` owns interaction state, domain classes implement game rules, and a repository
owns session attempts. No network, account, analytics, or database is used.

The accompanying class diagram is provided as non-lossy vector artwork:
[uml-class-diagram.svg](uml-class-diagram.svg).

## Class responsibilities

| Class | Main responsibility |
|---|---|
| `MainActivity` | Creates the Android window and obtains the lifecycle-retained ViewModel. |
| `RapidRecallApp` | Routes between the home, game, log, and summary composable screens. |
| `RapidRecallViewModel` | Owns navigation and round state; validates input; creates attempt records. |
| `AppScreen` | Enumerates the four top-level destinations. |
| `GamePhase` | Makes the legal round phases explicit: ready, memorize, input, and feedback. |
| `Attempt` | Immutable record containing length, input, target, result, and timestamp. |
| `SessionSummary` | Immutable aggregate that centralizes accuracy calculations. |
| `AttemptRepository` | Abstracts storage so gameplay does not depend on a persistence mechanism. |
| `SessionAttemptRepository` | Stores attempts in memory for the lifetime of the ViewModel/session. |
| `DigitSequenceGenerator` | Defines the contract for producing digit sequences. |
| `RandomDigitSequenceGenerator` | Generates 1–10 random digits while enforcing assignment bounds. |
| `SummaryCalculator` | Derives attempt totals and correct totals from log records. |

## Design rationale

- **Separation of concerns:** Android setup, UI rendering, application state, storage, generation, and
  aggregation are separate. UI code never edits repository collections or generates targets.
- **Information hiding:** ViewModel setters and the active target are private. The repository returns
  a copy of its records, preventing external mutation.
- **Dependency inversion:** `RapidRecallViewModel` depends on `AttemptRepository` and
  `DigitSequenceGenerator` interfaces. Deterministic fakes can therefore exercise complete rounds.
- **Explicit state machine:** `GamePhase` blocks actions in invalid phases, such as submitting during
  playback or changing sequence length after playback begins.
- **Lifecycle behavior:** The ViewModel survives configuration changes. Persistence intentionally
  ends with the app process, matching the assignment's session-persistence requirement.
- **Input integrity:** Only digits are accepted, input is capped at the chosen length, and submission
  remains disabled until the length is exact. Leading zeroes are retained.

## Tests

Local unit tests cover digit generation bounds/order, session repository ordering, summary accuracy,
and successful/incomplete round state transitions. Run them from `code` with:

```bash
./gradlew testDebugUnitTest
```

Build the required APK with:

```bash
./gradlew assembleDebug
```

The output is `code/app/build/outputs/apk/debug/app-debug.apk`.

## Known limitations

Attempt history is intentionally in memory and is cleared if the application process is killed. This
is a stated assignment constraint rather than an unresolved defect. The UI formats timestamps in the
device's current locale and time zone.
