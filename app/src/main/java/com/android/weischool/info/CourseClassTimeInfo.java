package com.android.weischool.info;
/**
 * Created by dayuer on 19/7/2.
 * 课程-课次结构体
 */
public class CourseClassTimeInfo {
    String mCourseClassTimeId = "";//课次id
//    String mCourseUnitId = "";//课程单元id
    String mCourseClassTimeName = "";//课次名称
    String mCourseClassTimeStartTime = "";//课次开始时间
    int liveStatus = 0;//课程状态 1 未开始；2 正在直播；3 已结束

    public String getmCourseClassTimeId() {
        return mCourseClassTimeId;
    }

    public void setmCourseClassTimeId(String mCourseClassTimeId) {
        this.mCourseClassTimeId = mCourseClassTimeId;
    }

    public String getmCourseClassTimeName() {
        return mCourseClassTimeName;
    }

    public void setmCourseClassTimeName(String mCourseClassTimeName) {
        this.mCourseClassTimeName = mCourseClassTimeName;
    }

    public String getmCourseClassTimeStartTime() {
        return mCourseClassTimeStartTime;
    }

    public void setmCourseClassTimeStartTime(String mCourseClassTimeStartTime) {
        this.mCourseClassTimeStartTime = mCourseClassTimeStartTime;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(int liveStatus) {
        this.liveStatus = liveStatus;
    }
}
