package com.example.dicodingevent.repository

import androidx.lifecycle.LiveData
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.database.FavoriteEventDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FavoriteEventRepository(
    private val favoriteEventDao: FavoriteEventDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = favoriteEventDao.getAllFavoriteEvent()

    suspend fun insert(favoriteEvent: FavoriteEvent) {
        withContext(ioDispatcher) {
            favoriteEventDao.insert(favoriteEvent)
        }
    }

    suspend fun delete(favoriteEvent: FavoriteEvent) {
        withContext(ioDispatcher) {
            favoriteEventDao.delete(favoriteEvent)
        }
    }

    suspend fun update(favoriteEvent: FavoriteEvent) {
        withContext(ioDispatcher) {
            favoriteEventDao.update(favoriteEvent)
        }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }
}
