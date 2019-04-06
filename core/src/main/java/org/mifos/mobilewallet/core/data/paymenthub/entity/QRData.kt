package org.mifos.mobilewallet.core.data.paymenthub.entity

data class QRData(var idType: String? = "", var id: String? = "", var name:String? = "",
                  var code: String? = "", var amount: String? = "", var note: String? = "",
                  var currency: String? = "", var clientRefId: String? = "",
                  var refUrl: String? = "")