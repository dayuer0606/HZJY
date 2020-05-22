package com.android.jwjy.zkktproduct;

/**
 * Created by czg on 2017/7/13.
 */

public interface ModelSoftCheckable {
    void setChecked(boolean checked, boolean isOrientation);

    /**
     * @return The current checked state of the view
     */
    boolean isChecked();

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    void toggle();
}
