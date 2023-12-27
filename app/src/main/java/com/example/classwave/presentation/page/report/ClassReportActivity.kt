package com.example.classwave.presentation.page.report

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.ActivityReportBinding
import com.example.classwave.domain.model.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class ClassReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private val viewModel: ReportViewModel by viewModels()

    private lateinit var reportParentAdapter: ReportParentAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedMonth = LocalDate.now().monthValue

    @RequiresApi(Build.VERSION_CODES.O)
    private val selectedDay = LocalDate.now().dayOfMonth

    @RequiresApi(Build.VERSION_CODES.O)
    private val selectedYear = LocalDate.now().year

    private var selectedFilterType = FilterType.Day

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentDate = LocalDate.now()
    private var clsId= ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clsId= intent.getStringExtra("clsId").toString()


        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)


        reportParentAdapter = ReportParentAdapter()
        binding.recyclerView.apply {
            adapter = reportParentAdapter
        }
        binding.textReportDate.text = currentDate.toString()


        if (clsId != null) {
            viewModel.fetchClassReport(
                clsId = clsId!!,
                day = selectedDay,
                month = selectedMonth,
                year = selectedYear,
            )
        }


        registerListener()
        initializeFlowCollectors()

    }
    private fun initializeFlowCollectors() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.reportClassList.collectLatest { data ->
                    when (data) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {

                            Log.d("_xyz", "initializeFlowCollectors: called ${data.data}")
                            data.data?.let {
                                Log.d("oo", "initializeFlowCollectors: ${it}")
                                reportParentAdapter.setReports(it)
                                Log.d("_check", "initializeFlowCollectors: ${it}")
                                viewModel.getTotalPosMarks(it)
                                viewModel.getTotalNegMarks(it)
                                viewModel.getTotalAchivedNegMarks(it)
                                viewModel.getTotalAchivedPosMarks(it)
                                binding.textPositiveMark.text =
                                    "Positive ${viewModel.getTotalAchivedPosMarks(it)}/${
                                        viewModel.getTotalPosMarks(
                                            it
                                        )
                                    }"
                                binding.textNegativeMark.text =
                                    "Negative ${viewModel.getTotalAchivedPosMarks(it)}/${
                                        viewModel.getTotalNegMarks(
                                            it
                                        )
                                    }"
                                var progress =
                                    viewModel.getProgress(
                                        viewModel.getTotalPosMarks(it),
                                        viewModel.getTotalAchivedPosMarks(it)
                                    )

                                binding.progressBar.progress = progress
                                binding.textMarksInPercent.text = "${progress.toString()}%"
                            }

                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerListener() {

        binding.btnNext.setOnClickListener {
            if (selectedFilterType == FilterType.Day) {

                Log.d("_xyz", "registerListener: jj")
                currentDate = currentDate.plusDays(1)
                binding.textReportDate.text = currentDate.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1,
                        currentDate.dayOfMonth,
                        currentDate.monthValue,
                        currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Month) {


                currentDate = currentDate.plusMonths(1)
                binding.textReportDate.text =
                    currentDate.month.toString() + " " + currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, currentDate.monthValue, currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Year) {

                currentDate = currentDate.plusYears(1)
                binding.textReportDate.text = currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, null, currentDate.year
                    )
                }
            }
        }

        binding.btnPrev.setOnClickListener {


            if (selectedFilterType == FilterType.Day) {
                Log.d("_xyz", "registerListener: jj")
                currentDate = currentDate.minusDays(1)
                binding.textReportDate.text = currentDate.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, currentDate.dayOfMonth, null, currentDate.year
                    )
                }
            }

            if (selectedFilterType == FilterType.Month) {

                currentDate = currentDate.minusMonths(1)
                binding.textReportDate.text =
                    currentDate.month.toString() + " " + currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, currentDate.monthValue, currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Year) {
                currentDate = currentDate.minusYears(1)
                binding.textReportDate.text = currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, null, currentDate.year
                    )
                }
            }
        }

        binding.cardDayReport.setOnClickListener {
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Day
            binding.textReportDate.text = currentDate.toString()
          clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1,
                    currentDate.dayOfMonth,
                    currentDate.monthValue,
                    currentDate.year
                )
            }

            /*  selectedFilterType = FilterType.Day*/
        }

        binding.cardMonthReport.setOnClickListener {
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Month
            binding.textReportDate.text =
                currentDate.month.toString() + " " + currentDate.year.toString()
           clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1, null, currentDate.monthValue, currentDate.year
                )
            }
        }

        binding.cardYearReport.setOnClickListener {
            currentDate = LocalDate.now()
            binding.textReportDate.text = currentDate.year.toString()
            selectedFilterType = FilterType.Year
            clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1, null, null, currentDate.year
                )
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}