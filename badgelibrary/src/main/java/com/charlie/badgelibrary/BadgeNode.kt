package com.charlie.badgelibrary

import androidx.annotation.MainThread


/**
 * 红点节点, 必须通过BadgeEditor修改
 * @param key
 * @param dismissOnParentClick   true: 点击父节点会消掉红点 false: 父节点不影响子节点
 */
data class BadgeNode(
    val key: String,
    val dismissOnParentClick: Boolean = true,
    val type: Type = Type.NUMBER,
    val parent: String? = null,
    val childList: MutableList<String>? = null,
    val number: Int = 0,
    val status: Int = BadgeState.STATE_OWN_CLICKED.ordinal,
    val payload: Any? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    //是否链接parent, false: 不计算入节点数
    val chainParent: Boolean = true,
    val drawableResId: Int? = null,
) {


    //类型按优先级排序(左边优先级高)
    enum class Type {
        DRAWABLE, NUMBER, DOT
    }

    fun isVisible(): Boolean {
        return status != BadgeState.STATE_OWN_CLICKED.ordinal
    }
}

