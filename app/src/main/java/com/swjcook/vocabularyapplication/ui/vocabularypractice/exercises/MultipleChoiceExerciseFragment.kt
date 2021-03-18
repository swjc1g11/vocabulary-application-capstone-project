package com.swjcook.vocabularyapplication.ui.vocabularypractice.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.swjcook.vocabularyapplication.R
import com.swjcook.vocabularyapplication.databinding.FragmentMultipleChoiceExerciseBinding
import com.swjcook.vocabularyapplication.ui.vocabularypractice.VocabularyPracticeFragment
import com.swjcook.vocabularyapplication.ui.vocabularypractice.VocabularyPracticeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val MULTIPLE_CHOICE_QUESTION = MultipleChoiceExercise.EXERCISE_TYPE

/**
 * A simple [Fragment] subclass.
 * Use the [MultipleChoiceExerciseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MultipleChoiceExerciseFragment : Fragment() {
    private lateinit var _viewModel : VocabularyPracticeViewModel
    private lateinit var binding : FragmentMultipleChoiceExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (requireParentFragment() !is VocabularyPracticeFragment) {
            throw ClassCastException("MultipleChoiceExerciseFragment must be a child fragment of VocabularyPracticeFragment.")
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_multiple_choice_exercise, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        val myParent = requireParentFragment() as VocabularyPracticeFragment
        _viewModel = myParent._viewModel
        binding.viewModel = _viewModel

        _viewModel.currentExercise.observe(viewLifecycleOwner, Observer {
            if (it is MultipleChoiceExercise) {
                binding.multipleChoiceExercise = it
            }
        })
    }

    fun setViewModel(viewModel: VocabularyPracticeViewModel) {
        _viewModel = viewModel
    }
}