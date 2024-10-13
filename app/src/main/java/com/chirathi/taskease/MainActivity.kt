package com.chirathi.taskease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.chirathi.taskease.database.TaskDatabase
import com.chirathi.taskease.repository.TaskRepository
import com.chirathi.taskease.viewmodel.TaskViewModel
import com.chirathi.taskease.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var  taskViewModel: TaskViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
    }

    private fun setupViewModel(){
        val taskRepository = TaskRepository(TaskDatabase(this))
        val viewModelProviderFactory = TaskViewModelFactory(application,taskRepository)
        taskViewModel = ViewModelProvider(this,viewModelProviderFactory)[TaskViewModel::class.java]
    }


}