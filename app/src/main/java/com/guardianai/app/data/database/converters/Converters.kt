package com.guardianai.app.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromDetectedMetricsList(value: List<Map<String, Any>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDetectedMetricsList(value: String): List<Map<String, Any>> {
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromEmergencyActionsList(value: List<Map<String, Any>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEmergencyActionsList(value: String): List<Map<String, Any>> {
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromEmergencyContactsList(value: List<Map<String, String>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEmergencyContactsList(value: String): List<Map<String, String>> {
        val listType = object : TypeToken<List<Map<String, String>>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromLocationData(value: Map<String, Any>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLocationData(value: String): Map<String, Any> {
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}