package io.mapwize.mapwizeuicomponents

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.indoorlocation.core.IndoorLocation
import io.indoorlocation.core.IndoorLocationProvider
import io.indoorlocation.manual.ManualIndoorLocationProvider
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings
import io.mapwize.mapwizecomponents.ui.UIBehaviour
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizePlugin
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener, UIBehaviour {


    private var mapwizeFragment: MapwizeFragment? = null
    private var mapboxMap: MapboxMap? = null
    private var mapwizePlugin: MapwizePlugin? = null
    private var locationProvider: ManualIndoorLocationProvider? = null
    private var manualIndoorLocationProvider: IndoorLocationProvider? = null
    private var navisensIndoorLocationProvider: NavisensIndoorLocationProvider? = null
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Uncomment and fill place holder to test MapwizeUI on your venue
        val opts = MapOptions.Builder()
                .restrictContentToOrganization("5c402a869c78c8001556db52")
                .restrictContentToVenue("5c4111f09c78c8001556e7d8")
                .centerOnVenue("5c4111f09c78c8001556e7d8")
//                .centerOnPlace("YOUR_PLACE_ID")
                .build()

        // Uncomment and change value to test different settings configuration
        var uiSettings = MapwizeFragmentUISettings.Builder()
                .menuButtonHidden(true)
                .followUserButtonHidden(false)
                .floorControllerHidden(false)
                .compassHidden(true)
                .build()
        mapwizeFragment = MapwizeFragment.newInstance(opts, uiSettings)
        this.mapwizeFragment?.uiBehaviour = this
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(fragmentContainer.id, mapwizeFragment!!)
        ft.commit()

    }

    /**
     * Fragment listener
     */
    override fun onFragmentReady(mapboxMap: MapboxMap?, mapwizePlugin: MapwizePlugin?) {
        this.mapboxMap = mapboxMap
        this.mapwizePlugin = mapwizePlugin

//        this.locationProvider = ManualIndoorLocationProvider()
//        this.mapwizePlugin?.setLocationProvider(this.locationProvider!!)
//
//        this.mapwizePlugin?.addOnLongClickListener {
//            val indoorLocation = IndoorLocation("manual_provider", it.latLngFloor.latitude, it.latLngFloor.longitude, it.latLngFloor.floor, System.currentTimeMillis())
//            this.locationProvider?.setIndoorLocation(indoorLocation)
//        }
        requestLocationPermission()
        mapwizePlugin?.addOnClickListener { latLngFloor ->
            val indoorLocation = IndoorLocation(manualIndoorLocationProvider?.getName(),
                    latLngFloor.latLngFloor.latitude, latLngFloor.latLngFloor.longitude,
                    latLngFloor.latLngFloor.floor, System.currentTimeMillis())
            manualIndoorLocationProvider?.dispatchIndoorLocationChange(indoorLocation)
        }
    }

    override fun onMenuButtonClick() {
        // TODO Do something with menu click
    }

    override fun onInformationButtonClick(place: Place?) {
        // TODO Do something with information button
    }

    /**
     * UIBehaviour
     * MapwizeFragment have a default UIBehaviour. You don't have to implement it if you do not need a custom behaviour.
     * This implementation is here for demo purpose.
     */
    override fun shouldDisplayInformationButton(mapwizeObject: MapwizeObject?): Boolean {
        when (mapwizeObject) {
            is Place -> return true
        }
        return false
    }

    override fun shouldDisplayFloorController(floors: MutableList<Double>?): Boolean {
        if (floors == null || floors.size <= 1) {
            return false
        }
        return true
    }

    private fun setupLocationProvider() {
        manualIndoorLocationProvider = ManualIndoorLocationProvider()
        navisensIndoorLocationProvider = NavisensIndoorLocationProvider(applicationContext, "sowYjJ2z0qZTVHYDdFKhP3RpGIE7tOjOY3hLcMeH31Zm48nxJNnnpCLqgmMPfVeg", manualIndoorLocationProvider)
        mapwizePlugin?.setLocationProvider(navisensIndoorLocationProvider!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationProvider()
                }
            }
        }
    }

    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            setupLocationProvider()
        }
    }

}
