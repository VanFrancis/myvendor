package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityCartListBinding
import com.mobilepoc.myvendor.model.CartItem
import com.mobilepoc.myvendor.model.FireStoreClass
import com.mobilepoc.myvendor.view.adapters.CartItemsListAdapter
import kotlinx.android.synthetic.main.activity_cart_list.*



class CartListActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCheckout.setOnClickListener(this)

    }

    fun successCartItemsList(cartList: ArrayList<CartItem>){
        //TODO ESCONDER O BARRA PROGRESSO

        if (cartList.size > 0 ){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE //LinearLayout
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this, cartList)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double = 0.0

            for (item in cartList) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                subTotal += (price * quantity)
            }

            tv_sub_total.text = "R$ $subTotal"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            tv_shipping_charge.text = "R$ 10.00"

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 10
                tv_total_amount.text = "R$ $total"
            } else {
                ll_checkout.visibility = View.GONE
            }

        } else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getCartitemsList()
    }

    private fun getCartitemsList(){
        //TODO EXIBE O BARRA PROGRESSO
        FireStoreClass().getCartList(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}