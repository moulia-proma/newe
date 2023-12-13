package com.example.classwave.presentation.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.classwave.R
import com.example.classwave.databinding.DialogProvideMarksBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel

class ProvideMarksDialog(val clsId: String, val stdId: String, val skillId: String) : DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogProvideMarksBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  DialogProvideMarksBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        registerListener()
    }
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerListener() {
    /*    binding.imgCancel.setOnClickListener {
            dismiss()
        }
        binding.editTxtClassName.addTextChangedListener {
            binding.btnCreateClass.isClickable = true
            binding.btnCreateClass.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        }*/
        binding.btnAddMarks.setOnClickListener {
          val mark = binding.editTextAddSkillName.text.toString()
            viewModel.addMarks(clsId,stdId,skillId,mark)
            dismiss()

        }
    }

    companion object {
        const val TAG = "ProvideMarksdialog"
    }
}