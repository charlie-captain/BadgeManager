package com.charlie.badgelibrary

import androidx.lifecycle.*

/**
 * created by charlie on 2020/5/9
 * 保证LiveData事件不丢失
 */
class LiveEventObserver<T>(private val liveData: LiveData<T>, private val owner: LifecycleOwner, private val observer: Observer<T>) :
    LifecycleObserver, Observer<T> {

    private val pendingData = mutableSetOf<T>()

    init {
        owner.lifecycle.addObserver(this)
        liveData.observeForever(this)
    }

    override fun onChanged(t: T) {
        if (isActive()) {
            observer.onChanged(t)
        } else {
            pendingData.add(t)
        }
    }


    private fun isActive(): Boolean {
        return owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        if (owner != this.owner) {
            return
        }
        if (pendingData.isNotEmpty() && (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME)) {
            pendingData.forEach {
                observer.onChanged(it)
            }
            pendingData.clear()
        }

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        liveData.removeObserver(this)
        owner.lifecycle.removeObserver(this)
        pendingData.clear()
    }


    companion object {
        fun <T> observe(liveData: LiveData<T>, owner: LifecycleOwner, observer: Observer<T>) {
            if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                return
            }
            LiveEventObserver<T>(liveData, owner, observer)
        }
    }
}