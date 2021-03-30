package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityForgotPasswordBinding
import com.mobilepoc.myvendor.utils.Util
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnForgotPassword.setOnClickListener(this)

        setupActionBar()
    }
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_email))
                false
            }
            else -> {
                Util.exibirToast(this,resources.getString(R.string.success_send_email))
                true
            }
        }
    }
    private fun ForgotPasswordUser(){
        if(validateRegisterDetails()) {
            val email: String = et_email.text.toString().trim { it <= ' ' }

            //create instance
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Util.exibirToast(this,"E-mail enviado com sucesso")
                        finish()
                    } else
                        Util.exibirToast(this,"Ocorreu um erro. Verifique se o email foi digitado corretamente")
                }
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar_forgot_password)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_forgot_password.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null ) {
            when (v.id) {
                R.id.btn_forgot_password ->{
                    ForgotPasswordUser()
                }
            }
        }
    }


}