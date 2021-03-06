package com.android.weischool;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.view.download.DownloadView;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.android.weischool.adapter.CommonListAdapter;
import com.android.weischool.appactivity.ModelCommunityAnswerActivity;
import com.android.weischool.classpacket.ClassPacketDetails;
import com.android.weischool.info.CourseInfo;
import com.android.weischool.info.CoursePacketInfo;
import com.android.weischool.info.CourseRecordPlayDownloadInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * ????????????
 */
public class ModelMy extends Fragment implements ModelOrderDetailsInterface{
    private static MainActivity mMainContext;
    private static String mContext = "xxxxxxxxxxxxx";
    //??????????????????
    static private int FragmentPage;
    private View mview, mMyView, mMyClassView, mMyClassPacketView, mMyCollectView, mMyCacheView, mMyCacheManagementCacheView,
            mMyOrderView, mMyOrderDetailsView, mMyCouponView, mMyMessageView, mMyMessageView0, mMyAnswerView, mMyAnswerDetailsView,
            mAnswerDetailsView,mLearnRecordView;
    private int width = 720;
    //????????????????????????tab??????????????????
    private int mMyCollectLastTabIndex = 1;
    private String mMyCollectCurrentTab = "course";
    //????????????????????????tab??????????????????
    private int mMyOrderLastTabIndex = 1;
    private String mMyOrderCurrentTab = "all";
    //???????????????????????????tab?????????????????????
    private int mMyCouponLastTabIndex = 1;
    private String mMyCouponCurrentTab = "notused";
    //????????????????????????tab????????????????????????
    private int mMyAnswerLastTabIndex = 1;
    private String mMyAnswerCurrentTab = "question";
    //????????????????????????tab??????????????????
    private int mMyRecordsLastTabIndex = 1;
    private String mMyRecordsCurrentTab = "class";
    private CommonListAdapter<Object> mLearnCourseAdapter;

    private ControllerCenterDialog mMyDialog, mMyCouponDialog;
    private ControllerMyMessage1Adapter adapter;
    private static final String TAG = "ModelMy";
    boolean m_isFind = false;  //????????????????????????????????????
    //?????????????????????
    List<ControllerMyMessage1Adapter.MyMessageInfo> list = new ArrayList<>();
    //????????????????????????
    private PersonalInfoBean.PersonalInfoDataBean mPersonalInfoDataBean;

    private Map<String, CourseRecordPlayDownloadInfo> mCourseRecordPlayDownloadInfoMap = new HashMap<>();
    private SmartRefreshLayout mSmart_model_my_myclass;
    private SmartRefreshLayout mSmart_model_my_myclasspacket;
    private SmartRefreshLayout mSmart_model_my_myorder;
//    private String product_name;
//    private String order_status;
//    private String order_num;
//    private int product_price;
    private SmartRefreshLayout mSmart_model_my_mycollect,mSmart_model_my_myorderdetails,mSmart_model_my_mymessage,mSmart_model_my_myanswer,mSmart_model_my_learnrecord,mSmart_model_my_myanswerdetails,mSmart_model_my_mycoupon;
    //??????????????????????????????
    private int mMyCourseCurrentPage = 0;
    private int mMyCoursePageCount = 10;
    private int mMyCourseSum = 0; //??????????????????
    //?????????????????????????????????
    private int mMyCoursePacketCurrentPage = 0;
    private int mMyCoursePacketPageCount = 10;
    private int mMyCoursePacketSum = 0; //??????????????????
    //??????????????????????????????
    private int mMyCollectCurrentPage = 0;
    private int mMyCollectPageCount = 10;
    private int mMyCollectSum = 0; //????????????????????????
    //???????????????????????????????????????
    private int mMyCollectPacketCurrentPage = 0;
    private int mMyCollectPacketPageCount = 10;
    private int mMyCollectPacketSum = 0; //???????????????????????????
    //??????????????????????????????
    private int mMyOrderCurrentPage = 0;
    private int mMyOrderPageCount = 10;
    private int mMyOrderSum = 0; //??????????????????
    //?????????????????????????????????
    private int mMyCouponCurrentPage = 0;
    private int mMyCouponPageCount = 10;
    private int mMyCouponSum = 0; //??????????????????
    //??????????????????????????????
    private int mMyMessageCurrentPage = 0;
    private int mMyMessagePageCount = 10;
    private int mMyMessageSum = 0; //??????????????????
    private int mMyMessageType = 3; //?????????3  1??????????????? 2???????????????
    //??????????????????????????????
    private int mMyQuestionAndAnswerCurrentPage = 0;
    private int mMyQuestionAndAnswerPageCount = 10;
    private int mMyQuestionAndAnswerSum = 0; //??????????????????
    //??????????????????????????????
    private int mMyQuestionAndAnswerDetailsCurrentPage = 0;
    private int mMyQuestionAndAnswerDetailsPageCount = 10;
    private int mMyQuestionAndAnswerDetailsSum = 0; //????????????????????????
    private Integer mAnswerDetailsQuestionId = null;
    private Integer mAnswerDetailsAnswerId = null;
    //??????????????????????????????
    private int mMyRecordsCurrentPage = 0;
    private int mMyRecordsPageCount = 10;
    private int mMyRecordsSum = 0; //??????

    public static Fragment newInstance(MainActivity content, String context, int iFragmentPage) {
        mContext = context;
        mMainContext = content;
        ModelMy myFragment = new ModelMy();
        FragmentPage = iFragmentPage;
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(FragmentPage, container, false);
        getPersonalInfoDatas(); // ??????????????????????????????????????????
        ModelMyInit();    //??????????????????
        return mview;
    }

    //??????????????????
    private void HideAllLayout() {
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        my_layout_main.removeAllViews();
    }

    //?????????????????????
    public void ModelMyInit() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyView == null) {
            mMyView = LayoutInflater.from(mMainContext).inflate(R.layout.my_layout_main, null);
            TextView username = mMyView.findViewById(R.id.username);
            username.setOnClickListener(v -> {
                if (mPersonalInfoDataBean == null) {
                    mMainContext.onClickImmediatelyLogin("login");
                } else {
                    mMainContext.onClickImmediatelyLogin("personinfo");
                }
            });
        }
        my_layout_main.addView(mMyView);
        DisplayMetrics dm = mMainContext.getResources().getDisplayMetrics(); //?????????????????????
        width = dm.widthPixels;
        //??????????????????
        ControllerCustomRoundAngleImageView headportraitImageView = mMyView.findViewById(R.id.headportrait);
        if (mPersonalInfoDataBean == null) {
            Glide.with(mMainContext).load("").listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                    return false;
                }
            }).error(mMainContext.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(headportraitImageView);
            TextView username = mMyView.findViewById(R.id.username);
            username.setText(mMyView.getResources().getString(R.string.title_loginimmediately));
            TextView userinfo = mMyView.findViewById(R.id.userinfo);
            userinfo.setText(mMyView.getResources().getString(R.string.title_personalstatement));
        } else {
            if (mPersonalInfoDataBean.stu_name != null) {  //??????????????????
                TextView username = mMyView.findViewById(R.id.username);
                username.setText(mPersonalInfoDataBean.stu_name);
            } else {
                TextView username = mMyView.findViewById(R.id.username);
                username.setText("");
            }
            if (mPersonalInfoDataBean.autograph != null) {  //????????????????????????
                TextView userinfo = mMyView.findViewById(R.id.userinfo);
                userinfo.setText(mPersonalInfoDataBean.autograph);
            }
            Glide.with(mMainContext).load(mPersonalInfoDataBean.head).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                    return false;
                }
            }).error(mMainContext.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(headportraitImageView);
        }

        getQueryMyPageNum();
        getQueryMyMsg();
    }

    //????????????????????????
    public void MyClassShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        //??????????????????????????????
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyClassView == null) {
            mMyClassView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclass, null);
            //Smart_model_my_myclass     ??????????????????

            mSmart_model_my_myclass = mMyClassView.findViewById(R.id.Smart_model_my_myclass);
            mSmart_model_my_myclass.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //???????????????????????????????????????
                    if (mMyCourseSum <= mMyCourseCurrentPage * mMyCoursePageCount){
                        LinearLayout mycourse_end = mMyClassView.findViewById(R.id.mycourse_end);
                        mycourse_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_myclass.finishLoadMore();
                        return;
                    }
                    getMyCourseListMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //????????????????????????
                    getMyCourseList();
                }
            });
        }
        my_layout_main.addView(mMyClassView);
        //??????????????????
        TextView modelmy_myclass_main_titletext = mMyClassView.findViewById(R.id.modelmy_myclass_main_titletext);
        modelmy_myclass_main_titletext.setText("????????????");
        //????????????????????????
        getMyCourseList();
    }

    //???????????????????????????
    public void MyClassPacketShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyClassPacketView == null) {
            mMyClassPacketView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclasspacket, null);
            //Smart_model_my_myclasspacket  ??????
            mSmart_model_my_myclasspacket = mMyClassPacketView.findViewById(R.id.Smart_model_my_myclasspacket);
            mSmart_model_my_myclasspacket.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //???????????????????????????????????????
                    if (mMyCoursePacketSum <= mMyCoursePacketCurrentPage * mMyCoursePacketPageCount){
                        LinearLayout mycoursepacket_end = mMyClassPacketView.findViewById(R.id.mycoursepacket_end);
                        mycoursepacket_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_myclasspacket.finishLoadMore();
                        return;
                    }
                    getMyPacketListMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getMyPacketList();
                }
            });
        }
        my_layout_main.addView(mMyClassPacketView);
        //????????????????????????
        getMyPacketList();
    }

    //????????????????????????
    public void MyCollectShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCollectView == null) {
            mMyCollectView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycollect, null);
            //Smart_model_my_mycollect
            mSmart_model_my_mycollect = mMyCollectView.findViewById(R.id.Smart_model_my_mycollect);
            mSmart_model_my_mycollect.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //???????????????????????????????????????
                    if (mMyCollectCurrentTab.equals("course")) {
                        if (mMyCollectSum <= mMyCollectCurrentPage * mMyCollectPageCount){
                            LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                            mycollect_end.setVisibility(View.VISIBLE);
                            mSmart_model_my_mycollect.finishLoadMore();
                            return;
                        }
                        //??????????????????  ????????????
                        getModelMyClassCollectionMore();
                    } else if (mMyCollectCurrentTab.equals("coursepacket")) {
                        if (mMyCollectPacketSum <= mMyCollectPacketCurrentPage * mMyCollectPacketPageCount){
                            LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                            mycollect_end.setVisibility(View.VISIBLE);
                            return;
                        }
                        //??????????????????  ????????????
                        getModelMyClassPacketCollectionMore();
                    } else if (mMyCollectCurrentTab.equals("question")) {
                        if (mMyCollectPacketSum <= mMyCollectPacketCurrentPage * mMyCollectPacketPageCount){
                            LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                            mycollect_end.setVisibility(View.VISIBLE);
                            return;
                        }
                        //??????????????????  ????????????
                        getModelMyAnswerCollectionMore();
                    }
                }
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
                    mycollect_end.setVisibility(View.INVISIBLE);
                    if (mMyCollectCurrentTab.equals("course")) {
                        //??????????????????  ????????????
                        getModelMyClassCollection();
                    } else if (mMyCollectCurrentTab.equals("coursepacket")) {
                        //??????????????????  ????????????
                        getModelMyClassPacketCollection();
                    } else if (mMyCollectCurrentTab.equals("question")) {
                        //??????????????????  ????????????
                        getModelMyAnswerCollection();
                    }
                }
            });
            //??????????????????
            TextView modelmy_mycollect_tab_course = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_course);
            modelmy_mycollect_tab_course.setOnClickListener(v -> {
                if (!mMyCollectCurrentTab.equals("course")) {
                    ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
                    Animation animation = new TranslateAnimation((mMyCollectLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycollect_cursor1.startAnimation(animation);
                    TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
                    modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    TextView modelmy_mycollect_tab_question = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_question);
                    modelmy_mycollect_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyCollectLastTabIndex = 1;
                mMyCollectCurrentTab = "course";
                //????????????????????????????????????????????????
                getModelMyClassCollection();
            });
            //?????????
            TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
            modelmy_mycollect_tab_coursepacket.setOnClickListener(v -> {
                if (!mMyCollectCurrentTab.equals("coursepacket")) {
                    ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
                    Animation animation = new TranslateAnimation((mMyCollectLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycollect_cursor1.startAnimation(animation);
                    modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    TextView modelmy_mycollect_tab_question = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_question);
                    modelmy_mycollect_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyCollectLastTabIndex = 2;
                mMyCollectCurrentTab = "coursepacket";
                getModelMyClassPacketCollection();
            });
            //??????
            TextView modelmy_mycollect_tab_question = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_question);
            modelmy_mycollect_tab_question.setOnClickListener(v -> {
                if (!mMyCollectCurrentTab.equals("question")) {
                    ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
                    Animation animation = new TranslateAnimation((mMyCollectLastTabIndex - 1) * width / 3, width *2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycollect_cursor1.startAnimation(animation);
                    modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycollect_tab_question.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyCollectLastTabIndex = 3;
                mMyCollectCurrentTab = "question";
                getModelMyAnswerCollection();
            });
        }
        my_layout_main.addView(mMyCollectView);
        ImageView modelmy_mycollect_cursor1 = mMyCollectView.findViewById(R.id.modelmy_mycollect_cursor1);
        int x = width / 6 - mMyCollectView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_mycollect_cursor1.setX(x);
        //????????????????????????
        mMyCollectLastTabIndex = 1;
        mMyCollectCurrentTab = "course";
        TextView modelmy_mycollect_tab_course = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_course);
        TextView modelmy_mycollect_tab_coursepacket = mMyCollectView.findViewById(R.id.modelmy_mycollect_tab_coursepacket);
        modelmy_mycollect_tab_course.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        modelmy_mycollect_tab_coursepacket.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCollectView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        LinearLayout mycollect_end = mMyCollectView.findViewById(R.id.mycollect_end);
        mycollect_end.setVisibility(View.INVISIBLE);
        //??????????????????  ????????????
        getModelMyClassCollection();
    }

    //????????????????????????-??????
    private void MyCollectShow_MyCourse(LinearLayout modelmy_mycollect_main_content,QueryMyCollectionListBean.DataBean.ListBean listBean) {
        //?????????????????????????????????
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclass1, null);
        //????????????
        TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
        modelmy_myclass1_classname.setText(listBean.course_name);
        //stuNum
        TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
        modelmy_myclass1_state.setText(listBean.stuNum + "");
        modelmy_mycollect_main_content.addView(view);
        //?????????????????????????????????????????????????????????
        TextView modelmy_myclass1_golearn = view.findViewById(R.id.modelmy_myclass1_golearn);
        modelmy_myclass1_golearn.setOnClickListener(v -> {
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setmCourseId(String.valueOf(listBean.course_id));
            courseInfo.setmCourseCover(listBean.cover);
            courseInfo.setmCourseType(listBean.course_type);
            courseInfo.setmCourseName(listBean.course_name);
            courseInfo.setmCoursePriceOld(String.valueOf(listBean.price));
            courseInfo.setmCoursePrice(String.valueOf(listBean.special_price)) ;
            courseInfo.setmCourseLearnPersonNum(String.valueOf(listBean.stuNum));
            //??????????????????
            ModelCourseCover modelCourseCover = new ModelCourseCover();
            View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext, courseInfo);
            modelCourseCover.CourseDetailsShow();
            HideAllLayout();
            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
            my_layout_main.addView(modelCourseView);
            mMainContext.onClickCourseDetails();
        });

        return ;
    }

    //????????????????????????-?????????
    private View MyCollectShow_MyCoursePacket(LinearLayout modelmy_mycollect_main_content,QueryMyCollectionPacketListBean.DataBean.ListBean listBean) {
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclasspacket1, null);
        //????????????????????????
        ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
        Glide.with(mMainContext).load(listBean.cover).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                return false;
            }
            @Override
            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                return false;
            }
        }).error(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails)).into(modelmy_myclasspacket1_cover);
        //???????????????
        TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
        modelmy_myclasspacket1_classname.setText(listBean.cp_name);
        //????????????
        TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
        modelmy_myclasspacket1_learnpersoncount.setText(listBean.stuNum + "");
        modelmy_mycollect_main_content.addView(view);
        TextView modelmy_myclasspacket1_golearn = view.findViewById(R.id.modelmy_myclasspacket1_golearn);
        //??????????????????
        modelmy_myclasspacket1_golearn.setOnClickListener(v -> {
            CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
            coursePacketInfo.mCoursePacketId = String.valueOf(listBean.course_package_id);//?????????id
            coursePacketInfo.mCoursePacketCover = listBean.cover;
            coursePacketInfo.mCoursePacketStageNum = String.valueOf(listBean.stageNum);
            coursePacketInfo.mCoursePacketName = listBean.cp_name;   //??????
            coursePacketInfo.mCoursePacketPrice = String.valueOf(listBean.favorable_price);//?????????
            coursePacketInfo.mCoursePacketCourseNum = String.valueOf(listBean.courseNum);
            coursePacketInfo.mCoursePacketPriceOld = String.valueOf(listBean.total_price);//?????????????????????
            coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(listBean.stuNum);//????????????
            //?????????????????????
            ClassPacketDetails ClassPacketDetails = new ClassPacketDetails();
            View ClassPacketView = ClassPacketDetails.ClassPacketDetails(mMainContext, coursePacketInfo);
            ClassPacketDetails.CoursePacketDetailsShow();
            HideAllLayout();
            LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
            my_layout_main.addView(ClassPacketView);
            mMainContext.onClickCoursePacketDetails();
        });
        //???????????????????????????????????????????????????????????????????????????????????????
        LinearLayout modelmy_myclasspacket1_learnprogresslayout = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresslayout);
        modelmy_myclasspacket1_learnprogresslayout.setVisibility(View.INVISIBLE);
        //??????????????????????????????
        LinearLayout modelmy_myclasspacket1_price_layout = view.findViewById(R.id.modelmy_myclasspacket1_price_layout);
        modelmy_myclasspacket1_price_layout.setVisibility(View.VISIBLE);
        //??????????????????
        View modelmy_myclasspacket1_line1 = view.findViewById(R.id.modelmy_myclasspacket1_line1);
        return modelmy_myclasspacket1_line1;
    }

    //????????????????????????-??????
    private View MyCollectShow_MyAnswer(LinearLayout modelmy_mycollect_main_content,Map<String,Object> map) {
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myquestion_layout, null);
        modelmy_mycollect_main_content.addView(view);
        TextView model_my_myquestion_content = view.findViewById(R.id.model_my_myquestion_content);
        model_my_myquestion_content.setText(map.get("title").toString());
        model_my_myquestion_content.setHint(map.get("questions_id") + "");
        //??????????????????
        View model_my_myquestion_line = view.findViewById(R.id.model_my_myquestion_line);
        return model_my_myquestion_line;
    }

    //????????????????????????
    public void MyCacheShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheView == null) {
            mMyCacheView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycache, null);
            //Smart_model_my_mycache  ??????????????????
//            mSmart_model_my_mycache = mMyCacheView.findViewById(R.id.Smart_model_my_mycache);
//            mSmart_model_my_mycache.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//                @Override
//                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                    mSmart_model_my_mycache.finishLoadMore();
//                }
//
//                @Override
//                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                    mSmart_model_my_mycache.finishRefresh();
//                }
//            });
        }
        my_layout_main.addView(mMyCacheView);
        //???????????????????????????
        LinearLayout modelmy_mycache_main_content = mMyCacheView.findViewById(R.id.modelmy_mycache_main_content);
        modelmy_mycache_main_content.removeAllViews();
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycache1, null);
        modelmy_mycache_main_content.addView(view);
        ControllerCustomRoundAngleImageView modelmy_mycache1_cover = view.findViewById(R.id.modelmy_mycache1_cover);
        Glide.with(mMainContext).load("").listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                return false;
            }

            @Override
            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                return false;
            }
        }).error(mMainContext.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_mycache1_cover);

        //????????????????????????????????????????????????????????????????????????
        LinearLayout modelmy_mycache1_classname_layout = view.findViewById(R.id.modelmy_mycache1_classname_layout);
        modelmy_mycache1_classname_layout.setOnClickListener(v -> {
            MyCache_ManagementCacheShow();
        });
        //??????????????????
        View modelmy_mycache1_line1 = view.findViewById(R.id.modelmy_mycache1_line1);
        modelmy_mycache1_line1.setVisibility(View.INVISIBLE);
    }

    //????????????????????????
    public void MyCacheShow(DownloadView downloadView) {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheView == null) {
            mMyCacheView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycache, null);
        }
        RelativeLayout modelmy_mycache_title = mMyCacheView.findViewById(R.id.modelmy_mycache_title);
        modelmy_mycache_title.setVisibility(View.VISIBLE);
        RelativeLayout modelmy_mycache_aliyunVodPlayerView = mMyCacheView.findViewById(R.id.modelmy_mycache_aliyunVodPlayerView);
        modelmy_mycache_aliyunVodPlayerView.removeAllViews();
        modelmy_mycache_aliyunVodPlayerView.setVisibility(View.INVISIBLE);
        my_layout_main.addView(mMyCacheView);
        if (downloadView.getParent() != null){
            LinearLayout parentView = (LinearLayout) downloadView.getParent();
            parentView.removeAllViews();
        }
        //???????????????????????????
        LinearLayout modelmy_mycache_main_content = mMyCacheView.findViewById(R.id.modelmy_mycache_main_content);
        modelmy_mycache_main_content.removeAllViews();
        if (downloadView != null){
            modelmy_mycache_main_content.addView(downloadView);
//            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) downloadView.getLayoutParams();
//            ll.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
//            downloadView.setLayoutParams(ll);
            downloadView.showDownloadContentView();
//        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycache1, null)
        };
//        modelmy_mycache_main_content.addView(view);
//        ControllerCustomRoundAngleImageView modelmy_mycache1_cover = view.findViewById(R.id.modelmy_mycache1_cover);
//        Glide.with(mMainContext).load("").listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
//                return false;
//            }
//        }).error(mMainContext.getResources().getDrawable(R.drawable.modelcoursecover)).into(modelmy_mycache1_cover);
//
//        //????????????????????????????????????????????????????????????????????????
//        LinearLayout modelmy_mycache1_classname_layout = view.findViewById(R.id.modelmy_mycache1_classname_layout);
//        modelmy_mycache1_classname_layout.setOnClickListener(v -> {
//            MyCache_ManagementCacheShow();
//        });
//        //??????????????????
//        View modelmy_mycache1_line1 = view.findViewById(R.id.modelmy_mycache1_line1);
//        modelmy_mycache1_line1.setVisibility(View.INVISIBLE);
    }

    public boolean MyCacheShow_Play(){
        if (mMyCacheView == null){
            return false;
        }
        RelativeLayout modelmy_mycache_title = mMyCacheView.findViewById(R.id.modelmy_mycache_title);
        modelmy_mycache_title.setVisibility(View.INVISIBLE);
        RelativeLayout modelmy_mycache_aliyunVodPlayerView = mMyCacheView.findViewById(R.id.modelmy_mycache_aliyunVodPlayerView);
        if (modelmy_mycache_aliyunVodPlayerView.getVisibility() == View.INVISIBLE){
            modelmy_mycache_aliyunVodPlayerView.removeAllViews();
            AliyunVodPlayerView aliyunVodPlayerView = new AliyunVodPlayerView(mMainContext);
            modelmy_mycache_aliyunVodPlayerView.addView(aliyunVodPlayerView);
            modelmy_mycache_aliyunVodPlayerView.setVisibility(View.VISIBLE);
//        modelmy_mycache_aliyunVodPlayerView.VideoIdSet();
            mMainContext.setmAliyunVodPlayerView(aliyunVodPlayerView);
        }
        return true;
    }
    //??????????????????-??????????????????
    public void MyCache_ManagementCacheShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCacheManagementCacheView == null) {
            mMyCacheManagementCacheView = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_download_manager, null);
        }
        my_layout_main.addView(mMyCacheManagementCacheView);
        
        TextView course_downloadmanager_layout_titletext = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_titletext);
        course_downloadmanager_layout_titletext.setText("????????????");
        int count = 0;
        LinearLayout course_downloadmanager_layout_content = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_content);
        course_downloadmanager_layout_content.removeAllViews();
        TextView course_downloadmanager_num = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_num);
        course_downloadmanager_num.setText("0");
        TextView course_downloadmanager_sumnum = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_sumnum);
        course_downloadmanager_sumnum.setText("/" + count);
        //??????????????????????????????
        TextView course_downloadmanager_availalesize = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_availalesize);
        long size = getAvailaleSize();
        course_downloadmanager_availalesize.setText("???????????????" + size + "G");
        ImageView course_downloadmanager_layout_return_button1 = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_return_button1);
        course_downloadmanager_layout_return_button1.setOnClickListener(v -> { //???????????????????????????
            MyCacheShow();
        });
        TextView course_downloadmanager_all = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_all);
        course_downloadmanager_all.setOnClickListener(v -> { //??????????????????
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_function = childView.findViewById(R.id.course_downloadmanager_child1_function);
                    TextView course_downloadmanager_child_state = childView.findViewById(R.id.course_downloadmanager_child_state);
                    ProgressBar progress_bar_healthy = childView.findViewById(R.id.progress_bar_healthy);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
                    if (id == R.drawable.button_pause_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_play_blue);
                        course_downloadmanager_child_state.setText("?????????");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg1));
                    }
                }
            }

        });
        TextView course_downloadmanager_startall = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_startall);
        course_downloadmanager_startall.setOnClickListener(v -> { //??????????????????
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_function = childView.findViewById(R.id.course_downloadmanager_child1_function);
                    TextView course_downloadmanager_child_state = childView.findViewById(R.id.course_downloadmanager_child_state);
                    ProgressBar progress_bar_healthy = childView.findViewById(R.id.progress_bar_healthy);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
                    if (id == R.drawable.button_play_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_pause_blue);
                        course_downloadmanager_child_state.setText("?????????");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg));
                    }
                }
            }
        });
        TextView course_downloadmanager_layout_edit = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_layout_edit);
        LinearLayout course_downloadmanager_function = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_function);
        LinearLayout course_downloadmanager_function1 = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_function1);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
        ll.height = mMyCacheManagementCacheView.getResources().getDimensionPixelSize(R.dimen.dp40);
        course_downloadmanager_function.setLayoutParams(ll);
        ll = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
        ll.height = 0;
        course_downloadmanager_function1.setLayoutParams(ll);
        //??????
        course_downloadmanager_layout_edit.setText("??????");
        course_downloadmanager_layout_edit.setOnClickListener(v -> {
            if (course_downloadmanager_layout_edit.getText().toString().equals("??????")) { //?????????????????????
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
                llp.height = 0;
                course_downloadmanager_function.setLayoutParams(llp);
                llp = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
                llp.height = mMyCacheManagementCacheView.getResources().getDimensionPixelSize(R.dimen.dp40);
                course_downloadmanager_function1.setLayoutParams(llp);
                course_downloadmanager_layout_edit.setText("??????");
                int num = course_downloadmanager_layout_content.getChildCount();
                for (int i = 0; i < num; i++) {
                    View view = course_downloadmanager_layout_content.getChildAt(i);
                    LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                    int childCount = course_downloadmanager_child_content.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        View childView = course_downloadmanager_child_content.getChildAt(j);
                        ImageView course_downloadmanager_child1_select = childView.findViewById(R.id.course_downloadmanager_child1_select);
                        LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) course_downloadmanager_child1_select.getLayoutParams();
                        LL.width = childView.getResources().getDimensionPixelSize(R.dimen.dp20);
                        LL.leftMargin = childView.getResources().getDimensionPixelSize(R.dimen.dp13);
                        course_downloadmanager_child1_select.setLayoutParams(LL);
                        course_downloadmanager_child1_select.setOnClickListener(View -> {
                            int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                            if (id == R.drawable.button_select_gray) {
                                course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_red);
                            } else {
                                course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_gray);
                            }
                        });
                    }
                }
            } else if (course_downloadmanager_layout_edit.getText().toString().equals("??????")) {  //???????????????????????????
                MyCache_ManagementCacheShow();
            }
        });
        //????????????
        TextView course_downloadmanager_allselect = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_allselect);
        course_downloadmanager_allselect.setOnClickListener(v -> {
            int num = course_downloadmanager_layout_content.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = course_downloadmanager_layout_content.getChildAt(i);
                LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
                int childCount = course_downloadmanager_child_content.getChildCount();
                for (int j = 0; j < childCount; j++) {
                    View childView = course_downloadmanager_child_content.getChildAt(j);
                    ImageView course_downloadmanager_child1_select = childView.findViewById(R.id.course_downloadmanager_child1_select);
                    int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                    if (id == R.drawable.button_select_gray) {
                        course_downloadmanager_child1_select.setBackgroundResource(R.drawable.button_select_red);
                    }
                }
            }
        });
        //??????
        TextView course_downloadmanager_delete = mMyCacheManagementCacheView.findViewById(R.id.course_downloadmanager_delete);
        course_downloadmanager_delete.setOnClickListener(v -> {
            View view = mMainContext.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            mMyDialog = new ControllerCenterDialog(mMainContext, 0, 0, view, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view.findViewById(R.id.tip);
            tip.setText("??????????????????");
            TextView dialog_content = view.findViewById(R.id.dialog_content);
            dialog_content.setText("??????????????????????????????");
            TextView button_cancel = view.findViewById(R.id.button_cancel);
            button_cancel.setText("??????");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            TextView button_sure = view.findViewById(R.id.button_sure);
            button_sure.setText("??????");
            button_sure.setOnClickListener(View -> {
                int num = course_downloadmanager_layout_content.getChildCount();
                for (int i = 0; i < num; i++) {
                    View childView = course_downloadmanager_layout_content.getChildAt(i);
                    LinearLayout course_downloadmanager_child_content = childView.findViewById(R.id.course_downloadmanager_child_content);
                    int childCount = course_downloadmanager_child_content.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        View childMyOrderShow_MyOrder_view2 = course_downloadmanager_child_content.getChildAt(j);
                        ImageView course_downloadmanager_child1_select = childMyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_select);
                        int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                        if (id == R.drawable.button_select_red) {//????????????????????????????????????
                            TextView course_downloadmanager_child1_name = childMyOrderShow_MyOrder_view2.findViewById(R.id.course_downloadmanager_child1_name);
                            if (this.mCourseRecordPlayDownloadInfoMap.get(course_downloadmanager_child1_name.getHint().toString()) != null) {
                                this.mCourseRecordPlayDownloadInfoMap.remove(course_downloadmanager_child1_name.getHint().toString());
                            }
                        }
                    }
                }
                mMyDialog.cancel();
                MyCache_ManagementCacheShow();
            });
        });
    }

    //????????????????????????
    private long getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory(); //??????sdcard????????????
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024 / 1024;

        //(availableBlocks * blockSize)/1024      KIB ??????

        //(availableBlocks * blockSize)/1024 /1024  MIB??????
    }

    private static int getV7ImageResourceId(ImageView imageView) {
        int imgid = 0;
        Field[] fields = imageView.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals("mBackgroundTintHelper")) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(imageView);
                    Field[] fields2 = obj.getClass().getDeclaredFields();
                    for (Field f2 : fields2) {
                        if (f2.getName().equals("mBackgroundResId")) {
                            f2.setAccessible(true);
                            imgid = f2.getInt(obj);
                            Log.d("1111", "Image ResourceId:" + imgid);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgid;
    }

    //????????????????????????
    public void MyOrderShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyOrderView == null) {
            mMyOrderView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorder, null);
            //Smart_model_my_myorder   ????????????
            mSmart_model_my_myorder = mMyOrderView.findViewById(R.id.Smart_model_my_myorder);
            mSmart_model_my_myorder.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyOrderSum <= mMyOrderCurrentPage * mMyOrderPageCount){
                        LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                        myorder_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_myorder.finishLoadMore();
                        return;
                    }
                    if (mMyOrderCurrentTab.equals("all")){
                        getModelMyOrderListMore("??????");
                    } else if (mMyOrderCurrentTab.equals("finished")){
                        getModelMyOrderListMore("?????????");
                    } else if (mMyOrderCurrentTab.equals("unfinish")){
                        getModelMyOrderListMore("?????????");
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                    myorder_end.setVisibility(View.INVISIBLE);
                    if (mMyOrderCurrentTab.equals("all")){
                        getModelMyOrderList("??????");
                    } else if (mMyOrderCurrentTab.equals("finished")){
                        getModelMyOrderList("?????????");
                    } else if (mMyOrderCurrentTab.equals("unfinish")){
                        getModelMyOrderList("?????????");
                    }
                }
            });
            //??????
            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
            modelmy_myorder_tab_all.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("all")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                    TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyOrderLastTabIndex = 1;
                mMyOrderCurrentTab = "all";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //????????????????????????????????????????????????
                getModelMyOrderList("??????");
            });
            //?????????
            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
            modelmy_myorder_tab_finished.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("finished")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyOrderLastTabIndex = 2;
                mMyOrderCurrentTab = "finished";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //????????????????????????????????????????????????
                getModelMyOrderList("?????????");
            });
            //?????????
            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
            modelmy_myorder_tab_unfinish.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("unfinish")) {
                    ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
                    Animation animation = new TranslateAnimation((mMyOrderLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_myorder_cursor1.startAnimation(animation);
                    modelmy_myorder_tab_unfinish.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_myorder_tab_all.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_myorder_tab_finished.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyOrderView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyOrderLastTabIndex = 3;
                mMyOrderCurrentTab = "unfinish";
                LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
                modelmy_myorder_main_content.removeAllViews();
                LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
                myorder_end.setVisibility(View.INVISIBLE);
                //????????????????????????????????????????????????
                getModelMyOrderList("?????????");
            });
        }
        ImageView modelmy_myorder_cursor1 = mMyOrderView.findViewById(R.id.modelmy_myorder_cursor1);
        int x = width / 6 - mMyOrderView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_myorder_cursor1.setX(x);
        //????????????????????????
        mMyOrderLastTabIndex = 1;
        mMyOrderCurrentTab = "all";
        my_layout_main.addView(mMyOrderView);
        LinearLayout myorder_end = mMyOrderView.findViewById(R.id.myorder_end);
        myorder_end.setVisibility(View.INVISIBLE);
        //????????????????????????????????????????????????
        getModelMyOrderList("??????");
    }

    //??????????????????-?????????
    private void MyOrderShow_MyOrder(LinearLayout modelmy_myorder_main_content,MyOrderlistBean.DataBean.ListBean listBean) {
        Date date = null;
        String invalid_date_date = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = df.parse(listBean.order_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = null;
            try {
                date1 = df1.parse(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date1 != null) {
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                invalid_date_date = df2.format(date1);
            }
        }
        //MyOrderShow_MyOrder_view3?????????????????????????????????????????????????????????
        if (listBean.order_status.equals("?????????")) {
            //???????????????????????????
            //?????????
            View MyOrderShow_MyOrder_view1 = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername1.setText(listBean.product_name);
            TextView modelmy_myorder1_orderdate = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderdate);
            modelmy_myorder1_orderdate.setText(invalid_date_date);
            TextView modelmy_myorder1_orderstate1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate1.setText("????????????????????????");
            ImageView modelmy_myorder1_orderimage = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderimage);
            modelmy_myorder1_orderimage.setBackground(getResources().getDrawable(R.drawable.my_order_pay_fail));
            TextView modelmy_myorder1_ordernumber1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber1.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney1.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1); //?????????
            //???????????????????????????????????????
            ImageView modelmy_myorder1_cancel = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_cancel);
            ImageView modelmy_myorder1_retrypay = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_retrypay);
            modelmy_myorder1_cancel.setOnClickListener(v -> {
                getCancelOrder(MyOrderShow_MyOrder_view1,listBean.order_id);
            });
            modelmy_myorder1_retrypay.setOnClickListener(v -> {
                HideAllLayout();
                View orderDetails = mMainContext.Page_OrderDetails(this,null,null, listBean);
                LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                my_layout_main.addView(orderDetails);
            });
        } else if (listBean.order_status.equals("????????????") || listBean.order_status.equals("?????????")|| listBean.order_status.equals("???????????????")) {
            //????????????
          // modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1);
            View MyOrderShow_MyOrder_view3 = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderdate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderdate);
            modelmy_myorder1_orderdate.setText(invalid_date_date);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText("???????????????????????????");
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view3); //?????????
            //?????????
            MyOrderShow_MyOrder_view3.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"success");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        } else if (listBean.order_status.equals("????????????")||listBean.order_status.equals("?????????")||listBean.order_status.equals("???????????????")||listBean.order_status.equals("????????????")) {
            //?????????
            View MyOrderShow_MyOrder_view2 = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderdate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderdate);
            modelmy_myorder1_orderdate.setText(invalid_date_date);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText("???????????????" + listBean.order_status);
            ImageView modelmy_myorder1_orderimage = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderimage);
            modelmy_myorder1_orderimage.setBackground(getResources().getDrawable(R.drawable.my_order_pay_fail));
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view2);
            MyOrderShow_MyOrder_view2.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"fail");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view2.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        } else if (listBean.order_status.equals("???????????????")) {
            //????????????
            // modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view1);
            View MyOrderShow_MyOrder_view3 = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorder1, null);
            TextView modelmy_myorder1_ordername = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordername);
            modelmy_myorder1_ordername.setText(listBean.product_name);
            TextView modelmy_myorder1_orderdate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderdate);
            modelmy_myorder1_orderdate.setText(invalid_date_date);
            TextView modelmy_myorder1_orderstate = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderstate);
            modelmy_myorder1_orderstate.setText("??????????????????????????????");
            TextView modelmy_myorder1_ordernumber = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordernumber);
            modelmy_myorder1_ordernumber.setText(listBean.order_num);
            TextView modelmy_myorder1_ordermoney = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_ordermoney);
            modelmy_myorder1_ordermoney.setText(listBean.product_price + "");
            modelmy_myorder_main_content.addView(MyOrderShow_MyOrder_view3); //???????????????
            //???????????????
            MyOrderShow_MyOrder_view3.setOnClickListener(v -> {
                getModelMyOrderDetails(listBean.order_id,"success");
            });
            RelativeLayout modelmy_myorder1_orderfunction = MyOrderShow_MyOrder_view3.findViewById(R.id.modelmy_myorder1_orderfunction);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction.getLayoutParams();
            rl.height = 0;
            modelmy_myorder1_orderfunction.setLayoutParams(rl);
        }
    }
    //????????????
    private void MyOrderShow_OrderDetails(MyOrderDetailsBean.DataBean dataBean,String state) {
        if (mview == null || dataBean == null) {
            return;
        }
        mMainContext.onClickMyOrderDetails();
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyOrderDetailsView == null) {
            //???????????????????????????
            mMyOrderDetailsView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myorderdetails, null);
            //Smart_model_my_myorderdetails  ????????????
            mSmart_model_my_myorderdetails = mMyOrderDetailsView.findViewById(R.id.Smart_model_my_myorderdetails);
            mSmart_model_my_myorderdetails.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    mSmart_model_my_myorderdetails.finishLoadMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mSmart_model_my_myorderdetails.finishRefresh();
                }
            });

        }
        //????????????
        TextView modelmy_myorderdetails_name = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_name);
        modelmy_myorderdetails_name.setText(dataBean.product_name);
        //????????????
        TextView modelmy_myorderdetails_price = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_price);
        modelmy_myorderdetails_price.setText(dataBean.product_price + "");
        //????????????
        TextView modelmy_myorderdetails_ordertime = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordertime);
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = df.parse(dataBean.order_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            Date date1 = null;
            try {
                date1 = df1.parse(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date1 != null) {
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dataBean.order_time = df2.format(date1).toString();
            }
        }
        modelmy_myorderdetails_ordertime.setText(dataBean.order_time);
        //????????????
        TextView modelmy_myorderdetails_ordernumber = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordernumber);
        modelmy_myorderdetails_ordernumber.setText(dataBean.order_num + "");
        //????????????
        TextView modelmy_myorderdetails_courseprice = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_courseprice);
        modelmy_myorderdetails_courseprice.setText(dataBean.product_price + "");
        //????????????
        TextView modelmy_myorderdetails_coursediscountprice = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_coursediscountprice);
        modelmy_myorderdetails_coursediscountprice.setText(dataBean.refund_payment_money + "");
        //????????????
        TextView modelmy_myorderdetails_payamount = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_payamount);
        modelmy_myorderdetails_payamount.setText(dataBean.refund_payment_money + "");
        //?????????????????????
        ImageView modelmy_myorderdetails_ordernumbercopy = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_ordernumbercopy);
        modelmy_myorderdetails_ordernumbercopy.setOnClickListener(v -> {
            String modelmy_myorderdetails_ordernumbertext = modelmy_myorderdetails_ordernumber.getText().toString();
            //???????????????????????????
            ClipboardManager cm = (ClipboardManager) mMainContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // ?????????????????????ClipData
            ClipData mClipData = ClipData.newPlainText("Label", modelmy_myorderdetails_ordernumbertext);
            // ???ClipData?????????????????????????????????
            cm.setPrimaryClip(mClipData);
            Toast.makeText(mMainContext, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
        });
        my_layout_main.addView(mMyOrderDetailsView);
        //??????????????????   ?????????????????????
        ImageView modelmy_myorderdetails_icon = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_icon);
        ImageView modelmy_myorderdetails_background_fail = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_background_fail);
        TextView modelmy_myorderdetails_invalid = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_invalid);
        ImageView modelmy_myorderdetails_invalidicon = mMyOrderDetailsView.findViewById(R.id.modelmy_myorderdetails_invalidicon);
        if (state.equals("success")) { //?????????????????????
            modelmy_myorderdetails_invalid.setText("????????????");
            modelmy_myorderdetails_background_fail.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_background_success));
            modelmy_myorderdetails_icon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_success));
            modelmy_myorderdetails_invalidicon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_paysuccess));
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorderdetails_invalidicon.getLayoutParams();
            rl.width = mMyOrderDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_77);
            modelmy_myorderdetails_invalidicon.setLayoutParams(rl);
        } else { //???????????????????????????
            modelmy_myorderdetails_invalid.setText("???????????????");
            modelmy_myorderdetails_background_fail.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_background_fail));
            modelmy_myorderdetails_icon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_fail));
            modelmy_myorderdetails_invalidicon.setBackground(mMyOrderDetailsView.getResources().getDrawable(R.drawable.orderdetails_icon_invalid));
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) modelmy_myorderdetails_invalidicon.getLayoutParams();
            rl.width = mMyOrderDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_58);
            modelmy_myorderdetails_invalidicon.setLayoutParams(rl);
        }
        //???????????????
        modelmy_myorderdetails_coursediscountprice.setPaintFlags(modelmy_myorderdetails_coursediscountprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    //???????????????????????????
    public void MyCouponShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyCouponView == null) {
            mMyCouponView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycoupon, null);
            //Smart_model_my_mycoupon   ?????????????????????
            mSmart_model_my_mycoupon = mMyCouponView.findViewById(R.id.Smart_model_my_mycoupon);
            mSmart_model_my_mycoupon.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyCouponSum <= mMyCouponCurrentPage * mMyCouponPageCount){
                        LinearLayout mycoupon_end = mMyCouponView.findViewById(R.id.mycoupon_end);
                        mycoupon_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_mycoupon.finishLoadMore();
                        return;
                    }
                    //??????????????????
                    if (mMyCouponCurrentTab.equals("notused")) {
                        getMyCouponListMore("?????????");
                    } else if (mMyCouponCurrentTab.equals("alreadyused")) {
                        getMyCouponListMore("?????????");
                    } else if (mMyCouponCurrentTab.equals("expired")) {
                        getMyCouponListMore("?????????");
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (mMyCouponCurrentTab.equals("notused")) {
                        getMyCouponList("?????????");
                    } else if (mMyCouponCurrentTab.equals("alreadyused")) {
                        getMyCouponList("?????????");
                    } else if (mMyCouponCurrentTab.equals("expired")) {
                        getMyCouponList("?????????");
                    }
                }
            });
            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
            modelmy_mycoupon_tab_notused.setOnClickListener(v -> {
                if (!mMyCouponCurrentTab.equals("notused")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                    TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyCouponLastTabIndex = 1;
                mMyCouponCurrentTab = "notused";        //?????????????????????
                getMyCouponList("?????????");
            });
            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
            modelmy_mycoupon_tab_alreadyused.setOnClickListener(v -> {
                if (!mMyCouponCurrentTab.equals("alreadyused")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                //?????????
                mMyCouponLastTabIndex = 2;
                mMyCouponCurrentTab = "alreadyused";
                getMyCouponList("?????????");
            });
            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
            modelmy_mycoupon_tab_expired.setOnClickListener(v -> {
                if (!mMyOrderCurrentTab.equals("expired")) {
                    ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
                    Animation animation = new TranslateAnimation((mMyCouponLastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_mycoupon_cursor1.startAnimation(animation);
                    modelmy_mycoupon_tab_expired.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_mycoupon_tab_notused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_mycoupon_tab_alreadyused.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mMyCouponView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                //?????????
                mMyCouponLastTabIndex = 3;
                mMyCouponCurrentTab = "expired";
                getMyCouponList("?????????");
            });
            //????????????
            TextView modelmy_mycoupon_main_exchange = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_exchange);
            modelmy_mycoupon_main_exchange.setOnClickListener(v -> {
                //?????????????????????????????????
                View view = mMainContext.getLayoutInflater().inflate(R.layout.dialog_sure_cancel1, null);
                mMyCouponDialog = new ControllerCenterDialog(mMainContext, 0, 0, view, R.style.DialogTheme);
                mMyCouponDialog.setCancelable(true);
                mMyCouponDialog.show();
                TextView button_cancel = view.findViewById(R.id.button_cancel);
                button_cancel.setOnClickListener(View -> {
                    mMyCouponDialog.cancel();
                });
                TextView button_sure = view.findViewById(R.id.button_sure);
                button_sure.setOnClickListener(View -> {
                    //?????????????????????
                    EditText dialog_content = view.findViewById(R.id.dialog_content);
                    if (dialog_content.getText().toString().equals("")){
                        Toast.makeText(mMainContext,"????????????????????????",Toast.LENGTH_LONG).show();
                        return;
                    }
                    CheckBeforeExchangingCoupons(dialog_content.getText().toString());
                });
            });
        }
        ImageView modelmy_mycoupon_cursor1 = mMyCouponView.findViewById(R.id.modelmy_mycoupon_cursor1);
        int x = width / 6 - mMyCouponView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_mycoupon_cursor1.setX(x);
        //????????????????????????
        mMyCouponLastTabIndex = 1;
        mMyCouponCurrentTab = "notused";
        my_layout_main.addView(mMyCouponView);
        getMyCouponList("?????????");
    }

    //?????????????????????-???????????????
    private void MyCouponShow_MyCoupon(LinearLayout modelmy_myorder_main_content,MyCoupon.DataBean.ListBean listBean) {
        View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mycoupon1, null);
        modelmy_myorder_main_content.addView(view);
        if (listBean.preferential_way.equals("??????")){
            //???????????????
            String price[] = listBean.dc_denomination.split(",");
            if (price.length > 2){
                return;
            }
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);//??????
            modelmy_mycoupon1_couponprice.setText(price[0]);
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);//?????????
            modelmy_mycoupon1_couponfullreduction.setText("?????????");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);//?????????
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);//????????????
            modelmy_mycoupon1_couponrequire.setText("???" + price[1] + "?????????");
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);//????????????
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        if (listBean.preferential_way.equals("??????")){
            //???????????????
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponrequire.getLayoutParams();
            ll.height = 0;
            modelmy_mycoupon1_couponrequire.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
            ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponpriceicon.getLayoutParams();
            ll.width = 0;
            modelmy_mycoupon1_couponpriceicon.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
            modelmy_mycoupon1_couponprice.setText(listBean.dc_denomination + "???");
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);
            modelmy_mycoupon1_couponfullreduction.setText("?????????");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        if (listBean.preferential_way.equals("??????")){
            //?????????
            TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mycoupon1_couponrequire.getLayoutParams();
            ll.height = 0;
            modelmy_mycoupon1_couponrequire.setLayoutParams(ll);
            TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
            modelmy_mycoupon1_couponprice.setText(listBean.dc_denomination);
            TextView modelmy_mycoupon1_couponfullreduction = view.findViewById(R.id.modelmy_mycoupon1_couponfullreduction);
            modelmy_mycoupon1_couponfullreduction.setText("?????????");
            TextView modelmy_mycoupon1_termofvaliditydata = view.findViewById(R.id.modelmy_mycoupon1_termofvaliditydata);
            modelmy_mycoupon1_termofvaliditydata.setText(listBean.service_life_end_time);
            TextView modelmy_mycoupon1_areaofapplication = view.findViewById(R.id.modelmy_mycoupon1_areaofapplication);
            modelmy_mycoupon1_areaofapplication.setText(listBean.scope);
        }
        //???????????????
//        if (mMyCouponCurrentTab.equals("notused")) {
//
//        } else
            if (mMyCouponCurrentTab.equals("alreadyused")) {
            //????????????
            {
                //???????????????
                //???????????? ????????????  ????????????
                TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
                modelmy_mycoupon1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
                modelmy_mycoupon1_couponprice.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
                modelmy_mycoupon1_couponrequire.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponstate = view.findViewById(R.id.modelmy_mycoupon1_couponstate);
                modelmy_mycoupon1_couponstate.setText("?????????");
                modelmy_mycoupon1_couponstate.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
            }
        } else if (mMyCouponCurrentTab.equals("expired")) {
            //??????????????????    ?????????????????????????????????
            {
                //???????????????
                TextView modelmy_mycoupon1_couponpriceicon = view.findViewById(R.id.modelmy_mycoupon1_couponpriceicon);
                modelmy_mycoupon1_couponpriceicon.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponprice = view.findViewById(R.id.modelmy_mycoupon1_couponprice);
                modelmy_mycoupon1_couponprice.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponrequire = view.findViewById(R.id.modelmy_mycoupon1_couponrequire);
                modelmy_mycoupon1_couponrequire.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
                TextView modelmy_mycoupon1_couponstate = view.findViewById(R.id.modelmy_mycoupon1_couponstate);
                modelmy_mycoupon1_couponstate.setText("?????????");
                modelmy_mycoupon1_couponstate.setTextColor(view.getResources().getColor(R.color.grayccbab9b9));
            }
        }
    }

    //????????????????????????
    public void MyMessageShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyMessageView == null) {
            mMyMessageView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mymessage, null);
            //Smart_model_my_mymessage   ????????????????????????
            mSmart_model_my_mymessage = mMyMessageView.findViewById(R.id.Smart_model_my_mymessage);
            mSmart_model_my_mymessage.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyMessageSum <= mMyMessageCurrentPage * mMyMessagePageCount){
                        LinearLayout mymessage_end = mMyMessageView.findViewById(R.id.mymessage_end);
                        mymessage_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_mymessage.finishLoadMore();
                        return;
                    }
                    getModelMyMessageListMore(mMyMessageType);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (list != null){
                        list.clear();
                    }
                    adapter.notifyDataSetChanged();
                    getModelMyMessageList(mMyMessageType);
                }
            });

            ImageView modelmy_mymessage_clear = mMyMessageView.findViewById(R.id.modelmy_mymessage_clear);
            //??????????????????
            modelmy_mymessage_clear.setOnClickListener(v -> {
               //modelmy_mymessage_noticemessagecount
                TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                if (modelmy_mymessage_noticemessagecount.getVisibility() == View.VISIBLE || modelmy_mymessage_advertisemessagecount.getVisibility() == View.VISIBLE) {  //???????????????
                    m_isFind = true;
                } else {
                    ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
                     //??????????????????????????????????????????
                    int childCount = listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View view = listView.getChildAt(i);
                        if (view == null) {
                            continue;
                        }
                        TextView modelmy_mymessage1_messagecount = view.findViewById(R.id.modelmy_mymessage1_messagecount);
                        if (modelmy_mymessage1_messagecount == null) {
                            continue;
                        }
                        if (modelmy_mymessage1_messagecount.getVisibility() == View.VISIBLE) {  //???????????????
                            m_isFind = true;
                            break;
                        }
                    }
                }
                //??????????????????????????????toast??????
                if (!m_isFind) {
                    Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_LONG).show();
                } else {
                    //??????????????????????????????????????????????????????
                    View dialogView = mMainContext.getLayoutInflater().inflate(R.layout.dialog_notip_sure_cancel, null);
                    mMyDialog = new ControllerCenterDialog(mMainContext, 0, 0, dialogView, R.style.DialogTheme);
                    mMyDialog.setCancelable(true);
                    mMyDialog.show();
                    TextView dialog_content = dialogView.findViewById(R.id.dialog_content);
                    dialog_content.setText("??????????????????????????????????????????");
                    TextView button_cancel = dialogView.findViewById(R.id.button_cancel);
                    button_cancel.setText("??????");
                    button_cancel.setOnClickListener(View -> {
                        mMyDialog.cancel();
                    });
                    TextView button_sure = dialogView.findViewById(R.id.button_sure);
                    button_sure.setText("??????");
                    button_sure.setOnClickListener(View -> {
                        //????????????
//                        modelmy_mymessage_noticemessagecount.setVisibility(android.view.View.INVISIBLE);
//                        modelmy_mymessage_advertisemessagecount.setVisibility(android.view.View.INVISIBLE);
//                        ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
//                        int childCount = listView.getChildCount();
//                        for (int i = 0; i < childCount; i++) {
//                            View view = listView.getChildAt(i);
//                            if (view == null) {
//                                continue;
//                            }
//                            TextView modelmy_mymessage1_messagecount = view.findViewById(R.id.modelmy_mymessage1_messagecount);
//                            if (modelmy_mymessage1_messagecount == null) {
//                                continue;
//                            }
//                            modelmy_mymessage1_messagecount.setVisibility(android.view.View.INVISIBLE);
//
//                        }
//                        String modelmy_mymessage1_Id = "";
//                        for (int i = 0; i < list.size(); i ++){
//                            ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo = list.get(i);
//                            if (myMessageInfo == null){
//                                continue;
//                            }
//                            modelmy_mymessage1_Id = modelmy_mymessage1_Id + "\"" + myMessageInfo.modelmy_mymessage1_Id + "\",";
//                        }
//                        if (!modelmy_mymessage1_Id.equals("") && modelmy_mymessage1_Id.length() >= 1){
//                            modelmy_mymessage1_Id = modelmy_mymessage1_Id.substring(0,modelmy_mymessage1_Id.length() - 1);
//                        }
//                        ReadMyNews(modelmy_mymessage1_Id);
                        ReadMyNewsAll();
                        mMyDialog.cancel();
                    });
                }
            });
        }
        //??????????????????
        ImageView modelmy_mymessage_clear = mMyMessageView.findViewById(R.id.modelmy_mymessage_clear);
        modelmy_mymessage_clear.setVisibility(View.VISIBLE);
        my_layout_main.addView(mMyMessageView);
        //??????????????????
        LinearLayout modelmy_mymessage_main_content = mMyMessageView.findViewById(R.id.modelmy_mymessage_main_content);
        modelmy_mymessage_main_content.removeAllViews();
        if (mMyMessageView0 == null) {
            mMyMessageView0 = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mymessage0, null);
        }
        //????????????
        RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
        modelmy_mymessage_notice.setOnClickListener(v -> {
             //???????????????
            mMyMessageType = 1;
            getModelMyMessageList(1);
        });
        //??????
        RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
        modelmy_mymessage_advertise.setOnClickListener(v -> {
            //???????????????
            mMyMessageType = 2;
            getModelMyMessageList(2);
        });
        modelmy_mymessage_main_content.addView(mMyMessageView0);
        mMyMessageType = 3;
        //?????????????????????????????????????????? ???????????????
        getModelMyMessageList(3);
    }

    //????????????????????????
    public void LearnRecordShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mLearnRecordView == null) {
            mLearnRecordView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myrecords, null);
            //????????????????????????
            mSmart_model_my_learnrecord = mLearnRecordView.findViewById(R.id.Smart_model_my_learnrecord);
            mSmart_model_my_learnrecord.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyRecordsSum <= mMyRecordsCurrentPage * mMyRecordsPageCount){
                        LinearLayout learnrecord_end = mLearnRecordView.findViewById(R.id.learnrecord_end);
                        learnrecord_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_learnrecord.finishLoadMore();
                        return;
                    }
                    if (mMyRecordsCurrentTab.equals("class")) {//??????
                        getModelMyLearnCourseListMore();
                    } else {//??????
                        getModelMyLearnQuestionListMore();
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    if (mMyRecordsCurrentTab.equals("class")) {
                        getModelMyLearnCourseList();
                    } else {
                        getModelMyLearnQuestionList();
                    }
                }
            });
            mLearnCourseAdapter = new CommonListAdapter<Object>() {
                @Override
                protected View initListCell(int position, View convertView, ViewGroup parent) {
                    convertView = getLayoutInflater().inflate(R.layout.model_my_myrecords_class, parent, false);
                    Map<String,Object> map = (Map<String, Object>) mLearnCourseAdapter.getItem(position);
                    TextView modelmy_records_class_goon = convertView.findViewById(R.id.modelmy_records_class_goon);
                    //??????????????????
                    String name;
                    String time;
                    if (mMyRecordsCurrentTab.equals("questionbank")) {
                        name = String.valueOf(map.get("test_paper_name"));
                        time = String.valueOf(map.get("time"));
                        modelmy_records_class_goon.setVisibility(View.INVISIBLE);
                        if (name.equals("null")) {
                            name = "????????????";
                        }
                    } else {
                        name = String.valueOf(map.get("course_name"));
                        time = String.valueOf(map.get("create_time"));
                    }
                    TextView modelmy_records_class_name = convertView.findViewById(R.id.modelmy_records_class_name);
                    modelmy_records_class_name.setText(name);
                    //????????????????????????
                    TextView modelmy_records_class_time = convertView.findViewById(R.id.modelmy_records_class_time);
                    Date date = null;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String dateS = "";
                    try {
                        date = df.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date != null) {
                        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                        Date date1 = null;
                        try {
                            date1 = df1.parse(date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date1 != null) {
                            dateS = dateFormat.format(date1);
                        }
                    }
                    modelmy_records_class_time.setText(dateS);
                    //????????????
                    modelmy_records_class_goon.setClickable(true);
                    modelmy_records_class_goon.setOnClickListener(v->{
                        CourseInfo courseInfo = new CourseInfo();
                        courseInfo.setmCourseId(String.valueOf((int) Double.parseDouble(String.valueOf(map.get("course_id")))));
                        courseInfo.setmCourseCover(String.valueOf(map.get("cover")));
                        courseInfo.setmCourseType(String.valueOf(map.get("course_type")));
                        courseInfo.setmCourseName(String.valueOf(map.get("course_name")));
                        courseInfo.setmCoursePriceOld(String.valueOf(map.get("price")));
                        courseInfo.setmCoursePrice(String.valueOf(map.get("special_price"))) ;
                        courseInfo.setmCourseLearnPersonNum(String.valueOf((int) Double.parseDouble(String.valueOf(map.get("buying_base_number")))));

                        //??????????????????
                        ModelCourseCover modelCourseCover = new ModelCourseCover();
                        View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext, courseInfo);
                        modelCourseCover.CourseDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCourseView);
                        mMainContext.onClickCourseDetails();
                    });
                    return convertView;
                }
            };
            ControllerListViewForScrollView modelmy_learnrecord_main_listview = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_main_listview);
            modelmy_learnrecord_main_listview.setAdapter(mLearnCourseAdapter);
            TextView modelmy_learnrecord_tab_class = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_tab_class);
            modelmy_learnrecord_tab_class.setOnClickListener(v -> {
                if (!mMyRecordsCurrentTab.equals("class")) {
                    ImageView modelmy_learnrecord_cursor1 = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_cursor1);
                    Animation animation = new TranslateAnimation((mMyRecordsLastTabIndex - 1) * width / 2, 0, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_learnrecord_cursor1.startAnimation(animation);
                    TextView modelmy_learnrecord_tab_questionbank = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_tab_questionbank);
                    modelmy_learnrecord_tab_class.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                    modelmy_learnrecord_tab_questionbank.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                }
                mMyRecordsLastTabIndex = 1;
                mMyRecordsCurrentTab = "class";
                getModelMyLearnCourseList();
            });
            TextView modelmy_learnrecord_tab_questionbank = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_tab_questionbank);
            modelmy_learnrecord_tab_questionbank.setOnClickListener(v -> {
                if (!mMyAnswerCurrentTab.equals("questionbank")) {
                    ImageView modelmy_learnrecord_cursor1 = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_cursor1);
                    Animation animation = new TranslateAnimation((mMyRecordsLastTabIndex - 1) * width / 2, width / 2, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    modelmy_learnrecord_cursor1.startAnimation(animation);
                    modelmy_learnrecord_tab_class.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                    modelmy_learnrecord_tab_questionbank.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                }
                mMyRecordsLastTabIndex = 2;
                mMyRecordsCurrentTab = "questionbank";
                getModelMyLearnQuestionList();
            });
        }
        my_layout_main.addView(mLearnRecordView);
        ImageView modelmy_learnrecord_cursor1 = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_cursor1);
        int x = width / 4 - mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        modelmy_learnrecord_cursor1.setX(x);
        //????????????????????????????????????-??????
        mMyRecordsLastTabIndex = 1;
        mMyRecordsCurrentTab = "class";
        TextView modelmy_learnrecord_tab_class = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_tab_class);
        TextView modelmy_learnrecord_tab_questionbank = mLearnRecordView.findViewById(R.id.modelmy_learnrecord_tab_questionbank);
        modelmy_learnrecord_tab_class.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        modelmy_learnrecord_tab_questionbank.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mLearnRecordView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        getModelMyLearnCourseList();
    }

    //????????????????????????
    public void MyAnswerShow() {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyAnswerView == null) {
            mMyAnswerView = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myanswer, null);
            //????????????????????????
            mSmart_model_my_myanswer = mMyAnswerView.findViewById(R.id.Smart_model_my_myanswer);
            mSmart_model_my_myanswer.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyQuestionAndAnswerSum <= mMyQuestionAndAnswerCurrentPage * mMyQuestionAndAnswerPageCount){
                        LinearLayout myanswer_end = mMyAnswerView.findViewById(R.id.myanswer_end);
                        myanswer_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_myanswer.finishLoadMore();
                        return;
                    }
                    getModelMyQuestionListMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getModelMyQuestionList();
                }
            });
        }
        my_layout_main.addView(mMyAnswerView);
        //??????????????????????????????
        getModelMyQuestionList();
    }

     //??????????????????
    private void MyAnswerShow_Details(Integer questions_id,Integer course_type) {
        if (mview == null) {
            return;
        }
        HideAllLayout();
        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
        if (mMyAnswerDetailsView == null) {
            mMyAnswerDetailsView = mMainContext.getLayoutInflater().inflate(R.layout.model_my_myanswerdetails, null);
            //Smart_model_my_myanswerdetails  ??????????????????
            mSmart_model_my_myanswerdetails = mMyAnswerDetailsView.findViewById(R.id.Smart_model_my_myanswerdetails);
            mSmart_model_my_myanswerdetails.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mMyQuestionAndAnswerDetailsSum <= mMyQuestionAndAnswerDetailsCurrentPage * mMyQuestionAndAnswerDetailsPageCount){
                        LinearLayout myanswerdetails_end = mMyAnswerDetailsView.findViewById(R.id.myanswerdetails_end);
                        myanswerdetails_end.setVisibility(View.VISIBLE);
                        mSmart_model_my_myanswerdetails.finishLoadMore();
                        return;
                    }
                    getQueryOneQuestionMore(mAnswerDetailsQuestionId,course_type);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //?????????????????????
                    getQueryOneQuestion(mAnswerDetailsQuestionId,course_type);
                }
            });
            //????????????????????????
            ImageView modelmy_myanswerdetails_delete = mMyAnswerDetailsView.findViewById(R.id.modelmy_myanswerdetails_delete);
            modelmy_myanswerdetails_delete.setOnClickListener(v -> {
                //?????????????????????????????????
                View view = mMainContext.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
                mMyDialog = new ControllerCenterDialog(mMainContext, 0, 0, view, R.style.DialogTheme);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                TextView tip = view.findViewById(R.id.tip);
                tip.setText("????????????");
                TextView dialog_content = view.findViewById(R.id.dialog_content);
                dialog_content.setText("??????????????????????????????");
                TextView button_cancel = view.findViewById(R.id.button_cancel);
                button_cancel.setText("??????");
                button_cancel.setOnClickListener(View -> {
                    mMyDialog.cancel();
                });
                TextView button_sure = view.findViewById(R.id.button_sure);
                button_sure.setText("??????");
                button_sure.setOnClickListener(View -> {
                    //??????????????? ??????????????????id??????????????????   ????????????
                    if (mMyAnswerCurrentTab.equals("question")) {
                        if (mAnswerDetailsQuestionId != null) {
                            DeleteMyQuestion(mAnswerDetailsQuestionId, 1);
                        }
                    } else {
                        if (mAnswerDetailsAnswerId != null) {
                            DeleteMyQuestion(mAnswerDetailsAnswerId, 1);
                        }
                    }
                });
            });
        }
        my_layout_main.addView(mMyAnswerDetailsView);
        LinearLayout modelmy_myanswerdetails_main_content = mMyAnswerDetailsView.findViewById(R.id.modelmy_myanswerdetails_main_content);
        modelmy_myanswerdetails_main_content.removeAllViews();
        mAnswerDetailsView = mMainContext.getLayoutInflater().inflate(R.layout.modelanswerdetails, null);
        modelmy_myanswerdetails_main_content.addView(mAnswerDetailsView);
        getQueryOneQuestion(questions_id,course_type);
        mMainContext.Page_AnswerDetails();
    }

    // ??????????????????????????????????????????
    public void getPersonalInfoDatas() {
        if (mMainContext.mStuId.equals("")){
            mPersonalInfoDataBean = null;
            //??????????????????
            ModelMyInit();
//            Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<PersonalInfoBean> call = modelObservableInterface.queryModelMyPersonInfo(body);
        call.enqueue(new Callback<PersonalInfoBean>() {
            @Override
            public void onResponse(Call<PersonalInfoBean> call, Response<PersonalInfoBean> response) {
                PersonalInfoBean personalInfoBean = response.body();
                if (personalInfoBean == null) {
                    Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(personalInfoBean.code,personalInfoBean.msg)){
                    mPersonalInfoDataBean = null;
                    //??????????????????
                    ModelMyInit();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //????????????????????????
                mPersonalInfoDataBean = personalInfoBean.getData();
                //??????????????????
                ModelMyInit();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<PersonalInfoBean> call, Throwable t) {
                Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    private void getQueryMyPageNum() {
        if (mMainContext.mStuId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<MyMsgBean> call = modelObservableInterface.queryMyPageNum(body);
        call.enqueue(new Callback<MyMsgBean>() {
            @Override
            public void onResponse(Call<MyMsgBean> call, Response<MyMsgBean> response) {
                MyMsgBean myMsgBean = response.body();
                if (myMsgBean == null) {
                    Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(myMsgBean.getCode(),myMsgBean.getMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //????????????????????????
                int collect_num = myMsgBean.coursePackageNUM +
                        myMsgBean.courseNUM + myMsgBean.myCollectionQuestionNUM;
                int my_question = myMsgBean.questionWENnum;
                int learn_num = myMsgBean.itemBankNUM + myMsgBean.recordingNUM;
                TextView mycollect_num = mMyView.findViewById(R.id.mycollect_num);
                mycollect_num.setText(collect_num + "");
                TextView myanswer_num = mMyView.findViewById(R.id.myanswer_num);
                myanswer_num.setText(my_question + "");
                TextView my_learn_record_num = mMyView.findViewById(R.id.my_learn_record_num);
                my_learn_record_num.setText(learn_num + "");
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<MyMsgBean> call, Throwable t) {
                Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    private void getQueryMyMsg() {
        if (mMainContext.mStuId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();

        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<ModelObservableInterface.BaseBean1> call = modelObservableInterface.queryMyMsg(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean1>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean1> call, Response<ModelObservableInterface.BaseBean1> response) {
                ModelObservableInterface.BaseBean1 baseBean = response.body();
                if (baseBean == null) {
                    Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                int hava_msg = Integer.parseInt(baseBean.getData());
                if (hava_msg != 0) {
                    ImageView notice = mMyView.findViewById(R.id.notice);
                    notice.setBackground(getResources().getDrawable(R.drawable.button_notice_have));
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean1> call, Throwable t) {
                Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    @Override
    public void onRecive() {

    }

    //????????????????????????????????????
    public static class PersonalInfoBean {
        private PersonalInfoDataBean data;
        private int code;
        private String msg;

        public PersonalInfoDataBean getData() {
            return data;
        }

        public void setData(PersonalInfoDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

        public String getErrorMsg() {
            return msg;
        }

        public void setErrorMsg(String msg) {
            this.msg = msg;
        }

        public static class PersonalInfoDataBean {
            private String autograph;       //????????????
            private String nickname;        //????????????
            private String stu_name;            //??????
            private String head;          //????????????
            private String tel;           //????????????
            private String login_number;    //??????
            private String ID_number;       //???????????????
            private String email;           //??????
        }
    }

    public static class MyMsgBean {
        private int code;
        private String msg;
        private int coursePackageNUM;
        private int itemBankNUM;
        private int courseNUM;
        private int questionWENnum;
        private int myCollectionQuestionNUM;
        private int recordingNUM;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCoursePackageNUM() {
            return coursePackageNUM;
        }

        public void setCoursePackageNUM(int coursePackageNUM) {
            this.coursePackageNUM = coursePackageNUM;
        }

        public int getItemBankNUM() {
            return itemBankNUM;
        }

        public void setItemBankNUM(int itemBankNUM) {
            this.itemBankNUM = itemBankNUM;
        }

        public int getCourseNUM() {
            return courseNUM;
        }

        public void setCourseNUM(int courseNUM) {
            this.courseNUM = courseNUM;
        }

        public int getQuestionWENnum() {
            return questionWENnum;
        }

        public void setQuestionWENnum(int questionWENnum) {
            this.questionWENnum = questionWENnum;
        }

        public int getMyCollectionQuestionNUM() {
            return myCollectionQuestionNUM;
        }

        public void setMyCollectionQuestionNUM(int myCollectionQuestionNUM) {
            this.myCollectionQuestionNUM = myCollectionQuestionNUM;
        }

        public int getRecordingNUM() {
            return recordingNUM;
        }

        public void setRecordingNUM(int recordingNUM) {
            this.recordingNUM = recordingNUM;
        }
    }

    //??????????????????
    public void getMyCourseList() {
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myclass != null){
                mSmart_model_my_myclass.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCourseCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        paramsMap.put("pageNum", mMyCourseCurrentPage);
        paramsMap.put("pageSize",mMyCoursePageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<QueryMyCourseListBean> call = modelObservableInterface.QueryMyCourseList(body);
        call.enqueue(new Callback<QueryMyCourseListBean>() {
            @Override
            public void onResponse(Call<QueryMyCourseListBean> call, Response<QueryMyCourseListBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    return;
                }
                QueryMyCourseListBean queryMyCourseListBean = response.body();
                if (queryMyCourseListBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(queryMyCourseListBean.getCode(),"")){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                QueryMyCourseListBean.DataBean dataBean = queryMyCourseListBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                List<QueryMyCourseListBean.DataBean.ListBean> courseListBeansList = dataBean.getList();
                if (courseListBeansList == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMyCourseSum = dataBean.getTotal();
                LinearLayout modelmy_myclass_main_content = mMyClassView.findViewById(R.id.modelmy_myclass_main_content);
                modelmy_myclass_main_content.removeAllViews();
                for (int i = 0; i < courseListBeansList.size(); i++) {
                    QueryMyCourseListBean.DataBean.ListBean courseListBean = courseListBeansList.get(i);
                    if (courseListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclass1, null);
                    //??????url
                    ControllerCustomRoundAngleImageView courseTeacherIcon = view.findViewById(R.id.courseTeacherIcon);
                    Glide.with(mMainContext).load(courseListBean.head).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    }).error(mMainContext.getResources().getDrawable(R.drawable.image_teachersdefault)).into(courseTeacherIcon);
                    TextView courseTeacherName = view.findViewById(R.id.courseTeacherName);
                    courseTeacherName.setText(courseListBean.true_name);
                    //????????????
                    TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
                    modelmy_myclass1_classname.setText(courseListBean.course_name);
                    //stuNum
                    TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
                    modelmy_myclass1_state.setText(courseListBean.stuNum + "");
                    modelmy_myclass_main_content.addView(view);
                    TextView modelmy_myclass1_golearn = view.findViewById(R.id.modelmy_myclass1_golearn);
                    //?????????????????????????????????????????????????????????
                    modelmy_myclass1_golearn.setOnClickListener(v -> {
                        CourseInfo courseInfo = new CourseInfo();
                        courseInfo.setmCourseId(String.valueOf(courseListBean.course_id));
                        courseInfo.setmCourseCover(courseListBean.cover);
                        courseInfo.setmCourseType(courseListBean.course_type);
                        courseInfo.setmCourseName(courseListBean.course_name);
                        courseInfo.setmCoursePriceOld(String.valueOf(courseListBean.price));
                        courseInfo.setmCoursePrice(String.valueOf(courseListBean.special_price)) ;
                        courseInfo.setmCourseLearnPersonNum(String.valueOf(courseListBean.stuNum));

                        //??????????????????
                        ModelCourseCover modelCourseCover = new ModelCourseCover();
                        View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext, courseInfo);
                        modelCourseCover.CourseDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCourseView);
                        mMainContext.onClickCourseDetails();
                    });
                }
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<QueryMyCourseListBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"????????????????????????????????????",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //??????????????????-????????????
    public void getMyCourseListMore() {
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myclass != null){
                mSmart_model_my_myclass.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCourseCurrentPage = mMyCourseCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        paramsMap.put("pageNum", mMyCourseCurrentPage);
        paramsMap.put("pageSize",mMyCoursePageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<QueryMyCourseListBean> call = modelObservableInterface.QueryMyCourseList(body);
        call.enqueue(new Callback<QueryMyCourseListBean>() {
            @Override
            public void onResponse(Call<QueryMyCourseListBean> call, Response<QueryMyCourseListBean> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                QueryMyCourseListBean queryMyCourseListBean = response.body();
                if (queryMyCourseListBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(queryMyCourseListBean.getCode(),"")){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                QueryMyCourseListBean.DataBean dataBean = queryMyCourseListBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                List<QueryMyCourseListBean.DataBean.ListBean> courseListBeansList = dataBean.getList();
                if (courseListBeansList == null){
                    if (mSmart_model_my_myclass != null){
                        mSmart_model_my_myclass.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMyCourseSum = dataBean.getTotal();
                LinearLayout modelmy_myclass_main_content = mMyClassView.findViewById(R.id.modelmy_myclass_main_content);
                for (int i = 0; i < courseListBeansList.size(); i++) {
                    QueryMyCourseListBean.DataBean.ListBean courseListBean = courseListBeansList.get(i);
                    if (courseListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclass1, null);
                    //????????????
                    TextView modelmy_myclass1_classname = view.findViewById(R.id.modelmy_myclass1_classname);
                    modelmy_myclass1_classname.setText(courseListBean.course_name);
                    //stuNum
                    TextView modelmy_myclass1_state = view.findViewById(R.id.modelmy_myclass1_state);
                    modelmy_myclass1_state.setText(courseListBean.stuNum + "");
                    modelmy_myclass_main_content.addView(view);
                    //?????????????????????????????????????????????????????????
                    TextView modelmy_myclass1_golearn = view.findViewById(R.id.modelmy_myclass1_golearn);
                    modelmy_myclass1_golearn.setOnClickListener(v -> {
                        CourseInfo courseInfo = new CourseInfo();
                        courseInfo.setmCourseId(String.valueOf(courseListBean.course_id));
                        courseInfo.setmCourseCover(courseListBean.cover);
                        courseInfo.setmCourseType(courseListBean.course_type);
                        courseInfo.setmCourseName(courseListBean.course_name);
                        courseInfo.setmCoursePriceOld(String.valueOf(courseListBean.price));
                        courseInfo.setmCoursePrice(String.valueOf(courseListBean.special_price)) ;
                        courseInfo.setmCourseLearnPersonNum(String.valueOf(courseListBean.stuNum));

                        //??????????????????
                        ModelCourseCover modelCourseCover = new ModelCourseCover();
                        View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext, courseInfo);
                        modelCourseCover.CourseDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(modelCourseView);
                        mMainContext.onClickCourseDetails();
                    });
                }
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishLoadMore();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<QueryMyCourseListBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"????????????????????????????????????",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclass != null){
                    mSmart_model_my_myclass.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //?????????????????????????????????
    public void getMyPacketList() {
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myclasspacket != null){
                mSmart_model_my_myclasspacket.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCoursePacketCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        paramsMap.put("pageNum", mMyCoursePacketCurrentPage);
        paramsMap.put("pageSize",mMyCoursePacketPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<MyclassPacketList> call = modelObservableInterface.QueryMyCoursePackageList(body);
        call.enqueue(new Callback<MyclassPacketList>() {
            @Override
            public void onResponse(Call<MyclassPacketList> call, Response<MyclassPacketList> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                MyclassPacketList myclassPacketList = response.body();
                if (myclassPacketList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(myclassPacketList.getCode(),"")){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                MyclassPacketList.DataBean dataBean = myclassPacketList.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                List<MyclassPacketList.DataBean.ListBean> coursePacketListBeansList = dataBean.getList();
                if (coursePacketListBeansList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMyCoursePacketSum = dataBean.getTotal();
                LinearLayout modelmy_myclasspacket_main_content = mMyClassPacketView.findViewById(R.id.modelmy_myclasspacket_main_content);
                modelmy_myclasspacket_main_content.removeAllViews();
                View line = null;
                for (int i = 0; i < coursePacketListBeansList.size(); i ++) {
                    MyclassPacketList.DataBean.ListBean coursePacketListBean = coursePacketListBeansList.get(i);
                    if (coursePacketListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclasspacket1, null);
                    //????????????????????????
                    ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
                    Glide.with(mMainContext).load(coursePacketListBean.cover).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    }).error(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails)).into(modelmy_myclasspacket1_cover);
                    //???????????????
                    TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
                    modelmy_myclasspacket1_classname.setText(coursePacketListBean.cp_name);
                    //????????????
                    TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
                    modelmy_myclasspacket1_learnpersoncount.setText(coursePacketListBean.stuNum + "");
                    //????????????
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(2);
                    TextView modelmy_myclasspacket1_learnprogresscount = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresscount);
                    if (coursePacketListBean.rateOfLearning == null) {
                        modelmy_myclasspacket1_learnprogresscount.setText(0 + "%");
                    } else {
                        modelmy_myclasspacket1_learnprogresscount.setText(nf.format(coursePacketListBean.rateOfLearning * 100) + "%");
                    }
                    modelmy_myclasspacket_main_content.addView(view);
                    TextView modelmy_myclasspacket1_golearn = view.findViewById(R.id.modelmy_myclasspacket1_golearn);
                    //???????????????????????????????????????????????????????????????
                    modelmy_myclasspacket1_golearn.setOnClickListener(v -> {
                        CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                        coursePacketInfo.mCoursePacketId = String.valueOf(coursePacketListBean.course_package_id);//?????????id
                        coursePacketInfo.mCoursePacketCover = coursePacketListBean.cover;
                        coursePacketInfo.mCoursePacketStageNum = String.valueOf(coursePacketListBean.stageNum);
                        coursePacketInfo.mCoursePacketName = coursePacketListBean.cp_name;   //??????
                        coursePacketInfo.mCoursePacketPrice = String.valueOf(coursePacketListBean.favorable_price);//?????????
                        coursePacketInfo.mCoursePacketCourseNum = String.valueOf(coursePacketListBean.courseNum);
                        coursePacketInfo.mCoursePacketPriceOld = String.valueOf(coursePacketListBean.total_price);//?????????????????????
                        coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(coursePacketListBean.stuNum);//????????????
                        //?????????????????????
                        ClassPacketDetails ClassPacketDetails = new ClassPacketDetails();
                        View ClassPacketView = ClassPacketDetails.ClassPacketDetails(mMainContext, coursePacketInfo);
                        ClassPacketDetails.CoursePacketDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(ClassPacketView);
                        mMainContext.onClickCoursePacketDetails();
                    });
                    //view???
                    line = view.findViewById(R.id.modelmy_myclasspacket1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<MyclassPacketList> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"???????????????????????????????????????",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //?????????????????????????????????-????????????
    public void getMyPacketListMore() {
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myclasspacket != null){
                mSmart_model_my_myclasspacket.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        Gson gson = new Gson();
        mMyCoursePacketCurrentPage = mMyCoursePacketCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        paramsMap.put("pageNum", mMyCoursePacketCurrentPage);
        paramsMap.put("pageSize",mMyCoursePacketPageCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Call<MyclassPacketList> call = modelObservableInterface.QueryMyCoursePackageList(body);
        call.enqueue(new Callback<MyclassPacketList>() {
            @Override
            public void onResponse(Call<MyclassPacketList> call, Response<MyclassPacketList> response) {
                int code = response.code();
                if (code != 200){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    Log.e("TAG", "queryAllCourseInfo  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                MyclassPacketList myclassPacketList = response.body();
                if (myclassPacketList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(myclassPacketList.getCode(),"")){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                MyclassPacketList.DataBean dataBean = myclassPacketList.getData();
                if (dataBean == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                List<MyclassPacketList.DataBean.ListBean> coursePacketListBeansList = dataBean.getList();
                if (coursePacketListBeansList == null){
                    if (mSmart_model_my_myclasspacket != null){
                        mSmart_model_my_myclasspacket.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMyCoursePacketSum = dataBean.getTotal();
                LinearLayout modelmy_myclasspacket_main_content = mMyClassPacketView.findViewById(R.id.modelmy_myclasspacket_main_content);
                View line = null;
                for (int i = 0; i < coursePacketListBeansList.size(); i ++) {
                    MyclassPacketList.DataBean.ListBean coursePacketListBean = coursePacketListBeansList.get(i);
                    if (coursePacketListBean == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_myclasspacket1, null);
                    //????????????????????????
                    ControllerCustomRoundAngleImageView modelmy_myclasspacket1_cover = view.findViewById(R.id.modelmy_myclasspacket1_cover);
                    Glide.with(mMainContext).load(coursePacketListBean.cover).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    }).error(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails)).into(modelmy_myclasspacket1_cover);
                    //???????????????
                    TextView modelmy_myclasspacket1_classname = view.findViewById(R.id.modelmy_myclasspacket1_classname);
                    modelmy_myclasspacket1_classname.setText(coursePacketListBean.cp_name);
                    //????????????
                    TextView modelmy_myclasspacket1_learnpersoncount = view.findViewById(R.id.modelmy_myclasspacket1_learnpersoncount);
                    modelmy_myclasspacket1_learnpersoncount.setText(coursePacketListBean.stuNum + "");
                    //????????????
                    TextView modelmy_myclasspacket1_learnprogresscount = view.findViewById(R.id.modelmy_myclasspacket1_learnprogresscount);
                    if (coursePacketListBean.rateOfLearning == null){
                        modelmy_myclasspacket1_learnprogresscount.setText(0 + "%");
                    } else {
                        NumberFormat nf = NumberFormat.getNumberInstance();
                        nf.setMaximumFractionDigits(2);
                        modelmy_myclasspacket1_learnprogresscount.setText(nf.format(coursePacketListBean.rateOfLearning * 100) + "%");
                    }
                    modelmy_myclasspacket_main_content.addView(view);
                    TextView modelmy_myclasspacket1_golearn = view.findViewById(R.id.modelmy_myclasspacket1_golearn);
                    //???????????????????????????????????????????????????????????????
                    modelmy_myclasspacket1_golearn.setOnClickListener(v -> {
                        CoursePacketInfo coursePacketInfo = new CoursePacketInfo();
                        coursePacketInfo.mCoursePacketId = String.valueOf(coursePacketListBean.course_package_id);//?????????id
                        coursePacketInfo.mCoursePacketCover = coursePacketListBean.cover;
                        coursePacketInfo.mCoursePacketStageNum = String.valueOf(coursePacketListBean.stageNum);
                        coursePacketInfo.mCoursePacketName = coursePacketListBean.cp_name;   //??????
                        coursePacketInfo.mCoursePacketPrice = String.valueOf(coursePacketListBean.favorable_price);//?????????
                        coursePacketInfo.mCoursePacketCourseNum = String.valueOf(coursePacketListBean.courseNum);
                        coursePacketInfo.mCoursePacketPriceOld = String.valueOf(coursePacketListBean.total_price);//?????????????????????
                        coursePacketInfo.mCoursePacketLearnPersonNum= String.valueOf(coursePacketListBean.stuNum);//????????????
                        //?????????????????????
                        ClassPacketDetails ClassPacketDetails = new ClassPacketDetails();
                        View ClassPacketView = ClassPacketDetails.ClassPacketDetails(mMainContext, coursePacketInfo);
                        ClassPacketDetails.CoursePacketDetailsShow();
                        HideAllLayout();
                        LinearLayout my_layout_main = mview.findViewById(R.id.my_layout_main);
                        my_layout_main.addView(ClassPacketView);
                        mMainContext.onClickCoursePacketDetails();
                    });
                    //view???
                    line = view.findViewById(R.id.modelmy_myclasspacket1_line1);
                }
                if (line != null) {
                    line.setVisibility(View.INVISIBLE);
                }
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<MyclassPacketList> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"???????????????????????????????????????",Toast.LENGTH_LONG).show();
                if (mSmart_model_my_myclasspacket != null){
                    mSmart_model_my_myclasspacket.finishLoadMore();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //?????????????????????????????????
    public void getModelMyClassPacketCollection(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        modelmy_mycollect_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectPacketCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectPacketCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPacketPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "?????????");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionPacketList(body)
                .enqueue(new Callback<QueryMyCollectionPacketListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionPacketListBean> call, Response<QueryMyCollectionPacketListBean> response) {
                        QueryMyCollectionPacketListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(listBean.getCode(),"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (code == 200){
                            QueryMyCollectionPacketListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectPacketSum = data.getTotal();
                                List<QueryMyCollectionPacketListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclasspacket1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionPacketListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishRefresh();
                                            }
                                            continue;
                                        }
                                        //???????????????????????????????????????????????????
                                        modelmy_myclasspacket1_line1 = MyCollectShow_MyCoursePacket(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclasspacket1_line1 != null){
                                        modelmy_myclasspacket1_line1.setVisibility(View.INVISIBLE);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishRefresh();
                                }
                                LoadingDialog.getInstance(mMainContext).dismiss();
                                return;
                            }
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionPacketListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                        Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????-????????????
    public void getModelMyClassPacketCollectionMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectPacketCurrentPage = mMyCollectPacketCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectPacketCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPacketPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "?????????");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionPacketList(body)
                .enqueue(new Callback<QueryMyCollectionPacketListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionPacketListBean> call, Response<QueryMyCollectionPacketListBean> response) {
                        QueryMyCollectionPacketListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(listBean.getCode(),"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (code == 200){
                            QueryMyCollectionPacketListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectPacketSum = data.getTotal();
                                List<QueryMyCollectionPacketListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    View modelmy_myclasspacket1_line1 = null;
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionPacketListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishLoadMore();
                                            }
                                            continue;
                                        }
                                        //???????????????????????????????????????????????????
                                        modelmy_myclasspacket1_line1 = MyCollectShow_MyCoursePacket(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (modelmy_myclasspacket1_line1 != null){
                                        modelmy_myclasspacket1_line1.setVisibility(View.INVISIBLE);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishLoadMore();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mMainContext).dismiss();
                                return;
                            }
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionPacketListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishLoadMore();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                        Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????????????????
    public void getModelMyClassCollection(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        modelmy_mycollect_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "??????");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<QueryMyCollectionListBean> call = modelObservableInterface.queryMyCollectionList(body);
        call.enqueue(new Callback<QueryMyCollectionListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionListBean> call, Response<QueryMyCollectionListBean> response) {
                        QueryMyCollectionListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code == 200){
                            QueryMyCollectionListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectSum = data.getTotal();
                                List<QueryMyCollectionListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishRefresh();
                                            }
                                            continue;
                                        }
                                        MyCollectShow_MyCourse(modelmy_mycollect_main_content,listBean1);
                                    }
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishRefresh();
                                }
                                LoadingDialog.getInstance(mMainContext).dismiss();
                                return;
                            }
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                        }else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishRefresh();
                            }
                            Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                        Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????????????????-????????????
    public void getModelMyClassCollectionMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = mMyCollectCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", "??????");
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        modelObservableInterface.queryMyCollectionList(body)
                .enqueue(new Callback<QueryMyCollectionListBean>() {
                    @Override
                    public void onResponse(Call<QueryMyCollectionListBean> call, Response<QueryMyCollectionListBean> response) {
                        QueryMyCollectionListBean listBean = response.body();
                        if (listBean == null){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = listBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code == 200){
                            QueryMyCollectionListBean.DataBean data = listBean.getData();
                            if (data != null){
                                mMyCollectSum = data.getTotal();
                                List<QueryMyCollectionListBean.DataBean.ListBean> list = data.getList();
                                if (list != null){
                                    for (int i = 0; i < list.size(); i ++){
                                        QueryMyCollectionListBean.DataBean.ListBean listBean1 = list.get(i);
                                        if (listBean1 == null){
                                            if (mSmart_model_my_mycollect != null){
                                                mSmart_model_my_mycollect.finishLoadMore();
                                            }
                                            continue;
                                        }
                                        MyCollectShow_MyCourse(modelmy_mycollect_main_content,listBean1);
                                    }
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishRefresh();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                } else {
                                    if (mSmart_model_my_mycollect != null){
                                        mSmart_model_my_mycollect.finishLoadMore();
                                    }
                                    LoadingDialog.getInstance(mMainContext).dismiss();
                                    return;
                                }
                            } else {
                                if (mSmart_model_my_mycollect != null){
                                    mSmart_model_my_mycollect.finishLoadMore();
                                }
                                LoadingDialog.getInstance(mMainContext).dismiss();
                                return;
                            }
                        } else {
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<QueryMyCollectionListBean> call, Throwable t) {
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishLoadMore();
                        }
                        Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                        Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????????????????
    public void getModelMyAnswerCollection(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        modelmy_mycollect_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<com.android.weischool.ModelObservableInterface.BaseBean> call = modelObservableInterface.queryMyCollectionQuestion(body);
        call.enqueue(new Callback<com.android.weischool.ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<com.android.weischool.ModelObservableInterface.BaseBean> call, Response<com.android.weischool.ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                int code = baseBean.getErrorCode();
                if (!HeaderInterceptor.IsErrorCode(code,"")){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (code != 200 ){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String,Object> dataBean = baseBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (dataBean.get("total") != null) {
                    mMyCollectSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                }
                List<Object> listBeans = (List<Object>) dataBean.get("list");
                if (listBeans == null){
                    if (mSmart_model_my_mycollect != null){
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                for (int i = 0; i < listBeans.size(); i ++){
                    Map<String,Object> map = (Map<String, Object>) listBeans.get(i);
                    if (map == null){
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        continue;
                    }
                    MyCollectShow_MyAnswer(modelmy_mycollect_main_content,map);
                }
                if (mSmart_model_my_mycollect != null){
                    mSmart_model_my_mycollect.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<com.android.weischool.ModelObservableInterface.BaseBean> call, Throwable t) {
                if (mSmart_model_my_mycollect != null){
                    mSmart_model_my_mycollect.finishRefresh();
                }
                Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //??????????????????????????????-????????????
    public void getModelMyAnswerCollectionMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycollect != null){
                mSmart_model_my_mycollect.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycollect_main_content = mMyCollectView.findViewById(R.id.modelmy_mycollect_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyCollectCurrentPage = mMyCollectCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyCollectCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCollectPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCollectionList
        Call<com.android.weischool.ModelObservableInterface.BaseBean> call = modelObservableInterface.queryMyCollectionQuestion(body);
        call.enqueue(new Callback<com.android.weischool.ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<com.android.weischool.ModelObservableInterface.BaseBean> call, Response<com.android.weischool.ModelObservableInterface.BaseBean> response) {
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                int code = baseBean.getErrorCode();
                if (!HeaderInterceptor.IsErrorCode(code,"")){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (code != 200 ){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String,Object> dataBean = baseBean.getData();
                if (dataBean == null){
                    if (mSmart_model_my_mycollect != null) {
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (dataBean.get("total") != null) {
                    mMyCollectSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                }
                List<Object> listBeans = (List<Object>) dataBean.get("list");
                if (listBeans == null){
                    if (mSmart_model_my_mycollect != null){
                        mSmart_model_my_mycollect.finishRefresh();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                for (int i = 0; i < listBeans.size(); i ++){
                    Map<String,Object> map = (Map<String, Object>) listBeans.get(i);
                    if (map == null){
                        if (mSmart_model_my_mycollect != null){
                            mSmart_model_my_mycollect.finishRefresh();
                        }
                        continue;
                    }
                    MyCollectShow_MyAnswer(modelmy_mycollect_main_content,map);
                }
                if (mSmart_model_my_mycollect != null){
                    mSmart_model_my_mycollect.finishRefresh();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<com.android.weischool.ModelObservableInterface.BaseBean> call, Throwable t) {
                if (mSmart_model_my_mycollect != null){
                    mSmart_model_my_mycollect.finishRefresh();
                }
                Log.e(TAG, "onFailure: "+t.getMessage()+"?????????" );
                Toast.makeText(mMainContext,"??????????????????????????????",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //??????????????????--------??????
    public void getModelMyMessageListDelect(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo){
        if (myMessageInfo == null){
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String content = "{\"student_information_id\": [\"" + myMessageInfo.modelmy_mymessage1_Id + "\"],\"tf_delete\":1}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
       //queryMyMessageDelectList
        queryMyCourseList.queryMyMessageDelectList(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
//                        adapter.DeleteItem(myMessageInfo);
                        getModelMyMessageList(mMyMessageType);
                        Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                });
    }

    //??????????????????
    public void getModelMyMessageList(Integer info_type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mymessage != null){
                mSmart_model_my_mymessage.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        if (list != null){
            list.clear();
        }
        ControllerListViewForScrollView listView = mMyMessageView0.findViewById(R.id.modelmy_mymessage_main_contentlistview);
        adapter = new ControllerMyMessage1Adapter(new ControllerMyMessage1Adapter.ControllerMyMessage1AdapterInterface() {
            @Override
            public void deleteItem(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo) {
                //??????????????????
                getModelMyMessageListDelect(myMessageInfo);
            }

            @Override
            public void clickItem(ControllerMyMessage1Adapter.MyMessageInfo myMessageInfo) {
                //??????????????????
                LinearLayout modelmy_mymessage_main_content = mMyMessageView.findViewById(R.id.modelmy_mymessage_main_content);
                modelmy_mymessage_main_content.removeAllViews();
                View view = LayoutInflater.from(mMainContext).inflate(R.layout.model_my_mymessage2, null);
                ControllerCustomRoundAngleImageView modelmy_mymessage2_cover = view.findViewById(R.id.modelmy_mymessage2_cover);
                if (info_type == 3) {
                    Glide.with(mMainContext).load(myMessageInfo.modelmy_mymessage1_coverurl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    }).error(mMainContext.getResources().getDrawable(R.drawable.image_teachersdefault)).into(modelmy_mymessage2_cover);
                } else if (info_type == 2) {
                    Glide.with(mMainContext).load(myMessageInfo.modelmy_mymessage1_coverurl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    }).error(mMainContext.getResources().getDrawable(R.drawable.img_mymessage_advertisement)).into(modelmy_mymessage2_cover);
                }
                TextView modelmy_mymessage2_time = view.findViewById(R.id.modelmy_mymessage2_time);
                modelmy_mymessage2_time.setText(myMessageInfo.modelmy_mymessage1_time);
                TextView modelmy_mymessage2_message = view.findViewById(R.id.modelmy_mymessage2_message);
                modelmy_mymessage2_message.setText(myMessageInfo.modelmy_mymessage1_message);
                modelmy_mymessage_main_content.addView(view);
                //??????????????????????????????
                ReadMyNews("\"" + myMessageInfo.modelmy_mymessage1_Id + "\"");
            }
        }, mMainContext, list);
        listView.setAdapter(adapter);
        LinearLayout mymessage_end = mMyMessageView.findViewById(R.id.mymessage_end);
        mymessage_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyMessageCurrentPage = 1;
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyMessageCurrentPage);//?????????
        paramsMap.put("pageSize",mMyMessagePageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        paramsMap.put("info_type",info_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyMessageList(body)
               .enqueue(new Callback<MymessageBean>() {
                   @Override
                   public void onResponse(Call<MymessageBean> call, Response<MymessageBean> response) {
                       MymessageBean body = response.body();
                       if (body == null) {
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       int code = body.getCode();
                       if (!HeaderInterceptor.IsErrorCode(code,"")){
                           if (mSmart_model_my_mycollect != null){
                               mSmart_model_my_mycollect.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       if (code != 200){
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       MymessageBean.DataBean dataBean = body.getData();
                       if (dataBean == null){
                           if (mSmart_model_my_mymessage != null){
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       if (info_type != 3) {
                           RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                           modelmy_mymessage_notice.setVisibility(View.INVISIBLE);
                           LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                           ll.height = 0;
                           modelmy_mymessage_notice.setLayoutParams(ll);
                           RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                           modelmy_mymessage_advertise.setVisibility(View.INVISIBLE);
                           ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                           ll.height = 0;
                           modelmy_mymessage_advertise.setLayoutParams(ll);
                       } else {
                           RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                           modelmy_mymessage_notice.setVisibility(View.VISIBLE);
                           LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                           ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                           modelmy_mymessage_notice.setLayoutParams(ll);
                           RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                           modelmy_mymessage_advertise.setVisibility(View.VISIBLE);
                           ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                           ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                           modelmy_mymessage_advertise.setLayoutParams(ll);
                           if (body.AD_sum != null) {
                               TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                               if (body.AD_sum > 0) {
                                   modelmy_mymessage_advertisemessagecount.setVisibility(View.VISIBLE);
                                   modelmy_mymessage_advertisemessagecount.setText(body.AD_sum + "");
                               }
                           }
                           if (body.Sys_num != null) {
                               TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                               if (body.Sys_num > 0) {
                                   modelmy_mymessage_noticemessagecount.setVisibility(View.VISIBLE);
                                   modelmy_mymessage_noticemessagecount.setText(body.Sys_num + "");
                               }
                           }
                       }
                       mMyMessageSum = dataBean.getTotal();
                       List<MymessageBean.DataBean.ListBean> listBeans = dataBean.getList();
                       if (listBeans == null) {
                           if (mSmart_model_my_mymessage != null) {
                               mSmart_model_my_mymessage.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       //???????????????????????????
                       for (int i = 0; i < listBeans.size(); i++) {
                           MymessageBean.DataBean.ListBean listBean = listBeans.get(i);
                           if (listBean == null) {
                               continue;
                           }
                           ControllerMyMessage1Adapter.MyMessageInfo message = new ControllerMyMessage1Adapter.MyMessageInfo();
                           message.modelmy_mymessage1_name = listBean.title;  //????????????????????????
                           if (listBean.tf_read == 2) {
                               message.modelmy_mymessage1_messagecount = "1";     //????????????????????????
                           } else {
                               message.modelmy_mymessage1_messagecount = "-1";     //????????????????????????
                           }
                           message.modelmy_mymessage1_message = listBean.content;//????????????message
                           message.modelmy_mymessage1_Id = listBean.student_information_id;
                           Date date = null;
                           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                           try {
                               date = df.parse(listBean.time);
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }
                           if (date != null) {
                               SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                               Date date1 = null;
                               try {
                                   date1 = df1.parse(date.toString());
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                               if (date1 != null) {
                                   SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                   listBean.time = df2.format(date1).toString();
                               }
                           }
                           message.modelmy_mymessage1_time = listBean.time;//??????????????????
                           list.add(message);
                       }
                       adapter.notifyDataSetChanged();
                       if (mSmart_model_my_mymessage != null){
                           mSmart_model_my_mymessage.finishRefresh();
                       }
                       LoadingDialog.getInstance(mMainContext).dismiss();
                   }

                   @Override
                   public void onFailure(Call<MymessageBean> call, Throwable t) {
                   //??????????????????
                       if (mSmart_model_my_mymessage != null){
                           mSmart_model_my_mymessage.finishRefresh();
                       }
                       LoadingDialog.getInstance(mMainContext).dismiss();
                   }
               });
    }

    //??????????????????
    public void getModelMyMessageListMore(Integer info_type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mymessage != null){
                mSmart_model_my_mymessage.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyMessageCurrentPage = mMyMessageCurrentPage + 1;
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyMessageCurrentPage);//?????????
        paramsMap.put("pageSize",mMyMessagePageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        paramsMap.put("info_type",info_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyMessageList(body)
                .enqueue(new Callback<MymessageBean>() {
                    @Override
                    public void onResponse(Call<MymessageBean> call, Response<MymessageBean> response) {
                        MymessageBean body = response.body();
                        if (body == null) {
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = body.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_mycollect != null){
                                mSmart_model_my_mycollect.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MymessageBean.DataBean dataBean = body.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyMessageSum = dataBean.getTotal();
                        List<MymessageBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_mymessage != null){
                                mSmart_model_my_mymessage.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (info_type != 3) {
                            RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                            modelmy_mymessage_notice.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                            ll.height = 0;
                            modelmy_mymessage_notice.setLayoutParams(ll);
                            RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                            modelmy_mymessage_advertise.setVisibility(View.INVISIBLE);
                            ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                            ll.height = 0;
                            modelmy_mymessage_advertise.setLayoutParams(ll);
                        } else {
                            RelativeLayout modelmy_mymessage_notice = mMyMessageView0.findViewById(R.id.modelmy_mymessage_notice);
                            modelmy_mymessage_notice.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) modelmy_mymessage_notice.getLayoutParams();
                            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            modelmy_mymessage_notice.setLayoutParams(ll);
                            RelativeLayout modelmy_mymessage_advertise = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertise);
                            modelmy_mymessage_advertise.setVisibility(View.VISIBLE);
                            ll = (LinearLayout.LayoutParams) modelmy_mymessage_advertise.getLayoutParams();
                            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            modelmy_mymessage_advertise.setLayoutParams(ll);
                            if (body.AD_sum != null) {
                                TextView modelmy_mymessage_advertisemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_advertisemessagecount);
                                if (body.AD_sum > 0) {
                                    modelmy_mymessage_advertisemessagecount.setVisibility(View.VISIBLE);
                                    modelmy_mymessage_advertisemessagecount.setText(body.AD_sum + "");
                                }
                            }
                            if (body.Sys_num != null) {
                                TextView modelmy_mymessage_noticemessagecount = mMyMessageView0.findViewById(R.id.modelmy_mymessage_noticemessagecount);
                                if (body.Sys_num > 0) {
                                    modelmy_mymessage_noticemessagecount.setVisibility(View.VISIBLE);
                                    modelmy_mymessage_noticemessagecount.setText(body.Sys_num + "");
                                }
                            }
                        }
                        //???????????????????????????
                        for (int i = 0; i < listBeans.size(); i++) {
                            MymessageBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null) {
                                continue;
                            }
//                           if (listBean.info_type == )
                            ControllerMyMessage1Adapter.MyMessageInfo message = new ControllerMyMessage1Adapter.MyMessageInfo();
//                           message.modelmy_mymessage1_coverurl = listBean.u;
                            message.modelmy_mymessage1_name = listBean.title;  //????????????????????????
                            if (listBean.tf_read == 2) {
                                message.modelmy_mymessage1_messagecount = "1";     //????????????????????????
                            } else {
                                message.modelmy_mymessage1_messagecount = "-1";     //????????????????????????
                            }
                            message.modelmy_mymessage1_message = listBean.content;//????????????message
                            message.modelmy_mymessage1_Id = listBean.student_information_id;
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.time = df2.format(date1).toString();
                                }
                            }

                            message.modelmy_mymessage1_time = listBean.time;//??????????????????
                            list.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        if (mSmart_model_my_mymessage != null){
                            mSmart_model_my_mymessage.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MymessageBean> call, Throwable t) {
                        //??????????????????
                        if (mSmart_model_my_mymessage != null){
                            mSmart_model_my_mymessage.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????(??????)---------??????
    public void DeleteMyQuestion(int id,int delete){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("questions_id", id);//????????????
        paramsMap.put("tf_delete",delete);//????????????
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.DeleteMyQuestion(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_LONG).show();
                        } else {
                            mMyDialog.cancel();
                            //??????????????????????????????
                            mMainContext.onClickMyAnswerReturn(mMyAnswerView);
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        Toast.makeText(mMainContext,"????????????",Toast.LENGTH_LONG).show();
                    }
                });
    }


    //???????????????????????????
    public void getModelMyQuestionList(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout myanswer_end = mMyAnswerView.findViewById(R.id.myanswer_end);
        myanswer_end.setVisibility(View.INVISIBLE);
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        modelmy_myanswer_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//?????????
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionList(body)
               .enqueue(new Callback<MyQuestionsBean>() {
                   @Override
                   public void onResponse(Call<MyQuestionsBean> call, Response<MyQuestionsBean> response) {
                       MyQuestionsBean myQuestionsBean = response.body();
                       if (myQuestionsBean == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       if (!HeaderInterceptor.IsErrorCode(myQuestionsBean.getCode(),"")){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       int code = myQuestionsBean.getCode();
                       if (code != 200 ){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       MyQuestionsBean.DataBean dataBean = myQuestionsBean.getData();
                       if (dataBean == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       mMyQuestionAndAnswerSum = dataBean.getTotal();
                       List<MyQuestionsBean.DataBean.ListBean> listBeans = dataBean.getList();
                       if (listBeans == null){
                           if (mSmart_model_my_myanswer != null) {
                               mSmart_model_my_myanswer.finishRefresh();
                           }
                           LoadingDialog.getInstance(mMainContext).dismiss();
                           return;
                       }
                       //????????????????????????for??????
                       for (int i = 0; i < listBeans.size();i ++){
                           MyQuestionsBean.DataBean.ListBean listBean = listBeans.get(i);
                           if (listBean == null){
                               continue;
                           }
                           View view = mMainContext.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                           TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                           modelmy_myanswer1_title.setText(listBean.title);
                           TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                           new ModelHtmlUtils(mMainContext, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                           TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                           Date date = null;
                           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                           try {
                               date = df.parse(listBean.creation_time);
                           } catch (ParseException e) {
                               e.printStackTrace();
                           }
                           if (date != null) {
                               SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                               Date date1 = null;
                               try {
                                   date1 = df1.parse(date.toString());
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                               if (date1 != null) {
                                   SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                   listBean.creation_time = df2.format(date1).toString();
                               }
                           }
                           modelmy_myanswer1_time.setText(listBean.creation_time);
                           modelmy_myanswer_main_content.addView(view);
                           if (listBean.questions_id != null) {
                               view.setOnClickListener(V -> {
                                   if (listBean.course_type == null){
                                       Toast.makeText(mMainContext,"????????????????????????",Toast.LENGTH_SHORT).show();
                                       return;
                                   }
                                   mAnswerDetailsQuestionId = listBean.questions_id;
                                   MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                               });
                           }
                       }
                       if (mSmart_model_my_myanswer != null) {
                           mSmart_model_my_myanswer.finishRefresh();
                       }
                       LoadingDialog.getInstance(mMainContext).dismiss();
                   }

                   @Override
                   public void onFailure(Call<MyQuestionsBean> call, Throwable t) {
                       if (mSmart_model_my_myanswer != null) {
                           mSmart_model_my_myanswer.finishRefresh();
                       }
                       LoadingDialog.getInstance(mMainContext).dismiss();
                   }
               });
    }

    //???????????????????????????-????????????
    public void getModelMyQuestionListMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myanswer != null) {
                mSmart_model_my_myanswer.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        mAnswerDetailsAnswerId = null;
        mAnswerDetailsQuestionId = null;
        LinearLayout modelmy_myanswer_main_content = mMyAnswerView.findViewById(R.id.modelmy_myanswer_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerCurrentPage = mMyQuestionAndAnswerCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerCurrentPage);//?????????
        paramsMap.put("pageSize",mMyQuestionAndAnswerPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyQuestionList(body)
                .enqueue(new Callback<MyQuestionsBean>() {
                    @Override
                    public void onResponse(Call<MyQuestionsBean> call, Response<MyQuestionsBean> response) {
                        MyQuestionsBean myQuestionsBean = response.body();
                        if (myQuestionsBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myQuestionsBean.getCode(),"")){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = myQuestionsBean.getCode();
                        if (code != 200 ){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyQuestionsBean.DataBean dataBean = myQuestionsBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerSum = dataBean.getTotal();
                        List<MyQuestionsBean.DataBean.ListBean> listBeans = dataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswer != null) {
                                mSmart_model_my_myanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        //????????????????????????for??????
                        for (int i = 0; i < listBeans.size();i ++){
                            MyQuestionsBean.DataBean.ListBean listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mMainContext.getLayoutInflater().inflate(R.layout.model_my_myanswer1, null);
                            TextView modelmy_myanswer1_title = view.findViewById(R.id.modelmy_myanswer1_title);
                            modelmy_myanswer1_title.setText(listBean.title);
                            TextView modelmy_myanswer1_content = view.findViewById(R.id.modelmy_myanswer1_content);
                            new ModelHtmlUtils(mMainContext, modelmy_myanswer1_content).setHtmlWithPic(listBean.content);
                            TextView modelmy_myanswer1_time = view.findViewById(R.id.modelmy_myanswer1_time);
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.creation_time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.creation_time = df2.format(date1).toString();
                                }
                            }
                            modelmy_myanswer1_time.setText(listBean.creation_time);
                            modelmy_myanswer_main_content.addView(view);
                            if (listBean.questions_id != null) {
                                view.setOnClickListener(V -> {
                                    if (listBean.course_type == null){
                                        Toast.makeText(mMainContext,"????????????????????????",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAnswerDetailsQuestionId = listBean.questions_id;
                                    MyAnswerShow_Details(listBean.questions_id,listBean.course_type);
                                });
                            }
                        }
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<MyQuestionsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswer != null) {
                            mSmart_model_my_myanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????
    public void getModelMyLearnCourseList(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_learnrecord != null) {
                mSmart_model_my_learnrecord.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout learnrecord_end = mLearnRecordView.findViewById(R.id.learnrecord_end);
        learnrecord_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyRecordsCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyRecordsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyRecordsPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseRecording(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = baseBean.getErrorCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200 ){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        Map<String,Object> dataBean = baseBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (dataBean.get("total") != null) {
                            mMyQuestionAndAnswerSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                        }
                        List<Object> listBeans = (List<Object>) dataBean.get("list");
                        if (listBeans == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mLearnCourseAdapter.clear();
                        mLearnCourseAdapter.addAll(listBeans);
                        mLearnCourseAdapter.notifyDataSetChanged();
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????-????????????
    public void getModelMyLearnCourseListMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_learnrecord != null) {
                mSmart_model_my_learnrecord.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyRecordsCurrentPage = mMyRecordsCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyRecordsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyRecordsPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseRecording(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = baseBean.getErrorCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200 ){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        Map<String,Object> dataBean = baseBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (dataBean.get("total") != null) {
                            mMyQuestionAndAnswerSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                        }
                        List<Object> listBeans = (List<Object>) dataBean.get("list");
                        if (listBeans == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mLearnCourseAdapter.addAll(listBeans);
                        mLearnCourseAdapter.notifyDataSetChanged();
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????
    public void getModelMyLearnQuestionList(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_learnrecord != null) {
                mSmart_model_my_learnrecord.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout learnrecord_end = mLearnRecordView.findViewById(R.id.learnrecord_end);
        learnrecord_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyRecordsCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyRecordsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyRecordsPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseItemBank(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = baseBean.getErrorCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200 ){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        Map<String,Object> dataBean = baseBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (dataBean.get("total") != null) {
                            mMyQuestionAndAnswerSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                        }
                        List<Object> listBeans = (List<Object>) dataBean.get("list");
                        if (listBeans == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mLearnCourseAdapter.clear();
                        mLearnCourseAdapter.addAll(listBeans);
                        mLearnCourseAdapter.notifyDataSetChanged();
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????-????????????
    public void getModelMyLearnQuestionListMore(){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_learnrecord != null) {
                mSmart_model_my_learnrecord.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyRecordsCurrentPage = mMyRecordsCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyRecordsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyRecordsPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseItemBank(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = baseBean.getErrorCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200 ){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        Map<String,Object> dataBean = baseBean.getData();
                        if (dataBean == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (dataBean.get("total") != null) {
                            mMyQuestionAndAnswerSum = (int) Double.parseDouble(String.valueOf(dataBean.get("total")));
                        }
                        List<Object> listBeans = (List<Object>) dataBean.get("list");
                        if (listBeans == null){
                            if (mSmart_model_my_learnrecord != null) {
                                mSmart_model_my_learnrecord.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mLearnCourseAdapter.addAll(listBeans);
                        mLearnCourseAdapter.notifyDataSetChanged();
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        if (mSmart_model_my_learnrecord != null) {
                            mSmart_model_my_learnrecord.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????(????????????)
    public void getQueryOneQuestion(Integer questions_id,Integer course_type){
        if (questions_id == null){
            if (mSmart_model_my_myanswerdetails != null) {
                mSmart_model_my_myanswerdetails.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout myanswerdetails_end = mMyAnswerDetailsView.findViewById(R.id.myanswerdetails_end);
        myanswerdetails_end.setVisibility(View.INVISIBLE);
        LinearLayout answerdetails_content = mAnswerDetailsView.findViewById(R.id.answerdetails_content);
        answerdetails_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerDetailsCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerDetailsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyQuestionAndAnswerDetailsPageCount);//????????????
        paramsMap.put("questions_id", questions_id);//??????id
        paramsMap.put("course_type", course_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<ModelCommunityAnswerActivity.CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<ModelCommunityAnswerActivity.CommunityDetilsBean> call, Response<ModelCommunityAnswerActivity.CommunityDetilsBean> response) {
                        ModelCommunityAnswerActivity.CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        //?????????????????????
                        if (communityDetilsDataBean.getState() == 0) { //??????
                            //?????????
                            ImageView answerdetails_top = mAnswerDetailsView.findViewById(R.id.answerdetails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_top.setLayoutParams(ll);
                            //?????????
                            ImageView answerdetails_fine = mAnswerDetailsView.findViewById(R.id.answerdetails_fine);
                            ll = (LinearLayout.LayoutParams) answerdetails_fine.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_fine.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.getState() == 1) {//??????
                            //?????????
                            ImageView answerdetails_top = mAnswerDetailsView.findViewById(R.id.answerdetails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_top.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.getState() == 2) {//??????
                            //?????????
                            ImageView answerdetails_fine = mAnswerDetailsView.findViewById(R.id.answerdetails_fine);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) answerdetails_fine.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            answerdetails_fine.setLayoutParams(ll);
                        }
                        ControllerCustomRoundAngleImageView answerdetails_headportrait = mAnswerDetailsView.findViewById(R.id.answerdetails_headportrait);
                        Glide.with(mMainContext).load(communityDetilsDataBean.getHead()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        }).error(mMainContext.getResources().getDrawable(R.drawable.image_teachersdefault)).into(answerdetails_headportrait);
                        TextView answerdetails_name = mAnswerDetailsView.findViewById(R.id.answerdetails_name);
                        answerdetails_name.setText(communityDetilsDataBean.getNicename());
                        TextView answerdetails_time = mAnswerDetailsView.findViewById(R.id.answerdetails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        answerdetails_time.setText(communityDetilsDataBean.getCreation_time());
                        TextView answerdetails_title = mAnswerDetailsView.findViewById(R.id.answerdetails_title);
                        answerdetails_title.setText(communityDetilsDataBean.getTitle());
                        TextView answerdetails_message = mAnswerDetailsView.findViewById(R.id.answerdetails_message);
                        new ModelHtmlUtils(mMainContext, answerdetails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        List<String> strings = communityDetilsDataBean.getSubject_id();
                        if (strings != null) {
                            for (int i = 0; i < strings.size(); i++) {
                                String string = strings.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0) {
                                    TextView answerdetails_sign1 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign1);
                                    answerdetails_sign1.setText(string);
                                    answerdetails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1) {
                                    TextView answerdetails_sign2 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign2);
                                    answerdetails_sign2.setText(string);
                                    answerdetails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2) {
                                    TextView answerdetails_sign3 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign3);
                                    answerdetails_sign3.setText(string);
                                    answerdetails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        GridLayout answerdetails_imagelayout = mAnswerDetailsView.findViewById(R.id.answerdetails_imagelayout);
                        answerdetails_imagelayout.removeAllViews();
                        //??????.size
                        if (communityDetilsDataBean.getPicture() != null) {
                            String pictures[] = communityDetilsDataBean.getPicture().split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mMainContext).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mMainContext).load(pictures[num]).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.d("Warn", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d("Warn", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                                            return false;
                                        }
                                    }).error(mMainContext.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    answerdetails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBean communityDetilsAnswerDataBean = communityDetilsDataBean.getHuida();
                        if (communityDetilsAnswerDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerDetailsSum = communityDetilsAnswerDataBean.getTotal();
                        List<ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList> listBeans = communityDetilsAnswerDataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        View line = null;
                        for (int i = 0; i < listBeans.size() ; i ++){
                            ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mMainContext.getLayoutInflater().inflate(R.layout.modelanswerdetails_child, null);
                            //???????????????
                            ControllerCustomRoundAngleImageView answerdetails_child_headportrait = view.findViewById(R.id.answerdetails_child_headportrait);
                            Glide.with(getActivity()).load(listBean.getA_head()).into(answerdetails_child_headportrait);
                            //????????????
                            TextView answerdetails_child_name = view.findViewById(R.id.answerdetails_child_name);
                            answerdetails_child_name.setText(listBean.getQ_nicename());
                            //???????????????
                            TextView manswerdetails_child_time = view.findViewById(R.id.answerdetails_child_time);
                            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.getCreation_time());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.setCreation_time(df2.format(date1).toString());
                                }
                            }
                            manswerdetails_child_time.setText(listBean.getCreation_time());
                            //???????????????
                            TextView answerdetails_child_message = view.findViewById(R.id.answerdetails_child_message);
                            new ModelHtmlUtils(mMainContext, answerdetails_child_message).setHtmlWithPic(listBean.getContent());
                            answerdetails_content.addView(view);
                            line = view.findViewById(R.id.answerdetails_child_line);
                        }
                        if (line != null) {
                            line.setVisibility(View.INVISIBLE);
                        }
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelCommunityAnswerActivity.CommunityDetilsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                });
    }

    //??????????????????-????????????
    public void getQueryOneQuestionMore(Integer questions_id,Integer course_type){
        if (questions_id == null){
            if (mSmart_model_my_myanswerdetails != null) {
                mSmart_model_my_myanswerdetails.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout answerdetails_content = mAnswerDetailsView.findViewById(R.id.answerdetails_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyQuestionAndAnswerDetailsCurrentPage = mMyQuestionAndAnswerDetailsCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyQuestionAndAnswerDetailsCurrentPage);//?????????
        paramsMap.put("pageSize",mMyQuestionAndAnswerDetailsPageCount);//????????????
        paramsMap.put("questions_id", questions_id);//??????id
        paramsMap.put("course_type", course_type);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<ModelCommunityAnswerActivity.CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<ModelCommunityAnswerActivity.CommunityDetilsBean> call, Response<ModelCommunityAnswerActivity.CommunityDetilsBean> response) {
                        ModelCommunityAnswerActivity.CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean= communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        //?????????????????????
                        ControllerCustomRoundAngleImageView answerdetails_headportrait = mAnswerDetailsView.findViewById(R.id.answerdetails_headportrait);
                        Glide.with(mMainContext).load(communityDetilsDataBean.getHead()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Wain", "???????????? errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Wain", "??????  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        }).error(mMainContext.getResources().getDrawable(R.drawable.image_teachersdefault)).into(answerdetails_headportrait);
                        TextView answerdetails_name = mAnswerDetailsView.findViewById(R.id.answerdetails_name);
                        answerdetails_name.setText(communityDetilsDataBean.getNicename());
                        TextView answerdetails_time = mAnswerDetailsView.findViewById(R.id.answerdetails_time);
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                            Date date1 = null;
                            try {
                                date1 = df1.parse(date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1 != null) {
                                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                communityDetilsDataBean.setCreation_time(df2.format(date1).toString());
                            }
                        }
                        answerdetails_time.setText(communityDetilsDataBean.getCreation_time());
                        TextView answerdetails_title = mAnswerDetailsView.findViewById(R.id.answerdetails_title);
                        answerdetails_title.setText(communityDetilsDataBean.getTitle());
                        TextView answerdetails_message = mAnswerDetailsView.findViewById(R.id.answerdetails_message);
                        new ModelHtmlUtils(mMainContext, answerdetails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        List<String> strings = communityDetilsDataBean.getSubject_id();
                        if (strings != null) {
                            for (int i = 0; i < strings.size(); i++) {
                                String string = strings.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0) {
                                    TextView answerdetails_sign1 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign1);
                                    answerdetails_sign1.setText(string);
                                    answerdetails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1) {
                                    TextView answerdetails_sign2 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign2);
                                    answerdetails_sign2.setText(string);
                                    answerdetails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2) {
                                    TextView answerdetails_sign3 = mAnswerDetailsView.findViewById(R.id.answerdetails_sign3);
                                    answerdetails_sign3.setText(string);
                                    answerdetails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBean communityDetilsAnswerDataBean = communityDetilsDataBean.getHuida();
                        if (communityDetilsAnswerDataBean == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyQuestionAndAnswerDetailsSum = communityDetilsAnswerDataBean.getTotal();
                        List<ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList> listBeans = communityDetilsAnswerDataBean.getList();
                        if (listBeans == null){
                            if (mSmart_model_my_myanswerdetails != null) {
                                mSmart_model_my_myanswerdetails.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        View line = null;
                        for (int i = 0; i < listBeans.size() ; i ++){
                            ModelCommunityAnswerActivity.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList listBean = listBeans.get(i);
                            if (listBean == null){
                                continue;
                            }
                            View view = mMainContext.getLayoutInflater().inflate(R.layout.modelanswerdetails_child, null);
                            //???????????????
                            ControllerCustomRoundAngleImageView answerdetails_child_headportrait = view.findViewById(R.id.answerdetails_child_headportrait);
                            Glide.with(getActivity()).load(listBean.getA_head()).into(answerdetails_child_headportrait);
                            //????????????
                            TextView answerdetails_child_name = view.findViewById(R.id.answerdetails_child_name);
                            answerdetails_child_name.setText(listBean.getQ_nicename());
                            //???????????????
                            TextView manswerdetails_child_time = view.findViewById(R.id.answerdetails_child_time);
                            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listBean.getCreation_time());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                                Date date1 = null;
                                try {
                                    date1 = df1.parse(date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (date1 != null) {
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                                    listBean.setCreation_time(df2.format(date1).toString());
                                }
                            }
                            manswerdetails_child_time.setText(listBean.getCreation_time());
                            //???????????????
                            TextView answerdetails_child_message = view.findViewById(R.id.answerdetails_child_message);
                            new ModelHtmlUtils(mMainContext, answerdetails_child_message).setHtmlWithPic(listBean.getContent());
                            answerdetails_content.addView(view);
                            line = view.findViewById(R.id.answerdetails_child_line);
                        }
                        if (line != null) {
                            line.setVisibility(View.INVISIBLE);
                        }
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelCommunityAnswerActivity.CommunityDetilsBean> call, Throwable t) {
                        if (mSmart_model_my_myanswerdetails != null) {
                            mSmart_model_my_myanswerdetails.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                });
    }

    //??????????????????????????????
    public static class MyQuestionsBean{
        private int code;
        private DataBean data;
        private Integer huida_num;
        private Integer wen_num;

        public Integer getHuida_num() {
            return huida_num;
        }

        public Integer getWen_num() {
            return wen_num;
        }

        public void setHuida_num(Integer huida_num) {
            this.huida_num = huida_num;
        }

        public void setWen_num(Integer wen_num) {
            this.wen_num = wen_num;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public DataBean getData() {
            return data;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"????????????","product_type":"?????????","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"?????????001"},{"order_status":"????????????","product_type":"?????????","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"?????????002"},{"order_status":"????????????","product_type":"??????","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"????????????11"},{"order_status":"?????????","product_type":"?????????","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"??????11"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"????????????"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"????????????"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {

                private int fid;
                private int visit_num;
                private Integer questions_id;
                private String creation_time;
                private String wen_rs;
                private String title;
                private String content;
                private Integer course_type;

                public Integer getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(Integer course_type) {
                    this.course_type = course_type;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getTitle() {
                    return title;
                }

                public int getFid() {
                    return fid;
                }

                public Integer getQuestions_id() {
                    return questions_id;
                }

                public int getVisit_num() {
                    return visit_num;
                }

                public String getCreation_time() {
                    return creation_time;
                }

                public String getWen_rs() {
                    return wen_rs;
                }

                public void setCreation_time(String creation_time) {
                    this.creation_time = creation_time;
                }

                public void setFid(int fid) {
                    this.fid = fid;
                }

                public void setQuestions_id(Integer questions_id) {
                    this.questions_id = questions_id;
                }

                public void setVisit_num(int visit_num) {
                    this.visit_num = visit_num;
                }

                public void setWen_rs(String wen_rs) {
                    this.wen_rs = wen_rs;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getContent() {
                    return content;
                }
            }
        }
    }

    //??????????????????Bean
    public static class MymessageBean{
        private int code;
        private DataBean data;
        private Integer Sys_num;
        private Integer AD_sum;

        public Integer getAD_sum() {
            return AD_sum;
        }

        public Integer getSys_num() {
            return Sys_num;
        }

        public void setAD_sum(Integer AD_sum) {
            this.AD_sum = AD_sum;
        }

        public void setSys_num(Integer sys_num) {
            Sys_num = sys_num;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public DataBean getData() {
            return data;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"????????????","product_type":"?????????","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"?????????001"},{"order_status":"????????????","product_type":"?????????","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"?????????002"},{"order_status":"????????????","product_type":"??????","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"????????????11"},{"order_status":"?????????","product_type":"?????????","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"??????11"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"????????????"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"????????????"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * order_status : ????????????
                 * product_type : ?????????
                 * order_num : 20190920105823
                 * order_time : 2019-09-20T10:58:40.000+0800
                 * product_price : 1
                 * order_id : 1
                 * product_name : ?????????001
                 */

                private int tf_read;
                private int info_type;
                private int student_information_id;
                private String time;
                private String title;
                private String content;

                public int getInfo_type() {
                    return info_type;
                }

                public int getStudent_information_id() {
                    return student_information_id;
                }

                public int getTf_read() {
                    return tf_read;
                }

                public String getTime() {
                    return time;
                }

                public String getContent() {
                    return content;
                }

                public String getTitle() {
                    return title;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public void setInfo_type(int info_type) {
                    this.info_type = info_type;
                }

                public void setStudent_information_id(int student_information_id) {
                    this.student_information_id = student_information_id;
                }

                public void setTf_read(int tf_read) {
                    this.tf_read = tf_read;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
    // ????????????----????????????
    public void ReadMyNews(String idS){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String content = "{\"student_information_id\": [" + idS + "],\"tf_read\":1}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), content);
        queryMyCourseList.ReadMyNews(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Toast.makeText(mMainContext,"????????????",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????-????????????
    public void ReadMyNewsAll(){
        if (mMainContext.mStuId.equals("")){
            Toast.makeText(mMainContext,"???????????????",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        paramsMap.put("info_type", mMyMessageType);//	??????
        paramsMap.put("tf_read", 1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.ReadMyNewsAll(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"???????????????",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"???????????????",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getModelMyMessageList(mMyMessageType);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        Toast.makeText(mMainContext,"???????????????????????????????????????",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Toast.makeText(mMainContext,"???????????????",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //????????????
    public void getModelMyMeent(View myclass_agreement,int agreement_id){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("agreement_id", agreement_id);//?????????
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryCourseModelNewsAgreeMent(body)
                .enqueue(new Callback<MyClassMeent>() {
                    @Override
                    public void onResponse(Call<MyClassMeent> call, Response<MyClassMeent> response) {
                        MyClassMeent classMeent = response.body();
                        if (classMeent == null){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(classMeent.getCode(),"")){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (classMeent.getCode() == 200){
                            MyClassMeent.DataBean data = classMeent.getData();
                            String agreement_content = data.getAgreement_content();
                            TextView tv1 = myclass_agreement.findViewById(R.id.model_agreement_content);
                            new ModelHtmlUtils(mMainContext, tv1).setHtmlWithPic(agreement_content);
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyClassMeent> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //????????????--???????????????????????????
    public void getCancelOrder(View MyOrderShow_MyOrder_view1,Integer order_id){
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        String strEntity = "{\"order_id\": \"" + order_id + "\"," +
                "    \"stu_id\":" + mMainContext.mStuId + "}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyCancelOrderStates(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"??????????????????",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            Toast.makeText(mMainContext,"??????????????????",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(mMainContext,"??????????????????",Toast.LENGTH_SHORT).show();
                        if (MyOrderShow_MyOrder_view1 != null) {
                            TextView modelmy_myorder1_orderstate1 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderstate);
                            //??????????????????????????????????????????
                            modelmy_myorder1_orderstate1.setText("???????????????????????????");
                            RelativeLayout modelmy_myorder1_orderfunction2 = MyOrderShow_MyOrder_view1.findViewById(R.id.modelmy_myorder1_orderfunction);
                            RelativeLayout.LayoutParams r2 = (RelativeLayout.LayoutParams) modelmy_myorder1_orderfunction2.getLayoutParams();
                            r2.height = 0;
                            modelmy_myorder1_orderfunction2.setLayoutParams(r2);
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        Toast.makeText(mMainContext,"??????????????????",Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //??????????????????????????????          queryMyPackageOrderList
    public void getModelMyOrderList(String type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myorder != null){
                mSmart_model_my_myorder.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        modelmy_myorder_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyOrderCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyOrderCurrentPage);//?????????
        paramsMap.put("pageSize",mMyOrderPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyPackageOrderList(body)
                .enqueue(new Callback<MyOrderlistBean>() {
                    @Override
                    public void onResponse(Call<MyOrderlistBean> call, Response<MyOrderlistBean> response) {
                        MyOrderlistBean orderlistBean = response.body();
                        if (orderlistBean == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = orderlistBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyOrderlistBean.DataBean data = orderlistBean.getData();
                        if (data == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyOrderSum = data.total;
                        if (orderlistBean.getTotal_num() != null){
                            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
                            modelmy_myorder_tab_all.setText("?????????" + orderlistBean.getTotal_num() + "???");
                        }
                        if (orderlistBean.getSuccess_num() != null){
                            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                            modelmy_myorder_tab_finished.setText("????????????" + orderlistBean.getSuccess_num() + "???");
                        }
                        if (orderlistBean.getFail_num() != null){
                            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                            modelmy_myorder_tab_unfinish.setText("????????????" + orderlistBean.getFail_num() + "???");
                        }
                        List<MyOrderlistBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i ++) {
                            MyOrderlistBean.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            MyOrderShow_MyOrder(modelmy_myorder_main_content,listBean);
                        }
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderlistBean> call, Throwable t) {
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????????????????          queryMyPackageOrderList -????????????
    public void getModelMyOrderListMore(String type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_myorder != null){
                mSmart_model_my_myorder.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mMyOrderCurrentPage = mMyOrderCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mMyOrderCurrentPage);//?????????
        paramsMap.put("pageSize",mMyOrderPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyPackageOrderList(body)
                .enqueue(new Callback<MyOrderlistBean>() {
                    @Override
                    public void onResponse(Call<MyOrderlistBean> call, Response<MyOrderlistBean> response) {
                        MyOrderlistBean orderlistBean = response.body();
                        if (orderlistBean == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = orderlistBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyOrderlistBean.DataBean data = orderlistBean.getData();
                        if (data == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (orderlistBean.getTotal_num() != null){
                            TextView modelmy_myorder_tab_all = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_all);
                            modelmy_myorder_tab_all.setText("?????????" + orderlistBean.getTotal_num() + "???");
                        }
                        if (orderlistBean.getSuccess_num() != null){
                            TextView modelmy_myorder_tab_finished = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_finished);
                            modelmy_myorder_tab_finished.setText("????????????" + orderlistBean.getSuccess_num() + "???");
                        }
                        if (orderlistBean.getFail_num() != null){
                            TextView modelmy_myorder_tab_unfinish = mMyOrderView.findViewById(R.id.modelmy_myorder_tab_unfinish);
                            modelmy_myorder_tab_unfinish.setText("????????????" + orderlistBean.getFail_num() + "???");
                        }
                        mMyOrderSum = data.total;
                        List<MyOrderlistBean.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i ++) {
                            MyOrderlistBean.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            MyOrderShow_MyOrder(modelmy_myorder_main_content,listBean);
                        }
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderlistBean> call, Throwable t) {
                        if (mSmart_model_my_myorder != null){
                            mSmart_model_my_myorder.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //??????????????????
    public void getModelMyOrderDetails(Integer order_id,String state){
        if (order_id == null){
            Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_myorder_main_content = mMyOrderView.findViewById(R.id.modelmy_myorder_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("order_id", order_id);//??????id
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryMyOrderDetails(body)
                .enqueue(new Callback<MyOrderDetailsBean>() {
                    @Override
                    public void onResponse(Call<MyOrderDetailsBean> call, Response<MyOrderDetailsBean> response) {
                        MyOrderDetailsBean myOrderDetailsBean = response.body();
                        if (myOrderDetailsBean == null){
                            Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = myOrderDetailsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            if (mSmart_model_my_myorder != null){
                                mSmart_model_my_myorder.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code != 200) {
                            Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyOrderDetailsBean.DataBean data = myOrderDetailsBean.getData();
                        if (data == null){
                            Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyOrderShow_OrderDetails(data,state);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyOrderDetailsBean> call, Throwable t) {
                        Toast.makeText(mMainContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }
    //??????????????????(??????)
   public static class QueryMyCollectionListBean{
        /**
         * code : 200
         * data : {"total":3,"list":[{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"course_type":"??????,??????","stuNum":201,"course_name":"????????????002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"course_type":"??????","stuNum":12,"course_name":"????????????","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"course_type":"??????,??????","stuNum":24,"course_name":"1","buying_base_number":23}],"pageNum":1,"pageSize":10,"size":3,"startRow":1,"endRow":3,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 3
             * list : [{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"course_type":"??????,??????","stuNum":201,"course_name":"????????????002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"course_type":"??????","stuNum":12,"course_name":"????????????","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"course_type":"??????,??????","stuNum":24,"course_name":"1","buying_base_number":23}]
             * pageNum : 1
             * pageSize : 10
             * size : 3
             * startRow : 1
             * endRow : 3
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * lastPage : 1
             * firstPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int lastPage;
            private int firstPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * cover : http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg
                 * course_id : 2
                 * course_type : ??????,??????
                 * stuNum : 201
                 * course_name : ????????????002
                 * buying_base_number : 199
                 */

                private String cover;
                private int course_id;
                private String course_type;
                private int stuNum;
                private String course_name;
                private int buying_base_number;
                private Double special_price;
                private Double price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public String getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(String course_type) {
                    this.course_type = course_type;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public String getCourse_name() {
                    return course_name;
                }

                public void setCourse_name(String course_name) {
                    this.course_name = course_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public void setSpecial_price(Double special_price) {
                    this.special_price = special_price;
                }

                public void setPrice(Double price) {
                    this.price = price;
                }

                public Double getPrice() {
                    return price;
                }

                public Double getSpecial_price() {
                    return special_price;
                }
            }
        }
    }
    //??????????????????(?????????)
    public static class QueryMyCollectionPacketListBean{
        /**
         * code : 200
         * data : {"total":1,"list":[{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":14,"stuNum":52,"courseNum":0,"cp_name":"??????11","buying_base_number":50}],"pageNum":1,"pageSize":10,"size":1,"startRow":1,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 1
             * list : [{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":0,"course_package_id":14,"stuNum":52,"courseNum":0,"cp_name":"??????11","buying_base_number":50}]
             * pageNum : 1
             * pageSize : 10
             * size : 1
             * startRow : 1
             * endRow : 1
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * lastPage : 1
             * firstPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int lastPage;
            private int firstPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * cover : http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png
                 * stageNum : 0
                 * course_package_id : 14
                 * stuNum : 52
                 * courseNum : 0
                 * cp_name : ??????11
                 * buying_base_number : 50
                 */

                private String cover;
                private int stageNum;
                private int course_package_id;
                private int stuNum;
                private int courseNum;
                private String cp_name;
                private int buying_base_number;
                private Double favorable_price;
                private Double total_price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getStageNum() {
                    return stageNum;
                }

                public void setStageNum(int stageNum) {
                    this.stageNum = stageNum;
                }

                public int getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(int course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public int getCourseNum() {
                    return courseNum;
                }

                public void setCourseNum(int courseNum) {
                    this.courseNum = courseNum;
                }

                public String getCp_name() {
                    return cp_name;
                }

                public void setCp_name(String cp_name) {
                    this.cp_name = cp_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public void setTotal_price(Double total_price) {
                    this.total_price = total_price;
                }

                public void setFavorable_price(Double favorable_price) {
                    this.favorable_price = favorable_price;
                }

                public Double getFavorable_price() {
                    return favorable_price;
                }

                public Double getTotal_price() {
                    return total_price;
                }
            }
        }
    }
    //??????????????????
    public static class QueryMyCourseListBean {
        /**
         * code : 200
         * data : {"total":4,"list":[{"cover":"??????88899","course_id":1,"agreement_id":null,"course_type":"??????","stuNum":53,"course_name":"????????????","buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"agreement_id":2,"course_type":"??????,??????","stuNum":201,"course_name":"????????????002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"agreement_id":null,"course_type":"??????","stuNum":12,"course_name":"????????????","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"agreement_id":null,"course_type":"??????,??????","stuNum":24,"course_name":"1","buying_base_number":23}],"pageNum":1,"pageSize":4,"size":4,"startRow":1,"endRow":4,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 4
             * list : [{"cover":"??????88899","course_id":1,"agreement_id":null,"course_type":"??????","stuNum":53,"course_name":"????????????","buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/public/fce05edc-b263-416d-8f59-0836f750cb23.jpg","course_id":2,"agreement_id":2,"course_type":"??????,??????","stuNum":201,"course_name":"????????????002","buying_base_number":199},{"cover":"http://image.yunduoketang.com/course/34270/20190829/11c506e7-d6f2-47c6-831c-9d19fa0b5c13.png","course_id":4,"agreement_id":null,"course_type":"??????","stuNum":12,"course_name":"????????????","buying_base_number":11},{"cover":"E:/upload111/1576150503144.png","course_id":5,"agreement_id":null,"course_type":"??????,??????","stuNum":24,"course_name":"1","buying_base_number":23}]
             * pageNum : 1
             * pageSize : 4
             * size : 4
             * startRow : 1
             * endRow : 4
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * cover : ??????88899
                 * course_id : 1
                 * agreement_id : null
                 * course_type : ??????
                 * stuNum : 53
                 * course_name : ????????????
                 * buying_base_number : 50
                 */

                private String cover;
                private int course_id;
                private Integer agreement_id;
                private String course_type;
                private int stuNum;
                private String course_name;
                private int buying_base_number;
                private double price;
                private double special_price;
                private String head; //????????????
                private String true_name;  //????????????

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public Integer getAgreement_id() {
                    return agreement_id;
                }

                public void setAgreement_id(Integer agreement_id) {
                    this.agreement_id = agreement_id;
                }

                public String getCourse_type() {
                    return course_type;
                }

                public void setCourse_type(String course_type) {
                    this.course_type = course_type;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public String getCourse_name() {
                    return course_name;
                }

                public void setCourse_name(String course_name) {
                    this.course_name = course_name;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public double getPrice() {
                    return price;
                }

                public double getSpecial_price() {
                    return special_price;
                }

                public void setPrice(double price) {
                    this.price = price;
                }

                public void setSpecial_price(double special_price) {
                    this.special_price = special_price;
                }
            }
        }
    }

    //?????????????????????
    public static class MyclassPacketList {
        /**
         * code : 200
         * data : {"total":2,"list":[{"cover":"E:/upload111/1576155322131.png","stageNum":10,"course_package_id":1,"agreement_id":4,"stuNum":52,"courseNum":6,"cp_name":"?????????001","rateOfLearning":0.4521,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":1,"course_package_id":14,"agreement_id":null,"stuNum":52,"courseNum":3,"cp_name":"??????11","rateOfLearning":0.4533,"buying_base_number":50}],"pageNum":1,"pageSize":10,"size":2,"startRow":1,"endRow":2,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"lastPage":1,"firstPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 2
             * list : [{"cover":"E:/upload111/1576155322131.png","stageNum":10,"course_package_id":1,"agreement_id":4,"stuNum":52,"courseNum":6,"cp_name":"?????????001","rateOfLearning":0.4521,"buying_base_number":50},{"cover":"http://image.yunduoketang.com/course/34270/20190313/ff89e692-6e38-425b-ab0e-1aa88f7ce5d6.png","stageNum":1,"course_package_id":14,"agreement_id":null,"stuNum":52,"courseNum":3,"cp_name":"??????11","rateOfLearning":0.4533,"buying_base_number":50}]
             * pageNum : 1
             * pageSize : 10
             * size : 2
             * startRow : 1
             * endRow : 2
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * lastPage : 1
             * firstPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int lastPage;
            private int firstPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * cover : E:/upload111/1576155322131.png
                 * stageNum : 10
                 * course_package_id : 1
                 * agreement_id : 4
                 * stuNum : 52
                 * courseNum : 6
                 * cp_name : ?????????001
                 * rateOfLearning : 0.4521
                 * buying_base_number : 50
                 */

                private String cover;
                private int stageNum;
                private int course_package_id;
                private Integer agreement_id;
                private int stuNum;
                private int courseNum;
                private String cp_name;
                private Double rateOfLearning;
                private int buying_base_number;
                private double total_price;
                private double favorable_price;

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public int getStageNum() {
                    return stageNum;
                }

                public void setStageNum(int stageNum) {
                    this.stageNum = stageNum;
                }

                public int getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(int course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public Integer getAgreement_id() {
                    return agreement_id;
                }

                public void setAgreement_id(Integer agreement_id) {
                    this.agreement_id = agreement_id;
                }

                public int getStuNum() {
                    return stuNum;
                }

                public void setStuNum(int stuNum) {
                    this.stuNum = stuNum;
                }

                public int getCourseNum() {
                    return courseNum;
                }

                public void setCourseNum(int courseNum) {
                    this.courseNum = courseNum;
                }

                public String getCp_name() {
                    return cp_name;
                }

                public void setCp_name(String cp_name) {
                    this.cp_name = cp_name;
                }

                public Double getRateOfLearning() {
                    return rateOfLearning;
                }

                public void setRateOfLearning(Double rateOfLearning) {
                    this.rateOfLearning = rateOfLearning;
                }

                public int getBuying_base_number() {
                    return buying_base_number;
                }

                public void setBuying_base_number(int buying_base_number) {
                    this.buying_base_number = buying_base_number;
                }

                public double getFavorable_price() {
                    return favorable_price;
                }

                public double getTotal_price() {
                    return total_price;
                }

                public void setFavorable_price(double favorable_price) {
                    this.favorable_price = favorable_price;
                }

                public void setTotal_price(double total_price) {
                    this.total_price = total_price;
                }
            }
        }
    }
    //??????????????????????????????????????????     queryCourseModelNewsAgreeMent
    public static  class MyClassMeent{
        /**
         * code : 200
         * data : {"agreement_id":1,"agreement_content":"\r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n???????????? ??????\r\n \r\n??????\r\n?????????\r\n??????\r\n \r\n??????\r\n \r\n??????\r\n \r\n?????????\r\n \r\n????????????\r\n \r\n????????????\r\n \r\n????????????\r\n????????????\r\n??????\r\n????????????\r\n?????????????????????????????????????????????????????????????????????20191015??????\r\n????????????\r\n????????????\r\n??????????????????????????????????????????????????????????????????\r\n??????????????????????????????????????????????????????????????????\r\n\r\n\u2014\u2014????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????????????????\r\n\r\n?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n??????????????????:     ?????????????????????[huozhongbanzhuren    ]\r\n\r\n?????????????????????huozhongedu    @126.com\r\n\r\n                   ???????????????\r\n\r\n                                01060703671 ???????????????09:00-12:00,  13:00-18:00\r\n                                01081705189 ???????????????13:00-18:00,  19:00-22:00\r\n\r\n????????????????????????????????????\r\n\r\n1?????????????????????????????????????????????\r\n\r\n2??????????????????????????????????????????????????????\r\n\r\n3????????????????????????????????????????????????????????????????????????\r\n\r\n4???1???1????????????????????????????????????????????????????????????????????????\r\n\r\n5????????????????????????????????????????????????????????????????????????/??????/QQ??????\r\n\r\n6???????????????????????????????????????????????????\r\n\r\n7???????????????????????????????????????????????????\r\n\r\n8????????????????????????????????????????????????????????????\r\n\r\n9??????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n10????????????????????????????????????????????????????????????2020????????????????????????????????????????????????24?????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n????????????????????????\r\n\r\n1?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n2?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????6???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????30%???\r\n\r\n3???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????3?????????????????????????????????????????????????????????????????????????????????\r\n\r\n4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????\r\n\r\n???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????????????????\r\n\r\n????????????????????????????????????????????????????????????????????????????????????????????????????????? 2020???????????????????????????2021????????????????????????????????????????????????????????????????????????????????????????????????2021??????????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n?????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????0?????????????????????????????????????????????????????????\r\n\r\n????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n???????????????????????????????????????????????????????????????????????????\r\n\r\n????????????????????????????????????????????????????????????????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n????????????????????????????????????\r\n\r\n??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2020???????????????????????????????????????3????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n?????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n??????????????????\r\n\r\n???????????????7?????????????????????????????????????????????????????????????????????????????????7???????????????30??????????????????????????????????????????10%????????????????????????30???????????????60??????????????????????????????????????????20%????????????????????????60???????????????90??????????????????????????????????????????30%????????????????????????90???????????????120??????????????????????????????????????????40%????????????????????????120????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n????????????????????????\r\n\r\n?????????????????????????????????????????????????????????????????????????????????????????????30????????????30????????????????????????????????????31?????????????????????????????????????????????3????????????????????????????????????\r\n\r\n??????????????????\r\n\r\n???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n????????????????????????\r\n\r\n????????????????????????????????????????????????QQ/???????????????????????????????????????????????????????????????????????????????????????6?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n?????????????????????\r\n\r\n1?????????????????????????????????????????????????????????????????????????????????QQ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n2????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????3?????????????????????\r\n\r\n3?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????10000??????\r\n\r\n4????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n5???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n6??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n7???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n8????????????????????????????????????????????????????????????????????????????????????????????????????????????\r\n\r\n"}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * agreement_id : 1
             * agreement_content :
             ??????

             ??????

             ??????

             ??????

             ??????

             ??????

             ??????

             ??????

             ??????

             ??????

             ???????????? ??????

             ??????
             ?????????
             ??????

             ??????

             ??????

             ?????????

             ????????????

             ????????????

             ????????????
             ????????????
             ??????
             ????????????
             ?????????????????????????????????????????????????????????????????????20191015??????
             ????????????
             ????????????
             ??????????????????????????????????????????????????????????????????
             ??????????????????????????????????????????????????????????????????

             ??????????????????????????????????????????????????????????????????

             ???????????????????????????

             ??????????????????????????????????????????????????????????????????????????????

             ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ??????????????????????????????????????????????????????????????????

             ???????????????????????????

             ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ??????????????????:     ?????????????????????[huozhongbanzhuren    ]

             ?????????????????????huozhongedu    @126.com

             ???????????????

             01060703671 ???????????????09:00-12:00,  13:00-18:00
             01081705189 ???????????????13:00-18:00,  19:00-22:00

             ????????????????????????????????????

             1?????????????????????????????????????????????

             2??????????????????????????????????????????????????????

             3????????????????????????????????????????????????????????????????????????

             4???1???1????????????????????????????????????????????????????????????????????????

             5????????????????????????????????????????????????????????????????????????/??????/QQ??????

             6???????????????????????????????????????????????????

             7???????????????????????????????????????????????????

             8????????????????????????????????????????????????????????????

             9??????????????????????????????????????????????????????????????????????????????????????????????????????

             10????????????????????????????????????????????????????????????2020????????????????????????????????????????????????24?????????????????????????????????????????????????????????????????????????????????????????????????????????

             ????????????????????????

             1?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             2?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????6???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????30%???

             3???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????3?????????????????????????????????????????????????????????????????????????????????

             4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ???????????????????????????

             ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ???????????????????????????????????????

             ????????????????????????????????????????????????????????????????????????????????????????????????????????? 2020???????????????????????????2021????????????????????????????????????????????????????????????????????????????????????????????????2021??????????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ?????????????????????????????????????????????????????????????????????????????????????????????

             ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????0?????????????????????????????????????????????????????????

             ????????????????????????2022????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ???????????????????????????????????????????????????????????????????????????

             ????????????????????????????????????????????????????????????????????????????????????

             ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ????????????????????????????????????

             ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2020???????????????????????????????????????3????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ?????????????????????????????????????????????????????????????????????????????????????????????

             ??????????????????

             ???????????????7?????????????????????????????????????????????????????????????????????????????????7???????????????30??????????????????????????????????????????10%????????????????????????30???????????????60??????????????????????????????????????????20%????????????????????????60???????????????90??????????????????????????????????????????30%????????????????????????90???????????????120??????????????????????????????????????????40%????????????????????????120????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ????????????????????????

             ?????????????????????????????????????????????????????????????????????????????????????????????30????????????30????????????????????????????????????31?????????????????????????????????????????????3????????????????????????????????????

             ??????????????????

             ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ????????????????????????

             ????????????????????????????????????????????????QQ/???????????????????????????????????????????????????????????????????????????????????????6?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             ?????????????????????

             1?????????????????????????????????????????????????????????????????????????????????QQ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             2????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????3?????????????????????

             3?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????10000??????

             4????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             5???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             6??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             7???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

             8????????????????????????????????????????????????????????????????????????????????????????????????????????????


             */

            private int agreement_id;
            private String agreement_content;

            public int getAgreement_id() {
                return agreement_id;
            }

            public void setAgreement_id(int agreement_id) {
                this.agreement_id = agreement_id;
            }

            public String getAgreement_content() {
                return agreement_content;
            }

            public void setAgreement_content(String agreement_content) {
                this.agreement_content = agreement_content;
            }
        }
    }


    //??????????????????
    public static class MyOrderlistBean{
        /**
         * code : 200
         * data : {"total":6,"list":[{"order_status":"????????????","product_type":"?????????","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"?????????001"},{"order_status":"????????????","product_type":"?????????","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"?????????002"},{"order_status":"????????????","product_type":"??????","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"????????????11"},{"order_status":"?????????","product_type":"?????????","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"??????11"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"????????????"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"????????????"}],"pageNum":1,"pageSize":10,"size":6,"startRow":1,"endRow":6,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;
        private Integer total_num;
        private Integer success_num;
        private Integer fail_num;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public Integer getFail_num() {
            return fail_num;
        }

        public Integer getSuccess_num() {
            return success_num;
        }

        public Integer getTotal_num() {
            return total_num;
        }

        public void setFail_num(Integer fail_num) {
            this.fail_num = fail_num;
        }

        public void setSuccess_num(Integer success_num) {
            this.success_num = success_num;
        }

        public void setTotal_num(Integer total_num) {
            this.total_num = total_num;
        }

        public static class DataBean {
            /**
             * total : 6
             * list : [{"order_status":"????????????","product_type":"?????????","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"?????????001"},{"order_status":"????????????","product_type":"?????????","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"?????????002"},{"order_status":"????????????","product_type":"??????","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"????????????11"},{"order_status":"?????????","product_type":"?????????","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"??????11"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"????????????"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"????????????"}]
             * pageNum : 1
             * pageSize : 10
             * size : 6
             * startRow : 1
             * endRow : 6
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * order_status : ????????????
                 * product_type : ?????????
                 * order_num : 20190920105823
                 * order_time : 2019-09-20T10:58:40.000+0800
                 * product_price : 1
                 * order_id : 1
                 * product_name : ?????????001
                 */

                private String order_status;
                private String product_type;
                private String order_num;
                private String order_time;
                private double product_price;
                private int order_id;
                private String product_name;
                private Integer CPC_id;

                public Integer getCPC_id() {
                    return CPC_id;
                }

                public void setCPC_id(Integer CPC_id) {
                    this.CPC_id = CPC_id;
                }

                public String getOrder_status() {
                    return order_status;
                }

                public void setOrder_status(String order_status) {
                    this.order_status = order_status;
                }

                public String getProduct_type() {
                    return product_type;
                }

                public void setProduct_type(String product_type) {
                    this.product_type = product_type;
                }

                public String getOrder_num() {
                    return order_num;
                }

                public void setOrder_num(String order_num) {
                    this.order_num = order_num;
                }

                public String getOrder_time() {
                    return order_time;
                }

                public void setOrder_time(String order_time) {
                    this.order_time = order_time;
                }

                public Double getProduct_price() {
                    return product_price;
                }

                public void setProduct_price(Double product_price) {
                    this.product_price = product_price;
                }

                public int getOrder_id() {
                    return order_id;
                }

                public void setOrder_id(int order_id) {
                    this.order_id = order_id;
                }

                public String getProduct_name() {
                    return product_name;
                }

                public void setProduct_name(String product_name) {
                    this.product_name = product_name;
                }
            }
        }

    }

    //??????????????????
    public static class MyOrderDetailsBean{
        /**
         * code : 200
         * data : {"total":6,"list":[{"order_status":"????????????","product_type":"?????????","order_num":"20190920105823","order_time":"2019-09-20T10:58:40.000+0800","product_price":1,"order_id":1,"product_name":"?????????001"},{"order_status":"????????????","product_type":"?????????","order_num":"20190920111237","order_time":"2019-09-20T11:12:37.000+0800","product_price":1,"order_id":2,"product_name":"?????????002"},{"order_status":"????????????","product_type":"??????","order_num":"1573527488150","order_time":"2019-11-12T10:58:08.000+0800","product_price":66,"order_id":51,"product_name":"????????????11"},{"order_status":"?????????","product_type":"?????????","order_num":"1576232505724","order_time":"2019-12-13T18:21:45.000+0800","product_price":111,"order_id":56,"product_name":"??????11"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577445735963","order_time":"2019-12-27T19:22:15.000+0800","product_price":66,"order_id":63,"product_name":"????????????"},{"order_status":"????????????","product_type":"??????","order_num":"S1C1T1577448266676","order_time":"2019-12-27T20:04:26.000+0800","product_price":66,"order_id":64,"product_name":"????????????"}],"pageNum":1,"pageSize":10,"size":6,"startRow":1,"endRow":6,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {

            private Integer course_id;
            private float refund_payment_money;
            private Integer validityTime;
            private String product_type;
            private Integer course_package_id;
            private String order_num;
            private String order_time;
            private float product_price;
            private String product_name;
            private priceInfoBean priceInfo;

            public Integer getCourse_id() {
                return course_id;
            }

            public void setCourse_id(Integer course_id) {
                this.course_id = course_id;
            }

            public float getRefund_payment_money() {
                return refund_payment_money;
            }

            public void setRefund_payment_money(float refund_payment_money) {
                this.refund_payment_money = refund_payment_money;
            }

            public Integer getValidityTime() {
                return validityTime;
            }

            public void setValidityTime(Integer validityTime) {
                this.validityTime = validityTime;
            }

            public String getProduct_type() {
                return product_type;
            }

            public void setProduct_type(String product_type) {
                this.product_type = product_type;
            }

            public Integer getCourse_package_id() {
                return course_package_id;
            }

            public void setCourse_package_id(Integer course_package_id) {
                this.course_package_id = course_package_id;
            }

            public String getOrder_num() {
                return order_num;
            }

            public void setOrder_num(String order_num) {
                this.order_num = order_num;
            }

            public String getOrder_time() {
                return order_time;
            }

            public void setOrder_time(String order_time) {
                this.order_time = order_time;
            }

            public float getProduct_price() {
                return product_price;
            }

            public void setProduct_price(float product_price) {
                this.product_price = product_price;
            }

            public String getProduct_name() {
                return product_name;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }

            public priceInfoBean getPriceInfo() {
                return priceInfo;
            }

            public void setPriceInfo(priceInfoBean priceInfo) {
                this.priceInfo = priceInfo;
            }

            public static class priceInfoBean {
                private float price;
                private float new_price;

                public float getPrice() {
                    return price;
                }

                public void setPrice(float price) {
                    this.price = price;
                }

                public float getNew_price() {
                    return new_price;
                }

                public void setNew_price(float new_price) {
                    this.new_price = new_price;
                }
            }
        }

    }
    //?????????????????????????????????
    public void getMyCouponList(String type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycoupon != null){
                mSmart_model_my_mycoupon.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout mycoupon_end = mMyCouponView.findViewById(R.id.mycoupon_end);
        mycoupon_end.setVisibility(View.INVISIBLE);
        LinearLayout modelmy_mycoupon_main_content = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_content);
        modelmy_mycoupon_main_content.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyCouponCurrentPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        HashMap<String, Integer> paramsMap = new HashMap<>();
        Gson gson = new Gson();
        paramsMap.put("pageNum", mMyCouponCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCouponPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.getMyCouponListMessage(body)
                .enqueue(new Callback<MyCoupon>() {
                    @Override
                    public void onResponse(Call<MyCoupon> call, Response<MyCoupon> response) {
                        MyCoupon myCoupon = response.body();
                        if (myCoupon == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myCoupon.getCode(),"")){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = myCoupon.getCode();
                        if (code != 200){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyCoupon.DataBean data = myCoupon.getData();
                        if (data == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyCouponSum = data.getTotal();
                        if (myCoupon.getNot_used() != null){
                            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
                            modelmy_mycoupon_tab_notused.setText("?????????(" + myCoupon.getNot_used() + ")");
                        }
                        if (myCoupon.getAlready_used() != null){
                            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                            modelmy_mycoupon_tab_alreadyused.setText("?????????(" + myCoupon.getAlready_used() + ")");
                        }
                        if (myCoupon.getExpired() != null){
                            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                            modelmy_mycoupon_tab_expired.setText("?????????(" + myCoupon.getExpired() + ")");
                        }
                        List<MyCoupon.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishRefresh();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            MyCoupon.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            //???????????????????????????????????????????????????
                            MyCouponShow_MyCoupon(modelmy_mycoupon_main_content,listBean);
                        }
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyCoupon> call, Throwable t) {
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishRefresh();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    //?????????????????????????????????
    public void getMyCouponListMore(String type){
        if (mMainContext.mStuId.equals("")){
            if (mSmart_model_my_mycoupon != null){
                mSmart_model_my_mycoupon.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        LinearLayout modelmy_mycoupon_main_content = mMyCouponView.findViewById(R.id.modelmy_mycoupon_main_content);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        mMyCouponCurrentPage = mMyCouponCurrentPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        HashMap<String, Integer> paramsMap = new HashMap<>();
        Gson gson = new Gson();
        paramsMap.put("pageNum", mMyCouponCurrentPage);//?????????
        paramsMap.put("pageSize",mMyCouponPageCount);//????????????
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));//??????id
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("type", type);
        String strEntity = gson.toJson(paramsMap);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.getMyCouponListMessage(body)
                .enqueue(new Callback<MyCoupon>() {
                    @Override
                    public void onResponse(Call<MyCoupon> call, Response<MyCoupon> response) {
                        MyCoupon myCoupon = response.body();
                        if (myCoupon == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(myCoupon.getCode(),"")){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = myCoupon.getCode();
                        if (code != 200){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        MyCoupon.DataBean data = myCoupon.getData();
                        if (data == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        mMyCouponSum = data.getTotal();
                        if (myCoupon.getNot_used() != null){
                            TextView modelmy_mycoupon_tab_notused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_notused);
                            modelmy_mycoupon_tab_notused.setText("?????????(" + myCoupon.getNot_used() + ")");
                        }
                        if (myCoupon.getAlready_used() != null){
                            TextView modelmy_mycoupon_tab_alreadyused = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_alreadyused);
                            modelmy_mycoupon_tab_alreadyused.setText("?????????(" + myCoupon.getAlready_used() + ")");
                        }
                        if (myCoupon.getExpired() != null){
                            TextView modelmy_mycoupon_tab_expired = mMyCouponView.findViewById(R.id.modelmy_mycoupon_tab_expired);
                            modelmy_mycoupon_tab_expired.setText("?????????(" + myCoupon.getExpired() + ")");
                        }
                        List<MyCoupon.DataBean.ListBean> list = data.getList();
                        if (list == null){
                            if (mSmart_model_my_mycoupon != null){
                                mSmart_model_my_mycoupon.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            MyCoupon.DataBean.ListBean listBean = list.get(i);
                            if (listBean == null){
                                continue;
                            }
                            //???????????????????????????????????????????????????
                            MyCouponShow_MyCoupon(modelmy_mycoupon_main_content,listBean);
                        }
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    @Override
                    public void onFailure(Call<MyCoupon> call, Throwable t) {
                        if (mSmart_model_my_mycoupon != null){
                            mSmart_model_my_mycoupon.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }

    private void CheckBeforeExchangingCoupons(String coupon) {
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("discount_code_value", coupon);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.checkBeforeExchangingCoupons(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (response.body() == null){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(),response.body().getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                String message = response.body().getErrorMsg();
                if (message == null){
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (message.equals("ok")) {
                    redeemCoupons(coupon);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                } else {
                    Log.e("TAG", "CheckBeforeExchangingCoupons  onErrorCode: " + code);
                    Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    private void redeemCoupons(String coupon) {
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("discount_code_value", coupon);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.redeemCoupons(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "redeemCoupons  onErrorCode: " + code);
                    Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (response.body() == null){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(),response.body().getErrorMsg())){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                if (mMyCouponDialog != null) {
                    mMyCouponDialog.cancel();
                }
                mSmart_model_my_mycoupon.autoRefresh();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mMainContext,"?????????????????????",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //?????????????????????
    public static class MyCoupon{
        /**
         * code : 200
         * data : {"total":1,"list":[{"subject_id":null,"dc_denomination":"0.02","course_id":0,"service_life_start_time":"2019-10-30","preferential_way":"??????","stu_id":1,"small_discount_status":"?????????","????????????":"?????????????????????","product_type":"??????","course_package_id":null,"project_id":null,"service_life_end_time":"2019-11-01","small_discount_id":9,"preferential_scope":"????????????"}],"pageNum":1,"pageSize":10,"size":1,"startRow":1,"endRow":1,"pages":1,"prePage":0,"nextPage":0,"isFirstPage":true,"isLastPage":true,"hasPreviousPage":false,"hasNextPage":false,"navigatePages":8,"navigatepageNums":[1],"navigateFirstPage":1,"navigateLastPage":1,"firstPage":1,"lastPage":1}
         */

        private int code;
        private DataBean data;
        private Integer expired;
        private Integer not_used;
        private Integer already_used;

        public Integer getAlready_used() {
            return already_used;
        }

        public Integer getExpired() {
            return expired;
        }

        public Integer getNot_used() {
            return not_used;
        }

        public void setAlready_used(Integer already_used) {
            this.already_used = already_used;
        }

        public void setExpired(Integer expired) {
            this.expired = expired;
        }

        public void setNot_used(Integer not_used) {
            this.not_used = not_used;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * total : 1
             * list : [{"subject_id":null,"dc_denomination":"0.02","course_id":0,"service_life_start_time":"2019-10-30","preferential_way":"??????","stu_id":1,"small_discount_status":"?????????","????????????":"?????????????????????","product_type":"??????","course_package_id":null,"project_id":null,"service_life_end_time":"2019-11-01","small_discount_id":9,"preferential_scope":"????????????"}]
             * pageNum : 1
             * pageSize : 10
             * size : 1
             * startRow : 1
             * endRow : 1
             * pages : 1
             * prePage : 0
             * nextPage : 0
             * isFirstPage : true
             * isLastPage : true
             * hasPreviousPage : false
             * hasNextPage : false
             * navigatePages : 8
             * navigatepageNums : [1]
             * navigateFirstPage : 1
             * navigateLastPage : 1
             * firstPage : 1
             * lastPage : 1
             */

            private int total;
            private int pageNum;
            private int pageSize;
            private int size;
            private int startRow;
            private int endRow;
            private int pages;
            private int prePage;
            private int nextPage;
            private boolean isFirstPage;
            private boolean isLastPage;
            private boolean hasPreviousPage;
            private boolean hasNextPage;
            private int navigatePages;
            private int navigateFirstPage;
            private int navigateLastPage;
            private int firstPage;
            private int lastPage;
            private List<ListBean> list;
            private List<Integer> navigatepageNums;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPageNum() {
                return pageNum;
            }

            public void setPageNum(int pageNum) {
                this.pageNum = pageNum;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getStartRow() {
                return startRow;
            }

            public void setStartRow(int startRow) {
                this.startRow = startRow;
            }

            public int getEndRow() {
                return endRow;
            }

            public void setEndRow(int endRow) {
                this.endRow = endRow;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public int getPrePage() {
                return prePage;
            }

            public void setPrePage(int prePage) {
                this.prePage = prePage;
            }

            public int getNextPage() {
                return nextPage;
            }

            public void setNextPage(int nextPage) {
                this.nextPage = nextPage;
            }

            public boolean isIsFirstPage() {
                return isFirstPage;
            }

            public void setIsFirstPage(boolean isFirstPage) {
                this.isFirstPage = isFirstPage;
            }

            public boolean isIsLastPage() {
                return isLastPage;
            }

            public void setIsLastPage(boolean isLastPage) {
                this.isLastPage = isLastPage;
            }

            public boolean isHasPreviousPage() {
                return hasPreviousPage;
            }

            public void setHasPreviousPage(boolean hasPreviousPage) {
                this.hasPreviousPage = hasPreviousPage;
            }

            public boolean isHasNextPage() {
                return hasNextPage;
            }

            public void setHasNextPage(boolean hasNextPage) {
                this.hasNextPage = hasNextPage;
            }

            public int getNavigatePages() {
                return navigatePages;
            }

            public void setNavigatePages(int navigatePages) {
                this.navigatePages = navigatePages;
            }

            public int getNavigateFirstPage() {
                return navigateFirstPage;
            }

            public void setNavigateFirstPage(int navigateFirstPage) {
                this.navigateFirstPage = navigateFirstPage;
            }

            public int getNavigateLastPage() {
                return navigateLastPage;
            }

            public void setNavigateLastPage(int navigateLastPage) {
                this.navigateLastPage = navigateLastPage;
            }

            public int getFirstPage() {
                return firstPage;
            }

            public void setFirstPage(int firstPage) {
                this.firstPage = firstPage;
            }

            public int getLastPage() {
                return lastPage;
            }

            public void setLastPage(int lastPage) {
                this.lastPage = lastPage;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public List<Integer> getNavigatepageNums() {
                return navigatepageNums;
            }

            public void setNavigatepageNums(List<Integer> navigatepageNums) {
                this.navigatepageNums = navigatepageNums;
            }

            public static class ListBean {
                /**
                 * subject_id : null
                 * dc_denomination : 0.02
                 * course_id : 0
                 * service_life_start_time : 2019-10-30
                 * preferential_way : ??????
                 * stu_id : 1
                 * small_discount_status : ?????????
                 * ???????????? : ?????????????????????
                 * product_type : ??????
                 * course_package_id : null
                 * project_id : null
                 * service_life_end_time : 2019-11-01
                 * small_discount_id : 9
                 * preferential_scope : ????????????
                 */

                private Object subject_id;
                private String dc_denomination;
                private int course_id;
                private String service_life_start_time;
                private String preferential_way;
                private int stu_id;
                private String small_discount_status;
                private String scope;
                private String product_type;
                private Object course_package_id;
                private Object project_id;
                private String service_life_end_time;
                private int small_discount_id;
                private String preferential_scope;

                public Object getSubject_id() {
                    return subject_id;
                }

                public void setSubject_id(Object subject_id) {
                    this.subject_id = subject_id;
                }

                public String getDc_denomination() {
                    return dc_denomination;
                }

                public void setDc_denomination(String dc_denomination) {
                    this.dc_denomination = dc_denomination;
                }

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public String getService_life_start_time() {
                    return service_life_start_time;
                }

                public void setService_life_start_time(String service_life_start_time) {
                    this.service_life_start_time = service_life_start_time;
                }

                public String getPreferential_way() {
                    return preferential_way;
                }

                public void setPreferential_way(String preferential_way) {
                    this.preferential_way = preferential_way;
                }

                public int getStu_id() {
                    return stu_id;
                }

                public void setStu_id(int stu_id) {
                    this.stu_id = stu_id;
                }

                public String getSmall_discount_status() {
                    return small_discount_status;
                }

                public void setSmall_discount_status(String small_discount_status) {
                    this.small_discount_status = small_discount_status;
                }

                public String getscope() {
                    return scope;
                }

                public void setscope(String scope) {
                    this.scope = scope;
                }

                public String getProduct_type() {
                    return product_type;
                }

                public void setProduct_type(String product_type) {
                    this.product_type = product_type;
                }

                public Object getCourse_package_id() {
                    return course_package_id;
                }

                public void setCourse_package_id(Object course_package_id) {
                    this.course_package_id = course_package_id;
                }

                public Object getProject_id() {
                    return project_id;
                }

                public void setProject_id(Object project_id) {
                    this.project_id = project_id;
                }

                public String getService_life_end_time() {
                    return service_life_end_time;
                }

                public void setService_life_end_time(String service_life_end_time) {
                    this.service_life_end_time = service_life_end_time;
                }

                public int getSmall_discount_id() {
                    return small_discount_id;
                }

                public void setSmall_discount_id(int small_discount_id) {
                    this.small_discount_id = small_discount_id;
                }

                public String getPreferential_scope() {
                    return preferential_scope;
                }

                public void setPreferential_scope(String preferential_scope) {
                    this.preferential_scope = preferential_scope;
                }
            }
        }
    }
}
