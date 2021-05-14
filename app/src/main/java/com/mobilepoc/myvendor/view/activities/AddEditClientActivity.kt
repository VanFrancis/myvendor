package com.mobilepoc.myvendor.view.activities

import android.os.Bundle
import android.text.TextUtils
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.data.entites.Client
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.databinding.ActivityAddEditClientBinding
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.Util
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_add_edit_client.*

class AddEditClientActivity : BaseActivity() {
    private var mClientDetails: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddEditClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_CLIENT_DETAILS)){
            mClientDetails = intent.getParcelableExtra(Constants.EXTRA_CLIENT_DETAILS)!!
        }
        setupActionBar()

        if (mClientDetails != null) {
            if (mClientDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_client)
                btn_submit_client.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mClientDetails?.name)
                et_phone_number.setText(mClientDetails?.mobileNumber)
                et_address.setText(mClientDetails?.address)
                et_zip_code.setText(mClientDetails?.zipCode)
                et_additional_note.setText(mClientDetails?.additionalNote)

            }
        }


        binding.btnSubmitClient.setOnClickListener{ saveClientToFirestore() }
    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_edit_client_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_add_edit_client_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveClientToFirestore() {
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }

        if (validateData()) {
            showProgressDialog()

            val clientModel = Client(
                FireStoreClass().getUserIDAtual(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
            )
            if(mClientDetails != null && mClientDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddress(
                        this@AddEditClientActivity,
                        clientModel,
                        mClientDetails!!.id
                )
            }else{
                FireStoreClass().addClient(this@AddEditClientActivity, clientModel)
            }
        }
    }


    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun addUpdateClientSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String = if (mClientDetails != null && mClientDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_client_updated_successfully)
        } else {
            resources.getString(R.string.err_your_client_added_successfully)
        }
        Util.exibirToast(baseContext,notifySuccessMessage)
        setResult(RESULT_OK)
        finish()
    }
}