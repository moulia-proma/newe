package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.classwave.R
import com.example.classwave.databinding.DialogAddSkillBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel


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
    }

    private fun registerListener() {
        /*    binding.imgCancel.setOnClickListener {
                dismiss()
            }
            binding.editTxtClassName.addTextChangedListener {
                binding.btnCreateClass.isClickable = true
                binding.btnCreateClass.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            }*/
        binding.btnAddSkill.setOnClickListener {
            val skillName = binding.editTextAddSkillName.text.toString()
            val hScore = binding.editTextAddSkillHighScoreName.text.toString()
            viewModel.createSkill(clsId, skillName, hScore, typeofSkill)
            dismiss()

        }
    }

    companion object {
        const val TAG = "SkillDialog"
    }
}