package com.arduia.expense.ui.common.customview

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.addTextChangedListener
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.google.android.material.card.MaterialCardView

class MaterialSearchBox @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = R.attr.materialCardViewStyle
) : MaterialCardView(ctx, attrs, defStyleRes) {

    private val searchBoxMargin = px(16)
    private val searchIconSize = px(35)

    private val searchIcon: AppCompatImageView
    private val searchEditText: EditText
    private var textChangeListener: SearchTextChangeListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MaterialSearchBox, defStyleRes, 0)
        val backgroundColor = a.getColor(R.styleable.MaterialSearchBox_backgroundColor, Color.WHITE)
        val cornerRadius = a.getDimension(R.styleable.MaterialSearchBox_cornerRadius, 0f)
        val hint = a.getString(R.styleable.MaterialSearchBox_hint) ?: "Search"
        a.recycle()
        setCardBackgroundColor(backgroundColor)
        radius = cornerRadius
        searchIcon = AppCompatImageView(context, attrs, defStyleRes)
        searchEditText = EditText(context, attrs, defStyleRes).apply { setHint(hint) }

        setupViews()
    }

    private fun setupViews() {
        cardElevation  = 0f
        with(searchEditText) {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, px(50)).apply {
                marginStart = searchBoxMargin
                marginEnd = searchBoxMargin
                this.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            }
            gravity = Gravity.CENTER_VERTICAL
            this.isEnabled = true
            inputType = InputType.TYPE_CLASS_TEXT
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
        }
        with(searchIcon) {
            layoutParams = LayoutParams(searchIconSize, searchIconSize).apply {
                marginStart = searchBoxMargin
                marginEnd = searchBoxMargin
                this.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }
            isCheckable = false
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setImageResource(R.drawable.ic_search)
        }
        addView(searchIcon)
        addView(searchEditText)

        searchEditText.addTextChangedListener {
            if (it != null) textChangeListener?.onChanged(it.toString())
        }
    }

    fun setOnSearchTextChangeListener(listener: SearchTextChangeListener) {
        this.textChangeListener = listener
    }

    fun interface SearchTextChangeListener {
        fun onChanged(text: String)
    }
}
