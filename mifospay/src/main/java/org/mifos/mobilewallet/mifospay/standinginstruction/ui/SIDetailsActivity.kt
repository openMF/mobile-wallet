package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnTextChanged
import kotlinx.android.synthetic.main.activity_si_details.*
import kotlinx.android.synthetic.main.placeholder_state.*
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionDetailsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DialogBox
import org.mifos.mobilewallet.mifospay.utils.Utils
import java.util.*
import javax.inject.Inject

class SIDetailsActivity : BaseActivity(), StandingInstructionContract.SIDetailsView {

    // using 0 as default values
    private var standingInstructionId: Long = 0

    private lateinit var standingInstruction: StandingInstruction
    private lateinit var res: Resources

    /**
     * This variable is used to overload fab to both save unsaved changes and to enter into edit
     * details screen. On start, its set to false to allow user to go to edit screen
     */
    private var doSave: Boolean = false

    @Inject
    lateinit var mPresenter: StandingInstructionDetailsPresenter
    private lateinit var mStandingInstructionPresenter:
            StandingInstructionContract.StandingInstructorDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_si_details)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        setToolbarTitle(getString(R.string.details))
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
        res = resources

        intent?.let {
            this.standingInstructionId = it.getLongExtra(Constants.SI_ID, 0)
            mStandingInstructionPresenter.fetchStandingInstructionDetails(standingInstructionId)
        }
        setUpUI()
    }

    private fun setUpUI() {
        tv_edit_pick.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)
            val picker = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        tv_valid_till.text = "$dayOfMonth-${(monthOfYear + 1)}-$year"
                    }, year, month, day)
            picker.show()
        }

        fab.setOnClickListener {
            fabOnClickAction()
        }
    }

    override fun setPresenter(presenter:
                              StandingInstructionContract.StandingInstructorDetailsPresenter) {
        this.mStandingInstructionPresenter = presenter
    }

    override fun showLoadingView() {
        layout_si_details.visibility = View.GONE
        inc_state_view.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    /**
     * This function is used to both save unsaved details and to allow user to enter edit screen.
     */
    private fun fabOnClickAction() {
        if (doSave) {
            if (checkInput()) {
                Utils.hideSoftKeyboard(this)

                this.standingInstruction.amount = et_si_edit_amount.text.toString().toDouble()
                this.standingInstruction.recurrenceInterval =
                        et_si_edit_interval.text.toString().toInt()
                val validTillArray = tv_valid_till.text.split("-")
                this.standingInstruction.validTill = validTillArray.map { it.toInt() }

                // passing the updated standing instruction
                mStandingInstructionPresenter.updateStandingInstruction(this.standingInstruction)
            }
        } else {
            doSave = true
            fab.hide()
            fab.setImageDrawable(res.getDrawable(R.drawable.ic_save))

            tv_si_amount.visibility = View.GONE
            til_si_edit_amount.visibility = View.VISIBLE

            tv_edit_pick.visibility = View.VISIBLE

            tv_recurrence_interval.visibility = View.GONE
            til_si_edit_interval.visibility = View.VISIBLE
        }
    }

    /**
     * Checks the input from the user and returns false if the input is invalid otherwise true
     */
    private fun checkInput(): Boolean {
        if (et_si_edit_amount.text.toString() == "") {
            showToast(getString(R.string.enter_amount))
            return false
        } else if (et_si_edit_amount.text.toString().toDouble() <= 0) {
            showToast(getString(R.string.enter_valid_amount))
            return false
        }
        if (et_si_edit_interval.text.toString() == "") {
            showToast(getString(R.string.enter_recurrence_interval))
            return false
        } else if (et_si_edit_interval.text.toString().toInt() <= 1) {
            showToast(getString(R.string.invalid_recurrence_interval))
            return false
        }
        return true
    }

    override fun showSIDetails(standingInstruction: StandingInstruction) {
        this.standingInstruction = standingInstruction
        hideProgressBar()
        layout_si_details.visibility = View.VISIBLE

        // setting up the layout
        tv_si_name.text = standingInstruction.name
        tv_si_id.text = standingInstruction.id.toString()
        /**
         * Using hardcoded Currency as response doesn't return the currency
         */
        tv_si_amount.text = res.getString(R.string.currency_amount, Constants.RUPEE,
                standingInstruction.amount.toString())

        tv_valid_from.text = res.getString(R.string.date_formatted,
                standingInstruction.validFrom[2].toString(),
                standingInstruction.validFrom[1].toString(),
                standingInstruction.validFrom[0].toString())
        tv_valid_till.text = res.getString(R.string.date_formatted,
                standingInstruction.validTill?.get(2).toString(),
                standingInstruction.validTill?.get(1).toString(),
                standingInstruction.validTill?.get(0).toString())

        tv_si_from_name.text = res.getString(R.string.name_client_name,
                standingInstruction.fromClient.displayName)
        tv_si_from_number.text = res.getString(R.string.number_account_number,
                standingInstruction.fromAccount.accountNo)
        tv_si_to_name.text = res.getString(R.string.name_client_name,
                standingInstruction.toClient.displayName)
        tv_si_to_number.text = res.getString(R.string.number_account_number,
                standingInstruction.toAccount.accountNo)

        tv_si_status.text = standingInstruction.status.value
        tv_recurrence_interval.text = standingInstruction.recurrenceInterval.toString()

        // setting up TextInputLayouts
        /**
         * Using hardcoded Currency as response doesn't return the currency
         */
        til_si_edit_amount.hint = "${Constants.RUPEE} ${standingInstruction.amount}"
        til_si_edit_interval.hint = standingInstruction.recurrenceInterval.toString()
        et_si_edit_amount.setText(standingInstruction.amount.toString())
        et_si_edit_interval.setText(standingInstruction.recurrenceInterval.toString())
    }

    @OnTextChanged(R.id.et_si_edit_amount, R.id.et_si_edit_interval, R.id.tv_valid_till)
    fun onDetailsChanged() {
        if (isDataSaveNecessary()) {
            fab.show()
        } else if (!doSave) {
            // for initial state
            fab.show()
        } else {
            fab.hide()
        }
    }

    private fun isDataSaveNecessary(): Boolean {
        val originalValidTillDate = "${standingInstruction.validTill?.get(2)}-" +
                "${standingInstruction.validTill?.get(1)}-${standingInstruction.validTill?.get(0)}"

        return !((this.standingInstruction.amount.toString() == et_si_edit_amount.text.toString())
                && (this.standingInstruction.recurrenceInterval.toString()
                == et_si_edit_interval.text.toString())
                && (originalValidTillDate == tv_valid_till.text.toString()))
    }

    override fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int) {
        hideProgressBar()
        layout_si_details.visibility = View.GONE
        inc_state_view.visibility = View.VISIBLE

        iv_empty_no_transaction_history
                    .setImageDrawable(res.getDrawable(drawable))
        tv_empty_no_transaction_history_title.text = res.getString(errorTitle)
        tv_empty_no_transaction_history_subtitle.text = res.getString(errorMessage)
    }

    override fun siDeletedSuccessfully() {
        showToast(getString(R.string.deleted_successfully))
        finish()
    }

    override fun updateDeleteFailure() {
        hideProgressBar()
        layout_si_details.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_si_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete ->
                /**
                 * perform delete action only when details have been successfully fetched
                 */
                if (this.standingInstructionId != 0L) {
                    mStandingInstructionPresenter.deleteStandingInstruction(
                            this.standingInstructionId)
                }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (isDataSaveNecessary()) {
            showDiscardChangesDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showDiscardChangesDialog() {
        val dialogBox = DialogBox()
        dialogBox.setOnPositiveListener { dialog, which ->
            dialog.dismiss()
            finish()
        }
        dialogBox.setOnNegativeListener { dialog, which ->
            dialog.dismiss()
        }
        dialogBox.show(this, R.string.discard_changes_and_exit,
                R.string.discard_and_exit, R.string.accept, R.string.cancel)
    }
}