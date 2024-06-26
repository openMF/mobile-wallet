package org.mifospay.history.specific_transactions.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import com.mifospay.core.model.domain.Transaction
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.history.HistoryContract
import org.mifospay.history.HistoryContract.SpecificTransactionsView
import org.mifospay.history.specific_transactions.presenter.SpecificTransactionsPresenter
import org.mifospay.history.ui.adapter.SpecificTransactionsAdapter
import org.mifospay.receipt.ui.ReceiptActivity
import org.mifospay.common.Constants
import org.mifospay.history.specific_transactions.presenter.SpecificTransactionsViewModel
import org.mifospay.theme.MifosTheme
import org.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class SpecificTransactionsActivity : BaseActivity() {

    private val viewModel: SpecificTransactionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setArguments(
            transactions = intent.getParcelableArrayListExtra(Constants.TRANSACTIONS)
                ?: arrayListOf(),
            accountNumber = intent.getStringExtra(Constants.ACCOUNT_NUMBER) ?: ""
        )
        setContent {
            MifosTheme {
                SpecificTransactionsScreen(
                    backPress = { onBackPressed() },
                    transactionItemClicked = { transactionId ->
                        navigateToReceiptActivity(transactionId)
                    }
                )
            }
        }
    }

    private fun navigateToReceiptActivity(transactionId: String) {
        val intent = Intent(this@SpecificTransactionsActivity, ReceiptActivity::class.java)
        intent.data = Uri.parse(Constants.RECEIPT_DOMAIN + transactionId)
        startActivity(intent)
    }
}