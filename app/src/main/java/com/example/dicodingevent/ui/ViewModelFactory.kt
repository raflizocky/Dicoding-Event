package com.example.dicodingevent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.repository.FavoriteEventRepository
import com.example.dicodingevent.ui.detail.DetailViewModel
import com.example.dicodingevent.ui.favorite.FavoriteEventViewModel
import com.example.dicodingevent.ui.setting.SettingPreferences
import com.example.dicodingevent.ui.setting.SettingViewModel

class ViewModelFactory private constructor(
    private val repository: FavoriteEventRepository,
    private val pref: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavoriteEventViewModel::class.java) -> {
                FavoriteEventViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val repository = Injection.provideRepository(context)
                val pref = Injection.provideSettingPreferences(context)
                ViewModelFactory(repository, pref).also { INSTANCE = it }
            }
        }
    }
}
