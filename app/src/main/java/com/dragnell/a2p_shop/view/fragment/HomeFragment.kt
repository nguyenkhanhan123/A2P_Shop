package com.dragnell.a2p_shop.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.databinding.HomeBinding
import com.dragnell.a2p_shop.view.act.Search
import com.dragnell.a2p_shop.viewmodel.CommonViewModel

class HomeFragment : BaseFragment<HomeBinding, CommonViewModel>() {
    override fun getClassVM(): Class<CommonViewModel> {
        return CommonViewModel::class.java
    }

    override fun initView() {
        mbinding.search.setOnClickListener {
            val intent = Intent(context, Search::class.java)
            requireContext().startActivity(intent)
        }
    }

    override fun initViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): HomeBinding {
        return HomeBinding.inflate(layoutInflater)
    }

    companion object {
        val TAG: String = HomeFragment::class.java.name
    }
}