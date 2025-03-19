package com.dragnell.a2p_shop.view.act

import android.view.View
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.MainactBinding
import com.dragnell.a2p_shop.view.fragment.HomeFragment
import com.dragnell.a2p_shop.view.fragment.MyCartFragment
import com.dragnell.a2p_shop.view.fragment.ProfileFragment
import com.dragnell.a2p_shop.viewmodel.CommonViewModel

class MainActivity : BaseActivity<MainactBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        clickView(mbinding.menu.home)
        mbinding.menu.home.setOnClickListener(this)
        mbinding.menu.mycart.setOnClickListener(this)
        mbinding.menu.profile.setOnClickListener(this)
    }

    override fun initViewBinding(): MainactBinding {
        return MainactBinding.inflate(layoutInflater)
    }

    override fun clickView(v: View) {
        super.clickView(v)
        if (v == mbinding.menu.home) {
            showFragment(HomeFragment.TAG, null, false, R.id.fr)
        }
        if (v == mbinding.menu.mycart) {
            showFragment(MyCartFragment.TAG, null, false, R.id.fr)
        }
        if (v == mbinding.menu.profile) {
            showFragment(ProfileFragment.TAG, null, false, R.id.fr)
        }
    }

}