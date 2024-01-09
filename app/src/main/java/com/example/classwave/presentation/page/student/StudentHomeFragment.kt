package com.example.classwave.presentation.page.student

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
import com.example.classwave.databinding.FragmentStudentHomeBinding
import com.example.classwave.presentation.dialog.ClassInviteDialog
import com.example.classwave.presentation.page.main.MainActivity
import com.example.classwave.presentation.page.report.Repo0rtActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudentHomeFragment : Fragment() {


    private val viewModel: StudentViewModel by activityViewModels()

    private var _binding: FragmentStudentHomeBinding? = null
    private var classId = ""

    private val binding get() = _binding!!
    //private var addStudentAdapter = AddStudentAdapter()
    // private var skillWiseReportAdapter = skillWiseReportAdapter()

    /*
        private lateinit var student: List<Student>
        var clsId = ""
        var stdId = ""
        private lateinit var clas: Class*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.cardViewReportBg.setOnClickListener {

            val intent = Intent(requireContext(), Repo0rtActivity::class.java)
            if (classId.isNotEmpty()) {
                intent.putExtra("classId", classId)
                intent.putExtra("student_id", Firebase.auth.uid.toString())
                startActivity(intent)
            }

        }
        viewModel.findUserType(Firebase.auth.uid.toString())


        val popup = binding.imgViewMore
        val showPopup = PopupMenu(requireContext(), popup)
        showPopup.menu.add(Menu.NONE, 0, 0, "Share class")
        showPopup.menu.add(Menu.NONE, 1, 1, "Sign out")
        showPopup.gravity = Gravity.END
        popup.setOnClickListener { showPopup.show() }

        showPopup.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {

            } else if (id == 1) {
                viewModel.signOut()
                var intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            true
        }

        initializeFlowCollectors()
    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selectedClass.collectLatest { cls ->
                    Log.d("TAG", "initializeFlowCollectors: i am cls")
                    if (cls != null) {

                        classId = cls.classId
                        //clas = cls

                    } else {
                        binding.textViewName.text = "No Class"
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest { user ->
                    Log.d("TAG", "initializeFlowCollectors: i am cls")
                    if (user.isNotEmpty()) {
                        binding.textViewName.text = "Welcome, ${user[1]}"
                    } else {
                        binding.textViewName.text = "No Class"
                    }
                }
            }
        }
        /* lifecycleScope.launch {
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

         }*/

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

//https://github.com/google/flexbox-layout