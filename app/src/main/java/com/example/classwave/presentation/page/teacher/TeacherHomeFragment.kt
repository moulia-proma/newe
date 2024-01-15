package com.example.classwave.presentation.page.teacher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.FragmentTeacherHomeBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.AddNewStdDialog
import com.example.classwave.presentation.dialog.ClassInviteDialog
import com.example.classwave.presentation.dialog.SkillDialog
import com.example.classwave.presentation.page.Attandance.AttendanceActivity
import com.example.classwave.presentation.page.main.MainActivity
import com.example.classwave.presentation.page.report.ClassReportActivity
import com.example.classwave.presentation.page.report.SkillReportActivity
import com.example.classwave.presentation.page.report.skillWiseReportAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class TeacherHomeFragment : Fragment() {


    private val viewModel: TeacherViewModel by activityViewModels()

    private var _binding: FragmentTeacherHomeBinding? = null

    private val binding get() = _binding!!
    private var addStudentAdapter = AddStudentAdapter()
    private var skillWiseReportAdapter = skillWiseReportAdapter()


    private lateinit var student: List<Student>
    var clsId = ""
    var stdId = ""
    private lateinit var clas: Class
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.cardAttendance.setOnClickListener {}

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewStdInfo.layoutManager = layoutManager
        binding.recyclerViewStdInfo.adapter = addStudentAdapter

        /*   val modal = ModalBottomSheetDialog()
           supportFragmentManager.let { modal.show(it, ModalBottomSheetDialog.TAG) }*/
        addStudentAdapter.setListener(listener = object : AddStudentAdapter.Listener {

            override fun onAddNewStudentClicked(clsId: String) {
                val dialog = AddNewStdDialog(clsId)
                dialog.show(parentFragmentManager, "CreateStdDialog")
            }

            override fun onClassSelected(
                clsId: String,
                stdId: String,
                studentName: String,
                img: String,

                ) {
                // viewModel.fetchStudentReport(stdId)
                val dialog = SkillDialog(clsId, stdId, studentName, img)
                dialog.show(parentFragmentManager, "ProvideMarksdialog")
            }
        })

        skillWiseReportAdapter.setListener(object : skillWiseReportAdapter.Listener {
            override fun onSkillSelected(skillId: String) {
                val intent = Intent(context, SkillReportActivity::class.java)
                intent.putExtra("skillId", skillId)
                startActivity(intent)

            }
        })
        binding.recyclerViewCardSkill.adapter = skillWiseReportAdapter


        val popup = binding.imgViewMore
        val showPopup = PopupMenu(requireContext(), popup)
        showPopup.menu.add(Menu.NONE, 0, 0, "Share class")
        showPopup.menu.add(Menu.NONE, 1, 1, "Sign out")
        showPopup.gravity = Gravity.END
        popup.setOnClickListener { showPopup.show() }

        showPopup.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {
                val dialog = ClassInviteDialog(clsId)
                dialog.show(parentFragmentManager, ClassInviteDialog.TAG)
            } else if (id == 1) {
                viewModel.signOut()
                var intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            true
        }





        initializeFlowCollectors()
        registerListener()
    }

    fun registerListener() {
        binding.cardClassReport.setOnClickListener {
            val intent = Intent(requireContext(), ClassReportActivity::class.java)
            intent.putExtra("clsId", clsId)
            startActivity(intent)

        }
        binding.cardAttendance.setOnClickListener {
            val intent = Intent(requireContext(), AttendanceActivity::class.java)
            intent.putExtra("clsId", clsId)
            intent.putExtra("stdId", stdId)
            startActivity(intent)
        }
    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selectedClass.collectLatest { cls ->
                    if (cls != null) {
                        Log.d("_xyz", "initializeFlowCollectors: ${cls.classId}")
                        binding.toolbar.title = cls.name
                        Log.d("TAG", "initializeFlowCollectors: stdAdapter called ")
                        addStudentAdapter.setId(cls.classId)
                        clsId = cls.classId
                        clas = cls

                    } else {
                        binding.toolbar.title = "No Class"
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.studentList.collectLatest {

                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {
                                binding.progressBarStudentLoading.visibility = View.VISIBLE
                                binding.recyclerViewStdInfo.visibility = View.INVISIBLE
                            }

                            is Resource.Success -> {
                                if (it.data != null) {
                                    binding.recyclerViewStdInfo.visibility = View.VISIBLE
                                    binding.progressBarStudentLoading.visibility = View.INVISIBLE
                                    it.data?.let { addStudentAdapter.setStudents(it) }

                                }

                                binding.progressBarStudentLoading.visibility = View.INVISIBLE

                            }
                        }

                    }
                }
            }


        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createStudent.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {}
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.posSkillList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                if (it.data != null) {
                                    skillWiseReportAdapter.setClasses(it.data)
                                }
                            }
                        }
                    }
                }
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//https://github.com/google/flexbox-layout