package com.android.jwjy.zkktproduct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.android.jwjy.zkktproduct.consts.PlayType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import net.sqlcipher.Cursor;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.fragment.NewImagePagerDialogFragment;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by dayuer on 19/7/2.
 * 课程详情
 */
public class ModelCourseCover implements View.OnClickListener, ModelOrderDetailsInterface ,ControllerOkManagerDownload.IProgress{
    private View modelCourse, mListView, mDetailsView, mQuestionView, mQuestionViewAdd, mQuestionDetailsView, mDownloadManagerView;
    private RecyclerView mRecyclerView;
    private ArrayList<ControllerPictureBean> mPictureBeansList;
    private ControllerPictureAdapter mPictureAdapter;
    private ArrayList<String> selPhotosPath = null;//选中的图片路径集合
    private ControlMainActivity mControlMainActivity = null;
    private ModelCourseCoverOnClickListener mModelCourseCoverOnClickListener = null;
    private int height = 1344;
    private int width = 720;
    private String mCurrentTab = "Details";  //当前标签为详情还是目录
    private String mCurrentCatalogTab = "Record"; //当前标签是录播还是直播
    private CourseInfo mCourseInfo;
    //    private Map<String, View> CourseQuestionViewMap = new HashMap<>();
//    private Map<String, List<CourseQuestionInfo>> CourseQuestionDetailsViewMap = new HashMap<>();
    private String mPage = "Detail";
    private boolean mQuestionPublishImage = false; //课程问答是否发布图片
    private boolean mQuestionPublishTitle = false;//课程问答是否发布标题
    private boolean mQuestionPublishContent = false;//课程问答是否发布内容
    private ControllerCenterDialog mMyDialog; //居中的对话框
    private ControllerPopDialog mCourseDownloadDialog = null;
    private ControllerCustomDialog mCustomDialog = null;

    private boolean mIsPublish = true;

    //问答列表-刷新控件
    private SmartRefreshLayout course_question_layout_refresh = null,course_questiondetails_layout_refresh = null;
    //问答列表分页
    private int mCourseQuestionPage = 0;
    private int mCourseQuestionCount = 10;
    private int mCourseQuestionSum = 0; //问答总数

    //课程问答详情分页
    private int mCourseQuestionDetailsPage = 0;
    private int mCourseQuestionDetailsCount = 10;
    private int mCourseQuestionDetailsSum = 0; //问答详情总数

    public void ModelCourseCoverOnClickListenerSet(ModelCourseCoverOnClickListener modelCourseCoverOnClickListener) {
        mModelCourseCoverOnClickListener = modelCourseCoverOnClickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRecive() {
        CourseDetailsShow();
    }

    @Override
    public void onProgress(int progress) {

    }

    public interface ModelCourseCoverOnClickListener {
        void OnClickListener(View view, ModelCourseCover modelCourseCover);
    }

    public View ModelCourseCover(Context context, CourseInfo courseInfo) {
        mControlMainActivity = (ControlMainActivity) context;
        if (courseInfo == null) {
            return null;
        }
        mCourseInfo = new CourseInfo(courseInfo);
        DisplayMetrics dm = context.getResources().getDisplayMetrics(); //获取屏幕分辨率
        height = dm.heightPixels;
        width = dm.widthPixels;
        modelCourse = LayoutInflater.from(context).inflate(R.layout.modelcourse_layout, null);
        mListView = LayoutInflater.from(context).inflate(R.layout.modelcourselist_layout, null);
        if (mDetailsView == null) {
            mDetailsView = LayoutInflater.from(context).inflate(R.layout.modelcoursedetails_layout, null);
            modelCourse.setOnClickListener(v -> {
                if (mModelCourseCoverOnClickListener == null || modelCourse == null || mPage.equals("Question")
                        || mPage.equals("QuestionAdd") || mPage.equals("QuestionDetails") || mPage.equals("DownloadManager")) {
                    return;
                }
                mModelCourseCoverOnClickListener.OnClickListener(v, this);
                //跳转到课程的详细界面
                CourseDetailsShow();
            });
            //课程详情按钮
            TextView course_details_label = mDetailsView.findViewById(R.id.course_details_label);
            TextView course_details_label1 = mDetailsView.findViewById(R.id.course_details_label1);
            //课程阶段按钮
            TextView course_coursestage_label = mDetailsView.findViewById(R.id.course_coursestage_label);
            TextView course_coursestage_label1 = mDetailsView.findViewById(R.id.course_coursestage_label1);
            //收藏课程
            LinearLayout course_details_bottomlayout_collect = mDetailsView.findViewById(R.id.course_details_bottomlayout_collect);
            LinearLayout course_details_bottomlayout_collect1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collect1);
            //课程问答
            LinearLayout course_details_bottomlayout_question = mDetailsView.findViewById(R.id.course_details_bottomlayout_question);
            //直播
            LinearLayout course_catalog_label_livemain = mDetailsView.findViewById(R.id.course_catalog_label_livemain);
            LinearLayout course_catalog_label_livemain1 = mDetailsView.findViewById(R.id.course_catalog_label_livemain1);
            //录播
            LinearLayout course_catalog_label_recordmain = mDetailsView.findViewById(R.id.course_catalog_label_recordmain);
            LinearLayout course_catalog_label_recordmain1 = mDetailsView.findViewById(R.id.course_catalog_label_recordmain1);
            ImageView course_fl_layout_title_download = mDetailsView.findViewById(R.id.course_fl_layout_title_download);
            //下载按钮
            ImageView course_details_download_button = mDetailsView.findViewById(R.id.course_details_download_button);
            ImageView course_details_download_button1 = mDetailsView.findViewById(R.id.course_details_download_button1);
            //立即购买按钮
            Button course_details_buy_button = mDetailsView.findViewById(R.id.course_details_buy_button);
            course_details_buy_button.setOnClickListener(this);
            course_details_download_button1.setOnClickListener(this);
            course_details_download_button.setOnClickListener(this);
            course_fl_layout_title_download.setOnClickListener(this);
            course_details_label.setOnClickListener(this);
            course_details_label1.setOnClickListener(this);
            course_coursestage_label.setOnClickListener(this);
            course_coursestage_label1.setOnClickListener(this);
            course_details_bottomlayout_collect.setOnClickListener(this);
            course_details_bottomlayout_collect1.setOnClickListener(this);
            course_details_bottomlayout_question.setOnClickListener(this);
            course_catalog_label_livemain.setOnClickListener(this);
            course_catalog_label_livemain1.setOnClickListener(this);
            course_catalog_label_recordmain.setOnClickListener(this);
            course_catalog_label_recordmain1.setOnClickListener(this);
        }
        //隐藏所有布局
        HideAllLayout();
        //初始化课程信息并显示
        CourseListInit(courseInfo);
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        course_main.addView(mListView);
        return modelCourse;
    }

    //展示课程详情
    public void CourseDetailsShow() {
        //获取课程详情
        getSingleCourseDetails();
        //获取课程目录
        getSingleCourseCatalog();
        if (modelCourse == null) {
            return;
        }
        mPage = "Detail";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        course_main.addView(mDetailsView);
        //默认显示目录界面
        TextView course_details_label = mDetailsView.findViewById(R.id.course_details_label);
        TextView course_details_label1 = mDetailsView.findViewById(R.id.course_details_label1);
        course_details_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        course_details_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        TextView course_coursestage_label = mDetailsView.findViewById(R.id.course_coursestage_label);
        TextView course_coursestage_label1 = mDetailsView.findViewById(R.id.course_coursestage_label1);
        course_coursestage_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        course_coursestage_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        //修改body为课程详情
        LinearLayout course_details_label_content_layout = mDetailsView.findViewById(R.id.course_details_label_content_layout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_details_label_content_layout.getLayoutParams();
        LP.height = 0;
        course_details_label_content_layout.setLayoutParams(LP);
        course_details_label_content_layout.setVisibility(View.INVISIBLE);
        LinearLayout course_catalog_label_content_layout_main = mDetailsView.findViewById(R.id.course_catalog_label_content_layout_main);
        LP = (LinearLayout.LayoutParams) course_catalog_label_content_layout_main.getLayoutParams();
        LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        course_catalog_label_content_layout_main.setLayoutParams(LP);
        course_catalog_label_content_layout_main.setVisibility(View.VISIBLE);
        ImageView course_imgv_cursor = mDetailsView.findViewById(R.id.course_imgv_cursor);
        ImageView course_imgv_cursor1 = mDetailsView.findViewById(R.id.course_imgv_cursor1);
        int x = width / 4 - mDetailsView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        course_imgv_cursor.setX(x);
        course_imgv_cursor1.setX(x);
        if (mCourseInfo.mCourseType.equals("直播")) {
            LinearLayout course_catalog_label_recordmain = mDetailsView.findViewById(R.id.course_catalog_label_recordmain);
            LinearLayout course_catalog_label_recordmain1 = mDetailsView.findViewById(R.id.course_catalog_label_recordmain1);
            course_catalog_label_recordmain.setVisibility(View.INVISIBLE);
            course_catalog_label_recordmain1.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_label_recordmain.getLayoutParams();
            ll.width = 0;
            course_catalog_label_recordmain.setLayoutParams(ll);
            ll = (LinearLayout.LayoutParams) course_catalog_label_recordmain1.getLayoutParams();
            ll.width = 0;
            course_catalog_label_recordmain1.setLayoutParams(ll);
            //将直播变为选中状态
            ImageView course_catalog_label_liveimage = mDetailsView.findViewById(R.id.course_catalog_label_liveimage);
            TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
            ImageView course_catalog_label_liveimage1 = mDetailsView.findViewById(R.id.course_catalog_label_liveimage1);
            TextView course_catalog_label_live1 = mDetailsView.findViewById(R.id.course_catalog_label_live1);
            course_catalog_label_liveimage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_blue));
            course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
            course_catalog_label_liveimage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_blue));
            course_catalog_label_live1.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
            mCurrentCatalogTab = "Live";
        } else if (mCourseInfo.mCourseType.equals("录播")){
            LinearLayout course_catalog_label_livemain = mDetailsView.findViewById(R.id.course_catalog_label_livemain);
            LinearLayout course_catalog_label_livemain1 = mDetailsView.findViewById(R.id.course_catalog_label_livemain1);
            course_catalog_label_livemain.setVisibility(View.INVISIBLE);
            course_catalog_label_livemain1.setVisibility(View.INVISIBLE);
        }
    }

    //展示课程问答界面
    public void CourseQuestionShow() {
        if (modelCourse == null) {
            return;
        }
        mPage = "Question";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        if (mQuestionView == null) {
            mQuestionView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question, null);
            //Smart_homepage_layout1
            course_question_layout_refresh = mQuestionView.findViewById(R.id.course_question_layout_refresh);
            LinearLayout course_question_layout_content = mQuestionView.findViewById(R.id.course_question_layout_content);
            course_question_layout_refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mCourseQuestionSum <= mCourseQuestionPage * mCourseQuestionCount){
                        LinearLayout course_end = mQuestionView.findViewById(R.id.course_question_end);
                        course_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    //控件加载更多
                    QueryStuCourseQuestionMore(course_question_layout_content);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //控件的刷新方法
                    QueryStuCourseQuestion(course_question_layout_content);
                }
            });
        }
        ImageView course_question_layout_return_button1 = mQuestionView.findViewById(R.id.course_question_layout_return_button1);
        ImageView course_question_layout_add_button1 = mQuestionView.findViewById(R.id.course_question_layout_add_button1);
        course_question_layout_return_button1.setOnClickListener(this);
        course_question_layout_add_button1.setOnClickListener(this);
        LinearLayout course_question_layout_content = mQuestionView.findViewById(R.id.course_question_layout_content);
        course_question_layout_content.removeAllViews();
        TextView course_question_layout_titletext = mQuestionView.findViewById(R.id.course_question_layout_titletext);
        course_question_layout_titletext.setText("精选问答(" + mCourseQuestionSum + ")");
        course_main.addView(mQuestionView);
        //查询课程问答列表
        QueryStuCourseQuestion(course_question_layout_content);
    }

    //隐藏所有布局
    public void HideAllLayout() {
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        course_main.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_details_label1://点击课程详情
                LinearLayout course_label = mDetailsView.findViewById(R.id.course_label);
                if (course_label.getAlpha() == 0){
                    break;
                }
            case R.id.course_details_label:{
                TextView course_details_label = mDetailsView.findViewById(R.id.course_details_label);
                TextView course_details_label1 = mDetailsView.findViewById(R.id.course_details_label1);
                //设置文件的字体大小
                course_details_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                course_details_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                TextView course_coursestage_label = mDetailsView.findViewById(R.id.course_coursestage_label);
                TextView course_coursestage_label1 = mDetailsView.findViewById(R.id.course_coursestage_label1);
                course_coursestage_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                course_coursestage_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));

                LinearLayout course_catalog_label1 = mDetailsView.findViewById(R.id.course_catalog_label1);
                View coursepacket_details_line6 = mDetailsView.findViewById(R.id.coursepacket_details_line6);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_label1.getLayoutParams();
                rl.height = 0;
                course_catalog_label1.setLayoutParams(rl);
                rl = (RelativeLayout.LayoutParams) coursepacket_details_line6.getLayoutParams();
                rl.height = 0;
                coursepacket_details_line6.setLayoutParams(rl);
                //修改body为课程详情
                LinearLayout course_details_label_content_layout = mDetailsView.findViewById(R.id.course_details_label_content_layout);
                LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_details_label_content_layout.getLayoutParams();
                LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                course_details_label_content_layout.setLayoutParams(LP);
                course_details_label_content_layout.setVisibility(View.VISIBLE);
                LinearLayout course_catalog_label_content_layout_main = mDetailsView.findViewById(R.id.course_catalog_label_content_layout_main);
                LP = (LinearLayout.LayoutParams) course_catalog_label_content_layout_main.getLayoutParams();
                LP.height = 0;
                course_catalog_label_content_layout_main.setLayoutParams(LP);
                course_catalog_label_content_layout_main.setVisibility(View.INVISIBLE);
                if (!mCurrentTab.equals("Details")) {
                    Animation animation = new TranslateAnimation(0, width / 2, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    ImageView course_imgv_cursor = mDetailsView.findViewById(R.id.course_imgv_cursor);
                    course_imgv_cursor.startAnimation(animation);
                    ImageView course_imgv_cursor1 = mDetailsView.findViewById(R.id.course_imgv_cursor1);
                    course_imgv_cursor1.startAnimation(animation);
                }
                mCurrentTab = "Details";
                break;
            }
            case R.id.course_coursestage_label1://点击课程目录
                course_label = mDetailsView.findViewById(R.id.course_label);
                if (course_label.getAlpha() == 0){
                    break;
                }
            case R.id.course_coursestage_label: {
                TextView course_details_label = mDetailsView.findViewById(R.id.course_details_label);
                TextView course_details_label1 = mDetailsView.findViewById(R.id.course_details_label1);
                course_details_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                course_details_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                TextView course_coursestage_label = mDetailsView.findViewById(R.id.course_coursestage_label);
                TextView course_coursestage_label1 = mDetailsView.findViewById(R.id.course_coursestage_label1);
                course_coursestage_label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                course_coursestage_label1.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                //修改body为目录
                LinearLayout course_catalog_label_content_layout_main = mDetailsView.findViewById(R.id.course_catalog_label_content_layout_main);
                LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_catalog_label_content_layout_main.getLayoutParams();
                LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_label_content_layout_main.setLayoutParams(LP);
                course_catalog_label_content_layout_main.setVisibility(View.VISIBLE);
                LinearLayout course_details_label_content_layout = mDetailsView.findViewById(R.id.course_details_label_content_layout);
                LP = (LinearLayout.LayoutParams) course_details_label_content_layout.getLayoutParams();
                LP.height = 0;
                course_details_label_content_layout.setLayoutParams(LP);
                course_details_label_content_layout.setVisibility(View.INVISIBLE);
                if (mCurrentCatalogTab.equals("Live")){
                    CourseCatalogLiveInit(mCourseInfo);
                } else if (mCurrentCatalogTab.equals("Record")){
                    //修改body为录播
                    CourseCatalogRecordInit(mCourseInfo);
                }

                if (!mCurrentTab.equals("Catalog")) {
                    Animation animation = new TranslateAnimation(width / 2, 0, 0, 0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(200);
                    ImageView course_imgv_cursor = mDetailsView.findViewById(R.id.course_imgv_cursor);
                    course_imgv_cursor.startAnimation(animation);
                    ImageView course_imgv_cursor1 = mDetailsView.findViewById(R.id.course_imgv_cursor1);
                    course_imgv_cursor1.startAnimation(animation);
                }
                mCurrentTab = "Catalog";
                break;
            }
            //点击课程收藏
            case R.id.course_details_bottomlayout_collect1:
            case R.id.course_details_bottomlayout_collect: {
                CollectOrNotCollectCourses();
                break;
            }
            case R.id.course_question_add_layout_return_button1:{ //点击添加课程问答的返回按钮
                if (mIsPublish) {
                    //查询课程问答列表
                    CourseQuestionShow();
                }
                break;
            }
            case R.id.course_details_bottomlayout_question: {//点击课程问答
                //查询课程问答列表
                CourseQuestionShow();
                break;
            }
            //点击目录中的直播tab
            case R.id.course_catalog_label_livemain1:
                LinearLayout course_catalog_label1 = mDetailsView.findViewById(R.id.course_catalog_label1);
                if (course_catalog_label1.getAlpha() == 0){
                    break;
                }
            case R.id.course_catalog_label_livemain: {
                if (!mCurrentCatalogTab.equals("Live")) {
                    ImageView course_catalog_label_liveimage = mDetailsView.findViewById(R.id.course_catalog_label_liveimage);
                    TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
                    ImageView course_catalog_label_liveimage1 = mDetailsView.findViewById(R.id.course_catalog_label_liveimage1);
                    TextView course_catalog_label_live1 = mDetailsView.findViewById(R.id.course_catalog_label_live1);
                    course_catalog_label_liveimage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_blue));
                    course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    course_catalog_label_liveimage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_blue));
                    course_catalog_label_live1.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    ImageView course_catalog_label_recordimage = mDetailsView.findViewById(R.id.course_catalog_label_recordimage);
                    TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
                    ImageView course_catalog_label_recordimage1 = mDetailsView.findViewById(R.id.course_catalog_label_recordimage1);
                    TextView course_catalog_label_record1 = mDetailsView.findViewById(R.id.course_catalog_label_record1);
                    course_catalog_label_recordimage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_record_gray));
                    course_catalog_label_record.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    course_catalog_label_recordimage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_record_gray));
                    course_catalog_label_record1.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    //修改body为直播
                    CourseCatalogLiveInit(mCourseInfo);
                }
                mCurrentCatalogTab = "Live";
                break;
            }
            //点击目录中的录播tab
            case R.id.course_catalog_label_recordmain1:
                course_catalog_label1 = mDetailsView.findViewById(R.id.course_catalog_label1);
                if (course_catalog_label1.getAlpha() == 0){
                    break;
                }
            case R.id.course_catalog_label_recordmain: {
                if (!mCurrentCatalogTab.equals("Record")) {
                    ImageView course_catalog_label_liveimage = mDetailsView.findViewById(R.id.course_catalog_label_liveimage);
                    TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
                    ImageView course_catalog_label_liveimage1 = mDetailsView.findViewById(R.id.course_catalog_label_liveimage1);
                    TextView course_catalog_label_live1 = mDetailsView.findViewById(R.id.course_catalog_label_live1);
                    course_catalog_label_liveimage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_gray));
                    course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    course_catalog_label_liveimage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_live_gray));
                    course_catalog_label_live1.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    ImageView course_catalog_label_recordimage = mDetailsView.findViewById(R.id.course_catalog_label_recordimage);
                    TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
                    ImageView course_catalog_label_recordimage1 = mDetailsView.findViewById(R.id.course_catalog_label_recordimage1);
                    TextView course_catalog_label_record1 = mDetailsView.findViewById(R.id.course_catalog_label_record1);
                    course_catalog_label_recordimage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_record_blue));
                    course_catalog_label_record.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    course_catalog_label_recordimage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_record_blue));
                    course_catalog_label_record1.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    //修改body为录播
                    CourseCatalogRecordInit(mCourseInfo);
                }
                mCurrentCatalogTab = "Record";
                break;
            }
            //点击课程问答的返回按钮
            case R.id.course_question_layout_return_button1: {
                CourseDetailsShow();
                break;
            }
            //点击课程问答的添加按钮
            case R.id.course_question_layout_add_button1: {
                CourseQuestionAddInit();
                break;
            }
            case R.id.course_fl_layout_title_download:
            case R.id.course_details_download_button:
            case R.id.course_details_download_button1: {
                CourseDownloadInit();
                break;
            }
            case R.id.course_details_buy_button: { //课程详情购买
                Toast.makeText(mControlMainActivity,"此功能还在完善，敬请期待！",Toast.LENGTH_SHORT).show();
//                HideAllLayout();
//                RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
//                View view = mControlMainActivity.Page_OrderDetails(this,mCourseInfo,null,null);
//                course_main.addView(view);
                break;
            }
            default:
                break;
        }
    }

    private DialogInterface.OnKeyListener keylistener = (dialog, keyCode, event) -> {
        Log.i("TAG", "键盘code---" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog.dismiss();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {//删除键
            return false;
        } else {
            return true;
        }
    };

    //课程信息初始化
    public void CourseListInit(CourseInfo courseInfo) {
        ControllerCustomRoundAngleImageView imageView = mListView.findViewById(R.id.coursecover);
        imageView.setImageDrawable(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover));//如果没有url，加载默认图片
        if (courseInfo.mCourseCover != null) {
            Glide.with(mControlMainActivity).
                    load(courseInfo.mCourseCover).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Wain", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Wain", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                    return false;
                }
            })
                    .error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(imageView);
        }
        //设置课程类型
        ImageView coursetype = mListView.findViewById(R.id.coursetype);
        if (courseInfo.mCourseType.equals("直播")) {
            coursetype.setBackground(mListView.getResources().getDrawable(R.drawable.button_myclass_live));
        } else if (courseInfo.mCourseType.equals("录播")) {
            coursetype.setBackground(mListView.getResources().getDrawable(R.drawable.button_myclass_record));
        } else if (courseInfo.mCourseType.equals("混合")) {
            coursetype.setBackground(mListView.getResources().getDrawable(R.drawable.button_myclass_mix));
        }
        TextView courseNameTextView = mListView.findViewById(R.id.courseName);
        if (courseInfo.mCourseName != null) {
            courseNameTextView.setText(courseInfo.mCourseName);
        }
        TextView coursepricebeginTextView = mListView.findViewById(R.id.coursepricebegin);
        TextView coursepriceTextView = mListView.findViewById(R.id.courseprice);
        TextView coursepriceendTextView = mListView.findViewById(R.id.coursepriceend);
        if (courseInfo.mCoursePrice != null) {
            if (!courseInfo.mCoursePrice.equals("免费")) {
                coursepriceTextView.setTextColor(Color.RED);
                coursepricebeginTextView.setText("¥");
                String coursePriceS[] = courseInfo.mCoursePrice.split("\\.");
                if (coursePriceS.length > 1) {
                    coursepriceTextView.setText(coursePriceS[0]);
                    coursepriceendTextView.setText("." + coursePriceS[1]);
                } else {
                    coursepriceTextView.setText(courseInfo.mCoursePrice);
                    coursepriceendTextView.setText(".00");
                }
                //设置原价格
                TextView coursepriceOldTextView = mListView.findViewById(R.id.coursepriceOld);
                //文字栅格化
                coursepriceOldTextView.setPaintFlags(coursepriceOldTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if (courseInfo.mCoursePriceOld != null) {
                    if (!courseInfo.mCoursePriceOld.equals("免费")) {
                        coursepriceOldTextView.setText("¥" + courseInfo.mCoursePriceOld);
                    }
                }
            } else {
                coursepriceTextView.setText(courseInfo.mCoursePrice);
                //设置原价格
                TextView coursepriceOldTextView = mListView.findViewById(R.id.coursepriceOld);
                coursepriceOldTextView.setText("");
            }
        }
    }

    //课程详情界面
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void CourseDetailsInit(CourseInfo courseInfo) {
        ImageView course_details_Cover = mDetailsView.findViewById(R.id.course_details_Cover);
        //课程界面
        if (courseInfo.mCourseCover != null) {
            Glide.with(mControlMainActivity).
                    load(courseInfo.mCourseCover).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }

                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                    return false;
                }
            })
                    .error(mControlMainActivity.getResources().getDrawable(R.drawable.modelcoursecover)).into(course_details_Cover);
        }
        //课程详情-课程名称
        TextView course_details_Name = mDetailsView.findViewById(R.id.course_details_Name);
        if (courseInfo.mCourseName != null) {
            course_details_Name.setText(courseInfo.mCourseName);
        }
        //课程详情-课程信息
        TextView course_details_content0 = mDetailsView.findViewById(R.id.course_details_content0);
        if (courseInfo.mCourseLearnPersonNum != null) {
            course_details_content0.setText(courseInfo.mCourseLearnPersonNum + "人已学习");
        }
        TextView course_details_content2 = mDetailsView.findViewById(R.id.course_details_content2);
        if (courseInfo.mCourseTotalHours != null) {
            course_details_content2.setText("课时数 " + courseInfo.mCourseTotalHours);
        }
        //课程价格
        TextView course_details_price = mDetailsView.findViewById(R.id.course_details_price);
        if (courseInfo.mCoursePrice != null) {
            if (!courseInfo.mCoursePrice.equals("免费")) {
                course_details_price.setTextColor(Color.RED);
                course_details_price.setText("¥" + courseInfo.mCoursePrice);
            } else {
                course_details_price.setText(courseInfo.mCoursePrice);
            }
        }
        //课程原价
        TextView course_details_priceOld = mDetailsView.findViewById(R.id.course_details_priceOld);
        //文字栅格化
        course_details_priceOld.setPaintFlags(course_details_priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (courseInfo.mCoursePriceOld != null) {
            if (!courseInfo.mCoursePriceOld.equals("免费")) {
                course_details_priceOld.setText("¥" + courseInfo.mCoursePriceOld);
            }
        }
        //课程有效期
        TextView course_details_periodofvalidity = mDetailsView.findViewById(R.id.course_details_periodofvalidity);
        Date date = null;
        String invalid_date_date = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            try {
                date = df.parse(courseInfo.mCourseValidityPeriod);
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
                    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                    invalid_date_date = df2.format(date1).toString();
                }
            }
        }
        course_details_periodofvalidity.setText("有效期至：" +  invalid_date_date);
        //课程简介
        TextView coursepacket_details_briefintroductioncontent = mDetailsView.findViewById(R.id.coursepacket_details_briefintroductioncontent);
        coursepacket_details_briefintroductioncontent.setText(courseInfo.mCourseMessage);
        AppBarLayout course_details_appbar = mDetailsView.findViewById(R.id.course_details_appbar);
        FrameLayout course_fl_layout = mDetailsView.findViewById(R.id.course_fl_layout);
        //课程名称
        TextView course_fl_layout_title = mDetailsView.findViewById(R.id.course_fl_layout_title);
        course_fl_layout_title.setText(courseInfo.mCourseName);
        //课程详情和课程阶段的标签层
        LinearLayout course_label = mDetailsView.findViewById(R.id.course_label);
        LinearLayout course_label1 = mDetailsView.findViewById(R.id.course_label1);
        //课程详情和课程阶段的标签层的下方游标
        ImageView course_imgv_cursor = mDetailsView.findViewById(R.id.course_imgv_cursor);
//        Matrix matrix = new Matrix();
//        matrix.postTranslate(width / 2, 0);
//        course_imgv_cursor.setImageMatrix(matrix);// 设置动画初始位置
        ImageView course_imgv_cursor1 = mDetailsView.findViewById(R.id.course_imgv_cursor1);
//        Matrix matrix1 = new Matrix();
//        matrix1.postTranslate(width / 2, 0);
//        course_imgv_cursor1.setImageMatrix(matrix1);// 设置动画初始位置
        //课程详情的内容  HTML格式
        TextView course_details_label_content = mDetailsView.findViewById(R.id.course_details_label_content);
        new ModelHtmlUtils(mControlMainActivity, course_details_label_content).setHtmlWithPic(courseInfo.mCourseDetails);
        course_details_appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            ImageView course_details_return_button = mDetailsView.findViewById(R.id.course_details_return_button);
            ImageView course_details_return_button1 = mDetailsView.findViewById(R.id.course_details_return_button1);
//            ImageView course_details_download_button = mDetailsView.findViewById(R.id.course_details_download_button);
//            ImageView course_details_download_button1 = mDetailsView.findViewById(R.id.course_details_download_button1);
            float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
            if (verticalOffset < -course_details_return_button.getY()) {
                course_fl_layout.setAlpha(percent);
                course_details_return_button.setVisibility(View.VISIBLE);
                course_details_return_button1.setVisibility(View.INVISIBLE);
//                course_details_download_button.setVisibility(View.VISIBLE);
//                course_details_download_button1.setVisibility(View.INVISIBLE);
            } else {
                course_fl_layout.setAlpha(0);
                course_details_return_button.setVisibility(View.INVISIBLE);
                course_details_return_button1.setVisibility(View.VISIBLE);
//                course_details_download_button.setVisibility(View.INVISIBLE);
//                course_details_download_button1.setVisibility(View.VISIBLE);
            }
            if (verticalOffset <= - course_details_Name.getY() - course_details_Name.getHeight()) {
                course_fl_layout_title.setVisibility(View.VISIBLE);
            } else {
                course_fl_layout_title.setVisibility(View.INVISIBLE);
            }
            LinearLayout course_catalog_label1 = mDetailsView.findViewById(R.id.course_catalog_label1);
//            LinearLayout course_catalog_label_content_layout_main = mDetailsView.findViewById(R.id.course_catalog_label_content_layout_main);
            View coursepacket_details_line6 = mDetailsView.findViewById(R.id.coursepacket_details_line6);
            LinearLayout course_catalog_label = mDetailsView.findViewById(R.id.course_catalog_label);
            if (mCurrentTab.equals("Details")) {
                course_catalog_label1.setAlpha(0);
                coursepacket_details_line6.setAlpha(0);
                course_catalog_label.setAlpha(1);
                if (verticalOffset <= -course_label1.getY() + course_label.getHeight() + course_label.getY()) {
                    course_label.setAlpha(percent);
                    course_label1.setAlpha(0);
                    course_imgv_cursor.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor));
                    course_imgv_cursor1.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor_white));
                } else {
                    course_label.setAlpha(0);
                    course_label1.setAlpha(1);
                    course_imgv_cursor.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor_white));
                    course_imgv_cursor1.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor));
                }
            } else if (mCurrentTab.equals("Catalog")) {
                course_catalog_label1.setAlpha(0);
                coursepacket_details_line6.setAlpha(0);
                course_catalog_label.setAlpha(1);
                if (verticalOffset <= -course_label1.getY() + course_label.getHeight() + course_label.getY()) {
                    course_label.setAlpha(percent);
                    course_label1.setAlpha(0);
                    course_catalog_label.setAlpha(0);
                    course_catalog_label1.setAlpha(percent);
                    coursepacket_details_line6.setAlpha(percent);
                    course_imgv_cursor.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor));
                    course_imgv_cursor1.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor_white));
                } else {
                    course_label.setAlpha(0);
                    course_label1.setAlpha(1);
                    course_catalog_label.setAlpha(1);
                    course_catalog_label1.setAlpha(0);
                    coursepacket_details_line6.setAlpha(0);
                    course_imgv_cursor.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor_white));
                    course_imgv_cursor1.setBackground(mControlMainActivity.getDrawable(R.drawable.image_cusor));
                }
            }
        });
        LinearLayout course_details_bottomlayout1 = mDetailsView.findViewById(R.id.course_details_bottomlayout1);
        LinearLayout course_details_bottomlayout = mDetailsView.findViewById(R.id.course_details_bottomlayout);
        if (courseInfo.mCourseIsHave.equals("1")) {
            //已购买的课程将按钮栏替换掉
            course_details_bottomlayout1.setVisibility(View.VISIBLE);
            course_details_bottomlayout.setVisibility(View.INVISIBLE);
        } else {
            course_details_bottomlayout1.setVisibility(View.INVISIBLE);
            course_details_bottomlayout.setVisibility(View.VISIBLE);
        }
        if (courseInfo.mCourseIsStartLearn.equals("1")){
            Button course_details_buy_button1 = mDetailsView.findViewById(R.id.course_details_buy_button1);
            course_details_buy_button1.setText("继续学习");
        }
        if (mCourseInfo.mCourseIsCollect.equals("1")){ //修改为收藏状态
            ImageView course_details_bottomlayout_collectImage1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage1);
            TextView course_details_bottomlayout_collectText1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText1);
            ImageView course_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage);
            TextView course_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText);
            course_details_bottomlayout_collectText1.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
            course_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
            course_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
            course_details_bottomlayout_collectImage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
        } else {
            ImageView course_details_bottomlayout_collectImage1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage1);
            TextView course_details_bottomlayout_collectText1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText1);
            ImageView course_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage);
            TextView course_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText);
            course_details_bottomlayout_collectText1.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
            course_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
            course_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
            course_details_bottomlayout_collectImage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
        }
    }

    //课程目录录播界面初始化
    public void CourseCatalogRecordInit(CourseInfo courseInfo) {
        if (courseInfo == null) {
            return;
        }
        if (courseInfo.mCourseChaptersInfoList == null) {
            return;
        }
        int recordCourseNum = 0;
        LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
        course_catalog_label_content_layout.removeAllViews();
        for (int i = 0; i < courseInfo.mCourseChaptersInfoList.size(); i++) {
            CourseChaptersInfo courseChaptersInfo = courseInfo.mCourseChaptersInfoList.get(i);
            if (courseChaptersInfo == null) {
                continue;
            }
            recordCourseNum = recordCourseNum + courseChaptersInfo.mCourseSectionsInfoList.size();
            View catalog_chapterview = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_chapter, null);
            TextView course_catalog_label_name = catalog_chapterview.findViewById(R.id.course_catalog_label_name);
            course_catalog_label_name.setText(courseChaptersInfo.mCourseChaptersName);
            ImageView course_catalog_label_arrow_down = catalog_chapterview.findViewById(R.id.course_catalog_label_arrow_down);
            ImageView course_catalog_label_arrow_right = catalog_chapterview.findViewById(R.id.course_catalog_label_arrow_right);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_label_arrow_down.getLayoutParams();
            ll.width = 0;
            course_catalog_label_arrow_down.setLayoutParams(ll);
            LinearLayout course_catalog_label_content = catalog_chapterview.findViewById(R.id.course_catalog_label_content);
            ModelExpandView course_catalog_label_expandView = catalog_chapterview.findViewById(R.id.course_catalog_label_expandView);
            LinearLayout course_catalog_label_namelayout = catalog_chapterview.findViewById(R.id.course_catalog_label_namelayout);
            course_catalog_label_namelayout.setClickable(true);
            course_catalog_label_namelayout.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                if (course_catalog_label_expandView.isExpand()) {
                    course_catalog_label_expandView.collapse();
                    //收缩隐藏布局
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_label_expandView.getLayoutParams();
                    rl.height = 0;
                    course_catalog_label_expandView.setLayoutParams(rl);
                    course_catalog_label_expandView.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_right.getLayoutParams();
                    ll1.width = catalog_chapterview.getResources().getDimensionPixelSize(R.dimen.dp6);
                    course_catalog_label_arrow_right.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_down.getLayoutParams();
                    ll1.width = 0;
                    course_catalog_label_arrow_down.setLayoutParams(ll1);
                } else {
                    if (courseChaptersInfo.mCourseSectionsInfoList.size() == 0) {
                        Toast.makeText(mControlMainActivity, "本章节暂时没有课程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    course_catalog_label_expandView.expand();
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_label_expandView.getLayoutParams();
                    rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    course_catalog_label_expandView.setLayoutParams(rl);
                    course_catalog_label_expandView.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_right.getLayoutParams();
                    ll1.width = 0;
                    course_catalog_label_arrow_right.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_down.getLayoutParams();
                    ll1.width = catalog_chapterview.getResources().getDimensionPixelSize(R.dimen.dp10);
                    course_catalog_label_arrow_down.setLayoutParams(ll1);
                    CourseCatalogRecordSectionsInit(course_catalog_label_content, courseChaptersInfo.mCourseChaptersId);
                }
            });
            //默认全部展开
            if (courseChaptersInfo.mCourseSectionsInfoList.size() != 0) {
                course_catalog_label_expandView.expand();
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_label_expandView.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_label_expandView.setLayoutParams(rl);
                course_catalog_label_expandView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_right.getLayoutParams();
                ll1.width = 0;
                course_catalog_label_arrow_right.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_label_arrow_down.getLayoutParams();
                ll1.width = catalog_chapterview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_label_arrow_down.setLayoutParams(ll1);
                CourseCatalogRecordSectionsInit(course_catalog_label_content, courseChaptersInfo.mCourseChaptersId);
            }
            course_catalog_label_content_layout.addView(catalog_chapterview);
            if (courseInfo.mCourseChaptersInfoList.size() - 1 == i) {
                //隐藏
                View course_catalog_label_line1 = catalog_chapterview.findViewById(R.id.course_catalog_label_line1);
                course_catalog_label_line1.setVisibility(View.INVISIBLE);
            }
        }
        TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
        TextView course_catalog_label_record1 = mDetailsView.findViewById(R.id.course_catalog_label_record1);
        course_catalog_label_record1.setText("录播(" + recordCourseNum + ")");
        course_catalog_label_record.setText("录播(" + recordCourseNum + ")");
        TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
        TextView course_catalog_label_live1 = mDetailsView.findViewById(R.id.course_catalog_label_live1);
        int liveCourseNum = courseInfo.mCourseClassTimeInfoTodayList.size() + courseInfo.mCourseClassTimeInfoBeforeList.size() + courseInfo.mCourseClassTimeInfoAfterList.size();
        course_catalog_label_live.setText("直播(" + liveCourseNum + ")");
        course_catalog_label_live1.setText("直播(" + liveCourseNum + ")");
    }

    //课程目录录播界面章节初始化
    public void CourseCatalogRecordSectionsInit(LinearLayout course_catalog_label_content, String id) {
        if (mCourseInfo == null) {
            return;
        }
        if (mCourseInfo.mCourseChaptersInfoList == null) {
            return;
        }
        CourseChaptersInfo courseChaptersInfo = null;
        for (int i = 0; i < mCourseInfo.mCourseChaptersInfoList.size(); i++) {
            if (mCourseInfo.mCourseChaptersInfoList.get(i).mCourseChaptersId.equals(id)) {
                courseChaptersInfo = mCourseInfo.mCourseChaptersInfoList.get(i);
                break;
            }
        }
        if (courseChaptersInfo == null) {
            return;
        }
        course_catalog_label_content.removeAllViews();
        for (int i = 0; i < courseChaptersInfo.mCourseSectionsInfoList.size(); i++) {
            CourseSectionsInfo courseSectionsInfo = courseChaptersInfo.mCourseSectionsInfoList.get(i);
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_chapter1, null);
            TextView course_catalog_record_chapter_name = view.findViewById(R.id.course_catalog_record_chapter_name);
            course_catalog_record_chapter_name.setText(courseSectionsInfo.mCourseSectionsName);
            TextView course_catalog_record_chapter_learnprogress = view.findViewById(R.id.course_catalog_record_chapter_learnprogress);
            course_catalog_record_chapter_learnprogress.setText(courseSectionsInfo.mCourseSectionsLearnProgress);
            TextView course_catalog_record_chapter_time = view.findViewById(R.id.course_catalog_record_chapter_time);
            course_catalog_record_chapter_time.setText(courseSectionsInfo.mCourseSectionsTime);
            TextView course_catalog_record_chapter_price = view.findViewById(R.id.course_catalog_record_chapter_price);
            course_catalog_record_chapter_price.setText(courseSectionsInfo.mCourseSectionsPrice);
            view.setOnClickListener(v -> {
//                courseSectionsInfo.mVideoId = "28b8e6b1e87340c2a9dcac78729ed24c";
                //判断是否有播放权限
                if (mCourseInfo.mCourseIsHave.equals("0")){
                    Toast.makeText(mControlMainActivity,"您还没有购买此课程",Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断是否为失效课程
                if (!mCourseInfo.mCourseValidityPeriod.equals("")){
                    long CourseValidityPeriod = 0;
                    long currentTime = System.currentTimeMillis();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Date date = null;
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(mCourseInfo.mCourseValidityPeriod);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            CourseValidityPeriod = date.getTime();
                        }
                    }
                    if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime){
                        Toast.makeText(mControlMainActivity,"您购买的课程已过期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                CourseCatalogRecordGo(courseSectionsInfo.mVideoId,courseSectionsInfo.mCourseSectionsId,
                        courseSectionsInfo.mCourseSectionsName, courseSectionsInfo.mCourseSectionsTime1);
            });
            course_catalog_label_content.addView(view);
            if (courseChaptersInfo.mCourseSectionsInfoList.size() != 1 && i != (courseChaptersInfo.mCourseSectionsInfoList.size() - 1)) {
                //添加横线
                View lineView = new View(mControlMainActivity);
                lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                course_catalog_label_content.addView(lineView);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                lineView.setLayoutParams(ll);
            }
        }
    }

    //课程目录直播界面初始化
    public void CourseCatalogLiveInit(CourseInfo courseInfo) {
        if (courseInfo == null) {
            return;
        }
        if (courseInfo.mCourseClassTimeInfoTodayList == null || courseInfo.mCourseClassTimeInfoBeforeList == null || courseInfo.mCourseClassTimeInfoAfterList == null) {
            return;
        }
        LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
        course_catalog_label_content_layout.removeAllViews();
        View catalog_chapter_liveview = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_live_chapter, null);
        //今日
        LinearLayout course_catalog_live_label_namelayout = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout);
        ImageView course_catalog_live_label_arrow_down = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down);
        ImageView course_catalog_live_label_arrow_right = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right);
        ModelExpandView course_catalog_live_label_expandView = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
        ll.width = 0;
        course_catalog_live_label_arrow_down.setLayoutParams(ll);
        LinearLayout course_catalog_live_label_content = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content);
        course_catalog_live_label_namelayout.setClickable(true);
        course_catalog_live_label_namelayout.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (course_catalog_live_label_expandView.isExpand()) {
                course_catalog_live_label_expandView.collapse();
                //收缩隐藏布局
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView.getLayoutParams();
                rl.height = 0;
                course_catalog_live_label_expandView.setLayoutParams(rl);
                course_catalog_live_label_expandView.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                course_catalog_live_label_arrow_right.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_down.setLayoutParams(ll1);
            } else {
                if (courseInfo.mCourseClassTimeInfoTodayList.size() == 0) {
                    Toast.makeText(mControlMainActivity, "今日暂时没有课程", Toast.LENGTH_SHORT).show();
                    return;
                }
                course_catalog_live_label_expandView.expand();
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_live_label_expandView.setLayoutParams(rl);
                course_catalog_live_label_expandView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_right.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content, "today");
            }
        });
        //默认全部展开
        if (courseInfo.mCourseClassTimeInfoTodayList.size() != 0) {
            course_catalog_live_label_expandView.expand();
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            course_catalog_live_label_expandView.setLayoutParams(rl);
            course_catalog_live_label_expandView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right.getLayoutParams();
            ll1.width = 0;
            course_catalog_live_label_arrow_right.setLayoutParams(ll1);
            ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
            ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
            course_catalog_live_label_arrow_down.setLayoutParams(ll1);
            CourseCatalogLiveClassTimeInit(course_catalog_live_label_content, "today");
        }

        //后续
        LinearLayout course_catalog_live_label_namelayout1 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout1);
        ImageView course_catalog_live_label_arrow_down1 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down1);
        ImageView course_catalog_live_label_arrow_right1 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right1);
        ModelExpandView course_catalog_live_label_expandView1 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView1);
        ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
        ll.width = 0;
        course_catalog_live_label_arrow_down1.setLayoutParams(ll);
        LinearLayout course_catalog_live_label_content1 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content1);
        course_catalog_live_label_namelayout1.setClickable(true);
        course_catalog_live_label_namelayout1.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (course_catalog_live_label_expandView1.isExpand()) {
                course_catalog_live_label_expandView1.collapse();
                //收缩隐藏布局
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView1.getLayoutParams();
                rl.height = 0;
                course_catalog_live_label_expandView1.setLayoutParams(rl);
                course_catalog_live_label_expandView1.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right1.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                course_catalog_live_label_arrow_right1.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_down1.setLayoutParams(ll1);
            } else {
                if (courseInfo.mCourseClassTimeInfoAfterList.size() == 0) {
                    Toast.makeText(mControlMainActivity, "后续暂时没有课程", Toast.LENGTH_SHORT).show();
                    return;
                }
                course_catalog_live_label_expandView1.expand();
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView1.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_live_label_expandView1.setLayoutParams(rl);
                course_catalog_live_label_expandView1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right1.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_right1.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down1.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content1, "after");
            }
        });

        //默认全部展开
        if (courseInfo.mCourseClassTimeInfoAfterList.size() != 0) {
            course_catalog_live_label_expandView1.expand();
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView1.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            course_catalog_live_label_expandView1.setLayoutParams(rl);
            course_catalog_live_label_expandView1.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right1.getLayoutParams();
            ll1.width = 0;
            course_catalog_live_label_arrow_right1.setLayoutParams(ll1);
            ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
            ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
            course_catalog_live_label_arrow_down1.setLayoutParams(ll1);
            CourseCatalogLiveClassTimeInit(course_catalog_live_label_content1, "after");
        }

        LinearLayout course_catalog_live_label_namelayout2 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout2);
        ImageView course_catalog_live_label_arrow_down2 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down2);
        ImageView course_catalog_live_label_arrow_right2 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right2);
        ModelExpandView course_catalog_live_label_expandView2 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView2);
        ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
        ll.width = 0;
        course_catalog_live_label_arrow_down2.setLayoutParams(ll);
        LinearLayout course_catalog_live_label_content2 = catalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content2);
        course_catalog_live_label_namelayout2.setClickable(true);
        course_catalog_live_label_namelayout2.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (course_catalog_live_label_expandView2.isExpand()) {
                course_catalog_live_label_expandView2.collapse();
                //收缩隐藏布局
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView2.getLayoutParams();
                rl.height = 0;
                course_catalog_live_label_expandView2.setLayoutParams(rl);
                course_catalog_live_label_expandView2.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right2.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                course_catalog_live_label_arrow_right2.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_down2.setLayoutParams(ll1);
            } else {
                if (courseInfo.mCourseClassTimeInfoBeforeList.size() == 0) {
                    Toast.makeText(mControlMainActivity, "历史暂时没有课程", Toast.LENGTH_SHORT).show();
                    return;
                }
                course_catalog_live_label_expandView2.expand();
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView2.getLayoutParams();
                rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_live_label_expandView2.setLayoutParams(rl);
                course_catalog_live_label_expandView2.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right2.getLayoutParams();
                ll1.width = 0;
                course_catalog_live_label_arrow_right2.setLayoutParams(ll1);
                ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
                ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down2.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content2, "before");
            }
        });
        //默认全部展开
        if (courseInfo.mCourseClassTimeInfoBeforeList.size() != 0) {
            course_catalog_live_label_expandView2.expand();
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) course_catalog_live_label_expandView2.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            course_catalog_live_label_expandView2.setLayoutParams(rl);
            course_catalog_live_label_expandView2.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_right2.getLayoutParams();
            ll1.width = 0;
            course_catalog_live_label_arrow_right2.setLayoutParams(ll1);
            ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
            ll1.width = catalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
            course_catalog_live_label_arrow_down2.setLayoutParams(ll1);
            CourseCatalogLiveClassTimeInit(course_catalog_live_label_content2, "before");
        }
        course_catalog_label_content_layout.addView(catalog_chapter_liveview);
        TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
        TextView course_catalog_label_live1 = mDetailsView.findViewById(R.id.course_catalog_label_live1);
        int liveCourseNum = courseInfo.mCourseClassTimeInfoTodayList.size() + courseInfo.mCourseClassTimeInfoBeforeList.size() + courseInfo.mCourseClassTimeInfoAfterList.size();
        course_catalog_label_live.setText("直播(" + liveCourseNum + ")");
        course_catalog_label_live1.setText("直播(" + liveCourseNum + ")");
    }

    //课程目录直播界面课次初始化
    private void CourseCatalogLiveClassTimeInit(LinearLayout course_catalog_label_content, String type) {
        if (mCourseInfo == null || type == null) {
            return;
        }
        if (type.equals("today")) {
            if (mCourseInfo.mCourseClassTimeInfoTodayList == null) {
                return;
            }
            course_catalog_label_content.removeAllViews();
            CourseClassTimeInfo courseClassTimeInfo = null;
            for (int i = 0; i < mCourseInfo.mCourseClassTimeInfoTodayList.size(); i++) {
                courseClassTimeInfo = mCourseInfo.mCourseClassTimeInfoTodayList.get(i);
                if (courseClassTimeInfo == null) {
                    continue;
                }
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.mCourseClassTimeName);
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.mCourseClassTimeStartTime);
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoTodayList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoTodayList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mControlMainActivity);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                CourseClassTimeInfo finalCourseClassTimeInfo = courseClassTimeInfo;
                view.setOnClickListener(v->{
                    //判断是否有播放权限
                    if (mCourseInfo.mCourseIsHave.equals("0")){
                        Toast.makeText(mControlMainActivity,"您还没有购买此课程",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.mCourseValidityPeriod.equals("")){
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Date date = null;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            try {
                                date = df.parse(mCourseInfo.mCourseValidityPeriod);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                CourseValidityPeriod = date.getTime();
                            }
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime){
                            Toast.makeText(mControlMainActivity,"您购买的课程已过期",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (finalCourseClassTimeInfo.liveStatus == 2 || finalCourseClassTimeInfo.liveStatus == 1){
                        mControlMainActivity.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.mCourseClassTimeId),2, PlayType.LIVE);
                    } else if (finalCourseClassTimeInfo.liveStatus == 3) {
                        mControlMainActivity.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.mCourseClassTimeId), 2, PlayType.PLAYBACK);
                    }
                });
            }
        } else if (type.equals("before")) {
            if (mCourseInfo.mCourseClassTimeInfoBeforeList == null) {
                return;
            }
            course_catalog_label_content.removeAllViews();
            CourseClassTimeInfo courseClassTimeInfo = null;
            for (int i = 0; i < mCourseInfo.mCourseClassTimeInfoBeforeList.size(); i++) {
                courseClassTimeInfo = mCourseInfo.mCourseClassTimeInfoBeforeList.get(i);
                if (courseClassTimeInfo == null) {
                    continue;
                }
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.mCourseClassTimeName);
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.mCourseClassTimeStartTime);
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoBeforeList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoBeforeList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mControlMainActivity);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                CourseClassTimeInfo finalCourseClassTimeInfo = courseClassTimeInfo;
                view.setOnClickListener(v->{
                    //判断是否有播放权限
                    if (mCourseInfo.mCourseIsHave.equals("0")){
                        Toast.makeText(mControlMainActivity,"您还没有购买此课程",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.mCourseValidityPeriod.equals("")){
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Date date = null;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            try {
                                date = df.parse(mCourseInfo.mCourseValidityPeriod);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                CourseValidityPeriod = date.getTime();
                            }
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime){
                            Toast.makeText(mControlMainActivity,"您购买的课程已过期",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (finalCourseClassTimeInfo.liveStatus == 2 || finalCourseClassTimeInfo.liveStatus == 1){
                        mControlMainActivity.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.mCourseClassTimeId),2, PlayType.LIVE);
                    } else if (finalCourseClassTimeInfo.liveStatus == 3) {
                        mControlMainActivity.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.mCourseClassTimeId), 2, PlayType.PLAYBACK);
                    }
                });
            }
        } else if (type.equals("after")) {
            if (mCourseInfo.mCourseClassTimeInfoAfterList == null) {
                return;
            }
            course_catalog_label_content.removeAllViews();
            CourseClassTimeInfo courseClassTimeInfo = null;
            for (int i = 0; i < mCourseInfo.mCourseClassTimeInfoAfterList.size(); i++) {
                courseClassTimeInfo = mCourseInfo.mCourseClassTimeInfoAfterList.get(i);
                if (courseClassTimeInfo == null) {
                    continue;
                }
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.mCourseClassTimeName);
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.mCourseClassTimeStartTime);
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoAfterList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoAfterList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mControlMainActivity);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                view.setOnClickListener(v->{
                    //判断是否有播放权限
                    if (mCourseInfo.mCourseIsHave.equals("0")){
                        Toast.makeText(mControlMainActivity,"您还没有购买此课程",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.mCourseValidityPeriod.equals("")){
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Date date = null;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            try {
                                date = df.parse(mCourseInfo.mCourseValidityPeriod);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null) {
                                CourseValidityPeriod = date.getTime();
                            }
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime){
                            Toast.makeText(mControlMainActivity,"您购买的课程已过期",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(mControlMainActivity,"直播课程还未开始呢",Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    //课程问答添加界面初始化
    private void CourseQuestionAddInit() {
        mPage = "QuestionAdd";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        if (mQuestionViewAdd == null) {
            mQuestionViewAdd = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question_add, null);
            ImageView course_question_add_layout_return_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_return_button1);
            course_question_add_layout_return_button1.setOnClickListener(this);
        }
        RecyclerView course_question_add_layout_image = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_image);
        course_question_add_layout_image.setLayoutManager(new GridLayoutManager(mControlMainActivity, 3));
        selPhotosPath = new ArrayList<>();
        //=============图片九宫格=========================
        mPictureAdapter = null;
        mPictureBeansList = new ArrayList<>();
        //设置布局管理器
        mRecyclerView = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_image);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mControlMainActivity, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mPictureAdapter == null) {
            //设置适配器
            mPictureAdapter = new ControllerPictureAdapter(mControlMainActivity, mPictureBeansList);
            mRecyclerView.setAdapter(mPictureAdapter);
            //添加分割线
            //设置添加删除动画
            //调用ListView的setSelected(!ListView.isSelected())方法，这样就能及时刷新布局
            mRecyclerView.setSelected(true);
        } else {
            mPictureAdapter.notifyDataSetChanged();
        }
        //图片九宫格点击事件
        mPictureAdapter.setOnItemClickLitener(new ControllerPictureAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View v, int position) {
                //打开自定义的图片预览对话框
                List<String> photos = mPictureAdapter.getAllPhotoPaths();

                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);

                NewImagePagerDialogFragment newImagePagerDialogFragment = NewImagePagerDialogFragment.getInstance(mControlMainActivity, photos, position, screenLocation, v.getWidth(),
                        v.getHeight(), false);
                newImagePagerDialogFragment.show(mControlMainActivity.getSupportFragmentManager(), "preview img");
            }

            @Override
            public void onItemAddClick() {
                PhotoPicker.builder()
                        .setPhotoCount(mPictureAdapter.MAX)
                        .setGridColumnCount(3)
//                        .setSelected(selPhotosPath)
                        .start(mControlMainActivity, ControllerGlobals.CHOOSE_PIC_REQUEST_CODE);
                mPictureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemDeleteClick(View view, int position) {
                mPictureBeansList.remove(position);
                mPictureAdapter.notifyDataSetChanged();
                if (mPictureBeansList.size() == 0) {
                    mQuestionPublishImage = false;
                }
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_blue);
                } else {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_gray);
                }
            }
        });
        EditText course_question_add_layout_contentetitledittext = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_contentetitledittext);
        course_question_add_layout_contentetitledittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    mQuestionPublishContent = true;
                } else {
                    mQuestionPublishContent = false;
                }
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_blue);
                } else {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        EditText course_question_add_layout_contentedittext = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_contentedittext);
        course_question_add_layout_contentedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    mQuestionPublishTitle = true;
                } else {
                    mQuestionPublishTitle = false;
                }
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_blue);
                } else {
                    ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
                    course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
        course_question_add_layout_commit_button1.setClickable(true);
        course_question_add_layout_commit_button1.setOnClickListener(v -> {
            String title = course_question_add_layout_contentetitledittext.getText().toString();
            String content = course_question_add_layout_contentedittext.getText().toString();
            //点击发布问题
            if (selPhotosPath.size() == 0 ){ //如果没有图片直接发送内容
                AddStuCourseQuestion(mCourseInfo.mCourseId,title,content);
            } else {//如果有图片先上传图片再发布问题
                upLoadAnswerImage(mCourseInfo.mCourseId,title,content);
            }
        });
        course_main.addView(mQuestionViewAdd);
    }

    //课程问答添加界面-添加图片
    public void ModelCourseCoverQuestionPictureAdd(Intent data) {
        //添加图片，发布按钮改为蓝色
        mQuestionPublishImage = true;
        if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
            ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
            course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_blue);
        } else {
            ImageView course_question_add_layout_commit_button1 = mQuestionViewAdd.findViewById(R.id.course_question_add_layout_commit_button1);
            course_question_add_layout_commit_button1.setBackgroundResource(R.drawable.button_publish_gray);
        }
        if (data != null) {
            selPhotosPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
        }
        if (selPhotosPath != null) {
            //下面的代码主要用于这样一个场景，就是注释了.setSelected(selPhotosPath)之后，还想要保证选择的图片不重复
					/*for(String path : selPhotosPath){
						Log.w(TAG,"path="+path);///storage/emulated/0/tempHxzk/IMG_1498034535796.jpg
						boolean existThisPic = false;
						for(int i=0;i<mPictureBeansList.size();i++){
							if(path.equals(mPictureBeansList.get(i).getPicPath())){
								//如果新选择的图片集合中存在之前选中的图片，那么跳过去
								existThisPic = true;
								break;
							}
						}
						if(! existThisPic){
							PictureBean pictureBean = new PictureBean();
							pictureBean.setPicPath(path);
							pictureBean.setPicName(getFileName(path));
							//去掉总数目的限制，这里通过增大MAX的数字来实现
							if (mPictureBeansList.size() < mPictureAdapter.MAX) {
								mPictureBeansList.add(pictureBean);
							} else {
								Toast.makeText(MainActivity.this, "最多可以选择" + mPictureAdapter.MAX + "张图片", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}*/

            //是常规操作，和上面的代码不可共存
            for (String path : selPhotosPath) {
                ControllerPictureBean pictureBean = new ControllerPictureBean();
                pictureBean.setPicPath(path);
                pictureBean.setPicName(ControllerGlobals.getFileName(path));
                //去掉总数目的限制，这里通过增大MAX的数字来实现
                if (mPictureBeansList.size() < mPictureAdapter.MAX) {
                    mPictureBeansList.add(pictureBean);
                } else {
                    Toast.makeText(mControlMainActivity, "最多可以选择" + mPictureAdapter.MAX + "张图片", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            mPictureAdapter.notifyDataSetChanged();
        }
    }

    //课程问答-问答详情界面初始化
    private void CourseQuestionDetailsInit(Integer questions_id) {
        if (modelCourse == null) {
            return;
        }
        mPage = "QuestionDetails";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        if (mQuestionDetailsView == null) {
            mQuestionDetailsView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_questiondetails, null);
            //Smart_homepage_layout1
            course_questiondetails_layout_refresh = mQuestionDetailsView.findViewById(R.id.course_questiondetails_layout_refresh);
            course_questiondetails_layout_refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mCourseQuestionDetailsSum <= mCourseQuestionDetailsPage * mCourseQuestionDetailsCount){
                        LinearLayout courseAnswer_datails_end = mQuestionDetailsView.findViewById(R.id.courseAnswer_datails_end);
                        courseAnswer_datails_end.setVisibility(View.VISIBLE);
                        return;
                    }
                    QueryStuCourseQuestionDetailsMore(questions_id);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //控件的刷新方法
                    QueryStuCourseQuestionDetails(questions_id);
                }
            });
        }
        ImageView course_questiondetails_layout_return_button1 = mQuestionDetailsView.findViewById(R.id.course_questiondetails_layout_return_button1);
        course_questiondetails_layout_return_button1.setClickable(true);
        course_questiondetails_layout_return_button1.setOnClickListener(v -> {
            //查询课程问答列表
            CourseQuestionShow();
        });
        course_main.addView(mQuestionDetailsView);
        //获取问答详情信息
        QueryStuCourseQuestionDetails(questions_id);
    }

    //课程-下载初始化
    public void CourseDownloadInit() {
        if (mCourseDownloadDialog != null) {
            mCourseDownloadDialog.dismiss();
        }
        mCourseDownloadDialog = new ControllerPopDialog(mControlMainActivity, R.style.customdialogstyle, R.layout.modelcoursedetails_download);
        mCourseDownloadDialog.setOnKeyListener(keylistener);
        mCourseDownloadDialog.show();
//        TextView coursedetails_download_num = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_num);
//        coursedetails_download_num.setText("5");
        LinearLayout coursedetails_download_chapterlist = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_chapterlist);
        View view1 = null;
        int count = 0;
        for (int i = 0; i < mCourseInfo.mCourseChaptersInfoList.size(); i++) {
            CourseChaptersInfo courseChaptersInfo = mCourseInfo.mCourseChaptersInfoList.get(i);
            if (courseChaptersInfo == null) {
                continue;
            }
            for (int num = 0; num < courseChaptersInfo.mCourseSectionsInfoList.size(); num++) {
                CourseSectionsInfo courseSectionsInfo = courseChaptersInfo.mCourseSectionsInfoList.get(num);
                if (courseSectionsInfo == null) {
                    continue;
                }
                View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download1, null);
                TextView coursedetails_download1_name = view.findViewById(R.id.coursedetails_download1_name);
                coursedetails_download1_name.setText(courseSectionsInfo.mCourseSectionsName);
                coursedetails_download1_name.setHint(courseSectionsInfo.mCourseSectionsId);
                ImageView coursedetails_download1_image = view.findViewById(R.id.coursedetails_download1_image);
                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(  //查可用且没有被删除的数据库
                        "select video_len from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId + " and section_id="  +  courseSectionsInfo.mCourseSectionsId, null);
                if (cursor != null){
                    while (cursor.moveToNext()){
                        int video_lenIndex = cursor.getColumnIndex("video_len");
                        int video_len = cursor.getInt(video_lenIndex);
                        ControllerRoundProgressBar coursedetails_download1_downloadprogress = view.findViewById(R.id.coursedetails_download1_downloadprogress);
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_download1_downloadprogress.getLayoutParams();
                        ll.width = view.getResources().getDimensionPixelSize(R.dimen.dp15);
                        coursedetails_download1_downloadprogress.setLayoutParams(ll);
                        ll = (LinearLayout.LayoutParams) coursedetails_download1_image.getLayoutParams();
                        ll.width = 0;
                        coursedetails_download1_image.setLayoutParams(ll);
                        int progress = 0;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //计算下载进度
//                                progress = Math.toIntExact(Long.valueOf(courseRecordPlayDownloadInfo.mCourseSectionsDownloadSize)
//                                        / video_len);
                            }
                        } catch (Exception e){

                        }
                        coursedetails_download1_downloadprogress.setProgress(progress);
                        if (progress == 100) {
                            coursedetails_download1_downloadprogress = view.findViewById(R.id.coursedetails_download1_downloadprogress);
                            ll = (LinearLayout.LayoutParams) coursedetails_download1_downloadprogress.getLayoutParams();
                            ll.width = 0;
                            coursedetails_download1_downloadprogress.setLayoutParams(ll);
                            ll = (LinearLayout.LayoutParams) coursedetails_download1_image.getLayoutParams();
                            ll.width = view.getResources().getDimensionPixelSize(R.dimen.dp15);
                            coursedetails_download1_image.setLayoutParams(ll);
                            coursedetails_download1_image.setBackgroundResource(R.drawable.button_download_finish);
                        }
                    }
                    cursor.close();
                }
                coursedetails_download1_image.setOnClickListener(v -> {  //点击开始下载
                    int id = getV7ImageResourceId(coursedetails_download1_image);
                    if (id == R.drawable.button_download_circle_blue) {
                        ControllerRoundProgressBar coursedetails_download1_downloadprogress = view.findViewById(R.id.coursedetails_download1_downloadprogress);
                        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_download1_downloadprogress.getLayoutParams();
                        ll.width = view.getResources().getDimensionPixelSize(R.dimen.dp15);
                        coursedetails_download1_downloadprogress.setLayoutParams(ll);
                        ll = (LinearLayout.LayoutParams) coursedetails_download1_image.getLayoutParams();
                        ll.width = 0;
                        coursedetails_download1_image.setLayoutParams(ll);
                        //进度为0
                        coursedetails_download1_downloadprogress.setProgress(0);
                        //将此条数据插入到本地数据库中
                        long time = System.currentTimeMillis();
                        Cursor cursor1 = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(  //查可用且没有被删除的数据库
                                "select video_download_localname from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId +
                                        " and section_id="  +  courseSectionsInfo.mCourseSectionsId, null);
                        if (cursor1 != null){
                            String localFileName = "";
                            while (cursor1.moveToNext()){
                                int localFileNameIndex = cursor1.getColumnIndex("video_download_localname");
                                localFileName = cursor1.getString(localFileNameIndex);
                            }
                            cursor1.close();
                            if (!localFileName.equals("")){
                                //先删除掉以前的记录
                                ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("delete from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId +
                                        " and section_id="  +  courseSectionsInfo.mCourseSectionsId);
                                //删除本地文件
                                ModelRootFileUtil.deleteFile(ModelRootFileUtil.getRootFile(ModelRootFileUtil.mRecordVideoFileDownloadDir) + "/" + localFileName);
                            }
                        }
                        //向数据库中插入一条新纪录
                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("INSERT INTO `video_download_table` \n" +
                                "(`video_download_time`,`video_download_url`,`video_download_name`,`video_download_localname`,`chapter_id`,`section_id`,`video_len`) VALUES \n" +
                                "('" + time + "', '" + courseSectionsInfo.mCourseSectionsDownloadUrl + "', '" + courseSectionsInfo.mCourseSectionsName +
                                "', '" + time + courseSectionsInfo.mCourseSectionsName + "', '" + courseChaptersInfo.mCourseChaptersId + "', '" +
                                courseSectionsInfo.mCourseSectionsId + "', '" + "');");
                        //添加一条下载
//                        downLoad = new ControllerOkManagerDownload(courseSectionsInfo.mCourseSectionsDownloadUrl,this,"com.huozhongedu/download/videodownload");
                    }
                });
                coursedetails_download_chapterlist.addView(view);
                view1 = view;
                count ++;
            }
        }
        TextView coursedetails_download_sumnum = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_sumnum);
        coursedetails_download_sumnum.setText("/" + count);
        if (view1 != null) {
            View line = view1.findViewById(R.id.coursedetails_download1_line1);
            line.setVisibility(View.INVISIBLE);
        }
        //获取手机剩余存储空间
        TextView coursedetails_download_availalesize = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_availalesize);
        float size = getAvailaleSize();
        coursedetails_download_availalesize.setText("剩余空间：" + size + "G");
        TextView coursedetails_download_all = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_all);
        coursedetails_download_all.setOnClickListener(v -> {  //点击全部缓存
            int num = coursedetails_download_chapterlist.getChildCount();
            for (int i = 0; i < num; i++) {
                View view = coursedetails_download_chapterlist.getChildAt(i);
                ImageView coursedetails_download1_image = view.findViewById(R.id.coursedetails_download1_image);
                TextView coursedetails_download1_name = view.findViewById(R.id.coursedetails_download1_name);
                int id = getV7ImageResourceId(coursedetails_download1_image);
                if (id == R.drawable.button_download_circle_blue) {
                    ControllerRoundProgressBar coursedetails_download1_downloadprogress = view.findViewById(R.id.coursedetails_download1_downloadprogress);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursedetails_download1_downloadprogress.getLayoutParams();
                    ll.width = view.getResources().getDimensionPixelSize(R.dimen.dp15);
                    coursedetails_download1_downloadprogress.setLayoutParams(ll);
                    ll = (LinearLayout.LayoutParams) coursedetails_download1_image.getLayoutParams();
                    ll.width = 0;
                    coursedetails_download1_image.setLayoutParams(ll);
                    for (int mCourseChaptersInfoListNum = 0; mCourseChaptersInfoListNum < mCourseInfo.mCourseChaptersInfoList.size(); mCourseChaptersInfoListNum++) {
                        CourseChaptersInfo courseChaptersInfo = mCourseInfo.mCourseChaptersInfoList.get(mCourseChaptersInfoListNum);
                        if (courseChaptersInfo == null) {
                            continue;
                        }
                        boolean m_isFind = false;
                        for (int mCourseSectionsInfoListNum = 0; mCourseSectionsInfoListNum < courseChaptersInfo.mCourseSectionsInfoList.size(); mCourseSectionsInfoListNum++) {
                            CourseSectionsInfo courseSectionsInfo = courseChaptersInfo.mCourseSectionsInfoList.get(mCourseSectionsInfoListNum);
                            if (courseSectionsInfo == null) {
                                continue;
                            }
                            if (coursedetails_download1_name.getHint().toString().equals(courseSectionsInfo.mCourseSectionsId)) {
                                m_isFind = true;
                                //将此条数据插入到本地数据库中
                                long time = System.currentTimeMillis();
                                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(  //查可用且没有被删除的数据库
                                        "select video_download_localname from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId +
                                                " and section_id="  +  courseSectionsInfo.mCourseSectionsId, null);
                                if (cursor != null){
                                    String localFileName = "";
                                    while (cursor.moveToNext()){
                                        int localFileNameIndex = cursor.getColumnIndex("video_download_localname");
                                        localFileName = cursor.getString(localFileNameIndex);
                                    }
                                    cursor.close();
                                    if (!localFileName.equals("")){
                                        //先删除掉以前的记录
                                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("delete from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId +
                                                " and section_id="  +  courseSectionsInfo.mCourseSectionsId);
                                        //删除本地文件
                                        ModelRootFileUtil.deleteFile(ModelRootFileUtil.getRootFile(ModelRootFileUtil.mRecordVideoFileDownloadDir) + "/" + localFileName);
                                    }
                                }
                                //将所有的未缓存视频加入缓存列表
                                ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("INSERT INTO `video_download_table` \n" +
                                        "(`video_download_time`,`video_download_url`,`video_download_name`,`video_download_localname`,`chapter_id`,`section_id`,`video_len`) VALUES \n" +
                                        "('" + time + "', '" + courseSectionsInfo.mCourseSectionsDownloadUrl + "', '" + courseSectionsInfo.mCourseSectionsName +
                                        "', '" + time + courseSectionsInfo.mCourseSectionsName + "', '" + courseChaptersInfo.mCourseChaptersId + "', '" +
                                        courseSectionsInfo.mCourseSectionsId + "', '" + "');");
                                coursedetails_download1_downloadprogress.setProgress(0);
                                break;
                            }
                        }
                        if (m_isFind) {
                            break;
                        }
                    }
                }
            }
        });
        TextView coursedetails_download_manager = mCourseDownloadDialog.getWindow().findViewById(R.id.coursedetails_download_manager);
        coursedetails_download_manager.setOnClickListener(v -> { //点击管理缓存
            CourseDownloadManagerInit();
        });
    }

    //课程-下载管理界面初始化
    private void CourseDownloadManagerInit() {
        if (modelCourse == null) {
            return;
        }
        if (mCourseDownloadDialog != null) {
            mCourseDownloadDialog.dismiss();
        }
        mPage = "DownloadManager";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        if (mDownloadManagerView == null) {
            mDownloadManagerView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager, null);
        }
        int count = 0;
        LinearLayout course_downloadmanager_layout_content = mDownloadManagerView.findViewById(R.id.course_downloadmanager_layout_content);
        course_downloadmanager_layout_content.removeAllViews();
        for (int i = 0; i < mCourseInfo.mCourseChaptersInfoList.size(); i++) {
            CourseChaptersInfo courseChaptersInfo = mCourseInfo.mCourseChaptersInfoList.get(i);
            if (courseChaptersInfo == null) {
                continue;
            }
            View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager_child, null);
            TextView course_downloadmanager_child_titletext = view.findViewById(R.id.course_downloadmanager_child_titletext);
            course_downloadmanager_child_titletext.setText(courseChaptersInfo.mCourseChaptersName);
            course_downloadmanager_child_titletext.setHint(courseChaptersInfo.mCourseChaptersId);
            if (courseChaptersInfo.mCourseSectionsInfoList.size() == 0) {
                View course_downloadmanager_child_line1 = view.findViewById(R.id.course_downloadmanager_child_line1);
                course_downloadmanager_child_line1.setVisibility(View.INVISIBLE);
            }
            course_downloadmanager_layout_content.addView(view);
            LinearLayout course_downloadmanager_child_content = view.findViewById(R.id.course_downloadmanager_child_content);
            course_downloadmanager_child_content.removeAllViews();
            for (int num = 0; num < courseChaptersInfo.mCourseSectionsInfoList.size(); num++) {
                CourseSectionsInfo courseSectionsInfo = courseChaptersInfo.mCourseSectionsInfoList.get(num);
                if (courseSectionsInfo == null) {
                    continue;
                }
                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(  //查可用且没有被删除的数据库
                        "select video_len from video_download_table where chapter_id=" + courseChaptersInfo.mCourseChaptersId + " and section_id="  +  courseSectionsInfo.mCourseSectionsId, null);
                int video_len = -1;
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int video_lenIndex = cursor.getColumnIndex("video_len");
                        video_len = cursor.getInt(video_lenIndex);
                    }
                    cursor.close();
                }
                if (video_len == -1) { //没有添加下载的不做处理
                    continue;
                }
                View view1 = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_download_manager_child1, null);
                TextView course_downloadmanager_child1_name = view1.findViewById(R.id.course_downloadmanager_child1_name);
                course_downloadmanager_child1_name.setText(courseSectionsInfo.mCourseSectionsName);
                course_downloadmanager_child1_name.setHint(courseSectionsInfo.mCourseSectionsId);
                ProgressBar progress_bar_healthy = view1.findViewById(R.id.progress_bar_healthy);
                int progress = 0;
                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //计算下载进度
//                        progress = Math.toIntExact(Long.valueOf(info.mCourseSectionsDownloadSize)
//                                / video_len);
//                    }
                } catch (Exception e){

                }
                progress_bar_healthy.setProgress(progress);
                ImageView course_downloadmanager_child1_function = view1.findViewById(R.id.course_downloadmanager_child1_function);
                TextView course_downloadmanager_child_state = view1.findViewById(R.id.course_downloadmanager_child_state);
                course_downloadmanager_child1_function.setOnClickListener(v -> {
                    int id = getV7ImageResourceId(course_downloadmanager_child1_function);
                    if (id == R.drawable.button_pause_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_play_blue);
                        course_downloadmanager_child_state.setText("已暂停");
                        progress_bar_healthy.setProgressDrawable(view1.getResources().getDrawable(R.drawable.progressbar_bg1));
                    } else if (id == R.drawable.button_play_blue) {
                        course_downloadmanager_child1_function.setBackgroundResource(R.drawable.button_pause_blue);
                        course_downloadmanager_child_state.setText("下载中");
                        progress_bar_healthy.setProgressDrawable(view1.getResources().getDrawable(R.drawable.progressbar_bg));
                    }
                });
                course_downloadmanager_child_content.addView(view1);
                count++;
            }
        }
        TextView course_downloadmanager_num = mDownloadManagerView.findViewById(R.id.course_downloadmanager_num);
        course_downloadmanager_num.setText("0");
        TextView course_downloadmanager_sumnum = mDownloadManagerView.findViewById(R.id.course_downloadmanager_sumnum);
        course_downloadmanager_sumnum.setText("/" + count);
        //获取手机剩余存储空间
        TextView course_downloadmanager_availalesize = mDownloadManagerView.findViewById(R.id.course_downloadmanager_availalesize);
        float size = getAvailaleSize();
        course_downloadmanager_availalesize.setText("剩余空间：" + size + "G");
        ImageView course_downloadmanager_layout_return_button1 = mDownloadManagerView.findViewById(R.id.course_downloadmanager_layout_return_button1);
        course_downloadmanager_layout_return_button1.setOnClickListener(v -> {
            CourseDetailsShow();
        });
        TextView course_downloadmanager_all = mDownloadManagerView.findViewById(R.id.course_downloadmanager_all);
        course_downloadmanager_all.setOnClickListener(v -> { //点击全部暂停
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
                        course_downloadmanager_child_state.setText("已暂停");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg1));
                    }
                }
            }

        });
        TextView course_downloadmanager_startall = mDownloadManagerView.findViewById(R.id.course_downloadmanager_startall);
        course_downloadmanager_startall.setOnClickListener(v -> { //点击全部开始
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
                        course_downloadmanager_child_state.setText("下载中");
                        progress_bar_healthy.setProgressDrawable(childView.getResources().getDrawable(R.drawable.progressbar_bg));
                    }
                }
            }
        });
        TextView course_downloadmanager_layout_edit = mDownloadManagerView.findViewById(R.id.course_downloadmanager_layout_edit);
        LinearLayout course_downloadmanager_function = mDownloadManagerView.findViewById(R.id.course_downloadmanager_function);
        LinearLayout course_downloadmanager_function1 = mDownloadManagerView.findViewById(R.id.course_downloadmanager_function1);
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
        ll.height = mDownloadManagerView.getResources().getDimensionPixelSize(R.dimen.dp40);
        course_downloadmanager_function.setLayoutParams(ll);
        ll = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
        ll.height = 0;
        course_downloadmanager_function1.setLayoutParams(ll);
        //编辑
        course_downloadmanager_layout_edit.setText("编辑");
        course_downloadmanager_layout_edit.setOnClickListener(v -> {
            if (course_downloadmanager_layout_edit.getText().toString().equals("编辑")) { //跳转到编辑界面
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) course_downloadmanager_function.getLayoutParams();
                llp.height = 0;
                course_downloadmanager_function.setLayoutParams(llp);
                llp = (LinearLayout.LayoutParams) course_downloadmanager_function1.getLayoutParams();
                llp.height = mDownloadManagerView.getResources().getDimensionPixelSize(R.dimen.dp40);
                course_downloadmanager_function1.setLayoutParams(llp);
                course_downloadmanager_layout_edit.setText("完成");
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
            } else if (course_downloadmanager_layout_edit.getText().toString().equals("完成")) {
                CourseDownloadManagerInit();
            }
        });
        //全部选择
        TextView course_downloadmanager_allselect = mDownloadManagerView.findViewById(R.id.course_downloadmanager_allselect);
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
        //删除
        TextView course_downloadmanager_delete = mDownloadManagerView.findViewById(R.id.course_downloadmanager_delete);
        course_downloadmanager_delete.setOnClickListener(v -> {
            View view = mControlMainActivity.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            mMyDialog = new ControllerCenterDialog(mControlMainActivity, 0, 0, view, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view.findViewById(R.id.tip);
            tip.setText("删除所选内容");
            TextView dialog_content = view.findViewById(R.id.dialog_content);
            dialog_content.setText("确定删除所选内容吗？");
            TextView button_cancel = view.findViewById(R.id.button_cancel);
            button_cancel.setText("取消");
            button_cancel.setOnClickListener(View -> {
                mMyDialog.cancel();
            });
            TextView button_sure = view.findViewById(R.id.button_sure);
            button_sure.setText("确定");
            button_sure.setOnClickListener(View -> {
                int num = course_downloadmanager_layout_content.getChildCount();
                for (int i = 0; i < num; i++) {
                    View childView = course_downloadmanager_layout_content.getChildAt(i);
                    LinearLayout course_downloadmanager_child_content = childView.findViewById(R.id.course_downloadmanager_child_content);
                    int childCount = course_downloadmanager_child_content.getChildCount();
                    for (int j = 0; j < childCount; j++) {
                        View childView1 = course_downloadmanager_child_content.getChildAt(j);
                        ImageView course_downloadmanager_child1_select = childView1.findViewById(R.id.course_downloadmanager_child1_select);
                        int id = getV7ImageResourceId(course_downloadmanager_child1_select);
                        if (id == R.drawable.button_select_red) {//将选中的项目缓存全部清除
                            TextView course_downloadmanager_child1_name = childView1.findViewById(R.id.course_downloadmanager_child1_name);
                            long time = System.currentTimeMillis();
                            Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mControlMainActivity).rawQuery(  //查可用且没有被删除的数据库
                                    "select video_download_localname from video_download_table where section_id="  +  course_downloadmanager_child1_name.getHint().toString(), null);
                            if (cursor != null){
                                String localFileName = "";
                                while (cursor.moveToNext()){
                                    int localFileNameIndex = cursor.getColumnIndex("video_download_localname");
                                    localFileName = cursor.getString(localFileNameIndex);
                                }
                                cursor.close();
                                if (!localFileName.equals("")){
                                    //先删除掉以前的记录
                                    ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mControlMainActivity).execSQL("delete from video_download_table where section_id="  +  course_downloadmanager_child1_name.getHint().toString());
                                    //删除本地文件
                                    ModelRootFileUtil.deleteFile(ModelRootFileUtil.getRootFile(ModelRootFileUtil.mRecordVideoFileDownloadDir) + "/" + localFileName);
                                }
                            }
                        }
                    }
                }
                mMyDialog.cancel();
                CourseDownloadManagerInit();
            });
        });
        course_main.addView(mDownloadManagerView);
    }

    //获取sdcard可用磁盘大小
    private float getAvailaleSize() {

        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024 / 1024 / 1024;

        //(availableBlocks * blockSize)/1024      KIB 单位

        //(availableBlocks * blockSize)/1024 /1024  MIB单位
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

    //录播视频播放
    private void CourseCatalogRecordGo(String videoId,String SectionsId,String title,int mCourseSectionsTime1) {
        if (videoId == null){
            Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
            return;
        }
        if (videoId.equals("")){
            Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("video_id", videoId);
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getAliCourseAccessVideo(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {

            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.body() == null){
                    Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (baseBean.getErrorCode() != 200){
                    Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (baseBean.getData() == null){
                    Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                Map<String, Object> map = baseBean.getData();
                String SecurityToken = (String) map.get("SecurityToken");
                String AccessKeyId = (String) map.get("AccessKeyId");
                String AccessKeySecret = (String) map.get("AccessKeySecret");
//                String resourse_name = (String) map.get("resourse_name");
                if (SecurityToken == null || AccessKeyId == null || AccessKeySecret == null ){
                    Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mControlMainActivity.onStsSuccess(videoId,AccessKeyId,AccessKeySecret,SecurityToken);
                AliyunVodPlayerView aliyunVodPlayerView = mDetailsView.findViewById(R.id.aliyunVodPlayerView);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) aliyunVodPlayerView.getLayoutParams();
                rl.height = mDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_244);
                aliyunVodPlayerView.VideoIdSet(videoId,SectionsId,mCourseSectionsTime1);
                mControlMainActivity.setmAliyunVodPlayerView(aliyunVodPlayerView);
//                cover = "http://video.huozhongedu.cn/bea996b56ca3466e81b2a37ebdf39756/snapshots/5c712f5ca526445d8e56b0fbe0235de3-00003.jpg";
//                if (!cover.equals("")){
//                    aliyunVodPlayerView.setCoverUri(cover);
//                }
                aliyunVodPlayerView.setTitleName(title);
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mControlMainActivity,"此课程暂无播放资源",Toast.LENGTH_SHORT).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                return;
            }
        });
    }

    //获取课程详情
    private void getSingleCourseDetails() {
        if (mCourseInfo.mCourseId.equals("")){
            Toast.makeText(mControlMainActivity,"查询课程详情失败",Toast.LENGTH_SHORT).show();
            mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        if (!mControlMainActivity.mStuId.equals("")) {
            paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        }
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.findSingleCourseDetails(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    Toast.makeText(mControlMainActivity,"查询课程详情失败",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mControlMainActivity,"查询课程详情失败",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                Map<String,Object> courseDataBean = baseBean.getData();
                if (courseDataBean == null){
                    Toast.makeText(mControlMainActivity,"查询课程详情失败",Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                String invalid_date_date = String.valueOf(courseDataBean.get("invalid_date_date"));
                mCourseInfo.mCourseDetails = String.valueOf(courseDataBean.get("details"));
                Map<String,String> stuCourseStatusInfo = (Map<String, String>)courseDataBean.get("stuCourseStatusInfo");
                Object rateOfLearningO = courseDataBean.get("rateOfLearning");
                if (rateOfLearningO != null){
                    float rateOfLearning = Float.parseFloat(String.valueOf(courseDataBean.get("rateOfLearning")));
                    if (rateOfLearning <= 0){
                        mCourseInfo.mCourseIsStartLearn = "0";
                    } else {
                        mCourseInfo.mCourseIsStartLearn = "1";
                    }
                }
//                String effictive_days = String.valueOf(courseDataBean.get("effictive_days"));
                mCourseInfo.mCourseMessage = String.valueOf(courseDataBean.get("course_describe"));
                mCourseInfo.mCourseTotalHours = String.valueOf(courseDataBean.get("total_hours"));
                mCourseInfo.mCourseTotalHours = mCourseInfo.mCourseTotalHours.substring(0,mCourseInfo.mCourseTotalHours.indexOf("."));
                if (stuCourseStatusInfo != null){
                    mCourseInfo.mCourseIsCollect = String.valueOf(stuCourseStatusInfo.get("collection_status"));
                    mCourseInfo.mCourseIsCollect = mCourseInfo.mCourseIsCollect.substring(0, mCourseInfo.mCourseIsCollect.indexOf("."));
                    String enrollment_time = stuCourseStatusInfo.get("enrollment_time");
                    if (enrollment_time != null){
                        if (enrollment_time.equals("")){
                            mCourseInfo.mCourseIsHave = "0";
                        } else {
                            mCourseInfo.mCourseIsHave = "1";
                        }
                    }
                }
                mCourseInfo.mCourseValidityPeriod = invalid_date_date;
                //课程详情界面
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CourseDetailsInit(mCourseInfo);
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程详情失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                mControlMainActivity.onClickCourseDetailsReturn(mDetailsView);
            }
        });
    }

    //收藏、取消收藏课程
    private void CollectOrNotCollectCourses() {
        if (mCourseInfo == null){
            Toast.makeText(mControlMainActivity,"系统错误",Toast.LENGTH_LONG).show();
            return;
        }
        if (mCourseInfo.mCourseId.equals("") ||mControlMainActivity.mStuId.equals("")){
            if (!mCourseInfo.mCourseIsCollect.equals("1")){
                Toast.makeText(mControlMainActivity,"收藏失败",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mControlMainActivity,"取消收藏失败",Toast.LENGTH_LONG).show();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap1 = new HashMap<>();
        if (!mCourseInfo.mCourseIsCollect.equals("1")){
            paramsMap1.put("collection_status", 1);
        } else {
            paramsMap1.put("collection_status", 0);
        }
        paramsMap1.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        paramsMap1.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.collectOrNotCollectCourses(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (!mCourseInfo.mCourseIsCollect.equals("1")){
                        Toast.makeText(mControlMainActivity,"收藏失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"取消收藏失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (response.body() == null){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (!mCourseInfo.mCourseIsCollect.equals("1")){
                        Toast.makeText(mControlMainActivity,"收藏失败",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mControlMainActivity,"取消收藏失败",Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(),response.body().getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (mCourseInfo.mCourseIsCollect.equals("1")) { //成功修改状态
                    mCourseInfo.mCourseIsCollect = "0";
                } else {
                    mCourseInfo.mCourseIsCollect = "1";
                }
                if (mCourseInfo.mCourseIsCollect.equals("1")){
                    ImageView course_details_bottomlayout_collectImage1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage1);
                    TextView course_details_bottomlayout_collectText1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText1);
                    ImageView course_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage);
                    TextView course_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText);
                    course_details_bottomlayout_collectText1.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
                    course_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
                    course_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
                    course_details_bottomlayout_collectImage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
                } else {
                    ImageView course_details_bottomlayout_collectImage1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage1);
                    TextView course_details_bottomlayout_collectText1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText1);
                    ImageView course_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectImage);
                    TextView course_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.course_details_bottomlayout_collectText);
                    course_details_bottomlayout_collectText1.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
                    course_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
                    course_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
                    course_details_bottomlayout_collectImage1.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                if (!mCourseInfo.mCourseIsCollect.equals("1")){
                    Toast.makeText(mControlMainActivity,"收藏失败",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mControlMainActivity,"取消收藏失败",Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取课程目录
    private void getSingleCourseCatalog() {
        if (mCourseInfo.mCourseId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        if (!mControlMainActivity.mStuId.equals("")){
            paramsMap.put("stu_id", Integer.valueOf(mControlMainActivity.mStuId));
        }
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        if (mCourseInfo.mCourseType.equals("直播,录播")){
            paramsMap1.put("course_type", "混合");
        } else {
            paramsMap1.put("course_type", mCourseInfo.mCourseType);
        }
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<CourseCatalogBean> call = modelObservableInterface.findSingleCourseCatalog(body);
        call.enqueue(new Callback<CourseCatalogBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CourseCatalogBean> call, Response<CourseCatalogBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CourseCatalogBean courseCatalogBean = response.body();
                if (courseCatalogBean == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogBean.getErrorCode(),courseCatalogBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                CourseCatalogBean.CourseCatalogDataBean courseCatalogDataBeans = courseCatalogBean.getData();
                if (courseCatalogDataBeans == null){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (courseCatalogDataBeans.liveCatalog != null){ //直播课目录
                    //计算今日课程、历史课程、后续课程
                    for (CourseCatalogBean.CourseCatalogLiveDataBean courseCatalogLiveDataBean:courseCatalogDataBeans.liveCatalog) {
                        if (courseCatalogLiveDataBean == null){
                            continue;
                        }
                        long begin_class_date = 0;
                        long end_time_datess = 0;
                        long today_end_time = 0;
                        String today_end_time1 ;
                        long currentTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Date date = null;
                            Date date1 = null;
                            Date date2 = null;
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd  23:59:59");
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            try {
                                date = df.parse(courseCatalogLiveDataBean.begin_class_date);
                                date1 = df.parse(courseCatalogLiveDataBean.end_time_datess);
                                today_end_time1 = df1.format(new Date(currentTime));
                                date2 = df2.parse(today_end_time1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date != null && date1 != null) {
                                begin_class_date = date.getTime();
                                end_time_datess = date1.getTime();
                            }
                            if (date2 != null){
                                today_end_time =  date2.getTime();
                            }
                        }
                        CourseClassTimeInfo info = new CourseClassTimeInfo();
                        info.mCourseClassTimeId = String.valueOf(courseCatalogLiveDataBean.course_times_id);
                        info.mCourseClassTimeName = courseCatalogLiveDataBean.ct_name;
                        info.liveStatus = courseCatalogLiveDataBean.liveStatus;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            DateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                            info.mCourseClassTimeStartTime = df.format(new Date(begin_class_date)) + "~" + df1.format(new Date(end_time_datess));
                        }
                        if (end_time_datess < currentTime ){ //历史课程
                            mCourseInfo.mCourseClassTimeInfoBeforeList.add(info);
                        } else if (begin_class_date > today_end_time){ //后续课程
                            mCourseInfo.mCourseClassTimeInfoAfterList.add(info);
                        } else { //今日课程-------------------*****
                            mCourseInfo.mCourseClassTimeInfoTodayList.add(info);
                        }
                    }
                }
                if (courseCatalogDataBeans.videoCatalog != null){ //录播目录
                    for (CourseCatalogBean.CourseCatalogChapterRecordDataBean courseCatalogChapterRecordDataBean:courseCatalogDataBeans.videoCatalog.chapterList) { //课程章列表
                        if (courseCatalogChapterRecordDataBean == null){
                            continue;
                        }
                        CourseChaptersInfo courseChaptersInfo = new CourseChaptersInfo();
                        courseChaptersInfo.mCourseChaptersId = String.valueOf(courseCatalogChapterRecordDataBean.chapter_id);
                        courseChaptersInfo.mCourseChaptersName = courseCatalogChapterRecordDataBean.chapter_name;
                        courseChaptersInfo.mCourseChaptersOrder = String.valueOf(courseCatalogChapterRecordDataBean.chapter_sort);
                        mCourseInfo.mCourseChaptersInfoList.add(courseChaptersInfo);
                    }
                    for (CourseCatalogBean.CourseCatalogSectionRecordDataBean courseCatalogSectionRecordDataBean:courseCatalogDataBeans.videoCatalog.sectionList) { //课程节列表
                        if (courseCatalogSectionRecordDataBean == null){
                            continue;
                        }
                        for (CourseChaptersInfo courseChaptersInfo:mCourseInfo.mCourseChaptersInfoList) {
                            if (courseChaptersInfo == null){
                                continue;
                            }
                            if (courseChaptersInfo.mCourseChaptersId.equals(String.valueOf(courseCatalogSectionRecordDataBean.chapter_id))){
                                CourseSectionsInfo courseSectionsInfo = new CourseSectionsInfo();
                                courseSectionsInfo.mCourseSectionsId = String.valueOf(courseCatalogSectionRecordDataBean.section_id);
                                courseSectionsInfo.mCourseSectionsOrder = String.valueOf(courseCatalogSectionRecordDataBean.section_sort);
                                courseSectionsInfo.mCourseSectionsName = courseCatalogSectionRecordDataBean.section_name;
                                courseSectionsInfo.mVideoId = courseCatalogSectionRecordDataBean.video_id;
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                                String hms = formatter.format(courseCatalogSectionRecordDataBean.Duration * 1000);
                                courseSectionsInfo.mCourseSectionsTime = hms;
                                courseSectionsInfo.mCourseSectionsTime1 = courseCatalogSectionRecordDataBean.Duration * 1000;
                                courseSectionsInfo.mCourseSectionsLearnProgress = Double.valueOf(courseCatalogSectionRecordDataBean.sectionLearningRate) * 100 + "%";
                                courseChaptersInfo.mCourseSectionsInfoList.add(courseSectionsInfo);
                            }
                        }
                    }
                }
                //课程详情和课程阶段的标签层的下方游标
                ImageView course_imgv_cursor = mDetailsView.findViewById(R.id.course_imgv_cursor);
                Matrix matrix = new Matrix();
                matrix.postTranslate(width / 2, 0);
                course_imgv_cursor.setImageMatrix(matrix);// 设置动画初始位置
                ImageView course_imgv_cursor1 = mDetailsView.findViewById(R.id.course_imgv_cursor1);
                Matrix matrix1 = new Matrix();
                matrix1.postTranslate(width / 2, 0);
                course_imgv_cursor1.setImageMatrix(matrix1);// 设置动画初始位置
                if (mCurrentCatalogTab.equals("Live")){
                    CourseCatalogLiveInit(mCourseInfo);
                } else if (mCurrentCatalogTab.equals("Record")){
                    //修改body为录播
                    CourseCatalogRecordInit(mCourseInfo);
                }
                mCurrentTab = "Catalog";
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<CourseCatalogBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程目录失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程问答-添加问答
    private void AddStuCourseQuestion(String courseId,String title,String content) {
        if (courseId.equals("") || title.equals("") || content.equals("")){
            Toast.makeText(mControlMainActivity,"添加课程问答失败",Toast.LENGTH_LONG).show();
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        String questionPublishImageS = "";
        if (selPhotosPath != null) {
            for (int i = 0; i < selPhotosPath.size(); i++) {
                questionPublishImageS = questionPublishImageS + selPhotosPath.get(i) + ";";
            }
        }
        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(courseId));
        paramsMap.put("publisher", Integer.valueOf(mControlMainActivity.mStuId));
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("title", title);
        paramsMap1.put("content", content);
        paramsMap1.put("picture", questionPublishImageS);
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.addStuCourseQuestion(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response == null){
                    Toast.makeText(mControlMainActivity,"添加课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mControlMainActivity,"添加课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (baseBean.getErrorCode() != 200){
                    Toast.makeText(mControlMainActivity,"添加课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                Toast.makeText(mControlMainActivity,"添加课程问答成功",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                CourseQuestionShow();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"添加课程问答失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //课程问答-回复问答
    private void ReplyStuCourseQuestion(Integer questionId,Integer replyStuId,String content) {
        if ( content.equals("") || mControlMainActivity.mStuId.equals("") || mCourseInfo.mCourseId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("mid", questionId);
        paramsMap.put("publisher", Integer.valueOf(mControlMainActivity.mStuId));
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        paramsMap.put("fid", replyStuId);
        String strEntity = gson.toJson(paramsMap);
        HashMap<String,String> paramsMap1 = new HashMap<>();
        paramsMap1.put("content", content);
        paramsMap1.put("title", " ");
        paramsMap1.put("picture", " ");
        String strEntity1 = gson.toJson(paramsMap1);
        strEntity1 = strEntity1.replace("{","");
        strEntity = strEntity.replace("}","," + strEntity1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.replyStuCourseQuestion(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response == null){
                    Toast.makeText(mControlMainActivity,"回复课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null){
                    Toast.makeText(mControlMainActivity,"回复课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (baseBean.getErrorCode() != 200){
                    Toast.makeText(mControlMainActivity,"回复课程问答失败",Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                Toast.makeText(mControlMainActivity,"回复课程问答成功",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
                if (mCustomDialog != null){
                    mCustomDialog.dismiss();
                    mCustomDialog = null;
                }
                CourseQuestionShow();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"回复课程问答失败",Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取课程-问答列表
    private void QueryStuCourseQuestion(LinearLayout course_question_layout_content) {
        if (mQuestionView != null) {
            LinearLayout course_end = mQuestionView.findViewById(R.id.course_question_end);
            course_end.setVisibility(View.VISIBLE);
        }
        if (mCourseInfo.mCourseId.equals("")){
            if (course_question_layout_refresh != null){
                course_question_layout_refresh.finishRefresh();
            }
            return;
        }
        LinearLayout course_end = mQuestionView.findViewById(R.id.course_question_end);
        course_end.setVisibility(View.INVISIBLE);
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        mCourseQuestionPage = 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_type", 2);
        paramsMap.put("pageNum", mCourseQuestionPage);
        paramsMap.put("pageSize",mCourseQuestionCount);
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelCommunityAnswer.CommunityBean> call = modelObservableInterface.queryAllCoursePackageCommunity(body);
        call.enqueue(new Callback<ModelCommunityAnswer.CommunityBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelCommunityAnswer.CommunityBean> call, Response<ModelCommunityAnswer.CommunityBean> response) {
                int code = response.code();
                if (code != 200){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                ModelCommunityAnswer.CommunityBean communityBean = response.body();
                if (communityBean == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                ModelCommunityAnswer.CommunityBean.CommunityDataBean communityDataBean = communityBean.getData();
                if (communityDataBean == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(communityBean.getCode(),communityBean.getMsg())){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                course_question_layout_content.removeAllViews();
                mCourseQuestionSum = communityDataBean.getTotal();
                if (communityDataBean.getList() == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                TextView course_question_layout_titletext = mQuestionView.findViewById(R.id.course_question_layout_titletext);
                course_question_layout_titletext.setText("精选问答(" + mCourseQuestionSum + ")");
                //将问答内容添加到布局中
                for (int i = 0; i < communityDataBean.getList().size(); i++) {
                    ModelCommunityAnswer.CommunityBean.ListDataBean listDataBean = communityDataBean.getList().get(i);
//                    CourseQuestionInfo courseQuestionInfo = mCourseInfo.mCourseQuestionInfoList.get(i);
                    if (listDataBean == null) {
                        continue;
                    }
                    Date date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(listDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            listDataBean.setCreation_time(df2.format(date));
                        }
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question_child, null);
                    //添加头像
                    ControllerCustomRoundAngleImageView course_question_child_headportrait = view.findViewById(R.id.course_question_child_headportrait);
                    if (listDataBean.getHead() != null) {
                        Glide.with(mControlMainActivity).
                                load(listDataBean.getHead()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        })
                                .error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(course_question_child_headportrait);
                    }
                    //问题的监听(点击问题查看详情)
                    LinearLayout course_question_child_content1 = view.findViewById(R.id.course_question_child_content1);
                    course_question_child_content1.setClickable(true);
                    course_question_child_content1.setOnClickListener(v -> {
                        CourseQuestionDetailsInit(listDataBean.getQuestions_id());
                    });
                    //回答者名字
                    TextView course_question_child_name = view.findViewById(R.id.course_question_child_name);
                    course_question_child_name.setText(listDataBean.getNicename());
                    course_question_child_name.setHint(listDataBean.getPublisher() + "");
                    //回答内容
                    TextView course_question_child_message = view.findViewById(R.id.course_question_child_message);
                    if (listDataBean.getContent() == null){
                        listDataBean.setContent("");
                    }
                    new ModelHtmlUtils(mControlMainActivity, course_question_child_message).setHtmlWithPic(listDataBean.getContent());
                    //回答图片？？
                    //时间
                    TextView course_question_child_time = view.findViewById(R.id.course_question_child_time);
                    course_question_child_time.setText(listDataBean.getCreation_time());
                    //浏览人数
                    TextView course_question_child_look = view.findViewById(R.id.course_question_child_look);
                    course_question_child_look.setText("浏览" + listDataBean.getVisit_num());
                    LinearLayout course_question_child_function_discuss = view.findViewById(R.id.course_question_child_function_discuss);
                    course_question_child_function_discuss.setClickable(true);
                    course_question_child_function_discuss.setOnClickListener(v -> {
                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle, "回复 " + listDataBean.getNicename(), false);
                        mCustomDialog.setOnKeyListener(keylistener);
                        mCustomDialog.show();
                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                            @Override
                            public void publish(String content) {
                                ReplyStuCourseQuestion(listDataBean.getQuestions_id(),listDataBean.getQuestions_id(),content);
                            }

                            @Override
                            public void image() {

                            }
                        });
                    });
                    course_question_layout_content.addView(view);
                    if (listDataBean.getHuida() != null) {
                        //添加评论个数
                        TextView course_question_child_discusstext = view.findViewById(R.id.course_question_child_discusstext);
                        course_question_child_discusstext.setText(String.valueOf(listDataBean.getHuida_num()));
                        if (listDataBean.getHuida().size() > 2) {
                            LinearLayout course_question_child_lookalldiscuss = view.findViewById(R.id.course_question_child_lookalldiscuss);
                            LinearLayout.LayoutParams rl = (LinearLayout.LayoutParams) course_question_child_lookalldiscuss.getLayoutParams();
                            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                            rl.topMargin = view.getResources().getDimensionPixelSize(R.dimen.dp13);
                            rl.bottomMargin = view.getResources().getDimensionPixelSize(R.dimen.dp13);
                            course_question_child_lookalldiscuss.setLayoutParams(rl);
                            course_question_child_lookalldiscuss.setClickable(true);
                            course_question_child_lookalldiscuss.setOnClickListener(v -> {
                            CourseQuestionDetailsInit(listDataBean.getQuestions_id());
                            });
                        }
                        LinearLayout course_question_child_content = view.findViewById(R.id.course_question_child_content);
                        int count = 0;
                        for (ModelCommunityAnswer.CommunityBean.DataBean dataBean:listDataBean.getHuida()) {
                            if (count >= 2){
                                break;
                            }
                            View childView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question_child1, null);
                            TextView course_question_child_style_name = childView.findViewById(R.id.course_question_child_style_name);
                            course_question_child_style_name.setText(dataBean.getA_nicename());
                            course_question_child_style_name.setHint(dataBean.getA_publisher() + "");
                            TextView course_question_child_style_name1 = childView.findViewById(R.id.course_question_child_style_name1);
                            course_question_child_style_name1.setText(dataBean.getQ_nicename());
                            course_question_child_style_name1.setHint(dataBean.getQ_publisher() + "");
                            TextView course_question_child_style_content = childView.findViewById(R.id.course_question_child_style_content);
                            if (dataBean.getContent() == null){
                                dataBean.setContent("");
                            }
                            new ModelHtmlUtils(mControlMainActivity, course_question_child_style_content).setHtmlWithPic(dataBean.getContent());
                            course_question_child_content.addView(childView);
                            count ++;
                        }
                    }
                }
                if (course_question_layout_refresh != null){
                    course_question_layout_refresh.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelCommunityAnswer.CommunityBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程问答列表失败",Toast.LENGTH_LONG).show();
                if (course_question_layout_refresh != null){
                    course_question_layout_refresh.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取课程-问答列表加载更多
    private void QueryStuCourseQuestionMore(LinearLayout course_question_layout_content) {
        if (mCourseInfo.mCourseId.equals("")){
            if (course_question_layout_refresh != null){
                course_question_layout_refresh.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        mCourseQuestionPage = mCourseQuestionPage + 1;
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_type", 2);
        paramsMap.put("pageNum", mCourseQuestionPage);
        paramsMap.put("pageSize",mCourseQuestionCount);
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.mCourseId));
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelCommunityAnswer.CommunityBean> call = modelObservableInterface.queryAllCoursePackageCommunity(body);
        call.enqueue(new Callback<ModelCommunityAnswer.CommunityBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelCommunityAnswer.CommunityBean> call, Response<ModelCommunityAnswer.CommunityBean> response) {
                int code = response.code();
                if (code != 200){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                ModelCommunityAnswer.CommunityBean communityBean = response.body();
                if (communityBean == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(communityBean.getCode(),communityBean.getMsg())){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCommunityAnswer.CommunityBean.CommunityDataBean communityDataBean = communityBean.getData();
                if (communityDataBean == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
//                course_question_layout_content.removeAllViews();
                mCourseQuestionSum = communityDataBean.getTotal();
                if (communityDataBean.getList() == null){
                    if (course_question_layout_refresh != null){
                        course_question_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestion  onErrorCode: " + code);
                    return;
                }
                TextView course_question_layout_titletext = mQuestionView.findViewById(R.id.course_question_layout_titletext);
                course_question_layout_titletext.setText("精选问答(" + mCourseQuestionSum + ")");
                //将问答内容添加到布局中
                for (int i = 0; i < communityDataBean.getList().size(); i++) {
                    ModelCommunityAnswer.CommunityBean.ListDataBean listDataBean = communityDataBean.getList().get(i);
//                    CourseQuestionInfo courseQuestionInfo = mCourseInfo.mCourseQuestionInfoList.get(i);
                    if (listDataBean == null) {
                        continue;
                    }
                    Date date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(listDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            listDataBean.setCreation_time(df2.format(date));
                        }
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question_child, null);
                    //添加头像
                    ControllerCustomRoundAngleImageView course_question_child_headportrait = view.findViewById(R.id.course_question_child_headportrait);
                    if (listDataBean.getHead() != null) {
                        Glide.with(mControlMainActivity).
                                load(listDataBean.getHead()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        })
                                .error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(course_question_child_headportrait);
                    }
                    //问题的监听(点击问题查看详情)
                    LinearLayout course_question_child_content1 = view.findViewById(R.id.course_question_child_content1);
                    course_question_child_content1.setClickable(true);
                    course_question_child_content1.setOnClickListener(v -> {
                        CourseQuestionDetailsInit(listDataBean.getQuestions_id());
                    });
                    //回答者名字
                    TextView course_question_child_name = view.findViewById(R.id.course_question_child_name);
                    course_question_child_name.setText(listDataBean.getNicename());
                    course_question_child_name.setHint(listDataBean.getPublisher() + "");
                    //回答内容
                    if (listDataBean.getContent() == null){
                        listDataBean.setContent("");
                    }
                    TextView course_question_child_message = view.findViewById(R.id.course_question_child_message);
                    new ModelHtmlUtils(mControlMainActivity, course_question_child_message).setHtmlWithPic(listDataBean.getContent());
                    //回答图片？？
                    //时间
                    TextView course_question_child_time = view.findViewById(R.id.course_question_child_time);
                    course_question_child_time.setText(listDataBean.getCreation_time());
                    //浏览人数
                    TextView course_question_child_look = view.findViewById(R.id.course_question_child_look);
                    course_question_child_look.setText("浏览" + listDataBean.getVisit_num());
                    LinearLayout course_question_child_function_discuss = view.findViewById(R.id.course_question_child_function_discuss);
                    course_question_child_function_discuss.setClickable(true);
                    course_question_child_function_discuss.setOnClickListener(v -> {
                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle, "回复 " + listDataBean.getNicename(), false);
                        mCustomDialog.setOnKeyListener(keylistener);
                        mCustomDialog.show();
                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                            @Override
                            public void publish(String content) {
                                ReplyStuCourseQuestion(listDataBean.getQuestions_id(),listDataBean.getQuestions_id(),content);
                            }

                            @Override
                            public void image() {

                            }
                        });
                    });
                    course_question_layout_content.addView(view);
                    if (listDataBean.getHuida() != null) {
                        //添加评论个数
                        TextView course_question_child_discusstext = view.findViewById(R.id.course_question_child_discusstext);
                        course_question_child_discusstext.setText(String.valueOf(listDataBean.getHuida_num()));
                        if (listDataBean.getHuida().size() > 2) {
                            LinearLayout course_question_child_lookalldiscuss = view.findViewById(R.id.course_question_child_lookalldiscuss);
                            LinearLayout.LayoutParams rl = (LinearLayout.LayoutParams) course_question_child_lookalldiscuss.getLayoutParams();
                            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                            rl.topMargin = view.getResources().getDimensionPixelSize(R.dimen.dp13);
                            rl.bottomMargin = view.getResources().getDimensionPixelSize(R.dimen.dp13);
                            course_question_child_lookalldiscuss.setLayoutParams(rl);
                            course_question_child_lookalldiscuss.setClickable(true);
                            course_question_child_lookalldiscuss.setOnClickListener(v -> {
                            CourseQuestionDetailsInit(listDataBean.getQuestions_id());
                            });
                        }
                        LinearLayout course_question_child_content = view.findViewById(R.id.course_question_child_content);
                        int count = 0;
                        for (ModelCommunityAnswer.CommunityBean.DataBean dataBean:listDataBean.getHuida()) {
                            if (count >= 2){
                                break;
                            }
                            View childView = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_question_child1, null);
                            TextView course_question_child_style_name = childView.findViewById(R.id.course_question_child_style_name);
                            course_question_child_style_name.setText(dataBean.getA_nicename());
                            course_question_child_style_name.setHint(dataBean.getA_publisher() + "");
                            TextView course_question_child_style_name1 = childView.findViewById(R.id.course_question_child_style_name1);
                            course_question_child_style_name1.setText(dataBean.getQ_nicename());
                            course_question_child_style_name1.setHint(dataBean.getQ_publisher() + "");
                            TextView course_question_child_style_content = childView.findViewById(R.id.course_question_child_style_content);
                            if (dataBean.getContent() == null){
                                dataBean.setContent("");
                            }
                            new ModelHtmlUtils(mControlMainActivity, course_question_child_style_content).setHtmlWithPic(dataBean.getContent());
                            course_question_child_content.addView(childView);
                            count ++;
                        }
                    }
                }
                if (course_question_layout_refresh != null){
                    course_question_layout_refresh.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelCommunityAnswer.CommunityBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程问答列表失败",Toast.LENGTH_LONG).show();
                if (course_question_layout_refresh != null){
                    course_question_layout_refresh.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取课程-问答详情
    private void QueryStuCourseQuestionDetails(int questionId) {
        if (mCourseInfo.mCourseId.equals("")){
            if (course_questiondetails_layout_refresh != null){
                course_questiondetails_layout_refresh.finishRefresh();
            }
            return;
        }
        LinearLayout courseAnswer_datails_end = mQuestionDetailsView.findViewById(R.id.courseAnswer_datails_end);
        courseAnswer_datails_end.setVisibility(View.INVISIBLE);
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCourseQuestionDetailsPage = 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("questions_id", questionId);
        paramsMap.put("pageNum", mCourseQuestionDetailsPage);//第几页
        paramsMap.put("pageSize",mCourseQuestionDetailsCount);//每页几条
        paramsMap.put("course_type",2);
        String strEntity = gson.toJson(paramsMap);
        Log.e("aaaaaaaaaaaaaa","aaaaaaaaaa           " + strEntity);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelCommunityAnswer.CommunityDetilsBean> call = modelObservableInterface.QueryCommunityQuestionsDetails(body);
        call.enqueue(new Callback<ModelCommunityAnswer.CommunityDetilsBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Response<ModelCommunityAnswer.CommunityDetilsBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCommunityAnswer.CommunityDetilsBean communityDetilsBean = response.body();
                if (communityDetilsBean == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                if (communityDetilsDataBean == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    return;
                }
                LinearLayout course_questiondetails_content = mQuestionDetailsView.findViewById(R.id.course_questiondetails_content);
                course_questiondetails_content.removeAllViews();
                //添加头像
                ControllerCustomRoundAngleImageView course_questiondetails_child_headportrait = mQuestionDetailsView.findViewById(R.id.course_questiondetails_child_headportrait);
                if (communityDetilsDataBean.getHead() != null) {
                    Glide.with(mControlMainActivity).
                            load(communityDetilsDataBean.getHead()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                            return false;
                        }
                    })
                            .error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(course_questiondetails_child_headportrait);
                }
                //回答者名字
                TextView course_questiondetails_child_name = mQuestionDetailsView.findViewById(R.id.course_questiondetails_child_name);
                course_questiondetails_child_name.setText(communityDetilsDataBean.getNicename());
                course_questiondetails_child_name.setHint(communityDetilsDataBean.getPublisher() + "");
                //回答内容
                TextView course_questiondetails_child_message = mQuestionDetailsView.findViewById(R.id.course_questiondetails_child_message);
                if (communityDetilsDataBean.getContent() == null){
                    communityDetilsDataBean.setContent("");
                }
                new ModelHtmlUtils(mControlMainActivity, course_questiondetails_child_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                //回答图片？？
                //时间
                {
                    Date date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(communityDetilsDataBean.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            communityDetilsDataBean.setCreation_time(df2.format(date));
                        }
                    }
                    TextView course_questiondetails_child_time = mQuestionDetailsView.findViewById(R.id.course_questiondetails_child_time);
                    course_questiondetails_child_time.setText(communityDetilsDataBean.getCreation_time());
                }
                //浏览人数
                TextView course_questiondetails_child_look = mQuestionDetailsView.findViewById(R.id.course_questiondetails_child_look);
                course_questiondetails_child_look.setText("浏览" + communityDetilsDataBean.getVisit_num());
                if (communityDetilsDataBean.getHuida() == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                if (communityDetilsDataBean.getHuida().getList() == null || communityDetilsDataBean.getHuida().getTotal() == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishRefresh();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                mCourseQuestionDetailsSum = communityDetilsDataBean.getHuida().getTotal();
                View course_questiondetails1_child_line = null;
                for (ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList communityDetilsAnswerDataBeanList: communityDetilsDataBean.getHuida().getList()) {
                    if (communityDetilsAnswerDataBeanList == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_questiondetails1, null);
                    view.setOnClickListener(v -> {
                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle, "回复 " + communityDetilsAnswerDataBeanList.getQ_nicename(), false);
                        mCustomDialog.setOnKeyListener(keylistener);
                        mCustomDialog.show();
                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                            @Override
                            public void publish(String content) {
                                ReplyStuCourseQuestion(communityDetilsDataBean.getQuestions_id(),communityDetilsAnswerDataBeanList.getqID(),content);
                            }

                            @Override
                            public void image() {

                            }
                        });
                    });
                    //添加头像
                    ControllerCustomRoundAngleImageView course_questiondetails1_child_headportrait = view.findViewById(R.id.course_questiondetails1_child_headportrait);
                    if (communityDetilsAnswerDataBeanList.getQ_head() != null) {
                        Glide.with(mControlMainActivity).
                                load(communityDetilsAnswerDataBeanList.getQ_head()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        })
                                .error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(course_questiondetails1_child_headportrait);
                    }
                    TextView course_questiondetails1_child_name = view.findViewById(R.id.course_questiondetails1_child_name);
                    course_questiondetails1_child_name.setText(communityDetilsAnswerDataBeanList.getQ_nicename());
                    course_questiondetails1_child_name.setHint(communityDetilsAnswerDataBeanList.getQ_publisher() + "");
                    TextView course_questiondetails1_child_time = view.findViewById(R.id.course_questiondetails1_child_time);
                    Date date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(communityDetilsAnswerDataBeanList.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            communityDetilsAnswerDataBeanList.setCreation_time(df2.format(date));
                        }
                    }
                    course_questiondetails1_child_time.setText(communityDetilsAnswerDataBeanList.getCreation_time());
                    TextView course_questiondetails1_child_message = view.findViewById(R.id.course_questiondetails1_child_message);
                    if (communityDetilsAnswerDataBeanList.getContent() == null){
                        communityDetilsAnswerDataBeanList.setContent("");
                    }
                    new ModelHtmlUtils(mControlMainActivity, course_questiondetails1_child_message).setHtmlWithPic(communityDetilsAnswerDataBeanList.getContent());
                    course_questiondetails1_child_line = view.findViewById(R.id.course_questiondetails1_child_line);
                    course_questiondetails_content.addView(view);
                }
                if (course_questiondetails1_child_line != null){
                    course_questiondetails1_child_line.setVisibility(View.INVISIBLE);
                }
                if (course_questiondetails_layout_refresh != null){
                    course_questiondetails_layout_refresh.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程问答详情失败",Toast.LENGTH_LONG).show();
                if (course_questiondetails_layout_refresh != null){
                    course_questiondetails_layout_refresh.finishRefresh();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    //获取课程-问答详情-加载更多
    private void QueryStuCourseQuestionDetailsMore(int questionId) {
        if (mCourseInfo.mCourseId.equals("")){
            if (course_questiondetails_layout_refresh != null){
                course_questiondetails_layout_refresh.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mControlMainActivity).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCourseQuestionDetailsPage = mCourseQuestionDetailsPage + 1;
        HashMap<String,Integer> paramsMap = new HashMap<>();
        paramsMap.put("questions_id", questionId);
        paramsMap.put("pageNum", mCourseQuestionDetailsPage);//第几页
        paramsMap.put("pageSize",mCourseQuestionDetailsCount);//每页几条
        paramsMap.put("course_type",2);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        Call<ModelCommunityAnswer.CommunityDetilsBean> call = modelObservableInterface.QueryCommunityQuestionsDetails(body);
        call.enqueue(new Callback<ModelCommunityAnswer.CommunityDetilsBean>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Response<ModelCommunityAnswer.CommunityDetilsBean> response) {
                int code = response.code();
                if (code != 200){
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCommunityAnswer.CommunityDetilsBean communityDetilsBean = response.body();
                if (communityDetilsBean == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(communityDetilsBean.getCode(),communityDetilsBean.getMsg())){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    return;
                }
                ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                if (communityDetilsDataBean == null){
                    if (course_questiondetails_layout_refresh != null){
                        course_questiondetails_layout_refresh.finishLoadMore();
                    }
                    LoadingDialog.getInstance(mControlMainActivity).dismiss();
                    Log.e("TAG", "QueryStuCourseQuestionDetails  onErrorCode: " + code);
                    return;
                }
                mCourseQuestionDetailsSum = communityDetilsDataBean.getHuida().getTotal();
                View course_questiondetails1_child_line = null;
                for (ModelCommunityAnswer.CommunityDetilsBean.CommunityDetilsAnswerDataBeanList communityDetilsAnswerDataBeanList: communityDetilsDataBean.getHuida().getList()) {
                    if (communityDetilsAnswerDataBeanList == null) {
                        continue;
                    }
                    View view = LayoutInflater.from(mControlMainActivity).inflate(R.layout.modelcoursedetails_questiondetails1, null);
                    view.setOnClickListener(v -> {
                        mCustomDialog = new ControllerCustomDialog(mControlMainActivity, R.style.customdialogstyle, "回复 " + communityDetilsAnswerDataBeanList.getQ_nicename(), false);
                        mCustomDialog.setOnKeyListener(keylistener);
                        mCustomDialog.show();
                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                            @Override
                            public void publish(String content) {
                                ReplyStuCourseQuestion(communityDetilsDataBean.getQuestions_id(),communityDetilsAnswerDataBeanList.getqID(),content);
                            }

                            @Override
                            public void image() {

                            }
                        });
                    });
                    //添加头像
                    ControllerCustomRoundAngleImageView course_questiondetails1_child_headportrait = view.findViewById(R.id.course_questiondetails1_child_headportrait);
                    if (communityDetilsAnswerDataBeanList.getQ_head() != null) {
                        Glide.with(mControlMainActivity).
                                load(communityDetilsAnswerDataBeanList.getQ_head()).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d("Warn", "加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Warn", "成功  Drawable Name:" + resource.getClass().getCanonicalName());
                                return false;
                            }
                        })
                                .error(mControlMainActivity.getResources().getDrawable(R.drawable.image_teachersdefault)).into(course_questiondetails1_child_headportrait);
                    }
                    TextView course_questiondetails1_child_name = view.findViewById(R.id.course_questiondetails1_child_name);
                    course_questiondetails1_child_name.setText(communityDetilsAnswerDataBeanList.getQ_nicename());
                    course_questiondetails1_child_name.setHint(communityDetilsAnswerDataBeanList.getQ_publisher() + "");
                    TextView course_questiondetails1_child_time = view.findViewById(R.id.course_questiondetails1_child_time);
                    Date date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        try {
                            date = df.parse(communityDetilsAnswerDataBeanList.getCreation_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                            communityDetilsAnswerDataBeanList.setCreation_time(df2.format(date));
                        }
                    }
                    course_questiondetails1_child_time.setText(communityDetilsAnswerDataBeanList.getCreation_time());
                    TextView course_questiondetails1_child_message = view.findViewById(R.id.course_questiondetails1_child_message);
                    if (communityDetilsAnswerDataBeanList.getContent() == null){
                        communityDetilsAnswerDataBeanList.setContent("");
                    }
                    new ModelHtmlUtils(mControlMainActivity, course_questiondetails1_child_message).setHtmlWithPic(communityDetilsAnswerDataBeanList.getContent());
                    course_questiondetails1_child_line = view.findViewById(R.id.course_questiondetails1_child_line);
                    LinearLayout course_questiondetails_content = mQuestionDetailsView.findViewById(R.id.course_questiondetails_content);
                    course_questiondetails_content.addView(view);
                }
                if (course_questiondetails1_child_line != null){
                    course_questiondetails1_child_line.setVisibility(View.INVISIBLE);
                }
                if (course_questiondetails_layout_refresh != null){
                    course_questiondetails_layout_refresh.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }

            @Override
            public void onFailure(Call<ModelCommunityAnswer.CommunityDetilsBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage()+"" );
                Toast.makeText(mControlMainActivity,"获取课程问答详情失败",Toast.LENGTH_LONG).show();
                if (course_questiondetails_layout_refresh != null){
                    course_questiondetails_layout_refresh.finishLoadMore();
                }
                LoadingDialog.getInstance(mControlMainActivity).dismiss();
            }
        });
    }

    static class CourseCatalogBean {
        private CourseCatalogDataBean data;
        private int code;
        private String msg;

        public CourseCatalogDataBean getData() {
            return data;
        }

        public void setData(CourseCatalogDataBean data) {
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

        public static class CourseCatalogDataBean{
            private CourseCatalogRecordDataBean videoCatalog;
            private List<CourseCatalogLiveDataBean> liveCatalog;
        }

        public static class CourseCatalogRecordDataBean{
            private List<CourseCatalogTestRecordDataBean> testList;
            private List<CourseCatalogChapterRecordDataBean> chapterList;
            private List<CourseCatalogSectionRecordDataBean> sectionList;
        }
        public static class CourseCatalogLiveDataBean{
            private String ct_name;
            private int course_times_id;
            private String begin_class_date;
            private String end_time_datess;
            private int liveStatus;
        }

        public static class CourseCatalogTestRecordDataBean{
            private int test_sort;
            private int chapter_id;
            private String test_name;
            private int test_id;
        }
        public static class CourseCatalogChapterRecordDataBean{
            private int chapter_sort;
            private int chapter_id;
            private String chapter_name;
        }
        public static class CourseCatalogSectionRecordDataBean{
            private int section_id;
            private int Duration;
            private String section_name;
            private int chapter_id;
            private int section_sort;
            private String sectionLearningRate;
            private String video_id;
        }
    }

    //上传课程问答中的图片
    private void upLoadAnswerImage(String courseId,String title,String content) {
        if (courseId.equals("") || title.equals("") || content.equals("")){
            Toast.makeText(mControlMainActivity, "问题发布失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String uu = UUID.randomUUID().toString();
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "multipart/form-data; boundary=" + uu)
                            .build();
                    return chain.proceed(request);
                }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mControlMainActivity.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Map<String, RequestBody> params=new HashMap<>() ;
        for (int i = 0; i < selPhotosPath.size(); i ++){
            File file = new File(selPhotosPath.get(i));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            params.put("file\"; filename=\""+ i + "#" + file.getName(), requestBody);
        }
        retrofit2.Call call = modelObservableInterface.upLoadImage(params);
        call.enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(retrofit2.Call call, retrofit2.Response response) {
                if (response == null){
                    Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddStuCourseQuestion(courseId,title,content);
            }
            //图片上传失败
            @Override
            public void onFailure(retrofit2.Call call, Throwable t) {
                Log.d("Tag",t.getMessage().toString());
                Toast.makeText(mControlMainActivity, "问题发布时上传图像失败!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class QuestionBean{
        private QuestionDataBean data;  //返回数据
        private int code;
//        private String errorMsg;

        public QuestionDataBean getData() {
            return data;
        }
        public void setData(QuestionDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

//        public String getErrorMsg() {
//            return errorMsg;
//        }

        //        public void setErrorMsg(String errorMsg) {
//            this.errorMsg = errorMsg;
//        }
        public static class QuestionDataBean {
            private List<QuestionListBean> list;
            private int total;
            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }
            public List<QuestionListBean> getData() {
                return list;
            }
            public void setData(List<QuestionListBean> list) {
                this.list = list;
            }
        }

        public static class QuestionListBean {
            private int fid;                //父id
            private String creation_time;           //创建时间
            private List<QuestionListBean> daan;    //子回复
            private int questions_id;               //问题id
            private int mid;                        //相当于问题id
            private String title;                   //问题标题
            private String content;                 //内容
            private String picture;                 //图片链接；分隔
            private String head;                    //发布者头像链接
            private int st_state;                   //教师还是学生2是老师1是学生
            private String publishername;           //发布者名称
            private int publisherId;                //发布者id
            private String producerName;            //回复了谁的名称
            private int producerId;                 //回复了谁的id
            private int discuss_num;                //评论数量
            private int visit_num;                  //浏览人数
        }
    }
    public static class QuestionDetailsBean{
        private QuestionDetailsDataBean data;  //返回数据
        private int code;
//        private String errorMsg;

        public QuestionDetailsDataBean getData() {
            return data;
        }
        public void setData(QuestionDetailsDataBean data) {
            this.data = data;
        }

        public int getErrorCode() {
            return code;
        }

        public void setErrorCode(int code) {
            this.code = code;
        }

//        public String getErrorMsg() {
//            return errorMsg;
//        }

        //        public void setErrorMsg(String errorMsg) {
//            this.errorMsg = errorMsg;
//        }
        public static class QuestionDetailsDataBean {
            private int fid;                //父id
            private String creation_time;           //创建时间
            private List<QuestionDetailsDataBean> daan;    //子回复
            private int questions_id;               //问题id
            private int mid;                        //相当于问题id
            private String title;                   //问题标题
            private String content;                 //内容
            private String picture;                 //图片链接；分隔
            private String head;                    //发布者头像链接
            private int st_state;                   //教师还是学生2是老师1是学生
            private String publishername;           //发布者名称
            private int publisherId;                //发布者id
            private String producerName;            //回复了谁的名称
            private int producerId;                 //回复了谁的id
            private int discuss_num;                //评论数量
            private int visit_num;                  //浏览人数

            public String getCreation_time() {
                return creation_time;
            }

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getContent() {
                return content;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public String getPicture() {
                return picture;
            }

            public String getHead() {
                return head;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            public void setVisit_num(int visit_num) {
                this.visit_num = visit_num;
            }

            public void setQuestions_id(int questions_id) {
                this.questions_id = questions_id;
            }

            public void setSt_state(int st_state) {
                this.st_state = st_state;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getVisit_num() {
                return visit_num;
            }

            public int getQuestions_id() {
                return questions_id;
            }

            public int getFid() {
                return fid;
            }

            public int getDiscuss_num() {
                return discuss_num;
            }

            public int getMid() {
                return mid;
            }

            public int getProducerId() {
                return producerId;
            }

            public int getPublisherId() {
                return publisherId;
            }

            public int getSt_state() {
                return st_state;
            }

            public List<QuestionDetailsDataBean> getDaan() {
                return daan;
            }

            public String getProducerName() {
                return producerName;
            }

            public String getPublishername() {
                return publishername;
            }

            public void setDaan(List<QuestionDetailsDataBean> daan) {
                this.daan = daan;
            }

            public void setDiscuss_num(int discuss_num) {
                this.discuss_num = discuss_num;
            }

            public void setMid(int mid) {
                this.mid = mid;
            }

            public void setProducerId(int producerId) {
                this.producerId = producerId;
            }

            public void setProducerName(String producerName) {
                this.producerName = producerName;
            }

            public void setPublisherId(int publisherId) {
                this.publisherId = publisherId;
            }

            public void setPublishername(String publishername) {
                this.publishername = publishername;
            }
        }
    }
}
