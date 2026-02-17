# Interbank Transfer Module

## Overview

The `send-interbank` module implements a complete interbank transfer flow for the Mobile Wallet application. It provides a multi-step user interface for transferring money between different bank accounts.

## Architecture

### Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                  Interbank Transfer Flow                         │
└─────────────────────────────────────────────────────────────────┘

1. SELECT ACCOUNT
   ├─ Load user's available accounts
   ├─ Display account list
   └─ User selects sender account
        ↓
2. SEARCH RECIPIENT
   ├─ Search by phone number or account number
   ├─ Display search results
   └─ User selects recipient
        ↓
3. TRANSFER DETAILS
   ├─ Enter amount
   ├─ Enter date
   ├─ Enter description
   └─ Validate inputs
        ↓
4. PREVIEW TRANSFER
   ├─ Display all transfer details
   ├─ Show sender and recipient info
   ├─ Show amount and date
   └─ User confirms or edits
        ↓
5. PROCESS TRANSFER
   ├─ Validate transfer payload
   ├─ Call API to initiate transfer
   └─ Handle response
        ↓
6. RESULT SCREEN
   ├─ SUCCESS: Show confirmation with receipt download option
   └─ FAILED: Show error with retry/support options
```

## Module Structure

```
feature/send-interbank/
├── src/
│   └── commonMain/
│       └── kotlin/org/mifospay/feature/send/interbank/
│           ├── InterbankTransferScreen.kt          # Main entry point
│           ├── InterbankTransferViewModel.kt       # State management
│           ├── InterbankTransferFlowScreen.kt      # Flow orchestrator
│           ├── screens/
│           │   ├── SelectAccountScreen.kt          # Step 1: Account selection
│           │   ├── SearchRecipientScreen.kt        # Step 2: Recipient search
│           │   ├── TransferDetailsScreen.kt        # Step 3: Transfer details
│           │   ├── PreviewTransferScreen.kt        # Step 4: Preview
│           │   └── TransferResultScreens.kt        # Step 5 & 6: Success/Failed
│           ├── navigation/
│           │   └── InterbankTransferNavigation.kt  # Navigation setup
│           └── di/
│               └── InterbankTransferModule.kt      # Dependency injection
└── build.gradle.kts
```

## Screen Details

### 1. Select Account Screen
**Purpose**: Allow user to choose the sender account

**Features**:
- Displays list of available accounts
- Shows account holder name and account number
- Shows account type (e.g., Wallet, Savings)
- Loading state while fetching accounts
- Error handling for account loading failures

**User Actions**:
- Select an account → Navigate to Search Recipient
- Back → Exit flow

### 2. Search Recipient Screen
**Purpose**: Find and select the recipient

**Features**:
- Search field for phone number or account number
- Real-time search results
- Display recipient name and account details
- Empty state when no results found

**User Actions**:
- Enter search query → Display results
- Select recipient → Navigate to Transfer Details
- Back → Return to Select Account

### 3. Transfer Details Screen
**Purpose**: Enter transfer amount, date, and description

**Features**:
- Display selected sender and recipient accounts
- Amount input field (decimal validation)
- Date input field
- Description input field (multi-line)
- Continue button enabled only when all fields are valid

**Validations**:
- Amount must be a valid decimal number
- Amount must be greater than 0
- Description must not be empty
- Date format validation

**User Actions**:
- Fill details → Continue to Preview
- Back → Return to Search Recipient

### 4. Preview Transfer Screen
**Purpose**: Review all transfer details before confirmation

**Features**:
- Display sender account with avatar
- Display recipient account with avatar
- Show transfer amount (highlighted)
- Show transfer date
- Show transfer description
- Edit button to go back and modify details
- Confirm button to proceed with transfer

**User Actions**:
- Confirm → Process transfer
- Edit → Go back to Transfer Details
- Back → Return to Transfer Details

### 5. Transfer Success Screen
**Purpose**: Confirm successful transfer

**Features**:
- Success icon and message
- Display recipient name and transfer amount
- Download receipt button
- Back to home button

**User Actions**:
- Download Receipt → Generate and download receipt
- Back to Home → Return to home screen

### 6. Transfer Failed Screen
**Purpose**: Handle transfer failures

**Features**:
- Error icon and message
- Display error details
- Retry button to attempt transfer again
- Contact support button
- Back to home button

**User Actions**:
- Retry → Go back to Preview and retry
- Contact Support → Open support contact
- Back to Home → Return to home screen

## State Management

### InterbankTransferState
```kotlin
data class InterbankTransferState(
    val currentStep: Step,                    // Current screen in flow
    val loadingState: LoadingState,           // Loading/Error state
    val fromAccounts: List<AccountOption>,    // Available sender accounts
    val selectedFromAccount: AccountOption?,  // Selected sender
    val selectedRecipient: RecipientInfo?,    // Selected recipient
    val transferAmount: String,               // Amount to transfer
    val transferDate: String,                 // Transfer date
    val transferDescription: String,          // Transfer description
    val isProcessing: Boolean,                // Processing transfer
    val errorMessage: String?,                // Error message if any
    val transferResponse: Any?,               // API response
)
```

### RecipientInfo
```kotlin
data class RecipientInfo(
    val clientId: Long,
    val officeId: Int,
    val accountId: Int,
    val accountType: Int,
    val clientName: String,
    val accountNo: String,
)
```

## Actions and Events

### InterbankTransferAction
- `NavigateToRecipientSearch(account)` - Move to search step
- `NavigateToTransferDetails(recipient)` - Move to details step
- `NavigateToPreview` - Move to preview step
- `NavigateBack` - Go to previous step
- `UpdateAmount(amount)` - Update transfer amount
- `UpdateDate(date)` - Update transfer date
- `UpdateDescription(description)` - Update description
- `ConfirmTransfer` - Initiate transfer
- `RetryTransfer` - Retry failed transfer
- `DismissError` - Dismiss error message

### InterbankTransferEvent
- `OnNavigateBack` - User exited flow
- `OnTransferSuccess` - Transfer completed successfully
- `OnTransferFailed(message)` - Transfer failed

## Integration

### Adding to Navigation Graph

```kotlin
interbankTransferScreen(
    onBackClick = { /* Handle back */ },
    onTransferSuccess = { destination -> /* Navigate to destination */ },
    onContactSupport = { /* Open support */ },
)
```

### Navigation to Interbank Transfer

```kotlin
navController.navigateToInterbankTransfer(
    returnDestination = "home",
)
```

### Dependency Injection

Add to your Koin module:

```kotlin
includes(interbankTransferModule)
```

## API Integration

### ThirdPartyTransferRepository
The module uses `ThirdPartyTransferRepository` for API calls:

- `getTransferTemplate()` - Fetch available accounts
- `makeTransfer(payload)` - Initiate transfer

### TransferPayload
```kotlin
data class TransferPayload(
    val fromOfficeId: Int?,
    val fromClientId: Long?,
    val fromAccountType: Int?,
    val fromAccountId: Int?,
    val toOfficeId: Int?,
    val toClientId: Long?,
    val toAccountType: Int?,
    val toAccountId: Int?,
    val transferDate: String?,
    val transferAmount: Double?,
    val transferDescription: String?,
    val dateFormat: String? = "dd MMMM yyyy",
    val locale: String? = "en",
)
```

## Error Handling

The module implements comprehensive error handling:

1. **Account Loading Errors**: Display error message and retry option
2. **Validation Errors**: Show validation messages for each field
3. **Transfer Errors**: Display error details with retry option
4. **Network Errors**: Handle gracefully with retry mechanism

## Future Enhancements

- [ ] Implement actual recipient search from API
- [ ] Add receipt generation and download
- [ ] Implement support contact integration
- [ ] Add transfer history
- [ ] Implement favorite recipients
- [ ] Add scheduled transfers
- [ ] Implement transfer templates
- [ ] Add biometric authentication for confirmation
- [ ] Implement transaction tracking

## Testing

### Unit Tests
- ViewModel state management
- Action handling
- Validation logic

### UI Tests
- Screen navigation flow
- Input validation
- Error handling

### Integration Tests
- End-to-end transfer flow
- API integration
- Error scenarios

## Dependencies

- `core:data` - Repository interfaces
- `core:network` - API models and responses
- `core:designsystem` - UI components and theme
- `core:ui` - Common UI utilities
- Compose - UI framework
- Koin - Dependency injection

## License

Copyright 2025 Mifos Initiative

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
