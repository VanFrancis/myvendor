package com.mobilepoc.myvendor.view.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityRegisterBinding
import com.mobilepoc.myvendor.data.entites.User
import com.mobilepoc.myvendor.utils.DialogProgress
import com.mobilepoc.myvendor.utils.Util
import kotlinx.android.synthetic.main.activity_register.*
import com.mobilepoc.myvendor.data.model.FireStoreClass
import com.myshoppal.ui.activities.BaseActivity

class RegisterActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener(this)

        //Botão que volta para pagina tela anterior - action bar
        setupActionBar()

    }

    /**
     * Função que gerencia componentes clicavéis
     */
    override fun onClick(v: View?) {
        if (v != null ) {
            when (v.id) {
                R.id.btn_register -> {
                    registraUsuario()
                }
                R.id.tv_login ->{
                    onBackPressed()
                }
            }
        }
    }
    /**
     * Função que valida as entradas do usuário
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_first_name))
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_last_name))
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_email))
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_password))
                false
            }
            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_enter_confirm_password))
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_password_and_confirm_password_mismatch))
                false
            }
            !sm_terms_and_condition.isChecked -> {
                Util.exibirToast(this,resources.getString(R.string.err_msg_agree_terms_and_condition))
                false
            }
            else -> {
                true
            }
        }
    }
    /**
     * Função que registra o novo usuário
     */
    private fun registraUsuario() {

        // Verifica se os campos são válidos
        if (validateRegisterDetails()) {

            showProgressDialog()

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            val dialogoProgress = DialogProgress()
            dialogoProgress.show(supportFragmentManager,"1")

            // Cria uma instancia e registra um novo usuario com email e senha
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    dialogoProgress.dismiss()
                    // Se for um sucesso
                    if (task.isSuccessful) {

                        // Registra novo usuário - Firebase Authentication
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            et_first_name.text.toString().trim { it <= ' ' } ,
                            et_last_name.text.toString().trim { it <= ' ' } ,
                            et_email.text.toString().trim { it <= ' ' }
                        )

                        //Registra novo usuário - Cloud Firestore
                        FireStoreClass().registraUsuario(this@RegisterActivity, user)

                        //FirebaseAuth.getInstance().signOut()
                        //finish()
                    } else {
                        hideProgressDialog()
                        // Se não for sucesso, msg de erro
                        val erro = task.exception.toString()
                        Util.exibirToast(this,erro)
                    }
                })
        }
    }
    /**
     * Função adiciona botão na actionbar para voltar para tela anterior
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext, resources.getString(R.string.register_success))

    }

}