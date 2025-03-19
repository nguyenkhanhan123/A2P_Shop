package com.dragnell.a2p_shop.view.api

import com.dragnell.a2p_shop.model.req_api.ClientLoginReq
import com.dragnell.a2p_shop.model.req_api.ClientSignUpReq
import com.dragnell.a2p_shop.model.res_api.CartRes
import com.dragnell.a2p_shop.model.res_api.ClientLogOutRes
import com.dragnell.a2p_shop.model.res_api.ClientLoginRes
import com.dragnell.a2p_shop.model.res_api.ClientSignUpRes
import com.dragnell.a2p_shop.model.res_api.SearchRes
import com.dragnell.a2p_shop.model.res_api.UserInfoRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("auth/register")
    @Headers("Content-Type: application/json")
    fun clientSignUp(
        @Body res: ClientSignUpReq
    ): Call<ClientSignUpRes>

    @POST("auth/login")
    @Headers("Content-Type: application/json")
    fun clientLogin(
        @Body res: ClientLoginReq
    ): Call<ClientLoginRes>

    @GET("search")
    @Headers("Content-Type: application/json")
    fun searchProducts(@Query("keyword") keyword: String): Call<SearchRes>

    @GET("cart")
    @Headers("Content-Type: application/json")
    fun getCart(@Header("Authorization") token: String, @Header("Cookie") cookie: String): Call<CartRes>

    @GET("user/info")
    @Headers("Content-Type: application/json")
    fun getUserInfo(@Header("Authorization") token: String): Call<UserInfoRes>

    @POST("auth/logout")
    @Headers("Content-Type: application/json")
    fun clientLogOut(@Header("Authorization") token: String): Call<ClientLogOutRes>
}