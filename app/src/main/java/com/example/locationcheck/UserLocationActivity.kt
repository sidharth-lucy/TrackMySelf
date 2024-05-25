package com.example.locationcheck

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locationcheck.databinding.GetLoactionLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.util.Locale


class UserLocationActivity:AppCompatActivity() {

    lateinit var binding: GetLoactionLayoutBinding
    lateinit var mFusedLoaction:FusedLocationProviderClient
    val permissionId=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GetLoactionLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLoaction = LocationServices.getFusedLocationProviderClient(this)
        binding.startButton.setOnClickListener {
            getCurrentLocation()
        }
        binding.btnTrack.setOnClickListener {
            goOnTrackingPage()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
         mFusedLoaction.lastLocation.addOnCompleteListener {
             val location = it.result
             if(location!=null){
                 val geocoder = Geocoder(this, Locale.getDefault())
                 val listOfAdd = geocoder.getFromLocation(location.latitude,location.longitude ,1)

                 if(listOfAdd!=null && listOfAdd.size>0){
                     val add = listOfAdd[0]
                     val detailAdd = "addressLines: " + (if(add.maxAddressLineIndex>=0) add.getAddressLine(add.maxAddressLineIndex)+"\n" else " \n" )+
                             "latitude: "+ add.latitude +"\n" +
                             "longitude: "+ add.longitude +"\n" +
                             "adminArea: " +add.adminArea + "\n" +
                             "subAdminArea: " +add.subAdminArea + "\n" +
                             "countryName: "+ add.countryName +"\n" +
                             "countryCode: " +add.countryCode + "\n" +
                             "locality: " +add.locality + "\n"+
                             "subLocality: " +add.subLocality + "\n" +
                             "locale: " +add.locale + "\n" +
                             "postalCode: "+ add.postalCode +"\n" +
                             "phone: " +add.phone + "\n" +
                             "premises: " +add.premises + "\n"


                     binding.address.text = detailAdd
                 }

             }
         }
    }

    fun goOnTrackingPage(){
        val intent = Intent(this, TrackRouteActivity::class.java)
        startActivity(intent)
    }


}

