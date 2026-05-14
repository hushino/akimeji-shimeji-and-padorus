# Akimeji (Shimeji/Padoru) – Android (Jetpack Compose)

Welcome to Akimeji – an open source Android project that brings desktop-style mascots to mobile. It supports two systems side-by-side:

- Shimeji (T1): classic shimejis
- Akimeji / Padoru (T2): festive padorus

Both systems share a similar architecture (network → decode → Room DB → overlay service) while keeping their code and data separate.

## Architecture overview

- UI: Jetpack Compose (Kotlin)
  - `MainActivity` renders:
    - Padorus (T2) list from GitHub JSON
    - Packs (T1) list from akimeji.com
    - Downloaded Shimejis (T1) from Room (live)
    - Downloaded Padorus (T2) from Room (live)
    - Start/Stop buttons for each overlay service
- Network: Retrofit (two clients)
  - Shimeji client base: `https://akimeji.com/`
  - Akimeji/Padoru client base: `https://hushino.github.io/`
  - `ApiService` provides:
    - `getPacks(@Url)` for T1 packs
    - `downloadImage(@Url)` for arbitrary JSON/binary
- Data / Persistence
  - T1 (Shimeji): `AppDatabase`
    - Tables: `mascots`, `shimeji`
    - Repos: `MascotRepository`, `ShimejiRepository`
    - DAO: `MascotsDao`, `ShimejiDao`
  - T2 (Akimeji/Padoru): `Akimejit2Database`
    - Tables: `pets`, frames table
    - Repo: `PetRepository` (suspend inserts on Dispatchers.IO)
    - DAO: `PetsDao`, `AkimejiDao`
- Overlay services
  - `ShimejiService` with `ShimejiView`
  - `AkimejiService` with `AkimejiView`
  - Each ticks and updates `WindowManager.LayoutParams` x/y/width/height
- Preferences
  - T1: `Helper` + `AppConstants`
  - T2: `HelperT2` + `AkimejiT2Constants`

## Naming and module split

We use clear prefixes to distinguish systems:

- T1 (Shimeji): classes and folders under `shimeji` or `dataAkimejiT1`
- T2 (Akimeji/Padoru): classes and folders under `akimeji` or `dataAkimejiT2`
- “Packs” → T1 content (akimeji.com); “Padorus” → T2 content (GitHub)

Examples:
- T1 DB: `AppDatabase`, `Mascots`, `ShimejiDao`, `ShimejiRepository`
- T2 DB: `Akimejit2Database`, `Pets`, `PetsDao`, `PetRepository`
- T1 service/view: `ShimejiService`, `ShimejiView`
- T2 service/view: `AkimejiService`, `AkimejiView`

## Networking

Retrofit instances in `di/ServiceLocator.kt`:

- `apiServiceShimeji` → base `https://akimeji.com/`
  - Packs JSON: `https://akimeji.com/packs/packv3.json`
- `apiServiceAkimeji` → base `https://hushino.github.io/`
  - Padorus list JSON: `${AppConstants.SERVER_BASE_PATH}akimejiT2/listThumbs2T2.json`
  - Padoru download: `${AppConstants.SERVER_BASE_PATH}akimejiT2/thumb/{id}`

Notes:
- `getPacks(@Url)` takes a full URL for flexibility
- `downloadImage(@Url)` used for both JSON text and binary assets

## Download and save flow

- T1 Packs (Shimeji):
  1. `PacksViewModel.loadPacks()` → loads packs via `apiServiceShimeji`
  2. On Download: fetch asset stream, decode frames, save via T1 DAOs

- T2 Padorus (Akimeji):
  1. `PacksViewModel.loadAkimejiThumbs()` → loads from GitHub; JSON may be a raw array or `{ "shimejis": [...] }`
  2. On Download: fetch `thumb/{id}`; if zip → unzip and decode frames; else decode single image
  3. Save via `PetRepository.addMascotToDatabase(...)` (Dispatchers.IO)

Live updates
- T1: `MascotRepository.getLiveDataOfMascotsInDb()`
- T2: `PetRepository.getLiveDataOfMascotsInDb()`

Observed in `MainActivity` to power both “Downloaded” sections.

## Overlay services and permissions

Declared in `AndroidManifest.xml`:
- `com.redbox.shimeji.live.shimejilife.system.shimeji.displayservice.ShimejiService`
- `com.redbox.shimeji.live.shimejilife.system.akimeji.displayservice.AkimejiService`

Both are foreground services and require overlay permission.

UI controls in `MainActivity`:
- Start → launches service (initializes, builds views, starts ticking)
- Stop → stops service
- If no active selections, a hint prompts to add one first

Overlay permission:
- Checked with `Settings.canDrawOverlays(context)` and requested via the system settings intent

## Views and movement

- `ShimejiView` / `AkimejiView`:
  - Pull frames from sprite services
  - Draw at ~60 FPS (16 ms handler)
  - Update window layout position every tick
  - Support drag; fling optional per system
- Facing direction: bitmaps flip horizontally when moving left so sprites face travel direction

## Compose UI

`MainActivity` is a single vertically scrollable screen containing:
- Downloaded Shimejis (image + name + Add/Remove)
- Downloaded Padorus (image + name + Add/Remove)
- Padorus (T2) list with Download buttons
- Packs (T1) list with banner + navigation to shimejis

`PacksViewModel` tracks loading and per-item states to disable buttons while downloading.

## Room & coroutines best practices

- All DB writes off the main thread (Dispatchers.IO)
- LiveData observed via `observeAsState()` in Compose
- Avoid synchronous Room calls from UI

## Build & Run

Prereqs
- Android Studio (Giraffe or newer)
- JDK 11

Build

```bash
./gradlew assembleDebug
```

Run
- Open in Android Studio → Run on device/emulator (Android 9+)
- Grant overlay permission when prompted

## Contributing

Contributions are welcome!

- Fork and create feature branches from `main`
- Follow naming and folder conventions (`shimeji`/T1 vs `akimeji`/T2)
- Use coroutines for IO; keep Room off the main thread
- Keep UI state in ViewModels
- Test services (start/stop/motion/drag) before submitting PRs

## License

MIT (or repository license). See `LICENSE` if present.

<div align="center">
    <img src="https://count.getloli.com/@akimeji-shimeji-and-padorus?name=hushino01&theme=booru-qualityhentais&darkmode=auto"/>
</div>
