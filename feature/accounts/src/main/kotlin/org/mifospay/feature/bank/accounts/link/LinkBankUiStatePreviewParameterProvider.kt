package org.mifospay.feature.bank.accounts.link

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mifospay.core.model.domain.Bank
import com.mifospay.core.model.domain.BankType
import org.mifospay.feature.bank.accounts.R

class LinkBankUiStatePreviewParameterProvider : PreviewParameterProvider<BankUiState> {

    val banks = ArrayList<Bank>().apply {
        add(Bank("RBL Bank", R.drawable.feature_accounts_logo_rbl, BankType.POPULAR))
        add(Bank("SBI Bank", R.drawable.feature_accounts_logo_sbi, BankType.POPULAR))
        add(Bank("PNB Bank", R.drawable.feature_accounts_logo_pnb, BankType.POPULAR))
        add(Bank("HDFC Bank", R.drawable.feature_accounts_logo_hdfc, BankType.POPULAR))
        add(Bank("ICICI Bank", R.drawable.feature_accounts_logo_icici, BankType.POPULAR))
        add(Bank("AXIS Bank", R.drawable.feature_accounts_logo_axis, BankType.POPULAR))
        add(Bank("HDFC Bank", R.drawable.feature_accounts_ic_bank, BankType.OTHER))
        add(Bank("ICICI Bank", R.drawable.feature_accounts_ic_bank, BankType.OTHER))
        add(Bank("AXIS Bank", R.drawable.feature_accounts_ic_bank, BankType.OTHER))
    }

    override val values: Sequence<BankUiState> = sequenceOf(
        BankUiState.Success(banks = banks)
    )
}