package com.mobilepoc.myvendor.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivitySettingsBinding
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.data.entites.User
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvEdit.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.llAddress.setOnClickListener(this)

    }
    override fun onResume() {
        super.onResume()

        getUserDetails()
    }
    /**
    * Função da classe Firestore para obter os detalhes do usuário do firestore que já foi criado.
     */
    private fun getUserDetails() {
        showProgressDialog()
        FireStoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user

        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_checkout_mobile_number.text = "${user.mobile}"
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when (v.id) {
                R.id.tv_edit -> {
                    val intent = Intent(this,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)

                }
                R.id.btn_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address ->{
                    val intent = Intent(this,ClientListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}