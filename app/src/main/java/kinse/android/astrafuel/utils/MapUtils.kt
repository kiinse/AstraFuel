package kinse.android.astrafuel.utils

import android.graphics.Color
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import kiinse.programs.kiinseapi.fuel.fueldata.Station
import kinse.android.astrafuel.MainActivity
import kinse.android.astrafuel.listeners.MapTapListener
import kinse.android.astrafuel.listeners.UserLocationListener
import kinse.android.astrafuel.listeners.UserObjectListener


object MapUtils {

    private val listener: MapTapListener = MapTapListener()
    private var mapObjectCollection: MapObjectCollection? = null
    private val markerDataList = HashSet<PlacemarkMapObject>()
    private var userLocation: UserLocationLayer? = null
    private var locationManager: LocationManager? = null
    private var stations = HashSet<Station>()

    private const val DESIRED_ACCURACY = 0.0
    private const val MINIMAL_TIME: Long = 1000
    private const val MINIMAL_DISTANCE = 1.0
    private const val USE_IN_BACKGROUND = false

    private val style = "[" +
            "        {" +
            "            \"types\": [\"point\"]," +
            "            \"stylers\": {" +
            "                \"visibility\": \"off\"" +
            "            }" +
            "        }," +
            "       {" +
            "            \"types\": [\"polyline\", \"polygon\"]," +
            "            \"stylers\": {" +
            "                \"saturation\": -0.6" +
            "            }" +
            "        }" +
            "    ]"

    fun createMap(map: Map, isDarkMode: Boolean) {
        map.isNightModeEnabled = isDarkMode
        map.setMapStyle(style)
        mapObjectCollection = map.mapObjects.addCollection()

        userLocation = MapKitFactory.getInstance().createUserLocationLayer(MainActivity.mapView!!.mapWindow)
        locationManager = MapKitFactory.getInstance().createLocationManager()
        userLocation!!.isVisible = true
        userLocation!!.setObjectListener(UserObjectListener)
        subscribeToLocationUpdate()
    }

    fun moveCamera(map: Map) {
        val userLocation = UserLocationListener.userLocation
        if (userLocation != null) {
            map.move(CameraPosition(userLocation, 13.0f, 0.0f, 0.0f),
                     Animation(Animation.Type.SMOOTH, 1F),
                     null)
        } else {
            map.move(CameraPosition(Point(59.9342802, 30.3350986), 11.0f, 0.0f, 0.0f),
                     Animation(Animation.Type.SMOOTH, 1F),
                     null)
        }
    }

    private fun subscribeToLocationUpdate() {
        locationManager?.subscribeForLocationUpdates(DESIRED_ACCURACY, MINIMAL_TIME, MINIMAL_DISTANCE, USE_IN_BACKGROUND, FilteringMode.OFF, UserLocationListener)
    }


    fun unSubscribeToLocationUpdate() {
        locationManager?.unsubscribe(UserLocationListener)
    }

    fun addAllMarks() {
        stations.clear()
        for (marker in markerDataList) {
            mapObjectCollection!!.remove(marker)
        }
        markerDataList.clear()
        stations.addAll(RESTUtils.getStations())

        val enabledPoint = ImageProvider.fromBitmap(CanvasUtils.drawMark(15, Color.GREEN))
        val disabledPoint = ImageProvider.fromBitmap(CanvasUtils.drawMark(15, Color.RED))

        for (station in stations) {
            val coordinates = station.location.coordinates
            markerDataList.add(addMarker(Point(coordinates.latitude.toDouble(), coordinates.longitude.toDouble()),
                                         when(station.enabled) {
                                             true -> enabledPoint
                                             else -> disabledPoint },
                                         station.id))
        }
    }

    fun getStation(id: Int): Station? {
        for (station in stations) {
            if (station.id == id) return station
        }
        return null
    }

    private fun addMarker(
        point: Point,
        imageProvider: ImageProvider,
        userData: Any? = null
    ): PlacemarkMapObject {
        val marker = mapObjectCollection!!.addPlacemark(point, imageProvider)
        marker.userData = userData
        listener.let { marker.addTapListener(it) }
        return marker
    }

}