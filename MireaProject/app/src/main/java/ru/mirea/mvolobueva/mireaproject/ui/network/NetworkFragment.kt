package ru.mirea.mvolobueva.mireaproject.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mirea.mvolobueva.mireaproject.R
import ru.mirea.mvolobueva.mireaproject.ui.network.IpApiService
import ru.mirea.mvolobueva.mireaproject.ui.network.IpInfoResponse
import ru.mirea.mvolobueva.mireaproject.ui.network.WeatherApiService
import ru.mirea.mvolobueva.mireaproject.ui.network.WeatherResponse

class NetworkFragment : Fragment(R.layout.fragment_network) {

    private lateinit var textViewIp: TextView
    private lateinit var textViewCity: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewIsp: TextView
    private lateinit var textViewCoordinates: TextView
    private lateinit var textViewWeather: TextView
    private lateinit var buttonLoadNetworkInfo: Button

    private lateinit var ipApiService: IpApiService
    private lateinit var weatherApiService: WeatherApiService

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewIp = view.findViewById(R.id.textViewIp)
        textViewCity = view.findViewById(R.id.textViewCity)
        textViewCountry = view.findViewById(R.id.textViewCountry)
        textViewIsp = view.findViewById(R.id.textViewIsp)
        textViewCoordinates = view.findViewById(R.id.textViewCoordinates)
        textViewWeather = view.findViewById(R.id.textViewWeather)
        buttonLoadNetworkInfo = view.findViewById(R.id.buttonLoadNetworkInfo)

        val ipRetrofit = Retrofit.Builder()
            .baseUrl("http://ip-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherRetrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ipApiService = ipRetrofit.create(IpApiService::class.java)
        weatherApiService = weatherRetrofit.create(WeatherApiService::class.java)

        buttonLoadNetworkInfo.setOnClickListener {
            loadNetworkInfo()
        }
    }

    private fun loadNetworkInfo() {
        textViewIp.text = "IP: загружается..."
        textViewCity.text = "Город: загружается..."
        textViewCountry.text = "Страна: загружается..."
        textViewIsp.text = "Провайдер: загружается..."
        textViewCoordinates.text = "Координаты: загружаются..."
        textViewWeather.text = "Погода: загружается..."

        ipApiService.getIpInfo().enqueue(object : Callback<IpInfoResponse> {
            override fun onResponse(call: Call<IpInfoResponse>, response: Response<IpInfoResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && body.status == "success") {
                        textViewIp.text = "IP: ${body.query}"
                        textViewCity.text = "Город: ${body.city}"
                        textViewCountry.text = "Страна: ${body.country}"
                        textViewIsp.text = "Провайдер: ${body.isp}"
                        textViewCoordinates.text = "Координаты: ${body.lat}, ${body.lon}"

                        loadWeather(body.lat, body.lon)
                    } else {
                        Toast.makeText(requireContext(), "Некорректный ответ сервера", Toast.LENGTH_SHORT).show()
                        showErrorState()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка ответа: ${response.code()}", Toast.LENGTH_SHORT).show()
                    showErrorState()
                }
            }

            override fun onFailure(call: Call<IpInfoResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                showErrorState()
            }
        })
    }

    private fun loadWeather(latitude: Double, longitude: Double) {
        weatherApiService.getWeather(latitude, longitude)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val weather = body.current_weather
                            textViewWeather.text =
                                "Погода: ${weather.temperature}°C, ветер ${weather.windspeed} км/ч, код ${weather.weathercode}"
                        } else {
                            textViewWeather.text = "Погода: нет данных"
                        }
                    } else {
                        textViewWeather.text = "Погода: ошибка ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    textViewWeather.text = "Погода: ошибка сети"
                }
            })
    }

    private fun showErrorState() {
        textViewIp.text = "IP: ошибка"
        textViewCity.text = "Город: ошибка"
        textViewCountry.text = "Страна: ошибка"
        textViewIsp.text = "Провайдер: ошибка"
        textViewCoordinates.text = "Координаты: ошибка"
        textViewWeather.text = "Погода: ошибка"
    }
}