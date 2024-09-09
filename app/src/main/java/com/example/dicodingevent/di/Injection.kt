package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.database.FavoriteEventRoomDatabase
import com.example.dicodingevent.repository.FavoriteEventRepository
import com.example.dicodingevent.ui.setting.SettingPreferences
import com.example.dicodingevent.ui.setting.dataStore

object Injection {
    fun provideRepository(context: Context): FavoriteEventRepository {
        val database by lazy { FavoriteEventRoomDatabase.getDatabase(context) }
        return FavoriteEventRepository(database.favoriteEventDao())
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}