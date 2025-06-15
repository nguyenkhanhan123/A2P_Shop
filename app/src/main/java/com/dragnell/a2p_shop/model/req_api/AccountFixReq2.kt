package com.dragnell.a2p_shop.model.req_api

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class AccountFixReq2(
    @SerializedName("id") val id: String,
    @SerializedName("deleted") val deleted: Boolean
) : Serializable
