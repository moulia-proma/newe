package com.example.classwave.presentation.page.teacher

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.classwave.R
import com.example.classwave.databinding.ActivityTeacherBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.presentation.dialog.AddNewStdDialog
import com.example.classwave.presentation.dialog.CreateClassDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



@AndroidEntryPoint
class TeacherActivity : AppCompatActivity() {

    private val viewModel: TeacherViewModel by viewModels()

    private lateinit var binding: ActivityTeacherBinding
    private lateinit var headerBinding: NavDrawerBinding

    private var addClassAdapter = AddClassAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setupWithNavController(navController)


        headerBinding =
            NavDrawerBinding.inflate(layoutInflater, binding.navigationViewDrawer, false)
        binding.navigationViewDrawer.addHeaderView(headerBinding.root)
        headerBinding.recyclerView.adapter = addClassAdapter
        addClassAdapter.setListener(listener = object : AddClassAdapter.Listener {
            override fun onAddNewClassClicked() {
                Log.d("_xyz", "onAddNewClassClicked: clicked")
                showCreateClassDialog()
            }

            override fun onClassSelected(cls: Class) {

                Log.d("_xyz", "onClassSelected: clicked")
            }
        })


        viewModel.fetchClassByTeacher(teacherId = "abc")

        initializeFlowCollectors()
    }


    private fun showCreateClassDialog() {
        val dialog = CreateClassDialog()
        dialog.show(supportFragmentManager,"CreateStdDialog")


    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.classList.collectLatest {
                    addClassAdapter.setClasses(it)
                }
            }
        }
    }

}