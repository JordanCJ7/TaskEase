package com.chirathi.taskease.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chirathi.taskease.databinding.TaskLayoutBinding
import com.chirathi.taskease.fragments.HomeFragmentDirections
import com.chirathi.taskease.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val itemBinding: TaskLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = differ.currentList[position]

        holder.itemBinding.tvTaskDate.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTask.date)
        holder.itemBinding.taskName.text = currentTask.taskName
        holder.itemBinding.taskDes.text = currentTask.taskDes

        // Convert Task.Priority enum to String
        val priorityText = currentTask.taskPriority.toString()

        holder.itemBinding.taskPriority.text = priorityText

        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditTaskFragment(currentTask)
            it.findNavController().navigate(direction)
        }
    }
}
