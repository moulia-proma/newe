package com.example.classwave.presentation.page.student

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.classwave.R
import com.example.classwave.databinding.ActivityTeacherBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.presentation.dialog.EnterClassCodeDialog
import com.example.classwave.presentation.page.teacher.AddClassAdapter
import com.example.classwave.presentation.page.teacher.Class
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherBinding
    private lateinit var headerBinding: NavDrawerBinding
    private var joinClassAdapter = JoinClassAdapter()
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
        binding.drawerLayout.openDrawer(GravityCompat.START)

        headerBinding.recyclerView.adapter = joinClassAdapter
        joinClassAdapter.setListener(listener = object : JoinClassAdapter.Listener {
            override fun onAddNewClassClicked() {
                val dialog = EnterClassCodeDialog("","","","","","create")
                dialog.show(supportFragmentManager,EnterClassCodeDialog.TAG)
            }

            override fun onClassSelected(cls: com.example.classwave.presentation.page.teacher.Class) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
              //  viewModel.updateClass(cls = cls)
            }

            override fun onEditClassClicked(cls: Class) {
                var dialog = EnterClassCodeDialog(
                    cls.classId,
                    cls.teacherId,
                    cls.name,
                    cls.img,
                    cls.grade,
                    "update"
                )
                dialog.show(supportFragmentManager, EnterClassCodeDialog.TAG)
            }
        })

    }
}