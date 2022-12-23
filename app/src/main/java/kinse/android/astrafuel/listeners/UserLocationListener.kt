package kinse.android.astrafuel.listeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import kinse.android.astrafuel.MainActivity
import kinse.android.astrafuel.utils.AppUtils

object UserLocationListener : LocationListener {

    var userLocation: Point? = null

    override fun onLocationUpdated(location: Location) {
        userLocation = location.position
    }

    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
        if (locationStatus == LocationStatus.NOT_AVAILABLE) {
            AppUtils.sendErrorDialog(MainActivity.context!!, "Ваша геопозиция не найдена!")
        }
    }
}