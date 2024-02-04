package org.mifos.mobilewallet.core.data.fineract.api

/**
 * Created by ankur on 21/May/2018
 */
class GenericResponse {
    var responseFields = HashMap<String, Any>()
    override fun toString(): String {
        return "GenericResponse{" +
                "responseFields=" + responseFields +
                '}'
    }
}
