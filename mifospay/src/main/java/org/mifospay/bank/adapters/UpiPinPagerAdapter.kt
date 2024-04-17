package org.mifospay.bank.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by ankur on 13/July/2018
 */
class UpiPinPagerAdapter(fm: FragmentManager?, private var mFragments: Array<Fragment?>) :
    FragmentPagerAdapter(
        fm!!
    ) {
    private val noOfFragments: Int = mFragments.size

    override fun getItem(position: Int): Fragment {
        return mFragments[position]!!
    }

    override fun getCount(): Int {
        return noOfFragments
    }
}