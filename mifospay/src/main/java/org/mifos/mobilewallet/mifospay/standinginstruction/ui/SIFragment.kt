package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_si.*
import kotlinx.android.synthetic.main.placeholder_state.*
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.adapter.StandingInstructionAdapter
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject


class SIFragment : BaseFragment(), StandingInstructionContract.SIListView {
    val newSIActivityRequestCode = 100

    @Inject
    lateinit var mPresenter: StandingInstructionsPresenter
    private lateinit var mStandingInstructionPresenter:
            StandingInstructionContract.StandingInstructionsPresenter

    lateinit var mSIAdapter: StandingInstructionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).activityComponent.inject(this)
        mSIAdapter = StandingInstructionAdapter(onClick = { position ->
            val intent = Intent(activity, SIDetailsActivity::class.java)
            val standingInstructionId =
                mSIAdapter.getStandingInstruction(position)?.id
            standingInstructionId?. let {
                intent.putExtra(Constants.SI_ID, standingInstructionId)
                startActivity(intent)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_si, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.attachView(this)
        mStandingInstructionPresenter.getAllSI()
        setUpUI()
    }

    override fun setPresenter(presenter:
                              StandingInstructionContract.StandingInstructionsPresenter) {
        this.mStandingInstructionPresenter = presenter
    }

    private fun setUpUI() {
        fab_new_si.setOnClickListener {
            val i = Intent(activity, NewSIActivity::class.java)
            startActivityForResult(i, newSIActivityRequestCode)
        }
        setUpRecyclerView()
        setupSwipeRefreshLayout()
    }

    private fun setUpRecyclerView() {
        rv_si.layoutManager = LinearLayoutManager(context)
        rv_si.adapter = mSIAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newSIActivityRequestCode && resultCode == RESULT_OK) {
            swipeRefreshLayout.isRefreshing = false
            mStandingInstructionPresenter.getAllSI()
        }
    }

    private fun setupSwipeRefreshLayout() {
        setSwipeEnabled(true)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            mStandingInstructionPresenter.getAllSI()
        }
    }

    override fun showLoadingView() {
        inc_state_view.visibility = View.GONE
        rv_si.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun showStandingInstructions(standingInstructionList: List<StandingInstruction>) {
        if (activity != null) {
            progressBar.visibility = View.GONE
            rv_si.visibility = View.VISIBLE
            mSIAdapter.setData(standingInstructionList)
        }
    }

    override fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int) {
        if (activity != null) {
            progressBar.visibility = View.GONE
            rv_si.visibility = View.GONE
            inc_state_view.visibility = View.VISIBLE

            // setting up state view elements
            val res = resources
            iv_empty_no_transaction_history
                    .setImageDrawable(ResourcesCompat.getDrawable(res, drawable,requireActivity().theme))
            tv_empty_no_transaction_history_title.text = res.getString(errorTitle)
            tv_empty_no_transaction_history_subtitle.text = res.getString(errorMessage)
        }
    }
}