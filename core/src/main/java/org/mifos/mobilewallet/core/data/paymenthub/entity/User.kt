package org.mifos.mobilewallet.core.data.paymenthub.entity

data class User(val firstName: String,
                val lastName: String,
                val username: String,
                val password: String,
                val tenant: String,
                val fspName: String,
                val role: String,
                val banks: List<Bank>)