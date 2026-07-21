package com.mxszty.wovenlight.model

import org.json.JSONObject

data class UrlItem(
    val id: String,
    val name: String,
    val url: String
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("url", url)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): UrlItem {
            return UrlItem(
                id = json.getString("id"),
                name = json.getString("name"),
                url = json.getString("url")
            )
        }
    }
}