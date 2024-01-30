package com.example.classwave.presentation.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EnterChildCodeDialog(val clsId: String, ) : DialogFragment() {


    private val viewModel: ParentViewModel by activityViewModels()
    private var _binding: DialogAddNewStdBinding? = null
    private val binding get() = _binding!!
    private var stdName = ""
    private var stdImage = ""

    private var parentName = ""
    private var parentPhoto = ""

    var stdId = ""

    private var mListener : Listener? = null

    interface Listener {
        fun onDialogClosed(dialog: EnterChildCodeDialog)
    }


    fun setListener(listener: Listener) {
        mListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddNewStdBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.editTextAddStdName.hint =
            "Enter your child connection code ,if you don't have ask to ur child"
        binding.btnAddStd.text = "Connect"
        binding.toolbar.title = "Add child"


        binding.btnAddStd.setOnClickListener {

            stdId = binding.editTextAddStdName.text.toString()
            viewModel.findUserType(Firebase.auth.uid.toString())
        }

        initialFlowCollectors()
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
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
                viewModel.isStudentExists.collectLatest {
                    if (it != null) {
                        stdName = it.data?.name ?: ""
                        stdImage = it.data?.uPhoto ?: ""
                        Log.d("_xyz", "initialFlowCollectors: i m 1  ${it.data}")

                        viewModel.addChild(
                            stdName,
                            stdImage,
                            stdId,
                            Firebase.auth.uid.toString(),
                            parentName,
                            parentPhoto,
                            "notAssigned"
                        )

                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest {
                    Log.d("_xyz", "initialFlowCollectors utype: ${it.data.toString()}")
                    if (it.data?.isNotEmpty() == true) {
                        parentName = it.data[1].toString()
                        parentPhoto = it.data[2].toString()
                        viewModel.isStudentExists(stdId)
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createChild.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {
                                binding.progressBarDeleteLoading.visibility = View.VISIBLE
                                binding.btnAddStd.text = ""
                            }

                            is Resource.Success -> {
                                binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                                mListener?.onDialogClosed(this@EnterChildCodeDialog)

//                                val intent = Intent(requireContext(), ParentActivity::class.java)
//                                startActivity(intent)
//                                viewModel.setNull()
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