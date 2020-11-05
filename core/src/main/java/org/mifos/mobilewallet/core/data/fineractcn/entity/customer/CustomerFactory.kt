package org.mifos.mobilewallet.core.data.fineractcn.entity.customer

import android.os.Build
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.ContactDetail.Group.PRIVATE
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.ContactDetail.Type.EMAIL
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.ContactDetail.Type.MOBILE
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.Customer.State.PENDING
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.Customer.Type.PERSON
import org.mifos.mobilewallet.core.utils.Constants.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CustomerFactory {

    fun createCustomer(
            customerIdentifier: String, firstName: String, middleName: String, lastName: String,
            day: Int, month: Int, year: Int, streetName: String, city: String, postalCode: String,
            countryCode: String, countryName: String, mobileNumber: String, emailId: String)
            : Customer {

        val dateOfBirth = DateOfBirth(year, month, day)
        val address = Address(streetName, city, "", postalCode, countryCode, countryName)
        val customerMobile = ContactDetail(MOBILE, mobileNumber, 1, true, PRIVATE)
        val customerEmail = ContactDetail(EMAIL, emailId, 1, true, PRIVATE)
        val contactList = arrayListOf<ContactDetail>(customerMobile, customerEmail)
        val values = emptyList<String>()
        var createdOn: String? = null
        var applicationDate: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val current = LocalDateTime.now()
            val applicationDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val createdOnFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            createdOn = current.format(createdOnFormatter)
            applicationDate = current.format(applicationDateFormatter)

        }

        return Customer(customerIdentifier, PERSON, firstName, middleName, lastName, dateOfBirth,
                true, CUSTOMER_ACCOUNT_BENEFICIARY, CUSTOMER_REFERENCE_CUSTOMER,
                CUSTOMER_ASSIGNED_OFFICE, CUSTOMER_ASSIGNED_EMPLOYEE, address, contactList,
                PENDING, applicationDate, values, USER, createdOn, null, null)
    }
}