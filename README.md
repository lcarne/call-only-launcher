# Incoming Call Only Launcher – Minimalist & Secure Android Launcher

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![fr](https://img.shields.io/badge/lang-fr-blue.svg)](README.fr.md)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/platform-Android%206.0+-green.svg)](#requirements)
[![Version](https://img.shields.io/badge/version-1.5.2-blue.svg)](https://github.com/lcarne/call-only-launcher/releases)

<img src="images/app_icon.svg" alt="App icon" width="160"/>

**Incoming Call Only Launcher** is a minimalist, open-source Android launcher designed for seniors and vulnerable users. It transforms an Android device into a **receive-only phone**, allowing incoming calls exclusively from trusted contacts while completely disabling outgoing calls and all non-essential system features.

> **Goal:** Provide a calm, safe, and confusion-free experience while giving caregivers and family members full control over the device.

**Designed for:** Seniors • Care Facilities • Hospitals • Caregivers • People with Cognitive Impairment • Kiosk Devices

---

## 📱 Download from Play Store

<a href="https://play.google.com/store/apps/details?id=com.incomingcallonly.launcher" target="_blank">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" width="200" />
</a>

Or download the latest APK from [GitHub Releases](https://github.com/lcarne/call-only-launcher/releases)

---

## Table of Contents

- [Purpose](#purpose)
- [Key Features](#key-features)
- [Requirements](#requirements)
- [Installation](#installation--setup)
- [Initial Setup](#initial-setup-onboarding)
- [Admin Access](#admin-access-how-to-open-admin-screen)
- [Technical Stack](#technical-stack)
- [Screenshots](#screenshots)
- [Privacy & Data](#privacy--data)
- [Contributing](#contributing)
- [License](#license)
- [Legal Notice](#legal-notice)

---

## Purpose

Incoming Call Only Launcher locks the user into a **single-purpose interface** focused on safety and clarity.

The user can:

- View the current date and time with a **large, high-contrast display**
- Receive **incoming calls only** from a curated list of trusted contacts

Everything else is intentionally hidden or restricted:

- No outgoing calls
- No notifications
- No messaging apps
- No system settings or navigation
- No accidental interactions

This design minimizes confusion and prevents misuse.

**Typical use cases include:**

- Elderly users who should not place accidental or emergency calls
- People with Alzheimer’s disease or cognitive impairment
- Patients in care facilities or hospitals
- Children or vulnerable individuals using a dedicated device
- Situations where caregivers need full control over who can call the device

---

## What this app is (and is not)

**Incoming Call Only Launcher is intentionally limited by design.**

✔ It is:

- A receive-only phone interface
- A safety-focused Android launcher
- A controlled environment for caregivers

✖ It is NOT:

- A standard phone app
- A dialer
- A messaging app
- A general-purpose launcher

---

## Key Features

- **Senior-Centric UI**  
  Large digital clock, full date, and high-contrast theme. Redesigned call screens with extra-large text, vibrant action buttons, and a reassuring visual style.

- **Intelligent Audio Routing**  
  Incoming calls start on **speakerphone by default**, helping users with hearing or dexterity challenges. Switch between speaker and earpiece using large, clear buttons.

- **Safe Call Handling**  
  - Incoming calls ring only for contacts.
  - Unknown callers are **automatically silenced or rejected**.
  - **2-Tap Safety**: Hang-up and Refuse actions require two taps to prevent accidental call termination.

- **Kiosk & Pinning Modes**
  - **Screen Pinning**: Easily lock the app to the screen from the Admin menu (no ADB required).
  - **Device Owner**: For full lockdown (status bar disabled, navigation blocked), set as Device Owner via ADB.

- **Protected Admin Interface**  
  A hidden admin screen allows caregivers to manage contacts and device behavior without risk of accidental access.

- **Simple PIN Access**  
  Admin access is protected by a PIN (default: `1234`) for quick and controlled caregiver access.

- **Call History**
  View a detailed log of incoming, missed, and rejected calls with duration and timestamps.

- **Local Backup & Restore**
  Easily export your contacts list to a JSON file and restore it on another device or after a reset.

---

## Privacy & Data

Incoming Call Only Launcher does **not collect, store, or transmit any personal data**.

- No analytics
- No tracking
- No cloud services
- No third-party integrations

All contacts, settings, and call history are stored **locally on the device only**.

---

## Technical Stack

- **Language**: Kotlin  
- **UI**: Jetpack Compose (Material 3)  
- **Typography**: [Inter font family](https://rsms.me/inter/) (SIL Open Font License)  
- **Architecture**: MVVM + Hilt  
- **Storage**: Room Database  
- **Security**:
  - `DevicePolicyManager` (Device Owner / Kiosk mode)
  - `CallScreeningService` (incoming call filtering)

See [ATTRIBUTIONS.md](ATTRIBUTIONS.md) for third-party licenses.

---

## Screenshots

> Clean, high-contrast UI designed for elderly and vulnerable users.

### Home & Incoming Call

<table>
  <tr>
    <td align="center">
      <img src="images/home.png" width="320" alt="Home screen"/><br/>
      <strong>Home screen</strong>
    </td>
    <td align="center">
      <img src="images/incoming_call.png" width="320" alt="Incoming call"/><br/>
      <strong>Incoming call</strong>
    </td>
    <td align="center">
      <img src="images/ongoing_call.png" width="280" alt="Ongoing call"/><br/>
      <strong>Ongoing call</strong>
    </td>
  </tr>
</table>

---

### Admin & Contact Management

<table>
  <tr>
    <td align="center">
      <img src="images/admin.png" width="320" alt="Admin PIN"/><br/>
      <strong>Admin / PIN entry</strong>
    </td>
    <td align="center">
      <img src="images/contacts.png" width="320" alt="Contacts"/><br/>
      <strong>Contacts management</strong>
    </td>
    <td align="center">
      <img src="images/contacts_add.png" width="320" alt="Add contact"/><br/>
      <strong>Add contact</strong>
    </td>
  </tr>
</table>

---

### Settings

<table>
  <tr>
    <td align="center">
      <img src="images/settings_1.png" width="320" alt="Unlock, Content & System"/><br/>
      <strong>Unlock, Content & System</strong>
    </td>
    <td align="center">
      <img src="images/settings_2.png" width="320" alt="Audio, Display & Power"/><br/>
      <strong>Audio, Display & Power</strong>
    </td> 
    <td align="center">
      <img src="images/settings_3.png" width="320" alt="Display & Localization"/><br/>
      <strong>Display & Localization</strong>
    </td>
    <td align="center">
      <img src="images/settings_4.png" width="320" alt="Data Management & Support"/><br/>
      <strong>Data Management & Support</strong>
    </td>
  </tr>
</table>

---

### Additional States

<table>
  <tr>
    <td align="center">
      <img src="images/home_ring_off.png" width="280" alt="Ring off"/><br/>
      <strong>Ringer disabled</strong>
    </td>
    <td align="center">
      <img src="images/home_night.png" width="280" alt="Night mode"/><br/>
      <strong>Night mode</strong>
    </td>
    <td align="center">
      <img src="images/dim_mode.png" width="280" alt="Dim mode"/><br/>
      <strong>Dim mode</strong>
    </td>
  </tr>
</table>

---

## Requirements

- **Android 6.0** (API level 23) or higher
- Minimum **50 MB** free storage
- Active phone line with incoming call capability
- For Device Owner mode: USB debugging enabled and ADB access

---

## Installation & Setup

### Option 1 – Download from Google Play Store (Recommended)

The easiest way to install is via the official Play Store:

📲 [Get it on Google Play Store](https://play.google.com/store/apps/details?id=com.incomingcallonly.launcher)

This ensures you always have the latest version with automatic updates.

### Option 2 – Download the pre-built APK

You can download a ready-to-install APK from GitHub Releases:

➡️ [GitHub Releases](https://github.com/lcarne/call-only-launcher/releases)

Each release includes:

- A signed APK
- Release notes

**Steps:**

1. Download the `.apk` file from the Releases page.
2. Copy it to the target Android device.
3. Allow installation from unknown sources if prompted.
4. Install the APK.

### Option 3 – Build from source

For developers who want to build from source code:

1. Clone this repository:

   ```bash
   git clone https://github.com/lcarne/call-only-launcher.git
   cd call-only-launcher
   ```

2. Open the project in Android Studio.
3. Build and install the APK on the target device:

   ```bash
   ./gradlew assembleDebug
   ```

---

## Initial Setup (Onboarding)

Upon the first launch, an **onboarding flow** will guide you through:

- Requesting location access (used to display network quality)
- Setting the default phone app
- Setting the default launcher
- Enabling app lock for security

---

## Admin Access (How to open Admin screen)

The Admin interface is intentionally hidden to prevent accidental access by the end user.

- On the home screen, **tap the date/time area 15 times rapidly** to open the Admin entry point.
- Enter the default PIN: `1234` (can be changed in Admin Settings)

### From the Admin interface you can:

- **Manage Contacts**: Add, edit, or remove trusted contacts.
- **View Call History**: Check recent activity including blocked calls.
- **Data Management**:
  - **Export/Import Contacts**: Backup your trusted list to a JSON file.
  - **Reset Data**: Clear call history or factory reset app settings.
- **Screen & Power**: Configure behavior dependent on power state (Plugged In vs On Battery):
  - **Off**: Standard Android timeout.
  - **Dim**: Screen stays on with reduced brightness, showing only the clock.
  - **Awake**: Screen stays on at normal brightness.
- **Customization**: Configure Night mode, Clock color, Ringer volume.
- **System Control**:
  - **Pin App**: Activate Android Screen Pinning to lock the user in the app (no ADB required).
  - **Unpin / Unlock**: Temporarily exit restricted mode.
  - **Set as Default Launcher**: Re-prompt to set as home app if needed.

### Enable True Kiosk Mode (Device Owner)

For full lockdown (disable status bar, navigation, system gestures), set the app as **Device Owner**.

⚠️ **Warning**  
This action is irreversible without ADB access.

#### Prerequisites

- Remove Google accounts from the device (recommended)
- Enable **USB debugging** in Developer Options

#### ADB command

```bash
adb shell dpm set-device-owner com.incomingcallonly.launcher/.receivers.IncomingCallOnlyAdminReceiver
```

If successful, the launcher will be pinned and the status bar/navigation will be disabled according to device policy.

### Emergency Unlock / Remove Device Owner

If you cannot access the Admin unlock button, remove the Device Owner via ADB:

```bash
adb shell dpm remove-active-admin com.incomingcallonly.launcher/.receivers.IncomingCallOnlyAdminReceiver
```

---

## Notes & Implementation Details

- The Admin receiver is `com.incomingcallonly.launcher.receivers.IncomingCallOnlyAdminReceiver` and is declared in the manifest with `BIND_DEVICE_ADMIN` permission.
- Kiosk behavior (lock task packages, disabling the status bar) is controlled via `DevicePolicyManager` in `MainActivity`.

## Contributing

Contributions and corrections are welcome. Please open issues or pull requests for feature requests, fixes, or documentation updates.

## License

This project is licensed under the MIT License, see the [LICENSE](LICENSE) file.

---

## Legal Notice

### Trademarks & Logos

This project references and includes links to the **Google Play Store**. The Google Play logo and branding are trademarks of Google LLC.

- We are **not affiliated** with Google LLC.
- The Google Play Store badge and logo are used under fair use for informational purposes only to direct users to our official app distribution channel.
- This app is distributed under the MIT License, and we comply with all Play Store policies and guidelines.

### Attribution

- [Inter font family](https://rsms.me/inter/) (SIL Open Font License)
- [Material You](https://material.io/you)

For a complete list of third-party licenses and attributions, please see [ATTRIBUTIONS.md](ATTRIBUTIONS.md).

---

_100% free, 100% open source, made with ❤️ for my grandmother_
