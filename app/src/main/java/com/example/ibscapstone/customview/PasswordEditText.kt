package com.example.ibscapstone.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.ibscapstone.R

class PasswordEditText : AppCompatEditText {
    private var isPasswordVisible = false
    private lateinit var toggleDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        // Initialize the toggle drawable
        toggleDrawable = ContextCompat.getDrawable(context, R.drawable.ic_eye_off)!!
        setButtonDrawables(endOfTheText = toggleDrawable)

        transformationMethod = PasswordTransformationMethod.getInstance()

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val toggleButtonStart = (width - paddingEnd - toggleDrawable.intrinsicWidth)
                if (event.x >= toggleButtonStart) {
                    isPasswordVisible = !isPasswordVisible
                    transformationMethod = if (isPasswordVisible) {
                        setButtonDrawables(endOfTheText = ContextCompat.getDrawable(context, R.drawable.ic_eye_on))
                        null
                    } else {
                        setButtonDrawables(endOfTheText = ContextCompat.getDrawable(context, R.drawable.ic_eye_off))
                        PasswordTransformationMethod.getInstance()
                    }
                    setSelection(text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) < 8) {
                    error = "Password must be at least 8 characters"
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}