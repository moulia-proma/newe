package com.example.classwave.presentation.page.student

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.classwave.R
import com.example.classwave.databinding.ActivityStudentBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.EnterClassCodeDialog
import com.example.classwave.presentation.page.teacher.Class
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudentActivity : AppCompatActivity() {
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var binding: ActivityStudentBinding
    private lateinit var headerBinding: NavDrawerBinding
    private var joinClassAdapter = JoinClassAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        val uName = intent.getStringExtra("name").toString()
        var uEmail = intent.getStringExtra("email").toString()

        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setupWithNavController(navController)

        headerBinding =
            NavDrawerBinding.inflate(layoutInflater, binding.navigationViewDrawer, false)
        binding.navigationViewDrawer.addHeaderView(headerBinding.root)
        binding.drawerLayout.openDrawer(GravityCompat.START)

        headerBinding.recyclerView.adapter = joinClassAdapter
        joinClassAdapter.setListener(listener = object : JoinClassAdapter.Listener {
            override fun onAddNewClassClicked() {
                Log.d("pro", "onAddNewClassClicked: $uName")
                val dialog = EnterClassCodeDialog("", "", "", "", "", "create", uName)
                dialog.show(supportFragmentManager, EnterClassCodeDialog.TAG)
            }

            override fun onClassSelected(cls: com.example.classwave.presentation.page.teacher.Class) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                viewModel.updateClass(cls = cls)
            }

            override fun onEditClassClicked(cls: Class) {
                var dialog = EnterClassCodeDialog(
                    cls.classId,
                    cls.teacherId,
                    cls.name,
                    cls.img,
                    cls.grade,
                    "update",
                    uName
                )
                dialog.show(supportFragmentManager, EnterClassCodeDialog.TAG)
            }
        })

        viewModel.fetchClassList(Firebase.auth.uid.toString())
        initializeFlowCollectors()

    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.classList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                Log.d("_pro", "initializeFlowCollectors: ab ${it.data}")
                                viewModel.showClasses(it.data)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.newClassList.collectLatest {


                    it?.let {

                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                Log.d("_pro", "initializeFlowCollectors: c ${it.data}")
                                showSuccessView(it.data)
                            }
                        }
                    }
                }
            }
        }


        /*     lifecycleScope.launch {
                 repeatOnLifecycle(Lifecycle.State.CREATED) {
                     viewModel.classList.collectLatest {
                         it?.let {
                             when (it) {
                                 is Resource.Error -> {}
                                 is Resource.Loading -> {}
                                 is Resource.Success -> {
                                     showSuccessView(it.data)
                                 }
                             }
                         }
                     }
                 }
             }*/

    }

    private fun showSuccessView(classList: List<Class>?) {
        headerBinding.progressBarClassLoading.visibility = View.INVISIBLE
        if (!classList.isNullOrEmpty()) {
            viewModel.updateClass(classList[0])

            joinClassAdapter.setClasses(classList)
        } else {
            viewModel.updateClass(null)
            joinClassAdapter.setClasses(listOf())
        }
    }


    private fun showSuccessViewClassList(classList: List<Class>?) {
        headerBinding.progressBarClassLoading.visibility = View.INVISIBLE
        if (!classList.isNullOrEmpty()) {
            viewModel.updateClass(classList[0])
            joinClassAdapter.setClasses(classList)
        } else {
            viewModel.updateClass(null)
            joinClassAdapter.setClasses(listOf())
        }
    }
}