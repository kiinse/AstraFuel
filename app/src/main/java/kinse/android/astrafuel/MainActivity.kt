package kinse.android.astrafuel

import android.Manifest
import android.accounts.Account
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView
import kinse.android.astrafuel.utils.AppUtils
import kinse.android.astrafuel.utils.MapUtils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        context = this
        instance = this
        supportActionBar?.hide()

        AppUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 100)

        MapKitFactory.setApiKey("b2cb8a86-0a23-4db0-a81e-fe21d0c59914")
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        mapView = findViewById(R.id.map)

        val map = mapView!!.map
        MapUtils.createMap(map, (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES))
        map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
        map.mapType = MapType.VECTOR_MAP
        map.poiLimit = 0
        MapUtils.moveCamera(map)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.map -> {
                    if (bottomNavigationView.selectedItemId != R.id.map) {
                        removeAllFragments()
                    } else {
                        MapUtils.moveCamera(map)
                    }
                    true
                }
                R.id.news -> {
                    if (bottomNavigationView.selectedItemId != R.id.news) {
                        supportFragmentManager.commit{
                            setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
                            replace(R.id.window_fragment, NewsFragment(), "news")
                            addToBackStack(null)
                        }
                    }
                    true
                }
                R.id.account -> {
                    if (bottomNavigationView.selectedItemId != R.id.account) {
                        val account = account
                        if (account == null) {
                            supportFragmentManager.commit{
                                setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
                                replace(R.id.window_fragment, AccountLoginFragment(), "account_login")
                                addToBackStack(null)
                            }
                        } else {
                            supportFragmentManager.commit{
                                setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
                                replace(R.id.window_fragment, AccountFragment(), "account")
                                addToBackStack(null)
                            }
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun removeAllFragments() {
        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.open_animator, R.anim.close_animator)
                remove(fragment)
                addToBackStack(null)
            }
        }
    }

    override fun onStop() {
        MapUtils.unSubscribeToLocationUpdate()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapUtils.addAllMarks()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        MapUtils.addAllMarks()
    }

    override fun onRestart() {
        super.onRestart()
        MapUtils.addAllMarks()
    }

    companion object {
        var mapView: MapView? = null
        var context: Context? = null
        var instance: MainActivity? = null
        var account: Account? = null
    }
}