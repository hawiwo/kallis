package de.kallis

import de.kallis.work.SalesSyncWorker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.kallis.model.SalesDatabase
import de.kallis.model.SalesRepository
import de.kallis.model.SettingsRepository
import de.kallis.ui.screens.MainScreen
import de.kallis.viewmodel.SalesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.work.*
import java.time.Duration
import android.app.Application


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SalesSyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sales_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        val db = SalesDatabase.getDatabase(applicationContext)
        val repo = SalesRepository(db.salesDao())

        setContent {
            val articles = remember {
                mutableStateOf(SettingsRepository.loadArticles(applicationContext))
            }

            val salesViewModel: SalesViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SalesViewModel(repo) as T
                    }
                }
            )

            MaterialTheme {
                MainScreen(
                    articles = articles.value,
                    onReload = {
                        articles.value = SettingsRepository.loadArticles(applicationContext)
                    }
                )
            }
        }
    }
}
