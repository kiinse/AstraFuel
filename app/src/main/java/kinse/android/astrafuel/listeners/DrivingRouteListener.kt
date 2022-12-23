package kinse.android.astrafuel.listeners

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.Error
import kinse.android.astrafuel.MainActivity
import kinse.android.astrafuel.utils.AppUtils


object DrivingRouteListener : DrivingRouteListener {

    private var router: DrivingRouter? = null
    private var mapObjectCollection: MapObjectCollection? = null

    fun createRoute(point: Point) {
        clearRoutes()

        val userLocation = UserLocationListener.userLocation
        if (userLocation != null) {
            mapObjectCollection = MainActivity.mapView?.map?.mapObjects?.addCollection()
            router = DirectionsFactory.getInstance().createDrivingRouter()
            val requestPoints: ArrayList<RequestPoint> = ArrayList()
            requestPoints.add(
                RequestPoint(
                    userLocation,
                    RequestPointType.WAYPOINT,
                    null))
            requestPoints.add(
                RequestPoint(
                    point,
                    RequestPointType.WAYPOINT,
                    null))
            val vehicleOptions = VehicleOptions()
            vehicleOptions.vehicleType = VehicleType.DEFAULT
            router?.requestRoutes(requestPoints, DrivingOptions(), vehicleOptions, this)
        }
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        clearRoutes()
        if (routes.size > 0) {
            mapObjectCollection?.addPolyline(routes[0].geometry)
        }
    }

    fun clearRoutes() {
        mapObjectCollection?.clear()
    }

    override fun onDrivingRoutesError(error: Error) {
        println(error.toString())
        AppUtils.sendErrorDialog(MainActivity.context!!, error.toString())
    }
}