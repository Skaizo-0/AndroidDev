package ru.mirea.mvolobueva.mireaproject.ui.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast?current_weather=true")
    fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<WeatherResponse>
}