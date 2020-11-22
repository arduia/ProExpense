package com.arduia.graph

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.View
import com.arduia.core.extension.px
import com.arduia.core.extension.pxS
import java.lang.IllegalArgumentException

class SpendGraph @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : View(context, attrs, defStyleAttrs),
    GraphView {

    //Whole Custom View
    private val viewF by lazy { createViewFrame() }

    //Internal Graph Padding between Whole view and Graph Canvas Frame
    private var graphPaddingLeft = 0f
    private var graphPaddingTop = 0f
    private var graphPaddingRight = 0f
    private var graphPaddingBottom = 0f
    private var pointCircleRadius = px(1.5f)

    //Week Name Height of the graph
    private var dayNameHeight = 0f

    //Drawable Canvas for Graph
    private val canvasF by lazy { createCanvasFrame() }

    //Drawable Canvas Frame for Line Graph
    private val lineCanvasF by lazy { createLineCanvasFrame() }

    //Drawable Canvas Frame for Day Name
    private val dayNameCanvasF by lazy { createDayNameFrame() }

    private val dayPaint by lazy { createDayPaint() }
    private val linePointPaint by lazy { createLinePointPaint() }
    private val linePaint by lazy { createLinePaint() }
    private val labelPaint by lazy { createLabelPaint() }

    var dayNameProvider: DayNameProvider = DayNameProviderImpl(context)

    /**
     * Interface Fields
     */

    var adapter: Adapter? = null
        set(value) {
            field = value
            adapter?.graphView = this
        }

    /**
     * Framework Callback Methods
     */
    init {

        val a = context.obtainStyledAttributes(attrs,R.styleable.SpendGraph,defStyleAttrs, 0)
        val graphColor = a.getColor(R.styleable.SpendGraph_graph_color, Color.GREEN)
        val dayColor = a.getColor(R.styleable.SpendGraph_day_color, Color.GREEN)
        a.recycle()

        dayPaint.color = dayColor
        linePaint.color = graphColor
        labelPaint.color = graphColor
        linePointPaint.color = dayColor


    }

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
    private fun Canvas.drawPointLines() {

        val linePath = Path()

        //just store in method reference
        val list = adapter?.getSpendPoints()

        // high of line graph
        val heightF = lineCanvasF.height()

        //bottom point of graph
        val bottomF = lineCanvasF.bottom

        var isPointMoved = false

        list?.forEachIndexed { i, point ->

            //Not Exist Rate
            if (point.rate < 0) return@forEachIndexed

            val xPosition = getDayPositionX(point.day)

            //ratio of height for rate
            val yPosition = bottomF - (point.rate * heightF)

            //draw the point
            drawLinePoint(xPosition, yPosition)

            if (isPointMoved.not()) {
                //move to first position
                linePath.moveTo(xPosition, yPosition)
                isPointMoved = true

            } else {
                //line to each position
                linePath.lineTo(xPosition, yPosition)
            }
        }

        //draw graph line on line Path
        drawPath(linePath, linePaint)

        //get highest Point
        val highestPoint = list?.maxBy { it.rate }

        //if has draw vertical
        highestPoint?.let {
            drawHighestVertical(it)
        }
    }

    private fun Canvas.drawHighestVertical(point: SpendPoint) {

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
        for (position in startY.toInt() downTo endY.toInt() step 5) {
            when (position % 3) {
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
        if (betweenTopY < labelTextSize) return

        //no space between label and canvas right, finish
        if (betweenRightX < (labelTextSize * 3)) return

        drawText("${(point.rate * 100).toInt()} %", labelPositionX, labelPositionY, labelPaint)
    }


    //draw point
    private fun Canvas.drawLinePoint(x: Float, y: Float) {
        drawCircle(x, y, pointCircleRadius, linePointPaint)
    }

    //draw day names on each pattern
    private fun Canvas.drawDayNames() {

        val totalDays = 7
        val dayPositionY = dayNameCanvasF.centerY() + (dayPaint.textSize / 2)

        for (day in 1..totalDays) {
            val dayPositionX = getDayPositionX(day)
            drawDayName(dayNameProvider.getName(day), dayPositionX, dayPositionY)
        }

    }

    //draw a day name
    private fun Canvas.drawDayName(name: String, x: Float, y: Float) {

        drawText(name, x, y, dayPaint)
    }

    private fun getDayPositionX(day: Int, totalDay: Int = 7): Float {

        val segmentWidth = dayNameCanvasF.width() / totalDay
        val segmentHalf = segmentWidth / 2

        return when (layoutDirection) {

            LayoutDirection.RTL -> {
                val canvasRight = dayNameCanvasF.right
                canvasRight - ((day * segmentWidth) - segmentHalf)
            }

            else -> {
                val canvasLeft = dayNameCanvasF.left
                canvasLeft + ((day * segmentWidth) - segmentHalf)
            }
        }

    }

    /**
     * Initialization Methods
     */
    private fun initConfig() {

        dayNameHeight = px(30f)

        val graphPadding = px(16f)
        graphPaddingLeft = graphPadding
        graphPaddingTop = 0f
        graphPaddingRight = graphPadding
        graphPaddingBottom = dayNameHeight
    }

    override fun refreshView() {
        invalidate()
    }


    /**
     * Creation Methods
     */
    private fun createDayPaint() =
        Paint().apply {
            color = Color.BLUE
            textSize = pxS(12f)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            strokeWidth = pxS(1f)
        }

    private fun createLinePointPaint() =
        Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = px(1f)
            isAntiAlias = true
        }

    private fun createLinePaint() =
        Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = px(1f)
            isAntiAlias = true
        }

    private fun createLabelPaint() =
        Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            textSize = pxS(15f)
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
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
            canvasF.bottom - graphPaddingBottom
        )

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

    abstract class Adapter {

        private val lists = mutableListOf<SpendPoint>()

        var graphView: GraphView? = null

        internal fun getSpendPoints() = lists

        abstract fun getRate(day: Int): Int

        fun notifyDataChanged() {
            fetchData()
            graphView?.refreshView()
        }

        fun notifyPointChanged(day: Int) {
            updatePointData(day)
            graphView?.refreshView()
        }

        private fun updatePointData(day: Int) {

            //Just update all data
            if (lists.size < 7) {
                fetchData()
                return
            }

            lists[day] = SpendPoint(day, getCalculatedRate(day))
        }

        private fun fetchData() {
            //Clear all data to add fresh data
            lists.clear()
            (1..7).forEach {
                lists.add(SpendPoint(it, getCalculatedRate(it)))
            }
        }

        private fun getCalculatedRate(day: Int): Float {
            //GetRaw Rates
            val rate = getRate(day)

            if (rate !in 0..100) return -1f

            return rate % 101 / 110f
        }

    }

}

interface GraphView {
    fun refreshView()
}
