package com.example.classwave.presentation.page.report

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.ActivitySkillReportBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SkillReportActivity : AppCompatActivity() {

    private val viewModel: TeacherViewModel by viewModels()
    private lateinit var binding: ActivitySkillReportBinding
    private var reportChildAdapter = ReportChildAdapter()
    var skillId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkillReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        skillId = intent.getStringExtra("skillId").toString()
        viewModel.getReportSkillSpecific(skillId)
        binding.recyclerView.adapter = reportChildAdapter
        initialFlowCollectors()
    }

    private fun initialFlowCollectors() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.skillReportList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                if (it.data != null) {
                                    Log.d("_xyz", "initialFlowCollectors: ${it.data.size}")
                                    reportChildAdapter.setReports(it.data)
                                     binding.textName.text = "Max: " + it.data[it.data.size-1].stdName + " "+ it.data[it.data.size-1].marks + "/" +  it.data[it.data.size-1].highestScore

                                 binding.imgViewProfile.setImageResource(it.data[it.data.size-1].stdProfile.toInt())
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

