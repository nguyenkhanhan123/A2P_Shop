package com.dragnell.a2p_shop.view.act

import android.content.Intent
import android.util.Log
import com.dragnell.a2p_shop.App
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.databinding.SignupBinding
import com.dragnell.a2p_shop.model.req_api.ClientSignUpReq
import com.dragnell.a2p_shop.model.res_api.ClientSignUpRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUp : BaseActivity<SignupBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        mbinding.Login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        mbinding.createAccount.setOnClickListener {
            val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
            apiInterface.clientSignUp(
                ClientSignUpReq(
                    mbinding.Email.text.toString().trim(),
                    mbinding.Password.text.toString().trim(),
                    mbinding.fullName.text.toString().trim()
                )
            ).enqueue(object : Callback<ClientSignUpRes> {
                    override fun onResponse(
                        call: Call<ClientSignUpRes>, response: Response<ClientSignUpRes>
                    ) {
                        val cookies = response.headers().values("Set-Cookie")

                        val cartIdLast = cookies.filter { it.startsWith("cartId=") }
                            .map { it.split(";")[0].substringAfter("cartId=") }
                            .lastOrNull()

                        CommonUtils.getInstance().savePref("Cookie", cartIdLast.toString())

                        if(response.body()!!.code == 200){
                            CommonUtils.getInstance().savePref("Token", response.body()!!.token)
                            startActivity(Intent(this@SignUp, MainActivity::class.java))
                            finish()
                        }
                        Log.i("KQ", response.body().toString())
                    }

                    override fun onFailure(call: Call<ClientSignUpRes>, t: Throwable) {
                        Log.e("KQ", "onFailure: " + t.message)
                    }

                })
        }
    }

    override fun initViewBinding(): SignupBinding {
        return SignupBinding.inflate(layoutInflater)
    }
}
