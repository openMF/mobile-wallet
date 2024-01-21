package org.mifos.mobilewallet.mifospay.kyc.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.kyc.UploadKYCDocs
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCLevel2View
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.FileUtils
import java.io.File
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
class KYCLevel2Presenter @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper,
) : KYCContract.KYCLevel2Presenter {

    override val context: Context = MifosPayApp.context!!

    @JvmField
    @Inject
    var uploadKYCDocsUseCase: UploadKYCDocs? = null
    private var file: File? = null
    private var mKYCLevel2View: KYCLevel2View? = null
    override fun attachView(baseView: BaseView<*>?) {
        mKYCLevel2View = baseView as KYCLevel2View?
        mKYCLevel2View!!.setPresenter(this)
    }

    override fun browseDocs() {
        val mimeTypes = arrayOf(Constants.APPLICATION_PDF, Constants.IMAGE)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        mKYCLevel2View!!.startDocChooseActivity(intent, READ_REQUEST_CODE)
    }

    override fun updateFile(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            file = File(FileUtils.getPath(context, uri))
            mKYCLevel2View!!.setFilename(file!!.path)
        }
    }

    override fun uploadKYCDocs(identityType: String?) {
        if (file != null) {
            uploadKYCDocsUseCase!!.requestValues = UploadKYCDocs.RequestValues(
                org.mifos.mobilewallet.core.utils.Constants.ENTITY_TYPE_CLIENTS,
                preferencesHelper.clientId, file!!.name, identityType,
                getRequestFileBody(file!!)
            )
            val requestValues = uploadKYCDocsUseCase!!.requestValues
            mUseCaseHandler.execute(uploadKYCDocsUseCase, requestValues,
                object : UseCaseCallback<UploadKYCDocs.ResponseValue?> {
                    override fun onSuccess(response: UploadKYCDocs.ResponseValue?) {
                        mKYCLevel2View!!.hideProgressDialog()
                        mKYCLevel2View!!.showToast(
                            Constants.KYC_LEVEL_2_DOCUMENTS_ADDED_SUCCESSFULLY
                        )
                        mKYCLevel2View!!.goBack()
                    }

                    override fun onError(message: String) {
                        mKYCLevel2View!!.hideProgressDialog()
                        mKYCLevel2View!!.showToast(Constants.ERROR_UPLOADING_DOCS)
                    }
                })
        } else {
            // choose a file first
            mKYCLevel2View!!.showToast(Constants.CHOOSE_A_FILE_TO_UPLOAD)
            mKYCLevel2View!!.hideProgressDialog()
        }
    }

    private fun getRequestFileBody(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.parse(Constants.MULTIPART_FORM_DATA),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(Constants.FILE, file.name, requestFile)
    }

    companion object {
        private const val READ_REQUEST_CODE = 42
    }
}