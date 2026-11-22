package com.lingshuo.myaitodo.ui.todos

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lingshuo.myaitodo.MyAiTodoApplication
import com.lingshuo.myaitodo.data.TodoRemoteRepository
import com.lingshuo.myaitodo.data.remote.TodoDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Everything the Todos screen can render.
 */
sealed interface TodosUiState {

    /** The request is in flight (initial load or a retry). */
    data object Loading : TodosUiState

    /** Todos were fetched successfully. May be empty. */
    data class Success(val todos: List<TodoDto>) : TodosUiState

    /** The request failed; [message] is safe to show in the UI. */
    data class Error(val message: String) : TodosUiState
}

/**
 * Holds the network todos for the Todos screen.
 *
 * The in-flight job is tracked so a double tap on refresh (or a fast retry)
 * cannot start overlapping requests; a new load always cancels the previous one.
 */
class TodosViewModel(
    private val repository: TodoRemoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodosUiState>(TodosUiState.Loading)
    val uiState: StateFlow<TodosUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadTodos()
    }

    /**
     * (Re)loads the first [limit] todos.
     *
     * No-op when a load is already running, so the refresh button and a retry
     * button tap cannot stack up duplicate requests.
     */
    fun loadTodos(limit: Int = DEFAULT_LIMIT) {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.value = TodosUiState.Loading
            val result = repository.fetchTodos(limit)
            _uiState.value = result.fold(
                onSuccess = { TodosUiState.Success(it) },
                onFailure = { TodosUiState.Error(it.message ?: FALLBACK_ERROR) },
            )
        }
    }

    /** Convenience alias used by the error state's Retry button. */
    fun retry() = loadTodos()

    companion object {
        const val DEFAULT_LIMIT = 10
        private const val FALLBACK_ERROR = "Couldn't load todos. Please try again."

        /**
         * Builds a [ViewModelProvider.Factory] that pulls [TodoRemoteRepository]
         * out of the application's manual [com.lingshuo.myaitodo.AppContainer].
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val container = (application as MyAiTodoApplication).container
                TodosViewModel(container.todoRemoteRepository)
            }
        }
    }
}
