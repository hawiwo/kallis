@file:OptIn(ExperimentalMaterial3Api::class)

package de.kallis.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.kallis.model.Article
import de.kallis.model.SaleRecordEntity
import de.kallis.ui.components.ItemRow
import de.kallis.viewmodel.SalesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(
    articles: List<Article>,
    onReload: () -> Unit
) {
    val viewModel: SalesViewModel = viewModel()
    val allSales by viewModel.allSales.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var customerList by remember { mutableStateOf(listOf<String>()) }
    val counts = remember { mutableStateMapOf<String, Int>() }

    val total = articles.sumOf { it.preis * (counts[it.artikel] ?: 0) }

    // 👉 neue API mit initialValue
    var animatedTotal by remember { mutableStateOf(total.toFloat()) }

    LaunchedEffect(total) {
        animatedTotal = total.toFloat()
    }

    val currentCustomer = customerList.lastOrNull()

    fun saveSale() {
        if (currentCustomer != null && total > 0) {
            val timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            viewModel.addSale(
                SaleRecordEntity(
                    customer = currentCustomer,
                    total = total,
                    time = timestamp
                )
            )
            counts.clear()
        }
    }

    if (showSettings) {
        SettingsScreen(
            onBack = {
                showSettings = false
                onReload()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kallis Verkauf") },
                actions = {
                    TextButton(onClick = { showSettings = true }) {
                        Text("Artikel")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text("Kunde") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = { showDialog = true }
                )
                ExtendedFloatingActionButton(
                    text = { Text("Abschließen") },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    onClick = { saveSale() }
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                if (showDialog) {
                    CustomerDialog(
                        onOk = { name ->
                            customerList = customerList + name
                            showDialog = false
                        },
                        onCancel = { showDialog = false }
                    )
                }

                if (currentCustomer != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kunde:", style = MaterialTheme.typography.titleMedium)
                            Text(currentCustomer, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                ) {
                    items(articles.size) { idx ->
                        val a = articles[idx]
                        val count = counts[a.artikel] ?: 0
                        ItemRow(
                            article = a,
                            count = count,
                            onAdd = { counts[a.artikel] = count + 1 },
                            onRemove = { if (count > 0) counts[a.artikel] = count - 1 }
                        )
                    }
                }

                Divider(thickness = 2.dp)
                Spacer(Modifier.height(8.dp))

                Text(
                    "Gesamt: %.2f €".format(animatedTotal),
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(16.dp))

                if (allSales.isNotEmpty()) {
                    Text("Verkaufs-Historie", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(allSales.size) { idx ->
                            val s = allSales[idx]
                            Text("• ${s.time} – ${s.customer}: %.2f €".format(s.total))
                        }
                    }
                }

                Spacer(Modifier.height(72.dp))
            }
        }
    )
}
