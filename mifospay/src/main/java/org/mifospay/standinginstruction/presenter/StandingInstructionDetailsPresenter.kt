package org.mifospay.standinginstruction.presenter

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.standinginstruction.FetchStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.DeleteStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.UpdateStandingInstruction
import org.mifospay.R
import org.mifospay.base.BaseView
import org.mifospay.standinginstruction.StandingInstructionContract
import javax.inject.Inject

/**
 * Created by Devansh on 09/06/2020
 */
class StandingInstructionDetailsPresenter @Inject constructor(
    val mUseCaseHandler: UseCaseHandler
) : StandingInstructionContract.StandingInstructorDetailsPresenter {

    @Inject
    lateinit var fetchStandingInstruction: FetchStandingInstruction

    @Inject
    lateinit var updateStandingInstruction: UpdateStandingInstruction

    @Inject
    lateinit var deleteStandingInstruction: DeleteStandingInstruction

    lateinit var mSIDetailsView: StandingInstructionContract.SIDetailsView

    override fun attachView(baseView: BaseView<*>?) {
        mSIDetailsView = baseView as StandingInstructionContract.SIDetailsView
        mSIDetailsView.setPresenter(this)
    }

    override fun fetchStandingInstructionDetails(standingInstructionId: Long) {
        mSIDetailsView.showLoadingView()
        mUseCaseHandler.execute(fetchStandingInstruction,
            FetchStandingInstruction.RequestValues(standingInstructionId), object :
                UseCase.UseCaseCallback<FetchStandingInstruction.ResponseValue> {

                override fun onSuccess(response: FetchStandingInstruction.ResponseValue) {
                    mSIDetailsView.showSIDetails(response.standingInstruction)
                }

                override fun onError(message: String) {
                    mSIDetailsView.showStateView(
                        R.drawable.ic_error_state, R.string.error_oops,
                        R.string.error_fetching_si_details
                    )
                }
            })
    }

    override fun deleteStandingInstruction(standingInstructionId: Long) {
        mSIDetailsView.showLoadingView()
        mUseCaseHandler.execute(deleteStandingInstruction,
            DeleteStandingInstruction.RequestValues(standingInstructionId), object :
                UseCase.UseCaseCallback<DeleteStandingInstruction.ResponseValue> {

                override fun onSuccess(response: DeleteStandingInstruction.ResponseValue) {
                    mSIDetailsView.siDeletedSuccessfully()
                }

                override fun onError(message: String) {
                    mSIDetailsView.updateDeleteFailure()
                    mSIDetailsView.showToast("Error occurred, cannot delete")
                }
            })
    }

    override fun updateStandingInstruction(standingInstruction: StandingInstruction) {
        mSIDetailsView.showLoadingView()
        mUseCaseHandler.execute(updateStandingInstruction,
            UpdateStandingInstruction.RequestValues(standingInstruction.id, standingInstruction),
            object : UseCase.UseCaseCallback<UpdateStandingInstruction.ResponseValue> {

                override fun onSuccess(response: UpdateStandingInstruction.ResponseValue) {
                    mSIDetailsView.showSIDetails(standingInstruction)
                }

                override fun onError(message: String) {
                    mSIDetailsView.updateDeleteFailure()
                    mSIDetailsView.showToast("Error occurred, cannot update")
                }
            })
    }
}