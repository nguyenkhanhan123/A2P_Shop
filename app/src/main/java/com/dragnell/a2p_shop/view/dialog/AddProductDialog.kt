package com.dragnell.a2p_shop.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.DialogProductBinding
import com.dragnell.a2p_shop.model.Product
import com.dragnell.a2p_shop.model.Role
import com.dragnell.a2p_shop.model.req_api.CategoryAddReq
import com.dragnell.a2p_shop.model.req_api.CategoryFixReq
import com.dragnell.a2p_shop.model.req_api.ProductAddReq
import com.dragnell.a2p_shop.model.req_api.ProductAddReq2
import com.dragnell.a2p_shop.model.res_api.CategoryAddRes
import com.dragnell.a2p_shop.model.res_api.CategoryFixRes
import com.dragnell.a2p_shop.model.res_api.ProductAddRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductDialog(context: Context, val parentId: String?, val role: Product?, private val onSucess: () -> Unit, private val onClick: (String) -> Unit) :
    BottomSheetDialog(context, R.style.CustomDialogStyle) {
    private val mbinding: DialogProductBinding = DialogProductBinding.inflate(layoutInflater)

    init {
        setContentView(mbinding.root)
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mbinding.icWed.setOnClickListener {
            onClick(mbinding.title.text.toString().trim())
        }
        if (role == null) {
            mbinding.btnApply.setOnClickListener {
                callAddProduct()
            }
        }
        else{
            mbinding.nameDialog.text="Chỉnh sửa Product"
            mbinding.title.setText(role.title)
            mbinding.price.setText(role.price.toString())
            mbinding.discountPercentage.setText(role.discountPercentage.toString())
            mbinding.stock.setText(role.stock.toString())
            mbinding.thumbnail.setText(role.thumbnail)
            mbinding.btnApply.setOnClickListener {
                callFixProduct()
            }
        }
    }

    private fun callAddProduct() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.addProducts(
            ProductAddReq(
                mbinding.title.text.toString().trim(),
                mbinding.describe.text.toString().trim(),
                mbinding.price.text.toString().trim().toInt(),
                mbinding.discountPercentage.text.toString().trim().toInt(),
                mbinding.stock.text.toString().trim().toInt(),
                mbinding.thumbnail.text.toString().trim(),
                "active",
                parentId.toString().trim()
            ), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<ProductAddRes> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ProductAddRes>, response: Response<ProductAddRes>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.message == "them thanh cong") {
                        CustomToast.showToast(
                            context, "Tạo sản phẩm thành công", CustomToast.ToastType.SUCCESS
                        )
                        dismiss()
                        onSucess()
                    } else {
                        CustomToast.showToast(
                            context, "Chưa thể cập nhật bây giờ!", CustomToast.ToastType.INFO
                        )
                    }
                }
                Log.i("AddProduct", response.body().toString())
            }

            override fun onFailure(call: Call<ProductAddRes>, t: Throwable) {
                CustomToast.showToast(
                    context, "Lỗi kết nối!", CustomToast.ToastType.WARNING
                )
                Log.e("AddProduct", "onFailure: " + t.message)
            }

        })
    }

    private fun callFixProduct() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.fixProduct(
            ProductAddReq2(
                role!!.id,
                mbinding.price.text.toString().trim().toInt(),
                mbinding.discountPercentage.text.toString().trim().toInt(),
                mbinding.stock.text.toString().trim().toInt()
            ), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<CategoryFixRes> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<CategoryFixRes>, response: Response<CategoryFixRes>
            ) {
                if (response.body() != null) {
                        CustomToast.showToast(
                            context, "Cập nhật thành công!", CustomToast.ToastType.SUCCESS
                        )
                        dismiss()
                        onSucess()
                    }
                Log.i("AddCategory", response.body().toString())
            }

            override fun onFailure(call: Call<CategoryFixRes>, t: Throwable) {
                CustomToast.showToast(
                    context, "Lỗi kết nối!", CustomToast.ToastType.WARNING
                )
                Log.e("AddCategory", "onFailure: " + t.message)
            }

        })
    }
}
