package org.mifos.mobilewallet.mifospay.faq.ui

import android.os.Bundle
import android.widget.ExpandableListView
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.faq.FAQContract
import org.mifos.mobilewallet.mifospay.faq.FAQContract.FAQView
import org.mifos.mobilewallet.mifospay.faq.FAQListAdapter
import org.mifos.mobilewallet.mifospay.faq.presenter.FAQPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * This class is the UI component of the Architecture.
 *
 * @author ankur
 * @since 11/July/2018
 */
class FAQActivity : BaseActivity(), FAQView {
    @JvmField
    @Inject
    var mPresenter: FAQPresenter? = null
    private var mFAQPresenter: FAQContract.FAQPresenter? = null
    private var expandableListView: ExpandableListView? = null
    private var faqListAdapter: FAQListAdapter? = null
    private var listDataGroup: MutableList<String>? = null
    private var listDataChild: HashMap<String, List<String>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle(Constants.FAQ)

        // initializing the views
        initViews()

        // initializing the objects
        initObjects()

        // preparing list data
        initListData()
    }

    override fun setPresenter(presenter: FAQContract.FAQPresenter?) {
        mFAQPresenter = presenter
    }

    /**
     * Method to initialize the views
     */
    override fun initViews() {
        expandableListView = findViewById(R.id.faq_list)
    }

    /**
     * Method to initialize the objects
     */
    override fun initObjects() {

        // initializing the list of groups
        listDataGroup = ArrayList()

        // initializing the list of child
        listDataChild = HashMap()

        // initializing the adapter object
        faqListAdapter = FAQListAdapter(this, listDataGroup as ArrayList<String>, listDataChild!!)

        // setting list adapter
        expandableListView?.setAdapter(faqListAdapter)
    }

    /**
     * Preparing the list data
     * Dummy Items
     */
    override fun initListData() {


        // Adding group data
        listDataGroup?.add(getString(R.string.question1))
        listDataGroup?.add(getString(R.string.question2))
        listDataGroup?.add(getString(R.string.question3))
        listDataGroup?.add(getString(R.string.question4))

        // list of alcohol
        val question1List: MutableList<String> = ArrayList()
        question1List.add(getString(R.string.answer1))

        // list of coffee
        val question2List: MutableList<String> = ArrayList()
        question2List.add(getString(R.string.answer2))

        // list of pasta
        val question3List: MutableList<String> = ArrayList()
        question3List.add(getString(R.string.answer3))


        // list of cold drinks
        val question4List: MutableList<String> = ArrayList()
        question4List.add(getString(R.string.answer4))


        // Adding child data
        listDataChild!![listDataGroup!![0]] = question1List
        listDataChild!![listDataGroup!![1]] = question2List
        listDataChild!![listDataGroup!![2]] = question3List
        listDataChild!![listDataGroup!![3]] = question4List

        // notify the adapter
        faqListAdapter?.notifyDataSetChanged()
    }
}