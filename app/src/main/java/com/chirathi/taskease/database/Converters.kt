package com.chirathi.taskease.database

import androidx.room.TypeConverter
import java.util.*

object Converters {
    @JvmStatic
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}