/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.utils

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

class RecyclerItemClickListener(context: Context?, listener: OnItemClickListener) :
    OnItemTouchListener {
    var listener: OnItemClickListener
    private val gestureDetector: GestureDetector
    private var childView: View? = null
    private var childViewPosition = 0

    init {
        gestureDetector = GestureDetector(context, GestureListener())
        this.listener = listener
    }

    override fun onInterceptTouchEvent(view: RecyclerView, event: MotionEvent): Boolean {
        childView = view.findChildViewUnder(event.x, event.y)
        childViewPosition = view.getChildAdapterPosition(childView!!)
        return childView != null && gestureDetector.onTouchEvent(event)
    }

    override fun onTouchEvent(view: RecyclerView, event: MotionEvent) {
        // Not needed.
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    /**
     * A click listener for items.
     */
    interface OnItemClickListener {
        /**
         * Called when an item is clicked.
         *
         * @param childView View of the item that was clicked.
         * @param position  Position of the item that was clicked.
         */
        fun onItemClick(childView: View?, position: Int)

        /**
         * Called when an item is long pressed.
         *
         * @param childView View of the item that was long pressed.
         * @param position  Position of the item that was long pressed.
         */
        fun onItemLongPress(childView: View?, position: Int)
    }

    /**
     * A simple click listener whose methods can be overridden one by one.
     */
    abstract class SimpleOnItemClickListener : OnItemClickListener {
        /**
         * Called when an item is clicked. The default implementation is a no-op.
         *
         * @param childView View of the item that was clicked.
         * @param position  Position of the item that was clicked.
         */
        override fun onItemClick(childView: View?, position: Int) {
            // Do nothing.
        }

        /**
         * Called when an item is long pressed. The default implementation is a no-op.
         *
         * @param childView View of the item that was long pressed.
         * @param position  Position of the item that was long pressed.
         */
        override fun onItemLongPress(childView: View?, position: Int) {
            // Do nothing.
        }
    }

    inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            if (childView != null) {
                listener.onItemClick(childView, childViewPosition)
            }
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            if (childView != null) {
                listener.onItemLongPress(childView, childViewPosition)
            }
        }

        override fun onDown(event: MotionEvent): Boolean {
            // Best practice to always return true here.
            // http://developer.android.com/training/gestures/detector.html#detect
            return true
        }
    }
}
