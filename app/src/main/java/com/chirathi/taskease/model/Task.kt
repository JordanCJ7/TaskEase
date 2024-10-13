package com.chirathi.taskease.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val taskName: String,
    val taskDes: String,
    val date: Date,
    val taskPriority: Priority // Change type to Priority enum
): Parcelable {

    // Define priority levels as an enum inside the Task class
    enum class Priority {
        HIGH,
        MEDIUM,
        LOW
    }
}
