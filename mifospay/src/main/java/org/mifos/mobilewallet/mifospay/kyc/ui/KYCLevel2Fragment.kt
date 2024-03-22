package org.mifos.mobilewallet.mifospay.kyc.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCLevel2View
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel2Presenter
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCLevel2Fragment : BaseFragment(), KYCLevel2View {

    @JvmField
    @Inject
    var mPresenter: KYCLevel2Presenter? = null
    var mKYCLevel2Presenter: KYCContract.KYCLevel2Presenter? = null

    @JvmField
    @BindView(R.id.btn_browse)
    var btnBrowse: Button? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnSubmit: Button? = null

    @JvmField
    @BindView(R.id.tv_filename)
    var tvFilename: TextView? = null

    @JvmField
    @BindView(R.id.et_idname)
    var etIdname: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_kyc_lvl2, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        //setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_2);
        return rootView
    }

    @OnClick(R.id.btn_browse)
    fun onBrowseClicked() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {

            // Permission has already been granted
            mKYCLevel2Presenter!!.browseDocs()
        }
    }

    @OnClick(R.id.btn_submit)
    fun onSubmitClicked() {
        showProgressDialog(Constants.PLEASE_WAIT)
        mKYCLevel2Presenter!!.uploadKYCDocs(etIdname!!.text.toString().trim { it <= ' ' })
    }

    override fun startDocChooseActivity(intent: Intent?, READ_REQUEST_CODE: Int) {
        startActivityForResult(
            Intent.createChooser(intent, Constants.CHOOSE_FILE),
            READ_REQUEST_CODE
        )
    }

    override fun setFilename(absolutePath: String?) {
        tvFilename!!.text = absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mKYCLevel2Presenter!!.updateFile(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    mKYCLevel2Presenter!!.browseDocs()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_BROWSE_DOCUMENTS)
                }
            }
        }
    }

    override fun showToast(s: String?) {
        Toaster.showToast(context, s)
    }

    override fun hideProgressDialog() {
        super.hideProgressDialog()
    }

    override fun setPresenter(presenter: KYCContract.KYCLevel2Presenter?) {
        mKYCLevel2Presenter = presenter
    }

    override fun goBack() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 1
        @JvmStatic
        fun newInstance(): KYCLevel2Fragment {
            val args = Bundle()
            val fragment = KYCLevel2Fragment()
            fragment.arguments = args
            return fragment
        }
    }
}