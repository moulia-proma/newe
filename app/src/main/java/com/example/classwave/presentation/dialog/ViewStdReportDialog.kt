package com.example.classwave.presentation.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogViewStdReportBinding
import com.example.classwave.presentation.page.teacher.Marks
import com.example.classwave.presentation.page.teacher.StudentReportAdapterWithDate
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate


class ViewStdReportDialog(private val stdId: String) : DialogFragment() {

    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogViewStdReportBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    var date = LocalDate.now()
    private val binding get() = _binding!!
    private var studentReportAdapterWithDate = StudentReportAdapterWithDate()

    var markList: List<Marks> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogViewStdReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeFlowCollectors()
        binding.textDateFilter.text = date.toString()



        viewModel.fetchStudentReport(stdId)
        registerListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.markList.collectLatest { mark ->
                    if (mark != null) {
                        val totalPos = mark.data?.let { viewModel.getTotalPosMarks(it) }
                        val posAchived = mark.data?.let { viewModel.getTotalAchivedPosMarks(it) }

                        val totalNeg = mark.data?.let { viewModel.getTotalNegMarks(it) }
                        val negAchived = mark.data?.let { viewModel.getTotalAchivedNegMarks(it) }

                        binding.textViewPositive.text = "Positive ${posAchived}/${totalPos}"
                        binding.textViewNegative.text = "Negative -${negAchived}/-${totalNeg}"

                        var progress =
                            viewModel.getProgress(totalNeg, totalPos, negAchived, posAchived)

                        binding.progressBar.progress = progress
                        binding.textMarksInPercent.text = "${progress.toString()}%"

                        mark.data?.let {
                            markList = it
                        }
                        studentReportAdapterWithDate.setMarks(markList, requireContext(),date)
                        val layoutManager = FlexboxLayoutManager(context)
                        layoutManager.flexDirection = FlexDirection.ROW
                        layoutManager.justifyContent = JustifyContent.FLEX_START

                        binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
                        binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate
                        studentReportAdapterWithDate.setMarks(markList, requireContext(),date)
                    } else {
                        binding.toolbar.title = "No Class"
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerListener() {

        binding.cardDayReport.setOnClickListener {

            studentReportAdapterWithDate.setMarks(markList, requireContext(),date)
        }
        binding.imageViewBackArrow.setOnClickListener {

            date = date.minusDays(1)

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START

            binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
            binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate
            studentReportAdapterWithDate.setMarks(markList, requireContext(),date)

            binding.textDateFilter.text = date.toString()
        }
        binding.imageViewFrontArrow.setOnClickListener {
            date = date.plusDays(1)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START

            binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
            binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate
            studentReportAdapterWithDate.setMarks(markList, requireContext(),date)

            binding.textDateFilter.text = date.toString()
            Log.d(TAG, "registerListener haha: ${date.minusDays(1)}")
        }
    }

    companion object {
        const val TAG = "viewStdDialog"
    }
}