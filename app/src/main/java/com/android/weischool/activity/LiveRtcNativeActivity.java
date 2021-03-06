package com.android.weischool.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.weischool.R;
import com.android.weischool.adapter.VideoAdapter;
import com.android.weischool.consts.EventType;
import com.android.weischool.consts.PlatformType;
import com.android.weischool.dialog.AlertDialogFactory;
import com.android.weischool.dialog.AlertDialogFragment;
import com.android.weischool.entity.Event;
import com.android.weischool.entity.VideoStatusData;
import com.android.weischool.event.SimpleGestureLayoutListener;
import com.android.weischool.helper.AudioManagerHelper;
import com.android.weischool.impl.OnRtcMemberImpl;
import com.android.weischool.manager.ColorViewPopManager;
import com.android.weischool.manager.DrawAndStrokePopManager;
import com.android.weischool.manager.RtcChatPopManager;
import com.android.weischool.manager.SwitchModePopManager;
import com.android.weischool.net.NetMonitor;
import com.android.weischool.net.NetWorkStateReceiver;
import com.android.weischool.util.AnimatorUtil;
import com.android.weischool.util.ClickUtil;
import com.android.weischool.util.DimensionUtils;
import com.android.weischool.util.EventBusUtil;
import com.android.weischool.util.GestureLayoutWrapper;
import com.android.weischool.util.SoftKeyboardStateWatcher;
import com.android.weischool.util.TimeUtil;
import com.android.weischool.util.ToastUtil;
import com.android.weischool.view.LoadingImageView;
import com.android.weischool.view.SectorLayout;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.MemberRole;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtDispatchChatMessageListener;
import com.talkfun.sdk.event.LiveInListener;
import com.talkfun.sdk.event.OnLiveDurationListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.log.TalkFunLogger;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.sdk.rtc.RtcOperatorProxy;
import com.talkfun.sdk.rtc.consts.ApplyStatus;
import com.talkfun.sdk.rtc.consts.DrawPowerStatus;
import com.talkfun.sdk.rtc.consts.MediaStatus;
import com.talkfun.sdk.rtc.entity.RtcInfo;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;
import com.talkfun.sdk.rtc.interfaces.OnRtcErrorListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMediaStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcMemberListener;
import com.talkfun.sdk.rtc.interfaces.OnRtcStatusListener;
import com.talkfun.sdk.rtc.interfaces.OnWhiteboardPowerListener;
import com.talkfun.widget.ColorView;
import com.zhy.m.permission.MPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.emitter.Emitter;
import q.rorbin.badgeview.QBadgeView;


public class LiveRtcNativeActivity extends BaseActivity implements LiveInListener, HtDispatchChatMessageListener, OnLiveDurationListener {
    @BindView(R.id.base_container)
    LinearLayout base_container;
    @BindView(R.id.ppt_fl_layout)
    FrameLayout pptLayout; // ????????????
    @BindView(R.id.ppt_container)
    FrameLayout pptContainer; // ??????
    @BindView(R.id.video_view_container_rv)
    RecyclerView videoContainerRV;//????????????
    @BindView(R.id.activity_small_container)
    LinearLayout controllContainer;//???????????????
    @BindView(R.id.activity_small_titlebar)
    RelativeLayout titlebarContainer;//???????????????
    @BindView(R.id.bad_netStatus_ll)
    LinearLayout badNetStatusLL;//???????????????
    //-------------------?????????????????????-----------------------------
    /**
     * ???????????????????????????
     */
    @BindView(R.id.platform_iv)
    LoadingImageView platformIV;
    /**
     * ???????????????
     */
    @BindView(R.id.down_platform_iv)
    View downPlatformIV;
    /**
     * ????????????
     */
    @BindView(R.id.chat_iv)
    View chatIV;
    /**
     * ????????????
     */
    @BindView(R.id.video_switch_iv)
    ImageView videoSwitchIV;
    /**
     * ????????????
     */
    @BindView(R.id.audio_switch_iv)
    ImageView audioSwitchIV;
    /***
     * ???????????????
     */
    private QBadgeView qBadgeView;

    //----------?????????????????????---------------------------------
    /**
     * ?????????
     */
    @BindView(R.id.activity_small_title_tv)
    TextView titleTV;
    /**
     * ????????????
     */
    @BindView(R.id.activity_small_play_total_time_tv)
    TextView cuurentPlayTotalTimeTV;
    @BindView(R.id.network_state_tv)
    TextView networkStateTV;

    //-------------------???????????????-------------------------------
    @BindView(R.id.paint_sl)
    SectorLayout paintSL;
    @BindView(R.id.sector_layout_paint_iv)
    ImageView paintIV;
    @BindView(R.id.sector_layout_cmd_iv)
    ImageView cmdIV;
    @BindView(R.id.sector_layout_stroke_cv)
    ColorView strokeCV;
    @BindView(R.id.sector_layout_color_cv)
    ColorView colorCV;
    @BindView(R.id.sector_layout_eraser_iv)
    ImageView eraserIV;
/*    @BindView(R.id.log_tv)
    TextView log_tv;*/


    /**
     * ???????????????????????????????????????
     */
    private DrawAndStrokePopManager mDrawAndStrokePopManager;
    /**
     * ?????????????????????
     */
    private RtcChatPopManager chatPopManager;
    /**
     * ??????????????????????????????
     */
    private ColorViewPopManager mColorViewPopManager;
    private SwitchModePopManager mSwitchModePopManager;
    private Map<Integer, VideoStatusData> mVideoMap = new ConcurrentHashMap<>();
    private RtcOperatorProxy mRtcOperatorProxy;
    private VideoAdapter videoAdapter;
    private Unbinder unbinder;
    private String mToken;
    /**
     * ??????Id
     */
    private int spidId;
    private HtSdk mHtSdk;
    /**
     * ???????????????????????????
     */
    private boolean isCloseVideoForZhubo = false;
    /**
     * ?????????????????????
     */
    private boolean isCloseAudioForZhubo = false;
    /**
     * ??????????????????
     */
    private boolean isLiving;
    /**
     * ?????????????????????
     */
    private boolean eraserIsSelect = false;
    /**
     * ?????????????????????
     */
    private boolean isDrawPower;
    private NetWorkStateReceiver netWorkStateReceiver;
    private int videoWidth;
    private boolean isReload;
    private int pptWidth;
    private int screenWidth;
    private int screenHeight;
    private boolean isDubug = false;
    private AlertDialog socketFailTips;
//    private TextView loadFailTv;
    /**
     * ?????????????????????
     */
    private boolean isFront;
    private boolean hasChecked = false;
    GestureLayoutWrapper gestureLayoutWrapper;
    AudioManagerHelper audioManagerHelper;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//??????????????????
        setContentView(R.layout.activity_rtc_live_native);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);//SurfaceView????????????
        unbinder = ButterKnife.bind(this);
        EventBusUtil.register(this);
        init();
        initView();
        initHtSdk();
        initListener();
//        initLogDialog();
    }

    private void init() {
        mToken = getIntent().getStringExtra("token");
        setVideoLayoutParams();
        audioManagerHelper = new AudioManagerHelper(this);
    }

    private void setVideoLayoutParams() {
        screenWidth = DimensionUtils.getScreenWidth(this);
        screenHeight = DimensionUtils.getScreenHeight(this);
        videoWidth = screenHeight / 3;
        videoContainerRV.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    private void initView() {
        setVideoAdapter();
        chatPopManager = new RtcChatPopManager(this);
        qBadgeView = new QBadgeView(this);
        qBadgeView.bindTarget(chatIV);
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
//        qBadgeView.setGravityOffset(5, true);
        gestureLayoutWrapper = new GestureLayoutWrapper(this, pptContainer);
    }

    private void setVideoAdapter() {
        videoAdapter = new VideoAdapter(this, null);
        videoAdapter.setHasStableIds(true);
        videoContainerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        videoContainerRV.setHasFixedSize(true);
        videoContainerRV.setItemViewCacheSize(10);
        videoContainerRV.setDrawingCacheEnabled(true);
        videoContainerRV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        videoContainerRV.setAdapter(videoAdapter);
    }

    private void initHtSdk() {
        mHtSdk = HtSdk.getInstance();
        mHtSdk.init(this, pptContainer, null, mToken, TFMode.LIVE_RTC);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View liveWaitView = layoutInflater.inflate(R.layout.live_wait_layout, null);
        View liveOverView = layoutInflater.inflate(R.layout.live_over_layout, null);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);
//        loadFailTv = loadFailView.findViewById(R.id.load_fail_tv);
        mHtSdk.setLiveWaitView(liveWaitView);//???????????????????????????view
        mHtSdk.setLiveOverView(liveOverView);//????????????????????????view
        mHtSdk.setLoadingView(loadingView); //???????????????????????????????????????view
        mHtSdk.setLoadFailView(loadFailView);//???????????????????????????????????????view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));
        mHtSdk.setPauseInBackground(false);
        //socket??????????????????
        mHtSdk.on(BroadcastCmdType.SOCKET_CONNECT_FAIL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (LiveRtcNativeActivity.this.isFinishing()) {
                    return;
                }
                if (socketFailTips == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LiveRtcNativeActivity.this);
                    builder.setMessage("socket??????????????????????????????????????????????????????????????????");
                    builder.setTitle(R.string.tips);
                    builder.setPositiveButton(R.string.refresh, (dialog, which) -> HtSdk.getInstance().reload()).setNegativeButton(R.string.goback, (dialog, which) -> LiveRtcNativeActivity.this.finish());
                    socketFailTips = builder.create();
                    socketFailTips.setCancelable(false);
                }
                socketFailTips.show();
                //Toast.makeText(LiveNativeActivity.this, "socket??????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initListener() {
        /**??????????????????????????????*/
        mHtSdk.setHtDispatchChatMessageListener(this);
        mHtSdk.setLiveDurationListener(this);
        mHtSdk.setRtcStatusListener(mOnRtcStatusListener);
        mHtSdk.setRtcMemberListener(mOnRtcMemberListener);
        mHtSdk.setRtcErrorListener(mOnRtcErrorListener);
        mHtSdk.setRtcMediaStatusListener(onRtcMediaStatusListener);
        mHtSdk.setWhiteboardPowerListener(onWhiteboardPowerListenter);
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());
        mHtSdk.setLiveListener(this);

//        CheckNetSpeed.getInstance().startCheckNetSpeed(new CheckNetSpeed.OnNetSpeedChangeListener() {
//            @Override
//            public void getNetSpeedAndState(int speed, String netState) {
//                networkStateTV.setVisibility(CheckNetSpeed.POOR_STATE.equals(netState) ? View.VISIBLE : View.GONE);
//            }
//        });
        /**
         * ???????????????
         */
        SoftKeyboardStateWatcher stateWatcher = new SoftKeyboardStateWatcher(pptLayout);
        stateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
            }

            @Override
            public void onSoftKeyboardClosed() {
                if (chatPopManager != null) {
                    chatPopManager.inputTextMsgDialogDismiss();
                }
            }
        });


        paintSL.setOnDrawEnableListener(new SectorLayout.OnDrawEnableListener() {
            @Override
            public void enable() {
                gestureLayoutWrapper.setMaskEnable(false);
                AnimatorUtil.rotate(paintIV, 45, 0);
            }

            @Override
            public void disable() {

                gestureLayoutWrapper.setMaskEnable(true);
                AnimatorUtil.rotate(paintIV, 0, 45);
            }
        });


        gestureLayoutWrapper.setGestureLayoutListener(new SimpleGestureLayoutListener() {
            @Override
            public void onStartVolumeOffset() {
                int streamType = applyStatus() == ApplyStatus.ALLOW ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
                audioManagerHelper.startVolumeOffset(streamType);
            }

            @Override
            public void onVolumeOffset(float offsetPercentage) {
                audioManagerHelper.volumeOffset(offsetPercentage);
            }

            @Override
            public void onStopVolumeOffset() {
                audioManagerHelper.stopVolumeOffset();
            }

            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onClick() {
                if (isTitleBarShow) {
                    hideController();
                } else {
                    showController();
                }
                return true;
            }

        });
    }


    /**
     * ???????????????
     *
     * @param entity
     */
    @Override
    public void receiveChatMessage(ChatEntity entity) {
        if (chatPopManager != null) {
            chatPopManager.receiveMessage(entity);
            if (qBadgeView != null) {//????????????
                qBadgeView.setBadgeNumber(chatPopManager.isShow() ? 0 : -1);
            }
        }
    }

    /**
     * ????????????
     *
     * @param
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.platform_iv, R.id.video_switch_iv, R.id.down_platform_iv, R.id.audio_switch_iv, R.id.paint_sl, R.id.activity_small_refresh_iv
            , R.id.activity_small_back_rl, R.id.chat_iv, R.id.ppt_container, R.id.sector_layout_color_cv, R.id.sector_layout_cmd_iv, R.id.sector_layout_stroke_cv, R.id.sector_layout_eraser_iv/*, R.id.log_tv*/})
    void onClick(View v) {
        if (!ClickUtil.getInstance().isClickable(v.getId()))
            return;
        switch (v.getId()) {
            case R.id.platform_iv:
                if (mRtcOperatorProxy != null) {
                    if (ApplyStatus.NO_APPLY == applyStatus()) {//???????????????
                        if (hasChecked) {
                            showApplyPlatformDialog();
                        } else {
                            checkPermission();
                        }
                    } else if (ApplyStatus.APPLYING == applyStatus()) {//???????????????
                        showCanclePlatformDialog();
                    }
                }
                break;
            case R.id.down_platform_iv: //?????????
                showDownPlatformDialog();
                break;
            case R.id.video_switch_iv: //???????????????
                switchVideo();
                break;
            case R.id.audio_switch_iv: //??????
                switchAudio();
                break;
            case R.id.activity_small_refresh_iv:
                mVideoMap.clear();
                if (videoAdapter != null) {
                    videoAdapter.clear();
                }
                reload();
                break;
            case R.id.chat_iv:
                showChatPop();
                break;
            case R.id.activity_small_back_rl:
                showExitDialog();
                break;
            case R.id.ppt_container:  //??????????????????
                if (isTitleBarShow) {
                    hideController();
                } else {
                    showController();
                }
                break;
            case R.id.sector_layout_color_cv:  //?????????-??????
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mColorViewPopManager.show(strokeCV);
                break;
            case R.id.sector_layout_cmd_iv:  //?????????-??????
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mDrawAndStrokePopManager.setShowType(DrawAndStrokePopManager.CMD_TYPE);
                mDrawAndStrokePopManager.show(strokeCV);
                break;
            case R.id.sector_layout_stroke_cv:  //?????????-??????
                if (eraserIsSelect) {
                    resetDrawCmd();
                }
                mDrawAndStrokePopManager.setShowType(DrawAndStrokePopManager.STROKE_TYPE);
                mDrawAndStrokePopManager.show(strokeCV);
                break;
            case R.id.sector_layout_eraser_iv:  //?????????-?????????
                eraserIsSelect = !eraserIsSelect;
                eraserIV.setSelected(eraserIsSelect);
                mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                break;
         /*   case R.id.log_tv:
                if (logPopManager != null) {
                    if (logPopManager.isShow()) {
                        logPopManager.dismiss();
                    } else {
                        logPopManager.show(chatIV);
                    }
                }
                break;*/
        }
//        if (v.getId() != R.id.ppt_container) {//??????????????????????????????titlebar????????????
//            stopDismissTitleBar();
//        }
    }

    private void switchAudio() {
        if (isCloseAudioForZhubo) {
            showToast("??????????????????????????????????????????");
            return;
        }
        if (mRtcOperatorProxy != null) {
            if (audioSwitchIV.isSelected()) {
                mRtcOperatorProxy.closeAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        setAudioSwitch(false);
                        updateVideoLayout(result, VideoAdapter.AUDIO);
                    }

                    @Override
                    public void failed(String failed) {
                        showToast(failed);
                    }
                });
            } else {
                mRtcOperatorProxy.openAudio(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        setAudioSwitch(true);
                        updateVideoLayout(result, VideoAdapter.AUDIO);
                    }

                    @Override
                    public void failed(String failed) {
                        showToast(failed);
                    }
                });
            }
        }

    }

    private void switchVideo() {
        if (isCloseVideoForZhubo) {
            showToast("?????????????????????????????????????????????");
            return;
        }
        if (mRtcOperatorProxy != null) {
            if (videoSwitchIV.isSelected()) {//???????????????
                mRtcOperatorProxy.closeVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        setVideoSwitch(false);
                        updateVideoLayout(result, VideoAdapter.VIDEO);
                    }

                    @Override
                    public void failed(String failed) {
                        showToast(failed);
                    }
                });
            } else {//???????????????
                mRtcOperatorProxy.openVideo(new Callback<RtcUserEntity>() {
                    @Override
                    public void success(RtcUserEntity result) {
                        setVideoSwitch(true);
                        updateVideoLayout(result, VideoAdapter.VIDEO);
                    }

                    @Override
                    public void failed(String failed) {
                        showToast(failed);
                    }
                });
            }

        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    private void resetDrawCmd() {
        if (mDrawAndStrokePopManager != null) {
            mDrawAndStrokePopManager.setEraser(false);
        }
        if (eraserIV != null) {
            eraserIV.setSelected(false);
        }
        eraserIsSelect = false;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private int applyStatus() {
        return mHtSdk != null && mHtSdk.getRtcInfo() == null ? ApplyStatus.NO_APPLY : mHtSdk.getRtcInfo().userApplyStatus;
    }

    /**
     * ?????????????????????
     */
    private void showChatPop() {
        if (chatPopManager != null) {
            if (chatPopManager.isShow()) {
                chatPopManager.dismiss();
            } else {
                if (qBadgeView != null) {
                    qBadgeView.setBadgeNumber(0);
                }
                chatPopManager.show(pptLayout);
//                stopDismissTitleBar();
            }
        }


    }

    private boolean isOpen;

    //------------------------------------------------------------------------------------------------------
    private OnRtcStatusListener mOnRtcStatusListener = new OnRtcStatusListener() {
        /**
         * rtc??????
         */
        @Override
        public void onOpen() {
            isOpen = true;
            int status = applyStatus();
            if (ApplyStatus.APPLYING == (status)) {
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.applying_platform));
                setPlatformStatus(PlatformType.APPLYING);
            } else if (ApplyStatus.ALLOW == (status)) {
            } else {
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.start_platform));
                setPlatformStatus(PlatformType.START);
            }
        }

        /**
         * rtc??????
         */
        @Override
        public void onClose() {
            isOpen = false;
            showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.close_platform));
            closePlatform();
            setPlatformStatus(PlatformType.CLOSE);

        }

        @Override
        public void onConnectionInterrupted() {
            if (mVideoMap != null) {
                for (VideoStatusData videoStatusData : mVideoMap.values()) {
                    videoStatusData.setVideoOfflineStatus(1);
                    videoAdapter.updateItemOfPart(VideoAdapter.VIDEOLOADING, videoStatusData);
                }
            }
        }

        @Override
        public void onReConnectSuccess() {
            for (VideoStatusData videoStatusData : mVideoMap.values()) {
                videoStatusData.setVideoOfflineStatus(0);
                videoAdapter.updateItemOfPart(VideoAdapter.VIDEOLOADING, videoStatusData);
            }
        }

    };
    private OnRtcMemberListener mOnRtcMemberListener = new OnRtcMemberImpl() {
        /**
         * ?????????????????????
         * @param rtcUserEntity
         */
        @Override
        public void onKick(RtcUserEntity rtcUserEntity) {
            if (!mVideoMap.containsKey(rtcUserEntity.getXid())) {
                return;
            }
            VideoStatusData videoStatusData = mVideoMap.remove(rtcUserEntity.getXid());
            videoAdapter.removeItem(videoStatusData);
            if (rtcUserEntity.isMe()) {
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.kick_platform));
                setPlatformStatus(PlatformType.KICK);
            }
        }

        /**
         * ?????????????????????????????????
         *
         * @param rtcUserEntity
         * @param videoView
         */
        @Override
        public void onUp(RtcUserEntity rtcUserEntity, View videoView) {
            addUpData(rtcUserEntity, videoView);
            if (rtcUserEntity.isMe()) {
//                AlertDialogFactory.dismiss();
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.up_platform));
                setVideoSwitch(rtcUserEntity.isVideoOpen());
                setAudioSwitch(rtcUserEntity.isAudioOpen());
                setIsCloseVideoForZhubo(rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO);
                setIsCloseAudioForZhubo(rtcUserEntity.getAudio() == MediaStatus.CLOSE_FOR_ZHUBO);
                setPlatformStatus(PlatformType.ALLOW);
            }
        }

        /**
         * ???????????????
         * @param rtcUserEntity
         */
        @Override
        public void onDown(RtcUserEntity rtcUserEntity) {
            videoAdapter.removeItem(mVideoMap.remove(rtcUserEntity.getXid()));
            if (rtcUserEntity.isMe()) {
                setPlatformStatus(PlatformType.DOWN);
            }
        }

        /**
         *
         * @param rtcUserEntity
         * @param reason
         */
        @Override
        public void onOffline(RtcUserEntity rtcUserEntity, int reason) {
            if (mVideoMap != null && mVideoMap.containsKey(rtcUserEntity.getXid())) {
                VideoStatusData mVideoStatusData = mVideoMap.remove(rtcUserEntity.getXid());
                videoAdapter.removeItem(mVideoStatusData);
            }
        }
    };
    private OnRtcErrorListener mOnRtcErrorListener = new OnRtcErrorListener() {
        @Override
        public void onError(int code, String msg) {
            showToast(msg);
        }
    };

    private synchronized void addUpData(RtcUserEntity rtcUserEntity, View view) {
        VideoStatusData videoStatusData = new VideoStatusData(rtcUserEntity, view);
        mVideoMap.put(rtcUserEntity.getXid(), videoStatusData);

        if (rtcUserEntity.isMe()) {
            if (mVideoMap.size() > 1) {
                videoAdapter.addItem(1, videoStatusData);
            } else {
                videoAdapter.addItem(videoStatusData);
            }
        } else if (MemberRole.MEMBER_ROLE_SUPER_ADMIN == rtcUserEntity.getRole()) {
            spidId = rtcUserEntity.getXid();
            videoAdapter.addItem(0, videoStatusData);
        } else {
            videoAdapter.addItem(videoStatusData);
        }
    }

//    @Override
//    public void requestPermissionsSuccess() {
//        hasChecked = true;
//        showApplyPlatformDialog();
//    }
//
//    @Override
//    public void requestPermissionsFailed() {
//        hasChecked = false;
//        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), "", getResources().getString(R.string.no_permission), new AlertDialogFragment.AlertDialogListener() {
//            @Override
//            public void onConfirm() {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.fromParts("package", getPackageName(), null));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }

    /**
     * ??????????????????????????????
     */
    class OnPlayVideoChangeLister implements OnVideoChangeListener {
        /**
         * ??????????????????
         *
         * @param mode ???????????????VideoModeType.CAMERA_MODE????????????????????????;VideoModeType.DESKTOP_MODE???????????????????????????)
         */
        @Override
        public void onVideoStart(int mode) {
        }

        /**
         * ??????????????????
         *
         * @param mode ???????????????VideoModeType.CAMERA_MODE????????????????????????;VideoModeType.DESKTOP_MODE???????????????????????????)
         */
        @Override
        public void onVideoStop(int mode) {
        }

        /**
         * ??????????????????????????????
         *
         * @param beforeMode  ???????????????
         * @param currentMode ???????????????
         *                    ???????????????VideoModeType.CAMERA_MODE????????????????????????;VideoModeType.DESKTOP_MODE???????????????????????????)
         */
        @Override
        public void onVideoModeChanging(int beforeMode, int currentMode) {
            if (mSwitchModePopManager == null) {
                mSwitchModePopManager = new SwitchModePopManager(LiveRtcNativeActivity.this);
            }
            removeHandler();
            platformIVStatus(currentMode);
            mSwitchModePopManager.setMode(currentMode);
            mSwitchModePopManager.show(base_container);
            delayDismiss();
        }

      /*  private void platformIVStatus(int currentMode) {
            if (currentMode == VideoModeType.DESKTOP_MODE) {
                platformIV.setVisibility(View.GONE);
                downPlatformIV.setVisibility(View.GONE);
            } else {
                if (applyStatus() == ApplyStatus.ALLOW) {
                    platformIV.setVisibility(View.GONE);
                    downPlatformIV.setVisibility(View.VISIBLE);
                } else if (applyStatus() == ApplyStatus.APPLYING) {
                    platformIV.setVisibility(View.VISIBLE);
                    downPlatformIV.setVisibility(View.GONE);
                } else {
                    platformIV.setVisibility(isOpen ? View.VISIBLE : View.GONE);
                    downPlatformIV.setVisibility(View.GONE);
                }
            }
        }
*/

        /**
         * ??????????????????
         */
        @Override
        public void onVideoModeChanged() {
//            if (mSwitchModePopManager != null) {
//                mSwitchModePopManager.dismiss();
//            }
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onCameraShow() {
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onCameraHide() {
        }

    }

    private void delayDismiss() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(0, 1200);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && mSwitchModePopManager != null) {
                mSwitchModePopManager.dismiss();
            }
        }
    };

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void platformIVStatus(int currentMode) {
        RtcInfo rtcInfo = mHtSdk.getRtcInfo();
        if (rtcInfo.autoUp == 1) {
            platformIV.setVisibility(View.GONE);
            downPlatformIV.setVisibility(View.GONE);
            return;
        }
        if (currentMode == VideoModeType.DESKTOP_MODE) {
            platformIV.setVisibility(View.GONE);
            downPlatformIV.setVisibility(View.GONE);
        } else {
            if (applyStatus() == ApplyStatus.ALLOW) {
                platformIV.setVisibility(View.GONE);
                downPlatformIV.setVisibility(View.VISIBLE);
            } else if (applyStatus() == ApplyStatus.APPLYING) {
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
            } else {
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
            }
        }
    }

    private OnRtcMediaStatusListener onRtcMediaStatusListener = new OnRtcMediaStatusListener() {
        @Override
        public void onVideoClose(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.VIDEO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.close_video));
                setVideoSwitch(false);
                isCloseVideoForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
            }

        }

        @Override
        public void onVideoOpen(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.VIDEO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.open_video));
                setVideoSwitch(true);
                isCloseVideoForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
            }
        }

        @Override
        public void onAudioOpen(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.AUDIO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                setAudioSwitch(true);
                isCloseAudioForZhubo = rtcUserEntity.getVideo() == MediaStatus.CLOSE_FOR_ZHUBO;
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.open_audio));
            }
        }

        @Override
        public void onAudioClose(RtcUserEntity rtcUserEntity) {
            updateVideoLayout(VideoAdapter.AUDIO, rtcUserEntity);
            if (rtcUserEntity.isMe()) {
                setAudioSwitch(false);
                isCloseAudioForZhubo = rtcUserEntity.getAudio() == MediaStatus.CLOSE_FOR_ZHUBO;
                showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.close_audio));
            }
        }
    };

    /**
     * ?????????????????????
     */
    private OnWhiteboardPowerListener onWhiteboardPowerListenter = new OnWhiteboardPowerListener() {
        @Override
        public void onDrawEnable(RtcUserEntity rtcUserEntity) {
            receiveDrawPower(rtcUserEntity);
        }

        @Override
        public void onDrawDisable(RtcUserEntity rtcUserEntity) {
            receiveDrawPower(rtcUserEntity);
        }
    };


    /**
     * ??????????????????
     *
     * @param rtcUserEntity
     */

    private void receiveDrawPower(RtcUserEntity rtcUserEntity) {
        updateVideoLayout(VideoAdapter.PAINT, rtcUserEntity);
        if (rtcUserEntity.isMe()) {
            isDrawPower = rtcUserEntity.getDrawPower() == DrawPowerStatus.OPEN;
            showToast(isDrawPower ? this.getResources().getString(R.string.use_paint) : this.getResources().getString(R.string.no_use_paint));
//            ToastUtil.show(LiveRtcNativeActivity.this, isDrawPower ? this.getResources().getString(R.string.use_paint) : this.getResources().getString(R.string.no_use_paint));
            setPaintSLVisibility(isDrawPower ? View.VISIBLE : View.GONE);
            if (isDrawPower) {
                paintSL.open();
                paintIV.setRotation(0);
                gestureLayoutWrapper.setMaskEnable(false);
            }
            dynamicLayout(isDrawPower);
            initPop(isDrawPower);
        } else if (isDrawPower) {
            isDrawPower = false;
            setPaintSLVisibility(isDrawPower ? View.VISIBLE : View.GONE);
            dynamicLayout(isDrawPower);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param isDraw
     */
    private void initPop(boolean isDraw) {
        if (isDraw) {
            if (mColorViewPopManager == null) {
                mColorViewPopManager = new ColorViewPopManager(this);
            }
            if (mDrawAndStrokePopManager == null) {
                mDrawAndStrokePopManager = new DrawAndStrokePopManager(this);
                if (eraserIsSelect) {
                    mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                }
            }
            if (isReload) {
                mColorViewPopManager.setColor();
                mDrawAndStrokePopManager.setDrawTypeAndStroke();
                if (eraserIsSelect) {
                    mDrawAndStrokePopManager.setEraser(eraserIsSelect);
                }
            }
//            stopDismissTitleBar();
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void closePlatform() {
        if (mVideoMap == null || mVideoMap.size() <= 1) {
            return;
        }
        VideoStatusData videoStatusData = mVideoMap.get(spidId);
        mVideoMap.clear();
        mVideoMap.put(spidId, videoStatusData);
        videoAdapter.close(videoStatusData);
    }

    @Override
    public void onLaunch() {
        if (mHtSdk != null && LiveStatus.STOP.equals(mHtSdk.getInitLiveStatus())) {
            isReload = false;
            reset();
            hideController();
        }
    }

    @Override
    public void onInitFail(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), (dialog, which) -> {
            dialog.dismiss();
            LiveRtcNativeActivity.this.finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onLiveStart() {
        isLiving = true;
        showController();
        cuurentPlayTotalTimeTV.setVisibility(View.VISIBLE);
        videoContainerRV.setVisibility(View.VISIBLE);
        chatIV.setVisibility(View.VISIBLE);
        mRtcOperatorProxy = mHtSdk.getRtcOperatorProxy();
        setTitle();
    }

    /**
     * ????????????
     */
    private void setTitle() {
        String title = mHtSdk.getRoomInfo().getLiveTitle();
        titleTV.setVisibility(View.VISIBLE);
        titleTV.setText(title);

    }

    @Override
    public void onLiveStop() {
        isReload = false;
        cuurentPlayTotalTimeTV.setText("");
        cuurentPlayTotalTimeTV.setVisibility(View.GONE);
        reset();
        hideController();

    }

    @Override
    public void memberForceout() {
        String reason = getResources().getString(R.string.member_forceout);
        memberOut(reason);
    }

    @Override
    public void memberKick() {
        String reason = getResources().getString(R.string.member_kick);
        memberOut(reason);
    }


    /**
     * ??????video?????????????????????????????????
     */
    private void setIsCloseVideoForZhubo(boolean isCloseVideoForZhubo) {
        this.isCloseVideoForZhubo = isCloseVideoForZhubo;
    }

    /**
     * ??????audio?????????????????????????????????
     */
    private void setIsCloseAudioForZhubo(boolean isCloseAudioForZhubo) {
        this.isCloseAudioForZhubo = isCloseAudioForZhubo;
    }


    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param result
     * @param type
     */
    private void updateVideoLayout(RtcUserEntity result, int type) {
        if (mVideoMap != null && result != null && mVideoMap.containsKey(result.getXid())) {
            VideoStatusData videoStatusData = mVideoMap.get(result.getXid());
            if (videoStatusData == null) {
                return;
            }
//            if (type == VideoAdapter.VIDEO) {
//                videoStatusData.getRtcUserEntity().setVideo(job);
//            } else {
//                videoStatusData.getRtcUserEntity().setAudio(job);
//            }
            videoAdapter.updateItemOfPart(type, videoStatusData);
        }
    }

    /**
     * ??????????????????
     *
     * @param type
     * @param rtcUserEntity
     */
    private void updateVideoLayout(int type, RtcUserEntity rtcUserEntity) {
        if (rtcUserEntity != null && mVideoMap != null && mVideoMap.containsKey(rtcUserEntity.getXid())) {
            VideoStatusData videoStatusData = mVideoMap.get(rtcUserEntity.getXid());
            if (videoStatusData == null || videoAdapter == null)
                return;
            videoAdapter.updateItemOfPart(type, videoStatusData);
        }
    }


    /**
     * ????????????????????????
     *
     * @param platformStatus
     */
    private void setPlatformStatus(String platformStatus) {
        RtcInfo rtcInfo = mHtSdk.getRtcInfo();
        if (rtcInfo.autoUp == 1) {
            platformIV.setVisibility(View.GONE);
            downPlatformIV.setVisibility(View.GONE);
            return;
        }
        switch (platformStatus) {
            case PlatformType.START://???????????????
                platformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                downPlatformIV.setVisibility(View.GONE);
                controllContainer.setVisibility(View.VISIBLE);
//                stopDismissTitleBar();
                break;
            case PlatformType.APPLYING://?????????
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setIsRotate(true);
                break;
            case PlatformType.ALLOW://???????????????
                downPlatformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                platformIV.setVisibility(View.GONE);
                break;
            case PlatformType.CLOSE://????????????
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setIsRotate(false);
                platformIV.setVisibility(View.GONE);
                setVideoVisibility(View.GONE);
                setAudioVisibility(View.GONE);
                setPaintSLVisibility(View.GONE);
                dynamicLayout(false);
                isDrawPower = false;
                break;
            case PlatformType.CANCLE://???????????????
                downPlatformIV.setVisibility(View.GONE);
                platformIV.setVisibility(View.VISIBLE);
                platformIV.setIsRotate(false);
                break;
            case PlatformType.KICK://????????????
            case PlatformType.DOWN://???????????????
                platformIV.setVisibility(View.VISIBLE);
                downPlatformIV.setVisibility(View.GONE);
                setVideoVisibility(View.GONE);
                setAudioVisibility(View.GONE);
                setPaintSLVisibility(View.GONE);
                dynamicLayout(false);
                eraserIsSelect = false;
                setDefault();
                break;
            default:
                break;
        }
    }

    /**
     * Event??????
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCallback(Event message) {
        switch (message.getType()) {
            case EventType.SMALL_ROOM_POP_COLOR:
                colorCV.setColor((Integer) message.getData());
                break;
            case EventType.SMALL_ROOM_POP_CMD:
                cmdIV.setBackgroundResource((Integer) message.getData());
                break;
            case EventType.SMALL_ROOM_POP_STROKE:
                strokeCV.setCheckedRingWidthPercent((float) message.getData());
                break;
            case EventType.NETWORK_STATE_CHANGE://????????????
                int netStatus = (int) message.getData();
                network(netStatus);
                break;
        }
    }


    /**
     * ????????????????????????
     */
    private void setVideoSwitch(boolean enable) {
        setVideoVisibility(View.VISIBLE);
        videoSwitchIV.setSelected(enable);
    }

    /**
     * ????????????????????????
     */
    private void setAudioSwitch(boolean enable) {
        setAudioVisibility(View.VISIBLE);
        audioSwitchIV.setSelected(enable);
    }

    /**
     * ????????????????????????
     */
    private void setVideoVisibility(int visibility) {
        if (videoSwitchIV != null) {
            videoSwitchIV.setVisibility(visibility);
        }
    }

    /**
     * ????????????????????????
     */
    private void setAudioVisibility(int visibility) {
        if (audioSwitchIV != null) {
            audioSwitchIV.setVisibility(visibility);
        }

    }

    /**
     * ??????????????????
     *
     * @param visibility
     */
    private void setPaintSLVisibility(int visibility) {
        paintSL.setVisibility(visibility);
    }

    /**
     * ??????????????????????????? ????????????????????????
     *
     * @param isDraw
     */
    private void dynamicLayout(boolean isDraw) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (isDraw && !DimensionUtils.isPad(this)) {
            params.addRule(RelativeLayout.ABOVE, R.id.paint_sl);
        } else {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        controllContainer.setLayoutParams(params);
    }


    /**
     * ????????????????????????
     */
    private void downPlatform() {
        if (mRtcOperatorProxy != null) {
            mRtcOperatorProxy.down(new Callback<RtcUserEntity>() {
                @Override
                public void success(RtcUserEntity result) {
                    if (result != null) {
                        VideoStatusData videoStatusData = mVideoMap.remove(result.getXid());
                        if (videoStatusData != null) {
                            videoAdapter.removeItem(videoStatusData);
                        }
                    }
                    setPlatformStatus(PlatformType.DOWN);
//                    ToastUtil.show(LiveRtcNativeActivity.this, LiveRtcNativeActivity.this.getResources().getString(R.string.down_platform));
                    showToast(LiveRtcNativeActivity.this.getResources().getString(R.string.down_platform));
                }

                @Override
                public void failed(String failed) {
//                    ToastUtil.show(LiveRtcNativeActivity.this, failed);
                    showToast(failed);
                }
            });

        }
    }

    /**
     * ???????????????
     */
    private void applyPlatform() {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.apply(new Callback<String>() {
            @Override
            public void success(String result) {
                setPlatformStatus(PlatformType.APPLYING);
//                ToastUtil.show(LiveRtcNativeActivity.this, "???????????????");
                showToast(getString(R.string.apply_platform));
            }

            @Override
            public void failed(String failed) {
//                ToastUtil.show(LiveRtcNativeActivity.this, failed);
                showToast(failed);
            }
        });
    }

    /**
     * ???????????????
     */
    private void canclePlatform() {
        if (mRtcOperatorProxy == null) {
            return;
        }
        mRtcOperatorProxy.cancel(new Callback() {
            @Override
            public void success(Object result) {
                setPlatformStatus(PlatformType.CANCLE);
//                ToastUtil.show(LiveRtcNativeActivity.this, LiveRtcNativeActivity.this.getResources().getString(R.string.cancle_platform));
                showToast(getString(R.string.cancle_platform));
            }

            @Override
            public void failed(String failed) {
//                ToastUtil.show(LiveRtcNativeActivity.this, failed);
                showToast(failed);
            }
        });
    }


    private ScheduledExecutorService lance;
    private boolean isTitleBarShow = false;

    /**
     * ??????????????????????????????
     */
//    private void showTitleBarAndOprationBar() {
//        if (lance != null && !lance.isShutdown())
//            lance.shutdown();
//        showController();
//        autoDismissTitleBar();
//    }
    private void showController() {
        if (isLiving) {
            controllContainer.setVisibility(View.VISIBLE);
        }
        titlebarContainer.setVisibility(View.VISIBLE);
        if (isDrawPower) {
            paintSL.setVisibility(View.VISIBLE);
        }
        isTitleBarShow = true;
    }

    private void hideController() {
        controllContainer.setVisibility(View.GONE);
        titlebarContainer.setVisibility(View.GONE);
        paintSL.setVisibility(View.GONE);
        isTitleBarShow = false;
    }

    //??????????????????. 3??????????????????
    private void autoDismissTitleBar() {
        Runnable sendBeatRunnable = () -> {
            if (isTitleBarShow) {
                if (lance != null && !lance.isShutdown()) {
                    runOnUiThread(() -> {
                        if (isTitleBarShow) {
                            hideController();
                        }
                    });
                }
            }
        };

        lance = Executors.newSingleThreadScheduledExecutor();
        lance.scheduleAtFixedRate(sendBeatRunnable, 5, 5, TimeUnit.SECONDS);
    }

    private void stopDismissTitleBar() {
        if (lance != null) {
            if (!lance.isShutdown()) {
                lance.shutdown();
            }
            lance = null;
        }
    }

    /**
     * @param totalTime ???????????????
     */
    @Override
    public void onTime(int totalTime) {
        if (cuurentPlayTotalTimeTV != null) {
            cuurentPlayTotalTimeTV.setText(TimeUtil.displayHHMMSS(totalTime));
        }
    }

    //------------------------------------????????????------------------------------------------------------------
    @Override
    protected void onResume() {
        isFront = true;
        super.onResume();
        mHtSdk.onResume();
        registerNetWorkStateReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
        HtSdk.getInstance().onPause();
        unRegisterNetWorkStateReceiver();

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        EventBusUtil.unregister(this);
//        stopDismissTitleBar();
        ToastUtil.cancel();
        mHtSdk.release();
        mRtcOperatorProxy = null;
        if (isDubug) {
            TalkFunLogger.removeListener();
        }
        mVideoMap.clear();
        removeHandler();
        mOnRtcStatusListener = null;
        mOnRtcMemberListener = null;
        onRtcMediaStatusListener = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }


    //-------------------------------------------------????????????-----------------------------------------------------------------------
    private boolean isNetworkAvailable = true;
    private long noNetWorkTime = 0;

    /**
     * ????????????
     *
     * @param netStatus
     */
    private void network(int netStatus) {
        if (netStatus == NetMonitor.NETWORK_NONE) {
            isNetworkAvailable = false;
            noNetWorkTime = System.currentTimeMillis();
        } else {
            if (!isNetworkAvailable && System.currentTimeMillis() - noNetWorkTime > 2 * 1000) {//????????????2??????????????????
                if (socketFailTips != null && socketFailTips.isShowing()) {
                    socketFailTips.dismiss();
                }
                reload();
            }
            isNetworkAvailable = true;
        }
        badNetStatusLL.setVisibility(netStatus == NetMonitor.NETWORK_NONE ? View.VISIBLE : View.GONE);
        if (netStatus == NetMonitor.NETWORK_MOBILE) {//4G ??????
            AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.mobile_connect), null);
        }
    }

    private void reload() {
        if (isDrawPower) {
            isReload = true;
        }
        reset();
        hideController();
        if (mHtSdk != null) {
            mHtSdk.reload();
        }
    }

    protected void registerNetWorkStateReceiver() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netWorkStateReceiver, intentFilter);
    }

    protected void unRegisterNetWorkStateReceiver() {
        if (netWorkStateReceiver == null)
            return;
        this.unregisterReceiver(netWorkStateReceiver);
    }
    //--------------------------------------------????????????--------------------------------------------------------------------------

    /**
     * ??????????????????
     */
    private void showDownPlatformDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.down_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> downPlatform());
    }

    /**
     * ??????????????????
     */
    private void showCanclePlatformDialog() {

        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.cancle_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> canclePlatform());
    }

    /**
     * ???????????????
     */
    private void showApplyPlatformDialog() {
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.apply_platform_tip),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> applyPlatform());
    }


    /**
     * ???????????????
     */
    private void showExitDialog() {
        RtcInfo rtcInfo = mHtSdk.getRtcInfo();
        if (isNetworkAvailable && applyStatus() == ApplyStatus.ALLOW && rtcInfo.autoUp != 1) {
            AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit_before_tip),
                    getResources().getString(R.string.confirm), null, null);
            return;
        }
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit),
                getResources().getString(R.string.confirm), getResources().getString(R.string.cancel),
                () -> {
//                        stopDismissTitleBar();
                    finish();
                });
    }

    //????????????
    private void memberOut(String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(reason);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), (dialog, which) -> {
            dialog.dismiss();
            LiveRtcNativeActivity.this.finish();
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * ?????????????????????Toast
     *
     * @param msg
     */
    private void showToast(String msg) {
        if (!isFront) {
            return;
        }
        ToastUtil.show(this, msg, videoWidth / 2);
    }

    private void reset() {
        setDefault();
        isLiving = false;
        isOpen = false;
        mVideoMap.clear();
        videoAdapter.clear();
        videoContainerRV.removeAllViews();
        videoContainerRV.setVisibility(View.GONE);

        if (chatPopManager != null && chatPopManager.isShow()) {
            chatPopManager.dismiss();
        }
        setPlatformStatus(PlatformType.CLOSE);
    }

    private void setDefault() {
        isDrawPower = false;
        isCloseAudioForZhubo = false;
        isCloseVideoForZhubo = false;
    }

    private static final int PERMISSION_ALL = 1; //???????????????

    private void checkPermission() {
        /**
         * 6.0??????????????????????????????????????????????????????????????????????????????*/
        final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO};
        MPermissions.requestPermissions(this, PERMISSION_ALL, PERMISSIONS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // @PermissionGrant(PERMISSION_ALL)
    public void requestPermissionsSuccess() {
        hasChecked = true;
        showApplyPlatformDialog();
//        LogUtil.d(this.getPackageName(),"requestPermissionsSuccess");
    }

    // @PermissionDenied(PERMISSION_ALL)
    public void requestPermissionsFailed() {
        hasChecked = false;
        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), "", getResources().getString(R.string.no_permission), new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}