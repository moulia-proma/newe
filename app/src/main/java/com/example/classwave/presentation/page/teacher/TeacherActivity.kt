package com.example.classwave.presentation.page.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.classwave.R
import com.example.classwave.databinding.ActivityTeacherBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class TeacherActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTeacherBinding

    var imageList= arrayListOf<Int>(
        R.drawable.boy,
        R.drawable.bussiness_man,
        R.drawable.profile_boy,
        R.drawable.profile ,
        R.drawable.profile_man,
        R.drawable.user
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var headerBinding: NavDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_tcr) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bttom_nav)
        navView.setupWithNavController(navController)

        val actionBarDrawerToggle =  ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, com.example.classwave.R.string.nav_close)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        binding.drawerLayout.openDrawer(binding.navView)
        actionBarDrawerToggle.syncState();
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        headerBinding = NavDrawerBinding.inflate(layoutInflater, binding.navView, false)
        binding.navView.addHeaderView(headerBinding.root)

        val adapter =  AddClassAdapter(this,getStudents())
        headerBinding.recyclerViewClsInfo.adapter = adapter


        /* navView.setNavigationItemSelectedListener {
             when (it.itemId) {
                 R.id.chat -> {
                     if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                         drawerLayout.closeDrawer(GravityCompat.START)
                     }

                     val fragmentManager = supportFragmentManager
                     val fragmentTrasiction = fragmentManager.beginTransaction()
                     fragmentTrasiction.replace(R.id.nav_host_fragment, ChatFragment())
                         .commit()

                     true

                 }

                 else -> {

                     if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                         drawerLayout.closeDrawer(GravityCompat.START)
                     }
                     val fragmentManager = supportFragmentManager
                     val fragmentTrasiction = fragmentManager.beginTransaction()
                     fragmentTrasiction.replace(R.id.nav_host_fragment, GreenScreenFragment())
                         .commit()
                     true


                 }
             }


         }*/
    }
    fun getStudents(): ArrayList<Student> {

        val Students: ArrayList<Student> = arrayListOf()

        Students.add(
            Student(
                "mak",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "malaika",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mina",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mikasa",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mizuka",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mifan",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mouli",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mou",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "maz",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mas",
                imageList.random()
            )

        )

        return Students

    }
}