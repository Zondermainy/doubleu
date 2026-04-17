package com.example.test_gemini

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TrainingPlansFragment : Fragment() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: TrainingPlanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_training_plans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val factory = NotesViewModelFactory(mainActivity.repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(NotesViewModel::class.java)

        adapter = TrainingPlanAdapter { plan ->
            showPlanDialog(plan)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_plans)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.allPlans.observe(viewLifecycleOwner) { plans ->
            adapter.submitList(plans)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_plan).setOnClickListener {
            showAddPlanDialog()
        }
    }

    private fun showAddPlanDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_plan, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_plan_name)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_plan_description)

        AlertDialog.Builder(requireContext())
            .setTitle("Новый план")
            .setView(dialogView)
            .setPositiveButton("Создать") { _, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createPlan(name, description.ifEmpty { null })
                    Toast.makeText(context, "План создан", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showPlanDialog(plan: com.example.test_gemini.data.TrainingPlanEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(plan.name)
            .setMessage(plan.description ?: "Без описания")
            .setPositiveButton("Закрыть", null)
            .show()
    }
}
