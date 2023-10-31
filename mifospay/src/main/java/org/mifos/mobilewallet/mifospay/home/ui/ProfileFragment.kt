package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.domain.model.client.Client
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.editprofile.ui.EditProfileActivity
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.ProfileView
import org.mifos.mobilewallet.mifospay.home.presenter.ProfilePresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.TextDrawable
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by naman on 7/9/17.
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment(), ProfileView {
    @JvmField
    @Inject
    var mPresenter: ProfilePresenter? = null
    var mProfilePresenter: BaseHomeContract.ProfilePresenter? = null

    @JvmField
    @BindView(R.id.iv_user_image)
    var ivUserImage: ImageView? = null

    @JvmField
    @BindView(R.id.tv_user_name)
    var tvUserName: TextView? = null

    @JvmField
    @BindView(R.id.nsv_profile_bottom_sheet_dialog)
    var vProfileBottomSheetDialog: View? = null

    @JvmField
    @BindView(R.id.inc_account_details_email)
    var vAccountDetailsEmail: View? = null

    @JvmField
    @BindView(R.id.inc_account_details_vpa)
    var vAccountDetailsVpa: View? = null

    @JvmField
    @BindView(R.id.inc_account_details_mobile_number)
    var vAccountDetailsMobile: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter?.attachView(this)
        setupUi()
        return rootView
    }

    private fun setupUi() {
        setToolbarTitle(Constants.PROFILE)
        setSwipeEnabled(false)
        hideBackButton()
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior
            .from(vProfileBottomSheetDialog!!)
        mBottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {}
            override fun onSlide(view: View, v: Float) {}
        })
    }

    override fun onResume() {
        super.onResume()
        mProfilePresenter?.fetchProfile()
        mProfilePresenter?.fetchAccountDetails()
        mProfilePresenter?.fetchClientImage()
    }

    override fun setPresenter(presenter: BaseHomeContract.ProfilePresenter?) {
        mProfilePresenter = presenter
    }

    @OnClick(R.id.iv_user_image)
    fun onUserImageEditClicked() {
        if (activity != null) {
            val i = Intent(activity, EditProfileActivity::class.java)
            i.putExtra(Constants.CHANGE_PROFILE_IMAGE_KEY, Constants.CHANGE_PROFILE_IMAGE_VALUE)
            startActivity(i)
        }
    }

    @OnClick(R.id.btn_profile_bottom_sheet_action)
    fun onEditProfileClicked() {
        if (activity != null) {
            activity?.startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    override fun showProfile(client: Client?) {
        val drawable = TextDrawable.builder().beginConfig()
            .width(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .height(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .endConfig().buildRound(client?.name?.substring(0, 1), R.color.colorAccentBlack)
        ivUserImage?.setImageDrawable(drawable)
        tvUserName?.text = client?.name
    }

    override fun showEmail(email: String?) {
        (vAccountDetailsEmail?.findViewById<View>(R.id.iv_item_casual_list_icon) as ImageView)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_email))
        (vAccountDetailsEmail?.findViewById<View>(R.id.tv_item_casual_list_title) as TextView).text =
            email
        (vAccountDetailsEmail?.findViewById<View>(R.id.tv_item_casual_list_subtitle) as TextView).text =
            resources.getString(R.string.email)
    }

    override fun showVpa(vpa: String?) {
        (vAccountDetailsVpa?.findViewById<View>(R.id.iv_item_casual_list_icon) as ImageView)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_transaction))
        (vAccountDetailsVpa?.findViewById<View>(R.id.tv_item_casual_list_title) as TextView).text =
            vpa
        (vAccountDetailsVpa?.findViewById<View>(R.id.tv_item_casual_list_subtitle) as TextView).text =
            resources.getString(R.string.vpa)
    }

    override fun showMobile(mobile: String?) {
        (vAccountDetailsMobile?.findViewById<View>(R.id.iv_item_casual_list_icon) as ImageView)
            .setImageDrawable(resources.getDrawable(R.drawable.ic_mobile))
        (vAccountDetailsMobile?.findViewById<View>(R.id.tv_item_casual_list_title) as TextView).text =
            mobile
        (vAccountDetailsMobile?.findViewById<View>(R.id.tv_item_casual_list_subtitle) as TextView).text =
            resources.getString(R.string.mobile)
    }

    override fun fetchImageSuccess(responseBody: ResponseBody?) {}
    override fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(view, message)
    }

    companion object {
        @JvmField
        var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
        fun newInstance(clientId: Long): ProfileFragment {
            val args = Bundle()
            args.putLong(Constants.CLIENT_ID, clientId)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}