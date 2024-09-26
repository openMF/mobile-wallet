package org.mifospay.core.datastore.proto

import kotlinx.serialization.Serializable

@Serializable
data class RolePreferences(
    val id: String,
    val name: String,
    val description: String,
    val disabled: Boolean
) {
    companion object {
        val DEFAULT = RolePreferences(
            id = "",
            name = "",
            description = "",
            disabled = false
        )
    }
}
