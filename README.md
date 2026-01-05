# CallOnly - Simple Android Launcher for Seniors

**CallOnly** est un launcher Android Open Source ultra-simplifié, conçu spécifiquement pour les personnes âgées ou désorientées. Il transforme un smartphone Android en un téléphone basique, facile à utiliser, et sécurisé contre les erreurs de manipulation.

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Android](https://img.shields.io/badge/platform-Android-green.svg)

## 🎯 Objectif

L'objectif de CallOnly est de **verrouiller** l'utilisateur dans une interface de confiance, où il ne peut faire que deux choses :
1.  **Voir l'heure et la date** (très lisible).
2.  **Appeler des contacts favoris** pré-enregistrés (avec de grandes photos).

Tout le reste (paramètres, notifications, autres applications) est masqué ou bloqué pour éviter que l'utilisateur ne se perde.

## ✨ Fonctionnalités

*   **Interface Épurée** :
    *   Horloge géante et date complète.
    *   Grille de contacts avec photos larges et noms lisibles.
    *   Thème à fort contraste pour une meilleure lisibilité.
*   **Mode "Kiosk" (Verrouillé)** :
    *   Se configure comme le Launcher (écran d'accueil) par défaut.
    *   Barres système (notifications, navigation) masquées (Mode Immersif).
    *   Bouton "Retour" désactivé sur l'écran d'accueil.
*   **Gestion Sécurisée** :
    *   Interface d'administration protégée par un code PIN (`1234` par défaut).
    *   Ajout, modification et suppression de contacts par un tiers de confiance.
    *   Sélection de photos depuis la galerie du téléphone.
*   **Offline First** :
    *   Fonctionne 100% hors ligne. Aucune donnée n'est envoyée dans le cloud.
    *   Base de données locale (Room) pour les contacts.

## 🛠️ Stack Technique

*   **Langage** : Kotlin
*   **UI** : Jetpack Compose (Material3)
*   **Architecture** : MVVM + Repository Pattern
*   **Injection de Dépendances** : Hilt
*   **Base de Données** : Room
*   **Images** : Coil

## 🚀 Installation & Configuration

### 1. Compiler et Installer
1.  Clonez ce dépôt :
    ```bash
    git clone https://github.com/votre-username/CallOnly.git
    ```
2.  Ouvrez le projet dans **Android Studio**.
3.  Compilez l'APK et installez-le sur le smartphone cible.

### 2. Définir comme Launcher
Lors du premier appui sur le bouton **Home** (Accueil) physique ou virtuel du téléphone :
1.  Android vous demandera quelle application utiliser.
2.  Sélectionnez **CallOnly**.
3.  Choisissez **"Toujours"** (Always).

### 3. Configurer les Contacts
1.  Sur l'écran d'accueil, appuyez sur le bouton **Paramètres** (⚙️) discrètement placé en haut à droite.
2.  Entrez le code PIN : **1234**.
3.  Appuyez sur le bouton **+** pour ajouter un contact (Nom, Numéro, Photo).

## 🔒 Comment sortir du mode Kiosk ?

Comme l'application est conçue pour bloquer la sortie, il faut une manipulation spécifique pour la désinstaller ou changer de launcher :
1.  Accédez aux **Paramètres Android** (souvent accessible en faisant glisser la barre de notifs depuis le haut, si non bloquée par le modèle de téléphone, ou via le bouton marche/arrêt > mode sécurisé).
2.  Allez dans **Applications > Applications par défaut > Écran d'accueil**.
3.  Changez CallOnly par le launcher système.

## 🤝 Contribuer

Les contributions sont les bienvenues ! Si vous souhaitez améliorer l'accessibilité ou ajouter des fonctionnalités (ex: réception d'appels simplifiée, thèmes de couleurs), n'hésitez pas à ouvrir une Issue ou une Pull Request.

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.
