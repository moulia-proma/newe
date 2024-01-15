package com.example.classwave.presentation.page.teacher

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.FragmentTeacherChatBinding
import com.example.classwave.domain.model.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule


class TeacherChatFragment : Fragment() {
    private var _binding: FragmentTeacherChatBinding? = null
    private val viewmodel: TeacherViewModel by activityViewModels()
    private var clsId = ""
    private val binding get() = _binding!!

    private val oldNotificationAdapter = oldNotificationAdapter()
    private val newNotificationAdapter = NewNotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTeacherChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewNewNotifications.adapter = newNotificationAdapter

        initializeFlowCollectors()
    }

    fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewmodel.selectedClass.collectLatest { cls ->
                    if (cls != null) {
                        Timer().schedule(3000) {
                            viewmodel.updateNotificationState(cls.classId)
                        }
                    } else {
                        binding.toolbar.title = "No Class"
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewmodel.newNotificationList.collectLatest {
                it?.let {
                    when (it) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            it.data?.let { it1 -> newNotificationAdapter.setNotification(it1) }
                        }
                    }
                }
            }
        }

/*        lifecycleScope.launch {
            viewmodel.oldNotificationList.collectLatest {
                it?.let {
                    when (it) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            it.data?.let { it1 -> newNotificationAdapter.setNotification(it1) }
                        }
                    }
                }
            }
        }*/


    }


}

