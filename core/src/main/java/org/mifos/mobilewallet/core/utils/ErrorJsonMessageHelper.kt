package org.mifos.mobilewallet.core.utils

import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Created by ankur on 26/June/2018
 */
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
        var message: String? = "Error"
        try {
            message = (e as HttpException).response().errorBody().string()
            message = getUserMessage(message)
        } catch (e1: Exception) {
            message = "Error"
        }
        return message
    }
}
