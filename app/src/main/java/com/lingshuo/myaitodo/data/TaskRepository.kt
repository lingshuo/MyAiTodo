package com.lingshuo.myaitodo.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for to-do data.
 *
 * Every write stamps `updatedAt` so callers can stay ignorant of bookkeeping.
 */
interface TaskRepository {

    /** Live stream of all tasks, pending first then newest first. */
    val tasks: Flow<List<TaskEntity>>

    /** Inserts a new task and returns its generated id. */
    suspend fun addTask(title: String, note: String = ""): Long

    /** Persists any user edits to [task] (title / note / isDone). */
    suspend fun updateTask(task: TaskEntity)

    /** Flips the done flag of [task] and persists the change. */
    suspend fun toggleDone(task: TaskEntity)

    /** Permanently removes [task]. */
    suspend fun deleteTask(task: TaskEntity)

    /** Returns the task with [id], or `null` when it no longer exists. */
    suspend fun getTask(id: Long): TaskEntity?
}

/**
 * Room-backed implementation of [TaskRepository].
 */
class DefaultTaskRepository(
    private val taskDao: TaskDao,
) : TaskRepository {

    override val tasks: Flow<List<TaskEntity>> = taskDao.observeAll()

    override suspend fun addTask(title: String, note: String): Long {
        val now = System.currentTimeMillis()
        return taskDao.insert(
            TaskEntity(
                title = title.trim(),
                note = note.trim(),
                isDone = false,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.update(task.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun toggleDone(task: TaskEntity) {
        taskDao.update(
            task.copy(
                isDone = !task.isDone,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }

    override suspend fun getTask(id: Long): TaskEntity? = taskDao.getById(id)
}
