package com.dragnell.a2p_shop.view.act

import CustomToast
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.DetailOrderBinding
import com.dragnell.a2p_shop.databinding.MainAdminActBinding
import com.dragnell.a2p_shop.model.Order
import com.dragnell.a2p_shop.model.res_api.AccountInfoRes
import com.dragnell.a2p_shop.model.res_api.ClientLogOutRes
import com.dragnell.a2p_shop.model.res_api.UserInfoRes
import com.dragnell.a2p_shop.view.api.Api
import com.dragnell.a2p_shop.view.api.ApiInterface
import com.dragnell.a2p_shop.view.fragment.AccountsFragment
import com.dragnell.a2p_shop.view.fragment.CategoryFragment
import com.dragnell.a2p_shop.view.fragment.MyCartFragment
import com.dragnell.a2p_shop.view.fragment.OrderAdminFragment
import com.dragnell.a2p_shop.view.fragment.PlaceAdminFragment
import com.dragnell.a2p_shop.view.fragment.PlaceClientFragment
import com.dragnell.a2p_shop.view.fragment.ProductOrderFragment
import com.dragnell.a2p_shop.view.fragment.RevenueFragment
import com.dragnell.a2p_shop.viewmodel.CommonViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailOrderClientActivity : BaseActivity<DetailOrderBinding, CommonViewModel>() {
    private lateinit var order: Order
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        order = intent.getSerializableExtra("order") as Order
        clickView(mbinding.product)
        mbinding.product.setOnClickListener(this)
        mbinding.place.setOnClickListener(this)
    }


    override fun initViewBinding(): DetailOrderBinding {
        return DetailOrderBinding.inflate(layoutInflater)
    }

    override fun clickView(v: View) {
        super.clickView(v)
        if (v == mbinding.place){
            if (order.status!="shipped"){
                CustomToast.showToast(this,"Đơn hàng không trong trạng thái giao hàng!", CustomToast.ToastType.WARNING)
            }
            else{
                showFragment(PlaceClientFragment.TAG, null, false, R.id.fr)
            }
        }
        if (v == mbinding.product) {
            showFragment(ProductOrderFragment.TAG, null, false, R.id.fr)
        }
    }



}