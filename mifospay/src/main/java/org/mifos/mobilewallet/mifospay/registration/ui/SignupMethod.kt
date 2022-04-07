package org.mifos.mobilewallet.mifospay.registration.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.mifos.mobilewallet.core.utils.Constants
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity
import org.mifos.mobilewallet.mifospay.databinding.DialogChooseSignupMethodBinding

/**
 * Created by ankur on 26/June/2018
 */
class SignupMethod : BottomSheetDialogFragment() {

    private var binding: DialogChooseSignupMethodBinding? = null

    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val googleSignInClient: GoogleSignInClient? = null
    private val account: GoogleSignInAccount? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogChooseSignupMethodBinding
            .inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(binding?.root)

        binding?.btnMerchant?.setOnClickListener {
            onMerchantClicked()
        }
        binding?.btnCustomer?.setOnClickListener {
            onCustomerClicked()
        }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun onMerchantClicked() {
        dismiss()
        if (activity is LoginActivity) {
            if (binding?.cbGoogleAccount?.isChecked == true) {
                (activity as LoginActivity?)?.signupUsingGoogleAccount(
                    Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                )
            } else {
                (activity as LoginActivity?)?.signup(Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID)
            }
        }
    }

    private fun onCustomerClicked() {
        dismiss()
        if (activity is LoginActivity) {
            if (binding?.cbGoogleAccount?.isChecked == true) {
                (activity as LoginActivity?)?.signupUsingGoogleAccount(
                    Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID
                )
            } else {
                (activity as LoginActivity?)?.signup(Constants.MIFOS_CONSUMER_SAVINGS_PRODUCT_ID)
            }
        }
    }
}