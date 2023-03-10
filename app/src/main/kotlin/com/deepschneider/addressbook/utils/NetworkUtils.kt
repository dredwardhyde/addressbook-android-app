package com.deepschneider.addressbook.utils

import android.content.Context
import androidx.preference.PreferenceManager

object NetworkUtils {
    fun addAuthHeader(
        sourceHeaders: MutableMap<String, String>?,
        context: Context
    ): MutableMap<String, String> {
        var headers = sourceHeaders
        if (headers == null || headers == emptyMap<String, String>()) {
            headers = HashMap()
        }
        headers["authorization"] = "Bearer " + PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.TOKEN_KEY, Constants.NO_VALUE)
        return headers
    }

    fun getServerUrl(context: Context): String {
        val serverHost = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(Constants.SETTINGS_SERVER_URL, Constants.NO_VALUE)
        if (serverHost == Constants.NO_VALUE || serverHost.isNullOrBlank()) return Constants.NO_VALUE
        val shouldUseHttp = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(Constants.SETTINGS_SHOULD_USE_HTTP, false)
        return (if (shouldUseHttp) "http://" else "https://") + serverHost
    }
}