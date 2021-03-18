package com.swjcook.vocabularyapplication.ui.vocabularylistdetail

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.swjcook.vocabularyapplication.MainActivity
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.VocabularyListDetailFragmentBinding
import com.swjcook.vocabularyapplication.domain.VocabularyListDetailItem
import com.swjcook.vocabularyapplication.domain.VocabularyListStudyReminder
import com.swjcook.vocabularyapplication.domain.VocabularyListWithWords
import com.swjcook.vocabularyapplication.domain.WordWithState
import com.swjcook.vocabularyapplication.receivers.VocabularyListStudyReminderReceiver
import com.swjcook.vocabularyapplication.util.sendNotification
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class VocabularyListDetailFragment : Fragment() {

    companion object {
        const val TAG = "VocabListDetailFrag"
    }

    private val _viewModel by viewModel<VocabularyListDetailViewModel>()
    private lateinit var binding: VocabularyListDetailFragmentBinding
    private lateinit var adapter : VocabularyListDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.vocabulary_list_detail_fragment, container, false)

        binding.refreshLayout.setOnRefreshListener {
            _viewModel.updateListById(VocabularyListDetailFragmentArgs.fromBundle(requireArguments()).listUUID)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vocabularyListDetailRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = VocabularyListDetailAdapter(mutableListOf<VocabularyListDetailItem>(), object: VocabularyListDetailAdapterListener {
            override fun onPracticeClick() {
                val temp = _viewModel.uuid.value
                val uuid : String = when(temp) {
                    null -> ""
                    else -> temp
                }
                findNavController().navigate(
                    VocabularyListDetailFragmentDirections.actionVocabularyListDetailFragmentToVocabularyPracticeFragment(uuid)
                )
            }

            override fun onAddEditReminderClick(currentListStudyReminder: VocabularyListStudyReminder?, canShowStudyReminderForDate: Date?) {
                if (currentListStudyReminder != null) {
                    Snackbar.make(requireView(), getString(R.string.fragment_vocabulary_list_details_reminder_deleted), Snackbar.LENGTH_LONG).show()
                    _viewModel.deleteStudyReminder()
                } else {
                    if (canShowStudyReminderForDate != null) {
                        val dateFormat = DateFormat.getDateFormat(requireContext())
                        val picker =
                                MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_12H)
                                        .setHour(7)
                                        .setMinute(0)
                                        .setTitleText(String.format(
                                                getString(R.string.fragment_vocabulary_list_details_set_reminder_for),
                                                dateFormat.format(canShowStudyReminderForDate)
                                        ))
                                        .build()
                        picker.addOnPositiveButtonClickListener {
                            val hour = picker.hour
                            val minute = picker.minute
                            _viewModel.setStudyReminder(canShowStudyReminderForDate, hour, minute)
                        }
                        picker.show(childFragmentManager, "tagTimePicker")
                    }
                }
            }
        })
        binding.vocabularyListDetailRecyclerView.adapter = adapter

        val id = VocabularyListDetailFragmentArgs.fromBundle(requireArguments()).listUUID
        _viewModel.setListId(id)
        _viewModel.updateExpiredWordState()

        _viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                binding.refreshLayout.isRefreshing = false
            } else {
                binding.refreshLayout.isRefreshing = true
            }
        })

        var count = 0
        _viewModel.vocabularyListWithDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size <= 1) {
                    _viewModel.updateListById(id)
                }
                adapter.submitList(it)
                _viewModel.checkStudyReminderAndDeleteIfExpired()
            }
        })
    }
}