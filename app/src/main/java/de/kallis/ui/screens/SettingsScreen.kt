@file:OptIn(ExperimentalMaterial3Api::class)

package de.kallis.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.kallis.model.Article
import de.kallis.model.SettingsRepository

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val articles = remember {
        mutableStateListOf<Article>().apply {
            addAll(SettingsRepository.loadArticles(context))
        }
    }

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedFloatingActionButton(
                    onClick = onBack
                ) {
                    Text("Abbrechen")
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        SettingsRepository.saveArticles(context, articles)
                        Toast.makeText(context, "Artikel gespeichert", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                ) {
                    Text("Speichern")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        "Artikel bearbeiten",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(
                    items = articles,
                    key = { index, _ -> index }
                ) { idx, a ->
                    ArticleEditorRow(
                        article = a,
                        onChange = { updated ->
                            articles[idx] = updated
                        },
                        onDelete = {
                            if (idx in articles.indices) {
                                articles.removeAt(idx)
                            }
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    ExtendedFloatingActionButton(
                        onClick = {
                            articles.add(
                                Article(
                                    artikel = "Neuer Artikel",
                                    preis = 0.0,
                                    bestand = 0
                                )
                            )
                        },
                        text = { Text("Artikel hinzufügen") },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    )
}

@Composable
private fun ArticleEditorRow(
    article: Article,
    onChange: (Article) -> Unit,
    onDelete: () -> Unit
) {
    var name by rememberSaveable(article.artikel) { mutableStateOf(article.artikel) }
    var preis by rememberSaveable(article.artikel) { mutableStateOf(article.preis.toString()) }
    var bestand by rememberSaveable(article.artikel) { mutableStateOf(article.bestand.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onChange(article.copy(artikel = it))
                    },
                    label = { Text("Artikelname") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Löschen")
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = preis,
                onValueChange = {
                    preis = it
                    it.toDoubleOrNull()?.let { v -> onChange(article.copy(preis = v)) }
                },
                label = { Text("Preis (€)") },
                singleLine = true
            )

            OutlinedTextField(
                value = bestand,
                onValueChange = {
                    bestand = it
                    it.toIntOrNull()?.let { v -> onChange(article.copy(bestand = v)) }
                },
                label = { Text("Bestand") },
                singleLine = true
            )
        }
    }
}
