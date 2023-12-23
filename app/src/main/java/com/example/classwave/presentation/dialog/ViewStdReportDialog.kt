package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogViewStdReportBinding
import com.example.classwave.presentation.page.teacher.StudentReportAdapter
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ViewStdReportDialog(private val stdId: String) :
    DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogViewStdReportBinding? = null
    private val binding get() = _binding!!
    private var studentReportAdapter = StudentReportAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        _binding = DialogViewStdReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val layoutManager = FlexboxLayoutManager(context)
        initializeFlowCollectors()
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        viewModel.fetchStudentReport(stdId)
        binding.recyclerViewReportDetails.layoutManager = layoutManager
        binding.recyclerViewReportDetails.adapter = studentReportAdapter
    }
    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.markList.collectLatest {mark ->
                    if (mark != null) {
                        mark.data?.let { studentReportAdapter.setMarks(it) }
                    } else {
                        binding.toolbar.title = "No Class"
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.markList.collectLatest {mark ->
                    if (mark != null) {
                       val totalPos =  mark.data?.let { viewModel.getTotalPosMarks(it) }
                        val posAchived=mark.data?.let { viewModel.getTotalAchivedPosMarks(it) }

                        val totalNeg =  mark.data?.let { viewModel.getTotalNegMarks(it) }
                        val negAchived=mark.data?.let { viewModel.getTotalAchivedNegMarks(it)}
                        binding.textViewPositive.text = "Positive ${posAchived}/${totalPos}"
                        binding.textViewNegative.text = "Negative -${negAchived}/-${totalNeg}"

                   /*     binding.progressBar.progress = viewModel.getProgress(totalNeg,totalPos,negAchived,posAchived)*/
                    }
                }
            }
        }


    }

        private fun registerListener() {
        /*    binding.imgCancel.setOnClickListener {
                dismiss()
            }
            binding.editTxtClassName.addTextChangedListener {
                binding.btnCreateClass.isClickable = true
                binding.btnCreateClass.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            }*/
   /*     binding.btnAddSkill.setOnClickListener {
            val skillName = binding.editTextAddSkillName.text.toString()
            val hScore = binding.editTextAddSkillHighScoreName.text.toString()
            viewModel.createSkill(clsId, skillName, hScore, typeofSkill)
            dismiss()

        }*/
    }

    companion object {
        const val TAG = "viewStdDialog"
    }
}