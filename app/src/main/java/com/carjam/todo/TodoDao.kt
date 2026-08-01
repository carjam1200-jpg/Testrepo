package com.carjam.todo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM todos WHERE completed = 1")
    suspend fun deleteCompleted()
}
