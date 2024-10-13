package com.chirathi.taskease.fragments

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
import com.chirathi.taskease.MainActivity
import com.chirathi.taskease.R
import com.chirathi.taskease.databinding.FragmentAddTaskBinding
import com.chirathi.taskease.viewmodel.TaskViewModel
import com.chirathi.taskease.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTaskFragment : Fragment(R.layout.fragment_add_task), MenuProvider,
    DatePickerDialog.OnDateSetListener {


    private var addTaskBinding: FragmentAddTaskBinding? = null
    private val binding get() = addTaskBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var addTaskView: View
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTaskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        addTaskView = view

        // Populate priority Spinner
        val priorityOptions = listOf("High", "Medium", "Low")
        val priorityAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        binding.addSpinnerPriority.adapter = priorityAdapter
        binding.tvTaskDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun saveTask(view: View) {
        val taskName = binding.addTaskTitle.text.toString().trim()
        val taskDes = binding.addTaskDesc.text.toString().trim()

        // Retrieve the selected priority from the Spinner
        val selectedPriority = binding.addSpinnerPriority.selectedItem.toString()
        val taskPriority = when (selectedPriority) {
            "High" -> Task.Priority.HIGH
            "Medium" -> Task.Priority.MEDIUM
            "Low" -> Task.Priority.LOW
            else -> Task.Priority.MEDIUM // Default to Medium if unknown priority selected
        }

        if (taskName.isNotEmpty() || selectedDate != null) {
            val task = Task(0, taskName, taskDes, selectedDate!!, taskPriority)
            tasksViewModel.addTask(task)
            Toast.makeText(addTaskView.context, "Task Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addTaskView.context, "Please enter all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this@AddTaskFragment,
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_task, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveTask(addTaskView)
                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addTaskBinding = null
    }

}