package com.carjam.todo

import android.app.Application
import androidx.room.Room

class TodoApp : Application() {
    companion object {
        lateinit var db: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, TodoDatabase::class.java, "todos.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
