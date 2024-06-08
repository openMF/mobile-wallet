import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifos.mobilewallet.mifospay.seup_upi.fragment.DebitCardScreen
import org.mifos.mobilewallet.mifospay.seup_upi.fragment.OtpScreen
import org.mifos.mobilewallet.mifospay.seup_upi.fragment.UpiPinScreen
import org.mifospay.bank.setupUpi.viewmodel.SetUpUpiViewModal
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.common.Constants
import org.mifospay.utils.Toaster.showToast

@Composable
fun SetUpUpiScreenContent(
    viewModal: SetUpUpiViewModal = hiltViewModel(),
    bankAccountDetails: BankAccountDetails,
    type: String,
    index: Int,
    correctlySetingUpi: (String) -> Unit
) {

    Column {
        if (type == Constants.CHANGE) {
            ChangeUpi(
                viewModal.requestOtp(bankAccountDetails), correctlySetingUpi
            )
        } else {
            SettingAndForgotUpi(
                correctlySetingUpi
            )
        }
    }
}

@Composable
fun SettingAndForgotUpi(
    correctlySetingUpi: (String) -> Unit
) {
    var debitCardVerified by rememberSaveable { mutableStateOf(false) }
    var otpVerified by rememberSaveable { mutableStateOf(false) }
    var debitCardScreenVisible by rememberSaveable { mutableStateOf(true) }
    var otpScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVerified by rememberSaveable { mutableStateOf(false) }
    var realOtp by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    DebitCardScreen(verificationStatus = debitCardVerified,
        isContentVisible = debitCardScreenVisible,
        onDebitCardVerified = {
            debitCardVerified = true
            otpScreenVisible = true
            realOtp = it
            debitCardScreenVisible = false
        },
        onDebitCardVerificationFailed = {
            showToast(context, it)
        })
    OtpScreen(verificationStatus = otpVerified,
        contentVisibility = otpScreenVisible,
        realOtp = realOtp,
        onOtpTextCorrectlyEntered = {
            otpScreenVisible = false
            otpVerified = true
            upiPinScreenVisible = true
        })
    UpiPinScreen(contentVisibility = upiPinScreenVisible, correctlySettingUpi = {
        upiPinScreenVerified = true
        correctlySetingUpi(it)
    })
}

@Composable
fun ChangeUpi(
    otpText: String, correctlySetingUpi: (String) -> Unit
) {
    var otpVerified by rememberSaveable { mutableStateOf(false) }
    var otpScreenVisible by rememberSaveable { mutableStateOf(true) }
    var upiPinScreenVisible by rememberSaveable { mutableStateOf(false) }
    var upiPinScreenVerified by rememberSaveable { mutableStateOf(false) }
    var realOtp by rememberSaveable { mutableStateOf(otpText) }

    OtpScreen(verificationStatus = otpVerified,
        contentVisibility = otpScreenVisible,
        realOtp = realOtp,
        onOtpTextCorrectlyEntered = {
            otpScreenVisible = false
            Log.i("OtpScreen", "$otpScreenVisible")
            otpVerified = true
            upiPinScreenVisible = true
            otpScreenVisible = false
        })
    UpiPinScreen(contentVisibility = upiPinScreenVisible, correctlySettingUpi = {
        upiPinScreenVerified = true
        correctlySetingUpi(it)
    })
}


@Preview
@Composable
fun PreviewSetUpUpiPin() {
    MaterialTheme {
        SetUpUpiScreenContent(bankAccountDetails = BankAccountDetails(),
            type = Constants.SETUP,
            index = 0,
            correctlySetingUpi = {})
    }
}

@Preview
@Composable
fun PreviewForgetUpiPin() {
    MaterialTheme {
        SetUpUpiScreenContent(bankAccountDetails = BankAccountDetails(),
            type = Constants.FORGOT,
            index = 0,
            correctlySetingUpi = {})
    }
}

@Preview
@Composable
fun PreviewChangeUpiPin() {
    MaterialTheme {
        SetUpUpiScreenContent(bankAccountDetails = BankAccountDetails(),
            type = Constants.CHANGE,
            index = 0,
            correctlySetingUpi = {})
    }
}