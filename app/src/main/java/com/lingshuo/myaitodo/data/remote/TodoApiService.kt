package com.lingshuo.myaitodo.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit description of the jsonplaceholder todo endpoints.
 *
 * All functions are `suspend`, so Retrofit dispatches them on its own
 * background executor and resumes the caller — no callbacks needed.
 */
interface TodoApiService {

    /** Fetches up to [limit] todos. Defaults to 10 to keep payloads small. */
    @GET("todos")
    suspend fun getTodos(@Query("_limit") limit: Int = 10): List<TodoDto>
}
