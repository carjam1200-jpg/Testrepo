package com.carjam.todo

class TodoRepository(private val dao: TodoDao) {
    fun all() = dao.getAll()
    suspend fun insert(todo: Todo) = dao.insert(todo)
    suspend fun update(todo: Todo) = dao.update(todo)
    suspend fun delete(todo: Todo) = dao.delete(todo)
    suspend fun clearCompleted() = dao.deleteCompleted()
}
