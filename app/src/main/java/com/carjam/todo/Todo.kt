package com.carjam.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: String,
    var text: String,
    var completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
