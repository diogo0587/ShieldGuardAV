# ShieldGuard AV - Specification Document

## 1. Project Overview

**Project Name:** ShieldGuard AV  
**Project Type:** Native Android Application (Kotlin)  
**Core Functionality:** Full-featured antivirus application with real-time protection, malware scanning, app management, network security, privacy protection, and device optimization features, competitive with market-leading solutions like AVG, Avast, and McAfee.

## 2. Technology Stack & Choices

### Framework and Language
- **Language:** Kotlin 1.9.x
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34

### Key Libraries/Dependencies
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt (Dagger)
- **Async:** Kotlin Coroutines + Flow
- **Database:** Room (SQLite)
- **Network:** Retrofit2 + OkHttp3
- **UI:** Jetpack Compose + Material3
- **Security:** AndroidX Security Crypto
- **Background:** WorkManager + Foreground Service
- **Navigation:** Compose Navigation

### State Management
- **Approach:** StateFlow + SharedFlow for reactive UI updates
- **ViewModel:** AndroidX ViewModel with SavedStateHandle

### Architecture Pattern
- **Pattern:** Clean Architecture with MVVM
- **Layers:** Presentation (UI) → Domain (Use Cases) → Data (Repository → DataSource)

## 3. Feature List

### Core Protection Features
1. **Real-time Antivirus Scanner**
   - Full system scan (all files)
   - Quick scan (critical areas)
   - Custom folder scan
   - Scheduled scans
   - Quarantine for detected threats

2. **Malware Database**
   - Built-in signature database (2000+ signatures)
   - Cloud-based lookup for new threats
   - Daily database updates

3. **App Scanner**
   - Scan installed apps for malware
   - Permissions analyzer
   - Risk assessment

4. **File Scanner**
   - Scan downloaded files
   - Scan before app installation
   - Auto-scan on download

### Security Features
5. **WiFi Security Scanner**
   - Detect insecure networks
   - Man-in-the-middle detection
   - Encryption strength analysis

6. **Web Protection**
   - Safe browsing (URL checker)
   - Phishing detection
   - Malicious website blocking

7. **Network Monitor**
   - Active connections viewer
   - Data usage per app
   - Suspicious connection alerts

### Privacy Features
8. **Privacy Advisor**
   - App permissions analysis
   - Data tracking app identification
   - Privacy score

9. **App Permissions Manager**
   - Detailed permission viewer
   - Dangerous permission alerts
   - Permission trends

### Device Protection
10. **Device Security**
    - Lost device location
    - SIM card change alert
    - Remote wipe capability
    - Boot protection

11. **Security Dashboard**
    - Overall security score
    - Protection status overview
    - Risk identification

### Optimization Features
12. **Junk Cleaner**
    - Cache files removal
    - Temporary files removal
    - Log files cleanup
    - APK files cleanup

13. **Device Booster**
    - RAM optimization
    - Battery optimization tips
    - Storage analysis

### Additional Features
14. **Notification Center**
    - Protection alerts
    - Scan completion notifications
    - Security warnings

15. **Settings**
    - Scan customization
    - Notification preferences
    - Auto-protection toggle
    - Dark/Light theme

## 4. UI/UX Design Direction

### Overall Visual Style
- **Design System:** Material Design 3
- **Style:** Modern, clean, professional security app
- **Feel:** Trustworthy, powerful, easy to use

### Color Scheme
- **Primary:** Deep Blue (#1565C0) - Trust and security
- **Secondary:** Green (#4CAF50) - Safe/Protected
- **Accent:** Red (#F44336) - Threats/Warnings
- **Background:** Light grey (#FAFAFA) / Dark (#121212)
- **Surface:** White (#FFFFFF) / Dark grey (#1E1E1E)

### Layout Approach
- **Navigation:** Bottom navigation bar with 4 main sections:
  1. Home (Dashboard/Scan)
  2. Apps (App Management)
  3. Security (Privacy/Network)
  4. Settings

- **Home Screen:** 
  - Large circular scan button
  - Security score card
  - Quick action buttons
  - Recent scan results

- **Scan Results:** Cards with threat details, action buttons

- **Theme:** Support for both Light and Dark mode