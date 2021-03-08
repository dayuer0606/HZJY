package com.android.school;

import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityManager {
    private static ActivityManager sSingleton ;

    public static ActivityManager getInstance() {
        if (sSingleton == null) {
            if (sSingleton == null) {
                sSingleton = new ActivityManager() ;
            }
        }
        return sSingleton;
    }

    private HashMap<Integer, FragmentActivity> map = new HashMap<>();
    private ArrayList<FragmentActivity> list = new ArrayList<>();


    public void put(int sessionId, FragmentActivity activity) {
        map.put(sessionId, activity);
        list.add(activity);
    }

    public FragmentActivity get(int sessionId) {
        return map.get(sessionId);
    }

    public void remove(int sessionId) {
        list.remove(map.remove(sessionId));
    }

    public void finish(int sessionId) {
        FragmentActivity activity = map.remove(sessionId);
        if (activity != null) {
            activity.finish();
        }
        list.remove(activity);
    }

    public void finishAll() {
        for (FragmentActivity activity : list) {
            activity.finish();
        }
        list.clear();
        map.clear();
    }
}
