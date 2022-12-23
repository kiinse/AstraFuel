package kinse.android.astrafuel.listeners

import androidx.fragment.app.commit
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import kinse.android.astrafuel.MainActivity
import kinse.android.astrafuel.R
import kinse.android.astrafuel.StationFragment
import kinse.android.astrafuel.utils.MapUtils


class MapTapListener : MapObjectTapListener {

    companion object {
        var lastCameraPosition: CameraPosition? = null
    }

    override fun onMapObjectTap(obj: MapObject, point: Point): Boolean {
        MainActivity.instance?.supportFragmentManager?.commit{
            lastCameraPosition = MainActivity.mapView?.map?.cameraPosition
            DrivingRouteListener.createRoute(point)
            MainActivity.mapView?.map?.move(
                CameraPosition(Point(point.latitude-0.005, point.longitude), 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1F),
                null)
            setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
            replace(R.id.station_fragment, StationFragment().setStation(MapUtils.getStation(obj.userData.toString().toInt())), "station")
            addToBackStack(null)
        }
        return false
    }
}