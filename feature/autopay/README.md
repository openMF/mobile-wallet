# AutoPay Feature

## Overview
The AutoPay feature module provides functionality for setting up and managing automatic payment schedules. This module allows users to configure recurring payments, set up payment rules, and manage their automatic payment preferences.

## Features
- **AutoPay Dashboard**: Main screen displaying active schedules, upcoming payments, and quick actions
- Set up recurring payment schedules
- Configure payment rules and conditions
- Manage automatic payment preferences
- View payment history and status
- Enable/disable automatic payments
- Pull-to-refresh functionality
- Schedule details view with management actions

## AutoPay Dashboard

The main AutoPay Dashboard screen provides a comprehensive view of all AutoPay activities:

### Dashboard Components
- **Dashboard Header**: Shows total active schedules and upcoming payments with visual statistics
- **Quick Actions**: Add new schedule and manage existing schedules buttons
- **Active Schedules**: List of all active AutoPay schedules with status indicators
- **Upcoming Payments**: List of scheduled payments with due dates
- **Pull-to-Refresh**: Swipe down to refresh dashboard data
- **Loading States**: Proper loading indicators during data fetch
- **Empty States**: Helpful messages when no schedules or payments exist

### Schedule Information Displayed
- Schedule name and recipient
- Payment amount and currency
- Frequency (Monthly, Weekly, etc.)
- Next payment date
- Status (Active, Paused, Cancelled, Completed)
- Account number (masked for security)

### Quick Actions
- **Add New**: Navigate to schedule setup screen
- **Manage**: Navigate to rules and preferences management
- **View Details**: Tap on any schedule to see detailed information

### Schedule Details Screen
When a user taps on an active schedule, they can view:
- Complete schedule information
- Payment details
- Schedule management actions (Pause/Resume, Edit, Cancel)

## Screenshots
### Android
*Screenshots will be added as the feature is developed*

### Desktop
*Screenshots will be added as the feature is developed*

### Web
*Screenshots will be added as the feature is developed*

## Module Structure
```
feature/autopay/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/org/mifospay/feature/autopay/
│   │   │   ├── di/
│   │   │   │   └── AutoPayModule.kt
│   │   │   ├── AutoPayScreen.kt (Dashboard)
│   │   │   ├── AutoPayScheduleDetailsScreen.kt
│   │   │   ├── AutoPayNavigation.kt
│   │   │   └── AutoPayViewModel.kt
│   │   └── composeResources/
│   └── androidMain/
│       └── kotlin/org/mifospay/feature/autopay/
├── build.gradle.kts
└── README.md
```

## Data Models

### AutoPaySchedule
```kotlin
data class AutoPaySchedule(
    val id: String,
    val name: String,
    val amount: Double,
    val currency: String,
    val frequency: String,
    val nextPaymentDate: String,
    val status: AutoPayStatus,
    val recipientName: String,
    val accountNumber: String,
)
```

### UpcomingPayment
```kotlin
data class UpcomingPayment(
    val id: String,
    val scheduleName: String,
    val amount: Double,
    val currency: String,
    val dueDate: String,
    val status: PaymentStatus,
    val recipientName: String,
)
```

### Status Enums
```kotlin
enum class AutoPayStatus {
    ACTIVE, PAUSED, CANCELLED, COMPLETED
}

enum class PaymentStatus {
    UPCOMING, PROCESSING, COMPLETED, FAILED
}
```

## Dependencies
- Compose UI components
- Material3 design system
- Koin dependency injection
- Core domain modules (as needed)

## Usage
This module is designed to be integrated into the main application through dependency injection. The AutoPayModule provides the necessary dependencies for the AutoPay feature.

### Navigation
The AutoPay feature includes the following navigation routes:
- `autopay` - Main dashboard
- `autopay/setup` - Setup new schedule
- `autopay/rules` - Manage rules
- `autopay/preferences` - Manage preferences
- `autopay/history` - View history
- `autopay/schedule/{scheduleId}` - Schedule details

## Development Status
✅ **Dashboard Implementation Complete** - AutoPay Dashboard with all required features implemented
- ✅ Display list of active AutoPay schedules
- ✅ Show upcoming payments with due dates
- ✅ Display quick action buttons (Add New, Manage Existing)
- ✅ Show payment status indicators
- ✅ Implement pull-to-refresh functionality
- ✅ Handle loading and error states
- ✅ Schedule details screen
- ✅ Dummy data for demonstration

🚧 **Additional Features** - Setup, Rules, Preferences, and History screens need implementation
