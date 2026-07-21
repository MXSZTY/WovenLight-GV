package com.mxszty.wovenlight.model

import org.json.JSONObject

data class CoordinateItem(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("latitude", latitude)
        json.put("longitude", longitude)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): CoordinateItem {
            return CoordinateItem(
                id = json.getString("id"),
                name = json.getString("name"),
                latitude = json.getDouble("latitude"),
                longitude = json.getDouble("longitude")
            )
        }
    }
}