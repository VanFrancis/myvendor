package com.mobilepoc.myvendor.view.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityUserProfileBinding
import com.mobilepoc.myvendor.model.FireStoreClass
import com.mobilepoc.myvendor.model.User
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.mobilepoc.myvendor.utils.Util
import com.myshoppal.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User
    private  var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            //Pega os detalhes do usuário  como um ParcelableExtra "Parcela Extra"
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)

        if (mUserDetails.profileCompleted == 0) {
            // Atualize o título da tela para completar o perfil.
            tv_title.text = resources.getString(R.string.title_complete_profile)

            // Aqui, alguns dos componentes de edição de texto são desabilitados porque são adicionados no momento do Registro.
            et_first_name.isEnabled = false
            et_last_name.isEnabled = false

            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)
        } else {
            setupActionBar()

            // Atualize o título da tela para editar o perfil.
            tv_title.text = resources.getString(R.string.title_edit_profile)

            // Carregue a imagem usando a classe GlideLoader com o uso da Biblioteca Glide.
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)

            // Defina os valores existentes para a IU e permita que o usuário edite, exceto a ID de e-mail.
            et_first_name.setText(mUserDetails.firstName)
            et_last_name.setText(mUserDetails.lastName)

            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)

            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
        }

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)

    }
    //Seta para voltar
    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    // verificar se a permissão já é permitida ou precisamos solicitá-la.
                    if (ContextCompat.checkSelfPermission(
                            this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                        //Util.exibirToast(baseContext,"Você já tem permissão de armazenamento ")
                    } else {
                        //Solicita permissão do usuário
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog()
                        if (mSelectedImageFileUri != null)
                            FireStoreClass().uploadImageToCloudStorage(this@UserProfileActivity, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)

                        else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }
    /**
     * função para atualizar os detalhes do perfil do usuário - firestore.
     */
    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        val firstName = et_first_name.text.toString().trim(){ it <= ' '}
        if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_last_name.text.toString().trim(){ it <= ' '}
        if (lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        // Aqui, os campos que não são editáveis não precisam de atualização. Portanto, vamos atualizar o número do celular e o sexo do usuário por enquanto.
        // Aqui, obtemos o texto de editText e cortamos o espaço
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty())
            userHashMap[Constants.IMAGE] = mUserProfileImageURL

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString())
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()

        if (gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //Faz uma entrada no banco de dados.
        FireStoreClass().updateUserProfileData(this,userHashMap)
    }

    /**
     * função para notificar o resultado do sucesso e prosseguir de acordo com a atualização dos detalhes do usuário.
     */
    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Util.exibirToast(baseContext,resources.getString(R.string.mgs_profile_update_success))

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //Se a permissão for concedida
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)

            } else {
                //Exibindo outra msg se a permissão não for concedida
                Util.exibirToast(baseContext,resources.getString(R.string.read_storage_permission_denied))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // O uri da imagem selecionada do armazenamento do telefone.
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Util.exibirToast(baseContext,resources.getString(R.string.image_selection_failed))
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Um registro é impresso quando o usuário fecha ou cancela a seleção da imagem.
            Log.e("Requisição Cancelada", "Imagem Selecionada Cancelada")
        }
    }
    /**
     * A função para validar as entradas de entrada para detalhes do perfil.
     */
    private fun validateUserProfileDetails(): Boolean {
        return when {

            // A imagem do perfil do usuário é opcional
            // O NOME, SOBRENOME e EMAIL não são editáveis quando vêm da tela de login.
            // O botão de opção para gênero sempre tem o valor padrão selecionado

            // Validando o numero de celular
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_enter_mobile_number))
                false
            }
            else -> {
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String){
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}