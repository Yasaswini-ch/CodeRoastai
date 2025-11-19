package com.example.coderoastai.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "roast_history")
@TypeConverters(Converters::class)
data class RoastHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val language: String,
    val personality: String,
    val intensity: Int,
    val score: Int? = null,
    val grade: String? = null,
    val roasts: List<String>,
    val issues: List<String>? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
