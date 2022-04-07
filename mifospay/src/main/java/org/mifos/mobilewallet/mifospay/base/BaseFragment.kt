package org.mifos.mobilewallet.mifospay.base

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout

/**
 * Created by naman on 17/6/17.
 */
open class BaseFragment : Fragment() {
    private var callback: BaseActivityCallback? = null

    protected fun setToolbarTitle(title: String?) {
        if (callback != null) {
            callback?.setToolbarTitle(title)
        }
    }

    protected fun showBackButton(drawable: Int) {
        if (callback != null) {
            callback?.showColoredBackButton(drawable)
        }
    }

    protected fun hideBackButton() {
        if (callback != null) {
            callback?.hideBackButton()
        }
    }

    protected fun showSwipeProgress() {
        if (callback != null) {
            callback?.showSwipeProgress()
        }
    }

    open fun hideSwipeProgress() {
        if (callback != null) {
            callback?.hideSwipeProgress()
        }
    }

    protected open fun showProgressDialog(message: String?) {
        callback?.showProgressDialog(message)
    }

    protected open fun hideProgressDialog() {
        callback?.hideProgressDialog()
    }

    protected fun setSwipeEnabled(enabled: Boolean) {
        if (callback != null) {
            callback?.setSwipeRefreshEnabled(enabled)
        }
    }

    protected val swipeRefreshLayout: SwipeRefreshLayout?
        get() = if (callback != null) {
            callback?.swipeRefreshLayout
        } else null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = if (context is Activity) context else null
        callback = try {
            activity as BaseActivityCallback?
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement BaseActivityCallback methods"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    fun replaceFragmentUsingFragmentManager(
        fragment: Fragment, addToBackStack: Boolean,
        containerId: Int
    ) {
        val backStateName = fragment.javaClass.name
        val fragmentPopped = fragmentManager?.popBackStackImmediate(
            backStateName,
            0
        )
        if (fragmentPopped == false && fragmentManager?.findFragmentByTag(backStateName) ==
            null
        ) {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(containerId, fragment, backStateName)
            if (addToBackStack) {
                transaction?.addToBackStack(backStateName)
            }
            transaction?.commit()
        }
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean, containerId: Int) {
        val backStateName = fragment.javaClass.name
        val fragmentPopped = childFragmentManager.popBackStackImmediate(
            backStateName,
            0
        )
        if (!fragmentPopped && childFragmentManager.findFragmentByTag(backStateName) ==
            null
        ) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(containerId, fragment, backStateName)
            if (addToBackStack) {
                transaction.addToBackStack(backStateName)
            }
            transaction.commit()
        }
    }
}