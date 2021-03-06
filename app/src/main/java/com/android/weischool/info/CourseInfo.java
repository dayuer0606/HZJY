package com.android.weischool.info;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 课程结构体
 */
public class CourseInfo {
    String mTeacherName = "";
    String mTeacherIcon = "";
    String mCourseId = "";//课程id
    String mCourseCover = "";//课程封面
    String mCourseName = "";//课程名称
    String mCourseType = "";//课程类型
    String mCourseLearnPersonNum = "";//课程学习人数
    String mCoursePrice = "";//课程价格
    String mCoursePriceOld = "";//课程原价格
    String mCourseMessage = "";//课程描述
    String mCourseDetails = "";//课程详情
    String mCourseValidityPeriod = "";//课程有效期限
    String mCourseIsHave = "0";//课程是否购买(0:没买 1:买了)
    String mCourseOrder = "";//课程排序
    String mCourseIsCollect = "0";//课程是否收藏 1:收藏，0没收藏
    String mCourseIsStartLearn = "0";//课程是否开始学习 1:是，0否
    String mCourseTotalHours = "";//课程-总课时
    public List<CourseChaptersInfo> mCourseChaptersInfoList = new ArrayList<>();//课程章
    public List<CourseUnitInfo> mCourseUnitInfoList = new ArrayList<>();//课程单元
    Integer mTodayLiveSum = 0; //今天直播课总数
    Integer mBeforeLiveSum = 0; //历史直播课总数
    Integer mAfterLiveSum = 0; //后续直播课总数
    public List<CourseClassTimeInfo> mCourseClassTimeInfoTodayList = new ArrayList<>();//课次(今日)
    public List<CourseClassTimeInfo> mCourseClassTimeInfoBeforeList = new ArrayList<>();//课次(历史)
    public List<CourseClassTimeInfo> mCourseClassTimeInfoAfterList = new ArrayList<>();//课次(后续)
    public List<CourseQuestionInfo> mCourseQuestionInfoList = new ArrayList<>();//课程问答
    public CourseInfo(){

    }
    public CourseInfo(CourseInfo courseInfo){
        mCourseId = courseInfo.mCourseId;//课程id
        mCourseCover = courseInfo.mCourseCover;//课程封面
        mCourseName = courseInfo.mCourseName;//课程名称
        mCourseType = courseInfo.mCourseType;//课程类型
        mCourseTotalHours = courseInfo.mCourseTotalHours;//课程-总课时
        mCourseLearnPersonNum = courseInfo.mCourseLearnPersonNum;//课程学习人数
        mCoursePrice = courseInfo.mCoursePrice;//课程价格
        mCoursePriceOld = courseInfo.mCoursePriceOld;//课程原价格
        mCourseMessage = courseInfo.mCourseMessage;//课程描述
        mCourseDetails = courseInfo.mCourseDetails;//课程详情
        mCourseValidityPeriod = courseInfo.mCourseValidityPeriod;//课程有效期限
        mCourseOrder = courseInfo.mCourseOrder;//课程排序
        mCourseIsCollect = courseInfo.mCourseIsCollect;//课程是否收藏 1:收藏，0没收藏
        mCourseIsStartLearn = courseInfo.mCourseIsStartLearn;////课程是否开始学习 1:是，0否
        mTeacherName = courseInfo.mTeacherName;
        mTeacherIcon = courseInfo.mTeacherIcon;
        mCourseChaptersInfoList.addAll(courseInfo.mCourseChaptersInfoList);//课程章
        mCourseUnitInfoList.addAll(courseInfo.mCourseUnitInfoList);//课程单元
        mCourseClassTimeInfoTodayList.addAll(courseInfo.mCourseClassTimeInfoTodayList);//课次(今日)
        mCourseClassTimeInfoBeforeList.addAll(courseInfo.mCourseClassTimeInfoBeforeList);//课次(历史)
        mCourseClassTimeInfoAfterList.addAll(courseInfo.mCourseClassTimeInfoAfterList);//课次(后续)
        mCourseQuestionInfoList.addAll(courseInfo.mCourseQuestionInfoList);//课程问答
    }

    public String getmCourseId() {
        return mCourseId;
    }

    public void setmCourseId(String mCourseId) {
        this.mCourseId = mCourseId;
    }

    public String getmCourseCover() {
        return mCourseCover;
    }

    public void setmCourseCover(String mCourseCover) {
        this.mCourseCover = mCourseCover;
    }

    public String getmCourseName() {
        return mCourseName;
    }

    public void setmCourseName(String mCourseName) {
        this.mCourseName = mCourseName;
    }

    public String getmCourseType() {
        return mCourseType;
    }

    public void setmCourseType(String mCourseType) {
        this.mCourseType = mCourseType;
    }

    public String getmCourseLearnPersonNum() {
        return mCourseLearnPersonNum;
    }

    public void setmCourseLearnPersonNum(String mCourseLearnPersonNum) {
        this.mCourseLearnPersonNum = mCourseLearnPersonNum;
    }

    public String getmCoursePrice() {
        return mCoursePrice;
    }

    public void setmCoursePrice(String mCoursePrice) {
        this.mCoursePrice = mCoursePrice;
    }

    public String getmCoursePriceOld() {
        return mCoursePriceOld;
    }

    public void setmCoursePriceOld(String mCoursePriceOld) {
        this.mCoursePriceOld = mCoursePriceOld;
    }

    public String getmCourseMessage() {
        return mCourseMessage;
    }

    public void setmCourseMessage(String mCourseMessage) {
        this.mCourseMessage = mCourseMessage;
    }

    public String getmCourseDetails() {
        return mCourseDetails;
    }

    public void setmCourseDetails(String mCourseDetails) {
        this.mCourseDetails = mCourseDetails;
    }

    public String getmCourseValidityPeriod() {
        return mCourseValidityPeriod;
    }

    public void setmCourseValidityPeriod(String mCourseValidityPeriod) {
        this.mCourseValidityPeriod = mCourseValidityPeriod;
    }

    public String getmCourseIsHave() {
        return mCourseIsHave;
    }

    public void setmCourseIsHave(String mCourseIsHave) {
        this.mCourseIsHave = mCourseIsHave;
    }

    public String getmCourseOrder() {
        return mCourseOrder;
    }

    public void setmCourseOrder(String mCourseOrder) {
        this.mCourseOrder = mCourseOrder;
    }

    public String getmCourseIsCollect() {
        return mCourseIsCollect;
    }

    public void setmCourseIsCollect(String mCourseIsCollect) {
        this.mCourseIsCollect = mCourseIsCollect;
    }

    public String getmCourseIsStartLearn() {
        return mCourseIsStartLearn;
    }

    public void setmCourseIsStartLearn(String mCourseIsStartLearn) {
        this.mCourseIsStartLearn = mCourseIsStartLearn;
    }

    public String getmCourseTotalHours() {
        return mCourseTotalHours;
    }

    public void setmCourseTotalHours(String mCourseTotalHours) {
        this.mCourseTotalHours = mCourseTotalHours;
    }

    public List<CourseChaptersInfo> getmCourseChaptersInfoList() {
        return mCourseChaptersInfoList;
    }

    public void setmCourseChaptersInfoList(List<CourseChaptersInfo> mCourseChaptersInfoList) {
        this.mCourseChaptersInfoList = mCourseChaptersInfoList;
    }

    public List<CourseUnitInfo> getmCourseUnitInfoList() {
        return mCourseUnitInfoList;
    }

    public void setmCourseUnitInfoList(List<CourseUnitInfo> mCourseUnitInfoList) {
        this.mCourseUnitInfoList = mCourseUnitInfoList;
    }

    public Integer getmTodayLiveSum() {
        return mTodayLiveSum;
    }

    public void setmTodayLiveSum(Integer mTodayLiveSum) {
        this.mTodayLiveSum = mTodayLiveSum;
    }

    public Integer getmBeforeLiveSum() {
        return mBeforeLiveSum;
    }

    public void setmBeforeLiveSum(Integer mBeforeLiveSum) {
        this.mBeforeLiveSum = mBeforeLiveSum;
    }

    public Integer getmAfterLiveSum() {
        return mAfterLiveSum;
    }

    public void setmAfterLiveSum(Integer mAfterLiveSum) {
        this.mAfterLiveSum = mAfterLiveSum;
    }

    public List<CourseClassTimeInfo> getmCourseClassTimeInfoTodayList() {
        return mCourseClassTimeInfoTodayList;
    }

    public void setmCourseClassTimeInfoTodayList(List<CourseClassTimeInfo> mCourseClassTimeInfoTodayList) {
        this.mCourseClassTimeInfoTodayList = mCourseClassTimeInfoTodayList;
    }

    public List<CourseClassTimeInfo> getmCourseClassTimeInfoBeforeList() {
        return mCourseClassTimeInfoBeforeList;
    }

    public void setmCourseClassTimeInfoBeforeList(List<CourseClassTimeInfo> mCourseClassTimeInfoBeforeList) {
        this.mCourseClassTimeInfoBeforeList = mCourseClassTimeInfoBeforeList;
    }

    public List<CourseClassTimeInfo> getmCourseClassTimeInfoAfterList() {
        return mCourseClassTimeInfoAfterList;
    }

    public void setmCourseClassTimeInfoAfterList(List<CourseClassTimeInfo> mCourseClassTimeInfoAfterList) {
        this.mCourseClassTimeInfoAfterList = mCourseClassTimeInfoAfterList;
    }

    public List<CourseQuestionInfo> getmCourseQuestionInfoList() {
        return mCourseQuestionInfoList;
    }

    public void setmCourseQuestionInfoList(List<CourseQuestionInfo> mCourseQuestionInfoList) {
        this.mCourseQuestionInfoList = mCourseQuestionInfoList;
    }

    public String getmTeacherName() {
        return mTeacherName;
    }

    public void setmTeacherName(String mTeacherName) {
        this.mTeacherName = mTeacherName;
    }

    public String getmTeacherIcon() {
        return mTeacherIcon;
    }

    public void setmTeacherIcon(String mTeacherIcon) {
        this.mTeacherIcon = mTeacherIcon;
    }
}
