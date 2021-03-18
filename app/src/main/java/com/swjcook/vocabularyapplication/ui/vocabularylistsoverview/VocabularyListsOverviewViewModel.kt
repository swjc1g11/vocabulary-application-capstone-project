package com.swjcook.vocabularyapplication.ui.vocabularylistsoverview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swjcook.vocabularyapplication.domain.VocabularyList
import com.swjcook.vocabularyapplication.domain.VocabularyListWithState
import com.swjcook.vocabularyapplication.repositories.VocabularyListRepository
import kotlinx.coroutines.launch

class VocabularyListsOverviewViewModel(private val repository: VocabularyListRepository) : ViewModel() {

    private val _vocabularyLists = repository.getAllVocabularyListsWithState()
    val vocabularyLists: LiveData<List<VocabularyListWithState>>
        get() = _vocabularyLists

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading : LiveData<Boolean>
        get() = _showLoading

    private val _showFilterBottomSheet = MutableLiveData<Boolean>(false)
    val showFilterBottomSheet : LiveData<Boolean>
        get() = _showFilterBottomSheet

    fun toggleFilterBottomSheet(value: Boolean) {
        _showFilterBottomSheet.value = value
    }

    fun updateVocabularyLists() {
        _showLoading.value = true
        viewModelScope.launch {
            Log.i("NetworkRequest", "Making a network request")
            repository.downloadAllVocabularyLists()
            _showLoading.value = false
        }
    }
}