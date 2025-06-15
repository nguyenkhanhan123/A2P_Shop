package com.dragnell.a2p_shop.view.act

import CustomToast
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.databinding.ProductBinding
import com.dragnell.a2p_shop.model.Product
import com.dragnell.a2p_shop.model.req_api.ProductDeleteReq
import com.dragnell.a2p_shop.model.res_api.CategoryDeleteRes
import com.dragnell.a2p_shop.model.res_api.CategorySlugRes
import com.dragnell.a2p_shop.view.adapter.ProductAdapter
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.view.dialog.AddProductDialog
import com.dragnell.a2p_shop.view.dialog.FixOrDeleteDialog
import com.dragnell.a2p_shop.view.dialog.FixOrDeleteDialog2
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductActivity : BaseActivity<ProductBinding, CommonViewModel>() {
    lateinit var id: String
    lateinit var title: String
    lateinit var slug: String
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        id = intent.getStringExtra("id").toString()
        title=intent.getStringExtra("title").toString()
        slug=intent.getStringExtra("slug").toString()
        mbinding.tvTittle.text=title
        mbinding.add.setOnClickListener {
            val dialog =
                AddProductDialog(this, id, null, onSucess = {
                    callGetProduct()
                }, onClick = {
                    val url = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(it)
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    startActivity(intent)
                })
            dialog.behavior.isHideable = false
            dialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            dialog.show()
        }
        callGetProduct()
    }

    private fun callGetProduct() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.getProductBySlugCategoryAdmin(slug, "Bearer ${CommonUtils.getInstance().getPref("Token")}")
            .enqueue(object : Callback<CategorySlugRes> {
                override fun onResponse(
                    call: Call<CategorySlugRes>, response: Response<CategorySlugRes>
                ) {
                    if (response.body()?.products?.isNotEmpty() == true) {
                        val productList = response.body()!!.products
                        mbinding.rvProduct.adapter = ProductAdapter(
                            response.body()!!.products, this@ProductActivity, onClickFolder = { i ->
                                val intent: Intent = Intent(
                                    this@ProductActivity, DetailProduct::class.java
                                )
                                intent.putExtra("slug", i)
                                startActivity(intent)
                            },
                            onLongClickFolder = {
                                val dialog = FixOrDeleteDialog2(this@ProductActivity, onFix = {
                                    callFixCategory(it)
                                }, onDelete = {
                                    callDeleteCategory(it)
                                })
                                dialog.show()
                            })
                    } else {
                        CustomToast.showToast(
                            this@ProductActivity,
                            "Không tìm thấy sản phẩm phù hợp!",
                            CustomToast.ToastType.INFO
                        )
                    }
                    Log.e("KQ", response.body().toString())
                }

                override fun onFailure(call: Call<CategorySlugRes>, t: Throwable) {
                    CustomToast.showToast(
                        this@ProductActivity,
                        "Lỗi kết nối!",
                        CustomToast.ToastType.WARNING
                    )
                    Log.e("KQ", "onFailure: " + t.message)
                }

            })
    }

    override fun initViewBinding(): ProductBinding {
        return ProductBinding.inflate(layoutInflater)
    }

    private fun callDeleteCategory(category: Product) {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.deleteProduct(
            ProductDeleteReq(category.id), "Bearer ${CommonUtils.getInstance().getPref("Token")}"
        ).enqueue(object : Callback<CategoryDeleteRes> {
            override fun onResponse(
                call: Call<CategoryDeleteRes>, response: Response<CategoryDeleteRes>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.message == "xoa thanh cong!") {
                        CustomToast.showToast(
                            this@ProductActivity, "Xóa thành công!", CustomToast.ToastType.SUCCESS
                        )
                        callGetProduct()
                    } else {
                        CustomToast.showToast(
                            this@ProductActivity,
                            "Chưa thể cập nhật bây giờ!",
                            CustomToast.ToastType.INFO
                        )
                    }
                }
                Log.e("KQ", response.body().toString())

            }

            override fun onFailure(call: Call<CategoryDeleteRes>, t: Throwable) {
                Log.e("KQ", "onFailure: " + t.message)
            }

        })
    }

    private fun callFixCategory(category: Product) {
        val dialog =
            AddProductDialog(this@ProductActivity, id, category, onSucess = {
                callGetProduct()
            }, onClick = {
                val url = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(it)
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
            })
        dialog.behavior.isHideable = false
        dialog.behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        dialog.show()
    }
}
