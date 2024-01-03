package com.example.classwave.presentation.page.report

import android.os.Bundle
import android.util.Log
import android.view.View
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
                            is Resource.Loading -> {
                                Log.d("_xyz", "initialFlowCollectors: ami ami")
                                binding.progressBarLoading.visibility=View.VISIBLE
                                binding.imageViewEmpty.visibility=View.GONE
                                binding.textEmpty.visibility=View.GONE
                                binding.scroll.visibility = View.GONE
                            }
                            is Resource.Success -> {

                                if (it.data?.isEmpty() != true) {
                                    binding.progressBarLoading.visibility=View.INVISIBLE
                                    binding.imageViewEmpty.visibility=View.GONE
                                    binding.textEmpty.visibility=View.GONE
                                    binding.scroll.visibility = View.VISIBLE
                                    Log.d(
                                        "_xyz",
                                        "initialFlowCollectors: ${it.data?.size}   ${it.data} "
                                    )
                                    it.data?.let { it1 -> reportChildAdapter.setReports(it1) }
                                    binding.textName.text =
                                        "Max: " + (it.data?.get(it.data.size - 1)?.stdName
                                            ?: "") + " " + (it.data?.get(it.data.size - 1)?.marks
                                            ?: "") + "/" + (it.data?.get(it.data.size - 1)?.highestScore
                                            ?: "")

                                    it.data?.get(it.data.size - 1)?.stdProfile?.let { it1 ->
                                        binding.imgViewProfile.setImageResource(
                                            it1.toInt()
                                        )
                                    }
                                }else {
                                    Log.d("_xyz", "initialFlowCollectors: abcd")
                                    reportChildAdapter.setReports(mutableListOf())
                                    binding.progressBarLoading.visibility=View.INVISIBLE
                                    binding.imageViewEmpty.visibility=View.VISIBLE
                                    binding.textEmpty.visibility=View.VISIBLE
                                    binding.scroll.visibility = View.GONE

                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

