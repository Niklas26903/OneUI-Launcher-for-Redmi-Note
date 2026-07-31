# 🚀 Release Update v1.1.0 - One Galaxy UI Suite

Dieses Release bringt eine kompromisslose Anpassbarkeit und volle Layout-Flexibilität für den **One Galaxy UI Launcher** sowie die komplett eigenständige **Routine+ Pro** App zur Automatisierung und Erstellung von Touch-Makros, Fingerabdruck-Triggern und Tasten-Kurzbefehlen.

---

## 📦 Download Assets (Fertig gebaute APK-Dateien)
Die fertig gebauten, installierbaren APKs befinden sich im Repository unter dem Ordner `/output/`:

1. **One Galaxy UI Launcher**
   - **Dateiname:** `OneGalaxyUI-Launcher.apk`
   - **Pfad im Repo:** [./output/OneGalaxyUI-Launcher.apk](./output/OneGalaxyUI-Launcher.apk)
   - **Beschreibung:** Custom Android Launcher mit unbegrenzter Gitter-Flexibilität, Gesten und anpassbaren Ordnern/Docks.

2. **Routine+ Pro Standalone App**
   - **Dateiname:** `RoutinePlus-Standalone.apk`
   - **Pfad im Repo:** [./output/RoutinePlus-Standalone.apk](./output/RoutinePlus-Standalone.apk)
   - **Beschreibung:** Standalone Automatisierungs-App mit Touch-Makros, Fingerabdruck-Simulations-Triggern und physischen Tasten-Kurzbefehlen.

---

## 🛠️ Zusammenfassung der Neuerungen

### 1. 🎨 Kompromisslose Layout-Flexibilität
* **Layout-Modus-Auswahl:** Sowohl der Startbildschirm als auch der App-Drawer können nun wahlweise auf **Horizontal** (seitenbasiertes Gitter mit sanftem `PagerSnapHelper`-Wischen) oder **Vertikal** (klassisches, flüssiges Endlos-Scrollen) umgeschaltet werden.
* **Freie Gitter-Eingabe (Grids):** Keine starren Vorgaben mehr! Über ein freies Textfeld kann jede Zeilen- und Spaltenkombination (z. B. `4x7`, `6x6`, `3x5` etc.) eingegeben werden. Die Layout-Engine parst und wendet diese Änderungen dynamisch in Echtzeit an.

### 2. 📂 Detaillierte Ordner-Anpassung
* **Geschlossener Zustand (Symbol):**
  - Stufenlose Skalierung der Ordnersymbol-Größe in dp.
  - Dynamisch gerenderte **Mini-Gitter-Vorschau** (2x2, 3x3), die in Echtzeit die tatsächlichen Symbole der enthaltenen Apps verkleinert darstellt.
  - Anpassbare Hintergrundfarbe mit stufenlosem Opazitäts-/Transparenzregler (0% bis 100%).
  - Modifizierbare Symbolform (Rund, Quadratisch, Squircle oder Eigene Form mit benutzerdefiniertem Radius).
  - Rand-Konfiguration (Rahmenfarbe und Stärke in dp).
* **Geöffneter Zustand (Popup):**
  - Frei wählbare Animationsstile beim Öffnen (Scale, Fade, Slide).
  - Konfigurierbare Fenstergröße (Breite und Höhe in % der Bildschirmgröße).
  - Einstellbare Hintergrund-Abdunklung (Dimming in %).
  - Separates, anpassbares Gitter innerhalb des Ordners (z. B. `3x3` oder `4x4`).
  - Anpassung der Schriftart (Default, Sans-Serif, Serif, Monospace) und der Textfarbe des Ordnernamens.

### 3. 📥 Dock-Anpassung
* **Flexible Symbolanzahl:** Frei wählbare Anzahl von 1 bis 9 Symbolen im persistenten Dock.
* **Hintergrundleiste mit Glaseffekt:** Programmatische Generierung einer edlen, abgerundeten Glas-Hintergrundleiste mit konfigurierbarer Hintergrundfarbe, Transparenz und Eckenradius.
* **Automatische Ausblendung:** Schaltet sich automatisch unsichtbar, sobald der App-Drawer geöffnet wird, um den vollen Platz zu nutzen.

### 4. 🖼️ Symbole- & Design-Einstellungs-Unterseite
* **Symbolgröße:** Stufenloser Skalierungs-Regler von 50% bis 150%.
* **Beschriftung unter Icons:** Optionales Ein-/Ausschalten, Einzeiligkeit vs. Zweizeiligkeit mit automatischer Ellipse, anpassbare Schriftgröße und Schriftfarbe.
* **Icon-Pack & Tinting-Support:** Ermöglicht die Angabe eines Hex-Farbcodes, um alle App-Symbole mit einem eleganten Farbton zu überlagern (Farb-Tinting).

### 5. 🧩 Gesteuertes Widgets- & Ausrichtungs-System
* **Zwei-Zonen-Gesten-Menü:** Die Geste des Herauszoomens (Pinch-to-Zoom) oder das lange Halten (Long-Press) auf dem Startbildschirm öffnet das elegante, geteilte Auswahlmenü (Zwei-Zonen-Prinzip):
  - **Links (Widgets):** Öffnet das Widget-Auswahlmenü.
  - **Rechts (Launcher-Einstellungen):** Startet direkt die Launcher-Einstellungen.
* **Interaktive, draggable Widgets:** Platziere Uhrzeit-, Wetter-, Akkustand- oder Kalender-Widgets direkt auf dem Startbildschirm.
* **Drag-and-Drop mit Kollisionsschutz:** Ziehe Widgets an jede beliebige Stelle:
  - **Snap-to-Grid (Automatisches Einrasten):** Rasted die Position automatisch an den berechneten Zellengrößen des Gitters ein.
  - **Freies Überlappen:** Ein-/Ausschaltbare Option. Verhindert bei Deaktivierung Kollisionen und stellt sicher, dass Widgets nicht aufeinander platziert werden können.

### 6. 🤖 Routine+ Standalone App (RoutinePlus-Standalone.apk)
Die eigenständige, hocheffiziente Automatisierungs-App emuliert Samsungs Modi und Good Lock Routinen:
* **Touch-Makros:** Definiere eine unbegrenzte Folge von Touch-Koordinaten (X, Y) und Wartezeiten und spiele sie als Makro ab.
* **Fingerabdruck-Trigger:** Führe vordefinierte System-Routinen aus (z. B. Stummschaltung, Starten von Spotify, Taschenlampe oder Energiesparmodus), sobald der Fingerabdruck-Sensor simuliert wird.
* **Tasten-Kurzbefehle:** Verknüpfe physische Hardware-Tasten (Lautstärke+/Lautstärke-) mit Aktionen (Kamera öffnen, Screenshot, Dark Mode, Recents) und führe sie über physische oder simulierte Drücke aus.

---

## 🏗️ Build- und Kompilierungs-Details
Beide Apps wurden erfolgreich im Verzeichnis gebaut:
- **Gradle Version:** Gradle 8.8
- **Kompiliert mit:** compileSdk 35 / targetSdk 35
- **Mindestvoraussetzung:** minSdk 26 (Android 8.0+)
- **Build-Befehl:** `./gradlew assembleDebug`
