package com.example.classwave.presentation.page.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.FragmentPositiveBinding
import com.example.classwave.presentation.dialog.AddSkillDialog
import com.example.classwave.presentation.dialog.ProvideMarksDialog
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class positiveFragment(val classId: String, val stdId: String) : Fragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: FragmentPositiveBinding? = null
    private val binding get() = _binding!!
    private var skillStudentAdapter = SkillsStudentAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPositiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.recyclerViewPositiveSkills.layoutManager = layoutManager
        binding.recyclerViewPositiveSkills.adapter = skillStudentAdapter

        skillStudentAdapter.setListener(listener = object : SkillsStudentAdapter.Listener {
            override fun onAddNewSkillClicked() {
                val dialog = AddSkillDialog(classId, "pos")
                dialog.show(parentFragmentManager, "SkillDialog")
            }

            override fun onSkillSelected(skillId: String) {
                val dialog = ProvideMarksDialog(classId, stdId, skillId)
                dialog.show(parentFragmentManager, "ProvideMarksdialog")
            }
        })

        initializeFlowCollectors()
    }
    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.posSkillList.collectLatest { skill ->
                    if (skill != null) {
                        skill.data?.let { skillStudentAdapter.setStudents(skill.data) }
                    }
                }
            }
        }
    }





}