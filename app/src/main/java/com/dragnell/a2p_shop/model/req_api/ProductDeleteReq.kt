package com.dragnell.a2p_shop.model.req_api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductDeleteReq(
    @SerializedName("id") val id: String
) : Serializable
