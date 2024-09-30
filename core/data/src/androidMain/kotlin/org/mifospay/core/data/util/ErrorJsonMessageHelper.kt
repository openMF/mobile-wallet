/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.util

import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

object ErrorJsonMessageHelper {
    @JvmStatic
    @Throws(JSONException::class)
    fun getUserMessage(message: String?): String {
        val jsonObject = JSONObject(message)
        return jsonObject.getJSONArray("errors")
            .getJSONObject(0).getString("defaultUserMessage")
    }

    @JvmStatic
    fun getUserMessage(e: Throwable): String? {
        var message: String?
        try {
            message = (e as HttpException).response()?.errorBody()?.string().toString()
            message = getUserMessage(message)
        } catch (e1: Exception) {
            message = e1.message.toString()
        }
        return message
    }
}
