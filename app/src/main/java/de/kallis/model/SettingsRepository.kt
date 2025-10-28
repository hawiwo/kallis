package de.kallis.model

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object SettingsRepository {
    private const val FILE_NAME = "settings.json"
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun loadArticles(context: Context): List<Article> {
        ensureLocalCopy(context)
        val file = File(context.filesDir, FILE_NAME)
        return json.decodeFromString(file.readText())
    }

    fun saveArticles(context: Context, articles: List<Article>) {
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json.encodeToString(articles))
    }
    fun ensureLocalCopy(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            val assetData = context.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
            file.writeText(assetData)
        }
    }

}
