package com.example.checkbox

import com.google.android.gms.maps.model.Marker

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-26
 * @desc
 */
class Main_Map {

    companion object {
        val latLngList = ArrayList<LatLngData>()
        var selectedMarker: Marker? = null
        val removelist = arrayListOf<LatLngData>()
        var exitck: Boolean = false
        var inputck: Boolean = false
    }
}