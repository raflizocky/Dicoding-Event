package com.example.dicodingevent.repository

import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.database.FavoriteEventDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteEventRepository(
    private val favoriteEventDao: FavoriteEventDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    fun getAllFavoriteEvents(): Flow<List<FavoriteEvent>> = favoriteEventDao.getAllFavoriteEvent()

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

    fun getFavoriteEventById(id: String): Flow<FavoriteEvent?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }
}
