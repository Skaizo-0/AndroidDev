package ru.mirea.mvolobueva.httpurlconnection

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLoadInfo: Button
    private lateinit var textViewIp: TextView
    private lateinit var textViewCity: TextView
    private lateinit var textViewRegion: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewCoordinates: TextView
    private lateinit var textViewWeather: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoadInfo = findViewById(R.id.buttonLoadInfo)
        textViewIp = findViewById(R.id.textViewIp)
        textViewCity = findViewById(R.id.textViewCity)
        textViewRegion = findViewById(R.id.textViewRegion)
        textViewCountry = findViewById(R.id.textViewCountry)
        textViewCoordinates = findViewById(R.id.textViewCoordinates)
        textViewWeather = findViewById(R.id.textViewWeather)

        buttonLoadInfo.setOnClickListener {
            loadIpAndWeatherInfo()
        }
    }

    private fun loadIpAndWeatherInfo() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show()
            return
        }

        textViewIp.text = "IP: загружается..."
        textViewCity.text = "Город: загружается..."
        textViewRegion.text = "Регион: загружается..."
        textViewCountry.text = "Страна: загружается..."
        textViewCoordinates.text = "Координаты: загружаются..."
        textViewWeather.text = "Погода: загружается..."

        Thread {
            try {
                val ipInfoJsonString = downloadUrl("http://ip-api.com/json/")
                val ipJson = JSONObject(ipInfoJsonString)

                val status = ipJson.optString("status", "fail")
                if (status != "success") {
                    throw IOException("IP API error: $ipInfoJsonString")
                }

                val ip = ipJson.optString("query", "--")
                val city = ipJson.optString("city", "--")
                val region = ipJson.optString("regionName", "--")
                val country = ipJson.optString("country", "--")
                val latitude = ipJson.optDouble("lat", Double.NaN)
                val longitude = ipJson.optDouble("lon", Double.NaN)

                val coordinatesText = if (!latitude.isNaN() && !longitude.isNaN()) {
                    "$latitude, $longitude"
                } else {
                    "--"
                }

                var weatherText = "Погода: не удалось получить"

                if (!latitude.isNaN() && !longitude.isNaN()) {
                    val weatherUrl =
                        "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"

                    val weatherJsonString = downloadUrl(weatherUrl)
                    val weatherJson = JSONObject(weatherJsonString)
                    val currentWeather = weatherJson.getJSONObject("current_weather")

                    val temperature = currentWeather.optDouble("temperature", Double.NaN)
                    val windSpeed = currentWeather.optDouble("windspeed", Double.NaN)
                    val weatherCode = currentWeather.optInt("weathercode", -1)

                    weatherText =
                        "Погода: температура ${temperature}°C, ветер ${windSpeed} км/ч, код погоды $weatherCode"
                }

                runOnUiThread {
                    textViewIp.text = "IP: $ip"
                    textViewCity.text = "Город: $city"
                    textViewRegion.text = "Регион: $region"
                    textViewCountry.text = "Страна: $country"
                    textViewCoordinates.text = "Координаты: $coordinatesText"
                    textViewWeather.text = weatherText
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    textViewIp.text = "IP: ошибка"
                    textViewCity.text = "Город: ошибка"
                    textViewRegion.text = "Регион: ошибка"
                    textViewCountry.text = "Страна: ошибка"
                    textViewCoordinates.text = "Координаты: ошибка"
                    textViewWeather.text = "Погода: ошибка"
                }
            }
        }.start()
    }

    @Throws(IOException::class)
    private fun downloadUrl(address: String): String {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        return try {
            val url = URL(address)
            connection = url.openConnection() as HttpURLConnection

            connection.readTimeout = 15000
            connection.connectTimeout = 15000
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true
            connection.useCaches = false
            connection.doInput = true
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Android Lesson7 App")

            val responseCode = connection.responseCode

            inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: throw IOException("HTTP error code: $responseCode")
            }

            val reader = BufferedReader(InputStreamReader(inputStream))
            val result = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line)
            }

            reader.close()

            if (responseCode !in 200..299) {
                throw IOException("HTTP $responseCode: $result")
            }

            result.toString()
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }
}