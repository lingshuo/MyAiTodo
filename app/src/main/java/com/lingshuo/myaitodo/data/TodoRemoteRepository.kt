package com.lingshuo.myaitodo.data

import com.lingshuo.myaitodo.data.remote.TodoApiService
import com.lingshuo.myaitodo.data.remote.TodoDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/**
 * Network source for todos coming from jsonplaceholder.
 *
 * Failures are returned as [Result.failure] rather than thrown, so the ViewModel
 * can turn them into a UI state without try/catch noise.
 */
interface TodoRemoteRepository {

    /**
     * Fetches up to [limit] todos.
     *
     * Never throws: failures are wrapped in [Result.failure] carrying a
     * user-presentable message. Cancellation is rethrown untouched.
     */
    suspend fun fetchTodos(limit: Int = 10): Result<List<TodoDto>>
}

/**
 * Retrofit-backed implementation of [TodoRemoteRepository].
 */
class DefaultTodoRemoteRepository(
    private val apiService: TodoApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TodoRemoteRepository {

    override suspend fun fetchTodos(limit: Int): Result<List<TodoDto>> =
        withContext(ioDispatcher) {
            try {
                // Retrofit already runs the call off the main thread; switching
                // here as well keeps any future synchronous work off the UI too.
                Result.success(apiService.getTodos(limit))
            } catch (cancellation: CancellationException) {
                // Structured concurrency: never swallow cancellation.
                throw cancellation
            } catch (io: IOException) {
                // No connectivity, DNS failure, timeout, ...
                Result.failure(
                    TodoFetchException(
                        message = "Could not reach the server. Check your connection.",
                        cause = io,
                    ),
                )
            } catch (http: HttpException) {
                Result.failure(
                    TodoFetchException(
                        message = "Server returned an error (HTTP ${http.code()}).",
                        cause = http,
                    ),
                )
            } catch (serialization: SerializationException) {
                Result.failure(
                    TodoFetchException(
                        message = "Received an unexpected response from the server.",
                        cause = serialization,
                    ),
                )
            } catch (unexpected: Exception) {
                Result.failure(
                    TodoFetchException(
                        message = "Something went wrong while loading todos.",
                        cause = unexpected,
                    ),
                )
            }
        }
}

/** A network failure with a message that is safe to show in the UI. */
class TodoFetchException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
