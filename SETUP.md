# SafeChain Android App — Setup Guide

## Prerequisites
- **Android Studio Hedgehog** (2023.1.1) or newer
- **JDK 17**
- Android SDK API 34
- A physical Android device or emulator (API 26+)

---

## How to Open the Project

1. Open **Android Studio**
2. Click **File → Open**
3. Navigate to: `C:\Users\rajko\Desktop\ANDROID APP\SafeChain`
4. Click **OK**
5. Wait for **Gradle Sync** to complete (~2–3 minutes first time)
6. Click **Run ▶** and select your device/emulator

---

## Features That Work on the Device

### 🥷 Stealth Mode
- App launches as **"DailyNotes"** — a fully functional fake notes app
- **Tap the title 3 times rapidly** to unlock SafeChain

### 🏠 Home Dashboard
- **Safety Score** computed in real-time based on time-of-day + incident data
- **SOS Button** — single tap navigates to Report; **hold 3 seconds** to trigger BLE mesh broadcast + vibration
- Quick access to camera, audio recording, map, and cases

### 📷 Report Wizard (4 Steps)
1. **Evidence Capture** — Tap to open camera; image is LSB-sealed with GPS + timestamp invisibly in pixels
2. **Incident Details** — Auto-detects GPS location; category selector + description
3. **ZK Shield** — Generates simulated ZK proof (proving area residency without revealing identity)
4. **Submit** — SHA-256 hash computed → IPFS CID generated → Polygon TX hash generated → saved to Room DB
- All works **fully offline** — data stored locally, marked for sync when connectivity returns

### 🗺️ Safety Map
- Opens **OpenStreetMap** (no API key required) via OSMDroid
- Centers on your real GPS location
- Coloured **heatmap circles** based on predictive safety scoring (time × incident density × lighting)
- **Community Flag** button — anonymously report an unsafe area; feeds into live heatmap

### 📋 My Cases
- **Real-time LiveData** list of all filed complaints from Room database
- Status badges: SUBMITTED → REVIEWED → ESCALATED → RESOLVED
- Escalation countdown timer — Smart Contract auto-escalation simulation
- IPFS CID and Polygon TX hash visible per case

### 🔐 Evidence Locker
- All captured evidence listed with SHA-256 hash + IPFS CID
- Blockchain seal status indicator

---

## Permissions Required (grant when prompted)
- Location — for GPS auto-fill and map centering
- Camera — for evidence capture
- Storage — for saving evidence images
- Vibrate — for SOS alert

---

## Architecture
```
SafeChain/
├── app/build.gradle          # All dependencies
├── app/src/main/
│   ├── AndroidManifest.xml    # Permissions + activities
│   ├── java/com/safechain/app/
│   │   ├── SplashActivity.java      # Stealth "DailyNotes" launcher
│   │   ├── MainActivity.java        # NavController + offline banner
│   │   ├── fragments/
│   │   │   ├── HomeFragment.java    # Dashboard + SOS
│   │   │   ├── ReportFragment.java  # 4-step reporting wizard
│   │   │   ├── MapFragment.java     # OSMDroid + heatmaps
│   │   │   ├── CasesFragment.java   # Complaint tracker
│   │   │   └── EvidenceFragment.java # Evidence locker
│   │   ├── adapters/
│   │   │   ├── CasesAdapter.java
│   │   │   └── EvidenceAdapter.java
│   │   ├── database/
│   │   │   ├── SafeChainDatabase.java  # Room DB singleton
│   │   │   ├── entities/               # Complaint, Evidence, CommunityReport
│   │   │   └── dao/                    # Room DAOs
│   │   ├── blockchain/
│   │   │   └── BlockchainManager.java  # SHA-256, IPFS, Polygon TX simulation
│   │   └── utils/
│   │       ├── LSBEmbedder.java        # Real LSB steganography
│   │       ├── LocationHelper.java     # GPS + offline fallback
│   │       ├── NetworkUtils.java       # Online/offline detection
│   │       └── SafetyScoreEngine.java  # ML safety scoring simulation
│   └── res/                          # All layouts, drawables, colors, themes
```

---

## Upgrade to Production
| Component | Current (Demo) | Production Path |
|-----------|---------------|-----------------|
| IPFS | Simulated CID | Add Pinata Android SDK |
| Polygon | Mock TX hash | Add Web3j + deploy SafeChain.sol |
| ZK Proofs | Simulated string | Port snarkjs circuit via JNI |
| Backend sync | Local Room DB | Add Retrofit2 + Node.js backend |
| BLE SOS | Vibration + log | Implement BluetoothLeAdvertiser |
