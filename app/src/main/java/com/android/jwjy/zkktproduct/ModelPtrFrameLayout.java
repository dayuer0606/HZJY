package com.android.jwjy.zkktproduct;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import in.srain.cube.views.ptr.PtrFrameLayout;

public class ModelPtrFrameLayout extends PtrFrameLayout {
    private float mDownX;
    private float mDownY;
    public ModelPtrFrameLayout(Context context) {
        super(context);
    }

    public ModelPtrFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModelPtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getRawY();
                float diffX = Math.abs(moveX - mDownX);
                float diffY = Math.abs(moveY - mDownY);
                boolean isHorizon = Math.tan(diffY / diffX) < Math.tan(45.0);
                if (isHorizon) {
                    return dispatchTouchEventSupper(ev);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
