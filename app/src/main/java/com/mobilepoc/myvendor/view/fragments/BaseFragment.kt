package com.mobilepoc.myvendor.view.fragments

import androidx.fragment.app.Fragment
import com.mobilepoc.myvendor.utils.DialogProgress


open class BaseFragment : Fragment() {
    val dialogoProgress = DialogProgress()

    fun showProgressDialog(){
        dialogoProgress.show(childFragmentManager,"1")
    }
    fun hideProgressDialog() {
        dialogoProgress.dismiss()
    }

}