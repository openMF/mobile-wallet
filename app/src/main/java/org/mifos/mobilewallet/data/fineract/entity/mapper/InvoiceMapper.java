package org.mifos.mobilewallet.data.fineract.entity.mapper;

import org.mifos.mobilewallet.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.data.fineract.entity.accounts.savings.Transactions;
import org.mifos.mobilewallet.invoice.domain.model.Invoice;
import org.mifos.mobilewallet.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by naman on 11/7/17.
 */

@Singleton
public class InvoiceMapper {

    @Inject
    public InvoiceMapper() {}

    public List<Invoice> transformInvoiceList(SavingsWithAssociations savingsWithAssociations) {
        List<Invoice> invoiceList = new ArrayList<>();

        if (savingsWithAssociations != null && savingsWithAssociations.getTransactions() != null
                && savingsWithAssociations.getTransactions().size() != 0) {

            for (Transactions transactions : savingsWithAssociations.getTransactions()) {
                invoiceList.add(transformInvoice(transactions));
            }


        }
        return invoiceList;
    }

    public Invoice transformInvoice(Transactions transactions) {
        Invoice invoice = new Invoice();

        if (transactions != null ) {

          invoice.setAmount(transactions.getAmount());

          if (transactions.getPaymentDetailData() != null) {
              invoice.setInvoiceId(transactions.getPaymentDetailData().getReceiptNumber());
          }

          if (transactions.getSubmittedOnDate() != null) {
              invoice.setDate(DateHelper.getDateAsString(transactions.getSubmittedOnDate()));
          }

        }
        return invoice;
    }
}
