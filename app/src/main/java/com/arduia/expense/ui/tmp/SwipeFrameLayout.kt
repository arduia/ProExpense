package com.arduia.expense.ui.tmp

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.ItemExpenseLogBinding
import com.arduia.expense.ui.vto.ExpenseVto
import kotlin.math.abs

class SwipeFrameLayout @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(ctx, attrs, defStyleRes) {

    private val binding by lazy { ItemExpenseLogBinding.bind(this) }

    private var minEndMargin = ctx.px(80f)
    private var minStartMargin = ctx.px(46f)
    private var currentTranslateAnimation: ValueAnimator? = null


    private var lastTranslationPhase = 0f
    private var idleToEndDuration = 500L

    private var currentState = STATE_IDLE
    private var isSwipeTouchActive = false
    private var halfWidth = 0F

    fun bindData(data: ExpenseVto) {
        with(binding) {
            tvAmount.text = data.amount
            tvCurrencySymbol.text = data.currencySymbol
            tvDate.text = data.date
            tvName.text = data.name
            imvCategory.setImageResource(data.category)
        }
        binding.cdExpense.setOnLongClickListener {
            if (currentState == STATE_IDLE) {
                translateIdleToEnd()
            } else if(currentState == STATE_END_LOCKED){
                translateEndToIdle()
            }
            return@setOnLongClickListener true
        }
    }

    fun changeState(newState: Int) {
        when {
            (currentState == STATE_IDLE) && newState == STATE_END_LOCKED -> translateIdleToEnd()
            (currentState == STATE_IDLE) && newState == STATE_START_LOCKED -> translateIdleToStart()
            (currentState == STATE_END_LOCKED) && newState == STATE_IDLE -> translateEndToIdle()
        }
    }

    private fun translateIdleToEnd() {
        if (currentState != STATE_IDLE) return
        binding.cdExpense.isClickable = false
        currentTranslateAnimation?.cancel()
        val startPosition = 0f
        val endPosition = -minEndMargin
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = idleToEndDuration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == -minEndMargin) {
                    currentState = STATE_END_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun calculateDuration(start: Float, end: Float) = abs(abs(start) - abs(end)).toLong() / 300

    private fun translateIdleToStart() {
        if (currentState != STATE_IDLE) return
        binding.cdExpense.isClickable = false
        currentTranslateAnimation?.cancel()
        val startPosition = 0f
        val endPosition = minStartMargin
        val duration = calculateDuration(startPosition, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = duration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == minStartMargin) {
                    currentState = STATE_START_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateCurrentToIdle(currentX: Float) {
        currentTranslateAnimation?.cancel()
        val endPosition = 0f
        val duration = calculateDuration(currentX, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(currentX, endPosition).apply {
            this.duration = duration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == 0f) {
                    currentState = STATE_IDLE
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateCurrentToStart(currentX: Float) {
        currentTranslateAnimation?.cancel()

        val endPosition = minStartMargin
        val duration = calculateDuration(currentX, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(currentX, endPosition).apply {
            this.duration = duration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == minStartMargin) {
                    currentState = STATE_END_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateCurrentToEnd(currentX: Float) {
        currentTranslateAnimation?.cancel()
        val startPosition = currentX
        val endPosition = -minEndMargin
        val duration = calculateDuration(startPosition, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = duration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == -minEndMargin) {
                    currentState = STATE_END_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateEndToIdle() {
        if (currentState != STATE_END_LOCKED) return
        binding.cdExpense.isClickable = false
        currentTranslateAnimation?.cancel()
        val start = -minEndMargin
        val end = 0f

        currentTranslateAnimation = ValueAnimator.ofFloat(start, end).apply {
            this.duration = idleToEndDuration
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == 0f) {
                    currentState = STATE_IDLE
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    fun onSwipe(isOnTouch: Boolean, dx: Float) {

        if (isOnTouch && (binding.cdExpense.translationX <= halfWidth)) {
            translateView(dx + lastTranslationPhase)
        }

        if (isSwipeTouchActive and isOnTouch.not()) {
            onTouchReleased(dx, binding.cdExpense.translationX)
        }

        isSwipeTouchActive = isOnTouch
    }

    private fun translateView(translationX: Float) {
        binding.cdExpense.translationX = translationX
        when {
            translationX > 10 -> onSwipeStartToEnd()
            translationX < -10 -> onSwipeEndToStart()
        }
    }

    private fun onSwipeStartToEnd() {
        binding.flBg.setBackgroundResource(R.color.green_400)
    }

    private fun onSwipeEndToStart() {
        binding.flBg.setBackgroundResource(R.color.red_400)
    }


    private fun onTouchReleased(dx: Float, translationX: Float) {
        when {
            translationX >= minStartMargin -> {
                translateCurrentToStart(translationX)
            }
            translationX <= -minEndMargin -> {
                translateCurrentToEnd(translationX)
            }
            else -> {
                translateCurrentToIdle(translationX)
            }
        }
    }

    fun onSelected() {
        lastTranslationPhase = binding.cdExpense.translationX
        currentState = STATE_IDLE
        halfWidth = width / 2f
    }

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_START_LOCKED = 1
        private const val STATE_END_LOCKED = 2
    }
}