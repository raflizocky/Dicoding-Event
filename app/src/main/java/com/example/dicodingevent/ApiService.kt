package com.example.dicodingevent

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>
}
