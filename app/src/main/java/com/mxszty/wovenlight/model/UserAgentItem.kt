package com.mxszty.wovenlight.model

import org.json.JSONObject

data class UserAgentItem(
    val id: String,
    val name: String,
    val userAgent: String
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("userAgent", userAgent)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): UserAgentItem {
            return UserAgentItem(
                id = json.getString("id"),
                name = json.getString("name"),
                userAgent = json.getString("userAgent")
            )
        }
    }
}