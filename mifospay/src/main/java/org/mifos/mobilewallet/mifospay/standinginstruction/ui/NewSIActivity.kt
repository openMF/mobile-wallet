package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_new_si.*
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.NewSIPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Utils
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


class NewSIActivity : BaseActivity(), StandingInstructionContract.NewSIView {

    @Inject
    lateinit var mPresenter: NewSIPresenter
    private lateinit var mNewSIPresenter: StandingInstructionContract.NewSIPresenter

    private var clientId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_si)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        setToolbarTitle(getString(R.string.tile_si_activity))
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
    }

    override fun setPresenter(presenter: StandingInstructionContract.NewSIPresenter) {
        this.mNewSIPresenter = presenter
    }

    @OnClick(R.id.btn_valid_till)
    fun pickToDate() {
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH)
        val year: Int = calendar.get(Calendar.YEAR)
        val picker = DatePickerDialog(this,
                OnDateSetListener {
                    view, year, monthOfYear, dayOfMonth ->
                    btn_valid_till.text = "${dayOfMonth.toString()}-${(monthOfYear + 1)}-$year"
                }, year, month, day)
        picker.show()
    }

    @OnClick(R.id.btn_create_si)
    fun createSI() {
        if (et_si_amount.text.toString() == "") {
            showToast(getString(R.string.enter_amount))
            return
        } else if (et_si_amount.text.toString().toInt() <= 0) {
            showToast(getString(R.string.enter_valid_amount))
            return
        }
        if (et_si_vpa.text.toString() == "") {
            showToast(getString(R.string.enter_VPA))
            return
        }
        if (et_si_interval.text.toString() == "") {
            showToast(getString(R.string.enter_recurrence_interval))
            return
        } else if (et_si_interval.text.toString().toInt() <= 1) {
            showToast(getString(R.string.invalid_recurrence_interval))
            return
        }
        if (btn_valid_till.text == Constants.SELECT_DATE) {
            showToast(getString(R.string.select_till_date))
            return
        }
        Utils.hideSoftKeyboard(this)
        mNewSIPresenter.fetchClient(et_si_vpa.text.toString())
    }

    override fun showClientDetails(clientId: Long, name: String, externalId: String) {
        this.clientId = clientId
        tv_client_name.text = name
        tv_client_vpa.text = externalId
        tv_amount.text = resources.getString(R.string.currency_amount,
                Constants.RUPEE, et_si_amount.text)

        ll_create_si.visibility = View.GONE
        ll_confirm_transfer.visibility = View.VISIBLE
        ll_confirm_cancel.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    @OnClick(R.id.btn_confirm)
    fun createNewStandingInstruction() {
        ll_confirm_cancel.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        mNewSIPresenter.createNewSI(clientId, (et_si_amount.text.toString()).toDouble(),
                et_si_interval.text.toString().toInt(), btn_valid_till.text.toString())
    }

    @OnClick(R.id.btn_cancel)
    fun cancelNewStandingInstruction() {
        ll_confirm_transfer.visibility = View.GONE
        ll_create_si.visibility = View.VISIBLE
    }

    override fun showLoadingView() {
        ll_create_si.visibility = View.GONE
        ll_confirm_cancel.visibility = View.GONE
        ll_confirm_transfer.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun showFailureSearchingClient(message: String) {
        progressBar.visibility = View.GONE
        ll_create_si.visibility = View.VISIBLE
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        showToast(message)
    }

    override fun showFailureCreatingNewSI(message: String) {
        ll_confirm_transfer.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        ll_confirm_cancel.visibility = View.VISIBLE
        showToast(message)
    }

}