package com.android.school.info;
import com.android.school.info.CourseInfo;

import java.util.ArrayList;
import java.util.List;

//课程信息结构体
public class StageCourseInfo {
    public String mStageCourseId = "";//阶段课程id
    public String mStageCourseName = "";//阶段课程名称 名称可以为空
    public String mStageCourseIsSale = "1";//是否按阶段授课  0:true;1:false
    public String mStageCourseDescribe = "";//阶段课程描述
    public String mStageCourseProgrProgress="";
//    String mStageCourseSalePrice;//阶段授课价格
    public String mStageCourseOrder;//阶段课程排序
    public List<CourseInfo> mCourseInfoList = new ArrayList<>();//阶段课程包含哪些课程,string 为其排序
}
