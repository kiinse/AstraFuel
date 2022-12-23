package kinse.android.astrafuel.listeners

import android.graphics.Color
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kinse.android.astrafuel.MainActivity
import kinse.android.astrafuel.utils.CanvasUtils
import kinse.android.astrafuel.utils.MapUtils


object UserObjectListener : UserLocationObjectListener {

    override fun onObjectAdded(userLocationView: UserLocationView) {
        val icon = ImageProvider.fromBitmap(CanvasUtils.drawMark(17, Color.DKGRAY))
        userLocationView.arrow.setIcon(icon)
        userLocationView.pin.setIcon(icon)
        MapUtils.moveCamera(MainActivity.mapView!!.map)
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {}
}