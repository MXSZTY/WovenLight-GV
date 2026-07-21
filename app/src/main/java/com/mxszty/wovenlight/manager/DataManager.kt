package com.mxszty.wovenlight.manager

import android.content.Context
import androidx.core.content.edit
import com.mxszty.wovenlight.model.CoordinateItem
import com.mxszty.wovenlight.model.UserAgentItem
import com.mxszty.wovenlight.model.UrlItem
import org.json.JSONArray
import org.json.JSONException
import java.util.UUID

object DataManager {
    private const val PREFS_NAME = "AppData"
    private const val PREF_URL_LIST = "url_list"
    private const val PREF_UA_LIST = "ua_list"
    private const val PREF_COORD_LIST = "coord_list"
    private const val PREF_SELECTED_URL_ID = "selected_url_id"
    private const val PREF_SELECTED_UA_ID = "selected_ua_id"
    private const val PREF_SELECTED_COORD_ID = "selected_coord_id"
    private const val PREF_USE_VIRTUAL_LOCATION = "use_virtual_location"

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getUrlList(context: Context): List<UrlItem> {
        val jsonString = getPrefs(context).getString(PREF_URL_LIST, "[]")
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<UrlItem>()
            for (i in 0 until jsonArray.length()) {
                list.add(UrlItem.fromJson(jsonArray.getJSONObject(i)))
            }
            list
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun saveUrlList(context: Context, list: List<UrlItem>) {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it.toJson()) }
        getPrefs(context).edit { putString(PREF_URL_LIST, jsonArray.toString()) }
    }

    fun addUrl(context: Context, name: String, url: String) {
        val list = getUrlList(context).toMutableList()
        list.add(UrlItem(UUID.randomUUID().toString(), name, url))
        saveUrlList(context, list)
    }

    fun updateUrl(context: Context, id: String, name: String, url: String) {
        val list = getUrlList(context).toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = UrlItem(id, name, url)
            saveUrlList(context, list)
        }
    }

    fun deleteUrl(context: Context, id: String) {
        val list = getUrlList(context).toMutableList()
        list.removeAll { it.id == id }
        saveUrlList(context, list)
        if (getSelectedUrlId(context) == id) {
            setSelectedUrlId(context, list.firstOrNull()?.id ?: "")
        }
    }

    fun getSelectedUrlId(context: Context): String {
        return getPrefs(context).getString(PREF_SELECTED_URL_ID, "") ?: ""
    }

    fun setSelectedUrlId(context: Context, id: String) {
        getPrefs(context).edit { putString(PREF_SELECTED_URL_ID, id) }
    }

    fun getSelectedUrl(context: Context): UrlItem? {
        val id = getSelectedUrlId(context)
        return getUrlList(context).firstOrNull { it.id == id }
    }

    fun getUserAgentList(context: Context): List<UserAgentItem> {
        val jsonString = getPrefs(context).getString(PREF_UA_LIST, "[]")
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<UserAgentItem>()
            for (i in 0 until jsonArray.length()) {
                list.add(UserAgentItem.fromJson(jsonArray.getJSONObject(i)))
            }
            list
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun saveUserAgentList(context: Context, list: List<UserAgentItem>) {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it.toJson()) }
        getPrefs(context).edit { putString(PREF_UA_LIST, jsonArray.toString()) }
    }

    fun addUserAgent(context: Context, name: String, userAgent: String) {
        val list = getUserAgentList(context).toMutableList()
        list.add(UserAgentItem(UUID.randomUUID().toString(), name, userAgent))
        saveUserAgentList(context, list)
    }

    fun updateUserAgent(context: Context, id: String, name: String, userAgent: String) {
        val list = getUserAgentList(context).toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = UserAgentItem(id, name, userAgent)
            saveUserAgentList(context, list)
        }
    }

    fun deleteUserAgent(context: Context, id: String) {
        val list = getUserAgentList(context).toMutableList()
        list.removeAll { it.id == id }
        saveUserAgentList(context, list)
        if (getSelectedUAId(context) == id) {
            setSelectedUAId(context, list.firstOrNull()?.id ?: "")
        }
    }

    fun getSelectedUAId(context: Context): String {
        return getPrefs(context).getString(PREF_SELECTED_UA_ID, "") ?: ""
    }

    fun setSelectedUAId(context: Context, id: String) {
        getPrefs(context).edit { putString(PREF_SELECTED_UA_ID, id) }
    }

    fun getSelectedUA(context: Context): UserAgentItem? {
        val id = getSelectedUAId(context)
        return getUserAgentList(context).firstOrNull { it.id == id }
    }

    fun getCoordinateList(context: Context): List<CoordinateItem> {
        val jsonString = getPrefs(context).getString(PREF_COORD_LIST, "[]")
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<CoordinateItem>()
            for (i in 0 until jsonArray.length()) {
                list.add(CoordinateItem.fromJson(jsonArray.getJSONObject(i)))
            }
            list
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun saveCoordinateList(context: Context, list: List<CoordinateItem>) {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it.toJson()) }
        getPrefs(context).edit { putString(PREF_COORD_LIST, jsonArray.toString()) }
    }

    fun addCoordinate(context: Context, name: String, latitude: Double, longitude: Double) {
        val list = getCoordinateList(context).toMutableList()
        list.add(CoordinateItem(UUID.randomUUID().toString(), name, latitude, longitude))
        saveCoordinateList(context, list)
    }

    fun updateCoordinate(context: Context, id: String, name: String, latitude: Double, longitude: Double) {
        val list = getCoordinateList(context).toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = CoordinateItem(id, name, latitude, longitude)
            saveCoordinateList(context, list)
        }
    }

    fun deleteCoordinate(context: Context, id: String) {
        val list = getCoordinateList(context).toMutableList()
        list.removeAll { it.id == id }
        saveCoordinateList(context, list)
        if (getSelectedCoordId(context) == id) {
            setSelectedCoordId(context, list.firstOrNull()?.id ?: "")
        }
    }

    fun getSelectedCoordId(context: Context): String {
        return getPrefs(context).getString(PREF_SELECTED_COORD_ID, "") ?: ""
    }

    fun setSelectedCoordId(context: Context, id: String) {
        getPrefs(context).edit { putString(PREF_SELECTED_COORD_ID, id) }
    }

    fun getSelectedCoord(context: Context): CoordinateItem? {
        val id = getSelectedCoordId(context)
        return getCoordinateList(context).firstOrNull { it.id == id }
    }

    fun getUseVirtualLocation(context: Context): Boolean {
        return getPrefs(context).getBoolean(PREF_USE_VIRTUAL_LOCATION, false)
    }

    fun setUseVirtualLocation(context: Context, enabled: Boolean) {
        getPrefs(context).edit { putBoolean(PREF_USE_VIRTUAL_LOCATION, enabled) }
    }
}