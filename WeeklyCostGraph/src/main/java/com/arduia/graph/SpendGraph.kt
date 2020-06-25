package com.arduia.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.arduia.core.extension.px
import com.arduia.core.extension.pxS

class SpendGraph @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null,
                                           defStyleAttrs: Int = 0): View(context, attrs, defStyleAttrs){

    //Whole Custom View
    private val viewF by lazy { createViewFrame() }

    //Internal Graph Padding between Whole view and Graph Canvas Frame
    private var graphPaddingLeft = 0f
    private var graphPaddingTop = 0f
    private var graphPaddingRight = 0f
    private var graphPaddingBottom= 0f

    //Week Name Height of the graph
    private var dayNameHeight = 0f

    //Drawable Canvas for Graph
    private val canvasF by lazy { createCanvasFrame() }

    //Drawable Canvas Frame for Line Graph
    private val lineCanvasF by lazy { createLineCanvasFrame() }

    //Drawable Canvas Frame for Day Name
    private val dayNameCanvasF by lazy { createDayNameFrame() }

    private val dayPaint by lazy { createDayPaint() }
    private val linePointPaint by lazy { createLinePointPaint()}
    private val linePaint by lazy { createLinePaint() }
    private val labelPaint by lazy { createLabelPaint() }

    private val dayNameProvider:DayNameProvider by lazy { DayNameProviderImpl(context) }

    /**
     * Interface Fields
     */
    var spendPoints = emptyList<SpendPoint>()
    set(value) { field = value; refreshView() }

    /**
     * Framework Callback Methods
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawDayNames()
            it.drawPointLines()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initConfig()
    }

    /**
     * Draw Methods
     */
    private fun Canvas.drawPointLines(){

        val linePath = Path()

        //just store in method reference
        val list = spendPoints

        // high of line graph
        val heightF = lineCanvasF.height()

        //bottom point of graph
        val bottomF = lineCanvasF.bottom

        list.forEachIndexed { i, point ->

            val xPosition = getDayPositionX(point.day)

            //ratio of height for rate
            val yPosition = bottomF - (point.rate * heightF)

            //draw the point
            drawLinePoint(xPosition, yPosition)

            if(i == 0){
                //move to first position
                linePath.moveTo(xPosition, yPosition)
            }else{
                //line to each position
                linePath.lineTo(xPosition, yPosition)
            }
        }

        //draw graph line on line Path
        drawPath(linePath, linePaint)

        //get highest Point
        val highestPoint  = list.maxBy { it.rate }

        //if has draw vertical
        highestPoint?.let {
            drawHighestVertical(it)
        }
    }

    private fun Canvas.drawHighestVertical(point: SpendPoint){

        //common X for  position X
        val commonX = getDayPositionX(point.day)

        //start from the canvas bottom
        val startY = lineCanvasF.bottom

        //ent to canvas height rate of point
        val endY = lineCanvasF.bottom - (point.rate * lineCanvasF.height())

        val labelTextSize = labelPaint.textSize

        val dotedPath = Path()

        //move to bottom
        dotedPath.moveTo(commonX, startY)

        //place doted points on path
        for(position in startY.toInt() downTo endY.toInt() step 5){
            when(position % 3) {
                1 -> dotedPath.moveTo(commonX, position.toFloat())
                2 -> dotedPath.lineTo(commonX, position.toFloat())
            }
        }

        //draw doted points
        drawPath(dotedPath, linePointPaint)

        //position of label
        val labelPositionX = commonX + (labelTextSize * 2)
        val labelPositionY = endY - (labelTextSize * 2)

        //space between sides
        val betweenTopY = labelPositionY - lineCanvasF.top
        val betweenRightX = labelPositionX - lineCanvasF.right

        //no space between label base and canvas top, finish
        if(betweenTopY < labelTextSize) return

        //no space between label and canvas right, finish
        if(betweenRightX < (labelTextSize * 3)) return

        drawText("${(point.rate * 100).toInt()} %", labelPositionX, labelPositionY, labelPaint)
    }


    //draw point
    private fun Canvas.drawLinePoint(x:Float, y:Float){
        drawCircle(x, y, px(2.5f), linePointPaint)
    }

    //draw day names on each pattern
    private fun Canvas.drawDayNames(){

        val totalDays = 7
        val dayPositionY = dayNameCanvasF.centerY() + (dayPaint.textSize/2)

        for(day in 1..totalDays){
            val dayPositionX = getDayPositionX(day)
            drawDayName(dayNameProvider.getName(day), dayPositionX, dayPositionY)
        }

    }

    //draw a day name
    private fun Canvas.drawDayName(name:String, x:Float, y:Float){

        drawText(name, x, y, dayPaint)
    }

    private fun getDayPositionX(day:Int, totalDay:Int = 7):Float{

        val canvasLeft = dayNameCanvasF.left
        val segmentWidth = dayNameCanvasF.width() / totalDay
        val segmentHalf = segmentWidth / 2

        return canvasLeft + ( (day * segmentWidth) - segmentHalf )
    }

    /**
     * Initialization Methods
     */
    private fun initConfig(){

        dayNameHeight = px(30f)

        val graphPadding = px(16f)
        graphPaddingLeft = graphPadding
        graphPaddingTop = 0f
        graphPaddingRight = graphPadding
        graphPaddingBottom = dayNameHeight
    }

    private fun refreshView(){
        invalidate()
    }


    /**
     * Creation Methods
     */
    private fun createDayPaint() =
        Paint().apply {
        color = Color.WHITE
        textSize = pxS(12f)
        textAlign = Paint.Align.CENTER
    }

    private fun createLinePointPaint() =
        Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = px(1f)
    }

    private fun createLinePaint() =
        Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = px(1f)
    }

    private fun createLabelPaint() =
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            textSize = pxS(15f)
            textAlign = Paint.Align.LEFT
        }


    private fun createDayNameFrame() =
        RectF(
            canvasF.left + graphPaddingLeft,
            canvasF.bottom - dayNameHeight,
            canvasF.right - graphPaddingRight,
            canvasF.bottom
        )

    //Above the Day Name Text
    private fun createLineCanvasFrame() =
        RectF(
            canvasF.left + graphPaddingLeft,
            canvasF.top + graphPaddingTop,
            canvasF.right - graphPaddingRight,
             canvasF.bottom - graphPaddingBottom  )

    //Trim the padding between Graph Canvas and Whole View
    private fun createCanvasFrame() =
        RectF(
            viewF.left + paddingLeft,
            viewF.top + paddingTop,
            viewF.right - paddingRight,
            viewF.bottom - paddingBottom
        )

    //Whole view Frame
    private fun createViewFrame() =
        RectF(0f, 0f, width.toFloat(), height.toFloat())

}
