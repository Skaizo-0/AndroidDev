package ru.mirea.mvolobueva.mireaproject.ui.network

data class IpInfoResponse(
    val status: String,
    val query: String,
    val city: String,
    val country: String,
    val isp: String,
    val lat: Double,
    val lon: Double
)