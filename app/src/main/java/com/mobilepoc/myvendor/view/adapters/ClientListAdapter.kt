package com.mobilepoc.myvendor.view.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Client
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.view.activities.AddEditClientActivity
import com.mobilepoc.myvendor.view.activities.CheckoutActivity
import kotlinx.android.synthetic.main.item_client_layout.view.*


open class ClientListAdapter (
    private val context: Context,
    private var list: ArrayList<Client>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_client_layout,
                        parent,
                        false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

            if (selectAddress){
                holder.itemView.setOnClickListener{
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_CLIENT, model)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int){
        val intent = Intent(context, AddEditClientActivity::class.java)
        intent.putExtra(Constants.EXTRA_CLIENT_DETAILS,list[position])
        activity.startActivityForResult(intent,Constants.ADD_CLIENT_REQUEST_CODE)
        notifyItemChanged(position)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
