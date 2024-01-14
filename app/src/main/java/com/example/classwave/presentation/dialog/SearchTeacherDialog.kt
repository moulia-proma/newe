package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.databinding.DialogSearchTeacherBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.example.classwave.presentation.page.parent.SearchResultAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchTeacherDialog(val stdId: String, val parentId: String) : DialogFragment() {

    private val viewModel: ParentViewModel by activityViewModels()
    private var _binding: DialogSearchTeacherBinding? = null
    private val binding get() = _binding!!
    private val searchAdapter = SearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogSearchTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewSearchResult.adapter = searchAdapter
        searchAdapter.setListener(object : SearchResultAdapter.Listener {
            override fun onTeacherClicked(tcr: UserItemResponse) {
                val dialog = ChooseClassDialog(tcr.uid, stdId, parentId)
                dialog.show(parentFragmentManager, ChooseClassDialog.TAG)
            }

        })
        registerListener()
        initialFlowCollectors()
    }


    private fun registerListener() {
        binding.editTxtClassName.addTextChangedListener {
            val teacherName = binding.editTxtClassName.text.toString()
            if (teacherName == "") {
                searchAdapter.setChild(arrayListOf())
            } else {
                Log.d("_xyz", "registerListener: $teacherName")
                viewModel.searchTeacher(teacherName)
            }

        }
    }

    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.teacherList.collectLatest {
                    // Log.d("_e", "initialFlowCollectors: ${it.data}")
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                //   Log.d("_xyz", "initialFlowCollectors: ${it.data}")
                                it.data?.let { it1 -> searchAdapter.setChild(it1) }

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