package org.mifos.mobilewallet.mifospay.faq.ui;

import android.os.Bundle;
import android.widget.ExpandableListView;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.faq.FAQContract;
import org.mifos.mobilewallet.mifospay.faq.FAQListAdapter;
import org.mifos.mobilewallet.mifospay.faq.presenter.FAQPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is the UI component of the Architecture.
 *
 * @author ankur
 * @since 11/July/2018
 */

public class FAQActivity extends BaseActivity implements FAQContract.FAQView {

    @Inject
    FAQPresenter mPresenter;
    FAQContract.FAQPresenter mFAQPresenter;
    private ExpandableListView expandableListView;
    private FAQListAdapter faqListAdapter;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setToolbarTitle(Constants.FAQ);

        // initializing the views
        initViews();

        // initializing the objects
        initObjects();

        // preparing list data
        initListData();
    }

    @Override
    public void setPresenter(FAQContract.FAQPresenter presenter) {
        mFAQPresenter = presenter;
    }

    /**
     * Method to initialize the views
     */
    public void initViews() {

        expandableListView = findViewById(R.id.faq_list);

    }

    /**
     * Method to initialize the objects
     */
    public void initObjects() {

        // initializing the list of groups
        listDataGroup = new ArrayList<>();

        // initializing the list of child
        listDataChild = new HashMap<>();

        // initializing the adapter object
        faqListAdapter = new FAQListAdapter(this, listDataGroup, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(faqListAdapter);

    }

    /**
     * Preparing the list data
     * Dummy Items
     */
    public void initListData() {


        // Adding group data
        listDataGroup.add(getString(R.string.question1));
        listDataGroup.add(getString(R.string.question2));
        listDataGroup.add(getString(R.string.question3));
        listDataGroup.add(getString(R.string.question4));

        // list of alcohol
        List<String> question1List = new ArrayList<>();
        question1List.add(getString(R.string.answer1));

        // list of coffee
        List<String> question2List = new ArrayList<>();
        question2List.add(getString(R.string.answer2));

        // list of pasta
        List<String> question3List = new ArrayList<>();
        question3List.add(getString(R.string.answer3));


        // list of cold drinks
        List<String> question4List = new ArrayList<>();
        question4List.add(getString(R.string.answer4));


        // Adding child data
        listDataChild.put(listDataGroup.get(0), question1List);
        listDataChild.put(listDataGroup.get(1), question2List);
        listDataChild.put(listDataGroup.get(2), question3List);
        listDataChild.put(listDataGroup.get(3), question4List);

        // notify the adapter
        faqListAdapter.notifyDataSetChanged();
    }

}
