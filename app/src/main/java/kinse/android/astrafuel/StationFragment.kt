package kinse.android.astrafuel

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import kiinse.programs.kiinseapi.fuel.fueldata.Station
import kinse.android.astrafuel.listeners.DrivingRouteListener
import kinse.android.astrafuel.listeners.MapTapListener
import kinse.android.astrafuel.utils.AppUtils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var data: Station? = null

    fun setStation(data: Station?): StationFragment {
        this.data = data
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exitButton = view.findViewById<ImageButton>(R.id.back_button)
        exitButton.setOnClickListener {
            exitButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.context, R.anim.alpha_animator))
            MainActivity.instance?.removeAllFragments()
        } 
        val station = data
        if (station != null) {
            val context = view.context

            view.findViewById<RelativeLayout>(R.id.station_enabled).background = when (station.enabled) {
                true -> ContextCompat.getDrawable(context, R.drawable.station_enabled)
                else -> ContextCompat.getDrawable(context, R.drawable.station_disabled)
            }

            view.findViewById<TextView>(R.id.station_name).text = station.name
            val location = station.location
            view.findViewById<TextView>(R.id.station_address).text = "${location.city.name}, ${location.address}"

            var layout = view.findViewById<LinearLayout>(R.id.fuel_list)
            var drawable = ContextCompat.getDrawable(context, R.drawable.rounded_all_corners_fuel)
            for (fuel in station.fuels) {
                AppUtils.createFuelLayout(context, fuel, layout, drawable, ContextCompat.getColor(context, R.color.gray))
            }

            layout = view.findViewById(R.id.suppliers_list)
            drawable = ContextCompat.getDrawable(context, R.drawable.rounded_all_corners_suppliers)
            for (supplier in station.suppliers) {
                AppUtils.createButton(context, " ${supplier.name} ", layout, drawable, AppUtils.getThemeTextColor(context))
            }
        }
    }
    override fun onStop() {
        super.onStop()
        DrivingRouteListener.clearRoutes()
        val cameraPosition = MapTapListener.lastCameraPosition
        if (cameraPosition != null) {
            MainActivity.mapView?.map?.move(
                cameraPosition,
                Animation(Animation.Type.SMOOTH, 1F),
                null)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}