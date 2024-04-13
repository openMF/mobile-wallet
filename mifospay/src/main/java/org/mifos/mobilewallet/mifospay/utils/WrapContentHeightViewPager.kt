package org.mifos.mobilewallet.mifospay.utils

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Created by ankur on 13/July/2018
 */
class WrapContentHeightViewPager : ViewPager {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) height = h
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    } //    @Override
    //    public boolean onInterceptTouchEvent(MotionEvent event) {
    //        // Never allow swiping to switch between pages
    //        return false;
    //    }
    //
    //    @Override
    //    public boolean onTouchEvent(MotionEvent event) {
    //        // Never allow swiping to switch between pages
    //        return false;
    //    }
}
