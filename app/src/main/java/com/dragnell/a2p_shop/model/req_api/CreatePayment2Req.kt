package com.dragnell.a2p_shop.model.req_api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreatePayment2Req(
    @SerializedName("amount") val total: String,
    @SerializedName("orderInfo") val orderInfo: String,
    @SerializedName("redirectUrl") val returnUrl: String
) : Serializable
