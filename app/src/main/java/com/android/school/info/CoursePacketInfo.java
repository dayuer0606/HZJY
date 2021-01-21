package com.android.school.info;

import com.android.school.StageCourseInfo;
import com.android.school.TeacherInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dayuer on 19/7/2.
 * 课程包结构体
 */
public class CoursePacketInfo {
    public String mCoursePacketId = "";//课程包id
    public String mCoursePacketCover = "";//课程包封面
    public String mCoursePacketName = "";//课程包名称
    public String mCoursePacketStageNum = "";//课程包阶段数量
    public String mCoursePacketCourseNum = "";//课程包课程数量
    public String mCoursePacketLearnPersonNum = "";//课程包学习人数
    public String mCoursePacketPrice = "";//课程包价格
    public String mCoursePacketPriceOld = "";//课程包原价格
    public String mCoursePacketMessage = "";//课程包描述
    public String mCoursePacketDetails = "";//课程包详情
    public String mCoursePacketIsHave = "0";//课程包是否已经购买(0:没买 1:买了)
    public List<StageCourseInfo> mStageCourseInfoList = new ArrayList<>();//课程包包含的课程阶段
    public List<TeacherInfo> mTeacherInfoList = new ArrayList<>();//课程包包含的师资
    public CoursePacketInfo(){

    }
    public CoursePacketInfo(CoursePacketInfo coursePacketInfo){
        mCoursePacketId = coursePacketInfo.mCoursePacketId;
        mCoursePacketCover = coursePacketInfo.mCoursePacketCover;
        mCoursePacketName = coursePacketInfo.mCoursePacketName;
        mCoursePacketStageNum = coursePacketInfo.mCoursePacketStageNum;//课程包阶段数量
        mCoursePacketCourseNum = coursePacketInfo.mCoursePacketCourseNum;//课程包课程数量
        mCoursePacketLearnPersonNum = coursePacketInfo.mCoursePacketLearnPersonNum;//课程包学习人数
        mCoursePacketPrice = coursePacketInfo.mCoursePacketPrice;//课程包价格
        mCoursePacketPriceOld = coursePacketInfo.mCoursePacketPriceOld;//课程包原价格
        mCoursePacketMessage = coursePacketInfo.mCoursePacketMessage;//课程包描述
        mCoursePacketDetails = coursePacketInfo.mCoursePacketDetails;//课程包详情
        mStageCourseInfoList.addAll(coursePacketInfo.mStageCourseInfoList);//课程包包含的课程阶段
        mTeacherInfoList.addAll(coursePacketInfo.mTeacherInfoList);
    }
}
