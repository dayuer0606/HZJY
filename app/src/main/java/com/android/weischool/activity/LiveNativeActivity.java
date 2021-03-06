package com.android.weischool.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.weischool.R;
import com.android.weischool.consts.EventType;
import com.android.weischool.dialog.AlertDialogFactory;
import com.android.weischool.dialog.ScoreDialogFragment;
import com.android.weischool.entity.ChatDisableAllStatusEntity;
import com.android.weischool.entity.Event;
import com.android.weischool.entity.SignEndEntity;
import com.android.weischool.entity.SignEntity;
import com.android.weischool.event.OnSendFlowerListener;
import com.android.weischool.event.OnSendMessageListener;
import com.android.weischool.event.SimpleGestureLayoutListener;
import com.android.weischool.helper.LiveLotteryDialogHelper;
import com.android.weischool.helper.LiveNetHelper;
import com.android.weischool.helper.LiveRollHelper;
import com.android.weischool.helper.LiveSignDialogHelper;
import com.android.weischool.helper.LiveVoteDialogHelper;
import com.android.weischool.helper.NetChoiseDiologHelper;
import com.android.weischool.net.NetMonitor;
import com.android.weischool.util.DanmakuFlameUtil;
import com.android.weischool.util.DimensionUtils;
import com.android.weischool.util.JsonUtil;
import com.android.weischool.util.ScreenSwitchUtils;
import com.android.weischool.util.SharedPreferencesUtil;
import com.android.weischool.util.SoftKeyboardStateWatcher;
import com.android.weischool.util.StringUtils;
import com.android.weischool.view.FullScreenInputBarView;
import com.android.weischool.view.GuideDialog;
import com.android.weischool.view.LiveMessageView;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.consts.BroadcastCmdType;
import com.talkfun.sdk.consts.LiveStatus;
import com.talkfun.sdk.consts.PlayerLoadState;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.HtBroadcastListener;
import com.talkfun.sdk.event.HtDispatchFlowerListener;
import com.talkfun.sdk.event.HtDispatchRoomMemberNumListener;
import com.talkfun.sdk.event.LiveInListener;
import com.talkfun.sdk.event.OnSocketConnectListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.event.VideoConnectListener;
import com.talkfun.sdk.module.BroadcastEntity;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.ModuleConfigHelper;
import com.talkfun.sdk.module.RoomInfo;
import com.talkfun.sdk.module.VideoModeType;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;
import com.talkfun.utils.PreventRepeatedUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.socket.emitter.Emitter.Listener;
import master.flame.danmaku.ui.widget.DanmakuView;

public class LiveNativeActivity extends BasePlayActivity implements
        LiveInListener, HtDispatchRoomMemberNumListener, HtDispatchFlowerListener, HtBroadcastListener, VideoConnectListener, OnSendMessageListener,
        View.OnTouchListener, OnSendFlowerListener, LiveMessageView.IPageChange {
    private final String TAG = LiveNativeActivity.class.getName();
    //?????????????????????
    @BindView(R.id.tab_container)
    LiveMessageView mLiveMessageView;

    @BindView(R.id.change_tip)
    TextView changeTip;
    @BindView(R.id.video_visibility_iv)
    ImageView videoVisibleIv;
    @BindView(R.id.iv_danmu_switch)
    ImageView ivDanmuSwitch;

    @BindView(R.id.title_bar)
    RelativeLayout titlebarContainer;

    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;


    @BindView(R.id.network_choice_iv)
    ImageView ivNetWorkChoice;

    @BindView(R.id.operation_btn_container)
    RelativeLayout operationContainer;

    //????????? -??????
    @BindView(R.id.ll_input_fullScreen)
    FullScreenInputBarView fullScreenInputBarView;

    @BindView(R.id.fab_float_window)
    TextView memberFloatTV;
    /**
     * ????????????????????????,???????????????
     */
    @BindView(R.id.ll_bottom_menu)
    LinearLayout llBottomMenu;

    @BindView(R.id.exchange)
    ImageView ivExchange;


    //    @BindView(R.id.mongolia_layer)
//    FrameLayout mongoliaLayer;   //??????

    //------------------------------------------??????---------------------------------------------------
    private int currentMode;
    private DanmakuFlameUtil danmakuFlameUtil;  //??????
    private boolean isLiveStart = false;
    private RoomInfo roomInfo; //????????????
    private ModuleConfigHelper moduleConfigHelper; //?????????????????????
    private boolean isNeedToGuide = true;

    //-------------------------?????????--------------------------------------
    /**
     *
     */
    private HtSdk mHtSdk;
    /**
     * ????????????
     */
    private LiveNetHelper mNetCheckHelper;
    /**
     * ??????????????????
     */
    private NetChoiseDiologHelper mNetChoiseDiologHelper;
    /**
     * ????????????
     */
    private LiveRollHelper mRollHelper;

    /**
     * ??????
     */
    private LiveSignDialogHelper mLiveSignDialogHelper;

    private boolean chatEnable = true;//??????????????????????????????
    /**
     * ??????
     */
    private LiveVoteDialogHelper mLiveVoteDialogHelper;

    private boolean pptDisplay = true; //???????????????ppt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.playing_activity_layout;
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * ???????????????
     */
    protected void initView() {
        super.initView();
        initGuideLayout();
        initDanmaku();
        mHtSdk = HtSdk.getInstance();
//        mHtSdk.setLogEnable(true);
//        mHtSdk.setLogLevel(3);
        mHtSdk.init(pptContainer, videoViewContainer, mToken);
        /**??????????????????/??????????????????
         * ???????????????????????????????????????????????????PPT??????????????????????????????????????????????????????????????????
         * */
        mHtSdk.setDesktopVideoContainer(desktopVideoContainer);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View liveWaitView = layoutInflater.inflate(R.layout.live_wait_layout, null);
        View liveOverView = layoutInflater.inflate(R.layout.live_over_layout, null);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);

        mHtSdk.setLiveWaitView(liveWaitView);//???????????????????????????view
        mHtSdk.setLiveOverView(liveOverView);//????????????????????????view
        mHtSdk.setLoadingView(loadingView); //???????????????????????????????????????view
        mHtSdk.setLoadFailView(loadFailView);//???????????????????????????????????????view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));

        showVideoContainer(videoVisibleIv, false);
        //HtSdk.getInstance().setPauseInBackground(false);
        operationContainer.bringToFront();
        titlebarContainer.bringToFront();
        hideTitleBar();
        initHelper();
    }

    /**
     * ??????????????????
     */
    private void initHelper() {
        mNetCheckHelper = new LiveNetHelper(this);
        mNetChoiseDiologHelper = new NetChoiseDiologHelper(this);
        mRollHelper = new LiveRollHelper(this, pptLayout);
        mLiveSignDialogHelper = new LiveSignDialogHelper(LiveNativeActivity.this);

    }

    /**
     * ??????????????????
     */
    private void initDanmaku() {
        ivDanmuSwitch.setSelected(false);
        danmakuFlameUtil = new DanmakuFlameUtil(danmakuView);
        danmakuFlameUtil.hide();
    }

  /*  @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }*/

    /**
     * ???????????????
     */
    private void initGuideLayout() {
        //??????????????????
        isNeedToGuide = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.SP_LIVEROOM_GUIDE);
//        ScreenSwitchUtils.getInstance(this).isOpenSwitchAuto(true);
        if (isNeedToGuide) {
            GuideDialog mGuideDialog = new GuideDialog(this);
            mGuideDialog.show();
        }
    }

    private void initEvent() {
        /**????????????????????????*/
        mHtSdk.setLiveListener(this);

        /**????????????????????????????????????*/
        mHtSdk.setVideoConnectListener(this);
        /**????????????????????????????????????*/
        mHtSdk.setHtDispatchRoomMemberNumListener(this);
        mHtSdk.setHtBroadcastListener(this);
        /**
         * ??????????????????????????????
         * ?????????????????????????????????/?????????????????????????????????????????????????????????onVideoModeChanging???onVideoModeChanged??????
         * ??????????????????????????????????????????/?????????????????????????????????????????????onVideoStart???onVideoStop??????
         * ???????????????????????????????????????onCameraShow???onCamerahide??????
         *
         * */
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());

        videoViewContainer.setOnTouchListener(this);

        /***
         * ??????????????????
         */
        mHtSdk.setOnPlayerLoadStateChangeListener(loadState -> {
            if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_START) {
                Log.d(TAG, "????????????");
            } else if (loadState == PlayerLoadState.MEDIA_INFO_BUFFERING_END) {
                Log.d(TAG, "????????????");
            }
        });
        /**
         * ???????????????
         */
        mLiveMessageView.addIPageChangeListener(this);
        mLiveMessageView.initListener();
        //?????????????????????????????????vgInputLayout????????????????????????????????????
        vgInputLayout.post(new Runnable() {
            @Override
            public void run() {
                updateLayout();
            }
        });


        fullScreenInputBarView.setOnSendMessageListener(content -> {
            isLongShowTitleBar = false;
            if (mLiveMessageView != null) {
                mLiveMessageView.sendChatMessage(content);
            }
        });
        fullScreenInputBarView.setOnFocusChangeListener(isFocus -> {
            if (isFocus) {
                isLongShowTitleBar = true;

            } else {
                isLongShowTitleBar = false;
            }
        });

        SoftKeyboardStateWatcher stateWatcher = new SoftKeyboardStateWatcher(linearContainer);
        stateWatcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                if (vgInputLayout == null) {
                    return;
                }
                if (ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait()) {
                    //??????????????????????????????????????????????????????????????????????????????
                    int videoViewX = DimensionUtils.getScreenWidth(LiveNativeActivity.this) - videoViewContainer.getLayoutParams().width;
                    updateVideoPosition(videoViewX, 0);
                } else {
                    vgInputLayout.switchInputAreaLength(true);

                }

            }

            @Override
            public void onSoftKeyboardClosed() {
                if (vgInputLayout == null) {
                    return;
                }
                ScreenSwitchUtils screenSwitchUtils = ScreenSwitchUtils.getInstance(LiveNativeActivity.this);
                if (screenSwitchUtils.isPortrait()) {
                    //??????????????????????????????????????????????????????????????????????????????
                    FrameLayout.LayoutParams videoViewParams = (FrameLayout.LayoutParams) videoViewContainer.getLayoutParams();
                    int videoViewContainerX = DimensionUtils.getScreenWidth(LiveNativeActivity.this) - videoViewContainer.getLayoutParams().width;
                    int height = 3 * DimensionUtils.getScreenWidth(LiveNativeActivity.this) / 4;
                    videoViewParams.leftMargin = videoViewContainerX;
                    videoViewParams.topMargin = height + getVideoYOffset();
                    videoViewContainer.setLayoutParams(videoViewParams);

                } else {
                    vgInputLayout.switchInputAreaLength(false);
                }
            }
        });

        if (vgInputLayout != null) {
            vgInputLayout.setOnSendMessageListener(this);
            vgInputLayout.setOnSendFlowerListener(this);
        }

        /**??????????????????????????????*/
        mHtSdk.setHtDispatchFlowerListener(this);
        mLiveVoteDialogHelper = new LiveVoteDialogHelper(this);
        mLiveVoteDialogHelper.registerListener();

        LiveLotteryDialogHelper mLiveLotteryDialogHelper = new LiveLotteryDialogHelper(this);
        mLiveLotteryDialogHelper.registerListener();

        mLiveSignDialogHelper.setOnSignInCallBack(new Callback() {
            @Override
            public void success(Object result) {
                if (mLiveMessageView != null) {
                    mLiveMessageView.insertChatMessage(LiveNativeActivity.this.getResources().getString(R.string.ht_sign_in));
                }
            }

            @Override
            public void failed(String failed) {

            }
        });

        //???????????????????????????
        mHtSdk.on(BroadcastCmdType.SIGN_NEW, (Listener) args -> {
            if (args == null || args.length == 0)
                return;
            for (int i = 0; i < args.length; i++) {
                final JSONObject obj = (JSONObject) args[i];
                if (obj == null)
                    continue;
                final SignEntity signEntity = JsonUtil.transferSignEntiy(obj);
                //??????????????????
                if (mLiveSignDialogHelper != null) {
                    handler.post(() -> mLiveSignDialogHelper.signStart(signEntity));
                }
            }
        });


        //???????????????????????????
        mHtSdk.on(BroadcastCmdType.SIGN_END, (Listener) args -> {
            if (args == null || args.length == 0)
                return;
            for (int i = 0; i < args.length; i++) {
                final JSONObject obj = (JSONObject) args[i];
                if (obj == null)
                    continue;
                final SignEndEntity signEndEntity = JsonUtil.transferSignEndEntity(obj);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //??????????????????
                        if (mLiveSignDialogHelper != null) {
                            mLiveSignDialogHelper.signStop();
                        }
                        //?????????????????????
                        if (mLiveMessageView != null) {
                            mLiveMessageView.insertChatMessage(signEndEntity);
                        }
                    }
                });
            }
        });


        //??????????????????/??????????????????
        mHtSdk.on(BroadcastCmdType.CHAT_DISABLE_ALL, (Listener) args -> {
            if (args == null || args.length == 0)
                return;
            for (int i = 0; i < args.length; i++) {
                final JSONObject obj = (JSONObject) args[i];
                if (obj == null)
                    continue;
                final JSONObject argsObj = obj.optJSONObject("args");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ChatDisableAllStatusEntity entity = ChatDisableAllStatusEntity.objectFromData(argsObj.toString());
                        if (mLiveMessageView != null)
                            mLiveMessageView.insertChatMessage(entity);
                        chatEnable = !entity.isDisable();
                        if (mLiveMessageView != null && mLiveMessageView.getCurrentTab() == LiveMessageView.TAB_CHAT) {
                            setCanInput(chatEnable);
                        }
                    }
                });
            }
        });

        //socket????????????
        mHtSdk.addSocketConnectionListener(new OnSocketConnectListener() {
            @Override
            public void onConnect() {
                Log.d("onSocketConnection", "onConnect");
            }

            @Override
            public void onReconnecting() {
                Log.d("onSocketConnection", "onReconnecting");
            }

            @Override
            public void onReconnectFailed() {
                Log.d("onSocketConnection", "onReconnectFailed");
            }

            @Override
            public void onConnectFailed() {
                if (LiveNativeActivity.this.isFinishing()) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(LiveNativeActivity.this);
                builder.setMessage("socket??????????????????????????????????????????????????????????????????");
                builder.setTitle(R.string.tips);
                builder.setPositiveButton(R.string.refresh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HtSdk.getInstance().reload();
                    }
                }).setNegativeButton(R.string.goback, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LiveNativeActivity.this.finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            public void onConnectSuccess() {
                Log.d("onSocketConnection", "onConnectSuccess");
            }
        });

        gestureLayoutWrapper.setGestureLayoutListener(new SimpleGestureLayoutListener() {
            @Override
            public void onStartVolumeOffset() {
                audioManagerHelper.startVolumeOffset();
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
                    isLongShowTitleBar = false;
                    hideTitleBar();
                } else {
                    showTitleBar();
                }
                return true;
            }

            @Override
            public boolean onDoubleClick() {
                switchFullScreen();
                return true;
            }
        });

        mHtSdk.on(BroadcastCmdType.CHAT_DISABLE, (Listener) args -> {

        });


    }


    @OnClick({
            R.id.fullScreen_iv, R.id.iv_go_back, R.id.exchange,
            R.id.video_visibility_iv, R.id.network_choice_iv, R.id.iv_danmu_switch, R.id.iv_refresh})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.fullScreen_iv: //??????
                switchFullScreen();
                break;
            case R.id.video_visibility_iv: //???????????????
                onVideoVisible(videoVisibleIv);
                break;
            case R.id.iv_go_back:  //??????
                gobackAction();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    showInputLayout();
                }
                break;
            case R.id.network_choice_iv: //????????????????????????
                if (isLiveStart) {
                    if (PreventRepeatedUtil.canClickable("networkChoice")) {
                        if (mNetChoiseDiologHelper != null) {
                            mNetChoiseDiologHelper.showNetworkChoiceDialog();
                        }
                    }
                }

                break;
            case R.id.exchange: //?????????????????????
                if (isLiveStart && isVideoViewContainerVisiable()) {
                    //??????????????????.???????????????????????????????????????
                    HtSdk.getInstance().exchangeVideoAndWhiteboard();
                    isExchangeViewContainer = !isExchangeViewContainer;
                }
                break;
            case R.id.iv_danmu_switch:  //????????????
                boolean selected = ivDanmuSwitch.isSelected();
                ivDanmuSwitch.setSelected(!selected);
                if (!selected) {
                    danmakuFlameUtil.show();
                } else {
                    danmakuFlameUtil.hide();
                }
                break;
            case R.id.iv_refresh:
                if (!PreventRepeatedUtil.canClickable("refresh")) {
                    return;
                }
                if (mNetChoiseDiologHelper != null) {
                    mNetChoiseDiologHelper.resetSelectPosition();
                }
                exchangeViewContainer();
                HtSdk.getInstance().reload();
                break;
        }

    }

    @Override
    public void layoutChanged() {  //????????????
        super.layoutChanged();
        vgInputLayout.reset();
        fullScreenInputBarView.reset();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { //tip:????????????????????????????????????????????????
        super.onConfigurationChanged(newConfig);
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
        if (!ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isFullScreen()) { //????????????
            if (mLiveMessageView != null) {
                mLiveMessageView.pageChanged();
            }
        }
        layoutChanged();
        if (!DimensionUtils.isPad(LiveNativeActivity.this) && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (vgInputLayout != null) {
                vgInputLayout.setVisibility(View.GONE);
            }
        } else {
            if (mLiveMessageView != null) {
                if (!LiveMessageView.TAB_NOTIFY.equals(mLiveMessageView.getCurrentTab())) {
                    if (vgInputLayout != null) {
                        vgInputLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void switchFullScreen() {
        onFullScreenChange();
//        showInputLayout();
    }

    private void showInputLayout() {
        if (vgInputLayout == null) {
            return;
        }
        vgInputLayout.postDelayed(() -> {
            if (!DimensionUtils.isPad(LiveNativeActivity.this) && !ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait()) {
                if (vgInputLayout != null) {
                    vgInputLayout.setVisibility(View.GONE);
                }
            } else {
                if (mLiveMessageView != null) {
                    if (mLiveMessageView.getCurrentTab() != LiveMessageView.TAB_NOTIFY) {
                        if (vgInputLayout != null) {
                            vgInputLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        }, 100);
    }

    /**
     * ???????????????????????????
     *
     * @param isShow
     */
    @Override
    public void showFullScreenInput(boolean isShow) {
        if (moduleConfigHelper != null &&
                (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE) || !moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE_CHAT))
        ) {
            fullScreenInputBarView.setVisibility(View.INVISIBLE);
            return;
        }
        fullScreenInputBarView.setVisibility(isShow == true ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    protected void onVideoVisible(ImageView videoVisibleIv) {
        if (isLiveStart && currentMode != VideoModeType.DESKTOP_MODE) {
            super.onVideoVisible(videoVisibleIv);
        }
    }


    @Override
    void showController() {
        titlebarContainer.setVisibility(View.VISIBLE);
        operationContainer.setVisibility(View.VISIBLE);
    }

    @Override
    void hideController() {
        if (titlebarContainer == null)
            return;
        titlebarContainer.setVisibility(View.GONE);
        operationContainer.setVisibility(View.GONE);
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        setMemberFloatTVWH(width, screenHeight);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param screenWidth
     * @param screenHeight
     */
    private void setMemberFloatTVWH(final int screenWidth, final int screenHeight) {
        if (memberFloatTV == null) return;
        memberFloatTV.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                memberFloatTV.removeOnLayoutChangeListener(this);
                int width = memberFloatTV.getWidth();
                memberFloatTV.setX(screenWidth - width);
                memberFloatTV.setY(screenHeight * 4 / 5);

            }
        });

    }

    //TODO-------------------------------------------??????????????????----------------------------------------

    /**
     * SDK?????????????????????
     */
    @Override
    public void onLaunch() {
        if (mHtSdk != null) {
            if (LiveStatus.STOP.equals(mHtSdk.getInitLiveStatus())) {
                Log.i(TAG, "???????????????");
            } else {
                if (LiveStatus.START.equals(mHtSdk.getInitLiveStatus())) {
                    Log.i(TAG, "???????????????");
                } else if (LiveStatus.WAIT.equals(mHtSdk.getInitLiveStatus())) {
                    Log.i(TAG, "???????????????");
                }

                roomInfo = mHtSdk.getRoomInfo();
                mLiveMessageView.addRoomInfo(roomInfo);
                if (roomInfo != null) {
                    chatEnable = roomInfo.getDisableall() == 0;
                    setCanInput(chatEnable);

                    pptDisplay = roomInfo.isPptDisplay();
                    if (!pptDisplay) {
                        hideWhiteboard();
                    } else {
                        showWhiteboard();
                    }
                }
            }

            moduleConfigHelper = mHtSdk.getModuleConfigHelper();

            //?????????????????????????????????????????????????????????
            if (moduleConfigHelper != null &&
                    (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE) || !moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE_CHAT))
            ) {
                mLiveMessageView.hideChatFragment();
                showFullScreenInput(false);
            } else {
                mLiveMessageView.showChatFragment();
                updateLayout();
            }

            //?????????????????????????????????????????????????????????
            if (moduleConfigHelper != null &&
                    (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE) || !moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_LIVE_QA))
            ) {
                mLiveMessageView.hideQuestionFragment();
            } else {
                mLiveMessageView.showQuestionFragment();
            }

            pageChange(mLiveMessageView.getCurrentItem());

        }
        userVideoShow = true;
        showTitleBar();
    }

    /**
     * ??????ppt??????
     */
    private void hideWhiteboard() {
        mHtSdk.setVideoViewContainer(pptContainer);
        videoViewContainer.setVisibility(View.GONE);
        ivExchange.setVisibility(View.GONE);
        videoVisibleIv.setVisibility(View.GONE);
    }

    /**
     * ??????ppt??????
     */
    private void showWhiteboard() {
        mHtSdk.setVideoViewContainer(videoViewContainer);
        videoViewContainer.setVisibility(View.VISIBLE);
        ivExchange.setVisibility(View.VISIBLE);
        videoVisibleIv.setVisibility(View.VISIBLE);
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void onInitFail(String msg) {
        Log.d(TAG, "onInitFail: " + msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), (dialog, which) -> {
            dialog.dismiss();
            LiveNativeActivity.this.finish();
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    //????????????
    @Override
    public void onLiveStart() {
        if (isFinish)
            return;
        isLiveStart = true;
        if (mNetCheckHelper != null) {
            mNetCheckHelper.startCheckNetStatus(ivNetWorkChoice);
        }

        if (mHtSdk == null || (roomInfo = mHtSdk.getRoomInfo()) == null)
            return;

        if (mLiveMessageView != null) {
            //mLiveMessageView.clearChatAndQuestionMessage();
            mLiveMessageView.addRoomInfo(roomInfo);
            if (roomInfo.getNoticeEntity() != null) {
                mLiveMessageView.showNotice();
            }
        }
        if (roomInfo.getRollEntity() != null) {
            if (mRollHelper != null) {
                mRollHelper.receiveRollAnnounce(roomInfo.getRollEntity());
            }
        }

        //???????????????
        if (moduleConfigHelper != null && moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_THEFTPROOF)) {
            if (roomInfo.getUser() != null)
                startShowWatermark(roomInfo.getUser().getUid());
            else
                startShowWatermark(roomInfo.getRoomid());
        }

    }

    //????????????
    @Override
    public void onLiveStop() {
        isLiveStart = false;
        if (mNetCheckHelper != null) {
            mNetCheckHelper.stopCheckNetStatus();
        }
        if (mRollHelper != null) {
            mRollHelper.cancel();
        }
        stopShowWatermark();
      /*  //????????????item??????
        if (mNetChoiseDiologHelper != null) {
            mNetChoiseDiologHelper.resetNetworkItem(0);
        }
*/
        if (isExchangeViewContainer) {
            HtSdk.getInstance().exchangeVideoAndWhiteboard();
            isExchangeViewContainer = !isExchangeViewContainer;
            showVideoContainer(videoVisibleIv, false);
        }
        if (memberFloatTV == null) {
            return;
        }
        memberFloatTV.setVisibility(View.GONE);

        showScoreDialogIfEnable();
    }

    /**
     * ????????????
     */
    private void showScoreDialogIfEnable() {
        if (moduleConfigHelper == null)
            return;

        if (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_SCORE_VISIBLE))
            return;

        ScoreDialogFragment scoreDialogFragment = ScoreDialogFragment.create(mHtSdk.getRoomInfo().getZhuBo().getScoreConfig());
        scoreDialogFragment.show(this.getSupportFragmentManager(), "score");
    }

    //TODO-----------------------------------------??????????????????------------------------------------

    //?????????????????????
    @Override
    public void memberForceout() {
        String reason = getResources().getString(R.string.member_forceout);
        memberOut(reason);
    }

    //????????????
    @Override
    public void memberKick() {
        String reason = getResources().getString(R.string.member_kick);
        memberOut(reason);
    }

    //????????????
    private void memberOut(String reason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(reason);
        builder.setTitle(R.string.tips);
        builder.setPositiveButton((R.string.goback), (dialog, which) -> {
            dialog.dismiss();
            LiveNativeActivity.this.finish();
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    //??????????????????
    public void updateMemberTotal(int total) {
        if (moduleConfigHelper != null && memberFloatTV != null) {
            if (moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_VISITORINFO) && isLiveStart) {
                if (memberFloatTV != null) {
                    setMemberFloatTVWH(width, screenHeight);
                    memberFloatTV.setVisibility(View.VISIBLE);
                    memberFloatTV.setText("??????" + total + "???");
                }
            } else {
                memberFloatTV.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void receiveBroadcast(BroadcastEntity broadcastEntity) {
        if (mLiveMessageView != null) {
            mLiveMessageView.insertChatMessage(broadcastEntity);
        }

    }

    /**
     * @param message
     */
    @Override
    public void eventCallback(Event message) {
        Log.i("eventCallback", "LiveNativeActivity");
        if (message != null) {
            int type = message.getType();
            switch (type) {
                case EventType.INSERTCHAT:
                    if (mLiveMessageView != null) {
                        mLiveMessageView.insertChatMessage(message.getData());
                    }
                    break;
                case EventType.ADDDANMAKU:
                    if (danmakuFlameUtil == null) return;
                    if (danmakuFlameUtil.isShown()) {   //???????????? view ???????????????????????????
                        danmakuFlameUtil.addDanmaku((SpannableString) message.getData(), false);
                    }
                    break;
                case EventType.SHOWTITLEBAR:
                    showTitleBar();
                    break;
                case EventType.LOOKOVERVOTES:
                    if (mLiveVoteDialogHelper != null) {
                        mLiveVoteDialogHelper.voteStart((VoteEntity) message.getData());
                    }
                    break;
                case EventType.LOOKOVERVOTERESULTS:
                    if (mLiveVoteDialogHelper != null) {
                        mLiveVoteDialogHelper.voteStop((VotePubEntity) message.getData());
                    }
                    break;
                case EventType.NETWORK_STATE_CHANGE:
                    int netStatus = (int) message.getData();
                    if (netStatus == NetMonitor.NETWORK_NONE) {
                        AlertDialogFactory.showAlertDialog(this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.not_connect), null);
                    }
                    break;
            }
        }
    }

    /**
     * ??????????????????
     */
    public void setCanInput(boolean value) {
        vgInputLayout.setCanInput(value);
        fullScreenInputBarView.setCanInput(value);
//        fullScreenInputBarViewOpen.setCanInput(value);
    }


    @Override
    public void connectVideoError(String error) {
        StringUtils.tip(getApplicationContext(), error);
    }


    @Override
    public void pageChange(int position) {
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
        boolean isPad = DimensionUtils.isPad(this);

        switch (mLiveMessageView.getCurrentTab()) {
            case LiveMessageView.TAB_CHAT:  //??????
                if (vgInputLayout != null) {

                    if ((isPad || isPortrait) && vgInputLayout.getVisibility() != View.VISIBLE) {
                        vgInputLayout.setVisibility(View.VISIBLE);
                    } else if (!isPad && !isPortrait) {
                        vgInputLayout.setVisibility(View.GONE);
                    }
                    vgInputLayout.setCanInput(chatEnable);
                    vgInputLayout.setSendFlowerEnable(true);
                    vgInputLayout.setInputExpressionEnable(true);
                }
                break;
            case LiveMessageView.TAB_QUESTION:  //??????
                if (vgInputLayout != null) {
                    if ((isPad || isPortrait) && vgInputLayout.getVisibility() != View.VISIBLE) {
                        vgInputLayout.setVisibility(View.VISIBLE);
                    } else if (!isPad && !isPortrait) {
                        vgInputLayout.setVisibility(View.GONE);
                    }
                    vgInputLayout.setCanInput(true);
                    vgInputLayout.setSendFlowerEnable(false);
                    vgInputLayout.setInputExpressionEnable(false);
                }
                break;
            case LiveMessageView.TAB_NOTIFY:  //??????
                if (vgInputLayout != null && vgInputLayout.getVisibility() == View.VISIBLE) {
                    vgInputLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    //TODO-----------------------------------??????????????????????????????------------------------------------

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
            currentMode = mode;
          /*  if (mode == VideoModeType.CAMERA_MODE) {
                showVideoContainer();
            }*/
        }

        /**
         * ??????????????????
         *
         * @param mode ???????????????VideoModeType.CAMERA_MODE????????????????????????;VideoModeType.DESKTOP_MODE???????????????????????????)
         */
        @Override
        public void onVideoStop(int mode) {
            if (mode == VideoModeType.CAMERA_MODE && !isExchangeViewContainer) {
                showVideoContainer(videoVisibleIv, false);
            }
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
            changeTip.setVisibility(View.VISIBLE);
            if (mNetCheckHelper != null) {
                mNetCheckHelper.dismissPop();
            }
        }

        /**
         * ??????????????????
         */
        @Override
        public void onVideoModeChanged() {
            changeTip.setVisibility(View.GONE);
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onCameraShow() {
            if (!userVideoShow) return;
            showVideoContainer(videoVisibleIv, true);
            if (DimensionUtils.isPad(LiveNativeActivity.this) && !ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait())
                updateLayout();
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onCameraHide() {
            exchangeViewContainer();
            showVideoContainer(videoVisibleIv, false);
            if (DimensionUtils.isPad(LiveNativeActivity.this) && !ScreenSwitchUtils.getInstance(LiveNativeActivity.this).isPortrait())
                updateLayout();
        }

    }

    //TODO----------------------------------------------??????-------------------------------------------
    @Override
    public void onSendFlower() {
        sendFlower();
    }

    /**
     * ??????
     */

    public void sendFlower() {
        if (HtSdk.getInstance().isLiving()) {
            HtSdk.getInstance().sendFlower();
            if (vgInputLayout != null) {
                vgInputLayout.setFlowerNum(0);
            }

        } else {
            StringUtils.tip(this, "????????????");
        }
    }

    //???????????????
    @Override
    public void setFlowerNum(int num) {
        if (vgInputLayout != null) {
            vgInputLayout.setFlowerNum(num);
        }
    }

    //????????????????????????????????????
    @Override
    public void setFlowerLeftTime(int time) {
        String tips = String.format("??????%s???????????????????????????", time);
        StringUtils.tip(this, tips);
    }


    //????????????
    @Override
    public void sendFlowerSuccess(String args) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int amount = jsonObject.optInt("amount");
            if (amount <= 0) {
                return;
            } else {
                String flower = "";
                for (int i = 0; i < amount; i++) {
                    flower += "[flower]";
                }
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setAvatar(jsonObject.optString("avatar"));
                chatEntity.setNickname(jsonObject.optString("nickname"));
                chatEntity.setXid(jsonObject.optInt("xid"));
                chatEntity.setRole(jsonObject.optString("role"));
                chatEntity.setTime(jsonObject.optString("sendtime"));
                chatEntity.setMsg("???????????????" + flower);
                chatEntity.setAvatar(jsonObject.optString("avatar"));
                if (mLiveMessageView != null) {
                    mLiveMessageView.insertChatMessage(chatEntity);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSendMessage(String content) {
        if (mLiveMessageView != null) {
            mLiveMessageView.onSendMessage(content);
        }

    }

    //TODO---------------------------------------------activity life-------------------------------------------
    @Override
    public void onBackPressed() {
        gobackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLayout();
        danmakuFlameUtil.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        danmakuFlameUtil.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        HtSdk.getInstance().onStop();
        if (mNetCheckHelper != null) {
            mNetCheckHelper.stopCheckNetStatus();
        }
    }

    boolean isFinish = false;

    @Override
    public void finish() {
        isFinish = true;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        danmakuFlameUtil.destroy();
        if (mNetCheckHelper != null) {
            mNetCheckHelper.dismissPop();
        }
    }

}
