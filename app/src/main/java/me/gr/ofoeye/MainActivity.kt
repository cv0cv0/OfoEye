package me.gr.ofoeye

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private val locationClient = AMapLocationClient(application)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map_view.onCreate(savedInstanceState)
        MainActivityPermissionsDispatcher.initMapWithCheck(this)
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    fun initMap() {
        val aMap = map_view.map
        val uiSettings = aMap.uiSettings
        val locationStyle = MyLocationStyle()
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        locationStyle.strokeColor(Color.TRANSPARENT)
        locationStyle.radiusFillColor(Color.TRANSPARENT)
        aMap.myLocationStyle = locationStyle
        aMap.isMyLocationEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER

        val locationOption = AMapLocationClientOption()
        locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
        locationOption.isNeedAddress = false
        locationOption.isOnceLocationLatest = true
        locationOption.isWifiScan = false
        locationClient.setLocationOption(locationOption)
        locationClient.setLocationListener { location ->
            if (location != null && location.errorCode == 0) {
                val latLng=LatLng(location.latitude,location.longitude)
                aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }

        location_btn.setOnClickListener { locationClient.startLocation() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()
        eye_view.registerSensorListener()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        eye_view.unregisterSensorListener()
        map_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}
