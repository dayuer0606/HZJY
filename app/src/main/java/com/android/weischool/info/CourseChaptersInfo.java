package com.android.weischool.info;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 课程-章节结构体
 */
public class CourseChaptersInfo {
    String mCourseChaptersId = "";//课程-章id
    String mCourseChaptersName = "";//课程-章名称
    String mCourseChaptersOrder = "";//课程-章排序
    int mCourseSectionsSum = 0;//课程-章下面的节总数
    int mCourseSectionsCount = 5;//课程-章下面的节一页显示多少个
    int mCourseSectionsPage = 1;//课程-章下面的节当前页数
    public List<CourseSectionsInfo> mCourseSectionsInfoList = new ArrayList<>();//课程-章-节
    public CourseChaptersInfo(){

    }
    public CourseChaptersInfo(CourseChaptersInfo courseChaptersInfo){
        mCourseChaptersId = courseChaptersInfo.mCourseChaptersId;//课程-章id
        mCourseChaptersName = courseChaptersInfo.mCourseChaptersName;//课程-章名称
        mCourseChaptersOrder = courseChaptersInfo.mCourseChaptersOrder;//课程-章排序
       mCourseSectionsInfoList.addAll(courseChaptersInfo.mCourseSectionsInfoList);//课程-章-节
    }

    public String getmCourseChaptersId() {
        return mCourseChaptersId;
    }

    public void setmCourseChaptersId(String mCourseChaptersId) {
        this.mCourseChaptersId = mCourseChaptersId;
    }

    public String getmCourseChaptersName() {
        return mCourseChaptersName;
    }

    public void setmCourseChaptersName(String mCourseChaptersName) {
        this.mCourseChaptersName = mCourseChaptersName;
    }

    public String getmCourseChaptersOrder() {
        return mCourseChaptersOrder;
    }

    public void setmCourseChaptersOrder(String mCourseChaptersOrder) {
        this.mCourseChaptersOrder = mCourseChaptersOrder;
    }

    public int getmCourseSectionsSum() {
        return mCourseSectionsSum;
    }

    public void setmCourseSectionsSum(int mCourseSectionsSum) {
        this.mCourseSectionsSum = mCourseSectionsSum;
    }

    public int getmCourseSectionsCount() {
        return mCourseSectionsCount;
    }

    public void setmCourseSectionsCount(int mCourseSectionsCount) {
        this.mCourseSectionsCount = mCourseSectionsCount;
    }

    public int getmCourseSectionsPage() {
        return mCourseSectionsPage;
    }

    public void setmCourseSectionsPage(int mCourseSectionsPage) {
        this.mCourseSectionsPage = mCourseSectionsPage;
    }

    public List<CourseSectionsInfo> getmCourseSectionsInfoList() {
        return mCourseSectionsInfoList;
    }

    public void setmCourseSectionsInfoList(List<CourseSectionsInfo> mCourseSectionsInfoList) {
        this.mCourseSectionsInfoList = mCourseSectionsInfoList;
    }
}
