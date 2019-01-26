package org.mifos.mobilewallet.mifospay.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * TabLayoutAdapter adapter for fragments loaded in a ViewPager
 * @author anonymous
 * @since unknown
 */
public class TabLayoutAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * Constructor for MainPresenter to initialize global fields
     * @param fragmentManager An instance of FragmentManager.
     */
    public TabLayoutAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * Used to add Fragments to the List.
     * @param fragment Fragment to be added
     * @param title Title of fragment to be added
     */
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    /**
     * Returns Fragment at i'th position
     * @param i Position
     */
    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    /**
     * Returns Size of the FragmentList.
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * Returns title of the Fragment at specified position.
     * @param position Position of the Fragment whose title is required
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
