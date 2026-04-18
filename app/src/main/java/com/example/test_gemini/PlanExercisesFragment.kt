package com.example.test_gemini

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.ExerciseEntity
import com.example.test_gemini.data.ExerciseWithDetails
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlanExercisesFragment : Fragment() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: PlanExerciseAdapter
    private var planId: Long = -1

    companion object {
        private const val ARG_PLAN_ID = "plan_id"
        private const val ARG_PLAN_NAME = "plan_name"

        fun newInstance(planId: Long, planName: String): PlanExercisesFragment {
            return PlanExercisesFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PLAN_ID, planId)
                    putString(ARG_PLAN_NAME, planName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planId = arguments?.getLong(ARG_PLAN_ID) ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_plan_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val factory = NotesViewModelFactory(mainActivity.repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(NotesViewModel::class.java)

        viewModel.selectPlan(planId)

        adapter = PlanExerciseAdapter(
            onItemClick = { exercise -> showEditSetsRepsDialog(exercise) },
            onRemoveClick = { exercise -> showRemoveConfirmation(exercise) }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_plan_exercises)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.selectedPlanExercises.observe(viewLifecycleOwner) { exercises ->
            adapter.submitList(exercises)
            view.findViewById<TextView>(R.id.tv_empty).visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_exercise).setOnClickListener {
            showAddExerciseDialog()
        }
    }

    private fun showEditSetsRepsDialog(exercise: com.example.test_gemini.data.ExerciseWithDetails) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sets_reps, null)
        val etSets = dialogView.findViewById<android.widget.EditText>(R.id.et_sets)
        val etReps = dialogView.findViewById<android.widget.EditText>(R.id.et_reps)
        etSets.setText(exercise.sets.toString())
        etReps.setText(exercise.reps.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать: ${exercise.name}")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val sets = etSets.text.toString().toIntOrNull() ?: exercise.sets
                val reps = etReps.text.toString().toIntOrNull() ?: exercise.reps
                viewModel.updateExerciseInPlan(planId, exercise.id, sets, reps)
                Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAddExerciseDialog() {
        val exercises = viewModel.allExercises.value ?: emptyList()
        if (exercises.isEmpty()) {
            Toast.makeText(context, "Нет доступных упражнений", Toast.LENGTH_SHORT).show()
            return
        }

        val names = exercises.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Выберите упражнение")
            .setItems(names) { _, which ->
                val selected = exercises[which]
                showSetsRepsDialog(selected)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showSetsRepsDialog(exercise: ExerciseEntity) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sets_reps, null)
        val etSets = dialogView.findViewById<android.widget.EditText>(R.id.et_sets)
        val etReps = dialogView.findViewById<android.widget.EditText>(R.id.et_reps)
        etSets.setText("3")
        etReps.setText("10")

        AlertDialog.Builder(requireContext())
            .setTitle("Подходы и повторения")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val sets = etSets.text.toString().toIntOrNull() ?: 3
                val reps = etReps.text.toString().toIntOrNull() ?: 10
                viewModel.addExerciseToPlan(planId, exercise.id, sets, reps)
                Toast.makeText(context, "Упражнение добавлено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRemoveConfirmation(exercise: ExerciseWithDetails) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить упражнение")
            .setMessage("Удалить \"${exercise.name}\" из плана?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.removeExerciseFromPlan(planId, exercise.id)
                Toast.makeText(context, "Упражнение удалено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}