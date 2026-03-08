# Android Tic-Tac-Toe

A clean Jetpack Compose sample project that implements a simple Tic-Tac-Toe game with state management, score tracking, and a light/dark theme toggle. The goal is to keep the project small enough for code reviews while demonstrating best practices (unidirectional data flow, previewable UI, and separation between UI state + game logic).

## Features

- Jetpack Compose UI with Material 3 styling
- Two-player hot-seat gameplay on a 3×3 grid
- Win/draw detection with automatic round reset
- Scoreboard with per-player wins and draws
- Simple theme toggle stored in memory (placeholder for DataStore)

## Structure

```
app/
  src/
    main/
      java/com/potterbot/tictactoe/  # Compose UI + game logic
      res/                          # Strings, colors, themes, icons
```

## Getting Started

1. Install the latest Android Studio (Ladybug+) with Android Gradle Plugin 8.2 or newer.
2. Clone this repository and open it in Android Studio.
3. Sync Gradle when prompted (Gradle wrapper is included).
4. Run the `app` configuration on an emulator or physical device running Android 8.0 (API 26) or higher.

```
./gradlew assembleDebug
./gradlew connectedAndroidTest
```

## Roadmap

- Add single-player mode with Minimax AI
- Persist scoreboard + theme via DataStore
- Add instrumentation tests for win/draw logic
- Improve accessibility (TalkBack hints, larger tap targets)

Contributions welcome!
