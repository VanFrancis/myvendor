package com.mobilepoc.myvendor.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class MVTextViewBold(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {
    init {
        applyFont()
    }

    private fun applyFont() {
        //This is used to get the file from the assets folder and set to the title TextView.
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets,"fonts/Oswald-Bold.ttf")
        setTypeface(typeface)
    }
}