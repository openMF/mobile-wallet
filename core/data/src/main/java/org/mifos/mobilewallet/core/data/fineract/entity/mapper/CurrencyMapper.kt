package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.entity.accounts.savings.Currency
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
class CurrencyMapper @Inject internal constructor() {
    fun transform(savingsCurrency: Currency): com.mifos.mobilewallet.model.domain.Currency {
        val currency: com.mifos.mobilewallet.model.domain.Currency =
            com.mifos.mobilewallet.model.domain.Currency()
        currency.code = savingsCurrency.code
        currency.displayLabel = savingsCurrency.displayLabel
        currency.displaySymbol = savingsCurrency.displaySymbol
        return currency
    }
}
