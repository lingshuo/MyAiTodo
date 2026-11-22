package com.lingshuo.myaitodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lingshuo.myaitodo.ui.theme.MyAiTodoTheme
import com.lingshuo.myaitodo.ui.todos.TodosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAiTodoTheme {
                TodosScreen()
            }
        }
    }
}
