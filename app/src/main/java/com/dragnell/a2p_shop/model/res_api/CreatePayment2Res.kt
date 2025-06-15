package com.dragnell.a2p_shop.model.res_api

import com.dragnell.a2p_shop.model.Account
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreatePayment2Res(
    @SerializedName("payUrl") val payUrl: String
): Serializable
