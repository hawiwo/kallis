package de.kallis.model

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object SettingsRepository {
    private const val FILE_NAME = "settings.json"

    fun ensureLocalCopy(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            val assetData = context.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
            file.writeText(assetData)
        }
    }

    fun loadArticles(context: Context): List<Article> {
        ensureLocalCopy(context)
        val file = File(context.filesDir, FILE_NAME)
        val jsonText = file.readText()
        val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
        return json.decodeFromString(jsonText)
    }

    fun saveArticles(context: Context, articles: List<Article>) {
        val json = Json { prettyPrint = true }
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(articles))
    }
}
