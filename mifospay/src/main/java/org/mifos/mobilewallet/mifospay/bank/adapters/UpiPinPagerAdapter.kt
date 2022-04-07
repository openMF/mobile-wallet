package org.mifos.mobilewallet.mifospay.bank.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by ankur on 13/July/2018
 */
class UpiPinPagerAdapter(fm: FragmentManager?, fragments: Array<Fragment?>) :
    FragmentPagerAdapter(fm) {
    private val noOfFragments: Int = fragments.size
    private var mFragments: Array<Fragment?>
    fun setFragments(fragments: Array<Fragment?>) {
        mFragments = fragments
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment? {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return noOfFragments
    }

    init {
        mFragments = fragments
    }
}