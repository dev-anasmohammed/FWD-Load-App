package com.devanasmohammed.fwd_load_app

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = ""
    private var buttonBackgroundColor = R.attr.buttonBackgroundColor
    private var progress: Float = 0f
    private val textRect = Rect()

    private val archColor = Color.parseColor("#F9A825")
    private val loadingBackgroundColor = "#004349"
    private val completedBackgroundColor = "#07C2AA"

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed)
    { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {
                loadingState()
            }
            ButtonState.Completed -> {
                completeState()
            }
        }
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            buttonText = getString(R.styleable.LoadingButton_text).toString()
            buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
        }

        buttonState = ButtonState.Completed

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        canvas.drawColor(buttonBackgroundColor)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
        }
        canvas.drawRoundRect(
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            10.0f,
            10.0f,
            paint
        )

        if (buttonState == ButtonState.Loading) {
            var progressVal = progress * measuredWidth.toFloat()

            val progressBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            }
            canvas.drawRoundRect(
                0f,
                0f,
                progressVal,
                backgroundHeight,
                10.0f,
                10.0f,
                progressBackground
            )

            val arcDiameter = 10.0f * 5
            val progressArc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = archColor
            }

            progressVal = progress * 360f
            canvas.drawArc(
                paddingStart + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                150f,
                150f,
                0f,
                progressVal,
                true,
                progressArc
            )
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 50.0f
            color = Color.WHITE
        }
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        canvas.drawText(
            buttonText,
            measuredWidth.toFloat() / 2,
            measuredHeight.toFloat() / 2 - textRect.centerY(),
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val min: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(min, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun loadingState() {
        //set text
        this.buttonText = "We are downloading"
        //set background
        buttonBackgroundColor = Color.parseColor(loadingBackgroundColor)
        invalidate()
        requestLayout()

        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            progress = valueAnimator.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 5000
        valueAnimator.start()
        //disable button until finishes
        this.isEnabled = false
    }

    private fun completeState() {
        //set text
        this.buttonText = "Downloaded"
        //set background
        buttonBackgroundColor = Color.parseColor(completedBackgroundColor)
        invalidate()
        requestLayout()
        //cancel animation
        valueAnimator.cancel()
        //reset progress
        progress = 0f
        //enable button again
        this.isEnabled = true
    }

}