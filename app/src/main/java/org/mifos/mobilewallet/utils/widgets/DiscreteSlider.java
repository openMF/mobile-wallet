package org.mifos.mobilewallet.utils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.etiennelawlor.discreteslider.library.R.id;
import com.etiennelawlor.discreteslider.library.R.layout;
import com.etiennelawlor.discreteslider.library.R.styleable;
import com.etiennelawlor.discreteslider.library.ui.DiscreteSeekBar;
import com.etiennelawlor.discreteslider.library.ui.DiscreteSeekBar.OnDiscreteSeekBarChangeListener;
import com.etiennelawlor.discreteslider.library.ui.DiscreteSliderBackdrop;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;

/**
 * Adds a method to return the internal seekbar used
 */

public class DiscreteSlider extends FrameLayout {

    private DiscreteSliderBackdrop discreteSliderBackdrop;
    private DiscreteSeekBar discreteSeekBar;
    private int tickMarkCount;
    private float tickMarkRadius;
    private int position;
    private float horizontalBarThickness;
    private int backdropFillColor;
    private int backdropStrokeColor;
    private float backdropStrokeWidth;
    private Drawable thumb;
    private Drawable progressDrawable;
    private com.etiennelawlor.discreteslider.library.ui.DiscreteSlider
            .OnDiscreteSliderChangeListener onDiscreteSliderChangeListener;
    private int discreteSeekBarLeftPadding = DisplayUtility.dp2px(this.getContext(), 32);
    private int discreteSeekBarRightPadding = DisplayUtility.dp2px(this.getContext(), 32);

    public DiscreteSlider(Context context) {
        super(context);
        this.init(context, (AttributeSet) null);
    }

    public DiscreteSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public DiscreteSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, styleable.DiscreteSlider);

        try {
            this.tickMarkCount =
                    attributeArray.getInteger(styleable.DiscreteSlider_tickMarkCount,
                            5);
            this.tickMarkRadius =
                    attributeArray.getDimension(styleable.DiscreteSlider_tickMarkRadius,
                            8.0F);
            this.position = attributeArray.getInteger(styleable.DiscreteSlider_position,
                    0);
            this.horizontalBarThickness =
                    attributeArray.getDimension(styleable.DiscreteSlider_horizontalBarThickness,
                            4.0F);
            this.backdropFillColor =
                    attributeArray.getColor(styleable.DiscreteSlider_backdropFillColor,
                            -7829368);
            this.backdropStrokeColor =
                    attributeArray.getColor(styleable.DiscreteSlider_backdropStrokeColor,
                            -7829368);
            this.backdropStrokeWidth =
                    attributeArray.getDimension(styleable.DiscreteSlider_backdropStrokeWidth,
                            1.0F);
            this.thumb = attributeArray.getDrawable(styleable.DiscreteSlider_thumb);
            this.progressDrawable =
                    attributeArray.getDrawable(styleable.DiscreteSlider_progressDrawable);
        } finally {
            attributeArray.recycle();
        }

        View view = inflate(context, layout.discrete_slider, this);
        this.discreteSliderBackdrop = (DiscreteSliderBackdrop)
                view.findViewById(id.discrete_slider_backdrop);
        this.discreteSeekBar = (DiscreteSeekBar) view.findViewById(id.discrete_seek_bar);
        this.setTickMarkCount(this.tickMarkCount);
        this.setTickMarkRadius(this.tickMarkRadius);
        this.setHorizontalBarThickness(this.horizontalBarThickness);
        this.setBackdropFillColor(this.backdropFillColor);
        this.setBackdropStrokeColor(this.backdropStrokeColor);
        this.setBackdropStrokeWidth(this.backdropStrokeWidth);
        this.setPosition(this.position);
        this.setThumb(this.thumb);
        this.setProgressDrawable(this.progressDrawable);
        this.discreteSeekBar.setPadding(this.discreteSeekBarLeftPadding, 0,
                this.discreteSeekBarRightPadding, 0);

        this.discreteSeekBar.setOnDiscreteSeekBarChangeListener(
                new OnDiscreteSeekBarChangeListener() {
                    public void onPositionChanged(int position) {
                        if (DiscreteSlider.this.onDiscreteSliderChangeListener != null) {
                            DiscreteSlider.this.onDiscreteSliderChangeListener
                                    .onPositionChanged(position);
                            DiscreteSlider.this.setPosition(position);
                        }

                    }
                });
    }

    public void setTickMarkCount(int tickMarkCount) {
        this.tickMarkCount = tickMarkCount;
        this.discreteSliderBackdrop.setTickMarkCount(tickMarkCount);
        this.discreteSliderBackdrop.invalidate();
        this.discreteSeekBar.setTickMarkCount(tickMarkCount);
        this.discreteSeekBar.invalidate();
    }

    public void setTickMarkRadius(float tickMarkRadius) {
        this.tickMarkRadius = tickMarkRadius;
        this.discreteSliderBackdrop.setTickMarkRadius(tickMarkRadius);
        this.discreteSliderBackdrop.invalidate();
    }

    public void setPosition(int position) {
        if (position < 0) {
            this.position = 0;
        } else if (position > this.tickMarkCount - 1) {
            this.position = this.tickMarkCount - 1;
        } else {
            this.position = position;
        }

        this.discreteSeekBar.setPosition(this.position);
    }

    public void setHorizontalBarThickness(float horizontalBarThickness) {
        this.discreteSliderBackdrop.setHorizontalBarThickness(horizontalBarThickness);
        this.discreteSliderBackdrop.invalidate();
    }

    public void setBackdropFillColor(int backdropFillColor) {
        this.discreteSliderBackdrop.setBackdropFillColor(backdropFillColor);
        this.discreteSliderBackdrop.invalidate();
    }

    public void setBackdropStrokeColor(int backdropStrokeColor) {
        this.discreteSliderBackdrop.setBackdropStrokeColor(backdropStrokeColor);
        this.discreteSliderBackdrop.invalidate();
    }

    public void setBackdropStrokeWidth(float backdropStrokeWidth) {
        this.discreteSliderBackdrop.setBackdropStrokeWidth(backdropStrokeWidth);
        this.discreteSliderBackdrop.invalidate();
    }

    public void setThumb(Drawable thumb) {
        if (thumb != null) {
            this.discreteSeekBar.setThumb(thumb);
            this.discreteSeekBar.invalidate();
        }

    }

    public void setProgressDrawable(Drawable progressDrawable) {
        if (progressDrawable != null) {
            this.discreteSeekBar.setProgressDrawable(progressDrawable);
            this.discreteSeekBar.invalidate();
        }

    }

    public void setOnDiscreteSliderChangeListener(com.etiennelawlor.discreteslider
                                                          .library.ui.DiscreteSlider
                                                          .OnDiscreteSliderChangeListener
                                                          onDiscreteSliderChangeListener) {
        this.onDiscreteSliderChangeListener = onDiscreteSliderChangeListener;
    }

    public int getTickMarkCount() {
        return this.tickMarkCount;
    }

    public float getTickMarkRadius() {
        return this.tickMarkRadius;
    }

    public int getPosition() {
        return this.position;
    }

    public DiscreteSeekBar getSeekBar() {
        return discreteSeekBar;
    }

    public interface OnDiscreteSliderChangeListener {
        void onPositionChanged(int var1);
    }
}