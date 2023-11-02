package com.dhanush.disneytest

import DailyForecast
import ForecastResponse
import WeatherApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@RestController
class RestController {
    private val webClient: WebClient = WebClient.create()
    @GetMapping("/current_data")
    fun getCurrentDayData(): Mono<ForecastResponse> {
        val url = "https://api.weather.gov/gridpoints/MLB/33,70/forecast"
        return webClient.get().uri(url).retrieve()
                .bodyToMono(WeatherApiResponse::class.java)
                .mapNotNull { response ->
                    //Current data is always on the top.
                    val requiredPeriods = response.properties.periods.take(2)
                    val currentPeriod = requiredPeriods.firstOrNull()
                    val nextPeriod = requiredPeriods.getOrNull(1)
                    //I am writing two approaches here. I am not sure about the term forecast_blurp. But assuming that
                    //it is asking me for the next short forecast, I am storing the current period and next period.
                    //if the requirement is for the current time forecast then I can use the commented code below.
                    if (currentPeriod != null && nextPeriod != null) {
                        val dayName = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        val tempCelsius = if (currentPeriod.temperatureUnit == "F") {
                            (currentPeriod.temperature - 32) * 5 / 9
                        } else {
                            currentPeriod.temperature
                        }
                        ForecastResponse(
                                daily = listOf(
                                        DailyForecast(
                                                day_name = dayName,
                                                temp_high_celsius = tempCelsius,
                                                forecast_blurp = nextPeriod.shortForecast
                                        )
                                )
                        )
                    } else {
                        throw RuntimeException("Forecast data not available")
                    }
//                    val currentDayForecast = response.properties.periods.firstOrNull()
//                    currentDayForecast?.let {
//                        val dayName = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
//                        val tempCelsius = if (it.temperatureUnit == "F") {
//                            (it.temperature - 32) * 5 / 9
//                        } else {
//                            it.temperature
//                        }
//                        ForecastResponse(
//                                daily = listOf(
//                                        DailyForecast(
//                                                day_name = dayName,
//                                                temp_high_celsius = tempCelsius,
//                                                forecast_blurp = it.shortForecast
//                                        )
//                                )
//                        )
//                    } //throw exception here in case the current day forecast in null
                }
    }

}