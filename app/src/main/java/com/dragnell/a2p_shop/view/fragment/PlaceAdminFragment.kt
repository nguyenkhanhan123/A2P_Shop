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

class PlaceAdminFragment : BaseFragment<PlaceBinding, CommonViewModel>(), OnMapReadyCallback {

    private lateinit var order: Order
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 111
    private var isUpdatingLocation = false

    override fun getClassVM(): Class<CommonViewModel> = CommonViewModel::class.java

    override fun initView() {
        order = requireActivity().intent.getSerializableExtra("order") as Order
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        mbinding.road.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                getCurrentLocationAndDrawRoute()
            }
        }

        mbinding.start.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                if (isUpdatingLocation) {
                    stopLocationUpdates()
                    Toast.makeText(requireContext(), "Đã dừng cập nhật vị trí", Toast.LENGTH_SHORT).show()
                } else {
                    startContinuousLocationUpdates()
                    Toast.makeText(requireContext(), "Đang cập nhật liên tục vị trí...", Toast.LENGTH_SHORT).show()
                }
                isUpdatingLocation = !isUpdatingLocation
            }
        }
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
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 15f))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Không thể hiển thị vị trí khách hàng", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationAndDrawRoute() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                val customerLatLng = LatLng(order.userInfo?.toadoa?.coordinates?.get(0) ?: 0.0,
                    order.userInfo?.toadoa?.coordinates?.get(1) ?: 0.0)
                drawRoute(currentLatLng, customerLatLng)
            } ?: Toast.makeText(requireContext(), "Không lấy được vị trí hiện tại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startContinuousLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 55000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val customerLatLng = LatLng(
                    order.userInfo?.toadoa?.coordinates?.get(0) ?: 0.0,
                    order.userInfo?.toadoa?.coordinates?.get(1) ?: 0.0
                )

                val results = FloatArray(1)
                Location.distanceBetween(
                    currentLatLng.latitude, currentLatLng.longitude,
                    customerLatLng.latitude, customerLatLng.longitude,
                    results
                )
                val distanceInMeters = results[0]
                Log.e("KQ", distanceInMeters.toString())

                if (distanceInMeters <= 10.0) {
                    stopLocationUpdates()
                    isUpdatingLocation = false
                    Toast.makeText(requireContext(), "Giao hàng thành công!", Toast.LENGTH_SHORT).show()
                    callAPIFixOrder("completed" ,listOf(location.latitude, location.longitude))
                    return
                }

                callAPIFixOrder("shipped",listOf(location.latitude, location.longitude))
                drawRoute(currentLatLng, customerLatLng)
            }
        }


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            Toast.makeText(requireContext(), "Cần cấp quyền vị trí", Toast.LENGTH_SHORT).show()
        }
    }

    private fun callAPIFixOrder(s: String,currentLatLng: List<Double>) {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.fixOrder(
            order._id.toString(), FixOrderReq(
                s, Coordinates(
                    "Point", currentLatLng
                )
            ),
            "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<FixOrderRes> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<FixOrderRes>, response: Response<FixOrderRes>
            ) {
                if (response.body() != null && response.body()!!.message == "Cập nhật thành công") {
                    Log.e("KQ", response.body().toString())
                }
                Log.e("KQ", response.body().toString())
            }

            override fun onFailure(call: Call<FixOrderRes>, t: Throwable) {
                CustomToast.showToast(
                    requireContext(),
                    "Lỗi kết nối!",
                    CustomToast.ToastType.WARNING
                )
                Log.e("KQ", "onFailure: " + t.message)
            }

        })
    }



    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndDrawRoute()
        } else {
            Toast.makeText(requireContext(), "Bạn cần cấp quyền để hiển thị lộ trình", Toast.LENGTH_SHORT).show()
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
                mMap?.addMarker(MarkerOptions().position(end).title("Kh\u00e1ch h\u00e0ng"))
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
        val TAG: String = PlaceAdminFragment::class.java.name
    }
}