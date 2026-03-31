<div align="center">

<a href="#">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=32&duration=3000&pause=1000&color=8247E5&center=true&vCenter=true&width=600&lines=%F0%9F%9B%A1%EF%B8%8F+Suraksha;Unified+Safety+Platform;Anonymous+%C2%B7+Tamper-Proof+%C2%B7+Women-First" alt="Typing SVG" />
</a>

<br/>

*Anonymous incident reporting · Real-time urban safety intelligence · Women-centric legal tools*

<br/>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Node.js](https://img.shields.io/badge/Node.js-Express-339933?logo=nodedotjs)](https://nodejs.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?logo=mongodb)](https://mongodb.com/)
[![Polygon](https://img.shields.io/badge/Polygon-Mumbai-8247E5?logo=polygon)](https://polygon.technology/)
[![IPFS](https://img.shields.io/badge/IPFS-Pinata-65C2CB?logo=ipfs)](https://ipfs.io/)
[![PWA](https://img.shields.io/badge/PWA-Offline--First-5A0FC8)](https://web.dev/progressive-web-apps/)

**Built for GDG WTF'26 · VIT Vellore**

</div>

---

## 🎯 The Problem

Women in India face harassment, stalking, and violence in urban spaces every single day. Yet the vast majority of incidents go **unreported** — not because victims don't want justice, but because the systems meant to protect them are broken in three fundamental ways:

| 😶 Fear of Exposure | ⚖️ Legal Maze | 🗺️ Zero Safety Intelligence |
|:---:|:---:|:---:|
| Filing a complaint means revealing your name, phone, and address — putting victims at further risk of retaliation | FIR procedures are opaque, intimidating, and require legal literacy most people don't have | There is no real-time, crowd-sourced data telling you which streets, stops, and areas are genuinely dangerous right now |

**Suraksha solves all three** — zero login, plain-language legal guidance, live community safety map, and auto-generated PDF reports ready for police or NGO submission.

---

## 🏗️ Diagram 1 — System Architecture

```mermaid
graph TB
    subgraph Client["Frontend — HTML · CSS · Vanilla JS"]
        UI1["report.html — File complaint"]
        UI2["track.html — Track status"]
        UI3["evidence.html — Evidence locker"]
        UI4["map.html — Safety heatmap"]
        UI5["legal.html — Legal guide"]
        UI6["route.html — Safe route"]
        UI7["contacts.html — Trusted contacts"]
    end

    subgraph Backend["Backend — Node.js · Express · Port 5000"]
        R1["complaints API"]
        R2["evidence API"]
        R3["safety API"]
        R4["report API — PDF"]
    end

    subgraph Storage["Storage Layer"]
        DB[("MongoDB Atlas — Complaints · Evidence · Scores")]
        IPFS[("Pinata IPFS — Evidence files")]
        BC[("Polygon Mumbai — Hash timestamps")]
    end

    Client -->|REST API| Backend
    Backend --> DB
    Backend --> IPFS
    Backend --> BC
```

---

## 🔄 Diagram 2 — Full Request Lifecycle

```mermaid
sequenceDiagram
    autonumber
    actor User as User Browser
    participant FE as Frontend
    participant BE as Backend API
    participant DB as MongoDB
    participant IPFS as Pinata IPFS
    participant Chain as Polygon

    User->>FE: Opens report page
    FE->>FE: crypto.randomUUID() generates anonymous token
    Note over FE: Token saved to localStorage only.<br/>No name, email, or IP ever sent.

    User->>FE: Fills complaint form and picks risk tags
    FE->>BE: POST complaints with token, category, description, riskTags
    BE->>DB: Save complaint document
    DB-->>BE: id and status submitted
    BE-->>FE: token and complaint id
    FE-->>User: Complaint filed — save your token

    User->>FE: Uploads evidence file
    FE->>FE: SHA-256 hash computed in browser via Web Crypto API
    FE->>BE: POST evidence with file, complaintId, hash
    BE->>BE: Verify hash server-side
    BE->>IPFS: Upload file buffer
    IPFS-->>BE: ipfsCid returned
    BE->>Chain: storeHash bytes32 via Ethers.js
    Chain-->>BE: txHash returned
    BE->>DB: Save sha256, ipfsCid, txHash, privacyControl
    BE-->>FE: sha256, ipfsCid, txHash
    FE-->>User: Evidence sealed and timestamped on blockchain
```

---

## 🗺️ Diagram 3 — Safety Map Data Flow

```mermaid
flowchart LR
    A(["User flags unsafe location"]) --> B["POST safety flag endpoint"]
    B --> C{"Update SafetyScore document"}
    C --> D["Increment incidentCount"]
    C --> E["Attach riskTags\nharass · stalk · dark · isolated"]
    D & E --> F["Recalculate score\nvia scoring formula"]

    F --> G{"Score threshold check"}
    G -->|"70 to 100"| H["Safe — Green marker"]
    G -->|"40 to 69"| I["Caution — Amber marker"]
    G -->|"0 to 39"| J["Unsafe — Red marker"]

    H & I & J --> K["GET heatmap endpoint\nreturns all incident points"]
    K --> L["Leaflet.heat re-renders\nheatmap layer"]
    L --> M(["All users see\nupdated live map"])

    style A fill:#8247E5,color:#fff
    style M fill:#0F6E56,color:#fff
    style H fill:#3B6D11,color:#fff
    style I fill:#BA7517,color:#fff
    style J fill:#A32D2D,color:#fff
```

---

## 🔐 Diagram 4 — Anonymity Architecture

```mermaid
flowchart TD
    A["User opens browser"] --> B["crypto.randomUUID generates token client-side"]
    B --> C["Token saved ONLY in browser localStorage"]
    C --> D["Sent as a data field — not as authentication"]

    D --> E["What MongoDB STORES\n──────────────────\nanonymousToken\ncategory · description\ncoordinates · riskTags\nstatus · statusHistory"]

    D --> F["What MongoDB NEVER STORES\n──────────────────────────\nName or email\nIP address\nDevice fingerprint\nPhone number"]

    C --> G["Trusted contacts linked\nvia same anonymous token"]
    G --> H["Zero identity bridge\nbetween contact and real person"]

    style F fill:#FCEBEB,color:#501313,stroke:#A32D2D
    style E fill:#EAF3DE,color:#173404,stroke:#3B6D11
    style B fill:#EEEDFE,color:#26215C,stroke:#534AB7
    style H fill:#EAF3DE,color:#173404,stroke:#3B6D11
```

---

## 📊 Diagram 5 — Safety Score Formula

```mermaid
graph TD
    START["Start Score = 100"] --> I1

    subgraph Penalties["Deduction Rules — applied in order"]
        I1["Each incident at location\n minus 5 pts · max minus 50"] --> I2
        I2["Poor lighting rating 1 out of 5\n minus 10 pts"] --> I3
        I3["Night hours 9PM to 5AM minus 15 pts\nEvening 6PM to 9PM minus 8 pts"] --> I4
        I4["Low crowd density at night\n minus 10 pts"] --> I5
        I5["Incident within last 24 hours\n minus 20 pts"] --> I6
        I6["Incident within last 7 days\n minus 10 pts"] --> I7
        I7["Risk tag penalties\nharassment or stalking minus 5 each"]
    end

    I7 --> FINAL["Final Score — 0 to 100"]

    FINAL --> R1["Safe — 70 to 100"]
    FINAL --> R2["Caution — 40 to 69"]
    FINAL --> R3["Unsafe — 0 to 39"]

    style START fill:#EEEDFE,color:#26215C,stroke:#534AB7
    style R1 fill:#3B6D11,color:#fff
    style R2 fill:#BA7517,color:#fff
    style R3 fill:#A32D2D,color:#fff
```

---

## 🚦 Diagram 6 — Build Roadmap

```mermaid
gantt
    title Suraksha — Build Phases
    dateFormat  X
    axisFormat  Phase %s

    section Foundation
    Phase 1 — Frontend UI only          :done, 0, 1
    Phase 2 — Backend and MongoDB       :done, 1, 2

    section Evidence and Map
    Phase 3 — Evidence Locker and IPFS  :done, 2, 3
    Phase 4 — Safety Map and Heatmap    :active, 3, 4

    section Women-Centric
    Phase 5 — Legal + Route + Contacts  :5, 4, 5
    Phase 6 — Legal PDF Generator       :6, 5, 6

    section Advanced
    Phase 7 — Blockchain Timestamping   :7, 6, 7
    Phase 8 — Offline and PWA           :8, 7, 8
```

---

## 📦 Project Structure

```
suraksha/
│
├── frontend/
│   ├── index.html               # Landing page
│   ├── report.html              # File a complaint
│   ├── track.html               # Track complaint status
│   ├── evidence.html            # Evidence locker
│   ├── map.html                 # Real-time safety map
│   ├── legal.html               ★ Legal awareness + rights guide
│   ├── route.html               ★ Safe route recommendation
│   ├── contacts.html            ★ Trusted contacts + live location share
│   │
│   ├── css/
│   │   ├── global.css           # Design tokens, reset, typography
│   │   ├── components.css       # Buttons, cards, forms, badges
│   │   └── animations.css       # Transitions and keyframes
│   │
│   └── js/
│       ├── app.js               # Shared utils, API base URL, token helper
│       ├── report.js            # Complaint form + risk tag selector
│       ├── track.js             # Status polling
│       ├── evidence.js          # File upload + SHA-256 hash (browser)
│       ├── map.js               # Leaflet + heatmap + risk tag layer
│       ├── offline.js           # Service worker registration
│       ├── legal.js             ★ Legal guidance lookup
│       ├── route.js             ★ Safe route using safety scores
│       ├── contacts.js          ★ Trusted contacts CRUD + location share
│       └── report-gen.js        ★ Trigger PDF download from browser
│
├── backend/
│   ├── server.js                # Entry point, middleware, route mounting
│   │
│   ├── routes/
│   │   ├── complaints.js        # /api/complaints
│   │   ├── evidence.js          # /api/evidence
│   │   ├── safety.js            # /api/safety
│   │   └── report.js            ★ /api/report — PDF generation
│   │
│   ├── models/
│   │   ├── Complaint.js         # + riskTags field
│   │   ├── Evidence.js          # + privacyControl field
│   │   ├── SafetyScore.js       # + riskTags array
│   │   └── TrustedContact.js    ★ Emergency contacts schema
│   │
│   └── utils/
│       ├── hash.js              # SHA-256 server-side verify
│       ├── ipfs.js              # Pinata upload helper
│       ├── safetyScore.js       # Scoring formula with tag penalties
│       ├── routeScore.js        ★ Score a path between coordinates
│       └── pdfReport.js         ★ PDFKit legal report builder
│
├── contracts/
│   ├── EvidenceRegistry.sol     # Timestamps hashes on-chain
│   └── hardhat.config.js
│
├── sw.js                        # Service worker — offline support
└── manifest.json                # PWA manifest
```

---

## 📡 API Reference

```
Complaints
  POST   /api/complaints                    →  File complaint → { token, id }
  GET    /api/complaints/:token             →  Status + full history

Evidence
  POST   /api/evidence/upload               →  { sha256, ipfsCid, txHash }
  GET    /api/evidence/:complaintId         →  List evidence for complaint
  PATCH  /api/evidence/:id/privacy       ★  →  Update privacy control

Safety
  GET    /api/safety/score?lat=&lng=        →  Score for coordinates
  GET    /api/safety/heatmap                →  Incident points for Leaflet.heat
  POST   /api/safety/flag                   →  Anonymous community flag
  GET    /api/safety/predict?lat=&lng=      →  Predicted risk — next 6 hrs
  GET    /api/safety/route?from=&to=     ★  →  Safe route between two points
  GET    /api/safety/tags?lat=&lng=      ★  →  Risk tags for a location

Legal PDF                                ★
  POST   /api/report/generate               →  Generate + stream PDF report
```

---

## ✨ Feature Set

| Feature | Category |
|---|---|
| Anonymous complaint filing — zero login, zero registration | Core |
| Real-time complaint status tracking via anonymous token | Core |
| Full status lifecycle: submitted → under review → escalated → resolved | Core |
| Gender-specific risk tagging (harassment-prone, stalking-prone, isolated) | Core |
| Digital evidence locker — drag & drop files | Core |
| Evidence privacy control (private / share-on-demand / public) | Core |
| SHA-256 tamper-proof hashing computed in browser | Core |
| IPFS decentralised storage via Pinata | ⭐ Brownie |
| Blockchain evidence timestamping on Polygon Mumbai | ⭐ Brownie |
| Real-time safety map with crowd-sourced heatmap | ⭐ Brownie |
| Community anonymous incident flagging | ⭐ Brownie |
| Predictive safety alerts based on time + location | ⭐ Brownie |
| Offline reporting support via IndexedDB queue | ⭐ Brownie |
| **Legal awareness + plain-language rights guide** | ★ Women-centric |
| **Safe route recommendation (avoids low-score zones)** | ★ Women-centric |
| **Trusted contact system + live location alert** | ★ Women-centric |
| **Auto-generated legal PDF report — police / NGO ready** | ★ Women-centric |
| PWA — installable on phone, works offline | PWA |

---

## 🧰 Tech Stack

| Layer | Tool | Cost |
|---|---|---|
| Frontend | HTML + CSS + Vanilla JS | Free |
| Maps | Leaflet.js + OpenStreetMap | Free |
| Backend | Node.js + Express.js | Free |
| Database | MongoDB Atlas | Free tier |
| File storage | Multer + GridFS | Free |
| Decentralised storage | Pinata IPFS — 5 GB | Free |
| Blockchain | Polygon Mumbai testnet | Free |
| Smart contracts | Solidity + Hardhat | Free |
| PDF generation | PDFKit | Free |
| Offline | Service Worker + IndexedDB | Free |

> **Total infrastructure cost: $0**

---

## 🚀 Quick Start

```bash
git clone https://github.com/yourname/suraksha.git
cd suraksha

# Backend
cd backend
npm install
cp .env.example .env       # fill in MONGO_URI + Pinata keys
node server.js             # → http://localhost:5000

# Frontend (new terminal)
cd ../frontend
npx serve .                # → http://localhost:3000
```

```env
PORT=5000
MONGO_URI=mongodb+srv://user:pass@cluster.mongodb.net/suraksha
PINATA_API_KEY=your_key
PINATA_SECRET_KEY=your_secret
POLYGON_RPC_URL=https://rpc-mumbai.maticvigil.com
CONTRACT_ADDRESS=deployed_address
```

---

<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=14&duration=4000&pause=1000&color=8247E5&center=true&vCenter=true&width=500&lines=Protecting+communities+through+anonymous+reporting;Tamper-proof+evidence+on+the+blockchain;Zero+login.+Zero+identity.+Full+justice." alt="Footer Typing SVG" />

<br/>

**MIT License · GDG WTF'26 · VIT Vellore**

</div>
