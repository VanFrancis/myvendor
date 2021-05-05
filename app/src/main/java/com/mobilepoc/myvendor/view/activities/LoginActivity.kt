package com.mobilepoc.myvendor.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityLoginBinding
import com.mobilepoc.myvendor.utils.Util
import com.google.firebase.auth.ktx.auth
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.mobilepoc.myvendor.data.entites.User
import com.mobilepoc.myvendor.utils.Constants
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*


class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)

        auth = Firebase.auth

        //Carregar app com o ultimo logado
        val usuario = auth.currentUser
        if (usuario != null){
            finish()
            startActivity(Intent(this, DashboardActivity::class.java))
        }

    }

    /**
     * Função que gerencia componentes clicavéis
     */
    override fun onClick(v: View?) {
        if (v != null ){
            when(v.id){
                R.id.btn_login -> {
                    logInRegisteredUser()
                }
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }
    /**
     * Função que valida as entradas do usuário
     */
    private fun validacaoLogin():Boolean{
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_email))
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_password))
                false
            }
            else -> {
                true
            }
        }
    }
    /**
     *  Função que permite o login do usuário -> com autenticação Firebase
     */
    private fun logInRegisteredUser() {
        if (validacaoLogin()) {
            showProgressDialog()

            // Pega o texto do EditText retirando os espaços
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            // Login usando firebase
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this)
                    } else {
                        hideProgressDialog()
                        val erro = task.exception.toString()
                        errosFirebase(erro)
                    }
                }
        }
    }

    /**
     * Função para notificar o usuário que efetuou login com sucesso e obter os detalhes do usuário do banco FireStore após a autenticação.
     */
    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        if (user.profileCompleted == 0) {
            // Se o perfil do usuário estiver incompleto, inicie o UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirecione o usuário para a tela principal após o login.
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }

    fun errosFirebase(erro: String){
        if( erro.contains("There is no user record corresponding to this identifier")){
            Util.exibirToast(baseContext,"E-mail ainda não está cadastrado")
        }
        else if( erro.contains("The password is invalid")){
            Util.exibirToast(baseContext,"Senha inválida")
        }
        else if(erro.contains("The email address is badly ")){
            Util.exibirToast(baseContext,"Este e-mail não é válido")
        }
    }


}