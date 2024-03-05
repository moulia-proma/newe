package com.example.classwave.presentation.dialog

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.classwave.R
import com.example.classwave.databinding.DialogProvideMarksBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel

class ProvideMarksDialog(
    private val clsId: String,
    private val stdId: String,
    private val skillId: String,
    private val highestScore: String,
    private val type: String,
    private val name: String,
    private val img: String,
    private val stdName: String,
    private val stdProfile: String
) : DialogFragment() {
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
        _binding = DialogProvideMarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val grade = viewModel.getMarksDropdownList(highestScore, type)


        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, grade)
        binding.autoCompleteTextViewDropdownItems.setAdapter(arrayAdapter)
        binding.autoCompleteTextViewDropdownItems.hint = "Select Score"
        registerListener()
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerListener() {
        binding.btnAddMarks.setOnClickListener {
            val mark = binding.autoCompleteTextViewDropdownItems.text.toString()
            viewModel.addMarks(
                stdId,
                skillId,
                mark,
                name,
                img,
                highestScore,
                clsId,
                stdName,
                stdProfile
            )

            dismiss()

        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }


    }

    companion object {
        const val TAG = "ProvideMarksdialog"
    }
}