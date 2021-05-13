package com.mobilepoc.myvendor.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Order
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.view.adapters.MyOrdersListAdapter
import com.mobilepoc.myvendor.viewmodel.OrdersViewModel
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {

    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        ordersViewModel =
                ViewModelProvider(this).get(OrdersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders, container, false)


        return root
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>){
        hideProgressDialog()
        if (ordersList.size > 0) {

            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rv_my_order_items.adapter = myOrdersAdapter
        } else {
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }

    }

    private fun getMyOrdersList() {
        showProgressDialog()
        FireStoreClass().getMyOrdersList(this@OrdersFragment)
    }
}