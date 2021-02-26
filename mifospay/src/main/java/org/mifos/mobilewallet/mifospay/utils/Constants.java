package org.mifos.mobilewallet.mifospay.utils;

import org.mifos.mobilewallet.mifospay.R;

/**
 * Created by naman on 17/6/17.
 */

public class Constants {

    public static final String BASIC = "Basic ";

    public static final String CLIENT_ID = "client_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String ACCOUNT = "account";
    public static final String TO_EXTERNAL_ID = "to_external_id";
    public static final String RUPEE = "₹";
    public static final String QR_DATA = "qr_data";
    public static final String MERCHANT_NAME = "merchant_name";
    public static final String MERCHANT_VPA = "merchant_vpa";
    public static final String MERCHANT_ACCOUNT_NO = "merchant_account_no";

    public static final String FILE = "file";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String CHOOSE_A_FILE_TO_UPLOAD = "Choose a file to upload.";
    public static final String ERROR_UPLOADING_DOCS = "Error uploading docs.";
    public static final String KYC_LEVEL_2_DOCUMENTS_ADDED_SUCCESSFULLY =
            "KYC Level 2 documents added successfully.";
    public static final String IMAGE = "image/*";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String ERROR_ADDING_KYC_LEVEL_1_DETAILS =
            "Error adding KYC Level 1 details.";
    public static final String KYC_LEVEL_1_DETAILS_ADDED_SUCCESSFULLY =
            "KYC Level 1 details added successfully.";
    public static final String PLEASE_TRY_AGAIN_LATER = "Please try again later.";
    public static final String PLEASE_WAIT = "Please wait..";
    public static final String COMPLETE_KYC = "Complete KYC";
    public static final String NEED_EXTERNAL_STORAGE_PERMISSION_TO_BROWSE_DOCUMENTS =
            "Need external storage permission to browse documents.";
    public static final String CHOOSE_FILE = "Choose File";
    public static final String KYC_REGISTRATION_LEVEL_2 = "KYC Registration Level 2";
    public static final String KYC_REGISTRATION_LEVEL_1 = "KYC Registration Level 1";
    public static final String DD_MM_YY = "dd/MM/yy";
    public static final String ERROR_DELETING_CARD = "Error deleting card.";
    public static final String CARD_DELETED_SUCCESSFULLY = "Card deleted successfully.";
    public static final String DELETING_CARD = "Deleting Card..";
    public static final String ERROR_UPDATING_CARD = "Error updating card.";
    public static final String CARD_UPDATED_SUCCESSFULLY = "Card updated successfully.";
    public static final String INVALID_CREDIT_CARD_NUMBER = "Invalid Credit Card Number";
    public static final String UPDATING_CARD = "Updating Card..";
    public static final String ERROR_ADDING_CARD = "Error adding card.";
    public static final String CARD_ADDED_SUCCESSFULLY = "Card added successfully.";
    public static final String ADDING_CARD = "Adding Card..";

    public static final String ADD_CARD_DIALOG = "Add Card Dialog";
    public static final String EDIT_CARD_DIALOG = "Edit Card Dialog";
    public static final String SAVED_CARDS = "Saved Cards";
    public static final String UPDATE = "Update";
    public static final String KYC_REGISTRATION_LEVEL_3 = "KYC Registration Level 3";
    public static final String OK = "OK";
    public static final String SCAN_CODE = "Scan code";
    public static final String QR_CODE = "QR code";
    public static final String FAILED_TO_WRITE_DATA_TO_QR = "Failed to write data to qr";
    public static final String ERROR_OCCURRED = "Error occurred";
    public static final String LOGGING_IN = "Logging in..";
    public static final String HOME = "Home";
    public static final String PROFILE = "Profile";
    public static final String ERROR_FETCHING_BALANCE = "Error fetching balance";
    public static final String UNABLE_TO_PROCESS_TRANSFER = "Unable to process transfer";
    public static final String TRANSACTION_SUCCESSFUL = "Transaction successful";
    public static final String SENDING_MONEY = "Sending money...";
    public static final String INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String ERROR_FINDING_VPA = "Error finding Virtual Payment Address";
    public static final String ERROR_FINDING_MOBILE_NUMBER = "Error finding Mobile Number";
    public static final String PLEASE_ENTER_VALID_AMOUNT = "Please enter a valid amount";
    public static final String SELF_ACCOUNT_ERROR = "Self Account transfer is not allowed";
    public static final String PLEASE_ENTER_AMOUNT =
            "Please enter a valid amount before making the transfer";
    public static final String NEED_READ_CONTACTS_PERMISSION = "Need read contacts permission";
    public static final String NEED_CAMERA_PERMISSION_TO_SCAN_QR_CODE =
            "Need camera permission to scan qr code.";
    public static final String ERROR_CHOOSING_CONTACT = "Error choosing contact";
    public static final String MAKE_TRANSFER_FRAGMENT = "Make Transfer Fragment";
    public static final String PLEASE_ENTER_ALL_THE_FIELDS = "Please enter all the fields";
    public static final String TRANSFER = "Transfer";
    public static final String TRANSACTION_DETAILS = "Transaction Details";
    public static final String ACCOUNT_NUMBER = "Account Number : ";
    public static final String TRANSACTION = "transaction";
    public static final String TRANSACTIONS_HISTORY = "Transactions History History";
    public static final String SPECIFIC_TRANSACTIONS = "Specific Transactions History";
    public static final String HISTORY_NOT_AVAILABLE = "No Transaction History Available";
    public static final String RECEIPT_DOMAIN = "https://receipt.mifospay.com/";
    public static final String OTHER = "Other";
    public static final String CREDIT = "Credit";
    public static final String DEBIT = "Debit";
    public static final String RECEIPT_ID = "Receipt ID";
    public static final String DATE = "Date";
    public static final String TRANSACTION_ID = "Transaction ID";
    public static final String TRANSACTIONS = "transactions";
    public static final String ERROR_FETCHING_TRANSACTIONS = "Error fetching transactions";
    public static final String TRANSFER_DETAILS = "transfer details";
    public static final String UNIQUE_PAYMENT_LINK_COPIED_TO_CLIPBOARD =
            "Unique Payment Link copied to clipboard";
    public static final String UNIQUE_PAYMENT_LINK = "Unique Payment Link";
    public static final String STATUS = "Status";
    public static final String UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD =
            "Unique Receipt Link copied to clipboard";
    public static final String UNIQUE_RECEIPT_LINK = "Unique Receipt Link";
    public static final String DONE = "Done";
    public static final String PENDING = "Pending";
    public static final String ITEMS = "Item(s)";
    public static final String AMOUNT = "Amount";
    public static final String INR = "INR";
    public static final String CONSUMER = "Consumer";
    public static final String MERCHANT = "Merchant";
    public static final String INVOICE = "Invoice";
    public static final String NEED_EXTERNAL_STORAGE_PERMISSION_TO_DOWNLOAD_RECEIPT =
            "Need external storage permission to download receipt";
    public static final String RECEIPT_DOWNLOADED_SUCCESSFULLY = "Receipt Downloaded Successfully";
    public static final String ERROR_DOWNLOADING_RECEIPT = "Error downloading receipt";
    public static final String MIFOSPAY = "mifospay";
    public static final String RECEIPT = "Receipt";
    public static final String PDF = ".pdf";
    public static final String ERROR_FETCHING_RECEIPT = "Error fetching receipt";
    public static final String INVOICE_DOMAIN = "https://invoice.mifospay.com/";

    public static final String SENDING_OTP_TO_YOUR_MOBILE_NUMBER =
            "Sending OTP to your mobile number..";
    public static final String MOBILE_NUMBER = "Mobile Number";
    public static final String COUNTRY = "Country";
    public static final String GOOGLE_SIGN_IN_FAILED = "Google Sign in failed.";
    public static final String GOOGLE_GIVEN_NAME = "GOOGLE_GIVEN_NAME";
    public static final String GOOGLE_FAMILY_NAME = "GOOGLE_FAMILY_NAME";
    public static final String GOOGLE_EMAIL = "GOOGLE_EMAIL";
    public static final String GOOGLE_DISPLAY_NAME = "GOOGLE_DISPLAY_NAME";
    public static final String GOOGLE_PHOTO_URI = "GOOGLE_PHOTO_URI";
    public static final String CHOOSE_SIGNUP_METHOD = "Choose Signup Method";
    public static final String LOGGING_OUT = "Logging out...";
    public static final String NEED_EXTERNAL_STORAGE_PERMISSION_TO_BROWSE_IMAGES =
            "Need external storage permission to browse images";
    public static final String SETTINGS = "Settings";
    public static final String EDIT_PROFILE = "Edit Profile";
    public static final String FAQ = "Frequent Asked Questions";
    public static final String LINKED_BANK_ACCOUNTS = "Linked Bank Accounts";
    public static final String BANK_ACCOUNT_DETAILS = "Bank Account Details";
    public static final String NEW_BANK_ACCOUNT = "newBankAccount";
    public static final String VERIFYING_MOBILE_NUMBER = "Verifying mobile number..";
    public static final String MIFOS_SAVINGS_PRODUCT_ID = "Mifos Savings Product Id";
    public static final String MERCHANTS = "Merchants";
    public static final String UPI_PIN = "UPI PIN";
    public static final String STEP = "step";
    public static final String TYPE = "Type";
    public static final String SETUP = "Setup";
    public static final String BANK_DELETED_SUCCESSFULLY = "Bank deleted successfully";
    public static final String ERROR_OCCURRED_WHILE_CHANGING_UPI_PIN =
            "Error occurred while changing UPI PIN";
    public static final String SETUP_UPI = "Setup UPI";
    public static final String ERROR_WHILE_SETTING_UP_UPI_PIN = "Error while setting up UPI PIN";
    public static final String UPI_PIN_SETUP_COMPLETED_SUCCESSFULLY =
            "UPI PIN Setup Completed Successfully";
    public static final String FORGOT = "Forgot";
    public static final String CHANGE = "Change";
    public static final String OTP = "otp";
    public static final String SETUP_UPI_PIN = "Setup UPI PIN";
    public static final String SETTING_UP_UPI_PIN = "Setting up UPI PIN..";
    public static final String UPDATED_BANK_ACCOUNT = "Updated Bank Account";
    public static final String INDEX = "Index";
    public static final String CHANGE_UPI_PIN = "Change UPI PIN";
    public static final String FORGOT_UPI_PIN = "Forgot UPI PIN";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final int HOME_HISTORY_TRANSACTIONS_LIMIT = 5;

    public static final String ERROR_FIELDS_CANNOT_BE_EMPTY = "Fields cannot be empty";
    public static final String ERROR_VALIDATING_PASSWORD = "Passwords are not the same";
    public static final String ERROR_PASSWORDS_CANT_BE_SAME =
            "New password can't be the same as old password.";

    public static final String CHANGE_PROFILE_IMAGE_KEY = "CHANGE_PROFILE_IMAGE_KEY";
    public static final String CHANGE_PROFILE_IMAGE_VALUE = "CHANGE_PROFILE_IMAGE_VALUE";
    public static final String TAP_TO_REVEAL = "Tap to Reveal";
    public static final String NAME = "Name : ";
    public static final String ERROR_FETCHING_TRANSACTION_DETAILS = "Error fetching details";

    public static final int WHITE_BACK_BUTTON = R.drawable.ic_arrow_back_white_24dp;
    public static final int BLACK_BACK_BUTTON = R.drawable.ic_arrow_back_black_24dp;
    public static final String VIEW = "View";

    public static final String CURRENT_PASSCODE = "current passcode";
    public static final String UPDATE_PASSCODE = "update passcode";
    public static final String SELECT_DATE = "SELECT DATE";
    public static final String SI_ID = "standing_instruction_id";
    public static final String UNAUTHORIZED_ERROR = "401 Unauthorized";

}
