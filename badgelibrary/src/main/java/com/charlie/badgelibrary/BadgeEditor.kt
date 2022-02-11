package com.charlie.badgelibrary


/**
 * 通过此方法更新红点信息
 */
class BadgeEditor(private val badgeManager: BadgeManager, node: BadgeNode) {

    private var needUpdateParent = false

    // 修改克隆的值, 最后commit再覆盖
    private var copyBadgeNode = node.copy()

    /**
     * 修改红点数量
     * @param updateStatus 是否自动修改状态
     */
    fun setBadgeNumber(value: Int, updateStatus: Boolean = true): BadgeEditor {
        needUpdateParent = true
        if (updateStatus) {
            copyBadgeNode = if (value > 0) {
                copyBadgeNode.copy(status = BadgeState.VISIBLE.ordinal)
            } else {
                copyBadgeNode.copy(status = BadgeState.STATE_OWN_CLICKED.ordinal)
            }
        }
        copyBadgeNode = copyBadgeNode.copy(number = value)
        return this
    }

    fun setBadgeStatus(status: Int): BadgeEditor {
        if (copyBadgeNode.status != status) {
            needUpdateParent = true
        }
        copyBadgeNode = copyBadgeNode.copy(status = status)
        return this
    }

    fun setBadgePayload(payload: Any?): BadgeEditor {
        copyBadgeNode = copyBadgeNode.copy(payload = payload)
        return this
    }

    fun setBadgeCreatedAt(createdAt: Long): BadgeEditor {
        copyBadgeNode = copyBadgeNode.copy(createdAt = createdAt)
        return this
    }

    fun setBadgeUpdatedAt(updatedAt: Long): BadgeEditor {
        copyBadgeNode = copyBadgeNode.copy(updatedAt = updatedAt)
        return this
    }

    fun setBadgeType(type: BadgeNode.Type): BadgeEditor {
        copyBadgeNode = copyBadgeNode.copy(type = type)
        return this
    }

    fun setBadgeChain(chain: Boolean): BadgeEditor {
        if (copyBadgeNode.chainParent != chain) {
            needUpdateParent = true
        }
        copyBadgeNode = copyBadgeNode.copy(chainParent = chain)
        return this
    }

    /**
     * 会刷新本节点
     */
    fun commit(updateParent: Boolean = true) {
        apply(updateParent)
        badgeManager.notifyDataChanged(copyBadgeNode.key)
    }

    /**
     * 不刷新本节点
     */
    fun apply(updateParent: Boolean = true) {
        badgeManager.setNode(copyBadgeNode)
        if (updateParent) {
            updateParentNode(copyBadgeNode)
        }
    }

    /**
     * 设节点为已点击状态, 并通知刷新
     */
    fun dismissAndCommit(){
        val childList = copyBadgeNode.childList
        if (childList == null) {
            setBadgeNumber(0)
        } else {
            //自己是parent, 只需给一级子节点mark
            childList.forEach { child ->
                val childNode = badgeManager.getNode(child) ?: return@forEach
                if (!childNode.dismissOnParentClick || !childNode.chainParent) {
                    return@forEach
                }
                badgeManager.edit(childNode.key)
                    .setBadgeStatus(BadgeState.STATE_PARENT_CLICKED.ordinal)
                    .commit()
            }
        }
        commit()
    }

    private fun updateParentNode(copyBadgeNode: BadgeNode) {
        if (!needUpdateParent) return
        var p = copyBadgeNode
        while (p.parent != null) {
            p = badgeManager.getNode(p.parent!!) ?: break
            updateNodeFromChild(p)
        }
        needUpdateParent = false
    }

    /**
     * 更新父节点的子节点数量
     */
    private fun updateNodeFromChild(parentNode: BadgeNode) {
        val typeSizeMap = hashMapOf<BadgeNode.Type, Int>()
        parentNode.childList?.forEach { childKey ->
            val childNode = badgeManager.getNode(childKey) ?: return@forEach
            if (!childNode.chainParent) return@forEach
            if (childNode.status != BadgeState.VISIBLE.ordinal) return@forEach
            typeSizeMap[childNode.type] = childNode.number + (typeSizeMap[childNode.type] ?: 0)
        }
        badgeManager.edit(parentNode.key).let { editor ->
            if (typeSizeMap.filter { it.value > 0 }.isEmpty()) {
                editor.setBadgeNumber(0)
            } else {
                for (type in BadgeNode.Type.values()) {
                    val number = typeSizeMap[type] ?: 0
                    if (number > 0) {
                        editor.setBadgeNumber(number)
                        editor.setBadgeType(type)
                        break
                    }
                }
            }
            //支持不需要再刷新父节点
            editor.commit(updateParent = false)
        }
    }

}

