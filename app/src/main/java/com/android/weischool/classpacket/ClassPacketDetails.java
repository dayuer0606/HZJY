package com.android.weischool.classpacket;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.weischool.ControllerCustomRoundAngleImageView;
import com.android.weischool.ControllerHorizontalProgressBar;
import com.android.weischool.HeaderInterceptor;
import com.android.weischool.LoadingDialog;
import com.android.weischool.MainActivity;
import com.android.weischool.ModelCourseCover;
import com.android.weischool.ModelExpandView;
import com.android.weischool.ModelHtmlUtils;
import com.android.weischool.ModelObservableInterface;
import com.android.weischool.ModelOrderDetailsInterface;
import com.android.weischool.R;
import com.android.weischool.info.StageCourseInfo;
import com.android.weischool.info.TeacherInfo;
import com.android.weischool.info.CourseInfo;
import com.android.weischool.info.CoursePacketInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.agora.rtc.internal.AudioRoutingController.TAG;
/**
 * Created by dayuer on 19/7/2.
 * ???????????????
 */
public class ClassPacketDetails implements View.OnClickListener, ModelOrderDetailsInterface {
    private View ClassPacket, mListView, mDetailsView;
    private MainActivity mMainContext = null;
    private ClassPacketDetailsOnClickListener mClassPacketDetailsOnClickListener = null;
    private int width = 720;
    private String mCurrentTab = "Details";
    private int lastTabIndex = 1;
    private boolean mIsCollect = false;
    private CoursePacketInfo mCoursePacketInfo;

    public void ClassPacketDetailsOnClickListenerSet(ClassPacketDetailsOnClickListener ClassPacketDetailsOnClickListener) {
        mClassPacketDetailsOnClickListener = ClassPacketDetailsOnClickListener;
    }

    public View ClassPacketDetails(Context context, CoursePacketInfo coursePacketInfo) {
        mMainContext = (MainActivity) context;
        if (coursePacketInfo == null) {
            return null;
        }
        //??????????????????????????????????????????
        mCoursePacketInfo = new CoursePacketInfo(coursePacketInfo);

        DisplayMetrics dm = context.getResources().getDisplayMetrics(); //?????????????????????
        width = dm.widthPixels;
        if (ClassPacket == null) {
            ClassPacket = LayoutInflater.from(context).inflate(R.layout.classpacket_layout, null);
            ClassPacket.setOnClickListener(v -> {
                if (mClassPacketDetailsOnClickListener == null || ClassPacket == null) {
                    return;
                }
                mClassPacketDetailsOnClickListener.OnClickListener(v);
                //?????????????????????????????????
                CoursePacketDetailsShow();
            });
        }
        mListView = LayoutInflater.from(context).inflate(R.layout.classpacketlist_layout, null);
        if (mDetailsView == null) {
            mDetailsView = LayoutInflater.from(context).inflate(R.layout.classpacketdetails_layout, null);
            TextView coursepacket_details_label = mDetailsView.findViewById(R.id.coursepacket_details_label);
            TextView coursepacket_details_label1 = mDetailsView.findViewById(R.id.coursepacket_details_label1);
            TextView coursepacket_coursestage_label = mDetailsView.findViewById(R.id.coursepacket_coursestage_label);
            TextView coursepacket_coursestage_label1 = mDetailsView.findViewById(R.id.coursepacket_coursestage_label1);
            TextView coursepacket_teachers_label1 = mDetailsView.findViewById(R.id.coursepacket_teachers_label1);
            TextView coursepacket_teachers_label = mDetailsView.findViewById(R.id.coursepacket_teachers_label);
            Button coursepacket_details_buy_button = mDetailsView.findViewById(R.id.coursepacket_details_buy_button);
            coursepacket_details_buy_button.setOnClickListener(this);
            LinearLayout coursepacket_details_bottomlayout_collect = mDetailsView.findViewById(R.id.coursepacket_details_bottomlayout_collect);
            coursepacket_details_bottomlayout_collect.setOnClickListener(this);
            coursepacket_details_label.setOnClickListener(this);
            coursepacket_details_label1.setOnClickListener(this);
            coursepacket_coursestage_label.setOnClickListener(this);
            coursepacket_coursestage_label1.setOnClickListener(this);
            coursepacket_teachers_label1.setOnClickListener(this);
            coursepacket_teachers_label.setOnClickListener(this);
            AppBarLayout mAppBarLayout = mDetailsView.findViewById(R.id.appbar);
            FrameLayout mFLayout = mDetailsView.findViewById(R.id.fl_layout);
            mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
                LinearLayout fl_layout_title_all = mDetailsView.findViewById(R.id.fl_layout_title_all);
                if (verticalOffset < -fl_layout_title_all.getY()) {
                    Toolbar.LayoutParams fl = (Toolbar.LayoutParams) mFLayout.getLayoutParams();
                    fl.height = FrameLayout.LayoutParams.MATCH_PARENT;
                    mFLayout.setLayoutParams(fl);
                    mFLayout.setAlpha(percent);
                } else {
                    Toolbar.LayoutParams fl = (Toolbar.LayoutParams) mFLayout.getLayoutParams();
                    fl.height = 0;
                    mFLayout.setLayoutParams(fl);
                    mFLayout.setAlpha(0);
                }
                //??????????????????????????????????????????
                LinearLayout coursepacket_label = mDetailsView.findViewById(R.id.coursepacket_label);
                LinearLayout coursepacket_label1 = mDetailsView.findViewById(R.id.coursepacket_label1);
                ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
                ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
                if (verticalOffset <= -coursepacket_label1.getY() + coursepacket_label.getHeight() + coursepacket_label.getY()) {
                    coursepacket_label.setAlpha(percent);
                    coursepacket_label1.setAlpha(0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imgv_cursor.setBackground(mMainContext.getDrawable(R.drawable.image_cusor));
                        imgv_cursor1.setBackground(mMainContext.getDrawable(R.drawable.image_cusor_white));
                    }
                } else {
                    coursepacket_label.setAlpha(0);
                    coursepacket_label1.setAlpha(1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imgv_cursor.setBackground(mMainContext.getDrawable(R.drawable.image_cusor_white));
                        imgv_cursor1.setBackground(mMainContext.getDrawable(R.drawable.image_cusor));
                    }
                }
            });
        }
        HideAllLayout();
        CoursePacketListInit(coursePacketInfo);
        RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
        coursepacket_main.addView(mListView);
        //        ???????????????????????????
        getDataPacketDetails();
        return ClassPacket;
    }
    //???????????????
    public void CoursePacketListInit(CoursePacketInfo coursePacketInfo) {
        ControllerCustomRoundAngleImageView imageView = mListView.findViewById(R.id.coursepacketcover);
        imageView.setImageDrawable(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails));//????????????url?????????????????????
        if (coursePacketInfo.mCoursePacketCover != null) {
            Glide.with(mMainContext).load(coursePacketInfo.mCoursePacketCover).listener(new RequestListener<Drawable>() {
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
            }).error(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails)).into(imageView);
        }
        TextView coursePacketNameTextView = mListView.findViewById(R.id.coursepacketName);
        if (coursePacketInfo.mCoursePacketName != null) {
            coursePacketNameTextView.setText(coursePacketInfo.mCoursePacketName);
        }
        TextView coursepacketcontentTextView = mListView.findViewById(R.id.coursepacketcontent);
        String content = "";
        if (coursePacketInfo.mCoursePacketLearnPersonNum != null) {
            content = coursePacketInfo.mCoursePacketLearnPersonNum + "???????????????";
        }
        coursepacketcontentTextView.setText(content);
        TextView coursepacketpriceTextView = mListView.findViewById(R.id.coursepacketprice);
        if (coursePacketInfo.mCoursePacketPrice != null) {
            if (!coursePacketInfo.mCoursePacketPrice.equals("??????")) {
                coursepacketpriceTextView.setTextColor(Color.RED);
                coursepacketpriceTextView.setText("??" + coursePacketInfo.mCoursePacketPrice);
            } else {
                coursepacketpriceTextView.setText(coursePacketInfo.mCoursePacketPrice);
            }
        }
        TextView coursepacketpriceoldTextView = mListView.findViewById(R.id.coursepacketpriceOld);
        //???????????????
        coursepacketpriceoldTextView.setPaintFlags(coursepacketpriceoldTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (coursePacketInfo.mCoursePacketPriceOld != null) {
            if (!coursePacketInfo.mCoursePacketPriceOld.equals("??????")) {
                coursepacketpriceoldTextView.setText("??" + coursePacketInfo.mCoursePacketPriceOld);
            }
        }
    }
    //??????????????????
    public void CoursePacketDetailsShow() {
        if (ClassPacket == null) {
            return;
        }
        HideAllLayout();
        RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
        coursepacket_main.addView(mDetailsView);
        //????????????????????????  ??????1
        TextView coursepacket_details_label = mDetailsView.findViewById(R.id.coursepacket_details_label);
        // ????????????2
        TextView coursepacket_details_label1 = mDetailsView.findViewById(R.id.coursepacket_details_label1);
        coursepacket_details_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        coursepacket_details_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        //????????????
        TextView coursepacket_coursestage_label = mDetailsView.findViewById(R.id.coursepacket_coursestage_label);
        //????????????2
        TextView coursepacket_coursestage_label1 = mDetailsView.findViewById(R.id.coursepacket_coursestage_label1);
        coursepacket_coursestage_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        coursepacket_coursestage_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
        //??????1
        TextView coursepacket_teachers_label1 = mDetailsView.findViewById(R.id.coursepacket_teachers_label1);
        //??????1
        TextView coursepacket_teachers_label = mDetailsView.findViewById(R.id.coursepacket_teachers_label);
        coursepacket_teachers_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        coursepacket_teachers_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
        //??????body??????????????????
        LinearLayout coursepacket_details_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_details_label_content_layout);
        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_details_label_content_layout.getLayoutParams();
        LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        coursepacket_details_label_content_layout.setLayoutParams(LP);
        coursepacket_details_label_content_layout.setVisibility(View.VISIBLE);
        LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
        LP = (LinearLayout.LayoutParams) coursepacket_coursestage_label_content_layout.getLayoutParams();
        LP.height = 0;
        coursepacket_coursestage_label_content_layout.setLayoutParams(LP);
        coursepacket_coursestage_label_content_layout.setVisibility(View.INVISIBLE);
        getDataPacketStageofcourse();
    }

    public void HideAllLayout() {
        RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
        coursepacket_main.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coursepacket_details_label:
            case R.id.coursepacket_details_label1: {
                //????????????1
                TextView coursepacket_details_label = mDetailsView.findViewById(R.id.coursepacket_details_label);
                //????????????2
                TextView coursepacket_details_label1 = mDetailsView.findViewById(R.id.coursepacket_details_label1);
                coursepacket_details_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                coursepacket_details_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                //????????????1
                TextView coursepacket_coursestage_label = mDetailsView.findViewById(R.id.coursepacket_coursestage_label);
                //????????????2
                TextView coursepacket_coursestage_label1 = mDetailsView.findViewById(R.id.coursepacket_coursestage_label1);
                coursepacket_coursestage_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_coursestage_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                //???????????????1
                TextView coursepacket_teachers_label1 = mDetailsView.findViewById(R.id.coursepacket_teachers_label1);
                //???????????????2
                TextView coursepacket_teachers_label = mDetailsView.findViewById(R.id.coursepacket_teachers_label);
                coursepacket_teachers_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_teachers_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                //??????body??????????????????
                LinearLayout coursepacket_details_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_details_label_content_layout);
                LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_details_label_content_layout.getLayoutParams();
                LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                coursepacket_details_label_content_layout.setLayoutParams(LP);
                coursepacket_details_label_content_layout.setVisibility(View.VISIBLE);
                LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_coursestage_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_coursestage_label_content_layout.setLayoutParams(LP);
                coursepacket_coursestage_label_content_layout.setVisibility(View.INVISIBLE);
                LinearLayout coursepacket_teachers_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_teachers_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_teachers_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_teachers_label_content_layout.setLayoutParams(LP);
                coursepacket_teachers_label_content_layout.setVisibility(View.INVISIBLE);
                if (!mCurrentTab.equals("Details")) {
                    ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
                    ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
                    Animation animation = new TranslateAnimation((lastTabIndex - 1) * width / 3, 0, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    imgv_cursor.startAnimation(animation);
                    imgv_cursor1.startAnimation(animation);
                }
                lastTabIndex = 1;
                mCurrentTab = "Details";
                break;
            }
            case R.id.coursepacket_coursestage_label:
            case R.id.coursepacket_coursestage_label1: {
                //        ???????????????????????????
                getDataPacketStageofcourse();
                //????????????1
                TextView coursepacket_details_label = mDetailsView.findViewById(R.id.coursepacket_details_label);
                //????????????2
                TextView coursepacket_details_label1 = mDetailsView.findViewById(R.id.coursepacket_details_label1);
                coursepacket_details_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_details_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                //????????????1
                TextView coursepacket_coursestage_label = mDetailsView.findViewById(R.id.coursepacket_coursestage_label);
                //????????????2
                TextView coursepacket_coursestage_label1 = mDetailsView.findViewById(R.id.coursepacket_coursestage_label1);
                coursepacket_coursestage_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                coursepacket_coursestage_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                //??????1
                TextView coursepacket_teachers_label1 = mDetailsView.findViewById(R.id.coursepacket_teachers_label1);
                //??????2
                TextView coursepacket_teachers_label = mDetailsView.findViewById(R.id.coursepacket_teachers_label);
                coursepacket_teachers_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_teachers_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                //??????body???????????????
                LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
                LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_coursestage_label_content_layout.getLayoutParams();
                LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                coursepacket_coursestage_label_content_layout.setLayoutParams(LP);
                coursepacket_coursestage_label_content_layout.setVisibility(View.VISIBLE);
                LinearLayout coursepacket_teachers_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_teachers_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_teachers_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_teachers_label_content_layout.setLayoutParams(LP);
                coursepacket_teachers_label_content_layout.setVisibility(View.INVISIBLE);
                LinearLayout coursepacket_details_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_details_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_details_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_details_label_content_layout.setLayoutParams(LP);
                coursepacket_details_label_content_layout.setVisibility(View.INVISIBLE);
                if (!mCurrentTab.equals("StageCourse")) {
                    ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
                    ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
                    Animation animation = new TranslateAnimation((lastTabIndex - 1) * width / 3, width / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    imgv_cursor.startAnimation(animation);
                    imgv_cursor1.startAnimation(animation);
                }
                lastTabIndex = 2;
                mCurrentTab = "StageCourse";
                break;
            }
            case R.id.coursepacket_teachers_label:
            case R.id.coursepacket_teachers_label1: {
                //??????
                //        ?????????????????????
                getDataPacketageTeacher();
                TextView coursepacket_details_label = mDetailsView.findViewById(R.id.coursepacket_details_label);
                TextView coursepacket_details_label1 = mDetailsView.findViewById(R.id.coursepacket_details_label1);
                coursepacket_details_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_details_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                TextView coursepacket_coursestage_label = mDetailsView.findViewById(R.id.coursepacket_coursestage_label);
                TextView coursepacket_coursestage_label1 = mDetailsView.findViewById(R.id.coursepacket_coursestage_label1);
                coursepacket_coursestage_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                coursepacket_coursestage_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize16));
                TextView coursepacket_teachers_label1 = mDetailsView.findViewById(R.id.coursepacket_teachers_label1);
                TextView coursepacket_teachers_label = mDetailsView.findViewById(R.id.coursepacket_teachers_label);
                coursepacket_teachers_label1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                coursepacket_teachers_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDetailsView.getResources().getDimensionPixelSize(R.dimen.textsize18));
                //??????body?????????
                LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
                LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_coursestage_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_coursestage_label_content_layout.setLayoutParams(LP);
                coursepacket_coursestage_label_content_layout.setVisibility(View.INVISIBLE);
                LinearLayout coursepacket_details_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_details_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_details_label_content_layout.getLayoutParams();
                LP.height = 0;
                coursepacket_details_label_content_layout.setLayoutParams(LP);
                coursepacket_details_label_content_layout.setVisibility(View.INVISIBLE);
                LinearLayout coursepacket_teachers_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_teachers_label_content_layout);
                LP = (LinearLayout.LayoutParams) coursepacket_teachers_label_content_layout.getLayoutParams();
                LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                coursepacket_teachers_label_content_layout.setLayoutParams(LP);
                coursepacket_teachers_label_content_layout.setVisibility(View.VISIBLE);
                if (!mCurrentTab.equals("Teachers")) {
                    ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
                    ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
                    Animation animation = new TranslateAnimation((lastTabIndex - 1) * width / 3, width * 2 / 3, 0, 0);
                    animation.setFillAfter(true);// True:??????????????????????????????
                    animation.setDuration(200);
                    imgv_cursor.startAnimation(animation);
                    imgv_cursor1.startAnimation(animation);
                }
                lastTabIndex = 3;
                mCurrentTab = "Teachers";

                break;
            }
            case R.id.coursepacket_details_bottomlayout_collect: {
                if (mMainContext.mStuId.equals("")){
                    //??????????????????
                    mMainContext.Page_LogIn();
                }
                //????????????????????????   ?????????????????????
                getDataPacketageCollection();
                break;
            }
            case R.id.coursepacket_details_buy_button: {
                if (!mCoursePacketInfo.mCoursePacketIsHave.equals("1")){
                    Toast.makeText(mMainContext,"???????????????????????????????????????",Toast.LENGTH_SHORT).show();
                }
//                HideAllLayout();
//                RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
//                View view = mMainContext.Page_OrderDetails(this,null,mCoursePacketInfo,null);
//                coursepacket_main.addView(view);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onRecive() {
        CoursePacketDetailsShow();
    }

    public interface ClassPacketDetailsOnClickListener {
        void OnClickListener(View view);
    }

    //?????????????????????
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void CoursePacketDetailsInit(CoursePacketInfo coursePacketInfo,int collection_status) {
        if (mDetailsView == null) {
            return;
        }
        ImageView coursepacket_details_Cover = mDetailsView.findViewById(R.id.coursepacket_details_Cover);
        //???????????????
        if (coursePacketInfo.mCoursePacketCover != null) {
            Glide.with(mMainContext).load(coursePacketInfo.mCoursePacketCover).listener(new RequestListener<Drawable>() {
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
            }).error(mMainContext.getResources().getDrawable(R.drawable.classpacketdetails)).into(coursepacket_details_Cover);
        }
        //???????????????-???????????????
        TextView coursepacket_details_Name = mDetailsView.findViewById(R.id.coursepacket_details_Name);
        if (coursePacketInfo.mCoursePacketName != null) {
            coursepacket_details_Name.setText(coursePacketInfo.mCoursePacketName);
        }
        //???????????????-???????????????
        TextView coursepacket_details_content0 = mDetailsView.findViewById(R.id.coursepacket_details_content0);
        TextView coursepacket_details_content2 = mDetailsView.findViewById(R.id.coursepacket_details_content2);
        if (coursePacketInfo.mCoursePacketStageNum != null) {
            coursepacket_details_content0.setText("??????" + coursePacketInfo.mCoursePacketStageNum);
        }
        if (coursePacketInfo.mCoursePacketCourseNum != null) {
            coursepacket_details_content2.setText("?????????:" + coursePacketInfo.mCoursePacketCourseNum);
        }

        if (coursePacketInfo.mCoursePacketLearnPersonNum != null) {
            TextView coursepacket_details_learnpersonnum = mDetailsView.findViewById(R.id.coursepacket_details_learnpersonnum);
            coursepacket_details_learnpersonnum.setText("????????????:" + coursePacketInfo.mCoursePacketLearnPersonNum);
        }
        //???????????????
        TextView coursepacket_details_price = mDetailsView.findViewById(R.id.coursepacket_details_price);
        if (coursePacketInfo.mCoursePacketPrice != null) {
            if (!coursePacketInfo.mCoursePacketPrice.equals("??????")) {
                coursepacket_details_price.setTextColor(Color.RED);
                coursepacket_details_price.setText("??" + coursePacketInfo.mCoursePacketPrice);
            } else {
                coursepacket_details_price.setText(coursePacketInfo.mCoursePacketPrice);
            }
        }
        //???????????????
        TextView coursepacket_details_priceOld = mDetailsView.findViewById(R.id.coursepacket_details_priceOld);
        //???????????????
        coursepacket_details_priceOld.setPaintFlags(coursepacket_details_priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (coursePacketInfo.mCoursePacketPriceOld != null) {
            if (!coursePacketInfo.mCoursePacketPriceOld.equals("??????")) {
                coursepacket_details_priceOld.setText("??" + coursePacketInfo.mCoursePacketPriceOld);
            }
        }
        //???????????????
        TextView fl_layout_title = mDetailsView.findViewById(R.id.fl_layout_title);
        fl_layout_title.setHint(coursePacketInfo.mCoursePacketName);
        //???????????????
        TextView coursepacket_details_briefintroductioncontent = mDetailsView.findViewById(R.id.coursepacket_details_briefintroductioncontent);
        coursepacket_details_briefintroductioncontent.setText(coursePacketInfo.mCoursePacketMessage);
        //?????????????????????????????????????????????????????????
        ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
        Matrix matrix = new Matrix();
        matrix.postTranslate(width / 3, 0);
        imgv_cursor.setImageMatrix(matrix);// ????????????????????????
        ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
        Matrix matrix1 = new Matrix();
        matrix1.postTranslate(width / 3, 0);
        imgv_cursor1.setImageMatrix(matrix1);// ????????????????????????
        //????????????????????????  HTML??????
        if (coursePacketInfo.mCoursePacketDetails != null) {
            TextView coursepacket_details_label_content = mDetailsView.findViewById(R.id.coursepacket_details_label_content);
            new ModelHtmlUtils(mMainContext, coursepacket_details_label_content).setHtmlWithPic(coursePacketInfo.mCoursePacketDetails);
        }
        //?????????????????????
        if (coursePacketInfo.mCoursePacketIsHave.equals("1")) {
            Button coursepacket_details_buy_button = mDetailsView.findViewById(R.id.coursepacket_details_buy_button);
            coursepacket_details_buy_button.setBackground(mDetailsView.getResources().getDrawable(R.drawable.button_style4));
            coursepacket_details_buy_button.setText("?????????");
        }
        //??????????????????
        if (coursePacketInfo.mInvalid_date_date != null) {
            TextView coursepacket_details_date = mDetailsView.findViewById(R.id.coursepacket_details_date);
            Date date = null;
            String invalid_date_date = "";
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                date = df.parse(coursePacketInfo.mInvalid_date_date);
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
            coursepacket_details_date.setText("???????????????" + invalid_date_date);
        } else if (coursePacketInfo.mEffictive_date != null) {
            TextView coursepacket_details_date = mDetailsView.findViewById(R.id.coursepacket_details_date);
            coursepacket_details_date.setText("???????????????" + coursePacketInfo.mEffictive_date);
        } else {
            TextView coursepacket_details_date = mDetailsView.findViewById(R.id.coursepacket_details_date);
            coursepacket_details_date.setText("");
        }
        //????????????
        ImageView coursepacket_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.coursepacket_details_bottomlayout_collectImage);
        TextView coursepacket_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.coursepacket_details_bottomlayout_collectText);
        if (collection_status == 2) {
            mIsCollect = false;   //????????????
            coursepacket_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
            coursepacket_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
        } else if (collection_status == 1){
            mIsCollect = true;    //????????????
            coursepacket_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
            coursepacket_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
        }
    }

    //??????????????????????????????
    public void CoursePacketStageCourseInit(CoursePacketInfo coursePacketInfo) {
        if (mCurrentTab.equals("StageCourse")){
            //??????body???????????????
            LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
            LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams) coursepacket_coursestage_label_content_layout.getLayoutParams();
            LP.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            coursepacket_coursestage_label_content_layout.setLayoutParams(LP);
            coursepacket_coursestage_label_content_layout.setVisibility(View.VISIBLE);
            LinearLayout coursepacket_teachers_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_teachers_label_content_layout);
            LP = (LinearLayout.LayoutParams) coursepacket_teachers_label_content_layout.getLayoutParams();
            LP.height = 0;
            coursepacket_teachers_label_content_layout.setLayoutParams(LP);
            coursepacket_teachers_label_content_layout.setVisibility(View.INVISIBLE);
            LinearLayout coursepacket_details_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_details_label_content_layout);
            LP = (LinearLayout.LayoutParams) coursepacket_details_label_content_layout.getLayoutParams();
            LP.height = 0;
            coursepacket_details_label_content_layout.setLayoutParams(LP);
            coursepacket_details_label_content_layout.setVisibility(View.INVISIBLE);
        }
        LinearLayout coursepacket_coursestage_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_coursestage_label_content_layout);
        //?????????????????????????????????????????????
        coursepacket_coursestage_label_content_layout.removeAllViews();
        if (coursePacketInfo.mStageCourseInfoList == null) {
            return;
        }
        for (int i = 0; i < coursePacketInfo.mStageCourseInfoList.size(); i++) {
            StageCourseInfo stageCourseInfo = coursePacketInfo.mStageCourseInfoList.get(i);
            if (stageCourseInfo == null) {
                continue;
            }
            if (stageCourseInfo.mStageCourseName.equals("")) {
                if (stageCourseInfo.mCourseInfoList == null) {
                    continue;
                }
                for (int num = 0; num < stageCourseInfo.mCourseInfoList.size(); num++) {
                    CourseInfo courseInfo = stageCourseInfo.mCourseInfoList.get(num);
                    if (courseInfo == null) {
                        continue;
                    }
                    ModelCourseCover modelCourseCover = new ModelCourseCover();
                    View modelCourseView = modelCourseCover.ModelCourseCover(mMainContext, courseInfo);
                    coursepacket_coursestage_label_content_layout.addView(modelCourseView);
                    modelCourseView.setOnClickListener(v->{ //??????????????????
                        ModelCourseCover modelCourseCover1 = new ModelCourseCover();
                        View modelCourseView1 = modelCourseCover1.ModelCourseCover(mMainContext,courseInfo);
                        modelCourseCover1.CourseDetailsShow();
                        HideAllLayout();
                        RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
                        coursepacket_main.addView(modelCourseView1);
                        mMainContext.onClickCourseDetails();
                    });
                }
                continue;
            }
            //???????????????????????????????????????????????????????????????
            View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelstagecourse, null);
            LinearLayout coursepacket_coursestage_label_namelayout = view.findViewById(R.id.coursepacket_coursestage_label_namelayout);
            TextView coursepacket_coursestage_label_name = view.findViewById(R.id.coursepacket_coursestage_label_name);
            coursepacket_coursestage_label_name.setText(stageCourseInfo.mStageCourseName);
            TextView coursepacket_coursestage_bef = view.findViewById(R.id.coursepacket_coursestage_bef);
            if (stageCourseInfo.mStageCourseDescribe != null) {
                coursepacket_coursestage_bef.setText(stageCourseInfo.mStageCourseDescribe);
            }
            if (stageCourseInfo.mStageCourseProgrProgress != null) {
                ControllerHorizontalProgressBar progressBarLarge = view.findViewById(R.id.progressBarLarge);
                progressBarLarge.updateProgress(Integer.parseInt(stageCourseInfo.mStageCourseProgrProgress));
                TextView progressBarLargenum = view.findViewById(R.id.progressBarLargenum);
                progressBarLargenum.setText(stageCourseInfo.mStageCourseProgrProgress + "%");
            }
            ModelExpandView mExpandView = view.findViewById(R.id.coursepacket_coursestage_label_expandView);
            ImageView coursepacket_coursestage_label_arrow_right = view.findViewById(R.id.coursepacket_coursestage_label_arrow_right);
            ImageView coursepacket_coursestage_label_arrow_down = view.findViewById(R.id.coursepacket_coursestage_label_arrow_down);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_right.getLayoutParams();
            ll.width = view.getResources().getDimensionPixelSize(R.dimen.dp6);
            coursepacket_coursestage_label_arrow_right.setLayoutParams(ll);
            ll = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_down.getLayoutParams();
            ll.width = 0;
            coursepacket_coursestage_label_arrow_down.setLayoutParams(ll);
            LinearLayout coursepacket_coursestage_label_card2_content = view.findViewById(R.id.coursepacket_coursestage_label_card2_content);
            coursepacket_coursestage_label_namelayout.setClickable(true);
            coursepacket_coursestage_label_namelayout.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                if (mExpandView.isExpand()) {
                    mExpandView.collapse();
                    //??????????????????
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) mExpandView.getLayoutParams();
                    rl.height = 0;
                    mExpandView.setLayoutParams(rl);
                    mExpandView.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_right.getLayoutParams();
                    ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp6);
                    coursepacket_coursestage_label_arrow_right.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_down.getLayoutParams();
                    ll1.width = 0;
                    coursepacket_coursestage_label_arrow_down.setLayoutParams(ll1);
                } else {
                    mExpandView.expand();
                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) mExpandView.getLayoutParams();
                    rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    mExpandView.setLayoutParams(rl);
                    mExpandView.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_right.getLayoutParams();
                    ll1.width = 0;
                    coursepacket_coursestage_label_arrow_right.setLayoutParams(ll1);
                    ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_down.getLayoutParams();
                    ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp10);
                    coursepacket_coursestage_label_arrow_down.setLayoutParams(ll1);
                }
            });
            coursepacket_coursestage_label_content_layout.addView(view);
            if (stageCourseInfo.mCourseInfoList == null) {
                continue;
            }
            if (coursepacket_coursestage_label_card2_content == null) {
                continue;
            }
            TextView coursepacket_coursestage_label_coursecount = view.findViewById(R.id.coursepacket_coursestage_label_coursecount);
            coursepacket_coursestage_label_coursecount.setText("??????" + stageCourseInfo.mCourseInfoList.size() + "??????");
            coursepacket_coursestage_label_card2_content.removeAllViews();
            for (int num = 0; num < stageCourseInfo.mCourseInfoList.size(); num++) {
                CourseInfo courseInfo = stageCourseInfo.mCourseInfoList.get(num);
                if (courseInfo == null) {
                    continue;
                }
                View stagecourse = LayoutInflater.from(mMainContext).inflate(R.layout.modelstagecourse1, null);
                TextView stagecourselistmain_name = stagecourse.findViewById(R.id.stagecourselistmain_name);
                stagecourselistmain_name.setText(courseInfo.getmCourseName());
                TextView stagecourselistmain_price = stagecourse.findViewById(R.id.stagecourselistmain_price);
                if (courseInfo.getmCoursePrice().equals("??????")) {
                    TextView stagecourselistmain_priceLogo = stagecourse.findViewById(R.id.stagecourselistmain_priceLogo);
                    stagecourselistmain_priceLogo.setText("");
                    stagecourselistmain_price.setTextColor(stagecourse.getResources().getColor(R.color.collectdefaultcolor3));
                } else {
                    TextView stagecourselistmain_priceLogo = stagecourse.findViewById(R.id.stagecourselistmain_priceLogo);
                    stagecourselistmain_priceLogo.setText("??");
                    stagecourselistmain_price.setTextColor(stagecourse.getResources().getColor(R.color.holo_red_dark));
                }
                stagecourselistmain_price.setText(courseInfo.getmCoursePrice());
                coursepacket_coursestage_label_card2_content.addView(stagecourse);
                stagecourse.setOnClickListener(v->{ //??????????????????
                    ModelCourseCover modelCourseCover1 = new ModelCourseCover();
                    View modelCourseView1 = modelCourseCover1.ModelCourseCover(mMainContext,courseInfo);
                    modelCourseCover1.CourseDetailsShow();
                    HideAllLayout();
                    RelativeLayout coursepacket_main = ClassPacket.findViewById(R.id.coursepacket_main);
                    coursepacket_main.addView(modelCourseView1);
                    mMainContext.onClickCourseDetails();
                });
            }
            //??????????????????????????????
            mExpandView.expand();
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) mExpandView.getLayoutParams();
            rl.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            mExpandView.setLayoutParams(rl);
            mExpandView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_right.getLayoutParams();
            ll1.width = 0;
            coursepacket_coursestage_label_arrow_right.setLayoutParams(ll1);
            ll1 = (LinearLayout.LayoutParams) coursepacket_coursestage_label_arrow_down.getLayoutParams();
            ll1.width = view.getResources().getDimensionPixelSize(R.dimen.dp10);
            coursepacket_coursestage_label_arrow_down.setLayoutParams(ll1);
        }
        ImageView imgv_cursor = mDetailsView.findViewById(R.id.imgv_cursor);
        ImageView imgv_cursor1 = mDetailsView.findViewById(R.id.imgv_cursor1);
        int x = width / 6 - mDetailsView.getResources().getDimensionPixelSize(R.dimen.dp18) / 2;
        imgv_cursor.setX(x);
        imgv_cursor1.setX(x);
    }
    //????????????
    public void CoursePacketTeachersInit(CoursePacketInfo coursePacketInfo) {
        LinearLayout coursepacket_teachers_label_content_layout = mDetailsView.findViewById(R.id.coursepacket_teachers_label_content_layout);
        //???????????????????????????????????????
        coursepacket_teachers_label_content_layout.removeAllViews();
        if (coursePacketInfo.mTeacherInfoList == null) {
            return;
        }
        for (int i = 0; i < coursePacketInfo.mTeacherInfoList.size(); i++) {
            TeacherInfo teacherInfo = coursePacketInfo.mTeacherInfoList.get(i);
            if (teacherInfo == null) {
                continue;
            }
            if (teacherInfo.mTeacherName.equals("")) {
                continue;
            }
            //???????????????????????????????????????????????????
            View view = LayoutInflater.from(mMainContext).inflate(R.layout.modelteachers, null);
            ControllerCustomRoundAngleImageView teachers_headportrait = view.findViewById(R.id.teachers_headportrait);
            //????????????
            Glide.with(mMainContext).load(teacherInfo.mTeacherCover).listener(new RequestListener<Drawable>() {
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
            }).error(mMainContext.getResources().getDrawable(R.drawable.image_teachersdefault)).into(teachers_headportrait);
            //??????name
            TextView teachers_content_name = view.findViewById(R.id.teachers_content_name);
            teachers_content_name.setText(teacherInfo.mTeacherName);
            if (teacherInfo.mTeacherMessage != null) {
                //??????message
                TextView teachers_content_message = view.findViewById(R.id.teachers_content_message);
                teachers_content_message.setText(teacherInfo.mTeacherMessage);
            }
            coursepacket_teachers_label_content_layout.addView(view);
        }
    }
    //?????????????????????   ??????????????????
    public void getDataPacketageCollection(){
        if (mMainContext.mStuId.equals("")){
            if (mIsCollect) {
                Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        //course_package_id??????id    ???????????????id
        paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));    //??????id
        paramsMap.put("course_package_id", Integer.valueOf(mCoursePacketInfo.mCoursePacketId)); //???id
        if (mIsCollect) {
            paramsMap.put("collection_status", 2);   //?????????
        } else {
            paramsMap.put("collection_status", 1);   //?????????
        }

        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.queryStageofcourseCoursecollection(body)
                .enqueue(new Callback<collectionBean>() {
                    @Override
                    public void onResponse(Call<collectionBean> call, Response<collectionBean> response) {
                        collectionBean collectionBean = response.body();
                        if (collectionBean == null){
                            if (mIsCollect) {
                                Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
                            }
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(collectionBean.code,collectionBean.message)){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        int code = collectionBean.getCode();//?????????
                        if (code == 200){
                            //???????????????????????????
                            String message = collectionBean.getMessage();
                            if (message.equals("success")){
                                ImageView coursepacket_details_bottomlayout_collectImage = mDetailsView.findViewById(R.id.coursepacket_details_bottomlayout_collectImage);
                                TextView coursepacket_details_bottomlayout_collectText = mDetailsView.findViewById(R.id.coursepacket_details_bottomlayout_collectText);
                                if (mIsCollect) {
                                    mIsCollect = false;   //????????????
                                    Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
                                    coursepacket_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.collectdefaultcolor));
                                    coursepacket_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_disable));
                                } else {
                                    mIsCollect = true;    //????????????
                                    Toast.makeText(mMainContext, "?????????", Toast.LENGTH_SHORT).show();
                                    coursepacket_details_bottomlayout_collectImage.setImageDrawable(mDetailsView.getResources().getDrawable(R.drawable.button_collect_enable));
                                    coursepacket_details_bottomlayout_collectText.setTextColor(mDetailsView.getResources().getColor(R.color.holo_red_dark));
                                }
                            }else {
                                if (mIsCollect) {
                                    Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {
                            if (mIsCollect) {
                                Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onFailure(Call<collectionBean> call, Throwable t) {
                        if (mIsCollect) {
                            Toast.makeText(mMainContext, "??????????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mMainContext, "????????????", Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }
                });
    }
    //???????????????????????????
    public void getDataPacketageTeacher() {
        if (mCoursePacketInfo.mCoursePacketId.equals("")){
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        final Observable<CourseTeacherBean> data = modelObservableInterface.queryCoursePackageTeacher(Integer.valueOf(mCoursePacketInfo.mCoursePacketId));
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CourseTeacherBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourseTeacherBean value) {
                        if (value == null){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        //????????????????????????
                        int code = value.code;
                        if (!HeaderInterceptor.IsErrorCode(code,"")){
                            LoadingDialog.getInstance(mMainContext).dismiss();
                            return;
                        }
                        if (code == 200) {
                            List<CourseTeacherBean.DataBean> dataBeans = value.getData();
                            if (dataBeans == null) {
                                LoadingDialog.getInstance(mMainContext).dismiss();
                                return;
                            }
                            mCoursePacketInfo.mTeacherInfoList.clear();
                            for (int i = 0; i < dataBeans.size(); i++) {
                                CourseTeacherBean.DataBean dataBean = dataBeans.get(i);
                                if (dataBean == null){
                                    continue;
                                }
                                String head = dataBean.getHead();//????????????
                                String introduction = dataBean.getIntroduction();//??????
                                String true_name = dataBean.getTrue_name();//????????????
                                int user_id = dataBean.getUser_id();//??????id
                                //????????????   ??????????????????   teacherInfo
                                TeacherInfo teacherInfo = new TeacherInfo();
                                teacherInfo.mTeacherName = true_name;
                                teacherInfo.mTeacherMessage = introduction;
                                teacherInfo.mTeacherCover = head;
                                teacherInfo.mTeacherID = String.valueOf(user_id);
                                mCoursePacketInfo.mTeacherInfoList.add(teacherInfo);
                            }
                            CoursePacketTeachersInit(mCoursePacketInfo);
                        }
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", "onError: "+e.getMessage()+"" + "Http:" + "http://192.168.30.141:8080/app/homePage/queryHomePageInfo/");
                        LoadingDialog.getInstance(mMainContext).dismiss();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    //???????????????????????????????????????????????????????????????
    public void getDataPacketStageofcourse() {
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("course_package_id", mCoursePacketInfo.mCoursePacketId);
        String strEntity = gson.toJson(paramsMap);
        if (!mMainContext.mStuId.equals("")) {
            HashMap<String, Integer> paramsMap1 = new HashMap<>();
            paramsMap1.put("stu_id", Integer.valueOf(mMainContext.mStuId));
            String strEntity1 = gson.toJson(paramsMap1);
            strEntity1 = strEntity1.replace("{","");
            strEntity = strEntity.replace("}","," + strEntity1);
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<StageCourseListBean> stageCourseListBeanCall = modelObservableInterface.queryStageofcoursecurriculum(body);
        stageCourseListBeanCall.enqueue(new Callback<StageCourseListBean>() {
            @Override
            public void onResponse(Call<StageCourseListBean> call, Response<StageCourseListBean> response) {
                StageCourseListBean body1 = response.body();
                if (body1 != null) {
                    Log.i("", "onResponse: " + body1.toString());
                    int code = body1.getCode();
                    if (!HeaderInterceptor.IsErrorCode(code,"")){
                        LoadingDialog.getInstance(mMainContext).dismiss();
                        return;
                    }
                    if (code == 200) {
                        StageCourseListBean.DataBean data = body1.getData();
                        //for??????????????????
                        mCoursePacketInfo.mStageCourseInfoList.clear();
                        List<StageCourseListBean.DataBean.StageListInfoBean> stageListInfo = data.getStageListInfo();
                        for (int i = 0; i < stageListInfo.size(); i++) {
                            StageCourseListBean.DataBean.StageListInfoBean stageListInfoBean = stageListInfo.get(i);
                            int stageLearningProgress = stageListInfoBean.getStageLearningProgress();//????????????
                            int sort_of_stage = stageListInfoBean.getSort_of_stage();//??????
                            int stage_id = stageListInfoBean.getStage_id();//??????id
                            StageCourseInfo stageCourseInfo = new StageCourseInfo();
                            stageCourseInfo.mStageCourseOrder= String.valueOf(sort_of_stage);
                            stageCourseInfo.mStageCourseId= String.valueOf(stage_id);
                            stageCourseInfo.mStageCourseProgrProgress= String.valueOf(stageLearningProgress);
                            stageCourseInfo.mStageCourseName = stageListInfoBean.stage_name;
                            stageCourseInfo.mStageCourseDescribe = stageListInfoBean.stage_describe;
                            mCoursePacketInfo.mStageCourseInfoList.add(stageCourseInfo);
                        }
                        List<StageCourseListBean.DataBean.CourseListInfoBean> courseListInfo = data.getCourseListInfo();
                        for (int i = 0; i < courseListInfo.size(); i ++) {
                            StageCourseListBean.DataBean.CourseListInfoBean stageListInfoBean = courseListInfo.get(i);
                            int course_id = stageListInfoBean.getCourse_id();
                            String course_name = stageListInfoBean.getCourse_name();
                            float special_price = stageListInfoBean.getSpecial_price();
                            int price = stageListInfoBean.getPrice();
                            int stage_id = stageListInfoBean.getStage_id();
                            for (int num = 0; num < mCoursePacketInfo.mStageCourseInfoList.size(); num ++ ){
                                StageCourseInfo stageCourseInfo = mCoursePacketInfo.mStageCourseInfoList.get(num);
                                if (stageCourseInfo.mStageCourseId.equals(String.valueOf(stage_id))){

                                    CourseInfo courseInfo = new CourseInfo();
                                    courseInfo.setmCourseId(String.valueOf(course_id));
                                    courseInfo.setmCourseCover(stageListInfoBean.cover);
                                    courseInfo.setmCourseType(stageListInfoBean.course_type);
                                    courseInfo.setmCourseName(course_name);
                                    courseInfo.setmCoursePriceOld(String.valueOf(price));
                                    courseInfo.setmCoursePrice(String.valueOf(special_price)) ;
                                    stageCourseInfo.mCourseInfoList.add(courseInfo);
                                }
                            }
                        }
                    }
                    //????????????
                    CoursePacketStageCourseInit(mCoursePacketInfo);
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<StageCourseListBean> call, Throwable t) {
                Log.e(TAG, "onFailure:??????????????? " + t.getMessage());
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });

    }

    //??????????????????
    public void getDataPacketDetails() {
        if (mCoursePacketInfo == null){
            return;
        }
        LoadingDialog.getInstance(mMainContext).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mMainContext.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("course_package_id", Integer.valueOf(mCoursePacketInfo.mCoursePacketId));
        if (mMainContext.mStuId.equals("")){
            paramsMap.put("stu_id", 0);
        } else {
            paramsMap.put("stu_id", Integer.valueOf(mMainContext.mStuId));
        }
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Call<DataPacketDetailsBean> dataPacketDetailsBeanCall = modelObservableInterface.queryCoursePackageDetails(body);
        dataPacketDetailsBeanCall.enqueue(new Callback<DataPacketDetailsBean>() {
            @Override
            public void onResponse(Call<DataPacketDetailsBean> call, Response<DataPacketDetailsBean> response) {
                DataPacketDetailsBean dataPacketDetailsBean = response.body();
                if (dataPacketDetailsBean == null) {
                    Toast.makeText(mMainContext, "???????????????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (!HeaderInterceptor.IsErrorCode(dataPacketDetailsBean.getCode(),"")){
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (dataPacketDetailsBean.code != 200){
                    Toast.makeText(mMainContext, "???????????????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                if (dataPacketDetailsBean.data == null){
                    Toast.makeText(mMainContext, "???????????????????????????", Toast.LENGTH_LONG).show();
                    LoadingDialog.getInstance(mMainContext).dismiss();
                    return;
                }
                /**
                 * collection_status : 2
                 * course_package_id : 14
                 * details : ?????????1??????
                 * describe : java????????????????????????    coursePacketInfo
                 */
                int collection_status = dataPacketDetailsBean.data.getCollection_status();//???????????????1???????????????2??????????????????
                if (dataPacketDetailsBean.data.purchase_stauts != null){
                    if (dataPacketDetailsBean.data.purchase_stauts == 1){ //??????????????????
                        mCoursePacketInfo.mCoursePacketIsHave = "1";
                    } else {
                        mCoursePacketInfo.mCoursePacketIsHave = "0";
                    }
                } else {
                    mCoursePacketInfo.mCoursePacketIsHave = "0";
                }
                //??????
                mCoursePacketInfo.mCoursePacketDetails = dataPacketDetailsBean.data.getDetails();//???????????????
                mCoursePacketInfo.mCoursePacketId = String.valueOf(dataPacketDetailsBean.data.getCourse_package_id());
                //???????????????
                mCoursePacketInfo.mCoursePacketMessage = dataPacketDetailsBean.data.getDescribe();//???????????????
                mCoursePacketInfo.mEffictive_date = dataPacketDetailsBean.data.effictive_date;
                mCoursePacketInfo.mEffictive_days_type = dataPacketDetailsBean.data.effictive_days_type;
                mCoursePacketInfo.mInvalid_date_date = dataPacketDetailsBean.data.invalid_date;
                //        ???????????????????????????
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if (mCurrentTab.equals("Details")){
                        CoursePacketDetailsInit(mCoursePacketInfo,collection_status);
//                    }
//                    else if (mCurrentTab.equals("StageCourse")){
//                        getDataPacketStageofcourse();
//                    }
                }
                LoadingDialog.getInstance(mMainContext).dismiss();
            }

            @Override
            public void onFailure(Call<DataPacketDetailsBean> call, Throwable t) {
                Log.e(TAG, "onFailure:??????????????? " + t.getMessage());
                LoadingDialog.getInstance(mMainContext).dismiss();
            }
        });
    }


    //??????
    public static class CourseTeacherBean {
        /**
         * code : 200
         * data : [{"head":"http://thirdwx.qlogo.cn/mmopen/vi_32/0RPnUdGTLGxHQdUVDm5qfOKac2TYwS4JEWu5dQx2E06PocsIWKsNTvXY0VSTFETa2GUa7sy2rmZQnqKHp77pGQ/132","user_id":1,"true_name":"??????","introduction":"1"}]
         */
        private int code;
        private List<DataBean> data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * head : http://thirdwx.qlogo.cn/mmopen/vi_32/0RPnUdGTLGxHQdUVDm5qfOKac2TYwS4JEWu5dQx2E06PocsIWKsNTvXY0VSTFETa2GUa7sy2rmZQnqKHp77pGQ/132
             * user_id : 1
             * true_name : ??????
             * introduction : 1
             */

            private String head;
            private int user_id;
            private String true_name;
            private String introduction;

            public String getHead() {
                return head;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public String getTrue_name() {
                return true_name;
            }

            public void setTrue_name(String true_name) {
                this.true_name = true_name;
            }

            public String getIntroduction() {
                return introduction;
            }

            public void setIntroduction(String introduction) {
                this.introduction = introduction;
            }
        }
    }


    //????????????
    public static class StageCourseListBean {
        /**
         * code : 200
         * data : {"courseListInfo":[{"course_id":1,"special_price":66,"course_name":"????????????11","stage_id":1,"sort_of_course":1},{"course_id":2,"special_price":200,"course_name":"????????????002","stage_id":1,"sort_of_course":2},{"course_id":4,"special_price":200,"course_name":"????????????","stage_id":1,"sort_of_course":4},{"course_id":5,"special_price":111,"course_name":"1","stage_id":1,"sort_of_course":5},{"course_id":1,"special_price":66,"course_name":"????????????11","stage_id":4,"sort_of_course":5},{"course_id":2,"special_price":200,"course_name":"????????????002","stage_id":6,"sort_of_course":1},{"course_id":1,"special_price":66,"course_name":"????????????11","stage_id":6,"sort_of_course":2}],"stageListInfo":[{"stageLearningProgress":0,"stage_id":1,"stage_name":"????????????","sort_of_stage":3,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":4,"stage_name":"????????????","sort_of_stage":2,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":5,"stage_name":"????????????","sort_of_stage":1,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":6,"stage_name":"????????????","sort_of_stage":4,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":7,"stage_name":"????????????","sort_of_stage":5,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":8,"stage_name":"????????????","sort_of_stage":6,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":10,"stage_name":"????????????","sort_of_stage":10,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":17,"stage_name":"????????????","sort_of_stage":11,"stage_describe":"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????"},{"stageLearningProgress":0,"stage_id":18,"stage_name":"??????000","sort_of_stage":8,"stage_describe":"??????222"},{"stageLearningProgress":0,"stage_id":19,"stage_name":"??????","sort_of_stage":7,"stage_describe":"??????222"}]}
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
            private List<CourseListInfoBean> courseListInfo;
            private List<StageListInfoBean> stageListInfo;

            public List<CourseListInfoBean> getCourseListInfo() {
                return courseListInfo;
            }

            public void setCourseListInfo(List<CourseListInfoBean> courseListInfo) {
                this.courseListInfo = courseListInfo;
            }

            public List<StageListInfoBean> getStageListInfo() {
                return stageListInfo;
            }

            public void setStageListInfo(List<StageListInfoBean> stageListInfo) {
                this.stageListInfo = stageListInfo;
            }

            public static class CourseListInfoBean {
                /**
                 * course_id : 1
                 * special_price : 66
                 * course_name : ????????????11
                 * stage_id : 1
                 * sort_of_course : 1
                 */

                private int course_id;
                private float special_price;
                private String course_name;
                private int stage_id;
                private int sort_of_course;
                private String cover;
                private String course_type;
                private int price;

                public int getCourse_id() {
                    return course_id;
                }

                public void setCourse_id(int course_id) {
                    this.course_id = course_id;
                }

                public float getSpecial_price() {
                    return special_price;
                }

                public void setSpecial_price(int special_price) {
                    this.special_price = special_price;
                }

                public String getCourse_name() {
                    return course_name;
                }

                public void setCourse_name(String course_name) {
                    this.course_name = course_name;
                }

                public int getStage_id() {
                    return stage_id;
                }

                public void setStage_id(int stage_id) {
                    this.stage_id = stage_id;
                }

                public int getSort_of_course() {
                    return sort_of_course;
                }

                public void setSort_of_course(int sort_of_course) {
                    this.sort_of_course = sort_of_course;
                }

                public int getPrice() {
                    return price;
                }

                public String getCover() {
                    return cover;
                }

                public String getCourse_type() {
                    return course_type;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public void setCourse_type(String course_type) {
                    this.course_type = course_type;
                }

                public void setPrice(int price) {
                    this.price = price;
                }
            }

            public static class StageListInfoBean {
                /**
                 * stageLearningProgress : 0
                 * stage_id : 1
                 * stage_name : ????????????
                 * sort_of_stage : 3
                 * stage_describe : ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                 */

                private int stageLearningProgress;
                private int stage_id;
                private String stage_name;
                private int sort_of_stage;
                private String stage_describe;

                public int getStageLearningProgress() {
                    return stageLearningProgress;
                }

                public void setStageLearningProgress(int stageLearningProgress) {
                    this.stageLearningProgress = stageLearningProgress;
                }

                public int getStage_id() {
                    return stage_id;
                }

                public void setStage_id(int stage_id) {
                    this.stage_id = stage_id;
                }

                public String getStage_name() {
                    return stage_name;
                }

                public void setStage_name(String stage_name) {
                    this.stage_name = stage_name;
                }

                public int getSort_of_stage() {
                    return sort_of_stage;
                }

                public void setSort_of_stage(int sort_of_stage) {
                    this.sort_of_stage = sort_of_stage;
                }

                public String getStage_describe() {
                    return stage_describe;
                }

                public void setStage_describe(String stage_describe) {
                    this.stage_describe = stage_describe;
                }
            }
        }
    }

    //DataPacketDetailsBean?????????????????????
    public static class DataPacketDetailsBean {
        private int code;
        private DataPacketDetailsDataBean data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public DataPacketDetailsDataBean getData() {
            return data;
        }

        public void setData(DataPacketDetailsDataBean data) {
            this.data = data;
        }

        public static class DataPacketDetailsDataBean {
            /**
             * collection_status : 2
             * course_package_id : 14
             * details : ?????????1??????
             * describe : java????????????????????????
             */

            private int collection_status;
            private int course_package_id;
            private String details;
            private String describe;
            private Integer purchase_stauts; //????????????
            private String effictive_date; //????????????
            private String invalid_date;//?????????
            private String effictive_days_type; //????????????

            public int getCollection_status() {
                return collection_status;
            }

            public void setCollection_status(int collection_status) {
                this.collection_status = collection_status;
            }

            public int getCourse_package_id() {
                return course_package_id;
            }

            public void setCourse_package_id(int course_package_id) {
                this.course_package_id = course_package_id;
            }

            public String getDetails() {
                return details;
            }

            public void setDetails(String details) {
                this.details = details;
            }

            public String getDescribe() {
                return describe;
            }

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public Integer getPurchase_stauts() {
                return purchase_stauts;
            }

            public void setPurchase_stauts(Integer purchase_stauts) {
                this.purchase_stauts = purchase_stauts;
            }

            public String getEffictive_date() {
                return effictive_date;
            }

            public void setEffictive_date(String effictive_date) {
                this.effictive_date = effictive_date;
            }

            public String getInvalid_date_date() {
                return invalid_date;
            }

            public void setInvalid_date_date(String invalid_date_date) {
                this.invalid_date = invalid_date_date;
            }

            public String getEffictive_days_type() {
                return effictive_days_type;
            }

            public void setEffictive_days_type(String effictive_days_type) {
                this.effictive_days_type = effictive_days_type;
            }
        }
    }
    //??????
    public static class collectionBean{

        /**
         * code : 200
         * message : success
         */

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
