package com.example.dicodingevent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.repository.FavoriteEventRepository
import com.example.dicodingevent.ui.detail.DetailViewModel

class ViewModelFactory(private val repository: FavoriteEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(repository: FavoriteEventRepository): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(repository).also { INSTANCE = it }
            }
        }
    }
}
