package org.mifos.mobilewallet.core.data.fineract.entity.mapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mifos.mobilewallet.core.domain.model.Currency;

/**
 * Created by naman on 17/8/17.
 */

public class CurrencyMapper {

    @Inject
    CurrencyMapper() {}

    public Currency transform(org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.Currency savingsCurrency) {

        Currency currency = new Currency();
        currency.setCode(savingsCurrency.getCode());
        currency.setDisplayLabel(savingsCurrency.getDisplayLabel());
        currency.setDisplaySymbol(savingsCurrency.getDisplaySymbol());

        return currency;
    }
}
