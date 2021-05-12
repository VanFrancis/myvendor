package com.mobilepoc.myvendor.view.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Address
import com.mobilepoc.myvendor.data.entites.CartItem
import com.mobilepoc.myvendor.data.entites.Product
import com.mobilepoc.myvendor.data.model.FireStoreClass
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilepoc.myvendor.data.entites.Order
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.Util
import com.mobilepoc.myvendor.view.adapters.CartItemsListAdapter
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_checkout.*


class CheckoutActivity : BaseActivity() {
    private var mAddressDetails : Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal = 0.00
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()


        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if(mAddressDetails?.otherDetails!!.isNotEmpty()){
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }
    fun successProductFromFireStore(productsList: ArrayList<Product>){
        mProductList = productsList
        getCartItemsList()
    }
    private fun getCartItemsList(){
        FireStoreClass().getCartList(this)
    }

    private fun getProductList(){
        showProgressDialog()
        FireStoreClass().getAllProductsList(this)
    }
    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()
        for (product in mProductList){
            for (cart in cartList){
                if (cart in cartList){
                    if (product.product_id == cart.product_id){
                        cart.stock_quantity = product.stock_quantity
                    }
                }
            }
        }
        mCartItemsList = cartList
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "R$ $mSubTotal"
        tv_checkout_shipping_charge.text = "R$ 10.00"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.00
            tv_checkout_total_amount.text = "R$ $mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }

    }

    private fun placeAnOrder(){
        showProgressDialog()
        if (mAddressDetails != null){
            val order = Order(
                FireStoreClass().getUserIDAtual(),
                mCartItemsList,
                mAddressDetails!!,
                "Meu pedido ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.00",
                mTotalAmount.toString(),
            )
            FireStoreClass().placeOrder(this@CheckoutActivity, order)
        }


    }
    fun orderPlacedSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext, "Seu pedido foi realizado com sucesso!")

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}