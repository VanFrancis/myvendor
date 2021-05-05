package com.mobilepoc.myvendor.view.fragments

import androidx.fragment.app.Fragment
import com.mobilepoc.myvendor.utils.DialogProgress
import com.myshoppal.ui.activities.BaseActivity


open class BaseFragment : Fragment() {

    val dialogoProgress = DialogProgress()

    fun showProgressDialog(){
        dialogoProgress.show(childFragmentManager,"1")
    }
    fun hideProgressDialog() {
        dialogoProgress.dismiss()
    }
}