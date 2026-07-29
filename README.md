# One Galaxy UI Launcher for Redmi Note 11 (FHD+)

Ein eleganter, eigenständiger und ungerooteter Custom Launcher im **Samsung One Galaxy UI Design**, optimiert für das **Redmi Note 11 (FHD+, 1080x2400)**. Diese App ersetzt die Standard-Startseite und läuft nahtlos als Standard-Home-App (`android.intent.category.HOME`).

---

## 🚀 Hauptfunktionen

### 1. 🎨 One Galaxy UI Design & Customization (Anpassung)
- **Grid-Größen:** Unterstützung für flexible Startbildschirm- und App-Bildschirm-Gitter (4x5, 4x6, 5x5, 5x6) im gewohnten One Galaxy UI Stil.
- **App Drawer:** Vertikaler oder horizontaler App-Drawer mit einer integrierten, schnellen Suchleiste für installierte Apps.
- **System-Gesten:**
  - *Nach unten streichen* für das Benachrichtigungsfeld.
  - *Nach oben streichen* zum Öffnen des App-Drawers.
  - *Doppeltippen* zum Sperren oder für haptisches Feedback.
- **FHD+ Optimierung:** Pixelscharfe Skalierung, perfekt abgestimmt auf das Redmi Note 11 (1080x2400).

### 2. ⚡ Integrierter Samsung Routines Nachbau (Wenn-Dann Automatisierung)
Erstelle mächtige Automatisierungen basierend auf Bedingungen direkt im Launcher:
- **Bedingungen (WENN):**
  - Bestimmte Uhrzeit (z. B. `14:30`)
  - Verbindung zu einem bestimmten WLAN-Netzwerk (z. B. `HomeWiFi`)
  - Akkustand erreicht einen Schwellenwert (z. B. `20%`)
- **Aktionen (DANN):**
  - Systemlautstärke ändern (z. B. auf lautlos oder bestimmte Prozent)
  - Bildschirmhelligkeit anpassen
  - Dark Mode umschalten
  - Eine bestimmte App automatisch starten (z. B. Spotify oder Maps)

---

## 🛠️ Installationsanleitung & Selberbauen

### Voraussetzungen
- Android 8.0 (API 26) oder höher
- JDK 17 oder 21
- Android SDK 34 oder 35

### Projekt selber bauen (Gradle)
Klone das Repository und baue die `.apk` mit dem Gradle Wrapper:

```bash
# Repository klonen
git clone https://github.com/user/OneUI-Launcher-for-Redmi-Note.git
cd OneUI-Launcher-for-Redmi-Note

# Debug APK kompilieren
./gradlew assembleDebug
```

Die fertige APK-Datei befindet sich nach dem Build unter:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Ordnerstruktur
```
OneUI-Launcher/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/oneui/launcher/       # Kotlin-Quellcode (MainActivity, Routines, Adapters)
│   │   │   ├── res/                           # One Galaxy UI Layouts, XML-Ressourcen und Styles
│   │   │   └── AndroidManifest.xml            # Launcher-Deklarationen & Permissions
│   └── build.gradle.kts                       # App-spezifische Gradle-Konfiguration
├── gradle/                                    # Version-Catalogs (libs.versions.toml) & Wrapper
├── build.gradle.kts                           # Root Gradle-Konfiguration
└── README.md                                  # Diese Dokumentation
```

---

## 📄 Lizenz
Dieses Projekt ist unter der **MIT-Lizenz** lizenziert. Weitere Details findest du in der `LICENSE`-Datei.
