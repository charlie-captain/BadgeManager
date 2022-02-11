package com.charlie.badgelibrary.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Size
import android.util.TypedValue
import android.view.View
import com.charlie.badgelibrary.R
import kotlin.math.roundToInt

/**
 * 红点View，现在只实现了在右上角、中心，未来增加不同位置增加扩展性
 */
class BadgeViewHelper(private val mIBadge: IBadge, context: Context, attrs: AttributeSet?, defaultBadgeGravity: BadgeGravity) {

    var backgroundDrawable: Drawable? = null
        private set
    private lateinit var mBadgePaint: Paint

    /**
     * 徽章背景色
     */
    var badgeBgColor = 0
        private set

    /**
     * 徽章文本的颜色
     */
    var badgeTextColor = 0
        private set

    /**
     * 徽章文本字体大小
     */
    var badgeTextSize = 0
        private set

    /**
     * 徽章背景与宿主控件上下边缘间距离
     */
    private var mBadgeVerticalMargin = 0

    /**
     * 徽章背景与宿主控件左右边缘间距离
     */
    private var mBadgeHorizontalMargin = 0

    /***
     * 徽章文本边缘与徽章背景边缘间的距离
     */
    var badgePadding = 0
        private set

    /**
     * 徽章文本
     */
    var badgeText: String? = null
        private set

    /**
     * 徽章文本所占区域大小
     */
    private lateinit var mBadgeNumberRect: Rect

    /**
     * 是否显示Badge
     */
    var isShowBadge = false
        private set

    /**
     * 徽章在宿主控件中的位置
     */
    private lateinit var mBadgeGravity: BadgeGravity

    /**
     * 整个徽章所占区域
     */
    lateinit var badgeRectF: RectF
        private set

    /***
     * 徽章描边宽度
     */
    private var mBadgeBorderWidth = 0

    /***
     * 徽章描边颜色
     */
    private var mBadgeBorderColor = 0

    private var mBadgeTextMaxSize = 12.dp(mIBadge.getContext())
    private var mBadgeTextMinSize = 8.dp(mIBadge.getContext())

    /**
     * 徽章最大边长
     */
    private var mBadgeMaxSize = 16.dp(mIBadge.getContext())

    /**
     * 小红点大小
     */
    private var mBadgeDotSize = 10.dp(mIBadge.getContext())

    private var isShowDrawable = false

    private val minCircleTextLength = 1

    private fun initDefaultAttrs(context: Context, defaultBadgeGravity: BadgeGravity) {
        mBadgeNumberRect = Rect()
        badgeRectF = RectF()
        badgeBgColor = Color.parseColor("#fff53733")
        badgeTextColor = Color.WHITE
        badgeTextSize = 10.sp(context).toInt()
        mBadgePaint = Paint()
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        mBadgePaint.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        // 设置mBadgeText居中，保证mBadgeText长度为1时，文本也能居中
        mBadgePaint.textAlign = Paint.Align.CENTER
        badgePadding = 4.dp(context)
        mBadgeVerticalMargin = 0
        mBadgeHorizontalMargin = 0
        mBadgeGravity = defaultBadgeGravity
        isShowBadge = false
        badgeText = null
        backgroundDrawable = null
        mBadgeBorderColor = Color.WHITE
    }

    private fun initCustomAttrs(context: Context, attrs: AttributeSet?) {
        attrs ?: return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)
        val N = typedArray.indexCount
        for (i in 0 until N) {
            initCustomAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()
    }

    private fun initCustomAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            R.styleable.BadgeView_badge_bgColor -> {
                badgeBgColor = typedArray.getColor(attr, badgeBgColor)
            }
            R.styleable.BadgeView_badge_textColor -> {
                badgeTextColor = typedArray.getColor(attr, badgeTextColor)
            }
            R.styleable.BadgeView_badge_textSize -> {
                badgeTextSize = typedArray.getDimensionPixelSize(attr, badgeTextSize)
            }
            R.styleable.BadgeView_badge_verticalMargin -> {
                mBadgeVerticalMargin = typedArray.getDimensionPixelSize(attr, mBadgeVerticalMargin)
            }
            R.styleable.BadgeView_badge_horizontalMargin -> {
                mBadgeHorizontalMargin = typedArray.getDimensionPixelSize(attr, mBadgeHorizontalMargin)
            }
            R.styleable.BadgeView_badge_padding -> {
                badgePadding = typedArray.getDimensionPixelSize(attr, badgePadding)
            }
            R.styleable.BadgeView_badge_gravity -> {
                val ordinal = typedArray.getInt(attr, mBadgeGravity.ordinal)
                mBadgeGravity = BadgeGravity.values()[ordinal]
            }
            R.styleable.BadgeView_badge_borderWidth -> {
                mBadgeBorderWidth = typedArray.getDimensionPixelSize(attr, mBadgeBorderWidth)
            }
            R.styleable.BadgeView_badge_borderColor -> {
                mBadgeBorderColor = typedArray.getColor(attr, mBadgeBorderColor)
            }
            R.styleable.BadgeView_badge_drawable -> {
                backgroundDrawable = typedArray.getDrawable(attr)
                isShowDrawable = true
            }
            R.styleable.BadgeView_badge_textMaxSize -> {
                mBadgeTextMaxSize = typedArray.getDimensionPixelSize(attr, mBadgeTextMaxSize)
            }
            R.styleable.BadgeView_badge_textMinSize -> {
                mBadgeTextMinSize = typedArray.getDimensionPixelSize(attr, mBadgeTextMinSize)
            }
            R.styleable.BadgeView_badge_maxSize -> {
                mBadgeMaxSize = typedArray.getDimensionPixelSize(attr, mBadgeMaxSize)
            }
            R.styleable.BadgeView_badge_dotSize -> {
                mBadgeDotSize = typedArray.getDimensionPixelSize(attr, mBadgeDotSize)
            }
        }
    }

    private fun afterInitDefaultAndCustomAttrs() {
        mBadgePaint.textSize = badgeTextSize.toFloat()
    }

    fun setBadgeBgColorInt(badgeBgColor: Int) {
        this.badgeBgColor = badgeBgColor
        mIBadge.postInvalidate()
    }

    fun setBadgeTextColorInt(badgeTextColor: Int) {
        this.badgeTextColor = badgeTextColor
        mIBadge.postInvalidate()
    }

    fun setBadgeTextSizeSp(badgetextSize: Int) {
        if (badgetextSize >= 0) {
            badgeTextSize = badgetextSize.sp(mIBadge.getContext()).toInt()
            mBadgePaint.textSize = badgeTextSize.toFloat()
            mIBadge.postInvalidate()
        }
    }

    fun setBadgeVerticalMarginDp(badgeVerticalMargin: Int) {
        if (badgeVerticalMargin >= 0) {
            mBadgeVerticalMargin = badgeVerticalMargin.dp(mIBadge.getContext())
            mIBadge.postInvalidate()
        }
    }

    fun setBadgeHorizontalMarginDp(badgeHorizontalMargin: Int) {
        if (badgeHorizontalMargin >= 0) {
            mBadgeHorizontalMargin = badgeHorizontalMargin.dp(mIBadge.getContext())
            mIBadge.postInvalidate()
        }
    }

    fun setBadgePaddingDp(badgePadding: Int) {
        if (badgePadding >= 0) {
            this.badgePadding = badgePadding.dp(mIBadge.getContext())
            mIBadge.postInvalidate()
        }
    }

    fun setBadgeGravity(badgeGravity: BadgeGravity?) {
        if (badgeGravity != null) {
            mBadgeGravity = badgeGravity
            mIBadge.postInvalidate()
        }
    }

    fun setBadgeBorderWidthDp(badgeBorderWidthDp: Int) {
        if (badgeBorderWidthDp >= 0) {
            mBadgeBorderWidth = badgeBorderWidthDp.dp(mIBadge.getContext())
            mIBadge.postInvalidate()
        }
    }

    fun setBadgeBorderColorInt(badgeBorderColor: Int) {
        mBadgeBorderColor = badgeBorderColor
        mIBadge.postInvalidate()
    }

    fun drawBadge(canvas: Canvas?) {
        canvas ?: return
        if (!isShowBadge) return

        if (isShowDrawable) {
            drawDrawableBadge(canvas)
        } else {
            drawTextBadge(canvas)
        }
    }

    /**
     * 绘制图像徽章
     *
     * @param canvas
     */
    private fun drawDrawableBadge(canvas: Canvas) {
        val backgroundDrawable = backgroundDrawable ?: return
        val viewWidth = mIBadge.getWidth()
        val viewHeight = mIBadge.getHeight()
        val drawableWidth = backgroundDrawable.intrinsicWidth.toFloat()
        val drawableHeight = backgroundDrawable.intrinsicHeight.toFloat()
        when (mBadgeGravity) {
            BadgeGravity.RightTop -> {
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + drawableHeight
                badgeRectF.left = viewWidth - mBadgeHorizontalMargin - drawableWidth
                badgeRectF.right = badgeRectF.left + drawableWidth
            }
            BadgeGravity.RightCenter -> {
                badgeRectF.top = (viewHeight - drawableHeight) / 2f
                badgeRectF.bottom = badgeRectF.top + drawableHeight
                badgeRectF.left = viewWidth - mBadgeHorizontalMargin - drawableWidth
                badgeRectF.right = badgeRectF.left + drawableWidth
            }
            BadgeGravity.RightBottom -> {
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - drawableHeight
                badgeRectF.left = viewWidth - mBadgeHorizontalMargin - drawableWidth
                badgeRectF.right = badgeRectF.left + drawableWidth
            }
            BadgeGravity.Center -> {
                badgeRectF.left = (viewWidth - drawableWidth) / 2f + mBadgeHorizontalMargin
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.top = (viewHeight - drawableHeight) / 2f + mBadgeVerticalMargin
                badgeRectF.bottom = badgeRectF.top + drawableHeight
            }
            BadgeGravity.CenterTop -> {
                badgeRectF.left = (viewWidth - drawableWidth) / 2f + mBadgeHorizontalMargin
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + drawableHeight
            }
            BadgeGravity.CenterBottom -> {
                badgeRectF.left = (viewWidth - drawableWidth) / 2f + mBadgeHorizontalMargin
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - drawableHeight
            }
            BadgeGravity.LeftTop -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + drawableHeight
            }
            BadgeGravity.LeftCenter -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.top = (viewHeight - drawableHeight) / 2f + mBadgeVerticalMargin
                badgeRectF.bottom = badgeRectF.top + drawableHeight
            }
            BadgeGravity.LeftBottom -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + drawableWidth
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - drawableHeight
            }
        }

        backgroundDrawable.setBounds(badgeRectF.left.toInt(), badgeRectF.top.toInt(), badgeRectF.right.toInt(), badgeRectF.bottom.toInt())
        backgroundDrawable.draw(canvas)
    }

    /**
     * 绘制文字徽章
     *
     * @param canvas
     */
    private fun drawTextBadge(canvas: Canvas) {
        val badgeText = this.badgeText ?: ""

        val viewWidth = mIBadge.getWidth()
        val viewHeight = mIBadge.getHeight()

        resetTextSize(badgeText)
        mBadgePaint.getTextBounds(badgeText, 0, badgeText.length, mBadgeNumberRect)
        val badgeNumberRect = mBadgeNumberRect

        // 计算徽章背景的宽高, 最大边
        val badgeHeight: Int
        val badgeWidth: Int
        when {
            badgeText.isEmpty() -> {
                //小圆点
                badgeHeight = mBadgeDotSize + mBadgeBorderWidth * 2
                badgeWidth = badgeHeight
            }
            badgeText.length <= minCircleTextLength -> {
                //圆形
                badgeHeight = mBadgeMaxSize
                badgeWidth = badgeHeight
            }
            else -> {
                // 其余都是圆角矩形
                badgeHeight = mBadgeMaxSize
                badgeWidth = badgeNumberRect.width() + badgePadding * 2
            }
        }

        when (mBadgeGravity) {
            BadgeGravity.RightTop -> {
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + badgeHeight
                badgeRectF.right = viewWidth - mBadgeHorizontalMargin.toFloat()
                badgeRectF.left = badgeRectF.right - badgeWidth
            }
            BadgeGravity.RightCenter -> {
                badgeRectF.top = mBadgeVerticalMargin.toFloat() + (viewHeight - badgeHeight) / 2f
                badgeRectF.bottom = badgeRectF.top + badgeHeight
                badgeRectF.right = viewWidth - mBadgeHorizontalMargin.toFloat()
                badgeRectF.left = badgeRectF.right - badgeWidth
            }
            BadgeGravity.RightBottom -> {
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - badgeHeight
                badgeRectF.right = viewWidth - mBadgeHorizontalMargin.toFloat()
                badgeRectF.left = badgeRectF.right - badgeWidth
            }
            BadgeGravity.Center -> {
                badgeRectF.left = mBadgeHorizontalMargin + (viewWidth - badgeWidth) / 2f
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat() + (viewHeight - badgeHeight) / 2f
                badgeRectF.bottom = badgeRectF.top + badgeHeight
            }
            BadgeGravity.CenterTop -> {
                badgeRectF.left = mBadgeHorizontalMargin + (viewWidth - badgeWidth) / 2f
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + badgeHeight
            }
            BadgeGravity.CenterBottom -> {
                badgeRectF.left = mBadgeHorizontalMargin + (viewWidth - badgeWidth) / 2f
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - badgeHeight
            }
            BadgeGravity.LeftTop -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat()
                badgeRectF.bottom = badgeRectF.top + badgeHeight
            }
            BadgeGravity.LeftCenter -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.top = mBadgeVerticalMargin.toFloat() + (viewHeight - badgeHeight) / 2f
                badgeRectF.bottom = badgeRectF.top + badgeHeight
            }
            BadgeGravity.LeftBottom -> {
                badgeRectF.left = mBadgeHorizontalMargin.toFloat()
                badgeRectF.right = badgeRectF.left + badgeWidth
                badgeRectF.bottom = viewHeight - mBadgeVerticalMargin.toFloat()
                badgeRectF.top = badgeRectF.bottom - badgeHeight
            }
        }

        if (mBadgeBorderWidth > 0) { // 设置徽章边框景色
            mBadgePaint.color = mBadgeBorderColor
            // 绘制徽章边框背景
            canvas.drawRoundRect(badgeRectF, badgeHeight / 2f, badgeHeight / 2f, mBadgePaint)
            // 设置徽章背景色
            mBadgePaint.color = badgeBgColor
            // 绘制徽章背景
            canvas.drawRoundRect(
                RectF(
                    badgeRectF.left + mBadgeBorderWidth,
                    badgeRectF.top + mBadgeBorderWidth,
                    badgeRectF.right - mBadgeBorderWidth,
                    badgeRectF.bottom - mBadgeBorderWidth
                ), (badgeHeight - 2 * mBadgeBorderWidth) / 2f, (badgeHeight - 2 * mBadgeBorderWidth) / 2f, mBadgePaint
            )
        } else { // 设置徽章背景色
            mBadgePaint.color = badgeBgColor
            // 绘制徽章背景
            canvas.drawRoundRect(badgeRectF, badgeHeight / 2f, badgeHeight / 2f, mBadgePaint)
        }
        if (badgeText.isNotEmpty()) {
            // 设置徽章文本颜色
            mBadgePaint.color = badgeTextColor

            // initDefaultAttrs方法中设置了mBadgeText居中，此处的x为徽章背景的中心点y
            val x = badgeRectF.left + badgeWidth / 2
            val y = (badgeRectF.bottom - badgeHeight / 2) - mBadgePaint.ascent() / 2 - mBadgePaint.descent() / 2
            // 绘制徽章文本
            canvas.drawText(badgeText, x, y, mBadgePaint)
        }
    }

    /**
     * 通过文字长度设置画笔的文字大小
     */
    private fun resetTextSize(badgeText: String) {
        if (badgeText.isEmpty() || badgeText.length == 1) {
            mBadgePaint.textSize = mBadgeTextMaxSize.toFloat()
        } else {
            mBadgePaint.textSize = mBadgeTextMinSize.toFloat()
        }
    }

    fun showCirclePointBadge() {
        showTextBadge(null)
    }

    fun showTextBadge(badgeText: String?) {
        isShowDrawable = false
        this.badgeText = badgeText
        isShowBadge = true
        mIBadge.postInvalidate()
    }

    fun hideBadge() {
        isShowBadge = false
        mIBadge.postInvalidate()
    }

    fun showDrawable(drawable: Drawable?) {
        this.backgroundDrawable = drawable
        isShowDrawable = true
        isShowBadge = true
        mIBadge.postInvalidate()
    }

    fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int): Size {
        val defaultWidth = View.getDefaultSize(0, widthMeasureSpec)
        val defaultHeight = View.getDefaultSize(0, heightMeasureSpec)

        if (defaultWidth > 0 && defaultHeight > 0) {
            //精确值直接返回
            return Size(defaultWidth, defaultHeight)
        }

        //没有在xml定义大小会走下面默认大小
        //会丢失gravity,需要在xml设置位置
        when {
            backgroundDrawable != null -> {
                //大小等于
                val width = (backgroundDrawable?.intrinsicWidth ?: defaultWidth) + mBadgeBorderWidth * 2 + mBadgeHorizontalMargin * 2
                val height = (backgroundDrawable?.intrinsicHeight ?: defaultHeight) + +mBadgeBorderWidth * 2 + mBadgeHorizontalMargin * 2
                return Size(width, height)
            }
            badgeText?.isNotEmpty() == true -> {
                val badgeText = badgeText!!
                val length = badgeText.length

                val defaultSize = mBadgeMaxSize
                return if (length <= minCircleTextLength) {
                    Size(defaultSize, defaultSize)
                } else {
                    resetTextSize(badgeText)
                    mBadgePaint.getTextBounds(badgeText, 0, badgeText.length, mBadgeNumberRect)
                    val width = mBadgeNumberRect.width() + badgePadding * 2 + mBadgeBorderWidth * 2
                    val height = mBadgeMaxSize
                    Size(width, height)
                }
            }
            else -> {
                //默认 + 描边宽度
                val size = mBadgeDotSize + mBadgeBorderWidth * 2
                return Size(size, size)
            }
        }
    }

    val rootView: View?
        get() = mIBadge.getRootView()


    init {
        initDefaultAttrs(context, defaultBadgeGravity)
        initCustomAttrs(context, attrs)
        afterInitDefaultAndCustomAttrs()
    }

    companion object {
        private const val TAG = "BadgeViewHelper"
    }
}


fun Int.dp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
).roundToInt()

fun Int.dpFloat(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Float.dp(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
).roundToInt()

fun Float.dpFloat(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Int.sp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Float.sp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics
)


enum class BadgeGravity {
    LeftTop,
    LeftCenter,
    LeftBottom,
    CenterTop,
    Center,
    CenterBottom,
    RightTop,
    RightCenter,
    RightBottom
}
