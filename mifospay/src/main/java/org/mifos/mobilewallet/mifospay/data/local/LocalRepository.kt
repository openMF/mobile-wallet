package org.mifos.mobilewallet.mifospay.data.local

import org.mifos.mobilewallet.core.data.fineract.local.PreferencesHelper
import com.mifos.mobilewallet.model.domain.client.Client
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by naman on 17/6/17.
 */
@Singleton
class LocalRepository @Inject constructor(val preferencesHelper: PreferencesHelper) {

    val clientDetails: com.mifos.mobilewallet.model.domain.client.Client
        get() {
            val details = com.mifos.mobilewallet.model.domain.client.Client()
            details.name = preferencesHelper.fullName
            details.clientId = preferencesHelper.clientId
            details.externalId = preferencesHelper.clientVpa
            return details
        }

    fun saveClientData(client: com.mifos.mobilewallet.model.domain.client.Client) {
        preferencesHelper.saveFullName(client.name)
        preferencesHelper.clientId = client.clientId
        preferencesHelper.clientVpa = client.externalId
    }
}