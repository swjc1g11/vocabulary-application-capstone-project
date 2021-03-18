package com.swjcook.vocabularyapplication.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val _imageUrl = MutableLiveData<String>().apply {
        value = "https://cdn.pixabay.com/photo/2015/12/05/19/49/bridge-1078671_1280.jpg"
    }
    val imageUrl: LiveData<String> = _imageUrl
}