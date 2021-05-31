package com.android.weischool.info;
/**
 * Created by dayuer on 19/7/2.
 * 课程-节结构体
 */
public class CourseSectionsInfo {
    String mCourseSectionsId = "";//课程-章-节id
    String mCourseSectionsName = "";//课程-章-节名称
    String mCourseSectionsOrder = "";//课程-章-节排序
    String mCourseSectionsPrice = "";//课程-章-节价格（免费、试听）
    String mCourseSectionsTime = "00:00:00";//课程-章-节时长
    int mCourseSectionsTime1 = 0;//课程-章-节时长
    String mCourseSectionsSize = "";//课程-章-节视频文件大小
    String mCourseSectionsDownloadUrl = "";//课程-章-节视频下载链接
    String mCourseSectionsLearnProgress = "";//课程-章-节学习进度
    String mVideoId = "";//课程-录播视频id

    public String getmCourseSectionsId() {
        return mCourseSectionsId;
    }

    public void setmCourseSectionsId(String mCourseSectionsId) {
        this.mCourseSectionsId = mCourseSectionsId;
    }

    public String getmCourseSectionsName() {
        return mCourseSectionsName;
    }

    public void setmCourseSectionsName(String mCourseSectionsName) {
        this.mCourseSectionsName = mCourseSectionsName;
    }

    public String getmCourseSectionsOrder() {
        return mCourseSectionsOrder;
    }

    public void setmCourseSectionsOrder(String mCourseSectionsOrder) {
        this.mCourseSectionsOrder = mCourseSectionsOrder;
    }

    public String getmCourseSectionsPrice() {
        return mCourseSectionsPrice;
    }

    public void setmCourseSectionsPrice(String mCourseSectionsPrice) {
        this.mCourseSectionsPrice = mCourseSectionsPrice;
    }

    public String getmCourseSectionsTime() {
        return mCourseSectionsTime;
    }

    public void setmCourseSectionsTime(String mCourseSectionsTime) {
        this.mCourseSectionsTime = mCourseSectionsTime;
    }

    public int getmCourseSectionsTime1() {
        return mCourseSectionsTime1;
    }

    public void setmCourseSectionsTime1(int mCourseSectionsTime1) {
        this.mCourseSectionsTime1 = mCourseSectionsTime1;
    }

    public String getmCourseSectionsSize() {
        return mCourseSectionsSize;
    }

    public void setmCourseSectionsSize(String mCourseSectionsSize) {
        this.mCourseSectionsSize = mCourseSectionsSize;
    }

    public String getmCourseSectionsDownloadUrl() {
        return mCourseSectionsDownloadUrl;
    }

    public void setmCourseSectionsDownloadUrl(String mCourseSectionsDownloadUrl) {
        this.mCourseSectionsDownloadUrl = mCourseSectionsDownloadUrl;
    }

    public String getmCourseSectionsLearnProgress() {
        return mCourseSectionsLearnProgress;
    }

    public void setmCourseSectionsLearnProgress(String mCourseSectionsLearnProgress) {
        this.mCourseSectionsLearnProgress = mCourseSectionsLearnProgress;
    }

    public String getmVideoId() {
        return mVideoId;
    }

    public void setmVideoId(String mVideoId) {
        this.mVideoId = mVideoId;
    }
}
