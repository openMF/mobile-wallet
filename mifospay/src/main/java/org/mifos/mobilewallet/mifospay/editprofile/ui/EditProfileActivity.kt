package org.mifos.mobilewallet.mifospay.editprofile.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import butterknife.*
import com.hbb20.CountryCodePicker
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import com.yalantis.ucrop.UCrop
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityEditProfileBinding
import org.mifos.mobilewallet.mifospay.editprofile.EditProfileContract
import org.mifos.mobilewallet.mifospay.editprofile.EditProfileContract.EditProfileView
import org.mifos.mobilewallet.mifospay.editprofile.presenter.EditProfilePresenter
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.password.ui.EditPasswordActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DialogBox
import org.mifos.mobilewallet.mifospay.utils.TextDrawable
import org.mifos.mobilewallet.mifospay.utils.Toaster
import java.io.File
import java.util.*
import javax.inject.Inject

class EditProfileActivity : BaseActivity(), EditProfileView {
    val binding = ActivityEditProfileBinding.inflate(layoutInflater)

    @JvmField
    @Inject
    var mPresenter: EditProfilePresenter? = null
    var mEditProfilePresenter: EditProfileContract.EditProfilePresenter? = null

    var userDetailsInputs: List<EditText>? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var dialogBox: DialogBox? = DialogBox()
    private var passcodePreferencesHelper: PasscodePreferencesHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        activityComponent.inject(this)
        ButterKnife.bind(this)
        setupUi()
        mPresenter!!.attachView(this)
        binding.ccpNewCode!!.registerCarrierNumberEditText(binding.etEditProfileMobile)
        mEditProfilePresenter!!.fetchUserDetails()
        passcodePreferencesHelper = PasscodePreferencesHelper(this)
        if (isChangeImageRequestFromProfile) {
            bottomSheetDialog!!.show()
        }
    }

    private fun setupUi() {
        hideFab()
        showCloseButton()
        setToolbarTitle(Constants.EDIT_PROFILE)
        setupBottomSheetDialog()
    }

    private fun setupBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this@EditProfileActivity)
        val sheetView = layoutInflater
            .inflate(R.layout.dialog_change_profile_picture, null)
        bottomSheetDialog!!.setContentView(sheetView)
        val bsv = BottomSheetViews()
        ButterKnife.bind(bsv, sheetView)
    }

    private val isChangeImageRequestFromProfile: Boolean
        get() = intent.getStringExtra(Constants.CHANGE_PROFILE_IMAGE_KEY) != null

    override fun setPresenter(presenter: EditProfileContract.EditProfilePresenter?) {
        mEditProfilePresenter = presenter
    }

    @OnClick(R.id.fab_edit_profile_save_changes)
    fun onSaveChangesClicked() {
        for (input in userDetailsInputs!!) {
            if (isDataSaveNecessary(input)) {
                mPresenter!!.updateInputById(input.id, input.text.toString())
            }
        }
        hideFab()
        hideKeyboard()
    }

    @OnClick(R.id.iv_user_image)
    fun onProfileImageClicked() {
        bottomSheetDialog!!.show()
    }

    @OnClick(R.id.btn_change_password)
    fun onChangePasswordClicked() {
        startActivity(Intent(applicationContext, EditPasswordActivity::class.java))
    }

    @OnClick(R.id.btn_change_passcode)
    fun onChangePasscodeClicked() {
        val currentPasscode = passcodePreferencesHelper!!.passCode
        // for re-initiating passcode generation process
        passcodePreferencesHelper!!.savePassCode("")
        val intent = Intent(this, PassCodeActivity::class.java)
        intent.putExtra(Constants.CURRENT_PASSCODE, currentPasscode)
        intent.putExtra(Constants.UPDATE_PASSCODE, true)
        startActivity(intent)
    }

    @OnTextChanged(
        R.id.et_edit_profile_username,
        R.id.et_edit_profile_email,
        R.id.et_edit_profile_vpa,
        R.id.et_edit_profile_mobile
    )
    fun onUserDetailsChanged() {
        for (et in userDetailsInputs!!) {
            if (isDataSaveNecessary(et)) {
                mPresenter?.handleNecessaryDataSave()
                break
            } else {
                hideFab()
            }
        }
    }

    private fun isDataSaveNecessary(input: EditText): Boolean {
        val content = input.text.toString()
        val currentContent = input.hint.toString()
        return !(TextUtils.isEmpty(content) || content == currentContent)
    }

    @OnFocusChange(
        R.id.et_edit_profile_username,
        R.id.et_edit_profile_email,
        R.id.et_edit_profile_vpa,
        R.id.et_edit_profile_mobile
    )
    fun onUserDetailsFocusChanged(input: EditText, isFocused: Boolean) {
        if (!isDataSaveNecessary(input)) {
            if (isFocused) {
                input.setText(input.hint.toString())
            } else {
                input.text.clear()
            }
        }
    }

    override fun showDefaultImageByUsername(fullName: String?) {
        val drawable = TextDrawable.builder().beginConfig()
            .width(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .height(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .endConfig().buildRound(fullName?.substring(0, 1), R.color.colorPrimary)
        binding.include.ivUserImage?.setImageDrawable(drawable)
    }

    override fun showUsername(username: String?) {
        binding.tilEditProfileUsername?.hint = username
        handleUpdatedInput(binding.etEditProfileUsername)
    }

    override fun showEmail(email: String?) {
        binding.tilEditProfileEmail?.hint = email
        handleUpdatedInput(binding.etEditProfileEmail)
    }

    override fun showVpa(vpa: String?) {
        binding.tilEditProfileVpa?.hint = vpa
        handleUpdatedInput(binding.etEditProfileVpa)
    }

    override fun showMobileNumber(mobileNumber: String?) {
        binding.tilEditProfileMobile?.hint = mobileNumber
        handleUpdatedInput(binding.etEditProfileMobile)
    }

    private fun handleUpdatedInput(input: EditText?) {
        input?.clearFocus()
        input?.text?.clear()
    }

    override fun showFab() {
        binding.fabEditProfileSaveChanges?.show()
    }

    override fun hideFab() {
        binding.fabEditProfileSaveChanges?.hide()
    }

    override fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_CAMERA)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toaster.showToast(
                        this,
                        getString(R.string.need_camera_permission_to_click_profile_picture)
                    )
                }
                return
            }
            REQUEST_READ_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    val i = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(i, REQUEST_READ_IMAGE)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_BROWSE_IMAGES)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_READ_IMAGE && data != null) {
                val selectedImageUri = data.data
                selectedImageUri?.let { startCrop(it) }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            } else if (requestCode == REQUEST_CAMERA) {
                val extras = data!!.extras
                val profileBitmapImage = extras!!["data"] as Bitmap?
                binding.include.ivUserImage!!.setImageBitmap(profileBitmapImage)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationFileName = UUID.randomUUID().toString() + ".jpg"
        var uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
        uCrop = getConfiguredUCrop(uCrop)
        uCrop.start(this@EditProfileActivity)
    }

    private fun getConfiguredUCrop(uCrop: UCrop): UCrop {
        var uCrop = uCrop
        uCrop = uCrop.withAspectRatio(1f, 1f)
        val size = resources.getDimension(R.dimen.user_profile_image_size).toInt()
        if (size > UCrop.MIN_SIZE) {
            uCrop = uCrop.withMaxResultSize(size, size)
        }
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(80)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.primaryBlue))
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black))
        options.setCircleDimmedLayer(true)
        options.setShowCropFrame(false)
        options.setCropGridColumnCount(0)
        options.setCropGridRowCount(0)
        return uCrop.withOptions(options)
    }

    override fun removeProfileImage() {
        // TODO: Remove image from database
    }

    override fun changeProfileImage() {
        pickImageFromGallery()
    }

    override fun clickProfileImage() {
        clickProfilePicFromCamera()
    }

    private fun pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            val i = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(
                Intent.createChooser(
                    i,
                    getString(R.string.label_select_picture)
                ), REQUEST_READ_IMAGE
            )
        }
    }

    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            binding.include.ivUserImage!!.setImageURI(resultUri)
        }
    }

    private fun clickProfilePicFromCamera() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {

            // Permission has already been granted
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    override fun showDiscardChangesDialog() {
        dialogBox!!.setOnPositiveListener { dialog, which ->
            mPresenter!!.onDialogPositive()
            dialog.dismiss()
        }
        dialogBox!!.setOnNegativeListener { dialog, which ->
            mPresenter!!.onDialogNegative()
            dialog.dismiss()
        }
        dialogBox!!.show(
            this, R.string.discard_changes_and_exit,
            R.string.discard_changes_and_exit_description, R.string.accept, R.string.cancel
        )
    }

    override fun onUpdateEmailError(message: String?) {
        showToast(message)
    }

    override fun onUpdateMobileError(message: String?) {
        showToast(message)
    }

    override fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }

    override fun startProgressBar() {
        showProgressDialog(Constants.PLEASE_WAIT)
    }

    override fun stopProgressBar() {
        hideProgressDialog()
    }

    override fun closeActivity() {
        finish()
    }

    override fun onBackPressed() {
        if (binding.fabEditProfileSaveChanges!!.isOrWillBeShown) {
            mPresenter!!.handleExitOnUnsavedChanges()
        } else {
            super.onBackPressed()
        }
    }

    public override fun onPause() {
        super.onPause()
        cancelProgressDialog()
        if (dialogBox != null) {
            dialogBox!!.dismiss()
        }
    }

    internal inner class BottomSheetViews {
        @OnClick(R.id.ll_change_profile_image_dialog_row)
        fun onChangeProfileImageClicked() {
            mPresenter!!.handleProfileImageChangeRequest()
            bottomSheetDialog!!.dismiss()
        }

        @OnClick(R.id.ll_remove_profile_image_dialog_row)
        fun onRemoveProfileImageClicked() {
            mEditProfilePresenter!!.handleProfileImageRemoved()
            bottomSheetDialog!!.dismiss()
        }

        @OnClick(R.id.ll_click_profile_image_dialog_row)
        fun onClickProfileImageClicked() {
            mEditProfilePresenter!!.handleClickProfileImageRequest()
            bottomSheetDialog!!.dismiss()
        }
    }

    companion object {
        private const val REQUEST_READ_IMAGE = 1
        private const val REQUEST_READ_EXTERNAL_STORAGE = 7
        private const val REQUEST_CAMERA = 0
    }
}