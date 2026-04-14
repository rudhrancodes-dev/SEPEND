# SEPEND - Smart Expense Tracker

A modern, AI-powered expense tracking Android application built with Kotlin and Jetpack Compose.

## Features

- 💰 **Expense Management** - Track and categorize your daily expenses
- 📊 **Analytics & Reports** - Visualize spending patterns with detailed analytics
- 💳 **Budget Tracking** - Set and monitor budgets per category
- 🔄 **Recurring Expenses** - Automatically track recurring payments (daily, weekly, monthly, yearly)
- 👥 **Bill Splitting** - Split bills with friends and track payments
- 🔔 **Smart Notifications** - Get alerts when budgets are exceeded
- 🤖 **AI Insights** - Receive personalized spending recommendations and insights

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with StateFlow
- **Database**: Firebase Firestore + Firebase Authentication
- **Async**: Kotlin Coroutines
- **Version**: 1.0

## Project Structure

```
SEPEND/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/sepend/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── model/          # Data models (Expense, Budget, etc.)
│   │   │   │   └── repository/     # Repository layer
│   │   │   ├── service/            # Firebase & AI services
│   │   │   ├── ui/
│   │   │   │   ├── screens/        # UI screens
│   │   │   │   ├── theme/          # Material Design 3 theme
│   │   │   │   └── viewmodel/      # ViewModels
│   │   └── res/                    # Resources
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites
- Android Studio Giraffe or later
- Android SDK 24+ (min), 35+ (target)
- Kotlin 1.9+

### Setup

1. Clone the repository:
```bash
git clone https://github.com/rudhrancodes-dev/SEPEND.git
cd SEPEND
```

2. Open in Android Studio:
```bash
./gradlew build
```

3. Run on emulator or device:
```bash
./gradlew installDebug
```

## Features in Detail

### Expense Tracking
- Add expenses with category, amount, payment method, and notes
- View expense history with filtering options
- Delete expenses with confirmation

### Budget Management
- Create category-specific budgets
- Visual progress indicators showing budget usage
- Alerts when approaching or exceeding budget limits

### Analytics Dashboard
- Monthly spending trends
- Category-wise breakdown
- Total and average expense calculations
- AI-powered insights and recommendations

### Recurring Expenses
- Set up recurring payments (Daily, Weekly, Monthly, Yearly)
- Toggle recurring expenses on/off
- Automatic tracking and notifications

### Bill Splitting
- Create bills and add participants
- Track individual shares
- Mark payments as completed
- Calculate who owes whom

### AI Integration
- Smart spending insights based on expense patterns
- Budget optimization recommendations
- Category-specific spending advice
- Personalized financial tips

## Firebase Setup

This app uses Firebase for authentication and data storage:

1. Create a Firebase project at https://console.firebase.google.com
2. Enable Firestore Database and Authentication
3. Download `google-services.json` and place in `app/` directory
4. Configure Firebase in your project settings

## Building Release APK

```bash
./gradlew assembleRelease
```

The APK will be available at: `app/build/outputs/apk/release/app-release.apk`

## License

This project is provided as-is for educational and personal use.

## Author

Rudhran - [@rudhrancodes-dev](https://github.com/rudhrancodes-dev)

---

**Note**: For production deployment, ensure proper Firebase configuration and API security measures are in place.
