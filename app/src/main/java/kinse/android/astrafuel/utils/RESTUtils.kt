package kinse.android.astrafuel.utils

import android.accounts.Account
import android.os.Build
import android.os.StrictMode
import kiinse.programs.kiinseapi.fuel.fueldata.*
import kinse.android.astrafuel.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Date

object RESTUtils {

    fun getStations(): Set<Station> {
        if (Build.VERSION.SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                val url = URL("https://api.kiinse.me/stations/all")
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                if (connection.responseCode != HttpURLConnection.HTTP_OK)
                    throw IOException("Unexpected code ${connection.responseCode}")
                val stations = HashSet<Station>()
                val array = JSONObject(AppUtils.inputStreamToString(connection.inputStream)).getJSONArray("data")
                for (i in 0 until array.length()) {
                    val json = JSONObject(array[i].toString())
                    stations.add(Station(json.getInt("id"),
                                         json.getBoolean("enabled"),
                                         json.getString("name"),
                                         getFuels(json.getJSONArray("fuels")),
                                         getLocation(json.getJSONObject("location")),
                                         getSuppliers(json.getJSONArray("suppliers"))))
                }
                return stations
            } catch (e: Exception) {
                AppUtils.sendErrorDialog(MainActivity.context!!, e.message)
            }
        }
        return HashSet()
    }

    fun getNews(): Set<NewsData> {
        if (Build.VERSION.SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                val url = URL("https://api.kiinse.me/news/all")
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                if (connection.responseCode != HttpURLConnection.HTTP_OK)
                    throw IOException("Unexpected code ${connection.responseCode}")
                val news = HashSet<NewsData>()
                val array = JSONObject(AppUtils.inputStreamToString(connection.inputStream)).getJSONArray("data")
                for (i in 0 until array.length()) {
                    val json = JSONObject(array[i].toString())
                    news.add(NewsData(json.getInt("id"),
                                     json.getString("title"),
                                      json.getString("text"),
                                      Date(json.getLong("date"))))
                }
                return news
            } catch (e: Exception) {
                AppUtils.sendErrorDialog(MainActivity.context!!, e.message)
            }
        }
        return HashSet()
    }

    fun login(credentials: JSONObject): Account? {
        if (Build.VERSION.SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL("https://api.kiinse.me/account/register")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("No-timeout", "kitsune")
            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            AppUtils.writeToStream(connection.outputStream, credentials)

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                when (responseCode) {
                    401 -> {
                        throw Exception(JSONObject(AppUtils.inputStreamToString(connection.errorStream)).getJSONObject("data").getString("message"))
                    }
                    else -> {
                        val json = JSONObject(AppUtils.inputStreamToString(connection.errorStream))
                        if (json.has("data")) {
                            throw Exception(json.getJSONObject("data").getString("message"))
                        }
                        throw Exception("Unexpected response code $responseCode")
                    }
                }
            }
            val json = JSONObject(AppUtils.inputStreamToString(connection.inputStream)).getJSONObject("data")
            return Account(credentials.getString("login"), json.getString("jwt"))
        }
        return null
    }

    fun removeAccount(credentials: JSONObject) {
        if (Build.VERSION.SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL("https://api.kiinse.me/account/remove")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("No-timeout", "kitsune")
            connection.requestMethod = "POST"
            connection.connectTimeout = 10000
            AppUtils.writeToStream(connection.outputStream, credentials)
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                val json: JSONObject?
                try {
                    json = JSONObject(AppUtils.inputStreamToString(connection.errorStream)).getJSONObject("data")
                } catch (e: Exception) {
                    throw Exception("Unexpected code ${connection.responseCode}")
                }
                if (json != null) {
                    throw Exception(json.getString("message"))
                }
                throw Exception("Unexpected code ${connection.responseCode}")
            }
        }
    }

    private fun getFuels(array: JSONArray): Set<Fuel> {
        val set = HashSet<Fuel>()
        for (i in 0 until array.length()) {
            val json = JSONObject(array[i].toString())
            set.add(
                Fuel(json.getInt("id"),
                     json.getString("name"),
                     BigDecimal.valueOf(json.getDouble("price")))
                   )
        }
        return set
    }

    private fun getSuppliers(array: JSONArray): Set<Supplier> {
        val set = HashSet<Supplier>()
        for (i in 0 until array.length()) {
            val json = JSONObject(array[i].toString())
            set.add(
                Supplier(json.getInt("id"),
                         json.getString("name"),
                         arrayToList(json.getJSONArray("fuels")),
                         arrayToList(json.getJSONArray("stations")))
                   )
        }
        return set
    }

    private fun getLocation(json: JSONObject): Location {
        val cityJson = json.getJSONObject("city")
        val coordinates = json.getJSONObject("coordinates")
        return Location(json.getInt("id"),
                        json.getInt("stationId"),
                        City(cityJson.getInt("id"),
                             cityJson.getString("name"),
                             arrayToList(cityJson.getJSONArray("stations"))),
                        json.getString("address"),
                        Coordinates(
                            BigDecimal.valueOf(coordinates.getDouble("latitude")),
                            BigDecimal.valueOf(coordinates.getDouble("longitude")))
                       )
    }

    private fun arrayToList(jsonArray: JSONArray): Array<Long> {
        val array = LongArray(jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            array[i] = jsonArray[i].toString().toLong()
        }
        return array.toTypedArray()
    }
}