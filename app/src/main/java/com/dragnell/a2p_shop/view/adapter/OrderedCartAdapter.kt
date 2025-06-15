package com.dragnell.a2p_shop.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dragnell.a2p_shop.CommonUtils
import com.dragnell.a2p_shop.R
import com.dragnell.a2p_shop.model.ProductOrder
import com.dragnell.a2p_shop.model.Products

class OrderedCartAdapter(
    private var list: List<ProductOrder>,
    private var context: Context
) : RecyclerView.Adapter<OrderedCartAdapter.ProductsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.item_product_order, parent, false)
        return ProductsHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductsHolder, position: Int) {
        val products: ProductOrder = list[position]
        Glide.with(context).load(products.thumbnail).into(holder.ivProduct)
        holder.nameProduct.text = products.title
        holder.cost.text = "$ ${products.price}"
        val discountedPrice = (products.price!! * (1 - products.discountPercentage!!.toDouble() / 100)).toInt()
        holder.totalCost.text = "$ $discountedPrice"
        holder.txtQuantity.text = products.quantity.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ProductsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameProduct: TextView
        var totalCost: TextView
        var cost: TextView
        var txtQuantity: TextView
        var ivProduct: ImageView

        init {
            nameProduct = itemView.findViewById(R.id.txtProductName)
            totalCost = itemView.findViewById(R.id.txtProductPrice)
            cost = itemView.findViewById(R.id.txtOriginalPrice)
            ivProduct = itemView.findViewById(R.id.imgProductCart)
            txtQuantity = itemView.findViewById(R.id.txtQuantity)
        }
    }
}
