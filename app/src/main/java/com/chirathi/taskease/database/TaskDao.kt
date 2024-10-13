package com.chirathi.taskease.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chirathi.taskease.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE taskName LIKE :query OR taskDes LIKE :query")
    fun searchTask(query: String?): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE taskPriority = :query")
    fun filterTask(query: Task.Priority?): LiveData<List<Task>>
}