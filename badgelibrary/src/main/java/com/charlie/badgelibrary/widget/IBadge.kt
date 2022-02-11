package com.charlie.badgelibrary.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewParent

interface IBadge {
    /**
     * 显示圆点徽章
     */
    fun showCirclePointBadge()

    /**
     * 显示文字徽章
     *
     * @param badgeText
     */
    fun showTextBadge(badgeText: String?)

    /**
     * 隐藏徽章
     */
    fun hideBadge()

    /**
     * 显示图像徽章
     *
     * @param drawable
     */
    fun showDrawableBadge(drawable: Drawable?)

    /**
     * 是否显示徽章
     *
     * @return
     */
    fun isShowBadge(): Boolean

    /**
     * 设置相对位置
     */
    fun setGravity(gravity: BadgeGravity)

    fun setBadgeBgColorInt(badgeBgColor: Int)
    fun setBadgeTextColorInt(badgeTextColor: Int)
    fun setBadgeTextSizeSp(textSize: Int)
    fun setBadgeVerticalMarginDp(badgeVerticalMargin: Int)
    fun setBadgeHorizontalMarginDp(badgeHorizontalMargin: Int)
    fun setBadgePaddingDp(badgePadding: Int)
    fun setBadgeBorderWidthDp(badgeBorderWidthDp: Int)
    fun setBadgeBorderColorInt(badgeBorderColor: Int)

    fun getBadgeViewHelper(): BadgeViewHelper?

    fun getWidth(): Int

    fun getHeight(): Int

    fun postInvalidate()

    fun getParent(): ViewParent?

    fun getId(): Int

    fun getGlobalVisibleRect(r: Rect?): Boolean

    fun getContext(): Context

    fun getRootView(): View?
}