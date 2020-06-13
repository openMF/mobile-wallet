package org.mifos.mobilewallet.core.data.fineract.entity.payload

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StandingInstructionPayload(var fromOfficeId : Int, var fromClientId : Int,
                                      var fromAccountType : Int, val name : String?,
                                      val transferType : Int, val priority : Int,
                                      val status : Int, var fromAccountId : Long,
                                      var toOfficeId : Int, var toClientId : Int,
                                      var toAccountType : Int, var toAccountId : Long,
                                      val instructionType : Int, var amount : Double,
                                      var validFrom : String?, val recurrenceType : Int,
                                      val recurrenceInterval : Int, val recurrenceFrequency : Int,
                                      val locale : String?, val dateFormat : String?,
                                      var validTill : String?, var recurrenceOnMonthDay : String?,
                                      val monthDayFormat : String?) : Parcelable