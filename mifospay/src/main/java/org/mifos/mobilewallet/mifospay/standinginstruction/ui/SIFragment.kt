package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentSiBinding
import org.mifos.mobilewallet.mifospay.databinding.PlaceholderStateBinding
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.adapter.StandingInstructionAdapter
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class SIFragment : BaseFragment(), StandingInstructionContract.SIListView {
    val newSIActivityRequestCode = 100

    @Inject
    lateinit var mPresenter: StandingInstructionsPresenter
    private lateinit var mStandingInstructionPresenter:
            StandingInstructionContract.StandingInstructionsPresenter

    lateinit var mSIAdapter: StandingInstructionAdapter

    lateinit var binding: FragmentSiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSIAdapter = StandingInstructionAdapter(activity as BaseActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_si,container,false)
        return binding.root
    }

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
        binding.fabNewSi.setOnClickListener {
            val i = Intent(activity, NewSIActivity::class.java)
            startActivityForResult(i, newSIActivityRequestCode)
        }
        setUpRecyclerView()
        setupSwipeRefreshLayout()
    }

    private fun setUpRecyclerView() {
        binding.rvSi.layoutManager =
            LinearLayoutManager(context)
        binding.rvSi.adapter = mSIAdapter
        binding.rvSi.addOnItemTouchListener(RecyclerItemClickListener(context,
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
        binding.incStateView.llPlaceHolderState.visibility = View.GONE
        binding.rvSi.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun showStandingInstructions(standingInstructionList: List<StandingInstruction>) {
        if (activity != null) {
            binding.progressBar.visibility = View.GONE
            binding.rvSi.visibility = View.VISIBLE
            mSIAdapter.setData(standingInstructionList)
        }
    }

    override fun showStateView(drawable: Int, errorTitle: Int, errorMessage: Int) {
        if (activity != null) {
            binding.progressBar.visibility = View.GONE
            binding.rvSi.visibility = View.GONE
            binding.incStateView.llPlaceHolderState.visibility = View.VISIBLE
            // setting up state view elements
            val res = resources
            binding.incStateView
            binding.incStateView.ivEmptyNoTransactionHistory.setImageDrawable(res.getDrawable(drawable))
            binding.incStateView.tvEmptyNoTransactionHistoryTitle.text = res.getString(errorTitle)
            binding.incStateView.tvEmptyNoTransactionHistorySubtitle.text = res.getString(errorMessage)
        }
    }
}