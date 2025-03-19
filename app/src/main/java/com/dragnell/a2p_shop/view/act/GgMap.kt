package com.dragnell.a2p_shop.view.act

import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.GgmapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class GgMap : BaseActivity<GgmapBinding, CommonViewModel>(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    override fun getClassVM(): Class<CommonViewModel> {
       return CommonViewModel::class.java
    }

    override fun initView() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@GgMap)
    }

    override fun initViewBinding(): GgmapBinding {
       return GgmapBinding.inflate(layoutInflater)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        val hanoi = LatLng(21.0285, 105.8542)
        mMap!!.addMarker(MarkerOptions().position(hanoi).title("Hà Nội"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 15f))
    }
}