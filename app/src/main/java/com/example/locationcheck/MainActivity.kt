package com.example.locationcheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnFailureListener


class MainActivity : AppCompatActivity(){
    companion object{
        private const val REQUEST_ENABLE_GPS = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("dfw", Build.VERSION.PREVIEW_SDK_INT.toString())
        if(isLocationPermissionGiven()){
            if(gpsIsOn()){
                val msg=Toast.makeText(this,"Gps ON" , Toast.LENGTH_LONG)
                msg.show()
                goToLocationPage()
            }else{
                askToOnGps()
                val msg=Toast.makeText(this,"please enable gps" , Toast.LENGTH_LONG)
                msg.show()
            }
        }
    }


    private fun isLocationPermissionGiven():Boolean{

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),REQUEST_ENABLE_GPS)
            return false
        }else{
            return true
        }
    }

    fun gpsIsOn():Boolean{
        val locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return gps
    }

    fun askToOnGps(){
        val locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).build()

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        locationSettingsRequestBuilder.addLocationRequest(locationRequest).setAlwaysShow(true)
        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        task.addOnSuccessListener {

        }
        task.addOnFailureListener(this, OnFailureListener {e->
            if (e is ResolvableApiException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                try {
                    e.startResolutionForResult(this, REQUEST_ENABLE_GPS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            } else {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_GPS) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(this, "Gps Enabled Successfully!", Toast.LENGTH_LONG).show()
                goToLocationPage()
            } else {
                // User did not enable GPS, handle it here
                Toast.makeText(this, "Gps Enabled Failed!", Toast.LENGTH_LONG).show()
                //askToOnGps()
            }
        }
    }

    private fun goToLocationPage(){
        val intent = Intent(this, UserLocationActivity::class.java)
        startActivity(intent)
        finish()
    }


}

