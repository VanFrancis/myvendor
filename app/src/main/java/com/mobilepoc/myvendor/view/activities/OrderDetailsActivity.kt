package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Order
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.view.adapters.CartItemsListAdapter
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        setupActionBar()

        var orderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)) {
            orderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_DETAILS)!!
        }
        setupUI(orderDetails)
    }

    //Cabeçalho do detalhe do pedido
    private fun setupUI(orderDetails: Order) {

        tv_order_details_id.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        tv_order_details_date.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Diferenca em Horas", "$diffInHours")

        when {
            diffInHours < 1 -> {
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.sea_pink
                    )
                )
            }
            diffInHours < 2 -> {
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {
                tv_order_status.text = resources.getString(R.string.order_status_delivered)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@OrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        //Popular a lista de itens do pedido
        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@OrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_full_name.text = orderDetails.client.name
        tv_my_order_details_address.text =
            "${orderDetails.client.address}, ${orderDetails.client.zipCode}"
        tv_my_order_details_additional_note.text = orderDetails.client.additionalNote

        tv_my_order_details_mobile_number.text = orderDetails.client.mobileNumber

        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }

        toolbar_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
}