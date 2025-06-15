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
import com.dragnell.a2p_shop.databinding.OrderedCartBinding
import com.dragnell.a2p_shop.databinding.PlaceBinding
import com.dragnell.a2p_shop.model.Coordinates
import com.dragnell.a2p_shop.model.Order
import com.dragnell.a2p_shop.model.req_api.FixOrderReq
import com.dragnell.a2p_shop.model.res_api.FixOrderRes
import com.dragnell.a2p_shop.view.adapter.OrderedCartAdapter
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

class ProductOrderAdminFragment : BaseFragment<OrderedCartBinding, CommonViewModel>() {

    private lateinit var order: Order

    override fun getClassVM(): Class<CommonViewModel> = CommonViewModel::class.java

    override fun initView() {
        order = requireActivity().intent.getSerializableExtra("order") as Order

        Log.e("order",order.toString())

        mbinding.rvProductCart.adapter=OrderedCartAdapter(order.products!!,requireContext())

        if (order.status=="cancelled"){
            mbinding.xuLyHoanHang.visibility=View.VISIBLE
            mbinding.xuLyHoanHang.setOnClickListener {
                callAPIFixOrder("completed2", order.toadoaDon!!.coordinates!!)

            }
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
                    mbinding.xuLyHoanHang.visibility=View.GONE
                    CustomToast.showToast(requireContext(),"Cập nhật thành công!",CustomToast.ToastType.SUCCESS)
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


    override fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): OrderedCartBinding {
        return OrderedCartBinding.inflate(inflater, container, false)
    }

    companion object {
        val TAG: String = ProductOrderAdminFragment::class.java.name
    }
}