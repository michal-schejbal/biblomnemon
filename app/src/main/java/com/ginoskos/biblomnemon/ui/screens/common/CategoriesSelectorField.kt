package com.ginoskos.biblomnemon.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.Category

@Composable
fun CategoriesSelectorField(
    categories: List<Category>,
    book: Book? = null,
    onChanged: (List<Category>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCategoryManager by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.library_edit_categories),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (categories.isEmpty()) {
                    Text(
                        text = stringResource(R.string.library_edit_categories_none),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FlowRow {
                        categories.forEach { category ->
                            AssistChip(
                                onClick = {},
                                label = { Text(category.title.orEmpty()) }
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { showCategoryManager = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.library_edit_categories_edit))
            }
        }
    }

    if (showCategoryManager) {
        CategoryManagerBottomSheet(
            book = book,
            onDismiss = { selectedCategories ->
                onChanged(selectedCategories)
                showCategoryManager = false
            }
        )
    }
}