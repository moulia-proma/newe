package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.classwave.R
import com.example.classwave.databinding.DialogSkillsBinding
import com.example.classwave.presentation.page.teacher.SkillsStudentAdapter
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SkillDialog(val classId: String, val stdId: String) : DialogFragment() {

    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogSkillsBinding? = null
    private val binding get() = _binding!!
    private var skillStudentAdapter = SkillsStudentAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "onAddNewSkillClicked: abcd ${classId}")
        _binding = DialogSkillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerViewSkills.layoutManager = layoutManager
        binding.recyclerViewSkills.adapter = skillStudentAdapter

        skillStudentAdapter.setListener(listener = object : SkillsStudentAdapter.Listener {
            override fun onAddNewSkillClicked() {
                val dialog = AddSkillDialog(classId)
                dialog.show(parentFragmentManager, "SkillDialog")
            }

            override fun onSkillSelected(skillId: String) {
                val dialog = ProvideMarksDialog(classId, stdId, skillId)
                dialog.show(parentFragmentManager, "ProvideMarksdialog")
            }
        })

        initializeFlowCollectors()
        registerListener()
    }

    private fun registerListener() {
        binding.imgCancel.setOnClickListener {
            dismiss()
        }
        /*
            binding.editTxtClassName.addTextChangedListener {
                binding.btnCreateClass.isClickable = true
                binding.btnCreateClass.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            }*/
        /*     binding.btnAddStd.setOnClickListener {
                 val studentName = binding.editTextAddStdName.text.toString()
                 viewModel.createStudent(clsId,studentName)
                 dismiss()

             }*/
    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.skillList.collectLatest { skill ->
                    if (skill != null) {
                        skill.data?.let { skillStudentAdapter.setStudents(skill.data) }
                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "ProvideMarksdialog"
    }
}