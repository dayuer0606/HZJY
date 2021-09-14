package com.android.weischool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.android.weischool.consts.PlayType;
import com.android.weischool.info.CourseChaptersInfo;
import com.android.weischool.info.CourseClassTimeInfo;
import com.android.weischool.info.CourseInfo;
import com.android.weischool.info.CourseSectionsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import net.sqlcipher.Cursor;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class ModelCourseCover implements View.OnClickListener, ModelOrderDetailsInterface, ControllerOkManagerDownload.IProgress {
    private View modelCourse, mListView, mDetailsView, mDownloadManagerView, mcatalog_chapter_liveview;
    private MainActivity mMainContext = null;
    private ModelCourseCoverOnClickListener mModelCourseCoverOnClickListener = null;
    private String mCurrentCatalogTab = "Live"; //当前标签是录播还是直播
    private CourseInfo mCourseInfo;
    private String mPage = "Detail";
    private ControllerCenterDialog mMyDialog; //居中的对话框
    private ControllerPopDialog mCourseDownloadDialog = null;

    //课程目录界面刷新
    private int mCourseCatalogPage = 1;
    private int mCourseCatalogCount = 5;
    private int mCourseCatalogSum = 0; //课程目录总数（如果当前界面是录播的话，此变量赋值为录播章总数，如果是直播的话，此变量赋值为直播课程总数）
    private int mRecCourseSum = 0; //录播课程总数

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
        mMainContext = (MainActivity) context;
        if (courseInfo == null) {
            return null;
        }
        mCourseInfo = new CourseInfo(courseInfo);
        modelCourse = LayoutInflater.from(context).inflate(R.layout.modelcourse_layout, null);
        mListView = LayoutInflater.from(context).inflate(R.layout.modelcourselist_layout, null);
        if (mDetailsView == null) {
            mDetailsView = LayoutInflater.from(context).inflate(R.layout.modelcoursedetails_layout, null);
            modelCourse.setOnClickListener(v -> {
                if (mModelCourseCoverOnClickListener == null || modelCourse == null || mPage.equals("DownloadManager")) {
                    return;
                }
                mModelCourseCoverOnClickListener.OnClickListener(v, this);
                //跳转到课程的详细界面
                CourseDetailsShow();
            });
            //直播
            TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
            //录播
            TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
            //收藏课程
            LinearLayout course_details_bottomlayout_collect = mDetailsView.findViewById(R.id.course_details_bottomlayout_collect);
            LinearLayout course_details_bottomlayout_collect1 = mDetailsView.findViewById(R.id.course_details_bottomlayout_collect1);
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
            course_details_bottomlayout_collect.setOnClickListener(this);
            course_details_bottomlayout_collect1.setOnClickListener(this);
            course_catalog_label_live.setOnClickListener(this);
            course_catalog_label_record.setOnClickListener(this);

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
        //获取课程资料
        if (modelCourse == null) {
            return;
        }
        mPage = "Detail";
        HideAllLayout();
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        course_main.addView(mDetailsView);
        //修改body为课程详情
        LinearLayout course_catalog_label_content_layout_main = mDetailsView.findViewById(R.id.course_catalog_label_content_layout_main);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) course_catalog_label_content_layout_main.getLayoutParams();
        LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        course_catalog_label_content_layout_main.setLayoutParams(LP);
        course_catalog_label_content_layout_main.setVisibility(View.VISIBLE);
        if (mCourseInfo.getmCourseType().equals("录播")) {
            TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
            course_catalog_label_record.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
            mCurrentCatalogTab = "Record";
            //获取课程目录
            getSingleCourseCatalogRecNew();
        } else {
            //将直播变为选中状态
            TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
            course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
            LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
            course_catalog_label_content_layout.removeAllViews();
            mCurrentCatalogTab = "Live";
            //获取课程目录（直播）
            getSingleCourseCatalogLiveNew(0);
            getSingleCourseCatalogLiveNew(1);
            getSingleCourseCatalogLiveNew(2);
            getSingleCourseCatalogLiveNew(3);
        }
    }

    //隐藏所有布局
    public void HideAllLayout() {
        RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
        course_main.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击课程收藏
            case R.id.course_details_bottomlayout_collect1:
            case R.id.course_details_bottomlayout_collect: {
                CollectOrNotCollectCourses();
                break;
            }
            //点击目录中的直播tab
            case R.id.course_catalog_label_live: {
                if (!mCurrentCatalogTab.equals("Live")) {
                    TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
                    course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
                    course_catalog_label_record.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
                    course_catalog_label_content_layout.removeAllViews();
                    //获取课程目录（直播）
                    getSingleCourseCatalogLiveNew(0);
                    getSingleCourseCatalogLiveNew(1);
                    getSingleCourseCatalogLiveNew(2);
                    getSingleCourseCatalogLiveNew(3);
                }
                mCurrentCatalogTab = "Live";
                break;
            }
            //点击目录中的录播tab
            case R.id.course_catalog_label_record: {
                if (!mCurrentCatalogTab.equals("Record")) {
                    TextView course_catalog_label_live = mDetailsView.findViewById(R.id.course_catalog_label_live);
                    course_catalog_label_live.setTextColor(mDetailsView.getResources().getColor(R.color.black999999));
                    TextView course_catalog_label_record = mDetailsView.findViewById(R.id.course_catalog_label_record);
                    course_catalog_label_record.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
//                    //修改body为录播
//                    CourseCatalogRecordInit(mCourseInfo);
                    //获取课程目录
                    getSingleCourseCatalogRecNew();
                }
                mCurrentCatalogTab = "Record";
                break;
            }
            case R.id.course_fl_layout_title_download:
            case R.id.course_details_download_button:
            case R.id.course_details_download_button1: {
                CourseDownloadInit();
                break;
            }
            case R.id.course_details_buy_button: { //课程详情购买
                //如果是免费的课程直接购买
                if (!mCourseInfo.getmCourseIsHave().equals("1")) {
                    Toast.makeText(mMainContext, "此功能还在完善，敬请期待！", Toast.LENGTH_SHORT).show();
                }
//                HideAllLayout();
//                RelativeLayout course_main = modelCourse.findViewById(R.id.course_main);
//                View view = mMainContext.Page_OrderDetails(this,mCourseInfo,null,null);
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
        TextView courseNameTextView = mListView.findViewById(R.id.courseName);
        if (courseInfo.getmCourseName() != null) {
            courseNameTextView.setText(courseInfo.getmCourseName());
        }
        TextView courseLearnStuNum = mListView.findViewById(R.id.courseLearnStuNum);
        courseLearnStuNum.setText(courseInfo.getmCourseLearnPersonNum() + "人正在学习");
        TextView coursepriceTextView = mListView.findViewById(R.id.courseprice);
        TextView coursepriceendTextView = mListView.findViewById(R.id.coursepriceend);
        TextView courseTeacherName = mListView.findViewById(R.id.courseTeacherName);
        if (courseInfo.getmTeacherName() != null) {
            courseTeacherName.setText(courseInfo.getmTeacherName());
        }
        ControllerCustomRoundAngleImageView courseTeacherIcon = mListView.findViewById(R.id.courseTeacherIcon);
        if (courseInfo.getmTeacherIcon() != null) {
            Glide.with(mMainContext.getApplicationContext()).
                    load(courseInfo.getmTeacherIcon()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d("Warn","加载失败 errorMsg:" + (e != null ? e.getMessage() : "null"));
                    return false;
                }
                @Override
                public boolean onResourceReady(final Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d("Warn","成功  Drawable Name:" + resource.getClass().getCanonicalName());
                    return false;
                }
            })
                    .error(mMainContext.getApplicationContext().getResources().getDrawable(R.drawable.image_teachersdefault)).into(courseTeacherIcon);
        }
        if (courseInfo.getmCoursePrice() != null) {
            if (!courseInfo.getmCoursePrice().equals("免费")) {
                coursepriceTextView.setTextColor(Color.RED);
                coursepriceendTextView.setText("¥" + courseInfo.getmCoursePrice());
                //设置原价格
                TextView coursepriceOldTextView = mListView.findViewById(R.id.coursepriceOld);
                //文字栅格化
                coursepriceOldTextView.setPaintFlags(coursepriceOldTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if (courseInfo.getmCoursePriceOld() != null) {
                    if (!courseInfo.getmCoursePriceOld().equals("免费")) {
                        coursepriceOldTextView.setText("¥" + courseInfo.getmCoursePriceOld());
                    }
                }
            } else {
                coursepriceTextView.setText(courseInfo.getmCoursePrice());
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
        if (courseInfo.getmCourseCover() != null) {
            Glide.with(mMainContext).
                    load(courseInfo.getmCourseCover()).listener(new RequestListener<Drawable>() {
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
                    .error(mMainContext.getResources().getDrawable(R.drawable.modelcoursecover)).into(course_details_Cover);
        }
        //课程详情-课程名称
        TextView course_details_Name = mDetailsView.findViewById(R.id.course_details_Name);
        if (courseInfo.getmCourseName() != null) {
            course_details_Name.setText(courseInfo.getmCourseName());
        }
        //课程详情-课程信息
        TextView course_details_content0 = mDetailsView.findViewById(R.id.course_details_content0);
        if (courseInfo.getmCourseLearnPersonNum() != null) {
            course_details_content0.setText("购买人数：" + courseInfo.getmCourseLearnPersonNum());
        }
        TextView course_details_content2 = mDetailsView.findViewById(R.id.course_details_content2);
        if (courseInfo.getmCourseTotalHours() != null) {
            course_details_content2.setText("课程数 " + courseInfo.getmCourseTotalHours() + "节");
        }
        //课程价格
        TextView course_details_price = mDetailsView.findViewById(R.id.course_details_price);
        if (courseInfo.getmCoursePrice() != null) {
            if (!courseInfo.getmCoursePrice().equals("免费")) {
                course_details_price.setTextColor(Color.RED);
                course_details_price.setText("¥" + courseInfo.getmCoursePrice());
            } else {
                course_details_price.setText(courseInfo.getmCoursePrice());
            }
        }
        //课程原价
        TextView course_details_priceOld = mDetailsView.findViewById(R.id.course_details_priceOld);
        //文字栅格化
        course_details_priceOld.setPaintFlags(course_details_priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (courseInfo.getmCoursePriceOld() != null) {
            if (!courseInfo.getmCoursePriceOld().equals("免费")) {
                course_details_priceOld.setText("¥" + courseInfo.getmCoursePriceOld());
            }
        }
        //课程有效期
        TextView course_details_periodofvalidity = mDetailsView.findViewById(R.id.course_details_periodofvalidity);
        Date date = null;
        String invalid_date_date = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = df.parse(courseInfo.getmCourseValidityPeriod());
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
                invalid_date_date = df2.format(date1).toString();
            }
        }
        course_details_periodofvalidity.setText("有效期至：" + invalid_date_date);
        //课程简介
        TextView coursepacket_details_briefintroductioncontent = mDetailsView.findViewById(R.id.coursepacket_details_briefintroductioncontent);
        coursepacket_details_briefintroductioncontent.setText("简介：" + courseInfo.getmCourseMessage());
        //课程名称
        TextView course_fl_layout_title = mDetailsView.findViewById(R.id.course_fl_layout_title);
        course_fl_layout_title.setText(courseInfo.getmCourseName());
        //课程详情的内容  HTML格式
        TextView course_details_label_content = mDetailsView.findViewById(R.id.course_details_label_content);
        new ModelHtmlUtils(mMainContext, course_details_label_content).setHtmlWithPic(courseInfo.getmCourseDetails());
        LinearLayout course_details_bottomlayout1 = mDetailsView.findViewById(R.id.course_details_bottomlayout1);
        LinearLayout course_details_bottomlayout = mDetailsView.findViewById(R.id.course_details_bottomlayout);

        if (courseInfo.getmCourseIsStartLearn().equals("1")) {
            Button course_details_buy_button1 = mDetailsView.findViewById(R.id.course_details_buy_button1);
            course_details_buy_button1.setText("已购买");
        }
        if (courseInfo.getmCourseIsHave().equals("1")) {
            //已购买的课程将按钮栏替换掉
            course_details_bottomlayout1.setVisibility(View.VISIBLE);
            course_details_bottomlayout.setVisibility(View.INVISIBLE);
            //判断是否是0元课程
        } else if (courseInfo.getmCoursePrice().equals("免费")||
                courseInfo.getmCoursePrice().equals("0")||courseInfo.getmCoursePrice().equals("0.0")){
            //已购买的课程将按钮栏替换掉
            course_details_bottomlayout1.setVisibility(View.VISIBLE);
            course_details_bottomlayout.setVisibility(View.INVISIBLE);
            Button course_details_buy_button1 = mDetailsView.findViewById(R.id.course_details_buy_button1);
            course_details_buy_button1.setText("立即体验");
            mCourseInfo.setmCourseIsHave("1");
        } else {
            course_details_bottomlayout1.setVisibility(View.INVISIBLE);
            course_details_bottomlayout.setVisibility(View.VISIBLE);
        }
        if (mCourseInfo.getmCourseIsCollect().equals("1")) { //修改为收藏状态
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
        if (mDetailsView != null) {
            if (mCourseCatalogSum <= mCourseCatalogPage * mCourseCatalogCount) {
                TextView course_catalog_label_content_endtextview = mDetailsView.findViewById(R.id.course_catalog_label_content_endtextview);
                course_catalog_label_content_endtextview.setText("已显示全部章内容");
                course_catalog_label_content_endtextview.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
            } else {
                TextView course_catalog_label_content_endtextview = mDetailsView.findViewById(R.id.course_catalog_label_content_endtextview);
                course_catalog_label_content_endtextview.setText("点击此处加载更多章节");
                course_catalog_label_content_endtextview.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                course_catalog_label_content_endtextview.setOnClickListener(v -> {
                    if (mCourseCatalogSum <= mCourseCatalogPage * mCourseCatalogCount) {
                        course_catalog_label_content_endtextview.setText("已显示全部章内容");
                        course_catalog_label_content_endtextview.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                    } else {
                        getSingleCourseCatalogRecNewMore();
                    }
                });
            }
        }
        LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
        course_catalog_label_content_layout.removeAllViews();
        for (int i = 0; i < courseInfo.mCourseChaptersInfoList.size(); i++) {
            CourseChaptersInfo courseChaptersInfo = courseInfo.mCourseChaptersInfoList.get(i);
            if (courseChaptersInfo == null) {
                continue;
            }
            View catalog_chapterview = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_chapter, null);
            //判断是否显示加载更多按钮
            if (courseChaptersInfo.getmCourseSectionsSum() > courseChaptersInfo.getmCourseSectionsPage() * mCourseCatalogCount) {
                TextView course_catalog_more = catalog_chapterview.findViewById(R.id.course_catalog_more);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_more.getLayoutParams();
                ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                course_catalog_more.setLayoutParams(ll);
                course_catalog_more.setOnClickListener(v -> {
                    if (courseChaptersInfo.getmCourseSectionsSum() > courseChaptersInfo.getmCourseSectionsPage() * mCourseCatalogCount) {
                        getSingleCourseCatalogSectionMore(courseChaptersInfo.getmCourseChaptersId(), catalog_chapterview);
                    } else {
                        course_catalog_more.setText("暂无更多课程");
                        Toast.makeText(mMainContext, "暂无更多课程", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                TextView course_catalog_more = catalog_chapterview.findViewById(R.id.course_catalog_more);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_more.getLayoutParams();
                ll.height = 0;
                course_catalog_more.setLayoutParams(ll);
            }
            TextView course_catalog_label_name = catalog_chapterview.findViewById(R.id.course_catalog_label_name);
            course_catalog_label_name.setText(courseChaptersInfo.getmCourseChaptersName());
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
                        Toast.makeText(mMainContext, "本章节暂时没有课程", Toast.LENGTH_SHORT).show();
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
                    CourseCatalogRecordSectionsInit(course_catalog_label_content, courseChaptersInfo.getmCourseChaptersId());
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
                CourseCatalogRecordSectionsInit(course_catalog_label_content, courseChaptersInfo.getmCourseChaptersId());
            }
            course_catalog_label_content_layout.addView(catalog_chapterview);
            if (courseInfo.mCourseChaptersInfoList.size() - 1 == i) {
                //隐藏
                View course_catalog_label_line1 = catalog_chapterview.findViewById(R.id.course_catalog_label_line1);
                course_catalog_label_line1.setVisibility(View.INVISIBLE);
            }
        }
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
            if (mCourseInfo.mCourseChaptersInfoList.get(i).getmCourseChaptersId().equals(id)) {
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
            View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_chapter1, null);
            TextView course_catalog_record_chapter_name = view.findViewById(R.id.course_catalog_record_chapter_name);
            course_catalog_record_chapter_name.setText(courseSectionsInfo.getmCourseSectionsName());
            TextView course_catalog_record_chapter_learnprogress = view.findViewById(R.id.course_catalog_record_chapter_learnprogress);
            course_catalog_record_chapter_learnprogress.setText(courseSectionsInfo.getmCourseSectionsLearnProgress());
            view.setOnClickListener(v -> {
//                courseSectionsInfo.mVideoId = "28b8e6b1e87340c2a9dcac78729ed24c";
                //判断是否有播放权限
                if (mCourseInfo.getmCourseIsHave().equals("0")) {
                    Toast.makeText(mMainContext, "您还没有购买此课程", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断是否为失效课程
                if (!mCourseInfo.getmCourseValidityPeriod().equals("")) {
                    long CourseValidityPeriod = 0;
                    long currentTime = System.currentTimeMillis();
                    Date date = null;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        date = df.parse(mCourseInfo.getmCourseValidityPeriod());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date != null) {
                        CourseValidityPeriod = date.getTime();
                    }
                    if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime) {
                        Toast.makeText(mMainContext, "您购买的课程已过期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                CourseCatalogRecordGo(courseSectionsInfo.getmVideoId(), courseSectionsInfo.getmCourseSectionsId(),
                        courseSectionsInfo.getmCourseSectionsName(), courseSectionsInfo.getmCourseSectionsTime1());
            });
            course_catalog_label_content.addView(view);
            if (courseChaptersInfo.mCourseSectionsInfoList.size() != 1 && i != (courseChaptersInfo.mCourseSectionsInfoList.size() - 1)) {
                //添加横线
                View lineView = new View(mMainContext);
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
    public void CourseCatalogLiveInit(CourseInfo courseInfo, int type) {
        if (courseInfo == null) {
            return;
        }
        if (courseInfo.mCourseClassTimeInfoTodayList == null || courseInfo.mCourseClassTimeInfoBeforeList == null || courseInfo.mCourseClassTimeInfoAfterList == null) {
            return;
        }
        TextView course_catalog_label_content_endtextview = mDetailsView.findViewById(R.id.course_catalog_label_content_endtextview);
        course_catalog_label_content_endtextview.setText("已显示全部章内容");
        course_catalog_label_content_endtextview.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
        if (mcatalog_chapter_liveview == null) {
            mcatalog_chapter_liveview = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_live_chapter, null);
        }
        LinearLayout course_catalog_label_content_layout = mDetailsView.findViewById(R.id.course_catalog_label_content_layout);
        if (course_catalog_label_content_layout.getChildCount() == 0) {
            course_catalog_label_content_layout.addView(mcatalog_chapter_liveview);
        }
        if (type == 2) {
            //今日
            LinearLayout course_catalog_live_label_namelayout = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout);
            ImageView course_catalog_live_label_arrow_down = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down);
            ImageView course_catalog_live_label_arrow_right = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right);
            ModelExpandView course_catalog_live_label_expandView = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
            ll.width = 0;
            course_catalog_live_label_arrow_down.setLayoutParams(ll);
            LinearLayout course_catalog_live_label_content = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content);
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                    course_catalog_live_label_arrow_right.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down.getLayoutParams();
                    ll1.width = 0;
                    course_catalog_live_label_arrow_down.setLayoutParams(ll1);
                } else {
                    if (courseInfo.mCourseClassTimeInfoTodayList.size() == 0) {
                        Toast.makeText(mMainContext, "没有即将开始的课程", Toast.LENGTH_SHORT).show();
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
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
                ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content, "today");
                if (courseInfo.getmTodayLiveSum() > courseInfo.mCourseClassTimeInfoTodayList.size()) {
                    TextView course_catalog_live_more = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more);
                    LinearLayout.LayoutParams fl = (LinearLayout.LayoutParams) course_catalog_live_more.getLayoutParams();
                    fl.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                    course_catalog_live_more.setLayoutParams(fl);
                    course_catalog_live_more.setText("点击此处加载更多");
                    course_catalog_live_more.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    course_catalog_live_more.setOnClickListener(v -> {
                        if (courseInfo.getmTodayLiveSum() > courseInfo.mCourseClassTimeInfoTodayList.size()) {
                            //点击加载更多
                            getSingleCourseCatalogLiveNewMore(2);
                        } else {
                            course_catalog_live_more.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                            course_catalog_live_more.setText("已加载全部");
                        }
                    });
                } else {
                    TextView course_catalog_live_more = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more);
                    course_catalog_live_more.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                    course_catalog_live_more.setText("已加载全部");
                }
            }
        } else if (type == 3) {
            //后续
            LinearLayout course_catalog_live_label_namelayout1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout1);
            ImageView course_catalog_live_label_arrow_down1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down1);
            ImageView course_catalog_live_label_arrow_right1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right1);
            ModelExpandView course_catalog_live_label_expandView1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView1);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
            ll.width = 0;
            course_catalog_live_label_arrow_down1.setLayoutParams(ll);
            LinearLayout course_catalog_live_label_content1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content1);
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                    course_catalog_live_label_arrow_right1.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down1.getLayoutParams();
                    ll1.width = 0;
                    course_catalog_live_label_arrow_down1.setLayoutParams(ll1);
                } else {
                    if (courseInfo.mCourseClassTimeInfoAfterList.size() == 0) {
                        Toast.makeText(mMainContext, "后续暂时没有课程", Toast.LENGTH_SHORT).show();
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
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
                ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down1.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content1, "after");
                if (courseInfo.getmAfterLiveSum() > courseInfo.mCourseClassTimeInfoAfterList.size()) {
                    TextView course_catalog_live_more1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more1);
                    LinearLayout.LayoutParams fl = (LinearLayout.LayoutParams) course_catalog_live_more1.getLayoutParams();
                    fl.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                    course_catalog_live_more1.setLayoutParams(fl);
                    course_catalog_live_more1.setText("点击此处加载更多");
                    course_catalog_live_more1.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    course_catalog_live_more1.setOnClickListener(v -> {
                        if (courseInfo.getmAfterLiveSum() > courseInfo.mCourseClassTimeInfoAfterList.size()) {
                            //点击加载更多
                            getSingleCourseCatalogLiveNewMore(3);
                        } else {
                            course_catalog_live_more1.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                            course_catalog_live_more1.setText("已加载全部");
                        }
                    });
                } else {
                    TextView course_catalog_live_more1 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more1);
                    course_catalog_live_more1.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                    course_catalog_live_more1.setText("已加载全部");
                }
            }
        } else if (type == 1) {
            LinearLayout course_catalog_live_label_namelayout2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_namelayout2);
            ImageView course_catalog_live_label_arrow_down2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_down2);
            ImageView course_catalog_live_label_arrow_right2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_arrow_right2);
            ModelExpandView course_catalog_live_label_expandView2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_expandView2);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
            ll.width = 0;
            course_catalog_live_label_arrow_down2.setLayoutParams(ll);
            LinearLayout course_catalog_live_label_content2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_label_content2);
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp6);
                    course_catalog_live_label_arrow_right2.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) course_catalog_live_label_arrow_down2.getLayoutParams();
                    ll1.width = 0;
                    course_catalog_live_label_arrow_down2.setLayoutParams(ll1);
                } else {
                    if (courseInfo.mCourseClassTimeInfoBeforeList.size() == 0) {
                        Toast.makeText(mMainContext, "没有已结束课程", Toast.LENGTH_SHORT).show();
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
                    ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
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
                ll1.width = mcatalog_chapter_liveview.getResources().getDimensionPixelSize(R.dimen.dp10);
                course_catalog_live_label_arrow_down2.setLayoutParams(ll1);
                CourseCatalogLiveClassTimeInit(course_catalog_live_label_content2, "before");
                if (courseInfo.getmBeforeLiveSum() > courseInfo.mCourseClassTimeInfoBeforeList.size()) {
                    TextView course_catalog_live_more2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more2);
                    LinearLayout.LayoutParams fl = (LinearLayout.LayoutParams) course_catalog_live_more2.getLayoutParams();
                    fl.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                    course_catalog_live_more2.setLayoutParams(fl);
                    course_catalog_live_more2.setText("点击此处加载更多");
                    course_catalog_live_more2.setTextColor(mDetailsView.getResources().getColor(R.color.blue649cf0));
                    course_catalog_live_more2.setOnClickListener(v -> {
                        if (courseInfo.getmBeforeLiveSum() > courseInfo.mCourseClassTimeInfoBeforeList.size()) {
                            //点击加载更多
                            getSingleCourseCatalogLiveNewMore(1);
                        } else {
                            course_catalog_live_more2.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                            course_catalog_live_more2.setText("已加载全部");
                        }
                    });
                } else {
                    TextView course_catalog_live_more2 = mcatalog_chapter_liveview.findViewById(R.id.course_catalog_live_more2);
                    course_catalog_live_more2.setTextColor(mDetailsView.getResources().getColor(R.color.graycc999999));
                    course_catalog_live_more2.setText("已加载全部");
                }
            }
        }
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
                View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.getmCourseClassTimeName());
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.getmCourseClassTimeStartTime());
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoTodayList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoTodayList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mMainContext);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                CourseClassTimeInfo finalCourseClassTimeInfo = courseClassTimeInfo;
                view.setOnClickListener(v -> {
                    //判断是否有播放权限
                    if (mCourseInfo.getmCourseIsHave().equals("0")) {
                        Toast.makeText(mMainContext, "您还没有购买此课程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.getmCourseValidityPeriod().equals("")) {
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(mCourseInfo.getmCourseValidityPeriod());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            CourseValidityPeriod = date.getTime();
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime) {
                            Toast.makeText(mMainContext, "您购买的课程已过期", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (finalCourseClassTimeInfo.getLiveStatus() == 2 || finalCourseClassTimeInfo.getLiveStatus() == 1 || finalCourseClassTimeInfo.getLiveStatus() == 0) {
                        mMainContext.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.getmCourseClassTimeId()), 2, PlayType.LIVE);
                    } else if (finalCourseClassTimeInfo.getLiveStatus() == 3) {
                        mMainContext.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.getmCourseClassTimeId()), 2, PlayType.PLAYBACK);
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
                View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.getmCourseClassTimeName());
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.getmCourseClassTimeStartTime());
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoBeforeList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoBeforeList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mMainContext);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                CourseClassTimeInfo finalCourseClassTimeInfo = courseClassTimeInfo;
                view.setOnClickListener(v -> {
                    //判断是否有播放权限
                    if (mCourseInfo.getmCourseIsHave().equals("0")) {
                        Toast.makeText(mMainContext, "您还没有购买此课程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.getmCourseValidityPeriod().equals("")) {
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(mCourseInfo.getmCourseValidityPeriod());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            CourseValidityPeriod = date.getTime();
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime) {
                            Toast.makeText(mMainContext, "您购买的课程已过期", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (finalCourseClassTimeInfo.getLiveStatus() == 2 || finalCourseClassTimeInfo.getLiveStatus() == 1 || finalCourseClassTimeInfo.getLiveStatus() == 0) {
                        mMainContext.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.getmCourseClassTimeId()), 2, PlayType.LIVE);
                    } else if (finalCourseClassTimeInfo.getLiveStatus() == 3) {
                        mMainContext.LoginLiveOrPlayback(Integer.parseInt(finalCourseClassTimeInfo.getmCourseClassTimeId()), 2, PlayType.PLAYBACK);
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
                View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_catalog_live_chapter1, null);
                TextView course_catalog_live_chapter_name = view.findViewById(R.id.course_catalog_live_chapter_name);
                course_catalog_live_chapter_name.setText(courseClassTimeInfo.getmCourseClassTimeName());
                TextView course_catalog_live_chapter_time = view.findViewById(R.id.course_catalog_live_chapter_time);
                course_catalog_live_chapter_time.setText(courseClassTimeInfo.getmCourseClassTimeStartTime());
                course_catalog_label_content.addView(view);
                if (mCourseInfo.mCourseClassTimeInfoAfterList.size() != 1 && i != (mCourseInfo.mCourseClassTimeInfoAfterList.size() - 1)) {
                    //添加横线
                    View lineView = new View(mMainContext);
                    lineView.setBackgroundColor(view.getResources().getColor(R.color.whitee5e5e5));
                    course_catalog_label_content.addView(lineView);
                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) lineView.getLayoutParams();
                    ll.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    ll.height = view.getResources().getDimensionPixelSize(R.dimen.dp1);
                    lineView.setLayoutParams(ll);
                }
                view.setOnClickListener(v -> {
                    //判断是否有播放权限
                    if (mCourseInfo.getmCourseIsHave().equals("0")) {
                        Toast.makeText(mMainContext, "您还没有购买此课程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断是否为失效课程
                    if (!mCourseInfo.getmCourseValidityPeriod().equals("")) {
                        long CourseValidityPeriod = 0;
                        long currentTime = System.currentTimeMillis();
                        Date date = null;
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        try {
                            date = df.parse(mCourseInfo.getmCourseValidityPeriod());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date != null) {
                            CourseValidityPeriod = date.getTime();
                        }
                        if (CourseValidityPeriod != 0 && CourseValidityPeriod < currentTime) {
                            Toast.makeText(mMainContext, "您购买的课程已过期", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(mMainContext, "直播课程还未开始呢", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    //课程-下载初始化
    public void CourseDownloadInit() {
        if (mCourseDownloadDialog != null) {
            mCourseDownloadDialog.dismiss();
        }
        mCourseDownloadDialog = new ControllerPopDialog(mMainContext, R.style.customdialogstyle, R.layout.modelcoursedetails_download);
        mCourseDownloadDialog.setOnKeyListener(keylistener);
        mCourseDownloadDialog.show();
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
                View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_download1, null);
                TextView coursedetails_download1_name = view.findViewById(R.id.coursedetails_download1_name);
                coursedetails_download1_name.setText(courseSectionsInfo.getmCourseSectionsName());
                coursedetails_download1_name.setHint(courseSectionsInfo.getmCourseSectionsId());
                ImageView coursedetails_download1_image = view.findViewById(R.id.coursedetails_download1_image);
                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mMainContext).rawQuery(  //查可用且没有被删除的数据库
                        "select video_len from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() + " and section_id=" + courseSectionsInfo.getmCourseSectionsId(), null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
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
                        Cursor cursor1 = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mMainContext).rawQuery(  //查可用且没有被删除的数据库
                                "select video_download_localname from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() +
                                        " and section_id=" + courseSectionsInfo.getmCourseSectionsId(), null);
                        if (cursor1 != null) {
                            String localFileName = "";
                            while (cursor1.moveToNext()) {
                                int localFileNameIndex = cursor1.getColumnIndex("video_download_localname");
                                localFileName = cursor1.getString(localFileNameIndex);
                            }
                            cursor1.close();
                            if (!localFileName.equals("")) {
                                //先删除掉以前的记录
                                ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mMainContext).execSQL("delete from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() +
                                        " and section_id=" + courseSectionsInfo.getmCourseSectionsId());
                                //删除本地文件
                                ModelRootFileUtil.deleteFile(ModelRootFileUtil.getRootFile(ModelRootFileUtil.mRecordVideoFileDownloadDir) + "/" + localFileName);
                            }
                        }
                        //向数据库中插入一条新纪录
                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mMainContext).execSQL("INSERT INTO `video_download_table` \n" +
                                "(`video_download_time`,`video_download_url`,`video_download_name`,`video_download_localname`,`chapter_id`,`section_id`,`video_len`) VALUES \n" +
                                "('" + time + "', '" + courseSectionsInfo.getmCourseSectionsDownloadUrl() + "', '" + courseSectionsInfo.getmCourseSectionsName() +
                                "', '" + time + courseSectionsInfo.getmCourseSectionsName() + "', '" + courseChaptersInfo.getmCourseChaptersId() + "', '" +
                                courseSectionsInfo.getmCourseSectionsId() + "', '" + "');");
                        //添加一条下载
                    }
                });
                coursedetails_download_chapterlist.addView(view);
                view1 = view;
                count++;
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
                            if (coursedetails_download1_name.getHint().toString().equals(courseSectionsInfo.getmCourseSectionsId())) {
                                m_isFind = true;
                                //将此条数据插入到本地数据库中
                                long time = System.currentTimeMillis();
                                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mMainContext).rawQuery(  //查可用且没有被删除的数据库
                                        "select video_download_localname from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() +
                                                " and section_id=" + courseSectionsInfo.getmCourseSectionsId(), null);
                                if (cursor != null) {
                                    String localFileName = "";
                                    while (cursor.moveToNext()) {
                                        int localFileNameIndex = cursor.getColumnIndex("video_download_localname");
                                        localFileName = cursor.getString(localFileNameIndex);
                                    }
                                    cursor.close();
                                    if (!localFileName.equals("")) {
                                        //先删除掉以前的记录
                                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mMainContext).execSQL("delete from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() +
                                                " and section_id=" + courseSectionsInfo.getmCourseSectionsId());
                                        //删除本地文件
                                        ModelRootFileUtil.deleteFile(ModelRootFileUtil.getRootFile(ModelRootFileUtil.mRecordVideoFileDownloadDir) + "/" + localFileName);
                                    }
                                }
                                //将所有的未缓存视频加入缓存列表
                                ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mMainContext).execSQL("INSERT INTO `video_download_table` \n" +
                                        "(`video_download_time`,`video_download_url`,`video_download_name`,`video_download_localname`,`chapter_id`,`section_id`,`video_len`) VALUES \n" +
                                        "('" + time + "', '" + courseSectionsInfo.getmCourseSectionsDownloadUrl() + "', '" + courseSectionsInfo.getmCourseSectionsName() +
                                        "', '" + time + courseSectionsInfo.getmCourseSectionsName() + "', '" + courseChaptersInfo.getmCourseChaptersId() + "', '" +
                                        courseSectionsInfo.getmCourseSectionsId() + "', '" + "');");
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
            mDownloadManagerView = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_download_manager, null);
        }
        int count = 0;
        LinearLayout course_downloadmanager_layout_content = mDownloadManagerView.findViewById(R.id.course_downloadmanager_layout_content);
        course_downloadmanager_layout_content.removeAllViews();
        for (int i = 0; i < mCourseInfo.mCourseChaptersInfoList.size(); i++) {
            CourseChaptersInfo courseChaptersInfo = mCourseInfo.mCourseChaptersInfoList.get(i);
            if (courseChaptersInfo == null) {
                continue;
            }
            View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_download_manager_child, null);
            TextView course_downloadmanager_child_titletext = view.findViewById(R.id.course_downloadmanager_child_titletext);
            course_downloadmanager_child_titletext.setText(courseChaptersInfo.getmCourseChaptersName());
            course_downloadmanager_child_titletext.setHint(courseChaptersInfo.getmCourseChaptersId());
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
                Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mMainContext).rawQuery(  //查可用且没有被删除的数据库
                        "select video_len from video_download_table where chapter_id=" + courseChaptersInfo.getmCourseChaptersId() + " and section_id=" + courseSectionsInfo.getmCourseSectionsId(), null);
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
                View view1 = LayoutInflater.from(mMainContext).inflate(R.layout.modelcoursedetails_download_manager_child1, null);
                TextView course_downloadmanager_child1_name = view1.findViewById(R.id.course_downloadmanager_child1_name);
                course_downloadmanager_child1_name.setText(courseSectionsInfo.getmCourseSectionsName());
                course_downloadmanager_child1_name.setHint(courseSectionsInfo.getmCourseSectionsId());
                ProgressBar progress_bar_healthy = view1.findViewById(R.id.progress_bar_healthy);
                int progress = 0;
//                try {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //计算下载进度
////                        progress = Math.toIntExact(Long.valueOf(info.mCourseSectionsDownloadSize)
////                                / video_len);
////                    }
//                } catch (Exception e){
//
//                }
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
            View view = mMainContext.getLayoutInflater().inflate(R.layout.dialog_sure_cancel, null);
            mMyDialog = new ControllerCenterDialog(mMainContext, 0, 0, view, R.style.DialogTheme);
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
                            Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mMainContext).rawQuery(  //查可用且没有被删除的数据库
                                    "select video_download_localname from video_download_table where section_id=" + course_downloadmanager_child1_name.getHint().toString(), null);
                            if (cursor != null) {
                                String localFileName = "";
                                while (cursor.moveToNext()) {
                                    int localFileNameIndex = cursor.getColumnIndex("video_download_localname");
                                    localFileName = cursor.getString(localFileNameIndex);
                                }
                                cursor.close();
                                if (!localFileName.equals("")) {
                                    //先删除掉以前的记录
                                    ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mMainContext).execSQL("delete from video_download_table where section_id=" + course_downloadmanager_child1_name.getHint().toString());
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
    private void CourseCatalogRecordGo(String videoId, String SectionsId, String title, int mCourseSectionsTime1) {
        if (videoId == null) {
            Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
            return;
        }
        if (videoId.equals("")) {
            Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
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
        HashMap<String, String> paramsMap1 = new HashMap<>();
        paramsMap1.put("video_id", videoId);
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.getAliCourseAccessVideo(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {

            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response.body() == null) {
                    Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null) {
                    Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(), baseBean.getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (baseBean.getErrorCode() != 200) {
                    Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (baseBean.getData() == null) {
                    Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                Map<String, Object> map = baseBean.getData();
                String SecurityToken = (String) map.get("SecurityToken");
                String AccessKeyId = (String) map.get("AccessKeyId");
                String AccessKeySecret = (String) map.get("AccessKeySecret");
//                String resourse_name = (String) map.get("resourse_name");
                if (SecurityToken == null || AccessKeyId == null || AccessKeySecret == null) {
                    Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mMainContext.onStsSuccess(videoId, AccessKeyId, AccessKeySecret, SecurityToken);
                AliyunVodPlayerView aliyunVodPlayerView = mDetailsView.findViewById(R.id.aliyunVodPlayerView);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) aliyunVodPlayerView.getLayoutParams();
                rl.height = mDetailsView.getResources().getDimensionPixelSize(R.dimen.dp_244);
                aliyunVodPlayerView.VideoIdSet(videoId, SectionsId, mCourseSectionsTime1);
                mMainContext.setmAliyunVodPlayerView(aliyunVodPlayerView);
//                cover = "http://video.huozhongedu.cn/bea996b56ca3466e81b2a37ebdf39756/snapshots/5c712f5ca526445d8e56b0fbe0235de3-00003.jpg";
//                if (!cover.equals("")){
//                    aliyunVodPlayerView.setCoverUri(cover);
//                }
                aliyunVodPlayerView.setTitleName(title);
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Toast.makeText(mMainContext, "此课程暂无播放资源", Toast.LENGTH_SHORT).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
                return;
            }
        });
    }

    //获取课程详情
    private void getSingleCourseDetails() {
        if (mCourseInfo.getmCourseId().equals("")) {
            Toast.makeText(mMainContext, "查询课程详情失败", Toast.LENGTH_SHORT).show();
            mMainContext.onClickCourseDetailsReturn(mDetailsView);
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
        HashMap<String, Integer> paramsMap1 = new HashMap<>();
        paramsMap1.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap1.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.findSingleCourseDetails(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    Toast.makeText(mMainContext, "查询课程详情失败", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    mMainContext.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                ModelObservableInterface.BaseBean baseBean = response.body();
                if (baseBean == null) {
                    Toast.makeText(mMainContext, "查询课程详情失败", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    mMainContext.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(), baseBean.getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    mMainContext.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                Map<String, Object> courseDataBean = baseBean.getData();
                if (courseDataBean == null) {
                    Toast.makeText(mMainContext, "查询课程详情失败", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    mMainContext.onClickCourseDetailsReturn(mDetailsView);
                    return;
                }
                String invalid_date = String.valueOf(courseDataBean.get("invalid_date"));
                mCourseInfo.setmCourseDetails(String.valueOf(courseDataBean.get("details")));
                Map<String, String> stuCourseStatusInfo = (Map<String, String>) courseDataBean.get("stuCourseStatusInfo");
                Object rateOfLearningO = courseDataBean.get("rateOfLearning");
                if (rateOfLearningO != null) {
                    float rateOfLearning = Float.parseFloat(String.valueOf(courseDataBean.get("rateOfLearning")));
                    if (rateOfLearning <= 0) {
                        mCourseInfo.setmCourseIsStartLearn("0");
                    } else {
                        mCourseInfo.setmCourseIsStartLearn("1");
                    }
                }
//                String effictive_days = String.valueOf(courseDataBean.get("effictive_days"));
                mCourseInfo.setmCourseMessage(String.valueOf(courseDataBean.get("course_describe")));
//                mCourseInfo.setmCourseTotalHours(String.valueOf(courseDataBean.get("total_hours")));
                mCourseInfo.setmCourseTotalHours(String.valueOf(courseDataBean.get("ct_num")));
                if (mCourseInfo.getmCourseTotalHours().indexOf(".") >= 0) {
                    mCourseInfo.setmCourseTotalHours(mCourseInfo.getmCourseTotalHours().substring(0, mCourseInfo.getmCourseTotalHours().indexOf(".")));
                }
                if (stuCourseStatusInfo != null) {
                    mCourseInfo.setmCourseIsCollect(String.valueOf(stuCourseStatusInfo.get("collection_status")));
                    if (mCourseInfo.getmCourseIsCollect().indexOf(".") >= 0) {
                        mCourseInfo.setmCourseIsCollect(mCourseInfo.getmCourseIsCollect().substring(0, mCourseInfo.getmCourseIsCollect().indexOf(".")));
                    }
                    String enrollment_time = stuCourseStatusInfo.get("enrollment_time");
                    if (enrollment_time != null) {
                        if (enrollment_time.equals("")) {
                            mCourseInfo.setmCourseIsHave("0");
                        } else {
                            mCourseInfo.setmCourseIsHave("1");
                        }
                    }
                }
                mCourseInfo.setmCourseValidityPeriod(invalid_date);
                //课程详情界面
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CourseDetailsInit(mCourseInfo);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程详情失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
                mMainContext.onClickCourseDetailsReturn(mDetailsView);
            }
        });
    }

    //收藏、取消收藏课程
    private void CollectOrNotCollectCourses() {
        if (mCourseInfo == null) {
            Toast.makeText(mMainContext, "系统错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (mCourseInfo.getmCourseId().equals("") || mMainContext.mStuId.equals("")) {
            if (!mCourseInfo.getmCourseIsCollect().equals("1")) {
                Toast.makeText(mMainContext, "收藏失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mMainContext, "取消收藏失败", Toast.LENGTH_LONG).show();
            }
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
        HashMap<String, Integer> paramsMap1 = new HashMap<>();
        if (!mCourseInfo.getmCourseIsCollect().equals("1")) {
            paramsMap1.put("collection_status", 1);
        } else {
            paramsMap1.put("collection_status", 0);
        }
        paramsMap1.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        paramsMap1.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        String strEntity = gson.toJson(paramsMap1);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.collectOrNotCollectCourses(body);
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (!mCourseInfo.getmCourseIsCollect().equals("1")) {
                        Toast.makeText(mMainContext, "收藏失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mMainContext, "取消收藏失败", Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (response.body() == null) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (!mCourseInfo.getmCourseIsCollect().equals("1")) {
                        Toast.makeText(mMainContext, "收藏失败", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mMainContext, "取消收藏失败", Toast.LENGTH_LONG).show();
                    }
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(response.body().getErrorCode(), response.body().getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (mCourseInfo.getmCourseIsCollect().equals("1")) { //成功修改状态
                    mCourseInfo.setmCourseIsCollect("0");
                } else {
                    mCourseInfo.setmCourseIsCollect("1");
                }
                if (mCourseInfo.getmCourseIsCollect().equals("1")) {
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
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                if (!mCourseInfo.getmCourseIsCollect().equals("1")) {
                    Toast.makeText(mMainContext, "收藏失败", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mMainContext, "取消收藏失败", Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //获取课程直播目录 type: 1 今天之前的; 2 今天的; 3 今天之后的
    private void getSingleCourseCatalogLiveNew(int type) {
        if (mCourseInfo.getmCourseId().equals("")) {
            return;
        }
        //将录播内容清空
        mCourseInfo.mCourseChaptersInfoList.clear();
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        paramsMap.put("type", type);
        paramsMap.put("pageNum", 1);
        paramsMap.put("pageSize", mCourseCatalogCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<CourseCatalogLiveBeanNew> call = modelObservableInterface.findSingleCourseCatalogLive(body);
        call.enqueue(new Callback<CourseCatalogLiveBeanNew>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CourseCatalogLiveBeanNew> call, Response<CourseCatalogLiveBeanNew> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                CourseCatalogLiveBeanNew courseCatalogLiveBeanNew = response.body();
                if (courseCatalogLiveBeanNew == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogLiveBeanNew.getErrorCode(), courseCatalogLiveBeanNew.getErrorMsg())) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                CourseCatalogLiveBeanNew.CourseCatalogLiveData courseCatalogLiveData = courseCatalogLiveBeanNew.getData();
                if (courseCatalogLiveData == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                if (courseCatalogLiveData.total == null || courseCatalogLiveData.list == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                if (type == 1) {
                    mCourseInfo.setmBeforeLiveSum(courseCatalogLiveData.total);
                    mCourseInfo.mCourseClassTimeInfoBeforeList.clear();
                } else if (type == 2) {
                    mCourseInfo.setmTodayLiveSum(courseCatalogLiveData.total);
                    mCourseInfo.mCourseClassTimeInfoTodayList.clear();
                } else if (type == 3) {
                    mCourseInfo.setmAfterLiveSum(courseCatalogLiveData.total);
                    mCourseInfo.mCourseClassTimeInfoAfterList.clear();
                }
                //计算今日课程、历史课程、后续课程
                for (CourseCatalogLiveBeanNew.CourseCatalogLiveDataList courseCatalogLiveDataList : courseCatalogLiveData.list) {
                    if (courseCatalogLiveDataList == null) {
                        continue;
                    }
                    CourseClassTimeInfo info = new CourseClassTimeInfo();
                    info.setmCourseClassTimeId(String.valueOf(courseCatalogLiveDataList.course_times_id));
                    info.setmCourseClassTimeName(courseCatalogLiveDataList.ct_name);
                    info.setLiveStatus(courseCatalogLiveDataList.liveStatus);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = null, date1 = null;
                    long begin_class_date = 0;
                    long end_time_datess = 0;
                    try {
                        date = df.parse(courseCatalogLiveDataList.begin_class_date);
                        date1 = df.parse(courseCatalogLiveDataList.end_time_datess);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date != null && date1 != null) {
                        begin_class_date = date.getTime();
                        end_time_datess = date1.getTime();
                    }
                    df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                    info.setmCourseClassTimeStartTime(df.format(new Date(begin_class_date)) + "~" + df1.format(new Date(end_time_datess)));
                    if (type == 1) {
                        mCourseInfo.mCourseClassTimeInfoBeforeList.add(info);
                    } else if (type == 2) {
                        mCourseInfo.mCourseClassTimeInfoTodayList.add(info);
                    } else if (type == 3) {
                        mCourseInfo.mCourseClassTimeInfoAfterList.add(info);
                    }
                }
                if (mCurrentCatalogTab.equals("Live")) {
                    CourseCatalogLiveInit(mCourseInfo, type);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }
            }

            @Override
            public void onFailure(Call<CourseCatalogLiveBeanNew> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程目录失败", Toast.LENGTH_LONG).show();
                if (mCurrentCatalogTab.equals("Live")) {
                    CourseCatalogLiveInit(mCourseInfo, type);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }
            }
        });
    }

    //获取课程直播目录 type: 1 今天之前的; 2 今天的; 3 今天之后的
    private void getSingleCourseCatalogLiveNewMore(int type) {
        if (mCourseInfo.getmCourseId().equals("")) {
            return;
        }
        //将录播内容清空
        mCourseInfo.mCourseChaptersInfoList.clear();
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);

        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        int pageNum = 1;
        if (type == 1) {
            pageNum = mCourseInfo.mCourseClassTimeInfoBeforeList.size() / mCourseCatalogCount;
            if (mCourseInfo.mCourseClassTimeInfoBeforeList.size() % mCourseCatalogCount == 0) {
                pageNum = pageNum + 1;
            }
        } else if (type == 2) {
            pageNum = mCourseInfo.mCourseClassTimeInfoTodayList.size() / mCourseCatalogCount;
            if (mCourseInfo.mCourseClassTimeInfoTodayList.size() % mCourseCatalogCount == 0) {
                pageNum = pageNum + 1;
            }
        } else if (type == 3) {
            pageNum = mCourseInfo.mCourseClassTimeInfoAfterList.size() / mCourseCatalogCount;
            if (mCourseInfo.mCourseClassTimeInfoAfterList.size() % mCourseCatalogCount == 0) {
                pageNum = pageNum + 1;
            }
        }
        if (pageNum == 0) {
            pageNum = 1;
        }
        paramsMap.put("type", type);
        paramsMap.put("pageNum", pageNum);
        paramsMap.put("pageSize", mCourseCatalogCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<CourseCatalogLiveBeanNew> call = modelObservableInterface.findSingleCourseCatalogLive(body);
        call.enqueue(new Callback<CourseCatalogLiveBeanNew>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CourseCatalogLiveBeanNew> call, Response<CourseCatalogLiveBeanNew> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                CourseCatalogLiveBeanNew courseCatalogLiveBeanNew = response.body();
                if (courseCatalogLiveBeanNew == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogLiveBeanNew.getErrorCode(), courseCatalogLiveBeanNew.getErrorMsg())) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                CourseCatalogLiveBeanNew.CourseCatalogLiveData courseCatalogLiveData = courseCatalogLiveBeanNew.getData();
                if (courseCatalogLiveData == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                if (courseCatalogLiveData.total == null || courseCatalogLiveData.list == null) {
                    if (mCurrentCatalogTab.equals("Live")) {
                        CourseCatalogLiveInit(mCourseInfo, type);
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                    return;
                }
                //计算今日课程、历史课程、后续课程
                for (CourseCatalogLiveBeanNew.CourseCatalogLiveDataList courseCatalogLiveDataList : courseCatalogLiveData.list) {
                    if (courseCatalogLiveDataList == null) {
                        continue;
                    }
                    CourseClassTimeInfo info = new CourseClassTimeInfo();
                    info.setmCourseClassTimeId(String.valueOf(courseCatalogLiveDataList.course_times_id));
                    info.setmCourseClassTimeName(courseCatalogLiveDataList.ct_name);
                    info.setLiveStatus(courseCatalogLiveDataList.liveStatus);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = null, date1 = null;
                    long begin_class_date = 0;
                    long end_time_datess = 0;
                    try {
                        date = df.parse(courseCatalogLiveDataList.begin_class_date);
                        date1 = df.parse(courseCatalogLiveDataList.end_time_datess);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date != null && date1 != null) {
                        begin_class_date = date.getTime();
                        end_time_datess = date1.getTime();
                    }
                    df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                    info.setmCourseClassTimeStartTime(df.format(new Date(begin_class_date)) + "~" + df1.format(new Date(end_time_datess)));
                    if (type == 1) {
                        mCourseInfo.mCourseClassTimeInfoBeforeList.add(info);
                    } else if (type == 2) {
                        mCourseInfo.mCourseClassTimeInfoTodayList.add(info);
                    } else if (type == 3) {
                        mCourseInfo.mCourseClassTimeInfoAfterList.add(info);
                    }
                }
                if (mCurrentCatalogTab.equals("Live")) {
                    CourseCatalogLiveInit(mCourseInfo, type);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }
            }

            @Override
            public void onFailure(Call<CourseCatalogLiveBeanNew> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程目录失败", Toast.LENGTH_LONG).show();
                if (mCurrentCatalogTab.equals("Live")) {
                    CourseCatalogLiveInit(mCourseInfo, type);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                }
            }
        });
    }

    //获取课程录播目录
    private void getSingleCourseCatalogRecNew() {
        if (mCourseInfo.getmCourseId().equals("")) {
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        mCourseCatalogPage = 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        paramsMap.put("pageNum", mCourseCatalogPage);
        paramsMap.put("pageSize", mCourseCatalogCount);
        paramsMap.put("sectionPageSize", mCourseCatalogCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<CourseCatalogBeanNew> call = modelObservableInterface.findSingleCourseCatalogRecNew(body);
        call.enqueue(new Callback<CourseCatalogBeanNew>() {
            @Override
            public void onResponse(Call<CourseCatalogBeanNew> call, Response<CourseCatalogBeanNew> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogBeanNew courseCatalogBeanNew = response.body();
                if (courseCatalogBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogBeanNew.getErrorCode(), courseCatalogBeanNew.getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogBeanNew.CourseCatalogDataBeanNew courseCatalogDataBeanNew = courseCatalogBeanNew.getData();
                if (courseCatalogDataBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (courseCatalogDataBeanNew.sectionNUM == null || courseCatalogDataBeanNew.chapterList == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mRecCourseSum = courseCatalogDataBeanNew.sectionNUM; //录播课程总数
                if (courseCatalogDataBeanNew.chapterList.total == null || courseCatalogDataBeanNew.chapterList.list == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mCourseCatalogSum = courseCatalogDataBeanNew.chapterList.total;
                mCourseInfo.mCourseChaptersInfoList.clear();//清除原内存中储存的信息
                for (CourseCatalogBeanNew.CourseCatalogChapterData courseCatalogChapterData : courseCatalogDataBeanNew.chapterList.list) {
                    if (courseCatalogChapterData == null) {
                        continue;
                    }
                    CourseChaptersInfo courseChaptersInfo = new CourseChaptersInfo();
                    courseChaptersInfo.setmCourseChaptersId(String.valueOf(courseCatalogChapterData.chapter_id));
                    courseChaptersInfo.setmCourseChaptersName(courseCatalogChapterData.chapter_name);
                    courseChaptersInfo.setmCourseChaptersOrder(String.valueOf(courseCatalogChapterData.chapter_sort));
                    if (courseCatalogChapterData.sectionList != null) { //给课程的章分配节信息
                        if (courseCatalogChapterData.sectionList.total != null || courseCatalogChapterData.sectionList.list != null) {
                            courseChaptersInfo.setmCourseSectionsSum(courseCatalogChapterData.sectionList.total);
                            for (CourseCatalogBeanNew.CourseCatalogSectionBean courseCatalogSectionBean : courseCatalogChapterData.sectionList.list) {
                                if (courseCatalogSectionBean == null) {
                                    continue;
                                }
                                CourseSectionsInfo courseSectionsInfo = new CourseSectionsInfo();
                                courseSectionsInfo.setmCourseSectionsId(String.valueOf(courseCatalogSectionBean.section_id));
                                courseSectionsInfo.setmCourseSectionsOrder(String.valueOf(courseCatalogSectionBean.section_sort));
                                courseSectionsInfo.setmCourseSectionsName(courseCatalogSectionBean.section_name);
                                courseSectionsInfo.setmVideoId(courseCatalogSectionBean.video_id);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                                if (courseCatalogSectionBean.Duration == null) {
                                    courseCatalogSectionBean.Duration = 0;
                                }
                                String hms = formatter.format(courseCatalogSectionBean.Duration * 1000);
                                courseSectionsInfo.setmCourseSectionsTime(hms);
                                courseSectionsInfo.setmCourseSectionsTime1(courseCatalogSectionBean.Duration * 1000);
                                courseSectionsInfo.setmCourseSectionsLearnProgress(courseCatalogSectionBean.sectionLearningRate * 100 + "%");
                                courseChaptersInfo.mCourseSectionsInfoList.add(courseSectionsInfo);
                            }
                        }
                    }
                    mCourseInfo.mCourseChaptersInfoList.add(courseChaptersInfo);
                }
                if (mCurrentCatalogTab.equals("Record")) {
                    //修改body为录播
                    CourseCatalogRecordInit(mCourseInfo);
                }
                mPage = "Catalog";
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<CourseCatalogBeanNew> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程目录失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }


    private void getSinglematerialslogRecNew() {
        if (mCourseInfo.getmCourseId().equals("")) {
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        mCourseCatalogPage = 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));

        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<materialsBean> call = modelObservableInterface.queryCourseResData(body);
        call.enqueue(new Callback<materialsBean>() {
            @Override
            public void onResponse(Call<materialsBean> call, Response<materialsBean> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                materialsBean materialsBean = response.body();
                if (materialsBean == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(materialsBean.code, "")) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                List<ModelCourseCover.materialsBean.materialsBeanData> materialsDataBeanNew = materialsBean.getData();
                if (materialsDataBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                //获取课程资料文件
                if (mDetailsView != null){
                    ControllerListViewForScrollView mlistview = mDetailsView.findViewById(R.id .course_materials_label_list_view);
                    mlistview.setAdapter(new MyAdapter(mMainContext,materialsDataBeanNew));
                    mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            TextView text = view.findViewById(R.id.text_name);
                            if (text.getHint() == null){
                                Toast.makeText(mMainContext,"无法获取该文件",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String string  = String.valueOf(text.getHint());
                            //判断文件格式
                            if (string.contains(".pdf")||string.contains(".pdfx")){
                                Intent intent = new Intent(mMainContext,PDFActivity.class);
                                intent.putExtra("url",string);
                                mMainContext.startActivity(intent);
                            }else if (string.contains(".doc")||string.contains(".docx")||string.contains(".xls")||string.contains(".xlsx")
                            ||string.contains(".ppt")||string.contains(".pptx")){
                                Intent intent = new Intent(mMainContext,OfficeActivity.class);
                                intent.putExtra("url",string);
                                mMainContext.startActivity(intent);
                            }else {
                                Toast.makeText(mMainContext,"文件格式不正确",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<materialsBean> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程资料失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //获取课程录播目录-加载更多
    private void getSingleCourseCatalogRecNewMore() {
        if (mCourseInfo.getmCourseId().equals("")) {
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMainContext.mIpadress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(ModelObservableInterface.client)
                .build();

        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        mCourseCatalogPage = mCourseCatalogPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_id", Integer.valueOf(mCourseInfo.getmCourseId()));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        paramsMap.put("pageNum", mCourseCatalogPage);
        paramsMap.put("pageSize", mCourseCatalogCount);
        paramsMap.put("sectionPageSize", mCourseCatalogCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<CourseCatalogBeanNew> call = modelObservableInterface.findSingleCourseCatalogRecNew(body);
        call.enqueue(new Callback<CourseCatalogBeanNew>() {
            @Override
            public void onResponse(Call<CourseCatalogBeanNew> call, Response<CourseCatalogBeanNew> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogBeanNew courseCatalogBeanNew = response.body();
                if (courseCatalogBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogBeanNew.getErrorCode(), courseCatalogBeanNew.getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogBeanNew.CourseCatalogDataBeanNew courseCatalogDataBeanNew = courseCatalogBeanNew.getData();
                if (courseCatalogDataBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (courseCatalogDataBeanNew.sectionNUM == null || courseCatalogDataBeanNew.chapterList == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mRecCourseSum = courseCatalogDataBeanNew.sectionNUM; //录播课程总数
                if (courseCatalogDataBeanNew.chapterList.total == null || courseCatalogDataBeanNew.chapterList.list == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                mCourseCatalogSum = courseCatalogDataBeanNew.chapterList.total;
                for (CourseCatalogBeanNew.CourseCatalogChapterData courseCatalogChapterData : courseCatalogDataBeanNew.chapterList.list) {
                    if (courseCatalogChapterData == null) {
                        continue;
                    }
                    CourseChaptersInfo courseChaptersInfo = new CourseChaptersInfo();
                    courseChaptersInfo.setmCourseChaptersId(String.valueOf(courseCatalogChapterData.chapter_id));
                    courseChaptersInfo.setmCourseChaptersName(courseCatalogChapterData.chapter_name);
                    courseChaptersInfo.setmCourseChaptersOrder(String.valueOf(courseCatalogChapterData.chapter_sort));
                    if (courseCatalogChapterData.sectionList != null) { //给课程的章分配节信息
                        if (courseCatalogChapterData.sectionList.total != null || courseCatalogChapterData.sectionList.list != null) {
                            courseChaptersInfo.setmCourseSectionsSum(courseCatalogChapterData.sectionList.total);
                            for (CourseCatalogBeanNew.CourseCatalogSectionBean courseCatalogSectionBean : courseCatalogChapterData.sectionList.list) {
                                if (courseCatalogSectionBean == null) {
                                    continue;
                                }
                                CourseSectionsInfo courseSectionsInfo = new CourseSectionsInfo();
                                courseSectionsInfo.setmCourseSectionsId(String.valueOf(courseCatalogSectionBean.section_id));
                                courseSectionsInfo.setmCourseSectionsOrder( String.valueOf(courseCatalogSectionBean.section_sort));
                                courseSectionsInfo.setmCourseSectionsName(courseCatalogSectionBean.section_name);
                                courseSectionsInfo.setmVideoId(courseCatalogSectionBean.video_id);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                                if (courseCatalogSectionBean.Duration == null) {
                                    courseCatalogSectionBean.Duration = 0;
                                }
                                String hms = formatter.format(courseCatalogSectionBean.Duration * 1000);
                                courseSectionsInfo.setmCourseSectionsTime(hms);
                                courseSectionsInfo.setmCourseSectionsTime1(courseCatalogSectionBean.Duration * 1000);
                                courseSectionsInfo.setmCourseSectionsLearnProgress(courseCatalogSectionBean.sectionLearningRate * 100 + "%");
                                courseChaptersInfo.mCourseSectionsInfoList.add(courseSectionsInfo);
                            }
                        }
                    }
                    mCourseInfo.mCourseChaptersInfoList.add(courseChaptersInfo);
                }
                    if (mCurrentCatalogTab.equals("Record")) {
                        //修改body为录播
                    CourseCatalogRecordInit(mCourseInfo);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<CourseCatalogBeanNew> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程目录失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    //获取课程录播目录节目录-加载更多
    private void getSingleCourseCatalogSectionMore(String ChaptersId, View catalog_chapterview) {
        if (ChaptersId.equals("")) {
            return;
        }
        boolean isFind = false;
        int courseSectionsPage = 0;
        for (CourseChaptersInfo courseChaptersInfo : mCourseInfo.mCourseChaptersInfoList) {
            if (courseChaptersInfo == null) {
                continue;
            }
            if (ChaptersId.equals(courseChaptersInfo.getmCourseChaptersId())) {
                isFind = true;
                courseSectionsPage = courseChaptersInfo.getmCourseSectionsPage() + 1;
                courseChaptersInfo.setmCourseSectionsPage(courseSectionsPage);
                break;
            }
        }
        if (!isFind) {
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
        paramsMap.put("chapter_id", Integer.valueOf(ChaptersId));
        if (!mMainContext.mStuId.equals("")) {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        paramsMap.put("pageNum", courseSectionsPage);
        paramsMap.put("pageSize", mCourseCatalogCount);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<CourseCatalogSectionBeanNew> call = modelObservableInterface.findSingleCourseCatalogRecSection(body);
        call.enqueue(new Callback<CourseCatalogSectionBeanNew>() {
            @Override
            public void onResponse(Call<CourseCatalogSectionBeanNew> call, Response<CourseCatalogSectionBeanNew> response) {
                int code = response.code();
                if (code != 200) {
                    Log.e("TAG", "getSingleCourseDetails  onErrorCode: " + code);
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogSectionBeanNew courseCatalogSectionBeanNew = response.body();
                if (courseCatalogSectionBeanNew == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(courseCatalogSectionBeanNew.getErrorCode(), courseCatalogSectionBeanNew.getErrorMsg())) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                CourseCatalogSectionBeanNew.CourseCatalogSectionData courseCatalogSectionData = courseCatalogSectionBeanNew.getData();
                if (courseCatalogSectionData == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (courseCatalogSectionData.sectionList == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (courseCatalogSectionData.sectionList.total == null || courseCatalogSectionData.sectionList.list == null) {
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                for (CourseChaptersInfo courseChaptersInfo : mCourseInfo.mCourseChaptersInfoList) {
                    if (courseChaptersInfo == null) {
                        continue;
                    }
                    for (CourseCatalogSectionBeanNew.CourseCatalogSectionBean courseCatalogSectionBean : courseCatalogSectionData.sectionList.list) {
                        if (courseCatalogSectionBean == null) {
                            continue;
                        }
                        CourseSectionsInfo courseSectionsInfo = new CourseSectionsInfo();
                        courseSectionsInfo.setmCourseSectionsId(String.valueOf(courseCatalogSectionBean.section_id));
                        courseSectionsInfo.setmCourseSectionsOrder(String.valueOf(courseCatalogSectionBean.section_sort));
                        courseSectionsInfo.setmCourseSectionsName(courseCatalogSectionBean.section_name);
                        courseSectionsInfo.setmVideoId(courseCatalogSectionBean.video_id);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                        if (courseCatalogSectionBean.Duration == null) {
                            courseCatalogSectionBean.Duration = 0;
                        }
                        String hms = formatter.format(courseCatalogSectionBean.Duration * 1000);
                        courseSectionsInfo.setmCourseSectionsTime(hms);
                        courseSectionsInfo.setmCourseSectionsTime1(courseCatalogSectionBean.Duration * 1000);
                        courseSectionsInfo.setmCourseSectionsLearnProgress(courseCatalogSectionBean.sectionLearningRate * 100 + "%");
                        courseChaptersInfo.mCourseSectionsInfoList.add(courseSectionsInfo);
                    }
                }
                if (mCurrentCatalogTab.equals("Record")) {
                    //修改body为录播
                    LinearLayout course_catalog_label_content = catalog_chapterview.findViewById(R.id.course_catalog_label_content);
                    course_catalog_label_content.removeAllViews();
                    CourseCatalogRecordSectionsInit(course_catalog_label_content, ChaptersId);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<CourseCatalogSectionBeanNew> call, Throwable t) {
                Log.e("TAG", "onError: " + t.getMessage() + "");
                Toast.makeText(mMainContext, "获取课程目录失败", Toast.LENGTH_LONG).show();
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }

    public static class MyAdapter extends BaseAdapter {
        protected List<materialsBean.materialsBeanData> mList;
        protected Context mContext;
        LayoutInflater mInflater;

        public MyAdapter(Context context, List<materialsBean.materialsBeanData> list) {
            this.mList = list;
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
                //视图操作
            View itemview = null;
            if (convertView == null) {
                //初始化布局
                itemview = mInflater.inflate(R.layout.item_materials, parent, false);
            } else {
                //复用convertView
                itemview = convertView;
            }
            TextView title = (TextView) itemview.findViewById(R.id.text_name);
            TextView ttt = (TextView) itemview.findViewById(R.id.text_id);
                 //绑定数据
            materialsBean.materialsBeanData bean = mList.get(position);
            title.setText(bean.c_recourses_name);
            title.setHint(bean.recourses_address);
            ttt.setText(""+bean.c_recourses_id);
            return itemview;

        }
    }


    static class materialsBean {
        private int code;
        private List<materialsBeanData> data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<materialsBeanData> getData() {
            return data;
        }

        public void setData(List<materialsBeanData> data) {
            this.data = data;
        }

        class materialsBeanData {
            private int c_recourses_id;
            private String c_recourses_name;
            private String recourses_address;

            public int getC_recourses_id() {
                return c_recourses_id;
            }

            public void setC_recourses_id(int c_recourses_id) {
                this.c_recourses_id = c_recourses_id;
            }

            public String getC_recourses_name() {
                return c_recourses_name;
            }

            public void setC_recourses_name(String c_recourses_name) {
                this.c_recourses_name = c_recourses_name;
            }

            public String getRecourses_address() {
                return recourses_address;
            }

            public void setRecourses_address(String recourses_address) {
                this.recourses_address = recourses_address;
            }
        }

        @Override
        public String toString() {
            return "materialsBean{" +
                    "code=" + code +
                    ", data=" + data +
                    '}';
        }

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

        public static class CourseCatalogDataBean {
            private CourseCatalogRecordDataBean videoCatalog;
            private List<CourseCatalogLiveDataBean> liveCatalog;
        }

        public static class CourseCatalogRecordDataBean {
            private List<CourseCatalogTestRecordDataBean> testList;
            private List<CourseCatalogChapterRecordDataBean> chapterList;
            private List<CourseCatalogSectionRecordDataBean> sectionList;
        }

        public static class CourseCatalogLiveDataBean {
            private String ct_name;
            private int course_times_id;
            private String begin_class_date;
            private String end_time_datess;
            private int liveStatus;
        }

        public static class CourseCatalogTestRecordDataBean {
            private int test_sort;
            private int chapter_id;
            private String test_name;
            private int test_id;
        }

        public static class CourseCatalogChapterRecordDataBean {
            private int chapter_sort;
            private int chapter_id;
            private String chapter_name;
        }

        public static class CourseCatalogSectionRecordDataBean {
            private int section_id;
            private int Duration;
            private String section_name;
            private int chapter_id;
            private int section_sort;
            private String sectionLearningRate;
            private String video_id;
        }
    }

    static class CourseCatalogBeanNew {
        private CourseCatalogDataBeanNew data;
        private int code;
        private String msg;

        public CourseCatalogDataBeanNew getData() {
            return data;
        }

        public void setData(CourseCatalogDataBeanNew data) {
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

        public static class CourseCatalogDataBeanNew {
            private Integer sectionNUM;
            private CourseCatalogChapter chapterList;
        }

        public static class CourseCatalogChapter {
            private Integer total;
            private List<CourseCatalogChapterData> list;

        }

        public static class CourseCatalogChapterData {
            private Integer chapter_sort;
            private String chapter_name;
            private Integer chapter_id;
            private CourseCatalogSection sectionList;
        }

        public static class CourseCatalogSection {
            private Integer total;
            private List<CourseCatalogSectionBean> list;
        }

        public static class CourseCatalogSectionBean {
            private float sectionLearningRate;
            private Integer section_id;
            private String resourse_size;
            private String section_name;
            private Integer Duration;
            private Integer chapter_id;
            private Integer section_sort;
            private String video_id;
        }
    }

    static class CourseCatalogSectionBeanNew {
        private CourseCatalogSectionData data;
        private int code;
        private String msg;

        public CourseCatalogSectionData getData() {
            return data;
        }

        public void setData(CourseCatalogSectionData data) {
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

        public static class CourseCatalogSectionData {
            private CourseCatalogSection sectionList;
        }

        public static class CourseCatalogSection {
            private Integer total;
            private List<CourseCatalogSectionBean> list;

        }

        public static class CourseCatalogSectionBean {
            private float sectionLearningRate;
            private Integer section_id;
            private String resourse_size;
            private String section_name;
            private Integer Duration;
            private Integer chapter_id;
            private Integer section_sort;
            private String video_id;
        }
    }

    static class CourseCatalogLiveBeanNew {
        private CourseCatalogLiveData data;
        private int code;
        private String msg;

        public CourseCatalogLiveData getData() {
            return data;
        }

        public void setData(CourseCatalogLiveData data) {
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

        public static class CourseCatalogLiveData {
            private Integer total;
            private List<CourseCatalogLiveDataList> list;
        }

        public static class CourseCatalogLiveDataList {
            private String ct_name;
            private Integer course_times_id;
            private String end_time_datess;
            private String begin_class_date;
            private Integer liveStatus;
        }
    }
}
