package com.lingshuo.myaitodo.ui.todos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lingshuo.myaitodo.data.remote.TodoDto
import com.lingshuo.myaitodo.ui.theme.MyAiTodoTheme

/** Caps the content column so it stays readable on tablets and foldables. */
private val MaxContentWidth = 640.dp

/**
 * Todos screen: fetches the first 10 todos from jsonplaceholder and renders
 * the loading / success / error states.
 */
@Composable
fun TodosScreen(
    modifier: Modifier = Modifier,
    viewModel: TodosViewModel = viewModel(factory = TodosViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodosScreenContent(
        uiState = uiState,
        onRefresh = viewModel::retry,
        modifier = modifier,
    )
}

/**
 * Stateless body of the screen, so every state is previewable and testable.
 */
@Composable
fun TodosScreenContent(
    uiState: TodosUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Todos", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh todos",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            // Below the cap we keep normal screen padding; above it the centered
            // 640dp column provides its own breathing room.
            val horizontalPadding = if (maxWidth >= MaxContentWidth) 0.dp else 16.dp
            Box(
                modifier = Modifier
                    .widthIn(max = MaxContentWidth)
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
            ) {
                when (uiState) {
                    is TodosUiState.Loading -> LoadingState()

                    is TodosUiState.Success ->
                        if (uiState.todos.isEmpty()) {
                            EmptyState()
                        } else {
                            TodosList(todos = uiState.todos)
                        }

                    is TodosUiState.Error -> ErrorState(
                        message = uiState.message,
                        onRetry = onRefresh,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Loading todos" },
            strokeWidth = 4.dp,
        )
        Text(
            text = "Loading todos…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TodosList(
    todos: List<TodoDto>,
    modifier: Modifier = Modifier,
) {
    val remaining = todos.count { !it.completed }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header") {
            Text(
                text = "${todos.size} todos · $remaining remaining",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }

        items(items = todos, key = { it.id }) { todo ->
            TodoRow(todo = todo)
        }
    }
}

@Composable
private fun TodoRow(
    todo: TodoDto,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.completed) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            overlineContent = {
                Text(
                    text = "Task #${todo.id} · User ${todo.userId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = {
                // Read-only indicator: this screen only displays remote data.
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier.semantics {
                        contentDescription = if (todo.completed) "Completed" else "Not completed"
                    },
                )
            },
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (todo.completed) TextDecoration.LineThrough else null,
                color = if (todo.completed) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    CenteredMessage(
        modifier = modifier,
        icon = Icons.Rounded.TaskAlt,
        iconTint = MaterialTheme.colorScheme.primary,
        iconContentDescription = "No todos",
        title = "Nothing to do",
        body = "The server didn't return any todos. Tap refresh to try again.",
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenteredMessage(
        modifier = modifier,
        icon = Icons.Rounded.CloudOff,
        iconTint = MaterialTheme.colorScheme.error,
        iconContentDescription = "Network error",
        title = "Couldn't load todos",
        body = message,
        action = {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Retry")
            }
        },
    )
}

@Composable
private fun CenteredMessage(
    icon: ImageVector,
    iconTint: Color,
    iconContentDescription: String?,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            tint = iconTint,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        action?.invoke()
    }
}

// --- Previews -------------------------------------------------------------

private val SampleTodos = listOf(
    TodoDto(userId = 1, id = 1, title = "delectus aut autem", completed = false),
    TodoDto(userId = 1, id = 2, title = "quis ut nam facilis et officia qui", completed = true),
    TodoDto(userId = 1, id = 3, title = "fugiat veniam minus", completed = false),
    TodoDto(userId = 1, id = 4, title = "et porro tempora", completed = true),
    TodoDto(userId = 1, id = 5, title = "laboriosam mollitia et enim quasi adipisci", completed = false),
)

@Preview(name = "Todos · Loading", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun TodosScreenLoadingPreview() {
    MyAiTodoTheme(dynamicColor = false) {
        TodosScreenContent(uiState = TodosUiState.Loading, onRefresh = {})
    }
}

@Preview(name = "Todos · Success", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun TodosScreenSuccessPreview() {
    MyAiTodoTheme(dynamicColor = false) {
        TodosScreenContent(uiState = TodosUiState.Success(SampleTodos), onRefresh = {})
    }
}

@Preview(name = "Todos · Error", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun TodosScreenErrorPreview() {
    MyAiTodoTheme(dynamicColor = false) {
        TodosScreenContent(
            uiState = TodosUiState.Error("Could not reach the server. Check your connection."),
            onRefresh = {},
        )
    }
}

@Preview(name = "Todos · Empty", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun TodosScreenEmptyPreview() {
    MyAiTodoTheme(dynamicColor = false) {
        TodosScreenContent(uiState = TodosUiState.Success(emptyList()), onRefresh = {})
    }
}
