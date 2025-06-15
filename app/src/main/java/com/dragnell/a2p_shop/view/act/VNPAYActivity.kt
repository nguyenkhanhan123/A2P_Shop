package com.dragnell.a2p_shop.view.act

import CustomToast
import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.net.toUri
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.databinding.VnpayBinding
import com.dragnell.a2p_shop.model.Coordinates
import com.dragnell.a2p_shop.model.req_api.CreatePaymentReq
import com.dragnell.a2p_shop.model.req_api.FixOrderClientReq
import com.dragnell.a2p_shop.model.req_api.FixOrderReq
import com.dragnell.a2p_shop.model.res_api.CreatePaymentRes
import com.dragnell.a2p_shop.model.res_api.FixOrderRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VNPAYActivity : BaseActivity<VnpayBinding, CommonViewModel>() {

    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        createPayment()
        mbinding.webview.settings.javaScriptEnabled = true
        mbinding.webview.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if (it.contains("vnp_ResponseCode")) {
                        val uri = it.toUri()
                        val responseCode = uri.getQueryParameter("vnp_ResponseCode")
                        if (responseCode == "00") {
                            callAPIFixOrder(intent.getStringExtra("id").toString())
                            Toast.makeText(
                                this@VNPAYActivity,
                                "Thanh toán thành công!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@VNPAYActivity,
                                "Thanh toán thất bại hoặc bị huỷ!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        finish()
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun callAPIFixOrder(s: String) {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.fixOrderClient(
            s, FixOrderClientReq(
                "paid"
            ), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
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
                    this@VNPAYActivity, "Lỗi kết nối!", CustomToast.ToastType.WARNING
                )
                Log.e("KQ", "onFailure: " + t.message)
            }

        })
    }

    private fun createPayment() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.createPayment(
            CreatePaymentReq(
                ((intent.getIntExtra("totalCost", 0) * 1111).toString()),
                "A2PShop",
                "https://www.facebook.com"
            ), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<CreatePaymentRes> {
            override fun onResponse(
                call: Call<CreatePaymentRes>, response: Response<CreatePaymentRes>
            ) {
                if (response.body() != null) {
                    mbinding.webview.loadUrl(response.body()!!.paymentUrl)
                }
                Log.e("KQ", response.body().toString())

            }

            override fun onFailure(call: Call<CreatePaymentRes>, t: Throwable) {
                Log.e("KQ", "onFailure: " + t.message)
            }

        })
    }

    override fun initViewBinding(): VnpayBinding {
        return VnpayBinding.inflate(layoutInflater)
    }
}
