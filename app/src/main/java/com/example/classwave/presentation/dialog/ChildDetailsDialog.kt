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
import com.example.classwave.databinding.DialogChooseClassesFromSelectedTeacherBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ClassListAdapter
import com.example.classwave.presentation.page.parent.ParentViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChildDetailsDialog(
  /*  val tcrId: String,
    val stdId: String,
    val parentId: String,
    val uPhoto: String,
    val name: String,
    val parentPhoto: String,
    val parentName: String,
    val stdImage: String,
    val stdName: String*/
  val stdName:String,
  val stdPhoto: String,
  val stdParent:String,
  val stdId: String,
  val stdEmail:String,
  val stdType: String,
) :
    DialogFragment() {

    private val viewModel: ParentViewModel by activityViewModels()
    private var _binding: DialogChildDetailsBinding? = null
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
        _binding = DialogChildDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewClsList.adapter = clsListAdapter
    /*    viewModel.fetchClassList(tcrId)*/

        clsListAdapter.setListener(object : ClassListAdapter.Listener {
            override fun onRequestClicked(clsId: String, type: String) {
                if (type == "remove") {
                    selectedClass.remove(clsId)
                } else {
                    selectedClass.add(clsId)
                }
            }

        })

        binding.floatingActionButtonRequestTeacher.setOnClickListener {
        /*    val dialog = SearchTeacherDialog(
                stdId,Firebase.auth.uid.toString(),


            )*/




        }

    /*    binding.floatingActionButtonRequestTeacher.setOnClickListener {
            viewModel.requestTeacher(tcrId, stdId, parentId, selectedClass,uPhoto,stdImage,parentPhoto,name,parentName,stdName)



            dismiss()
        }*/

        //   registerListener()
        initialFlowCollectors()
    }


    /*        private fun registerListener() {
                binding.editTxtClassName.addTextChangedListener {
                    val teacherName = binding.editTxtClassName.text.toString()
                    if (teacherName == "") {
                        searchAdapter.setChild(arrayListOf())
                    } else {
                        Log.d("_xyz", "registerListener: $teacherName")
                        viewModel.searchTeacher(teacherName)
                    }

                }
            }*/
    private fun initialFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.classList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                it.data?.let { it1 -> clsListAdapter.setChild(it1) }

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