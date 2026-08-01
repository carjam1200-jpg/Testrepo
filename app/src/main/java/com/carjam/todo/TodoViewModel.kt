package com.carjam.todo

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TodoViewModel(private val repo: TodoRepository) : ViewModel() {
    val todos: LiveData<List<Todo>> = repo.all()

    fun add(text: String) {
        val t = Todo(id = java.util.UUID.randomUUID().toString(), text = text.trim())
        viewModelScope.launch { repo.insert(t) }
    }

    fun toggle(todo: Todo) {
        val updated = todo.copy(completed = !todo.completed)
        viewModelScope.launch { repo.update(updated) }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch { repo.delete(todo) }
    }

    fun clearCompleted() {
        viewModelScope.launch { repo.clearCompleted() }
    }
}

class TodoViewModelFactory(private val repo: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
