package com.android.school;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 可以滚动的grid列表视图（解决嵌套scrollview 无法滚动问题）
 * Created by dayuer on 2019/12/4.
 */
public class ControllerGridViewForScrollView extends GridView {

    public ControllerGridViewForScrollView(Context context) {
        super(context);
    }

    public ControllerGridViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllerGridViewForScrollView(Context context, AttributeSet attrs,
                                           int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 只需要重写这个方法即可
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
