package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.entity.accounts.savings.Currency
import javax.inject.Inject
import com.mifospay.core.model.domain.Currency as DomainCurrency


class CurrencyMapper @Inject internal constructor() {
    fun transform(savingsCurrency: Currency): DomainCurrency {
        val currency: DomainCurrency =
            DomainCurrency()
        currency.code = savingsCurrency.code
        currency.displayLabel = savingsCurrency.displayLabel
        currency.displaySymbol = savingsCurrency.displaySymbol
        return currency
    }
}
