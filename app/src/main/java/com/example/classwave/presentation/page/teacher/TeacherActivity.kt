package com.example.classwave.presentation.page.teacher

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
import com.example.classwave.databinding.ActivityTeacherBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.domain.model.Resource
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

        var badge = binding.bottomNavigationView.getOrCreateBadge(R.id.chat)
        badge.isVisible = true
        badge.number = 99

/*        binding.bottomNavigationView.setOnItemSelectedListener {
            Log.d("_xyz", "onCreate: $it")
            true
        }*/


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setupWithNavController(navController)


        headerBinding =
            NavDrawerBinding.inflate(layoutInflater, binding.navigationViewDrawer, false)
        binding.navigationViewDrawer.addHeaderView(headerBinding.root)
        binding.drawerLayout.openDrawer(GravityCompat.START)

        headerBinding.recyclerView.adapter = addClassAdapter
        addClassAdapter.setListener(listener = object : AddClassAdapter.Listener {
            override fun onAddNewClassClicked() {
                showCreateClassDialog()
            }

            override fun onClassSelected(cls: Class) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                viewModel.updateClass(cls = cls)
            }

            override fun onEditClassClicked(cls: Class) {
                var dialog = CreateClassDialog(
                    cls.classId,
                    cls.teacherId,
                    cls.name,
                    cls.img,
                    cls.grade,
                    "update"
                )
                dialog.show(supportFragmentManager, CreateClassDialog.TAG)
            }
        })


        initializeFlowCollectors()
    }


    private fun showCreateClassDialog() {
        /*   supportFragmentManager.findFragmentByTag(CreateClassDialog.TAG)?.let {
               supportFragmentManager.beginTransaction().remove(it).commit()
           }*/
        val dialog = CreateClassDialog("", "", "", "", "", "create")
        dialog.show(supportFragmentManager, CreateClassDialog.TAG)
    }


    private fun initializeFlowCollectors() {


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.classList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> showErrorMessage(it.message ?: "")
                            is Resource.Loading -> showLoadingView()
                            is Resource.Success -> showSuccessView(it.data)
                        }
                    }
                }
            }
        }

    }

    private fun showSuccessView(classList: List<Class>?) {
        headerBinding.progressBarClassLoading.visibility = View.INVISIBLE
        if (!classList.isNullOrEmpty()) {
            viewModel.updateClass(classList[0])
            addClassAdapter.setClasses(classList)
        } else {
            viewModel.updateClass(null)
            addClassAdapter.setClasses(listOf())
        }
    }

    private fun showErrorMessage(message: String) {
        /*   val view = LayoutInflater.from(applicationContext).inflate(R.layout.create_class_dialog,,false)
           SnackbarUtil.show(this, message,view.findViewById(R.id.btn_create_class) )*/

    }

    private fun showLoadingView() {
        headerBinding.progressBarClassLoading.visibility = View.VISIBLE

    }

}