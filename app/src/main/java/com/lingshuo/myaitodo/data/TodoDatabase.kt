package com.lingshuo.myaitodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        private const val DATABASE_NAME = "my_ai_todo.db"

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        /** Returns the process-wide database instance, creating it on first access. */
        fun getInstance(context: Context): TodoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): TodoDatabase =
            Room.databaseBuilder(
                context,
                TodoDatabase::class.java,
                DATABASE_NAME,
            ).build()
    }
}
