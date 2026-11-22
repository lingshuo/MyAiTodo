package com.lingshuo.myaitodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single to-do item persisted in the `tasks` table.
 *
 * [updatedAt] is always written by the repository / DAO layer so that callers
 * never have to remember to bump it manually.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val note: String = "",
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
