package com.chirathi.taskease.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.chirathi.taskease.MainActivity
import com.chirathi.taskease.R
import com.chirathi.taskease.databinding.FragmentEditTaskBinding
import com.chirathi.taskease.viewmodel.TaskViewModel
import com.chirathi.taskease.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditTaskFragment : Fragment(R.layout.fragment_edit_task), MenuProvider,
    DatePickerDialog.OnDateSetListener {

    private var editTaskBinding: FragmentEditTaskBinding? = null
    private val binding get() = this.editTaskBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var currentTask: Task
    private var selectedDate: Date? = null

    private val args: EditTaskFragmentArgs by this.navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.editTaskBinding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        currentTask = args.task!!
        selectedDate = currentTask.date

        binding.tvTaskDate.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        binding.editTaskTitle.setText(currentTask.taskName)
        binding.editTaskDesc.setText(currentTask.taskDes)

        // Populate priority Spinner
        val priorityOptions = listOf("High", "Medium", "Low")
        val priorityAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        binding.editSpinnerPriority.adapter = priorityAdapter

        // Set selected priority
        val taskPriority = when (currentTask.taskPriority) {
            Task.Priority.HIGH -> "High"
            Task.Priority.MEDIUM -> "Medium"
            Task.Priority.LOW -> "Low"
            else -> Task.Priority.MEDIUM // Default to Medium if unknown priority selected
        }
        val selectedPriorityIndex = priorityOptions.indexOf(taskPriority)
        binding.editSpinnerPriority.setSelection(selectedPriorityIndex)

        binding.editTaskFab.setOnClickListener {
            val taskName = binding.editTaskTitle.text.toString().trim()
            val taskDesc = binding.editTaskDesc.text.toString().trim()
            val taskPriorityString = binding.editSpinnerPriority.selectedItem.toString()

            // Convert the selected priority string into a Task.Priority enum value
            val taskPriority = when (taskPriorityString) {
                "High" -> Task.Priority.HIGH
                "Medium" -> Task.Priority.MEDIUM
                "Low" -> Task.Priority.LOW
                else -> Task.Priority.MEDIUM // Default to Medium if unknown priority selected
            }

            if (taskName.isNotEmpty() || selectedDate != null) {
                val task = Task(currentTask.id, taskName, taskDesc, selectedDate!!, taskPriority)
                tasksViewModel.updateTask(task)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvTaskDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this@EditTaskFragment,
            currentYear,
            currentMonth,
            currentDay
        )

        // Set minimum date to current date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(Calendar.YEAR, year)
        selectedCalendar.set(Calendar.MONTH, month)
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        selectedDate = selectedCalendar.time

        // Now you can use the selected date as needed, for example:
        // Display it in a TextView
        binding.tvTaskDate.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)

    }

    private fun deleteTask() {

        AlertDialog.Builder(this.activity).apply {
            this.setTitle("Delete Task")
            this.setMessage("Do you want to delete this task?")
            this.setPositiveButton("Delete") { _, _ ->
                this@EditTaskFragment.tasksViewModel.deleteTask(this@EditTaskFragment.currentTask)
                Toast.makeText(this.context, "Task Deleted", Toast.LENGTH_SHORT).show()
                this@EditTaskFragment.view?.findNavController()
                    ?.popBackStack(R.id.homeFragment, false)

            }
            this.setNegativeButton("Cancel", null)
        }.create().show()
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_task, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                this.deleteTask()
                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.editTaskBinding = null
    }
}