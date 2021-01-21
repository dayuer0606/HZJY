package com.android.school;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.utils.DensityUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dayuer on 19/7/2.
 * 首页
 */
public class ModelHomePage extends Fragment{
    private static int FragmentPage;
    private ModelImageSlideshow mImageSlideshow;
    private boolean mIsUseImageSlideShow = true;
    private boolean mIsUseFunctionShow = true;
    private View mView = null;

    private static MainActivity mMainContext;
    private LayoutInflater inflater;
    private ViewGroup container;

    //点击查看课程详情需要用到的课程对象
    private ModelCourseCover mModelCourseCover = null;

    //数据
    private HomePageBean.HomePageDataBean mHomePageDataBean ;

    //刷新控件
    private SmartRefreshLayout mSmart_homepage_layout1 = null;

    private ListAdapter mAdapter;

    private class FunctionButtonInfo{
        String mFunctionalModule; // 1：课程包 2：公开课 3：题库 4：问答 5：课程表 6：新闻资讯 7：课程 8：我的
        String mButtonName; //按钮名称
    }
    //要显示的页面
//    private int FragmentPage;
    public  static  Fragment newInstance(MainActivity content, String context, int iFragmentPage){
        mMainContext = content;
        ModelHomePage myFragment = new ModelHomePage();
        FragmentPage = iFragmentPage;
        return  myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(FragmentPage,container,false);
        this.inflater = inflater;
        this.container = container;
        //获取首页数据
        getHomePageDatas();
        //首页展示
        HomePageShow();
        return mView;
    }

    //隐藏所有图层
    private void HideAllLayout(){
        LinearLayout homepage_layout_main = mView.findViewById(R.id.homepage_layout_main);
        homepage_layout_main.removeAllViews();
        return;
    }

    //首页展示
    public void HomePageShow(){
        if (mView == null){
            return;
        }
        HideAllLayout();
        LinearLayout homepage_layout_main = mView.findViewById(R.id.homepage_layout_main);
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.homepage_layout1, null);
        homepage_layout_main.addView(view);
        //Smart_homepage_layout1
        mSmart_homepage_layout1 = view.findViewById(R.id.Smart_homepage_layout1);
        mSmart_homepage_layout1.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //控件加载更多
                mSmart_homepage_layout1.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //控件的刷新方法
                getHomePageDatas();
            }
        });
        DisplayMetrics dm = mMainContext.getResources().getDisplayMetrics(); //获取屏幕分辨率
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        if (mIsUseImageSlideShow) {
            mImageSlideshow = view.findViewById(R.id.is_gallery);
            // 初始化轮播图数据
            initSlideImageData();
            // 为ImageSlideshow设置数据
            mImageSlideshow.setDotSpace(12);
            mImageSlideshow.setDotSize(12);
            mImageSlideshow.setDelay(3000);
            mImageSlideshow.commit();
        }
        if (mIsUseFunctionShow){
            List<Map<String, Object>> FunctionButtonInfoList = initNavigationData();
            while (FunctionButtonInfoList.size() > 8){ //功能按钮不能多于GridLayout的所创个数
                FunctionButtonInfoList.remove(FunctionButtonInfoList.size() - 1);
            }
            int size = FunctionButtonInfoList.size();
            GridView mfunctionButton = view.findViewById(R.id.functionButton);
            changeGridView(size,mfunctionButton);
            mAdapter = new SimpleAdapter(mMainContext, FunctionButtonInfoList,
                    R.layout.homepage_layout_functionbutton, new String[] {"FunctionalModule","ButtonName"}, new int[] {R.id.TextView_functionbutton,R.id.TextView_functionbuttonname});
            mfunctionButton.setAdapter(mAdapter);
            mfunctionButton.setOnItemClickListener((adapterView, view1, i, l) -> {
                TextView mFunctionalModule = view1.findViewById(R.id.TextView_functionbutton);
                if (mFunctionalModule == null) {
                    return;
                }
                //判断点击什么按钮，跳转什么功能界面1：课程包 2：公开课 3：题库 4：问答 5：课程表 6：新闻资讯 7：课程 8：我的
                if (mFunctionalModule.getText().toString().equals("课程包")){
                    mMainContext.Page_MoreCoursePacket();
                } else if (mFunctionalModule.getText().toString().equals("公开课")){
//                        mMainContext.LoginLiveOrPlayback("391068","dadada","",PlayType.LIVE);
//                        mMainContext.LoginLiveOrPlayback("365061","dadada","799723",PlayType.PLAYBACK);
                    mMainContext.Page_OpenClass();
                } else if (mFunctionalModule.getText().toString().equals("题库")){
                    mMainContext.Page_QuestionBank();
                } else if (mFunctionalModule.getText().toString().equals("问答")){
                    mMainContext.Page_CommunityAnswer();
                } else if (mFunctionalModule.getText().toString().equals("课程表")){
                    mMainContext.Page_ClassCheduleCard();
                } else if (mFunctionalModule.getText().toString().equals("新闻资讯")){
                    mMainContext.Page_News();
                } else if (mFunctionalModule.getText().toString().equals("课程")){
                    mMainContext.Page_Course();
                } else if (mFunctionalModule.getText().toString().equals("我的")){
                    mMainContext.Page_My();
                }
            });
        }
        //更多课程
        List<CourseInfo> CourseInfoList = initRecommendCourseData();
        LinearLayout courseModelLinearLayout = view.findViewById(R.id.coursemodel);
        View course_line1 = null;
        for (int i = 0; i < CourseInfoList.size(); i ++){
            CourseInfo courseInfo = CourseInfoList.get(i);
            if (courseInfo == null){
                continue;
            }
            ModelCourseCover modelCourseCover = new ModelCourseCover();
            View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext,courseInfo);
            modelCourseView.setOnClickListener(v->{ //点击某一课程
                ModelCourseCover modelCourseCover1 = new ModelCourseCover();
                View modelCourseView1 = modelCourseCover1.ModelCourseCover(mMainContext,courseInfo);
                modelCourseCover1.CourseDetailsShow();
                HideAllLayout();
                homepage_layout_main.addView(modelCourseView1);
                mMainContext.onClickCourseDetails();
                mModelCourseCover = modelCourseCover1;
            });
            courseModelLinearLayout.addView(modelCourseView);
            course_line1 = modelCourseView.findViewById(R.id.course_line1);
            Log.e("TAG", "onError: initRecommendCourseData4");
        }
        if (course_line1 != null){
            course_line1.setVisibility(View.INVISIBLE);
        }
        View courseline = view.findViewById(R.id.courseline);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) courseline.getLayoutParams();
        lp.rightMargin = width / 25;
        lp.leftMargin = width / 25;
        courseline.setLayoutParams(lp);

        //更多课程包
        List<CoursePacketInfo> CoursePacketInfoList = initRecommendCoursePacketData();
        LinearLayout coursePacketModelLinearLayout = view.findViewById(R.id.coursepacketmodel);
        for (int i = 0; i < CoursePacketInfoList.size(); i ++){
            CoursePacketInfo CoursePacketInfo = CoursePacketInfoList.get(i);
            if (CoursePacketInfo == null){
                continue;
            }
            ModelCoursePacketCover modelCoursePacketCover = new ModelCoursePacketCover();
            View modelCoursePacketView = modelCoursePacketCover.ModelCoursePacketCover(mMainContext,CoursePacketInfo);
            modelCoursePacketView.setOnClickListener(v->{ //点击某一课程包
                ModelCoursePacketCover modelCoursePacketCover1 = new ModelCoursePacketCover();
                View modelCoursePacketView1 = modelCoursePacketCover1.ModelCoursePacketCover(mMainContext,CoursePacketInfo);
                modelCoursePacketCover1.CoursePacketDetailsShow();
                HideAllLayout();
                homepage_layout_main.addView(modelCoursePacketView1);
                mMainContext.onClickCoursePacketDetails();
            });
            coursePacketModelLinearLayout.addView(modelCoursePacketView);
        }

        View line = view.findViewById(R.id.line);
        LinearLayout.LayoutParams lineRelativeLayoutLp = (LinearLayout.LayoutParams) line.getLayoutParams();
        lineRelativeLayoutLp.height = height / 11;
        line.setLayoutParams(lineRelativeLayoutLp);
        return;
    }

    /**
     * 将GridView改成单行横向布局
     */
    private void changeGridView(int size,GridView mfunctionButton) {
        // item宽度
        int itemWidth = DensityUtil.dip2px(mMainContext, 50);
        // item之间的间隔
        int itemPaddingH = DensityUtil.dip2px(mMainContext, 15);
        // 计算GridView宽度
        int gridviewWidth = size * (itemWidth + itemPaddingH);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        mfunctionButton.setLayoutParams(params);
        mfunctionButton.setColumnWidth(itemWidth);
        mfunctionButton.setHorizontalSpacing(itemPaddingH);
        mfunctionButton.setStretchMode(GridView.NO_STRETCH);
        mfunctionButton.setNumColumns(size);
    }

    /**
     * 初始化轮播图数据
     */
    private void initSlideImageData() {
        if (mHomePageDataBean == null){
            return;
        }
        if (mHomePageDataBean.rotationChartInfo == null){
            return;
        }
        //如果有url,使用远程图片
        for (int i = 0; i < mHomePageDataBean.rotationChartInfo.size(); i ++) {
            HomePageBean.HomePageRotationChartInfoBean homePageRotationChartInfoBean = mHomePageDataBean.rotationChartInfo.get(i);
            if (homePageRotationChartInfoBean == null){
                continue;
            }
//            if (!homePageRotationChartInfoBean.link_type.equals("外部链接")){//暂只处理外部链接数据
//                continue;
//            }
            if (homePageRotationChartInfoBean.server_address == null){ //没有轮播图链接
                continue;
            }
            mImageSlideshow.addImageUrlAndSkipUrl(homePageRotationChartInfoBean.link_num,homePageRotationChartInfoBean.server_address);
        }
        return;
    }

    /**
     * 初始化精钢区导航数据
     */
    private List<Map<String, Object>> initNavigationData() {
        List<Map<String, Object>> FunctionButtonInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return FunctionButtonInfoList;
        }
        if (mHomePageDataBean.allHomeNavigationAndbBottomMenu == null){
            return FunctionButtonInfoList;
        }
        for (int i = 0; i < mHomePageDataBean.allHomeNavigationAndbBottomMenu.size(); i ++) {
            HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean homePageAllHomeNavigationAndbBottomMenuBean = mHomePageDataBean.allHomeNavigationAndbBottomMenu.get(i);
            if (homePageAllHomeNavigationAndbBottomMenuBean == null){
                continue;
            }
            if (!homePageAllHomeNavigationAndbBottomMenuBean.nvigation_type.equals("首页导航")){//只查询精钢区导航按钮
                continue;
            }
            Map map = new HashMap<String, Object>();
            map.put("FunctionalModule",homePageAllHomeNavigationAndbBottomMenuBean.functional_module);
            map.put("ButtonName",homePageAllHomeNavigationAndbBottomMenuBean.custom_name);
            FunctionButtonInfoList.add(map);
        }
        return FunctionButtonInfoList;
    }

    /**
     * 初始化推荐课程数据
     */
    private List<CourseInfo> initRecommendCourseData() {
        Log.e("TAG", "onError: initRecommendCourseData");
        List<CourseInfo> CourseInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return CourseInfoList;
        }
        Log.e("TAG", "onError: initRecommendCourseData1");
        if (mHomePageDataBean.allRecommendCourseInfo == null){
            return CourseInfoList;
        }
        Log.e("TAG", "onError: initRecommendCourseData2");
        for (int i = 0; i < mHomePageDataBean.allRecommendCourseInfo.size(); i ++) {
            HomePageBean.HomePageAllRecommendCourseInfoBean homePageAllRecommendCourseInfoBean = mHomePageDataBean.allRecommendCourseInfo.get(i);
            if (homePageAllRecommendCourseInfoBean == null){
                continue;
            }
            if (homePageAllRecommendCourseInfoBean.cover == null){
                homePageAllRecommendCourseInfoBean.cover = "";
            }
            CourseInfo CourseInfo = new CourseInfo();
            CourseInfo.mCourseId = String.valueOf(homePageAllRecommendCourseInfoBean.course_id);
            CourseInfo.mCourseCover = homePageAllRecommendCourseInfoBean.cover;
            CourseInfo.mCourseLearnPersonNum = String.valueOf(homePageAllRecommendCourseInfoBean.buying_base_number);
            CourseInfo.mCourseName = homePageAllRecommendCourseInfoBean.course_name;
            CourseInfo.mCoursePrice = String.valueOf(homePageAllRecommendCourseInfoBean.special_price);
            CourseInfo.mCoursePriceOld = String.valueOf(homePageAllRecommendCourseInfoBean.price);
            CourseInfo.mCourseType = homePageAllRecommendCourseInfoBean.course_type;
            CourseInfoList.add(CourseInfo);
            Log.e("TAG", "onError: initRecommendCourseData3");
        }
        return CourseInfoList;
    }

    /**
     * 初始化推荐课程包数据
     */
    private List<CoursePacketInfo> initRecommendCoursePacketData() {
        List<CoursePacketInfo> CoursePacketInfoList = new ArrayList<>();
        if (mHomePageDataBean == null){
            return CoursePacketInfoList;
        }
        if (mHomePageDataBean.allRecommendCoursePackageInfo == null){
            return CoursePacketInfoList;
        }
        for (int i = 0; i < mHomePageDataBean.allRecommendCoursePackageInfo.size(); i ++) {
            HomePageBean.HomePageAllRecommendCoursePackageInfoBean homePageAllRecommendCoursePacketInfoBean = mHomePageDataBean.allRecommendCoursePackageInfo.get(i);
            if (homePageAllRecommendCoursePacketInfoBean == null){
                continue;
            }
            if (homePageAllRecommendCoursePacketInfoBean.cover == null){
                homePageAllRecommendCoursePacketInfoBean.cover = "";
            }
            CoursePacketInfo CoursePacketInfo = new CoursePacketInfo();
            CoursePacketInfo.mCoursePacketId = String.valueOf(homePageAllRecommendCoursePacketInfoBean.course_package_id);
            CoursePacketInfo.mCoursePacketCover = homePageAllRecommendCoursePacketInfoBean.cover;
            CoursePacketInfo.mCoursePacketLearnPersonNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.buying_base_number);
            CoursePacketInfo.mCoursePacketPriceOld = String.valueOf(homePageAllRecommendCoursePacketInfoBean.total_price);
            CoursePacketInfo.mCoursePacketName = homePageAllRecommendCoursePacketInfoBean.cp_name;
            CoursePacketInfo.mCoursePacketPrice = String.valueOf(homePageAllRecommendCoursePacketInfoBean.favorable_price);
            CoursePacketInfo.mCoursePacketStageNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.stageNum);
            CoursePacketInfo.mCoursePacketCourseNum = String.valueOf(homePageAllRecommendCoursePacketInfoBean.courseNum);
            CoursePacketInfoList.add(CoursePacketInfo);
        }
        return CoursePacketInfoList;
    }

    //轮播图排序
    public List<HomePageBean.HomePageRotationChartInfoBean> getRotationChartSortASCList(List<HomePageBean.HomePageRotationChartInfoBean> list) {
        Collections.sort(list, (o1, o2) -> Integer.valueOf(o1.getRotation_chart_sort()).compareTo(Integer.valueOf(o2.getRotation_chart_sort())));
        return list;
    }

    //首页导航和底部菜单排序
    public List<HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean> getHomeNavigationAndbBottomMenuASCList(List<HomePageBean.HomePageAllHomeNavigationAndbBottomMenuBean> list) {
        Collections.sort(list, (o1, o2) -> Integer.valueOf(o1.getNvigation_sort()).compareTo(Integer.valueOf(o2.getNvigation_sort())));
        return list;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        return;
    }
    @Override
    public void onDestroy() {
        // 释放资源
        if (mImageSlideshow != null) {
            mImageSlideshow.releaseResource();
        }
        super.onDestroy();
        return;
    }

    //获取首页数据
    private void getHomePageDatas() {
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        final Observable<HomePageBean> data = modelObservableInterface.queryHomePageInfo();
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<HomePageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomePageBean value) {
                        //网络请求数据成功
                        mHomePageDataBean = value.getData();
                        if (mHomePageDataBean != null) {
                            if (mHomePageDataBean.rotationChartInfo != null) {
                                //先将轮播图排序
                                mHomePageDataBean.rotationChartInfo = getRotationChartSortASCList(mHomePageDataBean.rotationChartInfo);
                            }
                            //先将首页导航和底部菜单排序
                            if (mHomePageDataBean.allHomeNavigationAndbBottomMenu != null) {
                                mHomePageDataBean.allHomeNavigationAndbBottomMenu = getHomeNavigationAndbBottomMenuASCList(mHomePageDataBean.allHomeNavigationAndbBottomMenu);
                            }
                        }
                        //刷新界面
                        HomePageShow();
                        //需要结束刷新头
                        if (mSmart_homepage_layout1 != null) {
                            mSmart_homepage_layout1.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        if (mHomePageDataBean != null) {
                            Log.e("TAG", "onError1: " + mHomePageDataBean.toString());
                        } else {
                            Log.e("TAG", "onError: mHomePageDataBean.toString()" + value.code );
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mMainContext,"刷新数据失败",Toast.LENGTH_LONG).show();
                        //需要结束刷新头
                        if (mSmart_homepage_layout1 != null) {
                            mSmart_homepage_layout1.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return;
    }

    public static class HomePageBean{
        private HomePageDataBean data;  //返回数据
        private int code;
//        private String errorMsg;

        public HomePageDataBean getData() {
            return data;
        }


        public void setData(HomePageDataBean data) {
            this.data = data;
            return;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
            return;
        }

//        public String getErrorMsg() {
//            return errorMsg;
//        }

        //        public void setErrorMsg(String errorMsg) {
//            this.errorMsg = errorMsg;
//        }
        public static class HomePageDataBean {
            private List<HomePageAllHomeNavigationAndbBottomMenuBean> allHomeNavigationAndbBottomMenu;  //精钢区和底部按钮
            private List<HomePageAllRecommendCoursePackageInfoBean> allRecommendCoursePackageInfo;      //推荐课程包
            private List<HomePageAllRecommendCourseInfoBean> allRecommendCourseInfo;                    //推荐课程
            private List<HomePageRotationChartInfoBean> rotationChartInfo;                              //轮播图
            public List<HomePageAllHomeNavigationAndbBottomMenuBean> getAllHomeNavigationAndbBottomMenuDatas() {
                return allHomeNavigationAndbBottomMenu;
            }
            public List<HomePageAllRecommendCoursePackageInfoBean> getAllRecommendCoursePackageDatas() {
                return allRecommendCoursePackageInfo;
            }
            public List<HomePageAllRecommendCourseInfoBean> getAllRecommendCourseInfoDatas() {
                return allRecommendCourseInfo;
            }
            public List<HomePageRotationChartInfoBean> getRotationChartInfoDatas() {
                return rotationChartInfo;
            }
            public void setAllHomeNavigationAndbBottomMenuDatas(List<HomePageAllHomeNavigationAndbBottomMenuBean> allHomeNavigationAndbBottomMenu) {
                this.allHomeNavigationAndbBottomMenu = allHomeNavigationAndbBottomMenu;
            }
            public void setAllRecommendCoursePackageDatas(List<HomePageAllRecommendCoursePackageInfoBean> allRecommendCoursePackageInfo) {
                this.allRecommendCoursePackageInfo = allRecommendCoursePackageInfo;
            }
            public void setAllRecommendCourseInfoDatas(List<HomePageAllRecommendCourseInfoBean> allRecommendCourseInfo) {
                this.allRecommendCourseInfo = allRecommendCourseInfo;
            }
            public void setRotationChartInfoDatas(List<HomePageRotationChartInfoBean> rotationChartInfo) {
                this.rotationChartInfo = rotationChartInfo;
            }
        }
        public static class HomePageAllHomeNavigationAndbBottomMenuBean{
            private int nvigation_id;           //首页导航/底部菜单id
            private String custom_name;         //名称
            private int nvigation_sort;         //顺序
            private String nvigation_type;      //类型（首页导航/底部菜单）
            private String functional_module;   //功能模块
            public int getNvigation_id(){
                return this.nvigation_id;
            }
            public void setNvigation_id(int nvigation_id){
                this.nvigation_id = nvigation_id;
            }
            public String getCustom_name(){
                return this.custom_name;
            }
            public void setCustom_name(String custom_name){
                this.custom_name = custom_name;
            }
            public int getNvigation_sort(){
                return this.nvigation_sort;
            }
            public void setNvigation_sort(int nvigation_sort){
                this.nvigation_sort = nvigation_sort;
                return;
            }
            public String getNvigation_type(){
                return this.nvigation_type;
            }
            public void setNvigation_type(String nvigation_type){
                this.nvigation_type = nvigation_type;
                return;
            }
            public String getFunctional_module(){
                return this.functional_module;
            }
            public void setFunctional_module(String functional_module){
                this.functional_module = functional_module;
                return;
            }
        }

        public static class HomePageAllRecommendCoursePackageInfoBean{
            private String cover;               //封面
            private int stageNum;               //阶段数量
            private int course_package_id;      //课程包id
            private float total_price;          //总价格
            private int courseNum;              //课程数量
            private String cp_name;             //课程包名字
            private float favorable_price;      //优惠价格
            private int buying_base_number;     //购买人数
            public String getCover(){
                return this.cover;
            }
            public void setCover(String cover){
                this.cover = cover;
            }
            public int getStageNum(){
                return this.stageNum;
            }
            public void setStageNum(int stageNum){
                this.stageNum = stageNum;
            }
            public int getCourse_package_id(){
                return this.course_package_id;
            }
            public void setCourse_package_id(int course_package_id){
                this.course_package_id = course_package_id;
            }
            public float getTotal_price(){
                return this.total_price;
            }
            public void setTotal_price(float total_price){
                this.total_price = total_price;
            }
            public int getCourseNum(){
                return this.courseNum;
            }
            public void setCourseNum(int courseNum){
                this.courseNum = courseNum;
            }
            public String getCp_name(){
                return this.cp_name;
            }
            public void setCp_name(String cp_name){
                this.cp_name = cp_name;
            }
            public float getFavorable_price(){
                return this.favorable_price;
            }
            public void setFavorable_price(float favorable_price){
                this.favorable_price = favorable_price;
            }
            public int getBuying_base_number(){
                return this.buying_base_number;
            }
            public void setBuying_base_number(int buying_base_number){
                this.buying_base_number = buying_base_number;
            }
        }
        public static class HomePageAllRecommendCourseInfoBean{
            private String cover;               //封面
            private int course_id;              //课程id
            private String course_type;         //课程类型（直播/录播/混合）
            private String course_name;         //课程名称
            private float special_price;        //优惠价格
            private float price;                //价格
            private int buying_base_number;     //学习人数
            public String getCover(){
                return this.cover;
            }
            public void setCover(String cover){
                this.cover = cover;
            }
            public int getCourse_id(){
                return this.course_id;
            }
            public void setCourse_id(int course_id){
                this.course_id = course_id;
            }
            public String getCourse_type(){
                return this.course_type;
            }
            public void setCourse_type(String course_type){
                this.course_type = course_type;
            }
            public String getCourse_name(){
                return this.course_name;
            }
            public void setCourse_name(String course_name){
                this.course_name = course_name;
            }
            public float getSpecial_price(){
                return this.special_price;
            }
            public void setSpecial_price(float special_price){
                this.special_price = special_price;
            }
            public float getPrice() {
                return price;
            }
            public void setPrice(float price){
                this.price = price;
            }
            public int getBuying_base_number(){
                return this.buying_base_number;
            }
            public void setBuying_base_number(int buying_base_number) {
                this.buying_base_number = buying_base_number;
            }
        }
        public static class HomePageRotationChartInfoBean{
            private String link_type;           //链接类型（课程/课程包/外部链接）
            private String link_num;            //链接值
            private int rotation_chart_sort;    //轮播图顺序
            private String server_address;      //服务器地址（即封面）
            private int rotation_chart_id;      //轮播图id

            public String getLink_type() {
                return link_type;
            }

            public void setLink_type(String link_type) {
                this.link_type = link_type;
            }

            public String getLink_num() {
                return link_num;
            }

            public void setLink_num(String link_num) {
                this.link_num = link_num;
            }

            public int getRotation_chart_sort() {
                return rotation_chart_sort;
            }

            public void setRotation_chart_sort(int rotation_chart_sort) {
                this.rotation_chart_sort = rotation_chart_sort;
            }

            public String getServer_address() {
                return server_address;
            }

            public void setServer_address(String server_address) {
                this.server_address = server_address;
            }

            public int getRotation_chart_id() {
                return rotation_chart_id;
            }

            public void setRotation_chart_id(int rotation_chart_id) {
                this.rotation_chart_id = rotation_chart_id;
            }
        }
    }
}