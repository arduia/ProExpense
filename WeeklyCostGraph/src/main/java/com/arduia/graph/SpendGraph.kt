package com.arduia.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.view.marginBottom
import com.arduia.core.extension.px
import com.arduia.core.extension.pxS

class SpendGraph @JvmOverloads constructor(context:Context,
                                           attrs:AttributeSet?=null,
                                           defStyleAttrs:Int=0):View(context,attrs,defStyleAttrs){

    //whole Custom View
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

    private val dayPaint by lazy { Paint().apply {
        color = Color.WHITE
        textSize = pxS(12f)
        textAlign = Paint.Align.CENTER
    }}

    private val linePointPaint by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = px(1f)
        }
    }

    private val linePaint by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = px(1f)
        }
    }

    private val labelText by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            textSize = pxS(15f)
            textAlign = Paint.Align.LEFT
        }
    }
    private val date:Int = 9

    private val dayNameProvider:DayNameProvider by lazy { DayNameProviderImpl(context) }

    /**
     * Interface Fields
     */

    var spendPoints:List<SpendPoint> = emptyList()
    set(value) {
        field = value
        refreshView()
    }

    /**
     * Framework Callback Methods
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {

            //test frame
//            it.drawRect(canvasF, Paint().apply {
//                color = Color.BLACK
//                style = Paint.Style.STROKE
//                strokeWidth =  px(1f)
//            })
//
//            //test graph line frame
//            it.drawRect(lineCanvasF,Paint().apply {
//                color = Color.GREEN
//                style = Paint.Style.STROKE
//                strokeWidth = px(1f)
//            })
//
//            //test day name frame
//            it.drawRect(dayNameCanvasF,Paint().apply {
//                color = Color.GREEN
//                style = Paint.Style.STROKE
//                strokeWidth = px(1f)
//            })

            it.drawDayNames()

            it.drawPointLines(spendPoints)

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initConfig()
    }

    /**
     * Draw Methods
     */

    private fun Canvas.drawPointLines(list:List<SpendPoint>){
        val linePath = Path()
        val heightF = lineCanvasF.height()
        val bottomF = lineCanvasF.bottom

        list.forEachIndexed { i, point ->

            val xPosition = getDayX(point.day)
            val yPosition = bottomF - (point.rate * heightF)

            drawLinePoint(xPosition,yPosition)

            if(i == 0){
                linePath.moveTo(xPosition,yPosition)
            }else{
                linePath.lineTo(xPosition,yPosition)
            }

        }
        drawPath(linePath,linePaint)
        val highestPoint  = list.maxBy { it.rate }
        highestPoint?.let {
            drawHighestVertical(it)
        }
    }

    private fun Canvas.drawHighestVertical(point:SpendPoint){
        val commonX = getDayX(point.day)
        val startY = lineCanvasF.bottom
        val endY = lineCanvasF.bottom - (point.rate * lineCanvasF.height())
        val path = Path()
        path.moveTo(commonX,startY)
        for(position in startY.toInt() downTo endY.toInt() step 5){
            when(position%3) {
                1 -> path.moveTo(commonX, position.toFloat())
                2 -> path.lineTo(commonX, position.toFloat())
            }
        }
        drawPath(path,linePointPaint)
        drawText("${(point.rate * 100).toInt()} %",commonX + (linePaint.textSize * 2), endY -  (linePaint.textSize * 2),labelText)
    }


    private fun Canvas.drawLinePoint(x:Float, y:Float){
        drawCircle(x,y,px(2.5f),linePointPaint)
    }

    private fun Canvas.drawDayNames(){

        val totalDays = 7
        val dayPositionY = dayNameCanvasF.centerY() + (dayPaint.textSize/2)

        for(day in 1..totalDays){
            val dayPositionX = getDayX(day)
            drawDayName(dayNameProvider.getName(day), dayPositionX, dayPositionY)
        }

    }

    private fun Canvas.drawDayName(name:String,x:Float,y:Float){
        drawText(name, x, y, dayPaint)
    }

    private fun getDayX(day:Int, totalDay:Int=7):Float{
        val frameLeft = dayNameCanvasF.left
        val segmentWidth = dayNameCanvasF.width()/totalDay
        val segmentHalf = segmentWidth/2

        return frameLeft + ((day * segmentWidth) - segmentHalf)
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

    private fun createDayNameFrame() =
        RectF(
            canvasF.left + graphPaddingLeft,
            canvasF.bottom - dayNameHeight,
            canvasF.right - graphPaddingRight,
            canvasF.bottom
        )

    private fun createLineCanvasFrame() =
        //Above the Day Name Text
        RectF(
            canvasF.left + graphPaddingLeft,
            canvasF.top + graphPaddingTop,
            canvasF.right - graphPaddingRight,
             canvasF.bottom - graphPaddingBottom  )

    private fun createCanvasFrame() =
        //Trim the padding between Graph Canvas and Whole View
        RectF(
            viewF.left + paddingLeft,
            viewF.top + paddingTop,
            viewF.right - paddingRight,
            viewF.bottom - paddingBottom
        )

    private fun createViewFrame() =
        RectF(0f,0f,width.toFloat(),height.toFloat())

}
