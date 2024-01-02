package com.example.classwave.presentation.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.CreateClassDialogBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateClassDialog(
    val classId: String,
    val teacherId: String,
    val name: String,
    val img: String,
    val section: String,
    private val type: String
) : DialogFragment() {

    private var _binding: CreateClassDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeacherViewModel by activityViewModels()
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = CreateClassDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val grade = resources.getStringArray(R.array.label_grades)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, grade)
        binding.autoCompleteTextViewDropdownItems.setAdapter(arrayAdapter)
        Log.d("_xyz", "onViewCreated: ${type}")
        if (type =="update"){
            binding.btnDeleteClass.visibility=View.VISIBLE
            binding.btnCreateClass.text="Update"
            binding.editTxtClassName.setText(name)
        }
            registerListener()
    }

    private fun registerListener() {
        binding.imgCancel.setOnClickListener {
            dismiss()
        }
        binding.editTxtClassName.addTextChangedListener {
            binding.btnCreateClass.isClickable = true
            binding.btnCreateClass.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        }
        binding.btnCreateClass.setOnClickListener {
            val className = binding.editTxtClassName.text.toString()
            val grade = binding.autoCompleteTextViewDropdownItems.text.toString()
            viewModel.createClass(className, grade)
            dismiss()
        }
    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createClass.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> showErrorMessage(message = it.message ?: "")
                            is Resource.Loading -> {}
                            is Resource.Success -> showSuccessView()
                        }

                    }


                }
            }
        }
    }

    private fun showErrorMessage(message: String) {

        SnackbarUtil.show(requireContext(), message, binding.btnCreateClass)
    }

    private fun showSuccessView() {
        val intent = Intent(requireContext(), TeacherActivity::class.java)
        /*  intent.putExtra("class_id",binding.editTxtClassName.text.toString())*/

        startActivity(intent)
        dismiss()
    }

    companion object {
        const val TAG = "CreateClassDialog"
    }


}