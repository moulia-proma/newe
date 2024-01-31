package com.example.classwave.presentation.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.classwave.R
import com.example.classwave.databinding.ClassInviteDialogBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel


class ShareProfileDialog(
    val stdId: String
) : DialogFragment() {
    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: ClassInviteDialogBinding? = null
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
        binding.textViewClsCode.text = stdId
        binding.btnShareCode.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, stdId)
            startActivity(Intent.createChooser(shareIntent, "Anything"))
        }
        binding.imgShareClsBg.setImageResource(R.drawable.share_profile_bg)
        binding.toolbar.title  = "Share profile"

        binding.cardViewClassLinkBg.setOnClickListener {
            val clipManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", stdId)
            clipManager.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), "Class code copied", Toast.LENGTH_LONG).show()
        }

        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }


        binding.textViewInviteInstruction.text = "By sharing this code your parent can connect with you,please share this code with your parent!"


        /*        binding.editTextAddStdName.hint = clsId
                   binding.btnAddStd.text = "Share Code"
                   initialFlowCollectors()

                registerListener()*/
    }


    /* private fun registerListener() {
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
     }*/
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