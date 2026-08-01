package com.carjam.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val onToggle: (Todo) -> Unit, private val onDelete: (Todo) -> Unit) :
    ListAdapter<Todo, TodoAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem == newItem
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val text: TextView = itemView.findViewById(R.id.text)
        val delete: ImageButton = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = getItem(position)
        holder.text.text = t.text
        holder.checkbox.isChecked = t.completed
        holder.text.alpha = if (t.completed) 0.6f else 1f

        holder.checkbox.setOnClickListener { onToggle(t) }
        holder.delete.setOnClickListener { onDelete(t) }

        holder.itemView.setOnLongClickListener {
            // allow long press to toggle too
            onToggle(t)
            true
        }
    }
}
