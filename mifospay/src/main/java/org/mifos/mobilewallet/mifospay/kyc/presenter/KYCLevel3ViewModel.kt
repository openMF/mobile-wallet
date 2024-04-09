package org.mifos.mobilewallet.mifospay.kyc.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class KYCLevel3ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository
) : ViewModel() {

}