package com.mobilepoc.myvendor.view.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityAddEditAddressBinding
import com.mobilepoc.myvendor.data.entites.Client
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.Util
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {
    private var mClientDetails: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mClientDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
        setupActionBar()

        if (mClientDetails != null) {
            if (mClientDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_client)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mClientDetails?.name)
                et_phone_number.setText(mClientDetails?.mobileNumber)
                et_address.setText(mClientDetails?.address)
                et_zip_code.setText(mClientDetails?.zipCode)
                et_additional_note.setText(mClientDetails?.additionalNote)

                when (mClientDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mClientDetails?.otherDetails)
                    }
                }
            }
        }

        binding.rgType.setOnCheckedChangeListener {_, checkedId ->
            if (checkedId == R.id.rb_other){
                til_other_details.visibility = View.VISIBLE
            }else {
                til_other_details.visibility = View.VISIBLE
            }
        }

        binding.btnSubmitAddress.setOnClickListener{ saveAddressToFirestore() }
    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveAddressToFirestore() {
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }

        if (validateData()) {
            showProgressDialog()

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Client(
                FireStoreClass().getUserIDAtual(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            if(mClientDetails != null && mClientDetails!!.id.isNotEmpty()){
                FireStoreClass().updateAddress(
                        this@AddEditAddressActivity,
                        addressModel,
                        mClientDetails!!.id
                )
            }else{
                FireStoreClass().addAddress(this@AddEditAddressActivity, addressModel)
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

            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun addUpdateAddressSuccess() {
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