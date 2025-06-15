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
import com.dragnell.a2p_shop.model.req_api.CreatePayment2Req
import com.dragnell.a2p_shop.model.req_api.CreatePaymentReq
import com.dragnell.a2p_shop.model.req_api.FixOrderClientReq
import com.dragnell.a2p_shop.model.req_api.FixOrderReq
import com.dragnell.a2p_shop.model.res_api.CreatePayment2Res
import com.dragnell.a2p_shop.model.res_api.CreatePaymentRes
import com.dragnell.a2p_shop.model.res_api.FixOrderRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MOMOActivity : BaseActivity<VnpayBinding, CommonViewModel>() {

    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        createPayment()
        mbinding.webview.settings.javaScriptEnabled = true
    }

    private fun createPayment() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.createPayment2(
            CreatePayment2Req(
                ((intent.getIntExtra("totalCost", 0) * 1111).toString()),
                "A2PShop",
                "https://www.facebook.com"
            ), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<CreatePayment2Res> {
            override fun onResponse(
                call: Call<CreatePayment2Res>, response: Response<CreatePayment2Res>
            ) {
                if (response.body() != null) {
                    mbinding.webview.loadUrl(response.body()!!.payUrl)
                }
                Log.e("KQ", response.body().toString())

            }

            override fun onFailure(call: Call<CreatePayment2Res>, t: Throwable) {
                Log.e("KQ", "onFailure: " + t.message)
            }

        })
    }

    override fun initViewBinding(): VnpayBinding {
        return VnpayBinding.inflate(layoutInflater)
    }
}
