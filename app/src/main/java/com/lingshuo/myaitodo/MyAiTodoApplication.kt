package com.lingshuo.myaitodo

import android.app.Application
import com.lingshuo.myaitodo.data.DefaultTaskRepository
import com.lingshuo.myaitodo.data.DefaultTodoRemoteRepository
import com.lingshuo.myaitodo.data.TaskRepository
import com.lingshuo.myaitodo.data.TodoDatabase
import com.lingshuo.myaitodo.data.TodoRemoteRepository
import com.lingshuo.myaitodo.data.remote.NetworkModule

/**
 * Application entry point that owns the app-wide manual dependency container.
 *
 * Deliberately dependency-injection-framework free: the graph is small enough
 * that a lazy container is clearer than Hilt/Koin wiring.
 */
class MyAiTodoApplication : Application() {

    /** Simple manual DI container. */
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/**
 * Lazily built dependencies, scoped to the application process.
 */
class AppContainer(application: Application) {

    private val database: TodoDatabase by lazy { TodoDatabase.getInstance(application) }

    val taskRepository: TaskRepository by lazy { DefaultTaskRepository(database.taskDao()) }

    /** Network-backed todos coming from jsonplaceholder.typicode.com. */
    val todoRemoteRepository: TodoRemoteRepository by lazy {
        DefaultTodoRemoteRepository(NetworkModule.todoApiService)
    }
}
