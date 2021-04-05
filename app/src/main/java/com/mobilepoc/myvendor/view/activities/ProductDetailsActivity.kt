package com.mobilepoc.myvendor.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityProductDetailsBinding
import com.mobilepoc.myvendor.model.CartItem
import com.mobilepoc.myvendor.model.FireStoreClass
import com.mobilepoc.myvendor.model.Product
import com.mobilepoc.myvendor.utils.Constants
import com.mobilepoc.myvendor.utils.GlideLoader
import com.mobilepoc.myvendor.utils.Util
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_register.*

class ProductDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        var productOwnerId: String = ""
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FireStoreClass().getUserIDAtual()== productOwnerId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE

        }else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()
       
    }

    private fun getProductDetails(){
        //TODO EXIBIR O BARRA PROGRESSO
        FireStoreClass().getProdructDetails(this, mProductId )
    }


    //Carrega os detalhes do produto
    fun productDetailsSuccess(product: Product){
        mProductDetails = product
        //Carrega a imagem do banco para image view
        GlideLoader(this).loadProductPicture(
                product.image,
                iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "R$ ${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity

       if (FireStoreClass().getUserIDAtual() != product.user_id){
            //TODO ESCONDER O BARRA PROGRESSO

       }else{
           FireStoreClass().checkIfItemExistInCart(this,mProductId)
       }

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_vector_arrow_left)
        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    //Preenche a coleção na base de dados
    private fun addToCart(){
        val cartItem = CartItem(
                FireStoreClass().getUserIDAtual(),
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )
        //TODO EXIBIR O BARRA PROGRESSO

        FireStoreClass().addCartItems(this, cartItem)
    }

    //Msg de sucesso
    fun addToCartSuccess() {
        //TODO ESCONDER O BARRA PROGRESSO
        Util.exibirToast(baseContext,resources.getString(R.string.success_message_item_added_to_cart))

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
       if (v != null)
           when(v.id){
               R.id.btn_add_to_cart ->{
                   addToCart()
               }
               R.id.btn_go_to_cart ->{
                    startActivity(Intent(this, CartListActivity::class.java))
               }
           }
    }

    /**
     * Existe uma função para notificar o resultado de sucesso do item no carrinho.
     */
    fun productExistsInCart() {
        //TODO ESCONDER O BARRA PROGRESSO
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

}