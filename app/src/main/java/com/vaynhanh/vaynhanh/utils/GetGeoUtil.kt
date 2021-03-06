package com.vaynhanh.vaynhanh.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v4.app.ActivityCompat

class GetGeoUtil(private val mActivity: Activity) {
    private var locationManager: LocationManager? = null
    private var locationProvider: String? = null

    //获取所有可用的位置提供器
    //如果是GPS
    //如果是Network
    //获取Location
    // TODO: Consider calling
    val geo: Location?
        get() {

            try {
                locationManager = mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val providers = locationManager!!.getProviders(true)
                if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationProvider = LocationManager.GPS_PROVIDER
                } else if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationProvider = LocationManager.NETWORK_PROVIDER
                } else if (locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    locationProvider = LocationManager.PASSIVE_PROVIDER
                } else {
                    return null
                }
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    return null
                }
                return locationManager!!.getLastKnownLocation(locationProvider)
            } catch (e: Exception) {
                return null
            }

        }


}