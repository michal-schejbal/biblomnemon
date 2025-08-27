package com.ginoskos.biblomnemon.ui.screens.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.koinSharedViewModel
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.ui.navigation.NavigationRoute
import com.ginoskos.biblomnemon.ui.screens.MainScreen
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout
import com.ginoskos.biblomnemon.ui.theme.components.CardComponent
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReadingActivityScreen(
    navController: NavHostController
) {
    val model: ReadingActivityViewModel = koinViewModel()
    val transfer: ReadingActivityTransferViewModel = koinSharedViewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.fetch()
    }

    MainScreen(
        navController = navController,
        title = stringResource(id = R.string.nav_activity),
        fab = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationRoute.ReadingActivityEdit) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(ThemeLayout.offset)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.nav_activity),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        ReadingActivityContent(
            state = state,
            onAdd = {
                navController.navigate(NavigationRoute.ReadingActivityEdit)
            },
            onEdit = { item ->
                transfer.put(item)
                navController.navigate(NavigationRoute.ReadingActivityEdit)
            }
        )
    }
}

@Composable
fun ReadingActivityContent(
    state: ReadingActivityUiState,
    onAdd: () -> Unit,
    onEdit: (ReadingActivity) -> Unit
) {
    when (state) {
        is ReadingActivityUiState.Loading -> LoadingComponent()
        is ReadingActivityUiState.Error -> MessageComponent(
            message = state.message,
            iconVector = Icons.Default.Clear
        )
        is ReadingActivityUiState.Empty -> EmptyState(onAdd = onAdd)
        is ReadingActivityUiState.Success -> {
            if (state.items.isEmpty()) {
                EmptyState(onAdd = onAdd)
            } else {
                ActivityList(items = state.items, onEdit = onEdit)
            }
        }
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MessageComponent(
            modifier = Modifier,
            message = stringResource(id = R.string.reading_activity_empty_title),
            iconPainter = painterResource(R.drawable.ic_nav_activity)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier.alpha(0.6f),
            onClick = onAdd
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.reading_activity_log_first)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.reading_activity_log_first))
        }
    }
}

@Composable
private fun ActivityList(
    items: List<ReadingActivity>,
    onEdit: (ReadingActivity) -> Unit
) {
    LazyColumn {
        items(items, key = { it.id ?: -1L }) { item ->
            ActivityRow(item = item, onClick = onEdit)
        }
    }
}

@Composable
private fun ActivityRow(
    item: ReadingActivity,
    onClick: (ReadingActivity) -> Unit
) {
    CardComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(item) }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp), 
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = item.book?.covers?.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                placeholder = painterResource(R.drawable.ic_book),
                error = painterResource(R.drawable.ic_book)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                // Title
                Text(
                    text = item.title ?: stringResource(R.string.reading_activity_item_default_title),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Book
                item.book?.title?.takeIf { it.isNotBlank() }?.let { bookTitle ->
                    Text(
                        text = bookTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Description
                item.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                // Duration + Pages
                Spacer(Modifier.height(4.dp))

                val properties = buildString {
                    val duration = item.toDurationReadable()
                    if (duration.isNotEmpty()) append(duration)
                    item.pagesRead?.let {
                        if (isNotEmpty()) append(" • ")
                        append(stringResource(R.string.reading_activity_pages_read, it))
                    }
                }
                if (properties.isNotEmpty()) {
                    Text(
                        text = properties,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
