<div align="center">
  <h1>🛡️ SafeChain</h1>
  <h3>Decentralized Offline-First Safety Ecosystem & Immutable Evidence Vault</h3>
  
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
  [![Android](https://img.shields.io/badge/Android-Java%20%7C%20Kotlin-3DDC84?logo=android)](https://developer.android.com/)
  [![Node.js](https://img.shields.io/badge/Node.js-18+-339933?logo=nodedotjs)](https://nodejs.org/)
  [![Python](https://img.shields.io/badge/Python-ML%20%7C%20FastAPI-3776AB?logo=python)](https://python.org)
  [![Polygon](https://img.shields.io/badge/Polygon-Smart%20Contracts-8247E5?logo=polygon)](https://polygon.technology/)
  [![IPFS](https://img.shields.io/badge/IPFS-Decentralized%20Storage-65C2CB?logo=ipfs)](https://ipfs.io/)

  **Built for WomenTechies WT'26**
</div>

---

## 🎯 Problem & Impact

| 📶 82% | ⏳ 4-5 Mins | ⚖️ 60% |
| :---: | :---: | :---: |
| Emergencies occur in low/no connectivity areas | Average delay in centralized SOS routing | Of digital evidence is challenged for tampering |

---

## 💡 Our Solution: Offline BLE Mesh & On-Chain Security

SafeChain eliminates the dependency on cellular networks during emergencies and ensures that all captured evidence is cryptographically sealed and legally undeniable.

```mermaid
graph LR
    classDef mobile fill:#3DDC84,stroke:#fff,stroke-width:2px,color:#000;
    classDef blockchain fill:#8247E5,stroke:#fff,stroke-width:2px,color:#fff;
    classDef ml fill:#3776AB,stroke:#fff,stroke-width:2px,color:#fff;
    classDef alert fill:#FF4B4B,stroke:#fff,stroke-width:2px,color:#fff;

    A[Offline Victim] -->|BLE Distress Signal| B[Nearby Peer Nodes]
    B -->|Mesh Relay| C[Connected Node]
    C -->|Trigger Action| D{SafeChain Engine}
    
    D --> E[IPFS Evidence Vault] & F[ZK-Proof Verification] & G[Community Alert]
    
    E -.-> H([Polygon Blockchain])
    F -.-> H
    
    class A,B,C mobile;
    class H blockchain;
    class D ml;
    class G alert;
```

---

## 🏗️ Complete System Architecture

### High-Level System Design

```mermaid
graph TD
    classDef frontend fill:#1E293B,stroke:#3DDC84,stroke-width:2px,color:#fff
    classDef backend fill:#1E293B,stroke:#339933,stroke-width:2px,color:#fff
    classDef ml fill:#1E293B,stroke:#3776AB,stroke-width:2px,color:#fff
    classDef web3 fill:#1E293B,stroke:#8247E5,stroke-width:2px,color:#fff
    
    subgraph Client Layer [Android Mobile App]
        UI[UI/UX Interfaces]
        BLE[BLE Mesh Engine]
        Capture[Hardware Camera/Mic]
    end

    subgraph API Gateway Layer [Node.js Express]
        Auth[JWT Authentication]
        Routes[API Endpoints]
        Socket[Real-time Websockets]
    end

    subgraph Intelligence Layer [Python Microservices]
        XGBoost[XGBoost Risk Routing]
        Stego[LSB Steganography]
        ZK[Zero-Knowledge Proof Generator]
    end

    subgraph Decentralized Storage & Web3
        IPFS[(IPFS Node)]
        SmartContract[[Solidity Smart Contracts]]
    end

    UI <--> Routes
    BLE -->|Offline Relay| Routes
    Capture --> Stego
    Routes <--> Auth
    Routes <--> XGBoost
    Stego --> IPFS
    ZK --> SmartContract
    IPFS -.-> SmartContract

    class UI,BLE,Capture frontend
    class Auth,Routes,Socket backend
    class XGBoost,Stego,ZK ml
    class IPFS,SmartContract web3
```

---

## 📊 Real-World Example

### Complete Incident Lifecycle

```mermaid
gantt
    title Incident Response & Evidence Journey
    dateFormat  YYYY-MM-DD HH:mm
    axisFormat %H:%M
    
    section Emergency Phase
    BLE SOS Broadcast         :crit, active, 2026-03-31 10:00, 5m
    Mesh Relay (Offline)      :crit, 2026-03-31 10:02, 3m
    
    section Response Phase
    Community Alert Dispatched:active, 2026-03-31 10:05, 10m
    Dynamic Safing Routing    :2026-03-31 10:06, 15m
    
    section Evidence Phase
    Capture & LSB Sealing     :2026-03-31 10:15, 5m
    IPFS Upload & ZK Proof    :2026-03-31 10:20, 10m
    Polygon Smart Contract Mint:2026-03-31 10:30, 5m
```

### Detailed Lifecycle Breakdown

| Timestamp | Phase | Action | Status | Security Level |
| :--- | :--- | :--- | :--- | :--- |
| **10:00 AM** | `SOS TRIGGER` | Button held for 3s. No internet detected. | 🔴 Critical | High |
| **10:02 AM** | `MESH RELAY` | BLE signal picked up by Peer Device B | 🟡 Pending | Encrypted Payload |
| **10:05 AM** | `NETWORK FIX` | Peer B uploads encrypted SOS payload to Node | 🟢 Connected | JWT Auth |
| **10:15 AM** | `EVIDENCE` | Victim records audio. Steganography applied. | 🔵 Processing | LSB Embedded |
| **10:30 AM** | `BLOCKCHAIN` | Evidence CID minted to Polygon Network | 🟣 Immutable | ZK-Rollup |

---

## ⚙️ Core Modules Deep Dive

### 1. Offline BLE Mesh Network
- **Mechanism:** Utilizing Android's `BluetoothLeScanner` and `BluetoothGattServer` to bounce encrypted SOS payloads from phone to phone until an internet-connected node is reached.
- **Payload:** Encrypted Lat/Long + Hardware ID.

### 2. Predictive Safety Routing
- **Model:** Python-backed XGBoost Classifier.
- **Inputs:** Historical incident data, time of day, crowd density APIs, and illumination levels.
- **Output:** Safest generated coordinate path utilizing Google Maps SDK.

### 3. ZK-Proof Anonymous Reporting
Allows victims and whistleblowers to prove authenticity of their organizational identity (e.g., specific college or workplace) without revealing their actual identity, shielding them from retaliation.

---

## 🧪 Testing & Quality

### System Coverage Architecture

```mermaid
pie title Test Coverage Distribution
    "BLE Mesh Connectivity" : 25
    "Smart Contract Audits" : 20
    "ML Routing Accuracy" : 15
    "Android Unit Tests" : 25
    "Steganography Verification" : 15
```

### Evidence Verification Flow

```mermaid
graph LR
    A[Raw Media] --> B(Extract LSB Metadata)
    B --> C{Verify Checksum}
    C -->|Match| D[Cross-reference IPFS CID]
    C -->|Mismatch| E[Flag Tampered]
    D --> F{Query Smart Contract}
    F -->|Verified| G((Admissible in Court))
    F -->|Not Found| E
```

---
<div align="center">
  <i>Empowering personal safety through decentralization.</i>
</div>
