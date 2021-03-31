package com.mobilepoc.myvendor.view.activities

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.mobilepoc.myvendor.databinding.ActivityAddProductBinding
import com.mobilepoc.myvendor.model.FireStoreClass
import com.mobilepoc.myvendor.model.Product
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.mobilepoc.myvendor.utils.Util
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class AddProductActivity : AppCompatActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.ivAddUpdateProduct.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }



    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_update_product ->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }

                }
                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        //Util.exibirToast(baseContext,"Show! os campos validados")
                        uploadProductImage()
                    }
                }
            }
        }
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
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                    try {
                        // O uri da imagem selecionada do armazenamento do telefone.
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_product_image)
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
    * Função para validar cadastro do produto
    */
    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_select_product_image))
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_enter_product_title))
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_enter_product_price))
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_enter_product_description))
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                Util.exibirToast(baseContext,resources.getString(R.string.err_msg_enter_product_quantity))
                false
            }
            else -> {
                true
            }
        }
    }
    private fun uploadProductImage(){
        FireStoreClass().uploadImageToCloudStorage(
                this@AddProductActivity,
                mSelectedImageFileUri,
                Constants.PRODUCT_IMAGE
        )
    }

    /**
     * Função que retorna sucesso se o produto foi atualizado
     */
    fun productUploadSuccess() {
        Util.exibirToast(baseContext, resources.getString(R.string.product_uploaded_success_message))
        finish()
    }

    fun imageUploadSuccess(imageURL: String){
        //Util.exibirToast(baseContext,"A img foi enviada com sucess $imageURL ")
        mProductImageURL = imageURL

        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(
                Constants.MYVENDOR_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val product = Product(
                FireStoreClass().getUserIDAtual(),
                username,
                et_product_title.text.toString().trim { it <= ' ' },
                et_product_price.text.toString().trim { it <= ' ' },
                et_product_description.text.toString().trim { it <= ' ' },
                et_product_quantity.text.toString().trim { it <= ' ' },
                mProductImageURL
        )
        FireStoreClass().uploadProductDetails(this, product)

    }
}