package com.mobilepoc.myvendor.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Order
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.mobilepoc.myvendor.view.activities.OrderDetailsActivity
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class MyOrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_item_image
            )

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "R$ ${model.total_amount}"
            holder.itemView.ib_delete_product.visibility = View.GONE

            //Detalhe do Pedido
            holder.itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ORDER_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}