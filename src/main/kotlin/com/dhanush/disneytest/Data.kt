//data  received from endpoint
data class WeatherApiResponse(
        val properties: WeatherApiProperties
)
//create data class for the data to be retrieved
data class WeatherApiProperties(
        val periods: List<WeatherApiPeriod>
)

data class WeatherApiPeriod(
        val name: String,
        val temperature: Double,
        val temperatureUnit: String,
        val shortForecast: String
)

data class DailyForecast(
        val day_name: String,
        val temp_high_celsius: Double,
        val forecast_blurp: String
)
//data needed for rest API
data class ForecastResponse(
        val daily: List<DailyForecast>
)
