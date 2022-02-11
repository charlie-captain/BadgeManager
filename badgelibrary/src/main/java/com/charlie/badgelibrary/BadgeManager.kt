package com.charlie.badgelibrary

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 消息红点树
 * 目前支持动态刷新
 * 如果有子节点, 那么只能通过修改子节点的数量, 最后刷新的时候会计算父节点的总数
 * 如果没有子节点, 可直接修改该节点数量
 * @sample
 * 1. 创建节点 @see[createNewNode]
 * 2. 在需要刷新UI的地方监听 @see[observeData]
 * 3. 修改该节点数量 @see[setBadgeNumber]
 * 4. 通知父节点刷新 @see[notifyDataChanged]
 */
class BadgeManager private constructor() {

    private val nodes: HashMap<String, BadgeNode> = HashMap()

    //数据刷新
    private val liveData = MutableLiveData<String>()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun observeData(owner: LifecycleOwner, observer: Observer<String>) {
        LiveEventObserver.observe<String>(liveData, owner, observer)
    }

    /**
     * 通知红点刷新
     * @param key
     */
    fun notifyDataChanged(key: String) {
        runInMainThread {
            liveData.value = key
        }
    }

    private fun runInMainThread(action: () -> Unit) {
        if (isMainThread()) {
            action.invoke()
        } else {
            mainHandler.post {
                action.invoke()
            }
        }
    }

    @MainThread
    fun createNewNode(
        key: String,
        type: BadgeNode.Type = BadgeNode.Type.NUMBER,
        dismissOnParentClick: Boolean = true,
        parentKey: String? = null
    ): Boolean {
        if (nodes.containsKey(key)) return false
        var node = BadgeNode(key, dismissOnParentClick, type = type)
        val parentNode: BadgeNode?
        if (parentKey != null) {
            parentNode = getNode(parentKey)
                ?: throw NullPointerException("parentName: $parentKey, parent node has not created")
            val childList = parentNode.childList ?: mutableListOf()
            childList.add(node.key)
            nodes[parentKey] = parentNode.copy(childList = childList)
            node = node.copy(parent = parentKey)
        }
        nodes[key] = node
        return true
    }

    /**
     * 如果要修改红点状态, 使用这个方法
     */
    @Synchronized
    fun edit(key: String): BadgeEditor {
        Log.d("test", "edit node = $key")
        val badgeNode = getNode(key)
            ?: throw IllegalArgumentException("badge node is not exist key = $key")
        return BadgeEditor(this, badgeNode)
    }

    @Synchronized
    fun setNode(badgeNode: BadgeNode) {
        nodes[badgeNode.key] = badgeNode
    }

    @Synchronized
    fun getNode(nodeName: String): BadgeNode? {
        return nodes[nodeName]
    }

    private fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    companion object {

        val instance: BadgeManager by lazy {
            BadgeManager()
        }

    }
}


