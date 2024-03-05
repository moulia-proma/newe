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
import com.example.classwave.databinding.DialogDetailsNotificationLayoutBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ClassListAdapter
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.example.classwave.presentation.util.SnackbarUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DetailsNotificationDialog(
    val parentName: String,
    val parentId: String,
    val parentPhoto: String,
    val clsId: String,
    val clsName: String,
    val clsImage: String,
    val stdId: String,
    val stdName: String,
    val stdImage: String,
    val state: String
) :
    DialogFragment() {

    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogDetailsNotificationLayoutBinding? = null
    private val binding get() = _binding!!
    private val clsListAdapter = ClassListAdapter()
    private var selectedClass = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogDetailsNotificationLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.textViewStudentName.text = "$stdName  (student)"
        binding.textViewParentName.text = "$parentName"
        binding.imgViewProfilePic.setImageResource(parentPhoto.toInt())
        registerListener()
    }


            @RequiresApi(Build.VERSION_CODES.O)
            private fun registerListener() {
                binding.toolbar.setNavigationOnClickListener {
                    dismiss()
                }
                if(state == "pending" || state == "viewed"){

                    binding.btnAccept.setOnClickListener {
                        viewModel.updateNotificationState("Accepted")
                        viewModel.createStudent("",stdName,stdId,stdImage)
                        SnackbarUtil.show(requireContext(),"Request Accepted",binding.btnReject)
                        dismiss()

                    }
                    binding.btnReject.setOnClickListener {
                        viewModel.updateNotificationState("Rejected")
                        SnackbarUtil.show(requireContext(),"Request Rejected",binding.btnReject)
                        dismiss()
                    }

                }else{
                    binding.groupNotResponded.visibility = View.GONE
                    binding.groupNotificationReviewed.visibility = View.VISIBLE
                    binding.textViewReviewedNotification.text = "You Already $state this Request!!"
                }
            }

    companion object {
        const val TAG = "SkillDialog"
    }
}
