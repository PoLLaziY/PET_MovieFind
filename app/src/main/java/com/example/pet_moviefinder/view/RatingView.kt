package com.example.pet_moviefinder.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.pet_moviefinder.R
import kotlin.math.min

class RatingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    //атрибуты
    var arcWidth: Float = 12f
    var numberSize: Float = 40f
    var numberWidth: Float = 10f
    var rating: Float = 50f

    //параметры
    var centerX: Float = 0f
    var centerY: Float = 0f
    var radius: Float = 20f
    var foneColor: Int = Color.DKGRAY

    //статические объекты
    var staticBitmap: Bitmap? = null
    var staticCanvas: Canvas? = null

    //Paints
    private lateinit var fonePaint: Paint
    private lateinit var arcPaint: Paint
    private lateinit var textPaint: Paint

    init {
        //получение атрибутов
        val attr = context.theme.obtainStyledAttributes(attributeSet, R.styleable.RatingView, 0, 0)
        try {
            numberWidth = attr.getFloat(R.styleable.RatingView_numberWidth, numberWidth)
            numberSize = attr.getFloat(R.styleable.RatingView_numberSize, numberSize)
            arcWidth = attr.getFloat(R.styleable.RatingView_arcWidth, arcWidth)
            rating = attr.getFloat(R.styleable.RatingView_rating, rating)
        } finally {
            attr.recycle()
        }
        //инициализация красок
        initialPaints()
    }

    //инициализация красок
    private fun initialPaints() {
        fonePaint = Paint().apply {
            color = foneColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        arcPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = arcWidth
            isAntiAlias = true
        }

        textPaint = Paint().apply {
            textSize = numberSize
            strokeWidth = numberWidth
            isAntiAlias = true
        }
    }

    //рассчёт размеров окна
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //получение режима площади
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //получение возможных размеров
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //получение и установка реальных размеров
        val width = getSize(widthSize, widthMode)
        val height = getSize(heightSize, heightMode)
        setMeasuredDimension(width, height)

        //установка координат и радиуса будующей окружности
        val minSize = min(width, height)
        centerX = width / 2f
        centerY = height / 2f
        radius = minSize / 2f
    }

    //получить реальный размер из режима площади и возможных размеров
    fun getSize(size: Int, mode: Int): Int {
        when (mode) {
            MeasureSpec.EXACTLY -> return size
            else -> return 100
        }
    }

    //отрисовка окна
    override fun onDraw(canvas: Canvas?) {
        //создать Bitmap из статических объектов, если её нет
        if (staticCanvas == null) {
            drawStaticBitmap()
        }

        //отрисовка элементов окна
        if (canvas != null) {
            //статических объектов из Bitmap
            canvas.drawBitmap(staticBitmap!!, 0f, 0f, null)

            //дуги рейтинга
            canvas.drawArc(
                centerX - radius + arcWidth / 2,
                centerY - radius + arcWidth / 2,
                centerX + radius - arcWidth / 2,
                centerY + radius - arcWidth / 2,
                -90f,
                getSweepAngle(rating),
                false,
                arcPaint.apply { color = getRatingColor(rating) }
            )

            //цифры рейтинга
            canvas.drawNumber(rating)
        }
    }

    //получение конечного угла для дуги прогресса
    private fun getSweepAngle(rating: Float): Float =
        -360f * rating / 100

    //отрисовать статик-объекты в Bitmap
    private fun drawStaticBitmap() {
        staticBitmap = Bitmap.createBitmap(
            (centerX * 2).toInt(),
            (centerY * 2).toInt(),
            Bitmap.Config.ARGB_8888
        )
        staticCanvas = Canvas(staticBitmap!!)
        staticCanvas!!.drawCircle(centerX, centerY, radius, fonePaint)
    }

    //нарисовать цифру в центре Canvas
    private fun Canvas.drawNumber(number: Float) {
        val message = String.format("%.1f", number / 10f)
        val widths = FloatArray(message.length)
        textPaint.getTextWidths(message, widths)
        var advance = 0f
        widths.forEach { advance += it }
        this.drawText(message,
            centerX - advance / 2, centerY + numberSize / 3,
            textPaint.apply { color = getRatingColor(rating) })
    }

    //получить цвет объектов, зависящих от величины рейтинга
    private fun getRatingColor(rating: Float): Int =
        when (rating) {
            in 25f..49f -> Color.RED
            in 50f..74f -> Color.YELLOW
            in 51f..100f -> Color.GREEN
            else -> Color.GRAY
        }
}