package org.mifos.mobilewallet.core.data.common;

import java.util.HashMap;

/**
 * Created by ankur on 21/May/2018
 */
public class GenericResponse {

    HashMap<String, Object> responseFields = new HashMap<String, Object>();

    public HashMap<String, Object> getResponseFields() {
        return responseFields;
    }

    public void setResponseFields(HashMap<String, Object> responseFields) {
        this.responseFields = responseFields;
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "responseFields=" + responseFields +
                '}';
    }
}
