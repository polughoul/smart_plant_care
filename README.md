# Smart Plant Care

Smart Plant Care is an Android app for managing your personal plant collection, watering schedule, plant information.

## Main Features

- Search plants from remote API and add them to your garden
- Create manual/custom plants
- Edit plant name, interval, image, and local details
- Mark watering for one plant or multiple plants at once
- Track watering history per plant
- Add personal notes to each plant
- Daily reminder notifications for due watering

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Navigation:** `navigation-compose`
- **Local DB:** Room + DAO + Repository
- **State:** ViewModel + StateFlow
- **Network:** Retrofit + Gson
- **Images:** Coil
- **Preferences:** DataStore
- **Background reminders:** AlarmManager + BroadcastReceiver + Notifications

## Project Structure

`app/src/main/java/com/example/smart_plant_care`

- `MainActivity.kt`  
  App entry point, theme setup, dependency wiring.

- `data/`
  - `local/`
    - `entity/` -> Room entities (`MyPlantEntity`, `WateringEventEntity`)
    - `dao/` -> database access contracts (`PlantDao`, `WateringEventDao`)
    - `db/` -> Room database (`AppDatabase`)
  - `remote/`
    - `api/` -> Retrofit client + API interface
    - `dto/` -> API response models/adapters
  - `repository/`
    - `PlantRepository` -> local business operations
    - `PlantRemoteRepository` -> remote search/details operations
  - `preferences/`
    - DataStore models and persistence (`UserPreferences*`)

- `notifications/`
  - `PlantReminderScheduler` -> schedules/cancels daily checks and test triggers
  - `PlantReminderReceiver` -> handles daily check and `Mark as watered` action

- `ui/`
  - `main/MainScreen.kt` -> root scaffold + nav host + screen wiring
  - `navigation/Screen.kt` -> routes
  - `screens/` -> all Compose screens
  - `viewmodels/` -> per-feature ViewModels
  - `theme/` -> colors/typography/theme
  - `util/` -> UI helpers (e.g., day change ticker)

- `util/`
  - shared domain helper logic (watering date calculations)

`app/src/main/res`
- `values/strings.xml` -> all user-visible strings
- `drawable/` -> icons/assets

## Core Flows

### 1) Search and Add Plant
1. User opens **Search** screen.
2. Query goes through `SearchViewModel` -> `PlantRemoteRepository` -> Retrofit API.
3. User opens details and adds plant to garden.
4. App stores plant in Room and prevents duplicates for same remote plant.

### 2) Garden Management
- My Garden list supports:
  - swipe actions (water/edit/delete),
  - long-press multi-selection,
  - bulk actions (water selected, delete selected),
  - sorting filter.

### 3) Watering and History
- `Mark as watered` updates `nextWateringDate` by saved interval.
- Each watering event is stored in `WateringEventEntity`.
- History is shown in details and dedicated history screen.

### 4) Daily Notifications
- `PlantReminderScheduler` schedules a daily alarm check (9:00 local time).
- `PlantReminderReceiver` loads due plants from DB:
  - **1 due plant** -> single notification with **Mark as watered** action.
  - **2+ due plants** -> summary notification.

## Permissions

- `INTERNET` for API calls.
- `POST_NOTIFICATIONS` (Android 13+) for reminder notifications.

