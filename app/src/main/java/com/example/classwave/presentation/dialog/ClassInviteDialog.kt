package com.example.classwave.presentation.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import com.example.classwave.databinding.ClassInviteDialogBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ClassInviteDialog : DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: ClassInviteDialogBinding? = null

    var clsId = ""
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ClassInviteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*   binding.editTextAddStdName.hint = clsId
           binding.btnAddStd.text = "Share Code"
           initialFlowCollectors()*/

        registerListener()
        initialFlowCollectors()
    }


    private fun registerListener() {
        binding.btnShareCode.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, clsId)
            startActivity(Intent.createChooser(shareIntent, "Anything"))
        }
        binding.cardViewClassLinkBg.setOnClickListener {
            val clipManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", clsId)
            clipManager.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), "Class code copied", Toast.LENGTH_LONG).show()
        }
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

    }

    fun initialFlowCollectors() {
        Log.d("_chk", "initialFlowCollectors: jjj")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedClass.collectLatest {
                    if (it == null) {

                        if (clsId.isEmpty()) {
                            binding.textViewClsCode.visibility = View.INVISIBLE
                            binding.btnShareCode.visibility = View.INVISIBLE
                            binding.textViewInviteInstruction.text =
                                "Please create a class first and then share your class code!"
                            binding.cardViewClassLinkBg.visibility= View.INVISIBLE
                            binding.imageCopyIcon.visibility = View.INVISIBLE
                        }
                    } else {
                        clsId = it.classId
                        binding.textViewClsCode.text = clsId
                    }

                }
            }
        }


    }

    /*

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
           }*/


    companion object {
        const val TAG = "CreateStdialog"
    }
}