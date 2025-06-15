package com.dragnell.a2p_shop.model.req_api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductAddReq2(
    @SerializedName("id") val id: String,
    @SerializedName("price") val price: Int,
    @SerializedName("discountPercentage") val discountPercentage: Int,
    @SerializedName("stock") val stock: Int
): Serializable