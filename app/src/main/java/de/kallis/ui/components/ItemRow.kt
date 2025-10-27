package de.kallis.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import de.kallis.model.Article

@Composable
fun ItemRow(article: Article, count: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAdd,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(60.dp)
                .height(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = article.artikel, style = MaterialTheme.typography.bodyLarge)
            if (count > 0) {
                Text("x$count (%.2f €)".format(count * article.preis))
            }
        }

        Button(
            onClick = onRemove,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(60.dp)
                .height(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("-", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
