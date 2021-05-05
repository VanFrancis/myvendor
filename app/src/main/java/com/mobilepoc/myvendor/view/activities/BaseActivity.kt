package com.myshoppal.ui.activities


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.utils.DialogProgress
import com.mobilepoc.myvendor.utils.Util
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.dialog_progress.*
import java.util.*


open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    val dialogoProgress = DialogProgress()
    /**
     * Uma função para mostrar o sucesso e mensagens e de erro no componente snack bar
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.sea_pink
                    )
            )
        }else{
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.vista_blue
                    )
            )
        }
        snackBar.show()
    }

    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Util.exibirToast(baseContext, resources.getString(R.string.please_click_back_again_to_exit))

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    fun showProgressDialog(){
        dialogoProgress.show(supportFragmentManager,"1")
    }
    fun hideProgressDialog(){
        dialogoProgress.dismiss()
    }

}