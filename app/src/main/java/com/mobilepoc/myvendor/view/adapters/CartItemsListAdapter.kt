package com.mobilepoc.myvendor.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.CartItem
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.mobilepoc.myvendor.view.activities.CartListActivity
import kotlinx.android.synthetic.main.item_cart_layout.view.*

open class CartItemsListAdapter(
        private val context: Context,
        private var list: ArrayList<CartItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Insere o item_cart_layout.xml como item da Recycleview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_cart_layout,
                        parent,
                        false
                )
        )
    }

    /**
     * Vincula cada item na ArrayList a uma view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_cart_item_image)

            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_item_price.text = "R$ ${model.price}"
            holder.itemView.tv_cart_quantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.itemView.ib_remove_cart_item.visibility = View.GONE
                holder.itemView.ib_add_cart_item.visibility = View.GONE

                holder.itemView.tv_cart_quantity.text =
                        context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.sea_pink
                        )
                )
            } else {
                holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                holder.itemView.ib_add_cart_item.visibility = View.VISIBLE

                holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                                context,
                                R.color.astronaut
                        )
                )
            }

            //Atribua o evento onclick ao ib_delete_cart_item.
            holder.itemView.ib_delete_cart_item.setOnClickListener {

                when (context) {
                    is CartListActivity -> {
                       context.showProgressDialog()
                    }
                }
                FireStoreClass().removeItemFromCart(context, model.id)
            }
            holder.itemView.ib_remove_cart_item.setOnClickListener{
                if (model.cart_quantity == "1"){
                    FireStoreClass().removeItemFromCart(context,model.id)
                }
                else{
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog()
                    }

                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }
            //Aumenta a quantidade do item
            holder.itemView.ib_add_cart_item.setOnClickListener{
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog()
                    }

                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                                context.resources.getString(
                                        R.string.msg_for_available_stock,
                                        model.stock_quantity
                                ),
                                true
                        )
                    }
                }
            }

        }
    }

    /**
     * Numero de item na lista
     */
    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}