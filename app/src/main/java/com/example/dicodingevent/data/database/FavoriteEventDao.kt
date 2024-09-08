package com.example.dicodingevent.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Update
    suspend fun update(favoriteEvent: FavoriteEvent)

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * from favoriteEvent ORDER BY id ASC")
    fun getAllFavoriteEvent(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * from favoriteEvent WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent?>
}
