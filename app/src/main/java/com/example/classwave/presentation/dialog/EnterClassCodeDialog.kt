package com.example.classwave.presentation.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogAddNewStdBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.student.StudentViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EnterClassCodeDialog(
    var clsId: String,
    teacherId: String,
    name: String,
    img: String,
    grade: String,
    type: String,
    private val uName: String?
) : DialogFragment() {
    private val viewModel: StudentViewModel by activityViewModels()
    private var _binding: DialogAddNewStdBinding? = null
    private val binding get() = _binding!!
    private var uType = arrayListOf<String>()

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
        binding.editTextAddStdName.hint = "Class code"
        binding.btnAddStd.text = "Join"

        initialFlowCollectors()
        registerListener()
    }

    fun registerListener() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.setNull()
            dismiss()
        }
        binding.btnAddStd.setOnClickListener {
        clsId = binding.editTextAddStdName.text.toString()
            if (uName != null) {
                viewModel.isClassExists(clsId, uName)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    /*    private fun registerListener() {
            binding.btnAddStd.setOnClickListener {
                val studentName = binding.editTextAddStdName.text.toString()
                viewModel.createStudent(clsId, studentName)
            }
        }*/


    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest {
                    if (it.isNotEmpty()) {
                        if (it[0] == "student") {
                            uType = it
                        }
                    }


                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createStudent.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {
                                it.message?.let { it1 ->
                                    SnackbarUtil.show(
                                        requireContext(),
                                        it1, binding.btnAddStd
                                    )

                                }
                                binding.btnAddStd.text = "join"
                                binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                            }

                            is Resource.Loading -> {
                                binding.btnAddStd.text = ""
                                binding.progressBarDeleteLoading.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                                binding.btnAddStd.text = "join"
                                dismiss()
                                viewModel.setNull()
                            }

                        }
                    }


                }
            }
        }

    }

    /*    private fun showSuccessView() {
            //   viewModel.setCreateClassNull()
            Log.d("_xyzz", "showSuccessView: my name")
            binding.progressBarDeleteLoading.visibility = View.INVISIBLE
            viewModel.setNull()
            dismiss()
        }*/


    companion object {
        const val TAG = "CreateStdialog"
    }
}