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
    enum class Type { DAY, MONTH, YEAR }

    private var reportType = Type.DAY

    @RequiresApi(Build.VERSION_CODES.O)
    var currentDate = LocalDate.now()

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogViewStdReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportType = Type.DAY
        dayUpdate()
        initializeFlowCollectors()

        registerListener()
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewReportDetailsWithDate.layoutManager = layoutManager
        binding.recyclerViewReportDetailsWithDate.adapter = studentReportAdapterWithDate

        // viewModel.fetchMarksByDay(stdId,date.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.markList.collectLatest { mark ->
                    if (mark != null) {



                        mark.data?.let {
                            markList = it
                        }


                        if (markList.isNotEmpty()) {
                            studentReportAdapterWithDate.setMarks(
                                markList, requireContext(), date, dateListSize
                            )

                        } else {
                            studentReportAdapterWithDate.setMarks(
                                markList, requireContext(), date, 0
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
            reportType = Type.DAY
            dayUpdate()
        }

        binding.cardMonthReport.setOnClickListener {
            reportType = Type.MONTH
            monthUpdate()
        }

        binding.imageViewBackArrow.setOnClickListener {
            if (reportType == Type.DAY) {

                date = arrayListOf(
                    date[0].minusDays(1)
                )
               /* viewModel.fetchMarksByDay(stdId, date[0].toString())*/
                binding.textDateFilter.text = date[0].toString()

            } else if (reportType == Type.MONTH) {
                currentDate = LocalDate.now()
                binding.imageViewBackArrow.setOnClickListener {
                    currentDate = currentDate.minusMonths(1)
                    var str = "${currentDate.month.toString()} ${currentDate.year.toString()}"
                    val d = date.distinct()
                    date = d as ArrayList<LocalDate>
                    Log.d("_xyz", "monthUpdate: $date ")

                    studentReportAdapterWithDate.setMarks(
                        markList, requireContext(), date, dateListSize
                    )
                    binding.textDateFilter.text = str
                }

            } else {


            }

        }



        binding.imageViewFrontArrow.setOnClickListener {
            if (reportType == Type.DAY) {
                date = arrayListOf(
                    date[0].plusDays(1)
                )
         /*       viewModel.fetchMarksByDay(stdId, date[0].toString())*/
                binding.textDateFilter.text = date[0].toString()

            } else if (reportType == Type.MONTH) {
                currentDate = currentDate.plusMonths(1)

                var str = "${currentDate.month.toString()} ${currentDate.year.toString()}"
                binding.textDateFilter.text = str
            }

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
        date = arrayListOf()
        date.add(LocalDate.now())
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


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dayUpdate() {

        date = arrayListOf()
        date.add(LocalDate.now())
        Log.d("see", "dayUpdate: ${markList}  abcdefg ${date}")
        /*viewModel.fetchMarksByDay(stdId, date[0].toString())*/
        binding.textDateFilter.text = date[0].toString()
        if (markList.isNotEmpty()) {
            studentReportAdapterWithDate.setMarks(
                markList, requireContext(), date, dateListSize
            )
        }


    }
}