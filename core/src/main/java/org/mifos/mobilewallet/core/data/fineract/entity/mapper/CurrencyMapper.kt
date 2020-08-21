package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Currency
import javax.inject.Inject




/**
 * Created by naman on 17/8/17.
 */
class CurrencyMapper @Inject constructor(){

    fun transform(savingsCurrency: Currency): org.mifos.mobilewallet.core.domain.model.Currency {
        val currency = org.mifos.mobilewallet.core.domain.model.Currency()
        currency.code = savingsCurrency.code
        currency.displayLabel = savingsCurrency.displayLabel
        currency.displaySymbol = savingsCurrency.displaySymbol
        return currency
    }
}