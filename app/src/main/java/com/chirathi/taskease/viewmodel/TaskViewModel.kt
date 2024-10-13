package com.chirathi.taskease.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chirathi.taskease.model.Task
import com.chirathi.taskease.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app: Application, private  val taskRepository: TaskRepository):AndroidViewModel(app) {


    fun addTask(task: Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
    }

    fun getAllNotes() = taskRepository.getAllTasks()

    fun searchTask(query: String?) = taskRepository.searchTask(query)

    fun filterTask(query: Task.Priority?) = taskRepository.filterTask(query)
}
