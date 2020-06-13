package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_si.*
import kotlinx.android.synthetic.main.fragment_si.inc_state_view
import kotlinx.android.synthetic.main.fragment_si.progressBar
import kotlinx.android.synthetic.main.placeholder_state.*
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.adapter.StandingInstructionAdapter
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject


class SIFragment : BaseFragment(), StandingInstructionContract.SIListView {

    @Inject
    lateinit var mPresenter: StandingInstructionsPresenter
    private lateinit var mStandingInstructionPresenter:
            StandingInstructionContract.StandingInstructionsPresenter

    lateinit var mSIAdapter: StandingInstructionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).activityComponent.inject(this)
        mSIAdapter = StandingInstructionAdapter(activity as BaseActivity)
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
            startActivity(i)
        }
        setUpRecyclerView()
        setupSwipeRefreshLayout()
    }

    private fun setUpRecyclerView() {
        rv_si.layoutManager = LinearLayoutManager(context)
        rv_si.adapter = mSIAdapter
        rv_si.addOnItemTouchListener(RecyclerItemClickListener(context,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemLongPress(childView: View?, position: Int) {

                    }

                    override fun onItemClick(childView: View, position: Int) {
                        val intent = Intent(activity, SIDetailsActivity::class.java)
                        val standingInstructionId =
                                mSIAdapter.getStandingInstruction(position)?.id
                        standingInstructionId?. let {
                            intent.putExtra(Constants.SI_ID, standingInstructionId)
                            startActivity(intent)
                        }
                    }
                }))
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
        progressBar.visibility = View.GONE
        rv_si.visibility = View.VISIBLE
        mSIAdapter.setData(standingInstructionList)
    }

    override fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int) {
        progressBar.visibility = View.GONE
        rv_si.visibility = View.GONE
        inc_state_view.visibility = View.VISIBLE

        // setting up state view elements
        val res = resources
        iv_empty_no_transaction_history
                .setImageDrawable(res.getDrawable(drawable))
        tv_empty_no_transaction_history_title.text = res.getString(errorTitle)
        tv_empty_no_transaction_history_subtitle.text = res.getString(errorMessage)
    }
}