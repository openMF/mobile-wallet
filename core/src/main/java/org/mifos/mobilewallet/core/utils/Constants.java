package org.mifos.mobilewallet.core.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by naman on 17/6/17.
 */

public class Constants {

    public static final String BASIC = "Basic ";

    public static final String SAVINGS = "savingsAccounts";
    public static final String TRANSACTIONS = "transactions";

    public static final int WALLET_ACCOUNT_SAVINGS_PRODUCT_ID = 165;

    public static final int MIFOS_MERCHANT_SAVINGS_PRODUCT_ID = 165; // 372
    public static final int MIFOS_CONSUMER_SAVINGS_PRODUCT_ID = 165; // 373

    private static final int MOBILE_WALLET_ROLE_ID = 471;
    private static final int SUPER_USER_ROLE_ID = 1;
    public static final Collection<Integer> NEW_USER_ROLE_IDS = Collections.unmodifiableList(
            Arrays.asList(MOBILE_WALLET_ROLE_ID, SUPER_USER_ROLE_ID));

    public static final String ENTITY_TYPE_CLIENTS = "clients";

    public static final String FETCH_ACCOUNT_TRANSFER_USECASE = "FetchAccountTransfer";
    public static final String NO_SAVED_CARDS = "No saved cards.";
    public static final String MIFOS_PASSWORD = "mifos:password";
    public static final String ERROR_VERIFYING_USER = "Error verifying user";
    public static final String ERROR_REGISTERING_USER = "Error registering user";
    public static final String NO_CLIENTS_FOUND = "No clients found";
    public static final String ERROR_SEARCHING_CLIENTS = "Error searching clients";
    public static final String CLIENTS = "clients";
    public static final String NO_ACCOUNTS_FOUND = "No accounts found";
    public static final String ERROR_FETCHING_ACCOUNTS = "Error fetching accounts";
    public static final String ERROR_MAKING_TRANSFER = "Error making transfer";
    public static final String WALLET_TRANSFER = "Wallet transfer";
    public static final String ERROR_ADDING_BENEFICIARY = "Error adding beneficiary";
    public static final String ERROR_FETCHING_BENEFICIARIES = "Error fetching beneficiaries";
    public static final String ERROR_FETCHING_TO_ACCOUNT = "Error fetching to account";
    public static final String NO_WALLET_FOUND = "No wallet found";
    public static final String ERROR_FETCHING_FROM_ACCOUNT = "Error fetching from account";
    public static final String ERROR_FETCHING_CLIENT_DATA = "Error fetching client data";
    public static final String NO_CLIENT_FOUND = "No client found";
    public static final String ERROR_LOGGING_IN = "Error logging in";
    public static final String ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS =
            "Error fetching remote account transactions";
    public static final String PDF = "PDF";
    public static final String ERROR_FETCHING_ACCOUNT = "Error fetching account";
    public static final String NO_ACCOUNT_FOUND = "No account found";
    public static final String INVOICE_DOES_NOT_EXIST = "Invoice does not exist.";
    public static final String INVALID_UPL = "Invalid UPL";
    public static final String MERCHANT = "merchant";
    public static final String FETCH_CLIENT_DETAILS_USE_CASE = "Fetch Client Details UseCase";
    public static final String ERROR_FETCHING_NOTIFICATIONS = "Error fetching notifications";
}
