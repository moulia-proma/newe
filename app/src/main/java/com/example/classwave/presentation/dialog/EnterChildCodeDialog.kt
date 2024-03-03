package com.example.classwave.presentation.dialog

import android.content.Intent
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
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log


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
            "Enter your child profile code"
        binding.btnAddStd.text = "Connect"
        Log.d("_lk", "onViewCreated i see: ${viewModel.createChild.value?.data.toString()}")
        binding.toolbar.title = "Connect child"
        binding.textViewRandomMsg.text = "Stay in touch to your child!!"
        binding.imageStdProfile.setImageResource(R.drawable.connect_child_bg)
        binding.textViewInstruction.text = "Enter your child profile code, if you don't have a code go to your child profile and click on the share profile option in the right corner and copy the code and then paste it here!"



        binding.btnAddStd.setOnClickListener {

            stdId = binding.editTextAddStdName.text.toString()
            viewModel.findUserType(Firebase.auth.uid.toString())
        }

        initialFlowCollectors()
        binding.toolbar.setNavigationOnClickListener {
            viewModel.setNull()
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
                viewModel.isStudentExists1.collectLatest {
                    if (it != null) {
                        Log.d("_xyz", "initialFlowCollectors: ${it.data}")
                    }else{
                        Log.d("_xyz", "initialFlowCollectors: mxx")
                    }
                    it?.let {
                      when(it){
                          is Resource.Error -> {

                              it.message?.let { it1 ->
                                  SnackbarUtil.show(
                                      requireContext(),
                                      it1, binding.btnAddStd
                                  )
                              }
                              binding.btnAddStd.text = "join"
                              binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                              //viewModel.setNull()
                          }
                          is Resource.Loading -> {
                              binding.btnAddStd.text = ""
                              binding.progressBarDeleteLoading.visibility = View.VISIBLE

                          }
                          is Resource.Success -> {
                              stdName = it.data?.name ?: ""
                              stdImage = it.data?.uPhoto?: ""
                              Log.d("_xyz", "initialFlowCollectors: i m 1  ${it.data}")
                              if(stdId !="" && stdImage!="" && stdName !=""){
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
                        viewModel.isStudentExists1(stdId)

                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createChild.collectLatest {
                    Log.d("_lm", "initialFlowCollectors: sry")
                    it?.let {
                        when (it) {
                            is Resource.Error -> {
                                it.message?.let { it1 ->
                                    SnackbarUtil.show(
                                        requireContext(),
                                        it1, binding.btnAddStd
                                    )
                                    viewModel.setNull()


                                }
                                binding.btnAddStd.text = "join"
                               // binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                                Log.d("_lm", "initialFlowCollectors: ee")
                                binding.progressBarDeleteLoading.visibility = View.INVISIBLE
                            }
                            is Resource.Loading -> {
                                Log.d("_lm", "initialFlowCollectors: ll")
                                binding.progressBarDeleteLoading.visibility = View.VISIBLE
                                binding.btnAddStd.text = ""
                            }

                            is Resource.Success -> {
                                Log.d("_lm", "initialFlowCollectors: ss")
                                  //viewModel.setNull()
                                binding.btnAddStd.text = "Join"
                                binding.progressBarDeleteLoading.visibility = View.INVISIBLE

                                mListener?.onDialogClosed(this@EnterChildCodeDialog)

                              // viewModel.
                                /*val intent = Intent(requireContext(), ParentActivity::class.java)
                                startActivity(intent)*/
                              //  dismiss()
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