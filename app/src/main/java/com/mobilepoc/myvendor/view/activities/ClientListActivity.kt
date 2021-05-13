package com.mobilepoc.myvendor.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Address
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.databinding.ActivityAddressListBinding
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.Util
import com.mobilepoc.myvendor.view.adapters.AddressListAdapter
import com.myshoppal.ui.activities.BaseActivity
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*


class AddressListActivity : BaseActivity() {
    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }

        setupActionBar()

        if(mSelectAddress){
            tv_title_address_list.text = resources.getString(R.string.title_select_address)
        }

        binding.tvAddAddress.setOnClickListener{
            val intent = Intent(this,AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)

        }
        getAddressesList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getAddressesList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Um registro é impresso quando o usuário fecha ou cancela
            Log.e("Requisicao Cancelada", "Para adicionar o endereço.")
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>){
        hideProgressDialog()
        if(addressList.size > 0){
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList, mSelectAddress)
            rv_address_list.adapter = addressAdapter

            if (!mSelectAddress){
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                                this@AddressListActivity,
                                viewHolder.bindingAdapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog()
                        FireStoreClass().deleteAddress(
                                this@AddressListActivity,
                                addressList[viewHolder.absoluteAdapterPosition].id
                        )
                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }

        }else{
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }
    private  fun getAddressesList(){
        showProgressDialog()
        FireStoreClass().getAddressesList(this)
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext, resources.getString(R.string.err_your_address_deleted_successfully))
        getAddressesList()
    }


}