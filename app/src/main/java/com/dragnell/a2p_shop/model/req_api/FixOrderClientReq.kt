package com.dragnell.a2p_shop.model.req_api

import com.dragnell.a2p_shop.model.Coordinates
import com.dragnell.a2p_shop.model.ProductOrder
import com.dragnell.a2p_shop.model.UserInfo
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FixOrderClientReq(
    @SerializedName("status") val status: String
) : Serializable
