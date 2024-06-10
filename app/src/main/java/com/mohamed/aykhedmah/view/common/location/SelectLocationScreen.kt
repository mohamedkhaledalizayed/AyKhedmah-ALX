package com.mohamed.aykhedmah.view.common.location

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.mohamed.aykhedmah.R

class SelectLocationScreen : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lat = 30.6046391
    private var lan = 30.876508
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location_screen)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
        val sydney = LatLng(lat, lan)
        mMap.setOnCameraIdleListener(GoogleMap.OnCameraIdleListener {
            val midLatLng = mMap.cameraPosition.target
            Log.e(
                "location",
                midLatLng.latitude.toString() + " " + midLatLng.longitude
            )
            lat = midLatLng.latitude
            lan = midLatLng.longitude
        })
        // For zooming automatically to the location of the marker
        val cameraPosition = CameraPosition.Builder().target(sydney).zoom(12f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun selectLocation(view: View?) {
        val intent = Intent()
        intent.putExtra("lat", lat)
        intent.putExtra("lan", lan)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}