package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.FragmentNeedsWorkBinding
import com.example.classwave.presentation.dialog.AddSkillDialog
import com.example.classwave.presentation.dialog.ProvideMarksDialog
import com.example.classwave.presentation.dialog.ViewStdReportDialog
import com.example.classwave.presentation.page.report.Repo0rtActivity
import com.example.classwave.presentation.page.signup.SignUpActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NeedsWorkFragment(val classId: String, val stdId: String) : Fragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: FragmentNeedsWorkBinding? = null
    private val binding get() = _binding!!
    private var skillStudentAdapter = SkillsStudentAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNeedsWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.recyclerViewNeedsWork.layoutManager = layoutManager
        binding.recyclerViewNeedsWork.adapter = skillStudentAdapter
        skillStudentAdapter.setListener(listener = object : SkillsStudentAdapter.Listener {
            override fun onAddNewSkillClicked() {
                val dialog = AddSkillDialog(classId,"neg")
                dialog.show(parentFragmentManager, "SkillDialog")
            }


            @SuppressLint("SuspiciousIndentation")
            override fun onViewReportClicked() {
                val intent = Intent(requireContext(), Repo0rtActivity::class.java)
                intent.putExtra("student_id" , stdId)
                startActivity(intent)
            }

            override fun onSkillSelected(
                skillId: String,
                highestScore: String,
                name: String,
                img: String
            ) {
                val dialog = ProvideMarksDialog(classId, stdId, skillId,highestScore,"neg",name,img)
                dialog.show(parentFragmentManager, "ProvideMarksdialog")
            }
        })

        initializeFlowCollectors()
    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.negSkillList.collectLatest { skill ->
                    if (skill != null) {
                        skill.data?.let { skillStudentAdapter.setStudents(skill.data) }
                    }
                }
            }
        }
    }
}