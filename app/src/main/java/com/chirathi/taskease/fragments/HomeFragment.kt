package com.chirathi.taskease.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chirathi.taskease.MainActivity
import com.chirathi.taskease.R
import com.chirathi.taskease.adapter.TaskAdapter
import com.chirathi.taskease.databinding.FragmentHomeBinding
import com.chirathi.taskease.model.Task
import com.chirathi.taskease.viewmodel.TaskViewModel




class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener,
    MenuProvider {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        setupHomeRecyclerView()

        binding.addTaskFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
    }

    private fun updateUI(task: List<Task>?) {
        if (task != null) {
            if (task.isNotEmpty()) {
                binding.emptyTasksImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
                binding.layoutFilter.visibility = View.VISIBLE
            } else {
                binding.emptyTasksImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
                binding.layoutFilter.visibility = View.GONE
            }
        }
    }

    private fun setupHomeRecyclerView() {
        taskAdapter = TaskAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = taskAdapter
        }
        showAllTask()

        val priorityOptions = listOf("All", "High", "Medium", "Low")
        val priorityAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        binding.spinnerPriority.adapter = priorityAdapter
        binding.spinnerPriority.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                filterTask()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })
    }

    private fun showAllTask() {
        tasksViewModel.getAllNotes().observe(viewLifecycleOwner) { tasks ->
            taskAdapter.differ.submitList(tasks)
            updateUI(tasks)
        }
    }

    private fun searchTask(query: String?) {
        val searchQuery = "%$query%"

        tasksViewModel.searchTask(searchQuery).observe(viewLifecycleOwner) { list ->
            taskAdapter.differ.submitList(list)
        }
    }

    private fun filterTask() {
        val taskPriority = when (binding.spinnerPriority.selectedItem.toString()) {
            "High" -> Task.Priority.HIGH
            "Medium" -> Task.Priority.MEDIUM
            "Low" -> Task.Priority.LOW
            else -> null
        }
        if (taskPriority != null) {
            tasksViewModel.filterTask(taskPriority).observe(viewLifecycleOwner) { list ->
                taskAdapter.differ.submitList(list)
            }
        } else {
            showAllTask()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchTask(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}
