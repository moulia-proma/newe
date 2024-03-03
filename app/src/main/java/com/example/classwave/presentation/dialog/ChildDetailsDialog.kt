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
import com.example.classwave.databinding.DialogChildDetailsBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.example.classwave.presentation.page.parent.StdClassListAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChildDetailsDialog(

    val stdName: String,
    val stdPhoto: String,
    val stdParent: String,
    val stdId: String,
    val stdEmail: String,
    val stdType: String,
) :
    DialogFragment() {

    private val viewModel: ParentViewModel by activityViewModels()
    private var _binding: DialogChildDetailsBinding? = null
    private val binding get() = _binding!!
    private val clsListAdapter = StdClassListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChildDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          //viewModel.setUtypeNull()
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewClsList.adapter = clsListAdapter
        viewModel.fetchStdClassList(stdId, Firebase.auth.uid.toString())
        binding.imageViewProfile.setImageResource(stdPhoto.toInt())
        binding.textViewTcrName.text = "${stdName}'s classes"

        binding.floatingActionButtonRequestTeacher.setOnClickListener {
            val dialog = SearchTeacherDialog(stdId, stdName, stdPhoto)
            dialog.show(parentFragmentManager, SearchTeacherDialog.TAG)
            dismiss()
        }

        initialFlowCollectors()
    }

    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stdClassList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                it.data?.let { it1 -> clsListAdapter.setChild(it1, context) }
                            }
                        }

                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SkillDialog"
    }
}