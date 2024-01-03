package com.example.classwave.presentation.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogAddNewStdBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initialFlowCollectors()
        registerListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerListener() {
        binding.btnAddStd.setOnClickListener {
            val studentName = binding.editTextAddStdName.text.toString()
            viewModel.createStudent(clsId, studentName)


        }
    }


    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createStudent.collectLatest {
                    it?.let {
                        when (it) {

                            is Resource.Error -> {}
                            is Resource.Loading -> {
                                Log.d("_xyzz", "initialFlowCollectors: hhhh")
                                binding.progressBarDeleteLoading.visibility = View.VISIBLE
                                binding.btnAddStd.text = ""
                            }
                            is Resource.Success -> {
                                showSuccessView()
                                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }


                }
            }
        }
    }
    private fun showSuccessView() {
        //   viewModel.setCreateClassNull()
        Log.d("_xyzz", "showSuccessView: my name")
        binding.progressBarDeleteLoading.visibility = View.INVISIBLE
        viewModel.setNull()
        dismiss()
    }


    companion object {
        const val TAG = "CreateStdialog"
    }
}