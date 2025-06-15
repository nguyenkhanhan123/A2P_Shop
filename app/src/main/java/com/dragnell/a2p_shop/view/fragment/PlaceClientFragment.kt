package com.dragnell.a2p_shop.view.fragment

import CustomToast
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.PlaceBinding
import com.dragnell.a2p_shop.model.Coordinates
import com.dragnell.a2p_shop.model.Order
import com.dragnell.a2p_shop.model.req_api.FixOrderReq
import com.dragnell.a2p_shop.model.res_api.FixOrderRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceClientFragment : BaseFragment<PlaceBinding, CommonViewModel>(), OnMapReadyCallback {

    private lateinit var order: Order
    private var mMap: GoogleMap? = null

    override fun getClassVM(): Class<CommonViewModel> = CommonViewModel::class.java

    override fun initView() {
        order = requireActivity().intent.getSerializableExtra("order") as Order

        Log.e("order",order.toString())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        mbinding.road.visibility= View.GONE
        mbinding.start.visibility=View.GONE
    }

    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): PlaceBinding {
        return PlaceBinding.inflate(inflater, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        try {
            val customerLatLng = LatLng(order.userInfo?.toadoa?.coordinates?.get(0) ?: 0.0,
                order.userInfo?.toadoa?.coordinates?.get(1) ?: 0.0)
            mMap?.addMarker(MarkerOptions().position(customerLatLng).title("Khách Hàng"))

            val itemLatLng = LatLng(order.toadoaDon?.coordinates?.get(0) ?: 0.0,
                order.toadoaDon?.coordinates?.get(1) ?: 0.0)
            mMap?.addMarker(MarkerOptions().position(customerLatLng).title("Đơn hàng"))

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 15f))

            drawRoute(itemLatLng,customerLatLng)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Không thể hiển thị vị trí khách hàng", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun drawRoute(start: LatLng, end: LatLng) {
        lifecycleScope.launch {
            try {
                val (path, distanceKm, timeMinutes) = withContext(Dispatchers.IO) {
                    val apiKey = "40eff71d-275a-496a-a83f-116383a7767d"
                    val url = "https://graphhopper.com/api/1/route?point=${start.latitude},${start.longitude}&point=${end.latitude},${end.longitude}&vehicle=car&locale=vi&points_encoded=false&key=$apiKey"
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val body = response.body()?.string() ?: return@withContext Triple(emptyList<LatLng>(), 0.0, 0L)

                    val json = JSONObject(body)
                    val pathObj = json.getJSONArray("paths").getJSONObject(0)
                    val distanceKm = pathObj.getDouble("distance") / 1000
                    val timeMinutes = pathObj.getLong("time") / 1000 / 60
                    val pointsArray = pathObj.getJSONObject("points").getJSONArray("coordinates")

                    val path = ArrayList<LatLng>().apply {
                        for (i in 0 until pointsArray.length()) {
                            val coord = pointsArray.getJSONArray(i)
                            add(LatLng(coord.getDouble(1), coord.getDouble(0)))
                        }
                    }

                    Triple(path, distanceKm, timeMinutes)
                }

                if (!isAdded) return@launch
                val timeHours = timeMinutes / 60
                val remainingMinutes = timeMinutes % 60
                val bearing = getBearing(path[0], path[1])

                mMap?.clear()
                mMap?.addPolyline(PolylineOptions().addAll(path).color(Color.BLUE).width(10f))
                mMap?.addMarker(MarkerOptions().position(path.first()).icon(BitmapDescriptorFactory.fromResource(R.drawable.space)).anchor(0.5f, 0.5f).flat(true).rotation(bearing))
                mMap?.addMarker(MarkerOptions().position(end).title("Khách hàng"))
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(path.first(), 14f))
                mbinding.s.text = "Quãng đường dự kiến: %.1f km".format(distanceKm)
                mbinding.t.text = "Thời gian dự kiến:: %d giờ %d phút".format(timeHours, remainingMinutes)
            } catch (e: Exception) {
                if (!isAdded) return@launch
                Toast.makeText(requireContext(), "Không thể vẽ tuyến đường", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBearing(from: LatLng, to: LatLng): Float {
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)
        val dLon = lon2 - lon1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        val brng = Math.toDegrees(Math.atan2(y, x))
        return ((brng + 360) % 360).toFloat()
    }

    companion object {
        val TAG: String = PlaceClientFragment::class.java.name
    }
}