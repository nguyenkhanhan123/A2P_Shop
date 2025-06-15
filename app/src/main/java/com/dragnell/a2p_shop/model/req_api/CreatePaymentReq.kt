package com.dragnell.a2p_shop.model.req_api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreatePaymentReq(
    @SerializedName("total") val total: String,
    @SerializedName("orderInfo") val orderInfo: String,
    @SerializedName("returnUrl") val returnUrl: String
) : Serializable
