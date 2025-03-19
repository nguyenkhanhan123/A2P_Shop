package com.dragnell.a2p_shop.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.HomeBinding
import com.dragnell.a2p_shop.databinding.MyCartBinding
import com.dragnell.a2p_shop.model.res_api.CartRes
import com.dragnell.a2p_shop.model.res_api.SearchRes
import com.dragnell.a2p_shop.view.act.Search
import com.dragnell.a2p_shop.view.adapter.ProductAdapter
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCartFragment : BaseFragment<MyCartBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        callAPI()
    }

    override fun initViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): MyCartBinding {
        return MyCartBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String = MyCartFragment::class.java.name
    }

    private fun callAPI() {
        Log.e("KQ", CommonUtils.getInstance().getPref("Token").toString())
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.getCart("Bearer ${CommonUtils.getInstance().getPref("Token")}","cartId=${CommonUtils.getInstance().getPref("Cookie")}")
            .enqueue(object : Callback<CartRes> {
                override fun onResponse(
                    call: Call<CartRes>, response: Response<CartRes>
                ) {
                    if (response.body()!!.code==200){
                        if (response.body()!!.cart.products.isEmpty()){
                            mbinding.emptyCartLayout.visibility=View.VISIBLE
                            mbinding.cartItemsLayout.visibility=View.GONE
                        }
                        else{
                            mbinding.emptyCartLayout.visibility=View.GONE
                            mbinding.cartItemsLayout.visibility=View.VISIBLE
                        }
                    }
                    Log.e("KQ", response.body().toString())
                }

                override fun onFailure(call: Call<CartRes>, t: Throwable) {
                    Log.e("KQ", "onFailure: " + t.message)
                }

            })
    }
}