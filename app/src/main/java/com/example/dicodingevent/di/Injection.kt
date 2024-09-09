package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.database.FavoriteEventRoomDatabase
import com.example.dicodingevent.repository.FavoriteEventRepository
import com.example.dicodingevent.ui.setting.SettingPreferences
import com.example.dicodingevent.ui.setting.dataStore
import kotlinx.coroutines.Dispatchers

object Injection {
    fun provideRepository(context: Context): FavoriteEventRepository {
        val database = FavoriteEventRoomDatabase.getDatabase(context)
        return FavoriteEventRepository(database.favoriteEventDao(), Dispatchers.IO)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}