package com.example.coderoastai.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RoastHistoryRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).roastHistoryDao()
    private val maxHistorySize = 50

    fun getRecentHistory(): Flow<List<RoastHistoryEntity>> {
        return dao.getRecentHistory()
    }

    fun getAllHistory(): Flow<List<RoastHistoryEntity>> {
        return dao.getHistory(maxHistorySize)
    }

    suspend fun getHistoryById(id: Long): RoastHistoryEntity? {
        return dao.getHistoryById(id)
    }

    fun getHistoryByLanguage(language: String): Flow<List<RoastHistoryEntity>> {
        return dao.getHistoryByLanguage(language)
    }

    fun searchHistory(query: String): Flow<List<RoastHistoryEntity>> {
        return dao.searchHistory(query)
    }

    suspend fun addHistory(history: RoastHistoryEntity) {
        // Check if we need to delete old entries
        val count = dao.getCount()
        if (count >= maxHistorySize) {
            dao.deleteOldest(count - maxHistorySize + 1)
        }
        dao.insert(history)
    }

    suspend fun deleteHistory(history: RoastHistoryEntity) {
        dao.delete(history)
    }

    suspend fun deleteHistoryById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearAll()
    }

    suspend fun exportHistoryToJson(context: Context): File? {
        return try {
            val history = dao.getHistory().first()
            val jsonArray = JSONArray()

            history.forEach { item ->
                val jsonObject = JSONObject().apply {
                    put("id", item.id)
                    put("code", item.code)
                    put("language", item.language)
                    put("personality", item.personality)
                    put("intensity", item.intensity)
                    put("score", item.score ?: JSONObject.NULL)
                    put("grade", item.grade ?: JSONObject.NULL)
                    put("roasts", JSONArray(item.roasts))
                    put("issues", item.issues?.let { JSONArray(it) } ?: JSONObject.NULL)
                    put("timestamp", item.timestamp)
                    put(
                        "date", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date(item.timestamp))
                    )
                }
                jsonArray.put(jsonObject)
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val file = File(context.getExternalFilesDir(null), "roast_history_$timestamp.json")
            file.writeText(jsonArray.toString(2))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
