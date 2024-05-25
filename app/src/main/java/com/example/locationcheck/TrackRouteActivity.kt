package com.example.locationcheck

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationcheck.databinding.TrackRouteActivityLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class TrackRouteActivity:AppCompatActivity(),OnMapReadyCallback {

    lateinit var binding: TrackRouteActivityLayoutBinding
    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var polylineOptions: PolylineOptions
    private var polyline: Polyline? = null
    private var marker:Marker? =null
    private var tracking = false
    private var flag=false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TrackRouteActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        onClick()

        locationCallback = object :LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                if(tracking && result!=null){
                    updateRoute(result.lastLocation!!)
                }
            }
        }

    }


    private fun initializePolyline(){
        polylineOptions = PolylineOptions().width(5f).color(ContextCompat.getColor(this,R.color.purple_700))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap= googleMap
        initializePolyline()

    }

    private fun startTracking(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,5000).build()
//        val locationRequest = LocationRequest.create().setInterval(5000).setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationClient.requestLocationUpdates(locationRequest ,locationCallback,null)
    }

    private fun stopTracking(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun toggleTracking(){
        tracking=!tracking
        if(tracking){
            startTracking()
            binding.startButton.text="STOP TRACKING"
        }else{
            stopTracking()
            binding.startButton.text="START TRACKING"
        }
    }

    private fun updateRoute(location:android.location.Location){
        val latLon = LatLng(location.latitude, location.longitude)
        polylineOptions.add(latLon)

        if(flag){
            binding.startButton.text="STOP TRACKING > \n"+location.latitude +"\n"+location.longitude
        }else{
            binding.startButton.text="STOP TRACKING\n"+location.latitude +"\n"+location.longitude
        }
        flag=!flag


        if(polyline==null){
            mMap.addPolyline(polylineOptions)
        }else{
            polyline?.points = polylineOptions.points
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLon , 15f))
        if(marker==null){
            val markerOpt = MarkerOptions().position(latLon).title("You'r here")
            marker = mMap.addMarker(markerOpt)
        }
        marker?.position = latLon

//        location.distanceTo()

    }

    private fun onClick(){
        binding.startButton.setOnClickListener {
            toggleTracking()
        }

        binding.mapType.setOnClickListener {
            binding.mapType.visibility = View.GONE
            binding.llMapType.visibility= View.VISIBLE
        }

        binding.tvNormalType.setOnClickListener {
            binding.mapType.visibility = View.VISIBLE
            binding.mapType.text = "Normal Map"
            binding.llMapType.visibility= View.GONE
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        binding.tvSatelliteType.setOnClickListener {
            binding.mapType.visibility = View.VISIBLE
            binding.mapType.text = "Satellite Map"
            binding.llMapType.visibility= View.GONE
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        binding.tvNoneType.setOnClickListener {
            binding.mapType.visibility = View.VISIBLE
            binding.mapType.text = "None Type Map"
            binding.llMapType.visibility= View.GONE
            mMap.mapType = GoogleMap.MAP_TYPE_NONE
        }
        binding.tvHybridType.setOnClickListener {
            binding.mapType.visibility = View.VISIBLE
            binding.mapType.text = "Hybrid Map"
            binding.llMapType.visibility= View.GONE
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        binding.tvTerrainType.setOnClickListener {
            binding.mapType.visibility = View.VISIBLE
            binding.mapType.text = "Terrain Map"
            binding.llMapType.visibility= View.GONE
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }




}