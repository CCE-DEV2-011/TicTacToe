# TicTacToe

A modular, multi-layered Android implementation of the classic Tic-Tac-Toe game. The project is organized using a clean architecture approach, with
separate modules for domain, data, features, and UI.

## Features

- Play Tic-Tac-Toe against another player
- Modular codebase with clear separation of concerns
- Modern Android development with Kotlin and Compose
- ViewModel-driven UI
- Snackbar feedback for invalid moves and errors
- Unit tested core logic

## Project Structure

- `app/` – Main Android application module
- `domain/` – Business logic and core models
- `data/` – Data sources and repositories
- `features/` – Feature-specific logic (e.g., game board)
- `ui/` – UI components and design system

## Requirements

- Android Studio (Giraffe or newer recommended)
- JDK 17+
- Gradle (wrapper included)

## Getting Started

1. **Clone the repository:**
   ```sh
   git clone https://github.com/CCE-DEV2-011/TicTacToe.git
   cd TicTacToe
   ```

2. **Open in Android Studio:**
    - Open the project root directory in Android Studio.

3. **Build the project:**
    - Use the Gradle wrapper:
      ```sh
      ./gradlew assembleDebug
      ```
    - Or click "Build > Make Project" in Android Studio.

4. **Run the app:**
    - Connect an Android device or start an emulator.
    - Click "Run > Run 'app'" in Android Studio, or use:
      ```sh
      ./gradlew installDebug
      ```

## Code Style & Linting

The project uses [detekt](https://detekt.dev/) for static code analysis and [ktlint](https://github.com/pinterest/ktlint) for Kotlin code formatting
and linting.

To run detekt:

```sh
./gradlew detekt
```

To run ktlint (if configured as a Gradle task):

```sh
./gradlew ktlintCheck
```

Both tools help ensure code quality and consistency throughout the project.

## Dependency Injection

Koin is used for dependency injection. Ensure the Android context is set up in your `Application` class:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
```
