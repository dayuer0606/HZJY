package com.android.weischool.appactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.svideo.common.PublicCommonUtil;
import com.android.weischool.ActivityManager;
import com.android.weischool.ControllerCenterDialog;
import com.android.weischool.ControllerCustomDialog;
import com.android.weischool.ControllerCustomRoundAngleImageView;
import com.android.weischool.ControllerGlobals;
import com.android.weischool.ControllerPictureAdapter;
import com.android.weischool.ControllerPictureBean;
import com.android.weischool.ControllerWarpLinearLayout;
import com.android.weischool.HeaderInterceptor;
import com.android.weischool.HorizontalListView;
import com.android.weischool.LoadingDialog;
import com.android.weischool.ModelGetPhotoFromPhotoAlbum;
import com.android.weischool.ModelHtmlUtils;
import com.android.weischool.ModelObservableInterface;
import com.android.weischool.ModelSearchRecordSQLiteOpenHelper;
import com.android.weischool.ModelSearchView;
import com.android.weischool.R;
import com.android.weischool.adapter.CommonListAdapter;
import com.android.weischool.appinfo.CommunityBean;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

import static com.android.weischool.appinfo.ActivityIDInfo.ACTION_COMMUNITY_ANSWER_PICTURE_ADD;
import static com.android.weischool.appinfo.ActivityIDInfo.COMMUNITY_ANSWER_ACTIVITY_ID;

/**
 * Created by dayuer on 19/7/2.
 * ????????????
 */
public class ModelCommunityAnswerActivity extends FragmentActivity {
    //????????????
    private static ModelCommunityAnswerActivity mThis;
    private static final String TAG = "ModelCommunityAnswer";
    //??????????????????
    private View mCommunityAnswerView ,mCommunityAnswerSelectView ,mCommunityAnswerAddView ,mCommunityAnswerChooseSignView
            ,mCommunityAnswerDetailsView;
    //????????????-????????????
    private String mCommunityAnswerSelect = "-1";
    //????????????-???????????????
    private RecyclerView mRecyclerView;
    private ArrayList<ControllerPictureBean> mPictureBeansList;
    private ControllerPictureAdapter mPictureAdapter;
    private ArrayList<String> selPhotosPath = null;//???????????????????????????
    //???????????????????????????
    private boolean mIsPublish = true;
    //??????????????????????????????????????????
    private boolean mQuestionPublishImage = false;
    //???????????????????????????????????????
    private boolean mQuestionPublishTitle = false;
    //???????????????????????????????????????
    private boolean mQuestionPublishContent = false;
    //?????????????????????
    private String mQuestionPublishContentS = "";
    //?????????????????????
    private String mQuestionPublishTitleS = "";
    //??????????????????(?????????????????????id)?????????????????????
    private List<String> mCommunityAnswerChooseSignList = new ArrayList<>();
    //?????????????????????dialog
    private ControllerCenterDialog mMyDialog;

    //??????????????????????????????
    private int mCommunityAnswerCurrentPage = 0;
    private int mCommunityAnswerPageCount = 10;
    private int mCommunityAnswerSum = 0; //??????????????????

    //??????????????????????????????
    private int mCommunityAnswerDetailsCurrentPage = 0;
    private int mCommunityAnswerDetailsPageCount = 10;
    private int mCommunityAnswerDetailsSum = 0; //????????????????????????

    private String mKey = "";//???????????????

    private String mIpadress = PublicCommonUtil.ipadress;
    private String mStuId = "";
    private String mToken = "";
    private String mPage = "main";

    //??????
    private ControllerCustomDialog mCustomDialog = null;
    private DialogInterface.OnKeyListener keylistener = (dialog, keyCode, event) -> {
        Log.i("TAG", "??????code---" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog.dismiss();
            return false;
        } else if(keyCode == KeyEvent.KEYCODE_DEL){//?????????
            return false;
        }else{
            return true;
        }
    };
    //??????????????????????????????
    private SmartRefreshLayout smart_model_communityanswer;
    private LinearLayout communityanswer_datails_linearlayout;
    //??????????????????????????????
    private SmartRefreshLayout mSmart_model_communityanswer_detalis;
    private LinearLayout.LayoutParams ll;
    //?????????????????????????????????
    private EditText communityanswer_add_layout_contentetitledittext;
    //?????????????????????????????????
    private EditText communityanswer_add_layout_contentedittext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_communityanswer);
        mThis = this;
        mStuId = getIntent().getStringExtra("stu_id");
        mIpadress = getIntent().getStringExtra("ip");
        mToken = getIntent().getStringExtra("token");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COMMUNITY_ANSWER_PICTURE_ADD);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, filter);
        ActivityManager.getInstance().put(COMMUNITY_ANSWER_ACTIVITY_ID,this);
        CommunityAnswerMainShow();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getAction());
            switch (intent.getAction()) {
                case ACTION_COMMUNITY_ANSWER_PICTURE_ADD:
                    CommunityAnswerPictureAdd(intent);
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CommunityAnswerMainShow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final        int CUPREQUEST        = 50;
    private final        int CAMERA            = 10;
    private final        int ALBUM             = 20;
    private Uri uritempFile;
    private String          picPath;
    private File            mOutImage;

    private File cameraSavePath;//??????????????????
    private Uri uri;//??????uri

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Log.w("", "{onActivityResult}resultCode="+resultCode);
        Log.w("", "{onActivityResult}requestCode="+requestCode);
        if (resultCode == Activity.RESULT_OK) {
            //????????????
            if(requestCode == ControllerGlobals.CHOOSE_PIC_REQUEST_CODE){
                CommunityAnswerPictureAdd(data);
            }
        }

        switch (requestCode) {
            // ??????????????????
            case CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    picPath = String.valueOf(cameraSavePath);
                } else {
                    picPath = uri.getEncodedPath();
                }
                Log.d("????????????????????????:", picPath);
                mOutImage = new File(picPath);
                setCropPhoto();
                break;
            //??????????????????
            case ALBUM:
                picPath = ModelGetPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
                Log.d("????????????????????????:", picPath);
                mOutImage = new File(picPath);
                setCropPhoto();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setCropPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //?????????7.0???????????? ?????? ?????????uri??????
            //??????FileProvider????????????content?????????Uri
            Uri inputUri = FileProvider.getUriForFile(ModelCommunityAnswerActivity.this,
                    PublicCommonUtil.fileProvider, mOutImage);
            startPhotoZoom(inputUri);//??????????????????
        } else {
            Uri inputUri = Uri.fromFile(mOutImage);
            startPhotoZoom(inputUri);
        }
    }

    //??????
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //??????????????????????????????????????????????????????
            intent.putExtra("noFaceDetection", false);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        }
        // ????????????
        intent.putExtra("crop", "true");
        // aspectX aspectY ???????????????
        //?????????????????? ??????????????????
        if (Build.MODEL.contains("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // outputX outputY ?????????????????????
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        //miui?????? ???????????? return-data??????????????????????????????
        if (Build.MANUFACTURER.contains("Xiaomi")){//??????????????????Uri?????????uritempFile???Uri?????????
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "eduhead.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        } else {
            intent.putExtra("return-data", true);
            uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "eduhead.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CUPREQUEST);
    }

    private CommonListAdapter<CommunityQuerytagsBean.CommunityissueDataBean> adapter ;
    //???????????????????????????
    public void CommunityAnswerMainShow() {
        mPage = "main";
        HideAllLayout();
        mCommunityAnswerSelect = "-1";
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerView == null){
            mCommunityAnswerView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer, null);
            adapter = new CommonListAdapter<CommunityQuerytagsBean.CommunityissueDataBean>() {
                @Override
                protected View initListCell(int position, View convertView, ViewGroup parent) {
                    convertView = getLayoutInflater().inflate(R.layout.homepage_layout_functionbutton, parent, false);
                    TextView TextView_functionbuttonname = convertView.findViewById(R.id.TextView_functionbuttonname);
                    TextView_functionbuttonname.setText(adapter.getItem(position).modify_name);
                    if (mCommunityAnswerSelect.equals(String.valueOf(adapter.getItem(position).pse_id))) {
                        TextView_functionbuttonname.setTextColor(Color.RED);
                    }
                    return convertView;
                }
            };
            HorizontalListView sign_list = mCommunityAnswerView.findViewById(R.id.sign_list);
            sign_list.setAdapter(adapter);
            sign_list.setOnItemClickListener((adapterView, view, i, l) -> {
                mCommunityAnswerSelect = String.valueOf(adapter.getItem(i).pse_id);
                adapter.notifyDataSetChanged();
                getCommunityData();
            });
            getCommunityQuerytagsBeanData();
                    //??????????????????
            smart_model_communityanswer = mCommunityAnswerView.findViewById(R.id.Smart_model_communityanswer);
            smart_model_communityanswer.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    //???????????????????????????????????????
                    if (mCommunityAnswerSum <= mCommunityAnswerCurrentPage * mCommunityAnswerPageCount){
                        LinearLayout communityanswer_end = mCommunityAnswerView.findViewById(R.id.communityanswer_end);
                        communityanswer_end.setVisibility(View.VISIBLE);
                        smart_model_communityanswer.finishLoadMore();
                        return;
                    }
                    getCommunityDataMore();
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    //?????????????????? ????????????
                    getCommunityData();
                }
            });
            //??????????????????
            ImageView communityanswer_add = mCommunityAnswerView.findViewById(R.id.communityanswer_add);
            communityanswer_add.setOnClickListener(v->{
                CommunityAnswerAddInit(true);
            });
        }
        communityanswer_layout_main.addView(mCommunityAnswerView);
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        communityanswer_linearlayout.removeAllViews();
        //?????????????????? ????????????
        getCommunityData();
    }
    //??????----?????????????????????
    public void CommunityAnswerAddInit(boolean m_isInit){
        mPage = "add";
        HideAllLayout();
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerAddView == null) {
            mCommunityAnswerAddView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_add, null);
        }
        selPhotosPath = new ArrayList<>();
        //=============???????????????=========================//
        mPictureAdapter = null;
        //????????????
        mPictureBeansList = new ArrayList<>();
        //?????????????????????
        mRecyclerView = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_image);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mThis, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if(mPictureAdapter == null){
            //???????????????
            mPictureAdapter = new ControllerPictureAdapter(mThis, mPictureBeansList);
            mRecyclerView.setAdapter(mPictureAdapter);
            //???????????????
            //????????????????????????
            //??????ListView???setSelected(!ListView.isSelected())???????????????????????????????????????
            mRecyclerView.setSelected(true);
        }else{
            mPictureAdapter.notifyDataSetChanged();
        }
        //???????????????????????????
        mPictureAdapter.setOnItemClickLitener(new ControllerPictureAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View v,int position) {
                //???????????????????????????????????????
                List<String> photos = mPictureAdapter.getAllPhotoPaths();
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                NewImagePagerDialogFragment newImagePagerDialogFragment = NewImagePagerDialogFragment.getInstance(mThis,photos,position,screenLocation, v.getWidth(),
                        v.getHeight(),false);
                newImagePagerDialogFragment.show(mThis.getSupportFragmentManager(),"preview img");
            }

            @Override
            public void onItemAddClick() {
                if (selPhotosPath.size() >= 9){
                    Toast.makeText(mThis,"??????????????????9?????????",Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(mPictureAdapter.MAX)
                        .setGridColumnCount(3)
//                        .setSelected(selPhotosPath)
                        .start(mThis, ControllerGlobals.CHOOSE_PIC_REQUEST_CODE);
            }

            @Override
            public void onItemDeleteClick(View view, int position){
                mPictureBeansList.remove(position);
                mPictureAdapter.notifyDataSetChanged();
                if (mPictureBeansList.size() == 0) {
                    mQuestionPublishImage = false;
                }
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.bottom_button_select));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.orangeFFA899));
                }
            }
        });

        //?????????
        TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
        communityanswer_add_layout_next_button1.setClickable(true);
        communityanswer_add_layout_next_button1.setOnClickListener(v->{
            if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                if (!mQuestionPublishTitle) {
                    //???????????????????????????????????????
                    Toast.makeText(mThis, "??????????????????????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mQuestionPublishContent) {
                    //????????????????????????????????????
                    Toast.makeText(mThis, "????????????????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
                if (communityanswer_add_layout_contentedittext.getText().toString().length() < 10) {
                    //????????????????????????????????????10??????
                    Toast.makeText(mThis, "?????????????????????10??????", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (!mIsPublish){
                Toast.makeText(mThis,"?????????????????????????????????",Toast.LENGTH_LONG).show();
                return;
            }
            //???????????????????????????
            CommunityAnswerChooseSign();
        });
        EditText communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
//        //??????????????????????????????????????? ???????????????
//        communityanswer_add_layout_contentetitledittext.setFocusable(true);
//        communityanswer_add_layout_contentetitledittext.setFocusableInTouchMode(true);
//        communityanswer_add_layout_contentetitledittext.requestFocus();
//        communityanswer_add_layout_contentetitledittext.setSelection(communityanswer_add_layout_contentetitledittext.getText().toString().length());
        communityanswer_add_layout_contentedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //???????????????????????????
                if (!s.toString().equals("")){
                    mQuestionPublishContent = true;
                    mQuestionPublishContentS = s.toString();
                } else {
                    mQuestionPublishContent = false;
                    mQuestionPublishContentS = "";
                }
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.bottom_button_select));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.orangeFFA899));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //?????????????????????
        communityanswer_add_layout_contentetitledittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentetitledittext);
        communityanswer_add_layout_contentetitledittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //???????????????????????????
                if (!s.toString().equals("")){
                    mQuestionPublishTitle = true;
                    mQuestionPublishTitleS = s.toString();
                } else {
                    mQuestionPublishTitle = false;
                    mQuestionPublishTitleS = "";
                }
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.bottom_button_select));
                } else {
                    TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
                    communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.orangeFFA899));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        communityanswer_layout_main.addView(mCommunityAnswerAddView);
        if (m_isInit) { //????????????????????????????????????????????? ????????????????????????????????????
            mQuestionPublishTitle = false;
            mQuestionPublishTitleS = "";
            mQuestionPublishContent = false;
            mQuestionPublishContentS = "";
            mQuestionPublishImage = false;
            mCommunityAnswerChooseSignList.clear();
            if (mPictureBeansList != null) {
                mPictureBeansList.clear();
            }
            if (selPhotosPath != null) {
                selPhotosPath.clear();
                mPictureBeansList.clear();
            }
            //????????????????????????????????????????????????????????????
            Cursor cursor = ModelSearchRecordSQLiteOpenHelper.getReadableDatabase(mThis).rawQuery(
                    "select * from communityanswerdraftbox ", null);
            while (cursor.moveToNext()) {
                int titleIndex = cursor.getColumnIndex("title");
                int contentIndex = cursor.getColumnIndex("content");
                int photospathIndex = cursor.getColumnIndex("photospath");
                int signIndex = cursor.getColumnIndex("sign");
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String photospath = cursor.getString(photospathIndex);
                String sign = cursor.getString(signIndex);
                if (title != null) {
                    communityanswer_add_layout_contentetitledittext.setText(title);
                    if (!title.equals("")) {
                        mQuestionPublishTitle = true;
                        mQuestionPublishTitleS = title;
                    }
                }
                if (content != null) {
                    communityanswer_add_layout_contentedittext.setText(content);
                    if (!content.equals("")) {
                        mQuestionPublishContent = true;
                        mQuestionPublishContentS = content;
                    }
                }
                if (photospath != null) {
                    String photospathS[] = photospath.split(";");
                    for (int i = 0; i < photospathS.length; i++) {
                        if (photospathS[i].equals("")) {
                            continue;
                        }
                        selPhotosPath.add(photospathS[i]);
                    }
                    for (String path : selPhotosPath) {
                        ControllerPictureBean pictureBean = new ControllerPictureBean();
                        pictureBean.setPicPath(path);
                        pictureBean.setPicName(ControllerGlobals.getFileName(path));
                        //?????????????????????????????????????????????MAX??????????????????
                        if (mPictureBeansList.size() < mPictureAdapter.MAX) {
                            mPictureBeansList.add(pictureBean);
                        } else {
                            Toast.makeText(mThis, "??????????????????" + mPictureAdapter.MAX + "?????????", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    mPictureAdapter.notifyDataSetChanged();
                }
                if (sign != null) {
                    String signS[] = sign.split(";");
                    for (int i = 0; i < signS.length; i++) {
                        if (signS[i].equals("")) {
                            continue;
                        }
                        mCommunityAnswerChooseSignList.add(signS[i]);
                    }
                }
                break;
            }
            cursor.close();
        }
    }
    //????????????????????????
    public void CommunityAnswerPictureAdd(Intent data){
        //???????????????????????????????????????
        mQuestionPublishImage = true;
        if (mQuestionPublishImage || mQuestionPublishTitle || mQuestionPublishContent) {
            TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
            communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.bottom_button_select));
        } else {
            TextView communityanswer_add_layout_next_button1 = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_next_button1);
            communityanswer_add_layout_next_button1.setTextColor(mCommunityAnswerView.getResources().getColor(R.color.orangeFFA899));
        }
        if (data != null) {
            selPhotosPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
        }
        if (selPhotosPath != null) {

            //???????????????????????????????????????????????????????????????.setSelected(selPhotosPath)????????????????????????????????????????????????
					/*for(String path : selPhotosPath){
						Log.w(TAG,"path="+path);///storage/emulated/0/tempHxzk/IMG_1498034535796.jpg
						boolean existThisPic = false;
						for(int i=0;i<mPictureBeansList.size();i++){
							if(path.equals(mPictureBeansList.get(i).getPicPath())){
								//??????????????????????????????????????????????????????????????????????????????
								existThisPic = true;
								break;
							}
						}
						if(! existThisPic){
							PictureBean pictureBean = new PictureBean();
							pictureBean.setPicPath(path);
							pictureBean.setPicName(getFileName(path));
							//?????????????????????????????????????????????MAX??????????????????
							if (mPictureBeansList.size() < mPictureAdapter.MAX) {
								mPictureBeansList.add(pictureBean);
							} else {
								Toast.makeText(MainActivity.this, "??????????????????" + mPictureAdapter.MAX + "?????????", Toast.LENGTH_SHORT).show();
								break;
							}
						}
					}*/

            //????????????????????????????????????????????????
            for (String path : selPhotosPath) {
                ControllerPictureBean pictureBean = new ControllerPictureBean();
                pictureBean.setPicPath(path);
                pictureBean.setPicName(ControllerGlobals.getFileName(path));
                //?????????????????????????????????????????????MAX??????????????????
                if (mPictureBeansList.size() < mPictureAdapter.MAX) {
                    mPictureBeansList.add(pictureBean);
                } else {
                    Toast.makeText(mThis, "??????????????????" + mPictureAdapter.MAX + "?????????", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            mPictureAdapter.notifyDataSetChanged();
        }
    }
    //????????????----????????????
    private void CommunityAnswerChooseSign(){
        mPage = "ChooseSign";
        HideAllLayout();
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerChooseSignView == null) {
            mCommunityAnswerChooseSignView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_choosesign, null);
        }
        communityanswer_layout_main.addView(mCommunityAnswerChooseSignView);
        //                   ????????????
        TextView communityanswer_choosesign_layout_commit_button1 = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_layout_commit_button1);
        communityanswer_choosesign_layout_commit_button1.setOnClickListener(v->{
            if (!mIsPublish){
                Toast.makeText(mThis,"?????????????????????????????????",Toast.LENGTH_LONG).show();
                return;
            }
            //?????????????????????????????????????????????????????????????????????????????????????????????
            if (mCommunityAnswerChooseSignList.size() != 0){
                mIsPublish = false;
                //communityanswer_add_layout_contentetitledittext    communityanswer_add_layout_contentedittext
                String name = communityanswer_add_layout_contentetitledittext.getText().toString();
                String context = communityanswer_add_layout_contentetitledittext.getText().toString();
                //??????????????????
                if (selPhotosPath.size() == 0 ){ //????????????????????????????????????
                    mIsPublish = true;
                    //???????????????????????????
                    getCommunityissue();
                    //????????????   ??????????????????
                } else if (selPhotosPath != null) {//???????????????????????????????????????????????????
                    upLoadAnswerImage(name,context);
                }
            }
        });
        //?????????????????????
        TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
         //??????????????????????????????????????????
        getCommunityQuerytagsBeanData_publish();      //??????????????????
        communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
    }

    //???????????????
    private void CommunityAnswerSelectShow(){
        HideAllLayout();
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerSelectView == null) {
            mCommunityAnswerSelectView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_select, null);
            ModelSearchView communityanswer_search_view = mCommunityAnswerSelectView.findViewById(R.id.communityanswer_search_view);
            communityanswer_search_view.init("communityanswersearchrecords");
            // 4. ????????????????????????????????????????????????????????????
            // ?????? = ????????????????????????,,,,,,,,.
            communityanswer_search_view.setOnClickSearch(string ->{
               // System.out.println("????????????" + string);
                Toast.makeText(mThis, "??????????????????"+string, Toast.LENGTH_SHORT).show();
                mKey = string ;
                CommunityAnswerMainShow();
            });
            // 5. ????????????????????????????????????????????????????????????
            communityanswer_search_view.setOnClickBack(()->{
                CommunityAnswerMainShow();
            });
        }
        mPage = "search";
        communityanswer_layout_main.addView(mCommunityAnswerSelectView);
    }
    //?????????????????????
    public void CommunityAnswerDetailsShow(Integer questions_id) {
        mPage = "details";
        HideAllLayout();
        //???????????????
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        if (mCommunityAnswerDetailsView == null) {
            mCommunityAnswerDetailsView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_details, null);
            //??????????????????
            //Smart_model_communityanswer_detalis
            mSmart_model_communityanswer_detalis = mCommunityAnswerDetailsView.findViewById(R.id.Smart_model_communityanswer_detalis);
            mSmart_model_communityanswer_detalis.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (mCommunityAnswerDetailsSum <= mCommunityAnswerDetailsCurrentPage * mCommunityAnswerDetailsPageCount){
                        LinearLayout communityanswer_datails_end = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_end);
                        communityanswer_datails_end.setVisibility(View.VISIBLE);
                        mSmart_model_communityanswer_detalis.finishLoadMore();
                        return;
                    }
                    getCommunityDetilsBeanDataMore(questions_id);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getCommunityDetilsBeanData(questions_id);
                }
            });
        }
        //?????????????????????????????????
        communityanswer_layout_main.addView(mCommunityAnswerDetailsView);
        getCommunityDetilsBeanData(questions_id);
    }

    //??????????????????
    private void HideAllLayout(){
        LinearLayout communityanswer_layout_main = findViewById(R.id.communityanswer_layout_main);
        communityanswer_layout_main.removeAllViews();
    }

    //?????????????????????
    public void CommunityAnswerAddReturn(){
        String title = "";
        String content = "";
        String photosPath = "";
        String sign = "";
        if (mCommunityAnswerAddView != null){ //??????????????????????????????????????????
            EditText communityanswer_add_layout_contentetitledittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentetitledittext);
            title = communityanswer_add_layout_contentetitledittext.getText().toString();
            EditText communityanswer_add_layout_contentedittext = mCommunityAnswerAddView.findViewById(R.id.communityanswer_add_layout_contentedittext);
            content = communityanswer_add_layout_contentedittext.getText().toString();
        }
        CommunityAnswerMainShow();
        //???????????????????????????
        if (mQuestionPublishTitle || mQuestionPublishContent || mQuestionPublishImage){
            //?????????????????????????????????
            View view = mThis.getLayoutInflater().inflate(R.layout.dialog_sure, null);
            mMyDialog = new ControllerCenterDialog(mThis, 0, 0, view, R.style.DialogTheme);
            mMyDialog.setCancelable(true);
            mMyDialog.show();
            TextView tip = view.findViewById(R.id.tip);
            tip.setText("??????????????????");
            TextView dialog_content = view.findViewById(R.id.dialog_content);
            dialog_content.setText("????????????????????????????????????");
            TextView button_sure = view.findViewById(R.id.button_sure);
            button_sure.setText("?????????");
            button_sure.setTextColor(view.getResources().getColor(R.color.blue649cf0));
            button_sure.setOnClickListener(View->{
                mMyDialog.cancel();
            });
            //???????????????????????????  ?????????
            for (int i = 0; i < mPictureBeansList.size() ; i ++){
                if (i == mPictureBeansList.size() - 1){
                    photosPath = photosPath + mPictureBeansList.get(i).getPicPath();
                } else {
                    photosPath = photosPath + mPictureBeansList.get(i).getPicPath() + ";";
                }
            }
            //????????????  ?????????
            for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                if (i == mCommunityAnswerChooseSignList.size() - 1){
                    sign = sign + mCommunityAnswerChooseSignList.get(i);
                } else {
                    sign = sign + mCommunityAnswerChooseSignList.get(i) + ";";
                }
            }
            //??????????????????????????????????????????  selPhotosPath:?????????????????????content ?????????title ?????????  mCommunityAnswerChooseSignList?????????
            ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mThis).
                    execSQL("delete from communityanswerdraftbox");
            ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mThis).
                    execSQL("insert into communityanswerdraftbox(title,content,photospath,sign) values('" + title + "','" + content + "','" + photosPath + "','" + sign + "')");
        }
    }

    public void onClickCommunityAnswerReturn(View view) {
        if (mPage.equals("main")) {
            ActivityManager.getInstance().finish(COMMUNITY_ANSWER_ACTIVITY_ID);
        } else if (mPage.equals("details")) {
            CommunityAnswerMainShow();
        } else if (mPage.equals("add")) {
            CommunityAnswerAddReturn();
        } else if (mPage.equals("ChooseSign")) {
            CommunityAnswerAddInit(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (mPage.equals("main")) {
                ActivityManager.getInstance().finish(COMMUNITY_ANSWER_ACTIVITY_ID);
                return true;
            } else if (mPage.equals("details") || mPage.equals("search")) {
                CommunityAnswerMainShow();
                return true;
            } else if (mPage.equals("add")) {
                CommunityAnswerAddReturn();
                return true;
            } else if (mPage.equals("ChooseSign")) {
                CommunityAnswerAddInit(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onCommunityAnswerMainSearch(View view) {
        CommunityAnswerSelectShow();
    }

    /**
 ????* ????????????????????????????????????
 ????* @param bgAlpha
 ????*/
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mThis.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mThis.getWindow().setAttributes(lp);
    }

    //?????????????????? ?????????????????????
    public static int getScreenHeight() {
        DisplayMetrics dm = mThis.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //?????????????????????
    private void getCommunityissue(){
        if (mThis.mStuId.equals("")){
            Toast.makeText(mThis,"???????????????????????????",Toast.LENGTH_SHORT).show();
            mIsPublish = true;
            LoadingDialog.getInstance(mThis).dismiss();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        String subject_id = ";";
        for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
            subject_id = subject_id + mCommunityAnswerChooseSignList.get(i) + ";";
        }
        paramsMap.put("subject_id",subject_id);
        paramsMap.put("publisher", mThis.mStuId);//?????????????????????
        paramsMap.put("content",mQuestionPublishContentS);
        String questionPublishImageS = "";
        if (selPhotosPath != null) {
            for (int i = 0; i < selPhotosPath.size(); i++) {
                if (i == selPhotosPath.size() -1){
                    questionPublishImageS = questionPublishImageS + selPhotosPath.get(i);
                } else {
                    questionPublishImageS = questionPublishImageS + selPhotosPath.get(i) + ";";
                }
            }
        }
        paramsMap.put("picture",questionPublishImageS);
        paramsMap.put("title",mQuestionPublishTitleS);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryMyCommunityissue(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                            mIsPublish = true;
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                            mIsPublish = true;
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //???????????????????????????????????????????????????????????????
                        ModelSearchRecordSQLiteOpenHelper.getWritableDatabase(mThis).execSQL("delete from communityanswerdraftbox");
                        mCommunityAnswerAddView = null;
                        CommunityAnswerMainShow();
                        mIsPublish = true;
                        selPhotosPath.clear();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage());
                        Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                        mIsPublish = true;
                        LoadingDialog.getInstance(mThis).dismiss();
                        return;
                    }
                });
        }

    //?????????????????????
    private void getCommunityData() {
        if (mThis.mStuId.equals("")){
            if (smart_model_communityanswer != null){
                smart_model_communityanswer.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        communityanswer_linearlayout.removeAllViews();
        LinearLayout communityanswer_end = mCommunityAnswerView.findViewById(R.id.communityanswer_end);
        communityanswer_end.setVisibility(View.INVISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCommunityAnswerCurrentPage = 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        if (mCommunityAnswerSelect != null) {
            if (!mCommunityAnswerSelect.equals("-1")) {
                paramsMap.put("subject_id", Integer.valueOf(mCommunityAnswerSelect));//??????id
            }
        }
        paramsMap.put("pageNum", mCommunityAnswerCurrentPage);//?????????
        paramsMap.put("pageSize", mCommunityAnswerPageCount);//????????????
        paramsMap.put("course_type", 1);
        paramsMap.put("stu_id", Integer.valueOf(mThis.mStuId));
        String strEntity = gson.toJson(paramsMap);
        if (mKey != null) {
            if (!mKey.equals("")) {
                HashMap<String, String> paramsMap1 = new HashMap<>();
                paramsMap1.put("content", mKey);
                String strEntity1 = gson.toJson(paramsMap1);
                strEntity1 = strEntity1.replace("{", "");
                strEntity = strEntity.replace("}", "," + strEntity1);
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryAllCoursePackageCommunity(body)
                .enqueue(new Callback<CommunityBean>() {
                    @Override
                    public void onResponse(Call<CommunityBean> call, Response<CommunityBean> response) {
                        if (response.body() == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //??????????????????
                        int code = response.body().getCode();
                        if (!HeaderInterceptor.IsErrorCode(code, response.body().getMsg())) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        CommunityBean.CommunityDataBean communityDataBean = response.body().getData();
                        if (communityDataBean == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        mCommunityAnswerSum = communityDataBean.getTotal();
                        if (communityDataBean.getList() == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        for (int i = 0; i < communityDataBean.getList().size(); i++) {
                            CommunityBean.ListDataBean listDataBean = communityDataBean.getList().get(i);
                            if (listDataBean == null) {
                                continue;
                            }
                            View model_communityanswer_child_view1 = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_child, null);
                            communityanswer_linearlayout.addView(model_communityanswer_child_view1);
                            TextView course_question_child_name = model_communityanswer_child_view1.findViewById(R.id.course_question_child_name);
                            course_question_child_name.setText(listDataBean.getNicename());
                            //????????????
                            TextView communityanswer_child_look = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_look);
                            communityanswer_child_look.setText(listDataBean.getVisit_num() + "");
                            //????????????????????????
                            ControllerCustomRoundAngleImageView communityanswer_child_headportrait = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_headportrait);
                            Glide.with(mThis).load(listDataBean.getHead()).listener(new RequestListener<Drawable>() {
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
                            }).error(mThis.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(communityanswer_child_headportrait);
                            //??????????????????
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listDataBean.getCreation_time());
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
                                    listDataBean.setCreation_time(df2.format(date1).toString());
                                }
                            }
                            TextView communityanswer_child_time = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_time);
                            communityanswer_child_time.setText(listDataBean.getCreation_time());
                            //??????????????????
                            TextView communityanswer_child_title = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_title);
                            communityanswer_child_title.setText(listDataBean.getTitle());
                            //??????????????????
                            TextView communityanswer_child_message = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_message);
                            new ModelHtmlUtils(mThis, communityanswer_child_message).setHtmlWithPic(listDataBean.getContent());
                            //??????????????????   communityanswer_child_imagelayout
                            GridLayout communityanswer_child_imagelayout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_imagelayout);
                            communityanswer_child_imagelayout.removeAllViews();
                            //??????.size
                            if (listDataBean.getPicture() != null) {
                                String pictures[] = listDataBean.getPicture().split(";");
                                if (pictures != null) {
                                    for (int num = 0; num < pictures.length; num++) {
                                        if (pictures[num] == null) {
                                            continue;
                                        }
                                        if (pictures[num].equals("")) {
                                            continue;
                                        }
                                        View imageView = LayoutInflater.from(mThis).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                        ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                        Glide.with(mThis).load(pictures[num]).listener(new RequestListener<Drawable>() {
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
                                        }).error(mThis.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                        communityanswer_child_imagelayout.addView(imageView);
                                    }
                                }
                            }
                            if (listDataBean.getState() == 0) { //??????
                                communityanswer_child_title.setTextColor(Color.BLACK);
                                //?????????
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
                            } else if (listDataBean.getState() == 1) {//??????
                                //?????????
                                ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                ll.width = 0;
                                ll.rightMargin = 0;
                                communityanswer_child_top.setLayoutParams(ll);
                                communityanswer_child_title.setTextColor(Color.BLACK);
                            }
//                            else if (listDataBean.state == 2) {//??????
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
//                            }
                            //??????????????????  ???????????? ????????????    communityanswer_child_sign1
                            if (listDataBean.getSubject_id() != null) {
                                if (listDataBean.getSubject_id().size() == 0) {
                                    LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                    rl.height = 0;
                                    rl.topMargin = 0;
                                    communityanswer_child_sign.setLayoutParams(rl);
                                } else {
                                    for (int num = 0; num < listDataBean.getSubject_id().size(); num++) {
                                        if (listDataBean.getSubject_id() == null) {
                                            continue;
                                        }
                                        if (num == 0) {
                                            TextView communityanswer_child_sign1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign1);
                                            communityanswer_child_sign1.setText(listDataBean.getSubject_id().get(num));
                                            communityanswer_child_sign1.setVisibility(View.VISIBLE);
                                        } else if (num == 1) {
                                            TextView communityanswer_child_sign2 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign2);
                                            communityanswer_child_sign2.setText(listDataBean.getSubject_id().get(num));
                                            communityanswer_child_sign2.setVisibility(View.VISIBLE);
                                        } else if (num == 2) {
                                            TextView communityanswer_child_sign3 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign3);
                                            communityanswer_child_sign3.setText(listDataBean.getSubject_id().get(num));
                                            communityanswer_child_sign3.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                rl.height = 0;
                                rl.topMargin = 0;
                                communityanswer_child_sign.setLayoutParams(rl);
                            }
                            if (listDataBean.getHuida() != null) {
                                //?????????????????????????????????????????????
                                LinearLayout communityanswer_child_body = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_body);
                                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_body.getLayoutParams();
                                rl.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp15);
                                communityanswer_child_body.setLayoutParams(rl);
                                communityanswer_child_body.setPadding(0, 0, 0, (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10));
//                                TextView communityanswer_child_discusstext = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_discusstext);
//                                communityanswer_child_discusstext.setText( "??????"listDataBean.huida_num + "");
                                //?????????????????????????????????
//                                if (listDataBean.huida_num <= 5) {
                                LinearLayout communityanswer_child_content = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content);
                                for (int num = 0; num < listDataBean.getHuida().size(); num++) {
                                    CommunityBean.DataBean dataBean = listDataBean.getHuida().get(num);
                                    if (dataBean == null) {
                                        continue;
                                    }
                                    if (num >= 5) {//?????????????????????
                                        //       ????????????????????????????????????????????????????????????????????????????????????
                                        LinearLayout communityanswer_child_lookalldiscuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_lookalldiscuss);
                                        ll = (LinearLayout.LayoutParams) communityanswer_child_lookalldiscuss.getLayoutParams();
                                        ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        ll.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10);
                                        communityanswer_child_lookalldiscuss.setLayoutParams(ll);

                                        //??????????????????????????????????????????
                                        communityanswer_child_lookalldiscuss.setOnClickListener(v -> {
                                            CommunityAnswerDetailsShow(listDataBean.getQuestions_id());
                                        });
                                        break;
                                    }
                                    View respondView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_child1, null);
                                    communityanswer_child_content.addView(respondView);
                                    TextView communityanswer_child1_content = respondView.findViewById(R.id.communityanswer_child1_content);
                                    communityanswer_child1_content.setText(dataBean.getContent());
                                    TextView communityanswer_child1_name = respondView.findViewById(R.id.communityanswer_child1_name);
                                    TextView communityanswer_child1_name1 = respondView.findViewById(R.id.communityanswer_child1_name1);
                                    TextView communityanswer_child1_answer = respondView.findViewById(R.id.communityanswer_child1_answer);
                                    if (dataBean.getQ_nicename() == null) {
                                        communityanswer_child1_name.setText(dataBean.getA_nicename());
                                        communityanswer_child1_name.setHint(dataBean.getaID() + "");
                                        LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) communityanswer_child1_name1.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_name1.setLayoutParams(LL);
                                        LL = (LinearLayout.LayoutParams) communityanswer_child1_answer.getLayoutParams();
                                        LL.width = 0;
                                        LL.leftMargin = 0;
                                        communityanswer_child1_answer.setLayoutParams(LL);
                                    } else {
                                        communityanswer_child1_name.setText(dataBean.getQ_nicename());
                                        communityanswer_child1_name.setHint(dataBean.getqID() + "");
                                        communityanswer_child1_name1.setText(dataBean.getA_nicename());
                                    }
                                    respondView.setOnClickListener(v -> {
                                        //??????????????????
                                        mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle, "?????? " + communityanswer_child1_name.getText().toString(), false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.getQuestions_id()), communityanswer_child1_name.getHint().toString(), mThis.mStuId, content, "");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
//                                }
                            }
                            //?????????????????????????????????
                            RelativeLayout communityanswer_child_function_discuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_function_discuss);
                            communityanswer_child_function_discuss.setOnClickListener(v -> {
                                mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle, "??????", false);
                                mCustomDialog.setOnKeyListener(keylistener);
                                mCustomDialog.show();
                                mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                    @Override
                                    public void publish(String content) {
                                        //??????????????????????????? for??????  ???????????????size???????????????size????????????3
                                        getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.getQuestions_id()), String.valueOf(listDataBean.getQuestions_id()), mThis.mStuId, content, "");
                                    }

                                    @Override
                                    public void image() {
                                        Toast.makeText(mThis, "?????????????????????", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });

                            //????????????????????????
                            LinearLayout communityanswer_child_content1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content1);
                            communityanswer_child_content1.setOnClickListener(v -> {
                                CommunityAnswerDetailsShow(listDataBean.getQuestions_id());
                            });
                            if (listDataBean.getCollection_status() == 1) {
                                ImageView communityanswer_child_like_icon = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like_icon);
                                communityanswer_child_like_icon.setBackground(getResources().getDrawable(R.drawable.button_collect_enable));
                                TextView communityanswer_child_like = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like);
                                communityanswer_child_like.setTextColor(Color.RED);
                            }
                            //???????????????????????????
                            RelativeLayout communityanswer_child_like_layout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like_layout);
                            communityanswer_child_like_layout.setOnClickListener(v->{
                                sendCollect(listDataBean.getQuestions_id(),listDataBean.getCollection_status());
                            });
                        }
                        if (smart_model_communityanswer != null) {
                            smart_model_communityanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityBean> call, Throwable t) {
                        if (smart_model_communityanswer != null) {
                            smart_model_communityanswer.finishRefresh();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                        return;
                    }
                });
    }

    //????????????????????? - ????????????
    private void getCommunityDataMore(){
        if (mThis.mStuId.equals("")){
            if (smart_model_communityanswer != null){
                smart_model_communityanswer.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        LinearLayout communityanswer_linearlayout = mCommunityAnswerView.findViewById(R.id.communityanswer_linearlayout);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        mCommunityAnswerCurrentPage = mCommunityAnswerCurrentPage + 1;
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        if (mCommunityAnswerSelect != null) {
            if (!mCommunityAnswerSelect.equals("-1")) {
                paramsMap.put("subject_id", Integer.valueOf(mCommunityAnswerSelect));//??????id
            }
        }
        paramsMap.put("pageNum", mCommunityAnswerCurrentPage);//?????????
        paramsMap.put("pageSize",mCommunityAnswerPageCount);//????????????
        paramsMap.put("course_type",1);
        paramsMap.put("stu_id", Integer.valueOf(mThis.mStuId));
        String strEntity = gson.toJson(paramsMap);
        if (mKey != null) {
            if (!mKey.equals("")) {
                HashMap<String, String> paramsMap1 = new HashMap<>();
                paramsMap1.put("content", mKey);
                String strEntity1 = gson.toJson(paramsMap1);
                strEntity1 = strEntity1.replace("{", "");
                strEntity = strEntity.replace("}", "," + strEntity1);
            }
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryAllCoursePackageCommunity(body)
                .enqueue(new Callback<CommunityBean>() {
                    @Override
                    public void onResponse(Call<CommunityBean> call, Response<CommunityBean> response) {
                        if (response.body() == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //??????????????????
                        int code = response.body().getCode();
                        if (!HeaderInterceptor.IsErrorCode(code, response.body().getMsg())) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (code != 200) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        CommunityBean.CommunityDataBean communityDataBean = response.body().getData();
                        if (communityDataBean == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        mCommunityAnswerSum = communityDataBean.getTotal();
                        if (communityDataBean.getList() == null) {
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        for (int i = 0; i < communityDataBean.getList().size(); i++) {
                            CommunityBean.ListDataBean listDataBean = communityDataBean.getList().get(i);
                            if (listDataBean == null) {
                                continue;
                            }
                            View model_communityanswer_child_view1 = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_child, null);
                            communityanswer_linearlayout.addView(model_communityanswer_child_view1);
                            TextView course_question_child_name = model_communityanswer_child_view1.findViewById(R.id.course_question_child_name);
                            course_question_child_name.setText(listDataBean.getNicename());
                            //????????????
                            TextView communityanswer_child_look = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_look);
                            communityanswer_child_look.setText(listDataBean.getVisit_num() + "");
                            //????????????????????????
                            ControllerCustomRoundAngleImageView communityanswer_child_headportrait = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_headportrait);
                            Glide.with(mThis).load(listDataBean.getHead()).listener(new RequestListener<Drawable>() {
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
                            }).error(mThis.getResources().getDrawable(R.drawable.modelmy_myheaddefault)).into(communityanswer_child_headportrait);
                            //??????????????????
                            Date date = null;
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            try {
                                date = df.parse(listDataBean.getCreation_time());
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
                                    listDataBean.setCreation_time(df2.format(date1).toString());
                                }
                                TextView communityanswer_child_time = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_time);
                                communityanswer_child_time.setText(listDataBean.getCreation_time());
                                //??????????????????
                                TextView communityanswer_child_title = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_title);
                                communityanswer_child_title.setText(listDataBean.getTitle());
                                //??????????????????
                                TextView communityanswer_child_message = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_message);
                                new ModelHtmlUtils(mThis, communityanswer_child_message).setHtmlWithPic(listDataBean.getContent());
                                GridLayout communityanswer_child_imagelayout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_imagelayout);
                                communityanswer_child_imagelayout.removeAllViews();
                                //??????.size
                                if (listDataBean.getPicture() != null) {
                                    String pictures[] = listDataBean.getPicture().split(";");
                                    if (pictures != null) {
                                        for (int num = 0; num < pictures.length; num++) {
                                            if (pictures[num] == null) {
                                                continue;
                                            }
                                            if (pictures[num].equals("")) {
                                                continue;
                                            }
                                            View imageView = LayoutInflater.from(mThis).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                            ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                            Glide.with(mThis).load(pictures[num]).listener(new RequestListener<Drawable>() {
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
                                            }).error(mThis.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                            communityanswer_child_imagelayout.addView(imageView);
                                        }
                                    }
                                }
                                if (listDataBean.getState() == 0) { //??????
                                    //?????????
                                    ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                    ll.width = 0;
                                    ll.rightMargin = 0;
                                    communityanswer_child_top.setLayoutParams(ll);
                                    communityanswer_child_title.setTextColor(Color.BLACK);
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
                                } else if (listDataBean.getState() == 1) {//??????
                                    //?????????
                                    ImageView communityanswer_child_top = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_top);
                                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_child_top.getLayoutParams();
                                    ll.width = 0;
                                    ll.rightMargin = 0;
                                    communityanswer_child_top.setLayoutParams(ll);
                                    communityanswer_child_title.setTextColor(Color.BLACK);
                                }
//                            else if (listDataBean.state == 2) {//??????
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
//                            }
                                //??????????????????  ???????????? ????????????    communityanswer_child_sign1
                                if (listDataBean.getSubject_id() != null) {
                                    if (listDataBean.getSubject_id().size() == 0) {
                                        LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                        rl.height = 0;
                                        rl.topMargin = 0;
                                        communityanswer_child_sign.setLayoutParams(rl);
                                    } else {
                                        for (int num = 0; num < listDataBean.getSubject_id().size(); num++) {
                                            if (listDataBean.getSubject_id() == null) {
                                                continue;
                                            }
                                            if (num == 0) {
                                                TextView communityanswer_child_sign1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign1);
                                                communityanswer_child_sign1.setText(listDataBean.getSubject_id().get(num));
                                                communityanswer_child_sign1.setVisibility(View.VISIBLE);
                                            } else if (num == 1) {
                                                TextView communityanswer_child_sign2 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign2);
                                                communityanswer_child_sign2.setText(listDataBean.getSubject_id().get(num));
                                                communityanswer_child_sign2.setVisibility(View.VISIBLE);
                                            } else if (num == 2) {
                                                TextView communityanswer_child_sign3 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign3);
                                                communityanswer_child_sign3.setText(listDataBean.getSubject_id().get(num));
                                                communityanswer_child_sign3.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                } else {
                                    LinearLayout communityanswer_child_sign = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_sign);
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_sign.getLayoutParams();
                                    rl.height = 0;
                                    rl.topMargin = 0;
                                    communityanswer_child_sign.setLayoutParams(rl);
                                }
                                if (listDataBean.getHuida() != null) {
                                    //?????????????????????????????????????????????
                                    LinearLayout communityanswer_child_body = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_body);
                                    RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_child_body.getLayoutParams();
                                    rl.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp15);
                                    communityanswer_child_body.setLayoutParams(rl);
                                    communityanswer_child_body.setPadding(0, 0, 0, (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10));
//                                TextView communityanswer_child_discusstext = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_discusstext);
//                                communityanswer_child_discusstext.setText(listDataBean.huida_num + "");
//                                if (listDataBean.huida_num <= 5) {
                                    //?????????????????????????????????
                                    LinearLayout communityanswer_child_content = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content);
                                    for (int num = 0; num < listDataBean.getHuida().size(); num++) {
                                        CommunityBean.DataBean dataBean = listDataBean.getHuida().get(num);
                                        if (dataBean == null) {
                                            continue;
                                        }
                                        if (num >= 5) {//?????????????????????
                                            //       ????????????????????????????????????????????????????????????????????????????????????
                                            LinearLayout communityanswer_child_lookalldiscuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_lookalldiscuss);
                                            ll = (LinearLayout.LayoutParams) communityanswer_child_lookalldiscuss.getLayoutParams();
                                            ll.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                            ll.topMargin = (int) model_communityanswer_child_view1.getResources().getDimension(R.dimen.dp10);
                                            communityanswer_child_lookalldiscuss.setLayoutParams(ll);

                                            //??????????????????????????????????????????
                                            communityanswer_child_lookalldiscuss.setOnClickListener(v -> {
                                                CommunityAnswerDetailsShow(listDataBean.getQuestions_id());
                                            });
                                            break;
                                        }
                                        View respondView = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_child1, null);
                                        communityanswer_child_content.addView(respondView);
                                        TextView communityanswer_child1_content = respondView.findViewById(R.id.communityanswer_child1_content);
                                        communityanswer_child1_content.setText(dataBean.getContent());
                                        TextView communityanswer_child1_name = respondView.findViewById(R.id.communityanswer_child1_name);
                                        TextView communityanswer_child1_name1 = respondView.findViewById(R.id.communityanswer_child1_name1);
                                        TextView communityanswer_child1_answer = respondView.findViewById(R.id.communityanswer_child1_answer);
                                        if (dataBean.getQ_nicename() == null) {
                                            communityanswer_child1_name.setText(dataBean.getA_nicename());
                                            communityanswer_child1_name.setHint(dataBean.getaID() + "");
                                            LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) communityanswer_child1_name1.getLayoutParams();
                                            LL.width = 0;
                                            LL.leftMargin = 0;
                                            communityanswer_child1_name1.setLayoutParams(LL);
                                            LL = (LinearLayout.LayoutParams) communityanswer_child1_answer.getLayoutParams();
                                            LL.width = 0;
                                            LL.leftMargin = 0;
                                            communityanswer_child1_answer.setLayoutParams(LL);
                                        } else {
                                            communityanswer_child1_name.setText(dataBean.getQ_nicename());
                                            communityanswer_child1_name.setHint(dataBean.getqID() + "");
                                            communityanswer_child1_name1.setText(dataBean.getA_nicename());
                                        }
                                        respondView.setOnClickListener(v -> {
                                            //??????????????????
                                            mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle, "?????? " + communityanswer_child1_name.getText().toString(), false);
                                            mCustomDialog.setOnKeyListener(keylistener);
                                            mCustomDialog.show();
                                            mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                                @Override
                                                public void publish(String content) {
                                                    getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.getQuestions_id()), communityanswer_child1_name.getHint().toString(), mThis.mStuId, content, "");
                                                }

                                                @Override
                                                public void image() {

                                                }
                                            });
                                        });
                                    }
//                                }
                                }
                                //?????????????????????????????????
                                RelativeLayout communityanswer_child_function_discuss = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_function_discuss);
                                communityanswer_child_function_discuss.setOnClickListener(v -> {
                                    mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle, "??????", false);
                                    mCustomDialog.setOnKeyListener(keylistener);
                                    mCustomDialog.show();
                                    mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                        @Override
                                        public void publish(String content) {
                                            //??????????????????????????? for??????  ???????????????size???????????????size????????????3
                                            getCommunityDetilsreplyBeanData(String.valueOf(listDataBean.getQuestions_id()), String.valueOf(listDataBean.getPublisher()), mThis.mStuId, content, "");
                                        }

                                        @Override
                                        public void image() {
                                            Toast.makeText(mThis, "?????????????????????", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                });

                                //????????????????????????
                                LinearLayout communityanswer_child_content1 = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_content1);
                                communityanswer_child_content1.setOnClickListener(v -> {
                                    CommunityAnswerDetailsShow(listDataBean.getQuestions_id());
                                });

                                if (listDataBean.getCollection_status() == 1) {
                                    ImageView communityanswer_child_like_icon = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like_icon);
                                    communityanswer_child_like_icon.setBackground(getResources().getDrawable(R.drawable.button_collect_enable));
                                    TextView communityanswer_child_like = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like);
                                    communityanswer_child_like.setTextColor(Color.RED);
                                }
                                //???????????????????????????
                                RelativeLayout communityanswer_child_like_layout = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_like_layout);
                                communityanswer_child_like_layout.setOnClickListener(v->{
                                    sendCollect(listDataBean.getQuestions_id(),listDataBean.getCollection_status());
                                });
                            }
                            if (smart_model_communityanswer != null) {
                                smart_model_communityanswer.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<CommunityBean> call, Throwable t) {
                        if (smart_model_communityanswer != null){
                            smart_model_communityanswer.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                        return;
                    }
                });
    }

    //?????????????????????
    public void sendCollect(int questions_id,int isCollect){
        if (mStuId.equals("")) {
            Toast.makeText(mThis, "????????????????????????", Toast.LENGTH_SHORT).show();
            LoadingDialog.getInstance(mThis).dismiss();
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("stu_id", Integer.valueOf(mStuId));
        paramsMap.put("questions_id", questions_id);
        if (isCollect == 1) {
            isCollect = 2;
        } else {
            isCollect = 1;
        }
        paramsMap.put("collection_status", isCollect);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        //queryMyCommunityQuerytags
        queryMyCourseList.addMyCollectionQuestion(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mThis).dismiss();
                            Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //????????????????????????
                        getCommunityData();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Toast.makeText(mThis,"??????????????????",Toast.LENGTH_SHORT).show();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //????????????-----??????
    public void getCommunityDetilsBeanData(Integer questions_id){
        if (questions_id == null){
            if (mSmart_model_communityanswer_detalis != null){
                mSmart_model_communityanswer_detalis.finishRefresh();
            }
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        LinearLayout communityanswer_datails_end = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_end);
        communityanswer_datails_end.setVisibility(View.INVISIBLE);
        //??????????????????????????????????????????view???
        communityanswer_datails_linearlayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_linearlayout);
        communityanswer_datails_linearlayout.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCommunityAnswerDetailsCurrentPage = 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCommunityAnswerDetailsCurrentPage);//?????????
        paramsMap.put("pageSize",mCommunityAnswerDetailsPageCount);//????????????
        paramsMap.put("questions_id", questions_id);//??????id
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<CommunityDetilsBean> call, Response<CommunityDetilsBean> response) {
                        CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,communityDetilsBean.msg)){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishRefresh();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //??????????????????title
                        TextView communityanswer_datails_titletext = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_titletext);
                        communityanswer_datails_titletext.setText("??????????????????");

                        //????????????
                        TextView communityanswer_datails_name = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_name);
                        communityanswer_datails_name.setText(communityDetilsDataBean.getNicename());
                        //???????????????
                        ControllerCustomRoundAngleImageView communityanswer_datails_headportrait = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_headportrait);
                        Glide.with(mThis).load(communityDetilsDataBean.getHead()).into(communityanswer_datails_headportrait);
                        //???????????????
                        TextView communityanswer_datails_time = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_time);
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
                        communityanswer_datails_time.setText(communityDetilsDataBean.getCreation_time());
                        //??????????????????
                        LinearLayout communityanswer_datails_content1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_content1);
                        communityanswer_datails_content1.setOnClickListener(v->{
                            //????????????
                            mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle,"?????? " + communityanswer_datails_name.getText().toString(),false);
                            mCustomDialog.setOnKeyListener(keylistener);
                            mCustomDialog.show();
                            mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                @Override
                                public void publish(String content) {
                                    getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id), String.valueOf(communityDetilsDataBean.publisher),mThis.mStuId,content,"");
                                }

                                @Override
                                public void image() {

                                }
                            });
                        });
                        //???????????????
                        TextView communityanswer_datails_title = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_title);
                        communityanswer_datails_title.setText(communityDetilsDataBean.getTitle());
                        if (communityDetilsDataBean.state == 0) { //??????
                            //?????????
                            ImageView communityanswer_datails_top = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_datails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            communityanswer_datails_top.setLayoutParams(ll);
                            communityanswer_datails_title.setTextColor(Color.BLACK);
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.state == 1) {//??????
                            //?????????
                            ImageView communityanswer_datails_top = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_datails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            communityanswer_datails_top.setLayoutParams(ll);
                            communityanswer_datails_title.setTextColor(Color.BLACK);
                        }
                        //???????????????
                        TextView communityanswer_datails_message = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_message);
                        new ModelHtmlUtils(mThis, communityanswer_datails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        if (communityDetilsDataBean.getSubject_id() != null){
                            for (int i = 0; i < communityDetilsDataBean.subject_id.size() ; i ++){
                                String string = communityDetilsDataBean.subject_id.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0){
                                    //???????????????1    communityanswer_datails_sign1
                                    TextView communityanswer_datails_sign1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign1);
                                    communityanswer_datails_sign1.setText(string);
                                    communityanswer_datails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1){
                                    //???????????????2    communityanswer_datails_sign2
                                    TextView communityanswer_datails_sign2 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign2);
                                    communityanswer_datails_sign2.setText(string);
                                    communityanswer_datails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2){
                                    //???????????????3    communityanswer_datails_sign3
                                    TextView communityanswer_datails_sign3 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign3);
                                    communityanswer_datails_sign3.setText(string);
                                    communityanswer_datails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            LinearLayout communityanswer_datails_sign = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_datails_sign.getLayoutParams();
                            rl.height = 0;
                            rl.topMargin = 0;
                            communityanswer_datails_sign.setLayoutParams(rl);
                        }
//                        //?????????
                        GridLayout communityanswer_datails_imagelayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_imagelayout);
                        communityanswer_datails_imagelayout.removeAllViews();
                        //??????.size
                        if (communityDetilsDataBean.picture != null) {
                            String pictures[] = communityDetilsDataBean.picture.split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mThis).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mThis).load(pictures[num]).listener(new RequestListener<Drawable>() {
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
                                    }).error(mThis.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    communityanswer_datails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        if (communityDetilsDataBean.huida != null){
                            mCommunityAnswerDetailsSum = communityDetilsDataBean.huida.total;
                            if (communityDetilsDataBean.huida.list != null) {
                                for (int i = 0; i < communityDetilsDataBean.huida.list.size(); i++) {
                                    if (communityDetilsDataBean.huida.list.get(i) == null){
                                        continue;
                                    }
                                    //for??????????????????????????????
                                    View view = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_details1, null);
                                    //????????????????????????
                                    ControllerCustomRoundAngleImageView mcommunityanswer_datails1_headportrait = view.findViewById(R.id.communityanswer_datails1_headportrait);
                                    Glide.with(mThis).load(communityDetilsDataBean.huida.list.get(i).getA_head()).into(mcommunityanswer_datails1_headportrait);
                                    //???????????????
                                    TextView communityanswer_datails1_name = view.findViewById(R.id.communityanswer_datails1_name);
                                    communityanswer_datails1_name.setText(communityDetilsDataBean.huida.list.get(i).q_nicename);
                                    communityanswer_datails1_name.setHint(communityDetilsDataBean.huida.list.get(i).qID + "");
                                    //??????
                                    TextView communityanswer_datails1_time = view.findViewById(R.id.communityanswer_datails1_time);
                                    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        date = df.parse(communityDetilsDataBean.huida.list.get(i).creation_time);
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
                                            communityDetilsDataBean.huida.list.get(i).creation_time = df2.format(date1).toString();
                                        }
                                    }
                                    communityanswer_datails1_time.setText(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    //??????
                                    TextView communityanswer_datails1_message = view.findViewById(R.id.communityanswer_datails1_message);
                                    new ModelHtmlUtils(mThis, communityanswer_datails1_message).setHtmlWithPic(communityDetilsDataBean.huida.list.get(i).content);
                                    communityanswer_datails_linearlayout.addView(view);

                                    view.setOnClickListener(v->{
                                        mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle,"?????? " + communityanswer_datails1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id),communityanswer_datails1_name.getHint().toString(),mThis.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                        }
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishRefresh();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityDetilsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishRefresh();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //????????????-----??????-????????????
    public void getCommunityDetilsBeanDataMore(Integer questions_id){
        if (questions_id == null){
            if (mSmart_model_communityanswer_detalis != null){
                mSmart_model_communityanswer_detalis.finishLoadMore();
            }
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        //??????????????????????????????????????????view???
        communityanswer_datails_linearlayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_linearlayout);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface modelObservableInterface = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        mCommunityAnswerDetailsCurrentPage = mCommunityAnswerDetailsCurrentPage + 1;
        HashMap<String, Integer> paramsMap = new HashMap<>();
        paramsMap.put("pageNum", mCommunityAnswerDetailsCurrentPage);//?????????
        paramsMap.put("pageSize",mCommunityAnswerDetailsPageCount);//????????????
        paramsMap.put("questions_id", questions_id);//??????id
        paramsMap.put("course_type",1);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        modelObservableInterface.QueryCommunityQuestionsDetails(body)
                .enqueue(new Callback<CommunityDetilsBean>() {
                    @Override
                    public void onResponse(Call<CommunityDetilsBean> call, Response<CommunityDetilsBean> response) {
                        CommunityDetilsBean communityDetilsBean = response.body();
                        if (communityDetilsBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        int code = communityDetilsBean.getCode();
                        if (!HeaderInterceptor.IsErrorCode(code,communityDetilsBean.msg)){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (code != 200){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        CommunityDetilsBean.CommunityDetilsDataBean communityDetilsDataBean = communityDetilsBean.getData();
                        if (communityDetilsDataBean == null){
                            if (mSmart_model_communityanswer_detalis != null){
                                mSmart_model_communityanswer_detalis.finishLoadMore();
                            }
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //??????????????????title
                        TextView communityanswer_datails_titletext = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_titletext);
                        communityanswer_datails_titletext.setText("??????????????????");
                        //????????????
                        TextView communityanswer_datails_name = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_name);
                        communityanswer_datails_name.setText(communityDetilsDataBean.getNicename());
                        //???????????????
                        ControllerCustomRoundAngleImageView communityanswer_datails_headportrait = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_headportrait);
                        Glide.with(mThis).load(communityDetilsDataBean.getHead()).into(communityanswer_datails_headportrait);
                        //???????????????
                        TextView communityanswer_datails_time = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_time);
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
                        communityanswer_datails_time.setText(communityDetilsDataBean.getCreation_time());
                        //??????????????????
                        LinearLayout communityanswer_datails_content1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_content1);
                        communityanswer_datails_content1.setOnClickListener(v->{
                            //????????????
                            mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle,"?????? " + communityanswer_datails_name.getText().toString(),false);
                            mCustomDialog.setOnKeyListener(keylistener);
                            mCustomDialog.show();
                            mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                @Override
                                public void publish(String content) {
                                    getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id), String.valueOf(communityDetilsDataBean.publisher),mThis.mStuId,content,"");
                                }

                                @Override
                                public void image() {

                                }
                            });
                        });
                        //???????????????
                        TextView communityanswer_datails_title = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_title);
                        communityanswer_datails_title.setText(communityDetilsDataBean.getTitle());
                        if (communityDetilsDataBean.state == 0) { //??????
                            //?????????
                            ImageView communityanswer_datails_top = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_datails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            communityanswer_datails_top.setLayoutParams(ll);
                            communityanswer_datails_title.setTextColor(Color.BLACK);
//                                //?????????
//                                ImageView communityanswer_child_fine = model_communityanswer_child_view1.findViewById(R.id.communityanswer_child_fine);
//                                ll = (LinearLayout.LayoutParams) communityanswer_child_fine.getLayoutParams();
//                                ll.width = 0;
//                                ll.rightMargin = 0;
//                                communityanswer_child_fine.setLayoutParams(ll);
                        } else if (communityDetilsDataBean.state == 1) {//??????
                            //?????????
                            ImageView communityanswer_datails_top = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_top);
                            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) communityanswer_datails_top.getLayoutParams();
                            ll.width = 0;
                            ll.rightMargin = 0;
                            communityanswer_datails_top.setLayoutParams(ll);
                            communityanswer_datails_title.setTextColor(Color.BLACK);
                        }
                        //???????????????
                        TextView communityanswer_datails_message = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_message);
                        new ModelHtmlUtils(mThis, communityanswer_datails_message).setHtmlWithPic(communityDetilsDataBean.getContent());
                        if (communityDetilsDataBean.getSubject_id() != null){
                            for (int i = 0; i < communityDetilsDataBean.subject_id.size() ; i ++){
                                String string = communityDetilsDataBean.subject_id.get(i);
                                if (string == null){
                                    continue;
                                }
                                if (i >= 3){
                                    break;
                                }
                                if (i == 0){
                                    //???????????????1    communityanswer_datails_sign1
                                    TextView communityanswer_datails_sign1 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign1);
                                    communityanswer_datails_sign1.setText(string);
                                    communityanswer_datails_sign1.setVisibility(View.VISIBLE);
                                } else if (i == 1){
                                    //???????????????2    communityanswer_datails_sign2
                                    TextView communityanswer_datails_sign2 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign2);
                                    communityanswer_datails_sign2.setText(string);
                                    communityanswer_datails_sign2.setVisibility(View.VISIBLE);
                                } else if (i == 2){
                                    //???????????????3    communityanswer_datails_sign3
                                    TextView communityanswer_datails_sign3 = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign3);
                                    communityanswer_datails_sign3.setText(string);
                                    communityanswer_datails_sign3.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            LinearLayout communityanswer_datails_sign = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_sign);
                            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) communityanswer_datails_sign.getLayoutParams();
                            rl.height = 0;
                            rl.topMargin = 0;
                            communityanswer_datails_sign.setLayoutParams(rl);
                        }

//                        //?????????
                        GridLayout communityanswer_datails_imagelayout = mCommunityAnswerDetailsView.findViewById(R.id.communityanswer_datails_imagelayout);
                        communityanswer_datails_imagelayout.removeAllViews();
                        //??????.size
                        if (communityDetilsDataBean.picture != null) {
                            String pictures[] = communityDetilsDataBean.picture.split(";");
                            if (pictures != null) {
                                for (int num = 0; num < pictures.length; num ++) {
                                    if (pictures[num] == null){
                                        continue;
                                    }
                                    if (pictures[num].equals("")){
                                        continue;
                                    }
                                    View imageView = LayoutInflater.from(mThis).inflate(R.layout.controllercustomroundangleimageview_layout, null);
                                    ControllerCustomRoundAngleImageView CustomRoundAngleImageView = imageView.findViewById(R.id.CustomRoundAngleImageView);
                                    Glide.with(mThis).load(pictures[num]).listener(new RequestListener<Drawable>() {
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
                                    }).error(mThis.getResources().getDrawable(R.drawable.modelcoursecover)).into(CustomRoundAngleImageView);
                                    communityanswer_datails_imagelayout.addView(imageView);
                                }
                            }
                        }
                        if (communityDetilsDataBean.huida != null){
                            mCommunityAnswerDetailsSum = communityDetilsDataBean.huida.total;
                            if (communityDetilsDataBean.huida.list != null) {
                                for (int i = 0; i < communityDetilsDataBean.huida.list.size(); i++) {
                                    if (communityDetilsDataBean.huida.list.get(i) == null){
                                        continue;
                                    }
                                    //for??????????????????????????????
                                    View view = LayoutInflater.from(mThis).inflate(R.layout.model_communityanswer_details1, null);
                                    //????????????????????????
                                    ControllerCustomRoundAngleImageView mcommunityanswer_datails1_headportrait = view.findViewById(R.id.communityanswer_datails1_headportrait);
                                    Glide.with(mThis).load(communityDetilsDataBean.huida.list.get(i).getA_head()).into(mcommunityanswer_datails1_headportrait);
                                    //???????????????
                                    TextView communityanswer_datails1_name = view.findViewById(R.id.communityanswer_datails1_name);
                                    communityanswer_datails1_name.setText(communityDetilsDataBean.huida.list.get(i).q_nicename);
                                    communityanswer_datails1_name.setHint(communityDetilsDataBean.huida.list.get(i).qID + "");
                                    //??????
                                    TextView communityanswer_datails1_time = view.findViewById(R.id.communityanswer_datails1_time);
                                    df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        date = df.parse(communityDetilsDataBean.huida.list.get(i).creation_time);
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
                                            communityDetilsDataBean.huida.list.get(i).creation_time = df2.format(date1).toString();
                                        }
                                    }
                                    communityanswer_datails1_time.setText(communityDetilsDataBean.huida.list.get(i).creation_time);
                                    //??????
                                    TextView communityanswer_datails1_message = view.findViewById(R.id.communityanswer_datails1_message);
                                    new ModelHtmlUtils(mThis, communityanswer_datails1_message).setHtmlWithPic(communityDetilsDataBean.huida.list.get(i).content);
                                    communityanswer_datails_linearlayout.addView(view);

                                    view.setOnClickListener(v->{
                                        mCustomDialog = new ControllerCustomDialog(mThis, R.style.customdialogstyle,"?????? " + communityanswer_datails1_name.getText().toString(),false);
                                        mCustomDialog.setOnKeyListener(keylistener);
                                        mCustomDialog.show();
                                        mCustomDialog.setOnClickPublishOrImagelistener(new ControllerCustomDialog.OnClickPublishOrImage() {
                                            @Override
                                            public void publish(String content) {
                                                getCommunityDetilsreplyBeanData(String.valueOf(communityDetilsDataBean.questions_id),communityanswer_datails1_name.getHint().toString(),mThis.mStuId,content,"");
                                            }

                                            @Override
                                            public void image() {

                                            }
                                        });
                                    });
                                }
                            }
                        }
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityDetilsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        if (mSmart_model_communityanswer_detalis != null){
                            mSmart_model_communityanswer_detalis.finishLoadMore();
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //????????????????????????
    public void getCommunityQuerytagsBeanData(){
        LoadingDialog.getInstance(mThis).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), "{}");
        //queryMyCommunityQuerytags
        queryMyCourseList.queryMyCommunityQuerytags(body)
                .enqueue(new Callback<CommunityQuerytagsBean>() {
                    @Override
                    public void onResponse(Call<CommunityQuerytagsBean> call, Response<CommunityQuerytagsBean> response) {
                        CommunityQuerytagsBean querytagsBean = response.body();
                        if (querytagsBean == null){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(querytagsBean.code,querytagsBean.msg)){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (querytagsBean.code != 200){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (querytagsBean.data == null){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        adapter.clear();
                        CommunityQuerytagsBean.CommunityissueDataBean bean = new CommunityQuerytagsBean.CommunityissueDataBean();
                        bean.pse_id = -1;
                        bean.modify_name = "??????";
                        adapter.add(bean);
                        adapter.addAll(querytagsBean.data);
                        adapter.notifyDataSetChanged();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityQuerytagsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //????????????????????????????????????
    public void getCommunityQuerytagsBeanData_publish(){
        LoadingDialog.getInstance(mThis).show();
        //????????????
        ControllerWarpLinearLayout communityanswer_choosesign_warpLinearLayout = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_warpLinearLayout);
        communityanswer_choosesign_warpLinearLayout.removeAllViews();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), "{}");
        //queryMyCommunityQuerytags
        queryMyCourseList.queryMyCommunityQuerytags(body)
                .enqueue(new Callback<CommunityQuerytagsBean>() {
                    @Override
                    public void onResponse(Call<CommunityQuerytagsBean> call, Response<CommunityQuerytagsBean> response) {
                        CommunityQuerytagsBean querytagsBean = response.body();
                        if (querytagsBean == null){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(querytagsBean.code,querytagsBean.msg)){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (querytagsBean.code != 200){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (querytagsBean.data == null){
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        //?????????????????????????????????
                        TextView communityanswer_choosesign_layout_commit_button1 = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_layout_commit_button1);
                        for (int num = 0; num < querytagsBean.data.size(); num ++){
                            CommunityQuerytagsBean.CommunityissueDataBean communityissueDataBean = querytagsBean.data.get(num);
                            if (communityissueDataBean == null){
                                continue;
                            }
                            if (communityissueDataBean.pse_id == null){
                                continue;
                            }
                            View view = mThis.getLayoutInflater().inflate(R.layout.model_communityanswer_selectpop_child, null);
                            TextView communityanswer_selectpop_child_signname = view.findViewById(R.id.communityanswer_selectpop_child_signname);
                            //???????????????????????????
                            communityanswer_selectpop_child_signname.setText(communityissueDataBean.modify_name);
                            communityanswer_selectpop_child_signname.setHint(communityissueDataBean.pse_id + "");
                            communityanswer_choosesign_warpLinearLayout.addView(view);
                            view.setOnClickListener(v->{
                                //???????????????????????????????????????????????????????????????
                                for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                                    String chooseSign = mCommunityAnswerChooseSignList.get(i);
                                    if (chooseSign.equals(communityanswer_selectpop_child_signname.getHint().toString())) {
                                        communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect));
                                        communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.grayff999999));
                                        communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5));
                                        mCommunityAnswerChooseSignList.remove(i);
                                        TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                        communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                                        if (mCommunityAnswerChooseSignList.size() == 0){
                                            communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.black999999));
                                        }
                                        return;
                                    }
                                }
                                //????????????
                                if (mCommunityAnswerChooseSignList.size() >= 3){
                                    Toast.makeText(mThis, "?????????????????????", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5));
                                String hint = communityanswer_selectpop_child_signname.getHint().toString();
                                mCommunityAnswerChooseSignList.add(hint);
                                //????????????????????????
                                if (mCommunityAnswerChooseSignList.size() != 0){
                                    communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.blackff333333));
                                }
                                TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                //???????????????????????????
                                communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                            });
                            //?????????????????????????????????
                            if (mCommunityAnswerChooseSignList.size() == 0){
                                communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                mCommunityAnswerChooseSignList.add(communityanswer_selectpop_child_signname.getHint().toString());
                                communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5),
                                        (int) view.getResources().getDimension(R.dimen.dp5));
                                TextView communityanswer_choosesign_choosecount = mCommunityAnswerChooseSignView.findViewById(R.id.communityanswer_choosesign_choosecount);
                                communityanswer_choosesign_choosecount.setText(String.valueOf(mCommunityAnswerChooseSignList.size()));
                                //????????????????????????
                                if (mCommunityAnswerChooseSignList.size() != 0){
                                    communityanswer_choosesign_layout_commit_button1.setTextColor(view.getResources().getColor(R.color.blackff333333));
                                }
                            } else {
                                for (int i = 0; i < mCommunityAnswerChooseSignList.size() ; i ++){
                                    String chooseSign = mCommunityAnswerChooseSignList.get(i);
                                    if (chooseSign.equals(communityanswer_selectpop_child_signname.getHint().toString())) {
                                        communityanswer_selectpop_child_signname.setBackground(view.getResources().getDrawable(R.drawable.textview_style_rect_blue));
                                        communityanswer_selectpop_child_signname.setTextColor(view.getResources().getColor(R.color.white));
                                        communityanswer_selectpop_child_signname.setPadding((int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5),
                                                (int) view.getResources().getDimension(R.dimen.dp5));
                                        break;
                                    }
                                }
                            }
                        }
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<CommunityQuerytagsBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //????????????-----??????
    public void getCommunityDetilsreplyBeanData(String mid,String fid,String publisher,String content,String picture){
        if (mid == null || fid == null || publisher == null || content == null){
            Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
            return;
        }
        if (mid.equals("") || fid.equals("") || publisher.equals("") || content.equals("")){
            Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mThis.mIpadress)
                .client(ModelObservableInterface.client)
                .build();
        ModelObservableInterface queryMyCourseList = retrofit.create(ModelObservableInterface.class);
        Gson gson = new Gson();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mid", mid);//????????????---??????
        paramsMap.put("fid", fid);
        paramsMap.put("publisher", publisher);
        paramsMap.put("content", content);
        paramsMap.put("picture", picture);
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        queryMyCourseList.queryCommunityDetilsreplyBean(body)
                .enqueue(new Callback<ModelObservableInterface.BaseBean>() {
                    @Override
                    public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                        ModelObservableInterface.BaseBean baseBean = response.body();
                        if (baseBean == null){
                            Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (!HeaderInterceptor.IsErrorCode(baseBean.getErrorCode(),baseBean.getErrorMsg())){
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        if (baseBean.getErrorCode() != 200){
                            Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
                            mCustomDialog.dismiss();
                            LoadingDialog.getInstance(mThis).dismiss();
                            return;
                        }
                        CommunityAnswerMainShow();
                        Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
                        mCustomDialog.dismiss();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }

                    @Override
                    public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
                        mCustomDialog.dismiss();
                        LoadingDialog.getInstance(mThis).dismiss();
                    }
                });
    }

    //??????????????????????????????
    private void upLoadAnswerImage(String title,String content) {
        if (title.equals("") || content.equals("")){
            mIsPublish = true;
            Toast.makeText(mThis, "??????????????????!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mThis.mToken == null) {
            mIsPublish = true;
            Toast.makeText(mThis, "????????????????????????!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mThis.mToken.equals("")) {
            mIsPublish = true;
            Toast.makeText(mThis, "????????????????????????!", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadingDialog.getInstance(mThis).show();
        int timeout = 6000;
        if (selPhotosPath.size() != 0){
            timeout = timeout * selPhotosPath.size();
        }
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String uu = UUID.randomUUID().toString();
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "multipart/form-data; boundary=" + uu)
                            .addHeader("Stuid", mThis.mStuId)
                            .addHeader("permissioncode", mThis.mToken)
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mThis.mIpadress)
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
        Call<ModelObservableInterface.BaseBean> call = modelObservableInterface.upLoadImage(params);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(timeout);
        Toast.makeText(mThis, "????????????????????????????????????!???????????????" + hms, Toast.LENGTH_LONG).show();
        mIsPublish = false;
        call.enqueue(new Callback<ModelObservableInterface.BaseBean>() {
            @Override
            public void onResponse(Call<ModelObservableInterface.BaseBean> call, Response<ModelObservableInterface.BaseBean> response) {
                if (response == null){
                    mIsPublish = true;
                    Toast.makeText(mThis, "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                if (response.body() == null){
                    mIsPublish = true;
                    Toast.makeText(mThis, "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }

                if (response.code() != 200){
                    mIsPublish = true;
                    Log.d("Tag???onFailure", response.message());
                    Toast.makeText(mThis, "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                if (response.body().getData() == null){
                    mIsPublish = true;
                    Toast.makeText(mThis, "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
                    LoadingDialog.getInstance(mThis).dismiss();
                    return;
                }
                if (selPhotosPath.size() == response.body().getData().size()){
                    selPhotosPath.clear();
                }
                for (int i = 0; i < response.body().getData().size(); i ++){
                    String path = (String) response.body().getData().get(String.valueOf(i));
                    selPhotosPath.add(path);
                }
                mIsPublish = true;
                //???????????????????????????
                getCommunityissue();
            }
            //??????????????????
            @Override
            public void onFailure(Call<ModelObservableInterface.BaseBean> call, Throwable t) {
                if (t.getMessage() != null) {
                    Log.d("Tag???onFailure", t.getMessage().toString());
                }
                mIsPublish = true;
                LoadingDialog.getInstance(mThis).dismiss();
                Toast.makeText(mThis, "?????????????????????????????????!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //????????????-????????????
    public static class CommunityQuerytagsBean{
        private List<CommunityissueDataBean> data;
        private int code;
        private String msg;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setData(List<CommunityissueDataBean> data) {
            this.data = data;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public List<CommunityissueDataBean> getData() {
            return data;
        }

        static class CommunityissueDataBean {
            private Integer pse_id;
            private String modify_name;

            public void setPse_id(Integer pse_id) {
                this.pse_id = pse_id;
            }

            public Integer getPse_id() {
                return pse_id;
            }

            public String getModify_name() {
                return modify_name;
            }

            public void setModify_name(String modify_name) {
                this.modify_name = modify_name;
            }
        }
    }
    //????????????????????????
    public static class CommunityDetilsBean{
        private CommunityDetilsDataBean data;
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setData(CommunityDetilsDataBean data) {
            this.data = data;
        }

        public CommunityDetilsDataBean getData() {
            return data;
        }

        public String getMsg() {
            return msg;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public class CommunityDetilsDataBean{
            private List<String> subject_id;
            private String creation_time;
            private Integer questions_id;
            private String nicename;
            private String title;
            private String content;
            private String picture;
            private String head;
            private Integer publisher;
            private Integer state;
            private Integer visit_num;
            private CommunityDetilsAnswerDataBean huida;

            public void setHuida(CommunityDetilsAnswerDataBean huida) {
                this.huida = huida;
            }

            public void setState(Integer state) {
                this.state = state;
            }

            public void setPublisher(Integer publisher) {
                this.publisher = publisher;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public void setNicename(String nicename) {
                this.nicename = nicename;
            }

            public void setHead(String head) {
                this.head = head;
            }

            public String getPicture() {
                return picture;
            }

            public String getNicename() {
                return nicename;
            }

            public String getHead() {
                return head;
            }

            public Integer getState() {
                return state;
            }

            public Integer getPublisher() {
                return publisher;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

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

            public void setVisit_num(Integer visit_num) {
                this.visit_num = visit_num;
            }

            public void setQuestions_id(Integer questions_id) {
                this.questions_id = questions_id;
            }

            public void setSubject_id(List<String> subject_id) {
                this.subject_id = subject_id;
            }

            public CommunityDetilsAnswerDataBean getHuida() {
                return huida;
            }

            public Integer getQuestions_id() {
                return questions_id;
            }

            public Integer getVisit_num() {
                return visit_num;
            }

            public List<String> getSubject_id() {
                return subject_id;
            }
        }
        public class CommunityDetilsAnswerDataBean {
            private Integer total;
            private List<CommunityDetilsAnswerDataBeanList> list;

            public void setTotal(Integer total) {
                this.total = total;
            }

            public Integer getTotal() {
                return total;
            }

            public List<CommunityDetilsAnswerDataBeanList> getList() {
                return list;
            }

            public void setList(List<CommunityDetilsAnswerDataBeanList> list) {
                this.list = list;
            }
        }

        public class CommunityDetilsAnswerDataBeanList {
            private Integer a_publisher;
            private String q_head;
            private Integer a_st;
            private String q_nicename;
            private Integer q_publisher;
            private Integer q_st;
            private Integer qID;
            private Integer aID;
            private String a_nicename;
            private String content;
            private String a_head;
            private String creation_time;

            public void setCreation_time(String creation_time) {
                this.creation_time = creation_time;
            }

            public String getCreation_time() {
                return creation_time;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public Integer getA_publisher() {
                return a_publisher;
            }

            public Integer getA_st() {
                return a_st;
            }

            public Integer getaID() {
                return aID;
            }

            public Integer getQ_publisher() {
                return q_publisher;
            }

            public Integer getQ_st() {
                return q_st;
            }

            public Integer getqID() {
                return qID;
            }

            public String getA_head() {
                return a_head;
            }

            public String getA_nicename() {
                return a_nicename;
            }

            public String getQ_head() {
                return q_head;
            }

            public String getQ_nicename() {
                return q_nicename;
            }

            public void setA_head(String a_head) {
                this.a_head = a_head;
            }

            public void setA_nicename(String a_nicename) {
                this.a_nicename = a_nicename;
            }

            public void setA_publisher(Integer a_publisher) {
                this.a_publisher = a_publisher;
            }

            public void setA_st(Integer a_st) {
                this.a_st = a_st;
            }

            public void setaID(Integer aID) {
                this.aID = aID;
            }

            public void setQ_head(String q_head) {
                this.q_head = q_head;
            }

            public void setQ_nicename(String q_nicename) {
                this.q_nicename = q_nicename;
            }

            public void setQ_publisher(Integer q_publisher) {
                this.q_publisher = q_publisher;
            }

            public void setQ_st(Integer q_st) {
                this.q_st = q_st;
            }

            public void setqID(Integer qID) {
                this.qID = qID;
            }
        }
    }
}
