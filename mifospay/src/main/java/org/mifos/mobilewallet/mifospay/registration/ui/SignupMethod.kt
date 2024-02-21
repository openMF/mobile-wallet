package org.mifos.mobilewallet.mifospay.registration.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.mifos.mobilewallet.core.utils.Constants
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity

/**
 * Created by ankur on 26/June/2018
 */
class SignupMethod : BottomSheetDialogFragment() {
    @JvmField
    @BindView(R.id.cb_google_account)
    var mCbGoogleAccount: CheckBox? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val googleSignInClient: GoogleSignInClient? = null
    private val account: GoogleSignInAccount? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.dialog_choose_signup_method, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        ButterKnife.bind(this, view)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @OnClick(R.id.btn_merchant)
    fun onMerchantClicked() {
        dismiss()
        /*if (activity is LoginActivity) {
            if (mCbGoogleAccount!!.isChecked) {
                (activity as LoginActivity?)!!.signupUsingGoogleAccount(
                    Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                )
            } else {
                (activity as LoginActivity?)!!.signup(Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID)
            }
        }*/
    }

    @OnClick(R.id.btn_customer)
    fun onCustomerClicked() {
        dismiss()
        /*if (activity is LoginActivity) {
            if (mCbGoogleAccount!!.isChecked) {
                (activity as LoginActivity?)!!.signupUsingGoogleAccount(
                    Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID
                )
            } else {
                (activity as LoginActivity?)!!.signup(Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID)
            }
        }*/
    }
}