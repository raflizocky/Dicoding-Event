package com.example.dicodingevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.repository.FavoriteEventRepository
import kotlinx.coroutines.flow.Flow

class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {
    val favorites: Flow<List<FavoriteEvent>> = repository.getAllFavoriteEvents()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        _isLoading.value = false
    }
}