
package com.android.jwjy.zkktproduct.event;


import com.android.jwjy.zkktproduct.entity.ExpressionEntity;

/**
 * Created by asus on 2015/11/24.
 */
public interface OnExpressionListener {
    void OnExpressionSelected(ExpressionEntity entity);
    void OnExpressionRemove();
}
