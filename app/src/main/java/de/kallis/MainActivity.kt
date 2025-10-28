package de.kallis

import de.kallis.work.SalesSyncWorker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
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
import de.kallis.viewmodel.SalesViewModelFactory
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import de.kallis.model.Article
import de.kallis.ui.theme.KallisTheme

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
            var articles by remember { mutableStateOf(emptyList<Article>()) }

            LaunchedEffect(Unit) {
                articles = withContext(Dispatchers.IO) {
                    SettingsRepository.loadArticles(applicationContext)
                }
            }

            val salesViewModel: SalesViewModel = viewModel(
                factory = SalesViewModelFactory(repo)
            )

            KallisTheme {
                MainScreen(
                    articles = articles,
                    onReload = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val newArticles = SettingsRepository.loadArticles(applicationContext)
                            withContext(Dispatchers.Main) {
                                articles = newArticles
                            }
                        }
                    }
                )
            }
        }

    }
}
