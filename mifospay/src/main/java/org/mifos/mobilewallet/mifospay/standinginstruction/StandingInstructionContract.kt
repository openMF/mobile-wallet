package org.mifos.mobilewallet.mifospay.standinginstruction

import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

interface StandingInstructionContract {

    interface NewSIPresenter : BasePresenter {

        fun fetchClient(externalId: String)

        fun createNewSI(toClientId: Long, amount: Double, recurrenceInterval: Int,
                        validTill: String)

    }

    interface StandingInstructionsPresenter : BasePresenter {

        fun getAllSI()

    }

    interface StandingInstructorDetailsPresenter : BasePresenter {

        fun fetchStandingInstructionDetails(standingInstructionId: Long)

        fun deleteStandingInstruction(standingInstructionId: Long)

        fun updateStandingInstruction(standingInstruction: StandingInstruction)

    }

    interface NewSIView : BaseView<NewSIPresenter> {

        fun showClientDetails(clientId: Long, name: String, externalId: String)

        fun showSuccess(message: String)

        fun showLoadingView()

        fun showFailureCreatingNewSI(message: String)

        fun showFailureSearchingClient(message: String)

    }

    interface SIListView : BaseView<StandingInstructionsPresenter> {

        fun showLoadingView()

        fun showStandingInstructions(standingInstructionList: List<StandingInstruction>)

        fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int)

    }

    interface SIDetailsView : BaseView<StandingInstructorDetailsPresenter> {

        fun showLoadingView()

        fun showSIDetails(standingInstruction: StandingInstruction)

        fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int)

        fun siDeletedSuccessfully()

        fun updateDeleteFailure()

        fun showToast(message: String)

    }

}