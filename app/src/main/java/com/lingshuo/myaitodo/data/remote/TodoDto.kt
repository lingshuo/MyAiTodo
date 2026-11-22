package com.lingshuo.myaitodo.data.remote

import kotlinx.serialization.Serializable

/**
 * One todo item as returned by `GET /todos` on jsonplaceholder.typicode.com.
 *
 * Wire shape:
 * ```
 * { "userId": 1, "id": 1, "title": "delectus aut autem", "completed": false }
 * ```
 */
@Serializable
data class TodoDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean,
)
