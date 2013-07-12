package com.doomonafireball.betterpickers;

import uk.co.androidalliance.edgeeffectoverride.ContextWrapperEdgeEffect;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * User: derek Date: 5/2/13 Time: 9:19 PM
 */
public class AutoScrollHorizontalScrollView extends HorizontalScrollView {

    public AutoScrollHorizontalScrollView(Context context) {
        super(new ContextWrapperEdgeEffect(context));
    }

    public AutoScrollHorizontalScrollView(Context context, AttributeSet attrs) {
        super(new ContextWrapperEdgeEffect(context), attrs);
    }

    public AutoScrollHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextWrapperEdgeEffect(context), attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.fullScroll(FOCUS_RIGHT);
    }
}
