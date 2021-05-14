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
import com.mobilepoc.myvendor.data.entites.Client
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.databinding.ActivityClientListBinding
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.Util
import com.mobilepoc.myvendor.view.adapters.ClientListAdapter
import com.myshoppal.ui.activities.BaseActivity
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_client_list.*


class ClientListActivity : BaseActivity() {
    private var mSelectClient: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_SELECT_CLIENT)){
            mSelectClient = intent.getBooleanExtra(Constants.EXTRA_SELECT_CLIENT,false)
        }

        setupActionBar()

        if(mSelectClient){
            tv_title_client_list.text = resources.getString(R.string.title_select_client)
        }

        binding.tvAddClient.setOnClickListener{
            val intent = Intent(this,AddEditClientActivity::class.java)
            startActivityForResult(intent, Constants.ADD_CLIENT_REQUEST_CODE)

        }
        getClientList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_CLIENT_REQUEST_CODE) {

                getClientList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Um registro é impresso quando o usuário fecha ou cancela
            Log.e("Requisicao Cancelada", "Para adicionar o endereço.")
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_client_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_client_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successClientListFromFirestore(clientList: ArrayList<Client>){
        hideProgressDialog()
        if(clientList.size > 0){
            rv_client_list.visibility = View.VISIBLE
            tv_no_client_found.visibility = View.GONE

            rv_client_list.layoutManager = LinearLayoutManager(this)
            rv_client_list.setHasFixedSize(true)

            val clientAdapter = ClientListAdapter(this, clientList, mSelectClient)
            rv_client_list.adapter = clientAdapter

            if (!mSelectClient){
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_client_list.adapter as ClientListAdapter
                        adapter.notifyEditItem(
                                this@ClientListActivity,
                                viewHolder.bindingAdapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_client_list)

                val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog()
                        FireStoreClass().deleteClient(
                                this@ClientListActivity,
                                clientList[viewHolder.absoluteAdapterPosition].id
                        )
                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_client_list)
            }

        }else{
            rv_client_list.visibility = View.GONE
            tv_no_client_found.visibility = View.VISIBLE
        }
    }
    private  fun getClientList(){
        showProgressDialog()
        FireStoreClass().getClientList(this)
    }

    fun deleteClientSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext, resources.getString(R.string.err_your_client_deleted_successfully))
        getClientList()
    }


}