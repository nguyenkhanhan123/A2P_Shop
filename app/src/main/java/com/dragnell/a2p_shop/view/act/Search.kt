package com.dragnell.a2p_shop.view.act

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.dragnell.a2p_shop.databinding.SearchBinding
import com.dragnell.a2p_shop.model.res_api.SearchRes
import com.dragnell.a2p_shop.view.adapter.ProductAdapter
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Search : BaseActivity<SearchBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        mbinding.tvSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                callAPI()
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        mbinding.icSearch.setOnClickListener {
            callAPI()
            hideKeyboard()
        }
    }

    private fun callAPI() {
        val apiInterface: ApiInterface = Api.client.create(ApiInterface::class.java)
        apiInterface.searchProducts(mbinding.tvSearch.text.toString())
            .enqueue(object : Callback<SearchRes> {
                override fun onResponse(
                    call: Call<SearchRes>, response: Response<SearchRes>
                ) {
                    mbinding.tvSearch.setText("")
                    if (response.body()?.products?.isNotEmpty() == true) {
                        mbinding.rvProduct.adapter =
                            ProductAdapter(response.body()!!.products, this@Search)
                    }
                    Log.e("KQ", response.body().toString())
                }

                override fun onFailure(call: Call<SearchRes>, t: Throwable) {
                    Log.e("KQ", "onFailure: " + t.message)
                }

            })
    }

    override fun initViewBinding(): SearchBinding {
        return SearchBinding.inflate(layoutInflater)
    }

    private fun Activity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}