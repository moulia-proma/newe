package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.classwave.R
import com.example.classwave.databinding.DialogAddNewStdBinding
import com.example.classwave.presentation.page.teacher.AddStudentAdapter
import com.example.classwave.presentation.page.teacher.TeacherViewModel


class AddNewStdDialog(val clsId: String) : DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogAddNewStdBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddNewStdBinding.inflate(inflater, container, false)
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
        binding.btnAddStd.setOnClickListener {
            val studentName = binding.editTextAddStdName.text.toString()
            viewModel.createStudent(clsId,studentName)
            dismiss()

        }
    }

    companion object {
        const val TAG = "CreateStdialog"
    }
}