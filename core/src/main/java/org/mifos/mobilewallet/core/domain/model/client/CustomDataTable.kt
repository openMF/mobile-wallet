package org.mifos.mobilewallet.core.domain.model.client

import java.util.HashMap

data class CustomDataTable(
        var registeredTableName: String = "client_info",
        var data: HashMap<String, Any?> = hashMapOf("local" to "en", "info_id" to 1))