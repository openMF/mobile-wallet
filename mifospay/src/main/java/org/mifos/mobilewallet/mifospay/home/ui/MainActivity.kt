package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.client.Client
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.faq.ui.FAQActivity
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.BaseHomePresenter
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.BaseHomeView
import org.mifos.mobilewallet.mifospay.home.adapter.TabLayoutAdapter
import org.mifos.mobilewallet.mifospay.home.presenter.MainPresenter
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment.Companion.newInstance
import org.mifos.mobilewallet.mifospay.home.ui.PaymentsFragment.Companion.newInstance
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantsFragment
import org.mifos.mobilewallet.mifospay.settings.ui.SettingsActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity(), BaseHomeView {
    @JvmField
    @Inject
    var mPresenter: MainPresenter? = null

    @JvmField
    @Inject
    var localRepository: LocalRepository? = null

    @JvmField
    @BindView(R.id.bottom_navigation)
    var bottomNavigationView: BottomNavigationView? = null
    private var mHomePresenter: BaseHomePresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        mPresenter?.attachView(this)
        mHomePresenter?.fetchClientDetails()
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            navigateFragment(item.itemId, false)
            true
        }
        bottomNavigationView?.selectedItemId = R.id.action_home
        setToolbarTitle(Constants.HOME)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_faq -> startActivity(Intent(applicationContext, FAQActivity::class.java))
            R.id.item_profile_setting -> startActivity(
                Intent(
                    applicationContext,
                    SettingsActivity::class.java
                )
            )
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun setPresenter(presenter: BaseHomePresenter?) {
        mHomePresenter = presenter
    }

    // TODO: Figure out the purpose of this
//    fun showClientDetails(client: Client?) {
//        tvUserName.setText(client.getName());
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(client.getName().substring(0, 1), R.color.colorPrimary);
//        ivUserImage.setImageDrawable(drawable);
//    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager
            .findFragmentById(R.id.bottom_navigation_fragment_container)
        if (fragment is FinanceFragment && fragment.isVisible()) {
            if (fragment.vpTabLayout?.currentItem?.let {
                    (fragment.vpTabLayout?.adapter as TabLayoutAdapter?)
                        ?.getItem(it)
                } is MerchantsFragment
            ) {
                val merchantsFragment = (fragment.vpTabLayout?.adapter as TabLayoutAdapter?)
                    ?.getItem(fragment.vpTabLayout?.currentItem ?: 0) as MerchantsFragment
                if (merchantsFragment.etMerchantSearch?.text.toString().isNotEmpty()) {
                    merchantsFragment.etMerchantSearch?.setText("")
                    return
                }
            }
        }
        if (fragment != null && fragment !is HomeFragment && fragment.isVisible) {
            if (fragment is ProfileFragment &&
                ProfileFragment.mBottomSheetBehavior?.state
                != BottomSheetBehavior.STATE_COLLAPSED
            ) {
                ProfileFragment.mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
            navigateFragment(R.id.action_home, true)
            return
        } else if (fragment != null && fragment is HomeFragment && fragment.isVisible()
            && (HomeFragment.mBottomSheetBehavior?.state
                    != BottomSheetBehavior.STATE_COLLAPSED)
        ) {
            HomeFragment.mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        super.onBackPressed()
    }

    private fun navigateFragment(id: Int, shouldSelect: Boolean) {
        if (shouldSelect) {
            bottomNavigationView?.selectedItemId = id
        } else {
            when (id) {
                R.id.action_home -> replaceFragment(
                    localRepository?.clientDetails?.let {
                        newInstance(
                            it
                                .clientId
                        )
                    }, false,
                    R.id.bottom_navigation_fragment_container
                )
                R.id.action_payments -> replaceFragment(
                    newInstance(), false,
                    R.id.bottom_navigation_fragment_container
                )
                R.id.action_finance -> replaceFragment(
                    FinanceFragment.newInstance(), false,
                    R.id.bottom_navigation_fragment_container
                )
                R.id.action_profile -> replaceFragment(
                    ProfileFragment(), false,
                    R.id.bottom_navigation_fragment_container
                )
            }
        }
    }
}