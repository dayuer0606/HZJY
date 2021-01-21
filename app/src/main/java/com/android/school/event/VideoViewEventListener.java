package com.android.school.event;

import android.view.View;

public interface VideoViewEventListener<T> {
    void onItemDoubleClick(View v, T item);
}
