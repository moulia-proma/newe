package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogSkillsBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import com.example.classwave.presentation.page.teacher.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SkillDialog(val classId: String, val stdId: String,val stdName:String,val stdProfile:String) : DialogFragment() {

    val tabArray = arrayOf(
        "Positive",
        "Needs work"
    )

    private val viewModel: TeacherViewModel by activityViewModels()
    private var _binding: DialogSkillsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "onAddNewSkillClicked: abcd ${classId}")
        _binding = DialogSkillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle,classId,stdId)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()

        binding.textViewName.text= stdName
        binding.imgViewProfilePic.setImageResource(stdProfile.toInt())

        registerListener()
    }

    private fun registerListener() {
        binding.imgCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "ProvideMarksdialog"
    }
}