package org.diyorbek.googlemap_l1

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import org.diyorbek.googlemap_l1.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var locationPermissionGranted = false
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var location: Location? = null
    var DEFAULT_ZOOM = 4f
    var defaultLocation = LatLng(40.747451466827094, 72.35951825384541)
    var fusedLocationClient: FusedLocationProviderClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermission()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        showMyLocation()
    }

    private fun showMyLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult: Task<Location>? = fusedLocationClient?.lastLocation
                locationResult?.addOnCompleteListener(
                    this
                ) {
                    Log.d("@@@","Task location")
                    if (it.isSuccessful) {
                        Log.d("@@@", "succes")
                        location = it.result
                        if (location != null) {
                            Log.d("@@@", "Location is Not Null")
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location!!.latitude,
                                        location!!.longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {
                            Log.d("@@@", "Location is Null")
                        }
                    } else {
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM)

                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (
            e: SecurityException
        ) {
            Log.e("exception: %s", e.message, e)
        }
    }

    private fun updateLocationUi() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                location = null
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                location = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUi()
    }
}