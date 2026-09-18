# Scripture Hunt

**Pokémon Go–style AR exploration with biblical overtones.**  
Walk the real world, find scripture spawns on the map, then uncover hidden words by tapping AR planes around you.

This repository is a **v1 Android app** (`0.2.0`): GPS map spawns + ARCore plane-tap reveals, with an in-memory collection sheet.

| | |
|---|---|
| **Package** | `com.elshadai.scripturehunt` |
| **Stack** | Kotlin · Jetpack Compose · ARCore · [SceneView](https://github.com/sceneview/sceneview-android) · Google Maps Compose · Play Services Location |
| **Min / target SDK** | 24 / 35 |
| **Version** | 0.2.0 |

---

## Game vision

Players explore physical places and uncover verses the way a trainer finds Pokémon — except the “spawns” are **scriptures**.

### How to play (v1)

1. Grant **Camera** (AR) and **Location** (map spawns) when prompted.
2. Open the **Map** tab — see yourself and nearby **hidden word** markers (stable per map grid cell).
3. Walk until a spawn is within ~**35 m** — it highlights and offers **Reveal in AR**.
4. Switch to **AR**, find a flat surface, **tap the plane** → that verse is claimed into your collection.
5. You can still free-roam in AR (tap planes for other verses) without a targeted spawn.
6. Open the gold **book** FAB to review discoveries for this session.

Verses are a small hardcoded **KJV** set (public domain). Themes include Love, Hope, Courage, Faith, and more.

### Roadmap

| Version | Idea |
|---------|------|
| **v0** | AR plane tap → scripture card + session collection. |
| **v1** *(this)* | Map / GPS scripture spawns, proximity → AR claim, Maps API key setup. |
| **v1+** | Persist discoveries, daily verses, shared locations, richer AR markers / ViewNodes. |

---

## Google Maps API key

Maps require a **Maps SDK for Android** key. Do **not** commit real keys.

1. In [Google Cloud Console](https://console.cloud.google.com/), create (or pick) a project.
2. Enable **Maps SDK for Android**.
3. Create an API key. For release builds, restrict it to package `com.elshadai.scripturehunt` and your signing cert.
4. Copy the example file and fill in the key:
   ```bash
   cp local.properties.example local.properties
   # edit MAPS_API_KEY=...
   ```
5. `local.properties` is gitignored. Rebuild the app.

If `MAPS_API_KEY` is missing or blank, the Map tab shows a clear setup screen instead of crashing. AR hunt still works.

The key is injected via Gradle (`manifestPlaceholders` + `BuildConfig.MAPS_API_KEY`) from `local.properties`.

---

## Device requirements

- A physical phone or tablet on Google’s [ARCore supported devices](https://developers.google.com/ar/devices) list.
- **ARCore** installed / up to date (Play Store usually prompts).
- Camera permission for AR; location permission for map spawns.
- Emulators generally **cannot** run full ARCore plane tracking — use a real device. Maps may work on emulator with a mocked location.

---

## Open in Android Studio

1. Clone this repo:
   ```bash
   git clone https://github.com/3lshadai-ux/Grok-bot.git
   cd Grok-bot
   ```
2. Copy `local.properties.example` → `local.properties` and set `MAPS_API_KEY` (and `sdk.dir` if needed).
3. Open the folder in **Android Studio** (Hedgehog or newer) via **File → Open**.
4. Let Gradle sync. Connect an ARCore device and press **Run**.

If sync fails on SceneView or Maps artifacts, confirm network access to Google Maven / Maven Central.

---

## Project layout

```
app/src/main/java/com/elshadai/scripturehunt/
  MainActivity.kt              # permissions, AR tap, location lifecycle
  ScriptureHuntApp.kt          # Map / AR bottom nav + hunt flow
  MapScreen.kt                 # Google Maps Compose + proximity UI
  ScriptureSpawn.kt            # grid-stable GPS spawn generation
  LocationTracker.kt           # Fused Location Provider wrapper
  Scripture.kt                 # KJV sample catalog
  ScriptureCards.kt            # reveal card + collection sheet
  DiscoveredScripturesStore.kt # in-memory discoveries / claimed spawns
  Theme.kt                     # parchment / gold accents
local.properties.example       # MAPS_API_KEY template
```

---

## License notes

- App scaffold: use freely for your project.
- Sample verse text: **KJV**, public domain.
- ARCore / SceneView / Google Maps: follow their respective licenses and Google Play requirements when you publish.
