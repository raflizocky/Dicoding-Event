package com.example.dicodingevent.repository

import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.database.FavoriteEventDao
import kotlinx.coroutines.flow.Flow

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {
    fun getAllFavoriteEvents(): Flow<List<FavoriteEvent>> = favoriteEventDao.getAllFavoriteEvent()

    suspend fun insert(favoriteEvent: FavoriteEvent) = favoriteEventDao.insert(favoriteEvent)

    suspend fun delete(favoriteEvent: FavoriteEvent) = favoriteEventDao.delete(favoriteEvent)

    fun getFavoriteEventById(id: String): Flow<FavoriteEvent?> =
        favoriteEventDao.getFavoriteEventById(id)
}
