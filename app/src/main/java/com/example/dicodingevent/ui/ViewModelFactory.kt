package com.example.dicodingevent.ui

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.ui.detail.DetailViewModel
import com.example.dicodingevent.ui.favorite.FavoriteEventViewModel
import com.example.dicodingevent.ui.setting.SettingViewModel

object ViewModelFactory {
    fun getInstance(context: Context) = viewModelFactory {
        val repository = Injection.provideRepository(context)
        val pref = Injection.provideSettingPreferences(context)
        val apiService = ApiConfig.apiService

        initializer {
            DetailViewModel(repository)
        }

        initializer {
            FavoriteEventViewModel(repository)
        }

        initializer {
            SettingViewModel(pref)
        }

        initializer {
            EventViewModel(apiService)
        }
    }
}
