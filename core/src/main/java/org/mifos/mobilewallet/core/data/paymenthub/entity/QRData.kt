package org.mifos.mobilewallet.core.data.paymenthub.entity

data class QRData(val idType: String, val id: String, val name:String, val code: String,
                  val amount: String, val note: String, val currency: String,
                  val clientRefId: String, val refUrl: String)