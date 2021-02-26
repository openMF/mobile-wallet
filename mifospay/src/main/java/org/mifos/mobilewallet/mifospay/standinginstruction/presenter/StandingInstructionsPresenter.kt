package org.mifos.mobilewallet.mifospay.standinginstruction.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.standinginstruction.GetAllStandingInstructions
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import javax.inject.Inject

/**
 * Created by Devansh on 08/06/2020
 */
class StandingInstructionsPresenter @Inject constructor (val mUseCaseHandler: UseCaseHandler,
                                                         val localRepository: LocalRepository)
    : StandingInstructionContract.StandingInstructionsPresenter {

    lateinit var mSIListView: StandingInstructionContract.SIListView

    @Inject
    lateinit var getAllStandingInstructions: GetAllStandingInstructions

    override fun attachView(baseView: BaseView<*>?) {
        mSIListView = baseView as StandingInstructionContract.SIListView
        mSIListView.setPresenter(this)
    }

    override fun getAllSI() {
        val client = localRepository.clientDetails
        mSIListView.showLoadingView()
        mUseCaseHandler.execute(getAllStandingInstructions,
                GetAllStandingInstructions.RequestValues(client.clientId), object :
                UseCaseCallback<GetAllStandingInstructions.ResponseValue> {

            override fun onSuccess(response: GetAllStandingInstructions.ResponseValue) {
                if (response.standingInstructionsList.isEmpty()) {
                    mSIListView.showStateView(R.drawable.ic_empty_state, R.string.error_oops,
                            R.string.empty_standing_instructions)
                } else {
                    mSIListView.showStandingInstructions(response.standingInstructionsList)
                }
            }

            override fun onError(message: String) {
                mSIListView.showStateView(R.drawable.ic_error_state, R.string.error_oops,
                        R.string.error_fetching_si_list)
            }
        })
    }
}