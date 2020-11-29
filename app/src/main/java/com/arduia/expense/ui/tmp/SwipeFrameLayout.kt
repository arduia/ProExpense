package com.arduia.expense.ui.tmp

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.arduia.core.extension.px
import com.arduia.expense.R
import com.arduia.expense.databinding.ItemExpenseLogBinding
import com.arduia.expense.ui.vto.ExpenseVto
import timber.log.Timber
import kotlin.math.abs

class SwipeFrameLayout @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(ctx, attrs, defStyleRes) {

    private val binding by lazy { ItemExpenseLogBinding.bind(this) }

    private var minEndMargin = ctx.px(40f)
    private var minStartMargin = ctx.px(30f)

    private var lockEndMargin = ctx.px(80f)
    private var lockStartMargin = ctx.px(46f)

    private var currentTranslateAnimation: ValueAnimator? = null

    private val swipeDxFactor = 0.4F
    private val translateInterpolator = LinearOutSlowInInterpolator()

    private var lastTranslationPhase = 0f
    private var idleToEndDuration = 500L

    private var currentState = STATE_IDLE
    private var currentDirection = DIRECTION_IDLE
    private var isSwipeTouchActive = false
    private var halfWidth = 0F

    private var selectedChangedListener: OnSelectedChangedListener? = null
    private var prepareChangedListener: OnPrepareChangedListener? = null

    fun bindData(data: ExpenseVto) {
        with(binding) {
            tvAmount.text = data.amount
            tvCurrencySymbol.text = data.currencySymbol
            tvDate.text = data.date
            tvName.text = data.name
            imvCategory.setImageResource(data.category)
        }
        binding.cdExpense.setOnLongClickListener {
            onSelected()
            invertEndLock()
            return@setOnLongClickListener true
        }
    }

    private fun invertEndLock() {
        if(currentState == STATE_START_LOCKED) return

        if (currentState == STATE_IDLE) {
            translateIdleToEnd()
        } else if (currentState == STATE_END_LOCKED) {
            translateEndToIdle()
        }
    }

    private fun translateIdleToEnd() {
        if (currentState != STATE_IDLE) return
        binding.cdExpense.isClickable = false
        currentTranslateAnimation?.cancel()
        val startPosition = 0f
        val endPosition = -lockEndMargin
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = idleToEndDuration
            interpolator = translateInterpolator
            startDelay = 100L
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == endPosition) {
                    currentState = STATE_END_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun calculateDuration(start: Float, end: Float):Long =
       ( abs(abs(start) - abs(end)) * 0.4f ).toLong()

    private fun translateIdleToStart() {
        if (currentState != STATE_IDLE) return
        binding.cdExpense.isClickable = false
        currentTranslateAnimation?.cancel()
        val startPosition = 0f
        val endPosition = lockStartMargin
        val duration = calculateDuration(startPosition, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = duration
            interpolator = translateInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == endPosition) {
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
            interpolator = translateInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == 0f) {
                    if(currentState == STATE_START_LOCKED){
                        selectedChangedListener?.onSelectedChanged(false)
                    }
                    if(currentState == STATE_END_LOCKED){
                        prepareChangedListener?.onPreparedChanged(false)
                    }
                    currentState = STATE_IDLE

                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateCurrentToStart(currentX: Float) {
        currentTranslateAnimation?.cancel()

        val endPosition = lockStartMargin
        val duration = calculateDuration(currentX, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(currentX, endPosition).apply {
            this.duration = duration
            interpolator = translateInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == endPosition) {
                    if (currentState == STATE_IDLE){
                        selectedChangedListener?.onSelectedChanged(true)
                    }
                    currentState = STATE_START_LOCKED
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    private fun translateCurrentToEnd(currentX: Float) {
        currentTranslateAnimation?.cancel()
        val startPosition = currentX
        val endPosition = -lockEndMargin
        val duration = calculateDuration(startPosition, endPosition)
        currentTranslateAnimation = ValueAnimator.ofFloat(startPosition, endPosition).apply {
            this.duration = duration
            interpolator = translateInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == endPosition) {
                    prepareChangedListener?.onPreparedChanged(true)
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
        val start = -lockEndMargin
        val end = 0f

        currentTranslateAnimation = ValueAnimator.ofFloat(start, end).apply {
            this.duration = idleToEndDuration
            interpolator = translateInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                translateView(value)
                if (value == end) {
                    currentState = STATE_IDLE
                    binding.cdExpense.isClickable = true
                }
            }
        }
        currentTranslateAnimation?.start()
    }

    fun onSwipe(isOnTouch: Boolean, dx: Float) {

        if (isOnTouch && (binding.cdExpense.translationX <= halfWidth)) {
            translateView((dx * swipeDxFactor) + lastTranslationPhase)
        }

        if (isSwipeTouchActive and isOnTouch.not()) {
            onTouchReleased(dx, binding.cdExpense.translationX)
        }

        isSwipeTouchActive = isOnTouch
    }

    private fun translateView(translationX: Float) {
        binding.cdExpense.translationX = translationX
        when {
            translationX > 10 -> {
                onDirectionChanged(DIRECTION_START_TO_END)
            }
            translationX < -10 -> {
                onDirectionChanged(DIRECTION_END_TO_START)
            }
            else -> onDirectionChanged(DIRECTION_IDLE)
        }
    }

    private fun onDirectionChanged(direction: Int) {
        if (this.currentDirection == direction) return
        this.currentDirection = direction
        when (currentDirection) {
            DIRECTION_START_TO_END -> {
                binding.flBack.setBackgroundResource(R.color.green_400)
            }
            DIRECTION_END_TO_START -> {
                binding.flBack.setBackgroundResource(R.color.red_400)
            }
        }
    }


    private fun onTouchReleased(dx: Float, translationX: Float) {
        when {
            (translationX >= minStartMargin) && (currentState == STATE_IDLE) -> {
                translateCurrentToStart(translationX)
            }
            (translationX <= -minEndMargin) && (currentState == STATE_IDLE) -> {
                translateCurrentToEnd(translationX)
            }
            else -> {
                translateCurrentToIdle(translationX)
            }
        }
    }

    fun onSelected() {
        lastTranslationPhase = binding.cdExpense.translationX
        currentState = when (lastTranslationPhase) {
            lockStartMargin -> STATE_START_LOCKED
            -lockEndMargin -> STATE_END_LOCKED
            else -> STATE_IDLE
        }
        halfWidth = width / 2f
    }

    fun setOnSelectedChangedListener(changedListener: OnSelectedChangedListener){
        this.selectedChangedListener = changedListener
    }

    fun setOnPrepareChangedListener(listener: OnPrepareChangedListener){
        this.prepareChangedListener = listener
    }

    fun interface OnSelectedChangedListener{
        fun onSelectedChanged(isSelected: Boolean)
    }

    fun interface OnPrepareChangedListener{
        fun onPreparedChanged(isPrepared: Boolean)
    }

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_START_LOCKED = 1
        private const val STATE_END_LOCKED = 2

        private const val DIRECTION_IDLE = 0
        private const val DIRECTION_START_TO_END = 1
        private const val DIRECTION_END_TO_START = 2
    }
}