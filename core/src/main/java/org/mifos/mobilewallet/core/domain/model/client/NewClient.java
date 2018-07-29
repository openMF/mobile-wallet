package org.mifos.mobilewallet.core.domain.model.client;

import org.mifos.mobilewallet.core.utils.DateHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by naman on 20/8/17.
 */

public class NewClient {

    private String fullname;
    private String externalId;
    private String officeId = "1";
    private boolean active = true;
    private String activationDate;
    private List<Address> address = new ArrayList<>();
    private String mobileNo;
    private String dateFormat = "dd MMMM yyyy";
    private String locale = "en";
    private String submittedOnDate;
    private int savingsProductId;
//    List<CustomDataTable> datatables = new ArrayList<>();

    public NewClient(String fullname, String externalId, String addressLine1,
            String addressLine2, String city, String postalCode, String stateProvinceId,
            String countryId, String mobileNo, int mifosSavingsProductId) {
        this.fullname = fullname;
        this.externalId = externalId + "@mifos";

        address.add(new Address(addressLine1, addressLine2, city, postalCode, stateProvinceId,
                countryId));
        this.mobileNo = mobileNo;

        activationDate = DateHelper.getDateAsStringFromLong(System.currentTimeMillis());
        submittedOnDate = activationDate;
        savingsProductId = mifosSavingsProductId;

//        CustomDataTable dataTable = new CustomDataTable();
//        datatables.add(dataTable);
    }
}

class Address {

    protected String addressTypeId = "1"; // office
    protected boolean isActive = true;
    protected String addressLine1;
    protected String addressLine2;
    protected String street;
    protected String postalCode;
    protected String stateProvinceId;
    protected String countryId;

    public Address(String addressLine1, String addressLine2, String street, String postalCode,
            String stateProvinceId, String countryId) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
        this.stateProvinceId = stateProvinceId;
        this.countryId = countryId;
        this.street = street;
    }
}

class CustomDataTable {
    private String registeredTableName = "client_info";
    private HashMap<String, Object> data;

    public CustomDataTable() {
        data = new HashMap<>();
        data.put("locale", "en");
        data.put("info_id", 1);
    }
}