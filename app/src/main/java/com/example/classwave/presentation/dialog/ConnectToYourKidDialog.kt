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
import androidx.lifecycle.lifecycleScope
import com.example.classwave.R
import com.example.classwave.databinding.DialogJoinRequestBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ConnectToYourKidDialog(
    val clsId: String,
    val clsName: String,
    val teacherName: String,
    val teacherId: String,
    val clsImage:String
) : DialogFragment() {

    private val viewModel: ParentViewModel by activityViewModels()
    private var _binding: DialogJoinRequestBinding? = null

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
        _binding = DialogJoinRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

         binding.textViewTeacherName.text = teacherName
        binding.textViewInstruction.text = "Once ${teacherName} accepts your connection request, you'll get announcements and be able to see your child's progress"
        registerListener()
        initialFlowCollectors()


    }


    fun registerListener() {
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
        viewModel.findUserType(Firebase.auth.uid.toString())

        binding.btnRequstConnection.setOnClickListener {
            val stdId = binding.editTextChildName.text.toString()
            //viewModel.isAlreadyNotified()

            Log.d("_me", "registerListener: lets check  ${stdId} ${clsId}")

            viewModel.isStudentExists1(stdId)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            viewModel.createChild.collectLatest {
                it?.let{
                   when(it){
                       is Resource.Error -> {
                           binding.progressBarBtnLoading.visibility = View.INVISIBLE
                           it.message?.let { it1 -> showError(it1) }
                       }
                       is Resource.Loading -> {
                           Log.d("_x", "initialFlowCollectors: xxxy")
                           binding.btnRequstConnection.text = ""
                           binding.progressBarBtnLoading.visibility = View.VISIBLE
                       }
                       is Resource.Success -> {
                           viewModel.setNull()
                           dismiss()
                       }
                   }
                }
            }
        }



        lifecycleScope.launch {
            viewModel.isStudentExists1.collectLatest {
                it?.let {
                    when (it) {
                        is Resource.Error -> {
                            //withContext(Dispatchers.Main) {
                            binding.progressBarBtnLoading.visibility = View.INVISIBLE
                                it.message?.let { it1 -> showError(it1) }
                           //// }

                        }

                        is Resource.Loading -> {
                            Log.d("_x", "initialFlowCollectors: xxx")
                            binding.btnRequstConnection.text = ""
                            binding.progressBarBtnLoading.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                      val arr = arrayListOf <String>()
                            val arr2 = arrayListOf<String>()
                            val arr3 =  arrayListOf<String>()
                            arr.add(clsId)
                            arr2.add(clsName)
                            arr3.add(clsImage)
                            it.data?.let { it1 ->
                                viewModel.requestTeacher(teacherId,it.data.uid,arr,"",it.data.uPhoto,teacherName,
                                    it1.name,arr2,arr3)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String?) {
        binding.btnRequstConnection.text = "Request to connect"

        message?.let { it1 ->
            SnackbarUtil.show(
                requireContext(),
                it1, binding.btnRequstConnection
            )
        }
    }

//    fun ShowError(msg: String) {
//       val dialog = AlertDialog.Builder(context)
//                   .setMessage(msg)
//                   .setNeutralButton("ok",
//                       object : DialogInterface.OnClickListener {
//                           override fun onClick(
//                               dialog: DialogInterface?,
//                               which: Int
//                           ) {
//                               dismiss()
//                           }
//
//                       })
//                   .show()*//*
//
//        val dialogView =
//            layoutInflater.inflate(com.example.classwave.R.layout.custom_dialog_no_class, null)
//        val dialog = AlertDialog.Builder(context).setView(dialogView).show()
//        val btnOkay = dialogView.findViewById<View>(com.example.classwave.R.id.btn_okay) as TextView
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        btnOkay.setOnClickListener {
//            dialog.dismiss()
//        }
//    }


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