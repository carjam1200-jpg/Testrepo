package com.carjam.todo

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup DB/repo/viewmodel
        val dao = TodoApp.db.todoDao()
        val repo = TodoRepository(dao)
        val factory = TodoViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        val input = findViewById<EditText>(R.id.todo_input)
        val addBtn = findViewById<Button>(R.id.add_btn)
        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        val count = findViewById<TextView>(R.id.count)
        val clear = findViewById<Button>(R.id.clear_completed)

        adapter = TodoAdapter(onToggle = { viewModel.toggle(it) }, onDelete = { viewModel.delete(it) })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        viewModel.todos.observe(this) { list ->
            adapter.submitList(list)
            val remaining = list.count { !it.completed }
            count.text = "$remaining item${if (remaining != 1) "s" else ""} left"
        }

        addBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isNotBlank()) {
                viewModel.add(text)
                input.text.clear()
            }
        }

        input.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text.toString()
                if (text.isNotBlank()) {
                    viewModel.add(text)
                    v.text = null
                }
                true
            } else false
        }

        clear.setOnClickListener { viewModel.clearCompleted() }
    }
}
