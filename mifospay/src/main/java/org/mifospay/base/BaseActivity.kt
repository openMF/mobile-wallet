package org.mifospay.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mifos.mobile.passcode.BasePassCodeActivity
import org.mifospay.R
import org.mifospay.feature.passcode.PassCodeActivity

/**
 * Created by naman on 16/6/17.
 */
open class BaseActivity : BasePassCodeActivity(), BaseActivityCallback {
    var toolbar: Toolbar? = null
    override var swipeRefreshLayout: SwipeRefreshLayout? = null
    var progressDialog: ProgressDialog? = null
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipe_layout)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    override fun showSwipeProgress() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.isEnabled = true
            swipeRefreshLayout!!.isRefreshing = true
        }
    }

    override fun hideSwipeProgress() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    override fun setSwipeRefreshEnabled(enabled: Boolean) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.isEnabled = enabled
        }
    }

    override fun showProgressDialog(message: String?) {
        if (progressDialog != null) {
            progressDialog!!.setMessage(message)
            progressDialog!!.show()
        } else {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.setMessage(message)
            progressDialog!!.show()
        }
    }

    override fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    override fun cancelProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

    override fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
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
        setToolbarIcon(R.drawable.feature_make_transfer_ic_close)
    }

    private fun showHomeButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setToolbarIcon(res: Int) {
        if (supportActionBar != null) {
            supportActionBar!!.setHomeAsUpIndicator(res)
        }
    }

    override fun hideBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
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
        transaction.add(containerId, fragment!!)
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