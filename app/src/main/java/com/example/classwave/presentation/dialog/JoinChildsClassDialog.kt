package com.example.classwave.presentation.dialog

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class JoinChildsClassDialog : DialogFragment() {
    private val viewModel: ParentViewModel by activityViewModels()
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
        binding.editTextAddStdName.hint =
            "Class code"
        binding.toolbar.title = "Join Child's Class"
        //binding.textViewRandomMsg.text = "Aha! Wise Wise! Greate decision to join child class!"
        binding.textViewRandomMsg.visibility = View.INVISIBLE
        binding.btnAddStd.text = "Submit"
        binding.textViewInstruction.text =
            "Enter your class child code, if you don't have a code ask it to your child's teacher,By joining a class you will get full access of the class , which is available for a parent."
        // Log.d("_pr", "onViewCreated:  classId = $clsId")
        binding.imageStdProfile.setImageResource(R.drawable.join_child_class_bg)


        initialFlowCollectors()
        registerListener()
    }


    fun registerListener() {

        binding.btnAddStd.setOnClickListener {
            val clsId = binding.editTextAddStdName.text.toString()
            viewModel.isClassExists(clsId)

        }
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
    }


    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isClassExists.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {
                                /*withContext(Dispatchers.Main) {*/
                                it.message?.let { it1 -> ShowError(it1) }
                                /* }*/

                            }

                            is Resource.Loading -> {
                                binding.btnAddStd.text = ""
                                binding.progressBarDeleteLoading.visibility = View.VISIBLE

                            }

                            is Resource.Success -> {
                                it.data?.let { it1 ->
                                    ConnectToYourKidDialog(
                                        it1.classId,
                                        it.data.name,

                                        it.data.teacherName,
                                        it.data.teacherId,
                                                it.data.img,
                                    )
                                }?.show(parentFragmentManager, ConnectToYourKidDialog.TAG)
                                viewModel.setNull()
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    fun ShowError(msg: String) {
        /*       val dialog = AlertDialog.Builder(context)
                   .setMessage(msg)
                   .setNeutralButton("ok",
                       object : DialogInterface.OnClickListener {
                           override fun onClick(
                               dialog: DialogInterface?,
                               which: Int
                           ) {
                               dismiss()
                           }

                       })
                   .show()*/
        val dialogView =
            layoutInflater.inflate(com.example.classwave.R.layout.custom_dialog_no_class, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).show()
        val btnOkay = dialogView.findViewById<View>(com.example.classwave.R.id.btn_okay) as TextView
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnOkay.setOnClickListener {
            binding.progressBarDeleteLoading.visibility=View.INVISIBLE
            binding.btnAddStd.text = "Submit"
            dialog.dismiss()
        }
    }

    //initialFlowCollectors()


    /* @RequiresApi(Build.VERSION_CODES.O)*/
    /*    private fun registerListener() {
            binding.btnAddStd.setOnClickListener {
                val studentName = binding.editTextAddStdName.text.toString()
                viewModel.createStudent(clsId, studentName)
            }
        }*/

    /*
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

        }*/

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