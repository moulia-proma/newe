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
    var date: ArrayList<LocalDate> = arrayListOf(LocalDate.now())
    private val binding get() = _binding!!
    private var studentReportAdapterWithDate = StudentReportAdapterWithDate()
    private var markList: List<Marks> = listOf()

    @RequiresApi(Build.VERSION_CODES.O)
    private var dateListSize = date.size


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
        dayUpdate()
        initializeFlowCollectors()

        registerListener()
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
        binding.recyclerViewReportDetailsWithDate.adapter =
            studentReportAdapterWithDate

        // viewModel.fetchMarksByDay(stdId,date.toString())
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


                        if (markList.isNotEmpty()) {
                            studentReportAdapterWithDate.setMarks(
                                markList,
                                requireContext(),
                                date,
                                dateListSize
                            )

                        } else {
                            studentReportAdapterWithDate.setMarks(
                                markList,
                                requireContext(),
                                date,
                                0
                            )

                        }

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
            dayUpdate()
        }

        binding.cardMonthReport.setOnClickListener {
            monthUpdate()
        }

    }

    private fun adapterAndLayoutSet() {
        Log.d("kk", "adapterAndLayoutSet: ")
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
        binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate
    }

    private fun childAdapterAndLayoutSet() {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
        binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate
    }

    companion object {
        const val TAG = "viewStdDialog"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun monthUpdate() {


        var currentDate = LocalDate.now()
        var month = currentDate.month
        val year = currentDate.year
        var str = "${month.toString()} ${year.toString()}"
        binding.textDateFilter.text = str

        var dat = currentDate


        while (dat.month == currentDate.month) {
            var i = 0
            while (i < markList.size) {
                Log.d("mont", "monthUpdate: ${i} ${markList.size}")
                if (markList[i].date == dat.toString()) {
                    date.add(dat)
                }
                i++
            }
            Log.d("montt", "monthUpdate: ${i} ${markList.size}")
            dat = dat.minusDays(1)
        }


        val d = date.distinct()
        date = d as ArrayList<LocalDate>
        Log.d("_xyz", "monthUpdate: $date $d")
        Log.d("_xyz", "monthUpdate: ${dat}")
        studentReportAdapterWithDate.setMarks(
            markList,
            requireContext(),
            date,
            dateListSize
        )




//        binding.imageViewBackArrow.setOnClickListener {
//            currentDate = currentDate.minusMonths(1)
//            str = "${currentDate.month.toString()} ${currentDate.year.toString()}"
//            binding.textDateFilter.text = str
//        }
//        binding.imageViewFrontArrow.setOnClickListener {
//
//            currentDate = currentDate.plusMonths(1)
//
//            str = "${currentDate.month.toString()} ${currentDate.year.toString()}"
//            binding.textDateFilter.text = str
//        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dayUpdate() {
        viewModel.fetchMarksByDay(stdId, date[0].toString())
        binding.textDateFilter.text = date[0].toString()
        if (markList.isNotEmpty()) {
            studentReportAdapterWithDate.setMarks(
                markList,
                requireContext(),
                date,
                dateListSize
            )
        }
        binding.imageViewBackArrow.setOnClickListener {

            date = arrayListOf(
                date[0].minusDays(1)
            )
            viewModel.fetchMarksByDay(stdId, date[0].toString())
            binding.textDateFilter.text = date[0].toString()
        }

        binding.imageViewFrontArrow.setOnClickListener {

            date = arrayListOf(
                date[0].plusDays(1)
            )
            viewModel.fetchMarksByDay(stdId, date[0].toString())
            binding.textDateFilter.text = date[0].toString()
        }


    }
}