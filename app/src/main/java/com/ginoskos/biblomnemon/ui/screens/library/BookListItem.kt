package com.ginoskos.biblomnemon.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.CardComponent

@Composable
fun BookListItem(item: Book, onClick: () -> Unit = {}) {
    CardComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = item.covers?.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                placeholder = painterResource(R.drawable.ic_book),
                error = painterResource(R.drawable.ic_book)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(item.title!!, style = MaterialTheme.typography.bodyLarge)
                item.authors?.let {
                    Text(
                        text = item.authors.joinToString(separator = " · ") { it.name },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                item.publishYear?.let { year ->
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookListItemPreview() {
    BiblomnemonTheme {
        BookListItem(
            item = Book(
                id = "1",
                title = "The Elegant Universe",
                authors = listOf(Author(name = "Brian Greene"), Author(name = "Brian Greene")),
                publishYear = 1999
            )
        )
    }
}