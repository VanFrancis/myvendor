package com.mobilepoc.myvendor.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MVRadioButton(context: Context, attr: AttributeSet): AppCompatRadioButton(context, attr) {
    init{
        applyFont()

    }
    fun applyFont(){
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets,"fonts/Oswald-Bold.ttf")
        setTypeface(typeface)
    }

}