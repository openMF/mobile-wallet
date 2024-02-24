package com.mifos.mobilewallet.model.entity

/**
 * @author Rajan Maurya
 */
data class Page<T> (
    var totalFilteredRecords: Int = 0,
    @JvmField
    var pageItems: MutableList<T> = ArrayList()

)