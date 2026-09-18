# Scripture Hunt

**Pokémon Go–style AR exploration with biblical overtones.**  
Walk the real world, scan your surroundings, and discover hidden scriptures waiting on the surfaces around you.

This repository is a **v0 Android starter**: tap a detected AR plane to reveal a sample King James Version (KJV) verse card, then browse everything you have found in an in-memory collection sheet.

| | |
|---|---|
| **Package** | `com.elshadai.scripturehunt` |
| **Stack** | Kotlin · Jetpack Compose · ARCore · [SceneView](https://github.com/sceneview/sceneview-android) (`arsceneview`) · AndroidX |
| **Min / target SDK** | 24 / 35 |

---

## Game vision

Players explore physical places and uncover verses the way a trainer finds Pokémon — except the “spawns” are **scriptures**. v0 proves the core loop:

1. Point the camera at a flat surface (floor, table, ground).
2. ARCore draws a plane when tracking is solid.
3. **Tap the plane** → a parchment-style **scripture card** appears.
4. Open **My collection** (book FAB) to review discoveries for this session.

Verses in v0 are a small hardcoded KJV set (public domain). Themes include Love, Hope, Courage, and more.

### Roadmap (not in this commit)

| Version | Idea |
|---------|------|
| **v1** | Map / GPS “scripture spawns” near the player (Pokémon Go map layer). Location permissions are already declared as a stub. |
| **v1+** | Persist discoveries, daily verses, shared locations, richer AR markers / ViewNodes over anchors. |

---

## Device requirements

- A physical phone or tablet on Google’s [ARCore supported devices](https://developers.google.com/ar/devices) list.
- **ARCore** installed / up to date (Play Store usually prompts).
- Camera permission granted.
- Emulators generally **cannot** run full ARCore plane tracking — use a real device.

---

## Open in Android Studio

1. Clone this repo:
   ```bash
   git clone https://github.com/3lshadai-ux/Grok-bot.git
   cd Grok-bot
   ```
2. Open the folder in **Android Studio** (Hedgehog or newer recommended) via **File → Open**.
3. Let Gradle sync. Android Studio will download the Gradle distribution from `gradle/wrapper/gradle-wrapper.properties` if the wrapper JAR is missing — use **File → New → Import** or let the IDE offer to create/fix the wrapper.
4. Connect an ARCore device, select it in the device dropdown, and press **Run**.

If sync fails on the SceneView artifact, confirm you have network access to Maven Central and that `io.github.sceneview:arsceneview:2.3.0` resolves.

---

## How to play (v0)

1. Grant **Camera** when prompted.
2. Move the phone slowly until a grid plane appears.
3. Tap the plane → read the revealed verse.
4. Tap the gold **book** button → see your session collection.

---

## Project layout

```
app/src/main/java/com/elshadai/scripturehunt/
  MainActivity.kt                 # AR session + scripture UI
  Scripture.kt                    # KJV sample catalog
  DiscoveredScripturesStore.kt    # in-memory discoveries
app/src/main/AndroidManifest.xml  # CAMERA + ARCore required + location stubs
```

---

## License notes

- App scaffold: use freely for your project.
- Sample verse text: **KJV**, public domain.
- ARCore / SceneView: follow their respective licenses and Google Play AR requirements when you publish.
