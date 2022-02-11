package com.charlie.badgelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.charlie.badgelibrary.BadgeManager
import com.charlie.badgelibrary.BadgeNode

/**
 * 可单独使用在xml的红点View
 * 尽量设置精确大小，或者用FrameLayout
 */
class BadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr), IBadge {

    private val mBadgeViewHelper: BadgeViewHelper =
        BadgeViewHelper(this, context, attrs, BadgeGravity.RightTop)


    fun bindNode(key: String) {
        updateNodeUI(key)
        val owner = context as? LifecycleOwner ?: return
        val instance = BadgeManager.instance
        instance.observeData(owner, Observer {
            if (it == key) {
                updateNodeUI(key)
            }
        })

    }

    private fun updateNodeUI(key: String) {
        val instance = BadgeManager.instance
        val node = instance.getNode(key) ?: return
        if (!node.isVisible()) {
            hideBadge()
            return
        }
        when (node.type) {
            BadgeNode.Type.DRAWABLE -> {
                node.drawableResId?.let {
                    showDrawableBadge(
                        ContextCompat.getDrawable(
                            context,
                            node.drawableResId
                        )
                    )
                }
            }
            BadgeNode.Type.NUMBER -> {
                showTextBadge(node.number.toString())
            }
            BadgeNode.Type.DOT -> {
                showCirclePointBadge()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBadgeViewHelper.drawBadge(canvas)
    }

    override fun showCirclePointBadge() {
        mBadgeViewHelper.showCirclePointBadge()
    }

    override fun showTextBadge(badgeText: String?) {
        mBadgeViewHelper.showTextBadge(badgeText)
    }

    override fun hideBadge() {
        mBadgeViewHelper.hideBadge()
    }

    override fun showDrawableBadge(drawable: Drawable?) {
        mBadgeViewHelper.showDrawable(drawable)
    }

    override fun isShowBadge(): Boolean {
        return mBadgeViewHelper.isShowBadge
    }

    override fun getBadgeViewHelper(): BadgeViewHelper {
        return mBadgeViewHelper
    }

    override fun setGravity(gravity: BadgeGravity) {
        mBadgeViewHelper.setBadgeGravity(gravity)
    }

    override fun setBadgeBgColorInt(badgeBgColor: Int) {
        mBadgeViewHelper.setBadgeBgColorInt(badgeBgColor)
    }

    override fun setBadgeTextColorInt(badgeTextColor: Int) {
        mBadgeViewHelper.setBadgeTextColorInt(badgeTextColor)
    }

    override fun setBadgeTextSizeSp(textSize: Int) {
        mBadgeViewHelper.setBadgeTextSizeSp(textSize)
    }

    override fun setBadgeVerticalMarginDp(badgeVerticalMargin: Int) {
        mBadgeViewHelper.setBadgeVerticalMarginDp(badgeVerticalMargin)
    }

    override fun setBadgeHorizontalMarginDp(badgeHorizontalMargin: Int) {
        mBadgeViewHelper.setBadgeHorizontalMarginDp(badgeHorizontalMargin)
    }

    override fun setBadgePaddingDp(badgePadding: Int) {
        mBadgeViewHelper.setBadgePaddingDp(badgePadding)
    }

    override fun setBadgeBorderWidthDp(badgeBorderWidthDp: Int) {
        mBadgeViewHelper.setBadgeBorderWidthDp(badgeBorderWidthDp)
    }

    override fun setBadgeBorderColorInt(badgeBorderColor: Int) {
        mBadgeViewHelper.setBadgeBorderColorInt(badgeBorderColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = mBadgeViewHelper.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(size.width, size.height)
    }

}