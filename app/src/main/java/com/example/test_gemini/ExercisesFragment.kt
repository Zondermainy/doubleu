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

class ExercisesFragment : Fragment() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val factory = NotesViewModelFactory(mainActivity.repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(NotesViewModel::class.java)

        adapter = ExerciseAdapter(
            onItemClick = { exercise -> showExerciseDialog(exercise) },
            onDeleteClick = { exercise -> showDeleteConfirmation(exercise) }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_exercises)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.allExercises.observe(viewLifecycleOwner) { exercises ->
            adapter.submitList(exercises)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_add_exercise).setOnClickListener {
            showAddExerciseDialog()
        }
    }

    private fun showAddExerciseDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_exercise, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_description)
        val etMuscleGroup = dialogView.findViewById<EditText>(R.id.et_muscle_group)

        AlertDialog.Builder(requireContext())
            .setTitle("Новое упражнение")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val muscleGroup = etMuscleGroup.text.toString().trim()
                if (name.isNotEmpty() && muscleGroup.isNotEmpty()) {
                    viewModel.addExercise(name, description, muscleGroup)
                    Toast.makeText(context, "Упражнение добавлено", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showExerciseDialog(exercise: com.example.test_gemini.data.ExerciseEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(exercise.name)
            .setMessage("${exercise.muscleGroup}\n\n${exercise.description}")
            .setPositiveButton("Закрыть", null)
            .show()
    }

    private fun showDeleteConfirmation(exercise: com.example.test_gemini.data.ExerciseEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить упражнение")
            .setMessage("Вы уверены, что хотите удалить \"${exercise.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteExercise(exercise)
                Toast.makeText(context, "Упражнение удалено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
