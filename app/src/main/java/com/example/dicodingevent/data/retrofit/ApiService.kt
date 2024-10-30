package com.example.dicodingevent.data.retrofit

import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: String): DetailEventResponse
}
