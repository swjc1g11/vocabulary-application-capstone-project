package com.swjcook.vocabularyapplication.ui.vocabularylistsoverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.FragmentVocabularyListsOverviewBinding
import com.swjcook.vocabularyapplication.domain.VocabularyListWithState
import org.koin.androidx.viewmodel.ext.android.viewModel

class VocabularyListsOverviewFragment : Fragment(), VocabularyListOverviewAdapterListener {

    companion object {
        const val TAG = "VocabularyListsOverview"
    }

    private lateinit var binding: FragmentVocabularyListsOverviewBinding
    val _viewModel by viewModel<VocabularyListsOverviewViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentVocabularyListsOverviewBinding>(inflater, R.layout.fragment_vocabulary_lists_overview, container, false)

        binding.refreshLayout.setOnRefreshListener { _viewModel.updateVocabularyLists() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = _viewModel

        binding.listOfVocabularyLists.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = VocabularyListOverviewAdapter(this)
        binding.listOfVocabularyLists.adapter = adapter

        _viewModel.vocabularyLists.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isEmpty()) {
                    _viewModel.updateVocabularyLists()
                }
                adapter.submitList(it)
            }
        })

        _viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.refreshLayout.isRefreshing = true
            } else {
                binding.refreshLayout.isRefreshing = false
            }
        })

        _viewModel.showFilterBottomSheet.observe(viewLifecycleOwner, Observer {
            if (it) {
                val fragment = FilterListsBottomSheetFragment()
                fragment.setViewModel(_viewModel)
                fragment.show(childFragmentManager, tag)
            }
        })

    }

    override fun onVocabularyListClick(listWithState: VocabularyListWithState) {
        // findNavController().navigate(VocabularyListsOverviewFragmentDirections.)
        findNavController().navigate(
            VocabularyListsOverviewFragmentDirections.actionNavHomeToVocabularyListDetailFragment(
                listWithState.list._id
            )
        )
        // Snackbar.make(requireView(), listWithState.list.title, Snackbar.LENGTH_LONG).show()
    }
}