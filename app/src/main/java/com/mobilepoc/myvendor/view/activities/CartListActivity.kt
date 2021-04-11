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
import com.mobilepoc.myvendor.model.Product
import com.mobilepoc.myvendor.utils.Util
import com.mobilepoc.myvendor.view.adapters.CartItemsListAdapter
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_cart_list.*



class CartListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItem: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCheckout.setOnClickListener(this)

    }

    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductsList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt()== 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItem = cartList

        if (mCartListItem.size > 0 ){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE //LinearLayout
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this, cartList)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double = 0.00

            for (item in mCartListItem) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0 ){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)

                }
            }

            tv_sub_total.text = "R$ $subTotal"
            //Valor fixo do frete
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
        getProductsList()
    }

    fun successProductListFromFireStore(productsList: ArrayList<Product>){
        mProductsList = productsList
        getCartitemsList()

    }
    private fun getProductsList(){
        showProgressDialog()
        FireStoreClass().getAllProductsList(this)
    }

    private fun getCartitemsList(){
        FireStoreClass().getCartList(this)
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext, resources.getString(R.string.mgs_item_removed_successfully))
        getCartitemsList()
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartitemsList()
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