package com.swjcook.vocabularyapplication.ui.vocabularypractice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.FragmentVocabularyPracticeBinding
import com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises.MultipleChoiceExercise
import com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises.MultipleChoiceExerciseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.acl.Owner


class VocabularyPracticeFragment : Fragment() {

    val _viewModel: VocabularyPracticeViewModel by viewModel()
    private lateinit var binding : FragmentVocabularyPracticeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vocabulary_practice, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        _viewModel.wordsWithState.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size >= 2) {
                    if (!_viewModel.exercisesGenerated) {
                        _viewModel.generateExercises(it)
                    }
                }
            }
        })

        _viewModel.currentExercise.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it is MultipleChoiceExercise) {
                    binding.fragmentContainer.removeAllViews()
                    val fragment = MultipleChoiceExerciseFragment()
                    //fragment.setViewModel(_viewModel)
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, fragment, "fragment")
                        .commit()
                }
            } else {
                Log.i("VocabPractice", "Removing all views...nulll")
                if (_viewModel.exercisesGenerated) {
                    findNavController().popBackStack()
                }
            }
        })

        _viewModel.progress.observe(viewLifecycleOwner, Observer {
            Log.i("VocabPractice", it.toString())
            binding.progressBar.setProgressCompat(it, true)
        })

        _viewModel.setListUUID(VocabularyPracticeFragmentArgs.fromBundle(requireArguments()).listUUID)
    }
}