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
import com.dragnell.a2p_shop.databinding.ProfileBinding
import com.dragnell.a2p_shop.model.res_api.CartRes
import com.dragnell.a2p_shop.model.res_api.ClientLogOutRes
import com.dragnell.a2p_shop.model.res_api.UserInfoRes
import com.dragnell.a2p_shop.view.act.Login
import com.dragnell.a2p_shop.view.act.Search
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : BaseFragment<ProfileBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        callAPI()

        mbinding.icLogOut.setOnClickListener {
            val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
            apiInterface.clientLogOut("Bearer ${CommonUtils.getInstance().getPref("Token")}")
                .enqueue(object : Callback<ClientLogOutRes> {
                    override fun onResponse(
                        call: Call<ClientLogOutRes>, response: Response<ClientLogOutRes>
                    ) {
                        if (response.body()!!.message=="Logout successful"){
                            CommonUtils.getInstance().clearPref("Token")
                            CommonUtils.getInstance().clearPref("Cookie")
                            val intent = Intent(context, Login::class.java)
                            requireContext().startActivity(intent)
                        }
                        Log.e("KQ", response.body().toString())
                    }

                    override fun onFailure(call: Call<ClientLogOutRes>, t: Throwable) {
                        Log.e("KQ", "onFailure: " + t.message)
                    }

                })
        }
    }

    override fun initViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): ProfileBinding {
        return ProfileBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String = ProfileFragment::class.java.name
    }

    private fun callAPI() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.getUserInfo("Bearer ${CommonUtils.getInstance().getPref("Token")}")
            .enqueue(object : Callback<UserInfoRes> {
                override fun onResponse(
                    call: Call<UserInfoRes>, response: Response<UserInfoRes>
                ) {
                    val cookies = response.headers().values("Set-Cookie")

                    val cartIdLast = cookies.filter { it.startsWith("cartId=") }
                        .map { it.substringAfter("cartId=").substringBefore(";") }
                        .lastOrNull()

                    val connectSidLast = cookies.filter { it.startsWith("connect.sid=") }
                        .map { it.substringAfter("connect.sid=").substringBefore(";") }
                        .lastOrNull()

                    CommonUtils.getInstance().savePref("cartId=",cartIdLast.toString())

                    CommonUtils.getInstance().savePref("connect.sid=",connectSidLast.toString())
                    if (response.body()!!.code==200){
                        if (response.body()!!.user!=null){
                            mbinding.fullName.text= response.body()!!.user!!.fullName
                            mbinding.Email.text= response.body()!!.user!!.email
                        }
                        else{
                            mbinding.fullName.text= ""
                            mbinding.Email.text= ""
                        }
                    }
                    Log.e("KQ", response.body().toString())
                }

                override fun onFailure(call: Call<UserInfoRes>, t: Throwable) {
                    Log.e("KQ", "onFailure: " + t.message)
                }

            })
    }
}