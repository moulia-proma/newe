package com.example.classwave.presentation.page.student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.FragmentStudentHomeBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.ClassInviteDialog
import com.example.classwave.presentation.dialog.EnterClassCodeDialog
import com.example.classwave.presentation.dialog.ShareProfileDialog
import com.example.classwave.presentation.page.main.MainActivity
import com.example.classwave.presentation.page.report.Repo0rtActivity
import com.example.classwave.presentation.page.teacher.Class
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudentActivity : AppCompatActivity() {
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var binding: FragmentStudentHomeBinding
    private lateinit var headerBinding: NavDrawerBinding
    private var joinClassAdapter = JoinClassAdapter()
    private var classId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        val uName = intent.getStringExtra("name").toString()
        var uEmail = intent.getStringExtra("email").toString()

        super.onCreate(savedInstanceState)
        binding = FragmentStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

/*        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setupWithNavController(navController)*/

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
        binding.cardViewReportBg.setOnClickListener {

            val intent = Intent(this, Repo0rtActivity::class.java)
            if (classId.isNotEmpty()) {
                intent.putExtra("classId", classId)
                intent.putExtra("student_id", Firebase.auth.uid.toString())
                startActivity(intent)
            }

        }
        viewModel.findUserType(Firebase.auth.uid.toString())
        val popup = binding.imgViewMore
        val showPopup = PopupMenu(this, popup)
        showPopup.menu.add(Menu.NONE, 0, 0, "Share class")
        showPopup.menu.add(Menu.NONE, 1, 1, "Sign out")
        showPopup.gravity = Gravity.END
        popup.setOnClickListener { showPopup.show() }

        showPopup.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {
                val dialog = ShareProfileDialog(Firebase.auth.uid.toString())
                dialog.show(supportFragmentManager, ClassInviteDialog.TAG)
            } else if (id == 1) {
                viewModel.signOut()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            true
        }
        binding.appBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
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
                viewModel.classList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                viewModel.showClasses(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest { user ->
                    if (user.isNotEmpty()) {
                        binding.textViewName.text = "Welcome, ${user[1]}"
                    } else {
                        binding.textViewName.text = "No Class"
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