package org.mifos.mobilewallet.mifospay.kyc.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCLevel1View
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel1Presenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCLevel1Fragment : BaseFragment(), KYCLevel1View {
    @JvmField
    @Inject
    var mPresenter: KYCLevel1Presenter? = null
    var mKYCLevel1Presenter: KYCContract.KYCLevel1Presenter? = null

    @JvmField
    @BindView(R.id.et_fname)
    var etFname: EditText? = null

    @JvmField
    @BindView(R.id.et_lname)
    var etLname: EditText? = null

    @JvmField
    @BindView(R.id.et_address1)
    var etAddress1: EditText? = null

    @JvmField
    @BindView(R.id.et_address2)
    var etAddress2: EditText? = null

    @JvmField
    @BindView(R.id.ccp_code)
    var ccpPhonecode: CountryCodePicker? = null

    @JvmField
    @BindView(R.id.et_mobile_number)
    var etMobileNumber: EditText? = null

    @JvmField
    @BindView(R.id.et_dob)
    var etDOB: EditText? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnSubmit: Button? = null
    var date: OnDateSetListener? = null
    private lateinit var myCalendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_kyc_lvl1, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        //setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_1);
        myCalendar = Calendar.getInstance()
        date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = Constants.DD_MM_YY
            val sdf = SimpleDateFormat(myFormat)
            etDOB!!.setText(sdf.format(myCalendar.getTime()))
        }
        ccpPhonecode!!.registerCarrierNumberEditText(etMobileNumber)
        ccpPhonecode!!.setCustomMasterCountries(null)
        return rootView
    }

    @OnClick(R.id.et_dob)
    fun onClickDOB() {
        myCalendar
            ?.get(Calendar.YEAR)?.let {
                DatePickerDialog(
                    requireContext(), date, it, myCalendar!![Calendar.MONTH],
                    myCalendar!![Calendar.DAY_OF_MONTH]
                ).show()
            }
    }

    @OnClick(R.id.btn_submit)
    fun onClickSubmit() {
        showProgressDialog(Constants.PLEASE_WAIT)
        val fname = etFname!!.text.toString().trim { it <= ' ' }
        val lname = etLname!!.text.toString().trim { it <= ' ' }
        val address1 = etAddress1!!.text.toString().trim { it <= ' ' }
        val address2 = etAddress2!!.text.toString().trim { it <= ' ' }
        val phoneno = ccpPhonecode!!.fullNumber
        val dob = etDOB!!.text.toString().trim { it <= ' ' }
        mKYCLevel1Presenter!!.submitData(fname, lname, address1, address2, phoneno, dob)
        hideSoftKeyboard(requireActivity())
    }

    override fun showToast(s: String?) {
        Toaster.show(view, s)
    }

    public override fun showProgressDialog(message: String?) {
        super.showProgressDialog(message)
    }

    override fun hideProgressDialog() {
        super.hideProgressDialog()
    }

    override fun goBack() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

    override fun setPresenter(presenter: KYCContract.KYCLevel1Presenter?) {
        mKYCLevel1Presenter = presenter
    }

    companion object {
        @JvmStatic
        fun newInstance(): KYCLevel1Fragment {
            val args = Bundle()
            val fragment = KYCLevel1Fragment()
            fragment.arguments = args
            return fragment
        }
    }
}