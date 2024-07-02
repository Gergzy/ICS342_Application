package com.example.myapplicationpackage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _currentCheckState = MutableLiveData(false)
    val currentCheckState: LiveData<Boolean> = _currentCheckState
    fun onChange(newCheckState: Boolean) {
        _currentCheckState.value = newCheckState
    }
}