package com.ginoskos.biblomnemon.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Book

@Composable
fun BookSelectorField(
    book: Book?,
    onBookSelected: (Book?) -> Unit
) {
    var showBookPicker by remember { mutableStateOf(false) }
    val selectedBookTitle = book?.title ?: "No book selected"
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showBookPicker = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.reading_activity_book_id),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = book?.covers?.firstOrNull(),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            placeholder = painterResource(R.drawable.ic_book),
                            error = painterResource(R.drawable.ic_book)
                        )
                        Text(
                            text = selectedBookTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (book != null) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            if (book != null) {
                IconButton(
                    onClick = { onBookSelected(null) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(R.string.reading_activity_clear_book)
                    )
                }
            }
        }
    }
    
    if (showBookPicker) {
        BookPickerBottomSheet(
            book = book,
            onDismiss = { selectedBook ->
                onBookSelected(selectedBook)
                showBookPicker = false
            }
        )
    }
}