package org.mifos.mobilewallet.mifospay.bank.link_bank

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import org.mifos.mobilewallet.mifospay.domain.model.BankType

class LinkBankUiStatePreviewParameterProvider : PreviewParameterProvider<BankUiState> {

    val banks = ArrayList<Bank>().apply {
        add(Bank("RBL Bank", R.drawable.logo_rbl, BankType.POPULAR))
        add(Bank("SBI Bank", R.drawable.logo_sbi, BankType.POPULAR))
        add(Bank("PNB Bank", R.drawable.logo_pnb, BankType.POPULAR))
        add(Bank("HDFC Bank", R.drawable.logo_hdfc, BankType.POPULAR))
        add(Bank("ICICI Bank", R.drawable.logo_icici, BankType.POPULAR))
        add(Bank("AXIS Bank", R.drawable.logo_axis, BankType.POPULAR))
        add(Bank("HDFC Bank", R.drawable.ic_bank, BankType.OTHER))
        add(Bank("ICICI Bank", R.drawable.ic_bank, BankType.OTHER))
        add(Bank("AXIS Bank", R.drawable.ic_bank, BankType.OTHER))
    }

    override val values: Sequence<BankUiState> = sequenceOf(
        BankUiState.Success(banks = banks)
    )
}