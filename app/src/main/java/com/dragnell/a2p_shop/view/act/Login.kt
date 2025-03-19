package com.dragnell.a2p_shop.view.act

import android.content.Intent
import android.util.Log
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.databinding.LoginBinding
import com.dragnell.a2p_shop.model.req_api.ClientLoginReq
import com.dragnell.a2p_shop.model.req_api.ClientSignUpReq
import com.dragnell.a2p_shop.model.res_api.ClientLoginRes
import com.dragnell.a2p_shop.model.res_api.ClientSignUpRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : BaseActivity<LoginBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        mbinding.SignUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
        mbinding.Login.setOnClickListener {
            val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
            apiInterface.clientLogin(ClientLoginReq(mbinding.Email.text.toString().trim(), mbinding.Password.text.toString().trim()))
                .enqueue(object : Callback<ClientLoginRes> {
                    override fun onResponse(
                        call: Call<ClientLoginRes>,
                        response: Response<ClientLoginRes>
                    ) {
                        val cookies = response.headers().values("Set-Cookie")

                        val cartIdLast = cookies.filter { it.startsWith("cartId=") }
                            .map { it.split(";")[0].substringAfter("cartId=") }
                            .lastOrNull()

                        CommonUtils.getInstance().savePref("Cookie", cartIdLast.toString())

                        if((response.body()!!.code == 200) && (response.body()!!.massage.trim() =="khi ban thay tin nhan nay, ban da dang nhap thanh cong")){
                            CommonUtils.getInstance().savePref("Token", response.body()!!.token)
                            startActivity(Intent(this@Login, MainActivity::class.java))
                            finish()
                        }
                        Log.e("KQ", response.body().toString())
                    }

                    override fun onFailure(call: Call<ClientLoginRes>, t: Throwable) {
                        Log.e("KQ", "onFailure: " + t.message)
                    }

                })
        }
    }

    override fun initViewBinding(): LoginBinding {
        return LoginBinding.inflate(layoutInflater)
    }
}
