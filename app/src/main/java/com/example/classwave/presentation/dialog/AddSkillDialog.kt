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
import com.example.classwave.databinding.DialogAddSkillBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddSkillDialog(private val clsId: String, private val typeofSkill: String) :
    DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogAddSkillBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddSkillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        registerListener()
        initialFlowCollectors()
    }

    private fun registerListener() {
        binding.btnAddSkill.setOnClickListener {
            val skillName = binding.editTextAddSkillName.text.toString()
            val hScore = binding.editTextAddSkillHighScoreName.text.toString()
            viewModel.createSkill(clsId, skillName, hScore, typeofSkill)
        }
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
            viewModel.setNull()
        }


    }
    private fun initialFlowCollectors(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.createSkill.collectLatest {
                    it?.let {
                        when(it){
                            is Resource.Error -> {
                                binding.btnAddSkill.text = "Add"
                                binding.progressBarDeleteLoading.visibility= View.INVISIBLE
                                it.message?.let { it1 ->
                                    SnackbarUtil.show(requireContext(),
                                        it1,binding.btnAddSkill)
                                }
                            }
                            is Resource.Loading -> {
                                binding.btnAddSkill.text=""
                                binding.progressBarDeleteLoading.visibility= View.VISIBLE
                            }
                            is Resource.Success -> {
                            binding.progressBarDeleteLoading.visibility=View.INVISIBLE
                                viewModel.setNull()
                                dismiss()
                            }

                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SkillDialog"
    }
}