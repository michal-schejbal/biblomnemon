package com.ginoskos.biblomnemon.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.koinSharedViewModel
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookCoverCoverType
import com.ginoskos.biblomnemon.ui.navigation.NavigationRoute
import com.ginoskos.biblomnemon.ui.navigation.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.navigateBack
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper
import com.ginoskos.biblomnemon.ui.screens.common.CollapsingScreen
import com.ginoskos.biblomnemon.ui.screens.library.BookTransferViewModel
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchDetailScreen(navController: NavHostController) {
    val model: SearchDetailViewModel = koinViewModel()
    val transfer: BookTransferViewModel = koinSharedViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    val imageTopHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.35f)

    LaunchedEffect(transfer) {
        transfer.pop { item ->
            model.fetch(item)
        }
    }

    CollapsingScreen(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageTopHeight - 20.dp)
            ) {
                ScreenToolbar(onBack = { navController.navigateBack() })
            }
        },
        background = {
            val state = uiState as? SearchDetailUiState.Success
            if (state != null) with(state) {
                AsyncImage(
                    model = item?.covers?.getOrNull(BookCoverCoverType.MEDIUM.ordinal)
                        ?: item?.covers?.getOrNull(BookCoverCoverType.SMALL.ordinal),
                    contentDescription = item?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageTopHeight)
                )
            }
        }
    ) {
        SearchDetailScreenContent(
            uiState = uiState,
            onAdd = { item ->
                transfer.put(item)
                navController.navigate(NavigationRoute.LibraryEdit)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDetailScreenContent(
    uiState: SearchDetailUiState,
    onAdd: (Book) -> Unit = {}
) {
    when (uiState) {
        is SearchDetailUiState.Empty -> {
            MessageComponent(
                message = stringResource(id = R.string.search_detail_empty)
            )
        }

        is SearchDetailUiState.Loading -> {
            LoadingComponent()
        }

        is SearchDetailUiState.Error -> {
            MessageComponent(
                message = stringResource(id = R.string.search_detail_error_message, uiState.message)
            )
        }

        is SearchDetailUiState.Success -> with(uiState) {
            if (item == null) {
                MessageComponent(
                    message = stringResource(id = R.string.search_detail_not_found)
                )
            } else {
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(item.title ?: "", style = MaterialTheme.typography.displayLarge)
                    item.authors?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = it.joinToString(" · ") { a -> a.name },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    item.publishYear?.let {
                        Text(stringResource(id = R.string.search_detail_published, it), style = MaterialTheme.typography.bodySmall)
                    }
                    item.publisher?.let {
                        Text(stringResource(id = R.string.search_detail_publisher, it), style = MaterialTheme.typography.bodySmall)
                    }
                    item.isbn?.let {
                        Text(stringResource(id = R.string.search_detail_isbn, it), style = MaterialTheme.typography.bodySmall)
                    }

                    if (!item.description.isNullOrBlank()) {
                        Text(item.description, style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(
                        onClick = { onAdd(item) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.search_detail_add_content_desc)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.search_detail_add_to_library))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchDetailScreen() {
    BiblomnemonTheme {
        ScreenWrapper {
            SearchDetailScreenContent(
                uiState = SearchDetailUiState.Success(
                    item = Book(
                        id = "1",
                        title = "A Brief History of Time",
                        authors = listOf(Author(name = "Stephen Hawking")),
                        description = "An overview of cosmology and black holes.",
                        publishYear = 1988
                    )
                )
            )
        }
    }
}