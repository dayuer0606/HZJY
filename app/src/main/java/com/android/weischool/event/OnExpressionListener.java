
package com.android.weischool.event;


import com.android.weischool.entity.ExpressionEntity;

/**
 * Created by asus on 2015/11/24.
 */
public interface OnExpressionListener {
    void OnExpressionSelected(ExpressionEntity entity);
    void OnExpressionRemove();
}
