package org.mifos.mobilewallet.mifospay.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.mifos.mobile.passcode.BasePassCodeActivity
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.injection.component.ActivityComponent
import org.mifos.mobilewallet.mifospay.injection.component.DaggerActivityComponent
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity

/**
 * Created by naman on 16/6/17.
 */
open class BaseActivity : BasePassCodeActivity(), BaseActivityCallback {
    var toolbar: Toolbar? = null
    override var swipeRefreshLayout: SwipeRefreshLayout? = null
    var progressDialog: ProgressDialog? = null
    var activityComponent: ActivityComponent? = null
        get() {
            if (field == null) {
                field = DaggerActivityComponent.builder()
                    .activityModule(ActivityModule(this))
                    .applicationComponent(MifosPayApp.get(this).component())
                    .build()
            }
            return field
        }
        private set

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipe_layout)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    override fun showSwipeProgress() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout?.isEnabled = true
            swipeRefreshLayout?.isRefreshing = true
        }
    }

    override fun hideSwipeProgress() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout?.isRefreshing = false
        }
    }

    override fun setSwipeRefreshEnabled(enabled: Boolean) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout?.isEnabled = enabled
        }
    }

    override fun showProgressDialog(message: String?) {
        if (progressDialog != null) {
            progressDialog?.setMessage(message)
            progressDialog?.show()
        } else {
            progressDialog = ProgressDialog(this)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setMessage(message)
            progressDialog?.show()
        }
    }

    override fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.hide()
        }
    }

    override fun cancelProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.cancel()
        }
    }

    override fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    override fun setToolbarTitle(title: String?) {
        if (supportActionBar != null && getTitle() != null) {
            setTitle(title)
        }
    }

    override fun showColoredBackButton(drawable: Int) {
        showHomeButton()
        setToolbarIcon(drawable)
    }

    override fun showCloseButton() {
        showHomeButton()
        setToolbarIcon(R.drawable.ic_close)
    }

    private fun showHomeButton() {
        if (supportActionBar != null) {
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setToolbarIcon(res: Int) {
        if (supportActionBar != null) {
            supportActionBar?.setHomeAsUpIndicator(res)
        }
    }

    override fun hideBackButton() {
        if (supportActionBar != null) {
            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun addFragment(fragment: Fragment?, containerId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment?.let { transaction.add(containerId, it) }
        transaction.commit()
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean, containerId: Int) {
        invalidateOptionsMenu()
        val backStateName = fragment.javaClass.name
        val fragmentPopped = supportFragmentManager.popBackStackImmediate(
            backStateName,
            0
        )
        if (!fragmentPopped && supportFragmentManager.findFragmentByTag(backStateName) ==
            null
        ) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            transaction.replace(containerId, fragment, backStateName)
            if (addToBackStack) {
                transaction.addToBackStack(backStateName)
            }
            transaction.commit()
        }
    }

    fun clearFragmentBackStack() {
        val fm = supportFragmentManager
        val backStackCount = supportFragmentManager.backStackEntryCount
        for (i in 0 until backStackCount) {
            val backStackId = supportFragmentManager.getBackStackEntryAt(i).id
            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun getPassCodeClass(): Class<*> {
        return PassCodeActivity::class.java
    }
}