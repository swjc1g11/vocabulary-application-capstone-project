package com.swjcook.vocabularyapplication.ui.vocabularylistsoverview

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.FragmentFilterListsBottomSheetBinding

interface FilterListsBottomSheetListener {
    fun onCancel(): Unit
    fun onDismiss(): Unit
}

class FilterListsBottomSheetFragment() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterListsBottomSheetBinding
    private var _viewModel: VocabularyListsOverviewViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_lists_bottom_sheet, container, false)
        return binding.root
    }

    fun setViewModel(viewModel: VocabularyListsOverviewViewModel) {
        _viewModel = viewModel
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _viewModel?.toggleFilterBottomSheet(false)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        _viewModel?.toggleFilterBottomSheet(false)
    }
}