package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.database.FavoriteEventRoomDatabase
import com.example.dicodingevent.repository.FavoriteEventRepository
import kotlinx.coroutines.Dispatchers

object Injection {
    fun provideRepository(context: Context): FavoriteEventRepository {
        val database = FavoriteEventRoomDatabase.getDatabase(context)
        return FavoriteEventRepository(database.favoriteEventDao(), Dispatchers.IO)
    }
}