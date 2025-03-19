package com.dragnell.a2p_shop.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class Cart(
    @SerializedName("_id") val id: String,
    @SerializedName("products") val products: List<Product>,
    @SerializedName("createdAt") val createdAt: Date,
    @SerializedName("updatedAt") val updatedAt: Date,
    @SerializedName("__v") val version: Int,
    @SerializedName("user_id") val userId: String
): Serializable