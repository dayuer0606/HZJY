package com.android.weischool.activity;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cpiz.android.bubbleview.BubbleStyle;
import com.jakewharton.rxbinding.view.RxView;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.TextParams;
import com.android.weischool.R;
import com.android.weischool.adapter.OTMVideoAdapter;
import com.android.weischool.adapter.OTOVideoAdapter;
import com.android.weischool.bean.ViewModelEvent;
import com.android.weischool.consts.EventType;
import com.android.weischool.databinding.ActivityLiveOneToMultiNativeBinding;
import com.android.weischool.dialog.AlertDialogFactory;
import com.android.weischool.entity.Event;
import com.android.weischool.helper.OTMVoteHelper;
import com.android.weischool.manager.OTMChatManager;
import com.android.weischool.manager.RTCPanelManager;
import com.android.weischool.util.DimensionUtils;
import com.android.weischool.util.ExpressionUtil;
import com.android.weischool.util.TimeUtil;
import com.android.weischool.util.ToastUtil;
import com.android.weischool.view.OTMAwardPopView;
import com.android.weischool.view.OTMChatInputDialog;
import com.android.weischool.viewmodel.BaseLiveRtcViewModel;
import com.android.weischool.viewmodel.LiveOneToMultiViewModel;
import com.talkfun.sdk.consts.DocType;
import com.talkfun.sdk.event.Callback;
import com.talkfun.sdk.event.OnMultiMediaStatusChangeListener;
import com.talkfun.sdk.module.ChatEntity;
import com.talkfun.sdk.module.VoteDelEntity;
import com.talkfun.sdk.module.VoteEntity;
import com.talkfun.sdk.module.VotePubEntity;
import com.talkfun.sdk.rtc.consts.ApplyStatus;
import com.talkfun.sdk.rtc.entity.AwardEntity;
import com.talkfun.sdk.rtc.entity.RtcUserEntity;
import com.talkfun.utils.NetMonitor;
import com.talkfun.utils.PreventRepeatedUtil;
import com.talkfun.widget.HoloView;
import com.talkfun.widget.RippleView;
import com.talkfun.widget.anni.HorizontalGravity;
import com.talkfun.widget.anni.VerticalGravity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import q.rorbin.badgeview.QBadgeView;
import rx.functions.Action1;

/**
 * 1 V 16
 * Created by ccy on 2019/5/16/10:54
 */
public class LiveOneToMultiNativeActivity extends BaseLiveRtcActivity<ActivityLiveOneToMultiNativeBinding, LiveOneToMultiViewModel> implements BaseLiveRtcViewModel.OnVideoDataChangeListener,
        OTMChatInputDialog.OnSendMessageListener, BaseLiveRtcViewModel.OnTimeListener, OnMultiMediaStatusChangeListener {

    /**
     * ???????????????
     */
    private OTMVideoAdapter otmVideoAdapter;
    /**
     * ??????????????????
     */
    private RTCPanelManager mRTCPanelManager;
    private boolean isSelectEraser;
    /**
     * ??????????????????
     */
    private OTMChatManager otmChatManager;
    /***
     * ???????????????
     */
    private QBadgeView mChatBadgeView;
    /***
     * ???????????????
     */
    private QBadgeView mVoteBadgeView;

    /**
     * ???????????????
     */
    private OTMVoteHelper mOtmVoteHelper;
    /**
     * ???????????????
     */
    private OTMChatInputDialog mChatInputDialog;

    @Override
    protected int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_live_one_to_multi_native;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    protected LiveOneToMultiViewModel initViewModel() {
        return new LiveOneToMultiViewModel(this.getApplication());
    }

    @Override
    protected void initView() {
        initRedPoint();
        ExpressionUtil.tvImgHeight = isIPad() ? getDimension(R.dimen.dp_20) : getDimension(R.dimen.dp_37);//????????????
        ExpressionUtil.tvImgWidth = isIPad() ? getDimension(R.dimen.dp_25) : getDimension(R.dimen.dp_45);
        initAdapter();
        if (mOtmVoteHelper == null) {
            mOtmVoteHelper = new OTMVoteHelper(this);
        }
        onClick();
    }


    /**
     * ??????????????????
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCallback(Event message) {
        if (message.getType() == EventType.NETWORK_STATE_CHANGE) {
            int netStatus = (int) message.getData();
            if (netStatus == NetMonitor.TYPE_NOT_CONNECTED) {
                noNetWorkTime = System.currentTimeMillis();
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.VISIBLE);
            } else if (netStatus == NetMonitor.TYPE_MOBILE) {
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.GONE);
                showToast(this.getString(R.string.network_4G_tip));
            } else {
                mDatabinding.llBadNetStatus.badNetStatusLl.setVisibility(View.GONE);
                if (noNetWorkTime != 0 && System.currentTimeMillis() - noNetWorkTime > 2 * 1000) {//????????????2??????????????????
                    reset();
                    baseViewModel.reload();
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    private void initRedPoint() {
        mChatBadgeView = new QBadgeView(this);
        int width = getDimension(R.dimen.dp_0_1);
        int height = getDimension(R.dimen.dp_0_1);
        int offsetX = getDimension(R.dimen.dp_1);
        int offsetY = getDimension(R.dimen.dp_1);
        mChatBadgeView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        mChatBadgeView.bindTarget(chatIV());
        mChatBadgeView.setGravityOffset(offsetX, offsetY, true);
        mChatBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);

        mVoteBadgeView = new QBadgeView(this);
        mVoteBadgeView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        mVoteBadgeView.setGravityOffset(offsetX, offsetY, true);
        mVoteBadgeView.bindTarget(voteIV());
        mVoteBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
    }

    private void onClick() {
        /**
         * ?????????
         */
        banFrequentlyClick(mDatabinding.toolBar.backIv, o -> {

            if (baseViewModel != null && baseViewModel.applyStatus() == ApplyStatus.ALLOW && !baseViewModel.isAutoUp()) {
                AlertDialogFactory.showAlertDialog(LiveOneToMultiNativeActivity.this.getSupportFragmentManager(), getResources().getString(R.string.tips), getResources().getString(R.string.exit_before_tip),
                        getResources().getString(R.string.confirm), null, null);
                return;
            }
            popupBackDialog();
        });
        /**
         * ??????
         */
        banFrequentlyClick(mDatabinding.toolBar.refreshIv, o -> {
            reset();
            mDatabinding.mlMultiStatus.showLoading();
            baseViewModel.reload();
        }, 1000);


        /**
         *???????????????
         */
        banFrequentlyClick(operatorRtcApplyIV(), o -> showRtcApplyDialog());

        /**
         *???????????????
         */
        banFrequentlyClick(operatorRtcCancelFL(), o -> showRtcCancleDialog());

        /**
         *???????????????
         */
        banFrequentlyClick(operatorRtcDownIV(), o -> showRtcDownDialog());

        /**
         * ????????????
         */
        operatorAudioIV().setOnClickListener(v -> {
            if (!PreventRepeatedUtil.canClickable(String.valueOf(v.getId()))) {
                showToast(getString(R.string.quick_click_tips));
                return;
            }
            baseViewModel.switchMedia(OTOVideoAdapter.AUDIO, !operatorAudioIV().isSelected(), new Callback<String>() {
                @Override
                public void success(String result) {
                    setAudioSwitch(!operatorAudioIV().isSelected());
                }

                @Override
                public void failed(String failed) {
                    showToast(failed);
                }
            });
        });
        /**
         * ????????????
         */
        operatorVideoIV().setOnClickListener(v -> {
            if (!PreventRepeatedUtil.canClickable(String.valueOf(v.getId()))) {
                showToast(getString(R.string.quick_click_tips));
                return;
            }
            baseViewModel.switchMedia(OTOVideoAdapter.VIDEO, !operatorVideoIV().isSelected(), new Callback<String>() {
                @Override
                public void success(String result) {
                    setVideoSwitch(!operatorVideoIV().isSelected());
                }

                @Override
                public void failed(String failed) {
                    showToast(failed);
                }
            });
        });


        /**
         * ????????????
         */
        banFrequentlyClick(drawIV(), aVoid -> {
            selectedDrawOrEraser(false);
            popupDrawPopView();
        });

        /**
         * ???????????????
         */
        banFrequentlyClick(eraserIV(), o -> {
            selectedDrawOrEraser(true);
            baseViewModel.setEraser();
        });


        /**
         * ????????????
         */
        banFrequentlyClick(voteIV(), o -> {
            mVoteBadgeView.setBadgeNumber(0);
            mOtmVoteHelper.show();
        });

        /**
         * ????????????
         */
        banFrequentlyClick(chatIV(), aVoid -> {
            showChatPop();
          /*  ScreenSwitchUtils.getInstance(LiveOneToMultiNativeActivity.this).toggleScreen();*/
        });

        if (isIPad()) {//???????????????
            banFrequentlyClick(mDatabinding.bottomBar.bottomChatMessageTv, aVoid -> showDialog(true));
            banFrequentlyClick(mDatabinding.bottomBar.ivEmoticon, aVoid -> showDialog(false));
        }
        /**
         * ???????????????
         */
        banFrequentlyClick(mDatabinding.multimediaLayout.ivFullScreen, aVoid -> {
            boolean isSelect = mDatabinding.multimediaLayout.ivFullScreen.isSelected();
            multiMediaViewFullScreen(!isSelect);
        });
        mDatabinding.multimediaLayout.flMultiMediaParent.setOnClickListener(v -> {
            if (mDatabinding.multimediaLayout.progressBarParent == null) {
                return;
            }
            if (mDatabinding.multimediaLayout.progressBarParent.getVisibility() == View.VISIBLE) {
                hideProgressBar();
            } else {
                showProgressBar();
            }
        });
    }


    /**
     * ????????????????????????
     */
    private void showChatPop() {
        if (mChatBadgeView != null) {//???????????????
            mChatBadgeView.setBadgeNumber(0);
        }
        initChatManager();
        if (isIPad()) {
            //????????????????????????????????????????????????????????????
            int height = baseViewModel != null && !baseViewModel.isLiving() ? DimensionUtils.getScreenHeight(this) - getDimension(R.dimen.dp_22) :
                    DimensionUtils.getScreenHeight(this) - getDimension(R.dimen.dp_22) - getDimension(R.dimen.dp_84);
            otmChatManager.setHeight(height);
            otmChatManager.show(mDatabinding.llWhiteboardParent, VerticalGravity.ALIGN_TOP, HorizontalGravity.ALIGN_RIGHT);
        } else {
            otmChatManager.show(mDatabinding.llParent, VerticalGravity.ALIGN_TOP, HorizontalGravity.ALIGN_RIGHT);
        }
    }

    private void initChatManager() {
        if (otmChatManager == null) {
            boolean isIPad = isIPad();
            int height = isIPad ? DimensionUtils.getScreenHeight(this) - getDimension(R.dimen.dp_22) - getDimension(R.dimen.dp_84) : ViewGroup.LayoutParams.MATCH_PARENT;
            int width = isIPad ? getDimension(R.dimen.dp_175) : getDimension(R.dimen.dp_260);
            otmChatManager = new OTMChatManager(LiveOneToMultiNativeActivity.this, baseViewModel, width, height, isIPad);
        }
    }

    /**
     * ???????????????
     */
    private void dismissChatPop() {
        if (otmChatManager == null || !isIPad()) {
            return;
        }
        otmChatManager.dismiss();
    }

    /**
     * ??????????????????
     *
     * @param view
     * @param action
     */
    private void banFrequentlyClick(View view, Action1<Void> action) {
        banFrequentlyClick(view, action, 800);
    }

    private void banFrequentlyClick(View view, Action1<Void> action, long time) {
        RxView.clicks(view).throttleFirst(time, TimeUnit.MILLISECONDS).subscribe(action);
    }

    private void initAdapter() {
        mDatabinding.rvVideo.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        otmVideoAdapter = new OTMVideoAdapter();
        otmVideoAdapter.setDataList(baseViewModel.getVideoList());
        mDatabinding.rvVideo.setAdapter(otmVideoAdapter);
    }

    @Override
    protected void initData() {
        String accessToken = getIntent().getStringExtra("token");
        baseViewModel.initSdk(accessToken, mDatabinding.whiteboardFl);
        baseViewModel.setOnVideoDataChangeListener(this);
        baseViewModel.setDesktopVideoContainer(mDatabinding.flDesktopLayout);
        baseViewModel.setMultiMediaViewContainer(mDatabinding.multimediaLayout.flMultiMedia);
        baseViewModel.setOnTimeListener(this);
        baseViewModel.setOnMultiMediaStatusChangeListener(this);
        baseViewModel.getLiveData().observe(this, liveObserver);

    }

    /**
     * ????????????
     */
    private Observer liveObserver = new Observer<ViewModelEvent>() {
        @Override
        public void onChanged(@Nullable ViewModelEvent viewModelEvent) {
            if (viewModelEvent == null) {
                return;
            }
            Object msg = viewModelEvent.getObject();
            switch (viewModelEvent.getType()) {
                case LiveOneToMultiViewModel.RTC_OPEN:
                    if (!baseViewModel.isAutoUp()) {
                        showToast(getString(R.string.start_platform));
                    }
                    setIconStatus(LiveOneToMultiViewModel.RTC_OPEN);
                    break;
                case LiveOneToMultiViewModel.RTC_CLOSE:
                    if (!baseViewModel.isAutoUp()) {
                        showToast(getString(R.string.close_platform));
                    }
                    setIconStatus(LiveOneToMultiViewModel.RTC_CLOSE);
                    break;
                case LiveOneToMultiViewModel.RTC_ALLOW:
                    if (!baseViewModel.isAutoUp()) {
                        showToast(getString(R.string.up_platform));
                    }
                    setMediaVisibility(msg);
                    setIconStatus(LiveOneToMultiViewModel.RTC_ALLOW);
                    break;
                case LiveOneToMultiViewModel.RTC_APPLYING:
                    setIconStatus(LiveOneToMultiViewModel.RTC_APPLYING);
                    break;
                case LiveOneToMultiViewModel.RTC_KICK:
//                    showToast(getString(R.string.kick_platform));
//                    setIconStatus(LiveOneToMultiViewModel.RTC_KICK);
//                    break;
                case LiveOneToMultiViewModel.RTC_DOWN:
                    showToast(getString(R.string.kick_platform));
                    setIconStatus(LiveOneToMultiViewModel.RTC_KICK);
                    break;
                case BaseLiveRtcViewModel.RECEIVER_CHAT:
                    receiverChatEntity((ChatEntity) msg);
                    break;
                case BaseLiveRtcViewModel.LIVE_WAIT:
//                    titleAndTimeVisibility(View.GONE);
//                    hideMultipleStatusLayout();
                    hideView();
                    break;
                case BaseLiveRtcViewModel.LIVE_START:
                    showView();
                    setMultiMediaViewVisiable(View.GONE);
                    mDatabinding.toolBar.titleTv.setText(baseViewModel.getLiveTitle());
                    break;
                case BaseLiveRtcViewModel.LIVE_STOP:
                    reset();
                    hideView();
                    if (isMuitiMediaFullScreen) {
                        multiMediaViewFullScreen(false);
                    }
                    break;
               /* case BaseLiveRtcViewModel.LIVE_TIME:
                    setLiveTime(msg);
                    break;*/
                case BaseLiveRtcViewModel.LIVE_DRAW:
                    isDraw = (boolean) msg;
                    if (isDraw)
                        initOTMDrawManager();
                    //??????????????????????????????????????????????????????????????????????????????
                    if (mRTCPanelManager != null) {
                        drawTip(isDraw);
                    }
                    if (isApplicateMuitiMedia) {
                        return;
                    }
                    drawVisibility(isDraw);
//                    selectedDrawOrEraser(isDraw);
                    break;
                case BaseLiveRtcViewModel.LIVE_FAIL:
//                    mDatabinding.mlMultiStatus.showContent();
                    showFail(msg.toString());
//                    showToast(msg.toString());
                    break;
                case BaseLiveRtcViewModel.AUDIO_ENABLE: //??????
                    boolean audio = (boolean) msg;
                    showToast(audio ? getString(R.string.open_audio) : getString(R.string.close_audio));
                    setAudioSwitch(!audio);
                    break;
                case BaseLiveRtcViewModel.VIDEO_ENABLE://??????
                    boolean visibility = (boolean) msg;
                    showToast(visibility ? getString(R.string.open_video) : getString(R.string.close_video));
                    setVideoSwitch(!visibility);
                    break;
                case BaseLiveRtcViewModel.ERROR_MESSAGE:
                    if (msg != null)
//                        errorTips((String) msg);
                        break;
                case LiveOneToMultiViewModel.RTC_AWARD:
                    if (msg == null)
                        return;
                    initChatManager();
                    otmChatManager.receiveAwardEntity(msg);
                    showAwardPopView(msg);
                    break;
                case LiveOneToMultiViewModel.LIVE_VOTE_START:
                    if (msg != null)
                        mOtmVoteHelper.voteStart((VoteEntity) msg);
                    voteRedPointTip();
                    break;
                case LiveOneToMultiViewModel.LIVE_VOTE_STOP:
                    if (msg != null)
                        mOtmVoteHelper.voteStop((VotePubEntity) msg);
                    voteRedPointTip();
                    break;
                case LiveOneToMultiViewModel.LIVE_VOTE_DEL:
                    if (msg != null)
                        mOtmVoteHelper.voteDel((VoteDelEntity) msg);
                    voteRedPointTip();
                    break;
                case LiveOneToMultiViewModel.LIVE_VOTE_ALL:
                    if (msg != null)
                        mOtmVoteHelper.addVoteList((List<Object>) msg);
                    break;
                case LiveOneToMultiViewModel.LIVE_SOCKET_CONNNECT_FAIL:
                    socketConnectFail();
                    break;
                case LiveOneToMultiViewModel.MEMBER_KICK:
                    memberKick();
                    break;
                case LiveOneToMultiViewModel.MEMBER_FORCEOUT:
                    memberForceout();
                    break;
                case LiveOneToMultiViewModel.RTC_DESTOP_MODE:
                    if (isMuitiMediaFullScreen) {
                        multiMediaViewFullScreen(false);
                    }
                    break;
                case BaseLiveRtcViewModel.RTC_INVITE:
                    popupInviteDialog(new BaseLiveRtcActivity.OnPositiveOrNegativeListener() {
                        @Override
                        public void onPositive() {
                            acceptOrInvite(true);
                        }

                        @Override
                        public void onNegative() {
                            acceptOrInvite(false);
                        }
                    });
                    break;
            }
        }
    };
    /**
     * ?????????????????????
     */
    protected BaseCircleDialog popupInviteDialog(final OnPositiveOrNegativeListener onPositiveOrNegativeListener) {
        int width = isIPad() ? getDimension(R.dimen.dp_150) : getDimension(R.dimen.dp_210);
        BaseCircleDialog baseCircleDialog = new CircleDialog.Builder().setCanceledOnTouchOutside(false).setWidth(width).setText(getResouceString(R.string.invite_rtc_up_tips)).configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.textColor = Color.parseColor("#1D334E");
                /*   params.height = isIPad() ? getDimension(R.dimen.dp_65) : getDimension(R.dimen.dp_95);*/
            }
        }).setTitle("??????").setPositive("??????", v -> {
            if (onPositiveOrNegativeListener != null) {
                onPositiveOrNegativeListener.onPositive();
            }
        }).configPositive(params -> {
            params.height = dialogBtnHeight();
            params.textColor = getResources().getColor(R.color.rtc_invite_accept);
        }).setNegative("??????", v -> {
            if (onPositiveOrNegativeListener != null) {
                onPositiveOrNegativeListener.onNegative();
            }
        }).configNegative(new ConfigButton() {
            @Override
            public void onConfig(ButtonParams params) {
                params.height = dialogBtnHeight();
                params.textColor = Color.parseColor("#263548");
            }
        }).create();
        baseCircleDialog.show(this.getSupportFragmentManager(), "InviteDialog");
        return baseCircleDialog;
    }
    private void hideView() {
        setMultiMediaViewVisiable(View.GONE);
        titleAndTimeVisibility(View.GONE);
        drawVisibility(false);
        hideVoteRedPoint();
        setVoteVisibility(View.GONE);
        hideRtcOperatorView();
//        hideDrawOperatorView();
        hideMultipleStatusLayout();
        dismissChatPop();
        dismissVotePop();

    }

    private void dismissVotePop() {
        if (mOtmVoteHelper != null) {
            mOtmVoteHelper.dismiss();
        }
    }

    /**
     * ????????????
     */
    private void showView() {
        dismissChatPop();
        titleAndTimeVisibility(View.VISIBLE);
//        mDatabinding.rvVideo.setVisibility(View.VISIBLE);
        setVoteVisibility(View.VISIBLE);
        hideMultipleStatusLayout();
    }

    /**
     * ??????????????????
     * QBadgeView ??????????????????????????????????????????????????????tagetView?????????????????????
     *
     * @param visible
     */
    private void setVoteVisibility(int visible) {
        voteIV().setVisibility(visible);
        View view = (View) voteIV().getParent();
        view.setVisibility(visible);
    }

    /**
     * ??????????????????
     */
    private void voteRedPointTip() {
        if (mVoteBadgeView != null) {//????????????
            mVoteBadgeView.setBadgeNumber(mOtmVoteHelper.isShowing() ? 0 : -1);
        }
    }

    private void hideVoteRedPoint() {
        if (mVoteBadgeView != null) {//????????????
            mVoteBadgeView.setBadgeNumber(0);
        }
        if (mChatBadgeView != null) {
            mChatBadgeView.setBadgeNumber(0);
        }
    }

    /***
     * ??????????????????
     * {//????????????????????????????????????????????????
     * @param msg
     */
    OTMAwardPopView awardPopView;

    private void showAwardPopView(Object msg) {
        AwardEntity awardEntity = (AwardEntity) msg;
        if (TextUtils.equals(awardEntity.getToUid(), baseViewModel.getUid())) {
            if (awardPopView == null) {
                awardPopView = new OTMAwardPopView(this).createPopup();
            }
            if (awardPopView.isShowing()) {
                return;
            }
            awardPopView.showAtAnchorView(mDatabinding.whiteboardFl, VerticalGravity.CENTER, HorizontalGravity.CENTER);
        }
    }

    /**
     * ????????????
     *
     * @param msg
     */
    private void showFail(String msg) {
        if (msg.contains("????????????")) {
            mDatabinding.mlMultiStatus.showEmpty();
        } else {
            mDatabinding.mlMultiStatus.showError();
        }
    }

    private void receiverChatEntity(ChatEntity msg) {
        initChatManager();
        otmChatManager.receiveChatEntity(msg);
        if (mChatBadgeView != null && !TextUtils.equals(baseViewModel.getUid(), msg.getUid())) {//????????????
            mChatBadgeView.setBadgeNumber(otmChatManager.isShow() ? 0 : -1);
        }
    }

    private void hideMultipleStatusLayout() {
        mDatabinding.mlMultiStatus.showContent();
    }

    private void initOTMDrawManager() {
        baseViewModel.setDefaultDrawValue();
        selectedDrawOrEraser(isSelectEraser);
        if (mRTCPanelManager != null)
            return;
        boolean isIPad = isIPad();
        int width = isIPad ? getDimension(R.dimen.dp_160) : getDimension(
                R.dimen.dp_240);
        int height = isIPad ? getDimension(R.dimen.dp_115) : getDimension(R.dimen.dp_189);
        mRTCPanelManager = new RTCPanelManager(this, width, height);
        mRTCPanelManager.setArrowDirection(isIPad ? BubbleStyle.ArrowDirection.Down : BubbleStyle.ArrowDirection.Right);
        mRTCPanelManager.setOnDrawCmdListener(new RTCPanelManager.OnDrawCmdListener() {
            @Override
            public void onPaintColor(int color) {
                if (baseViewModel != null) {
                    baseViewModel.setPaintColor(color);
                }
            }

            @Override
            public void onStrokeWidth(int size) {
                if (baseViewModel != null) {
                    baseViewModel.setStokeWidth(size);
                }
            }

            @Override
            public void onDrawType(int type) {
                if (baseViewModel != null) {
                    baseViewModel.setDrawType(type);
                }
            }
        });
        mRTCPanelManager.getDrawLiveData().observeForever(integer -> setDrawViewResource(drawIV(), integer, isIPad() ? getDimension(R.dimen.dp_4) : getDimension(R.dimen.dp_10)));
    }

    /**
     * ????????????icon
     *
     * @param view
     * @param resource
     */
    private void setDrawViewResource(ImageView view, int resource, int padding) {
        view.setImageDrawable(LiveOneToMultiNativeActivity.this.getResources().getDrawable(resource));
        view.setPadding(padding, padding, padding, padding);
    }

    /**
     * ???????????? ?????????
     */
    private void hideRtcOperatorView() {
        operatorRtcCancelFL().setVisibility(View.GONE);
        operatorRtcDownIV().setVisibility(View.GONE);
        operatorRtcApplyIV().setVisibility(View.GONE);
        operatorAudioIV().setVisibility(View.GONE);
        operatorVideoIV().setVisibility(View.GONE);
    }

    /**
     * ?????????????????????
     */
    private void hideDrawOperatorView() {
        drawIV().setVisibility(View.GONE);
        eraserIV().setVisibility(View.GONE);
        voteIV().setVisibility(View.GONE);
        isSelectEraser = false;
    }

    /**
     * ????????????????????????
     *
     * @param isDraw
     */
    private void drawVisibility(boolean isDraw) {
        drawVisibility(isDraw, true);
    }

    /**
     * @param isDraw
     * @param isClear ????????????????????????
     */
    private void drawVisibility(boolean isDraw, boolean isClear) {
        eraserIV().setVisibility(isDraw ? View.VISIBLE : View.GONE);
        drawIV().setVisibility(isDraw ? View.VISIBLE : View.GONE);
        if (!isDraw && mRTCPanelManager != null && mRTCPanelManager.isShowing()) {
            mRTCPanelManager.dismiss();
        }
        if (!isDraw && isClear) {
            mRTCPanelManager = null;
            isSelectEraser = false;
        }
    }

    /**
     * ??????????????????
     */
    private void popupDrawPopView() {
        initOTMDrawManager();
        int offY = -getDimension(R.dimen.dp_2);
        int verticalGravity = isIPad() ? VerticalGravity.ABOVE : VerticalGravity.ALIGN_TOP;
        int horizontalGravity = isIPad() ? HorizontalGravity.ALIGN_LEFT : HorizontalGravity.LEFT;
        mRTCPanelManager.show(drawIV(), verticalGravity, horizontalGravity, 0, offY);
        if (!isSelectEraser) {
            baseViewModel.setDrawType(mRTCPanelManager.getLastSelectDrawType());
        }
    }

    /**
     * ????????????
     *
     * @param isDraw
     */
    private void drawTip(boolean isDraw) {
        showToast(isDraw ? getString(R.string.use_paint) : getString(R.string.no_use_paint));
    }

    /**
     * ??????????????????????????????
     *
     * @param isSelectEraser
     */
    private void selectedDrawOrEraser(boolean isSelectEraser) {
        eraserIV().setSelected(isSelectEraser);
        drawIV().setSelected(!isSelectEraser);
        this.isSelectEraser = isSelectEraser;
    }

    /**
     * ?????????????????????
     *
     * @param object
     */
    private void setMediaVisibility(Object object) {
        if (object == null) {
            return;
        }
        RtcUserEntity rtcUserEntity = (RtcUserEntity) object;
        setVideoSwitch(!rtcUserEntity.isVideoOpen());
        setAudioSwitch(!rtcUserEntity.isAudioOpen());
    }

    /**
     * ????????????????????????
     *
     * @param platformStatus
     */
    private void setIconStatus(int platformStatus) {

        if (baseViewModel.isAutoUp()) {
            operatorRtcApplyIV().setVisibility(View.GONE);
            operatorRtcDownIV().setVisibility(View.GONE);
            setOperatorRtcCancelVisibility(View.GONE);
            return;
        }

        switch (platformStatus) {
            case LiveOneToMultiViewModel.RTC_OPEN://???????????????
                operatorRtcApplyIV().setVisibility(View.VISIBLE);
                operatorRtcDownIV().setVisibility(View.GONE);
                setOperatorRtcCancelVisibility(View.GONE);

                break;
            case LiveOneToMultiViewModel.RTC_APPLYING://?????????
                operatorRtcApplyIV().setVisibility(View.GONE);
                operatorRtcDownIV().setVisibility(View.GONE);
                setOperatorRtcCancelVisibility(View.VISIBLE);
                break;
            case LiveOneToMultiViewModel.RTC_ALLOW://???????????????
                operatorRtcApplyIV().setVisibility(View.GONE);
                operatorRtcDownIV().setVisibility(View.VISIBLE);
                setOperatorRtcCancelVisibility(View.GONE);
                break;
            case LiveOneToMultiViewModel.RTC_CLOSE://????????????
                setDefaultStatus();
                operatorRtcApplyIV().setVisibility(View.GONE);
                break;
            case LiveOneToMultiViewModel.RTC_CANCLE://???????????????
                operatorRtcApplyIV().setVisibility(View.VISIBLE);
                operatorRtcDownIV().setVisibility(View.GONE);
                setOperatorRtcCancelVisibility(View.GONE);
                break;
            case LiveOneToMultiViewModel.RTC_KICK://????????????
            case LiveOneToMultiViewModel.RTC_DOWN://???????????????
                setDefaultStatus();
                break;
        }
    }

    private void setOperatorRtcCancelVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            operatorRtcCancelRippleView().startRippleAnimation();
        } else {
            operatorRtcCancelRippleView().stopRippleAnimation();
        }
        operatorRtcCancelFL().setVisibility(visibility);
    }

    /**
     * ????????????
     */
    private void setDefaultStatus() {
        eraserIV().setVisibility(View.GONE);
        drawIV().setVisibility(View.GONE);
        operatorRtcDownIV().setVisibility(View.GONE);
        operatorAudioIV().setVisibility(View.GONE);
        operatorVideoIV().setVisibility(View.GONE);
        operatorRtcApplyIV().setVisibility(View.VISIBLE);
        setOperatorRtcCancelVisibility(View.GONE);
        isSelectEraser = false;
        isDraw = false;
    }

    private void titleAndTimeVisibility(int visible) {
        mDatabinding.toolBar.titleTv.setVisibility(visible);
        mDatabinding.toolBar.timeTv.setVisibility(visible);
        mDatabinding.toolBar.timeIv.setVisibility(visible);
//        mDatabinding.toolBar.refreshIv.setVisibility(visible);

    }

    /**
     * ????????????
     */
    private void setLiveTime(long time) {
        mDatabinding.toolBar.timeTv.setText(TimeUtil.displayHHMMSS(time));
    }

    private void showToast(String msg) {
        ToastUtil.show(this, msg, Gravity.CENTER, -0, 0, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMuitiMediaFullScreen) {
            multiMediaViewFullScreen(false);
        }
        baseViewModel.onResume();
        mDatabinding.mlMultiStatus.showLoading();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveObserver = null;
        baseViewModel.release();
    }

    /**
     * socket????????????
     */
    private void socketConnectFail() {
        if (!NetMonitor.isNetworkAvailable(LiveOneToMultiNativeActivity.this)) {
            return;
        }
        if (LiveOneToMultiNativeActivity.this.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LiveOneToMultiNativeActivity.this);
        builder.setMessage("???????????????????????????????????????????????????????????????????????????");
        builder.setTitle(R.string.tips);
        builder.setPositiveButton(R.string.refresh, (dialog, which) -> {
            if (baseViewModel != null) {
                baseViewModel.reload();
            }
        }).setNegativeButton(R.string.goback, (dialog, which) -> LiveOneToMultiNativeActivity.this.finish());
        AlertDialog socketFailTips = builder.create();
        socketFailTips.setCancelable(false);
        socketFailTips.show();
        return;
    }


    //-------------------------------??????????????????------------------------------------------------
    @Override
    public void updateItemOfPart(int position, int type) {
        if (otmVideoAdapter == null) {
            return;
        }
        otmVideoAdapter.notifyDataOfPart(position, type);
    }

    @Override
    public void notifyItemRangeChanged(int position, int itemCount) {
        if (otmVideoAdapter == null) {
            return;
        }
        otmVideoAdapter.notifyItemRangeChanged(position, itemCount);
    }

    @Override
    public void notifyItemRemoved(int position, int itemCount) {
        if (otmVideoAdapter == null) {
            return;
        }
        otmVideoAdapter.notifyItemRemoved(position);
        otmVideoAdapter.notifyItemRangeChanged(position, itemCount);
    }

    @Override
    public void notifyDataSetChanged() {
        mDatabinding.rvVideo.getAdapter().notifyDataSetChanged();
    }

    /**
     * ????????????
     *
     * @param errorMsg
     */
    private void errorTips(String errorMsg) {
        showToast(errorMsg);
    }

    /**
     * ?????????????????????
     */
    private void showRtcDownDialog() {
        new CircleDialog.Builder().setWidth(isIPad() ? getDimension(R.dimen.dp_170) : getDimension(R.dimen.dp_200)).setTitle(getResources().getString(R.string.tips))
                .setText(getResources().getString(R.string.down_platform_tip))/*.configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.height = isIPad() ? getDimension(R.dimen.dp_60) : getDimension(R.dimen.dp_80);
            }
        })*/.setPositive(getResources().getString(R.string.confirm), v -> rtcDown()).configPositive(params -> {
            params.height = dialogBtnHeight();
            params.textColor = getResources().getColor(R.color.red);
        }).setNegative(getResources().getString(R.string.cancel), null).configNegative(params -> params.height = dialogBtnHeight()).show(this.getSupportFragmentManager());

    }

    private void rtcDown() {
        baseViewModel.rtcDown(new Callback<RtcUserEntity>() {
            @Override
            public void success(RtcUserEntity result) {
                setIconStatus(LiveOneToMultiViewModel.RTC_DOWN);
                showToast(getString(R.string.down_platform));
            }

            @Override
            public void failed(String failed) {
                errorTips(failed);
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void showRtcApplyDialog() {

        new CircleDialog.Builder().setWidth(isIPad() ? getDimension(R.dimen.dp_170) : getDimension(R.dimen.dp_200)).setTitle(getResources().getString(R.string.tips)).setText(getResources().getString(R.string.apply_platform_tip))/*.configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.height = isIPad() ? getDimension(R.dimen.dp_60) : getDimension(R.dimen.dp_80);
            }
        })*/.setPositive(getResources().getString(R.string.confirm), v -> rtcApply()).configPositive(params -> {
            params.height = dialogBtnHeight();
            params.textColor = getResources().getColor(R.color.red);
        }).setNegative(getResources().getString(R.string.cancel), null).configNegative(params -> params.height = dialogBtnHeight()).show(this.getSupportFragmentManager());
    }


    private void rtcApply() {
        baseViewModel.rtcApply(new Callback<String>() {
            @Override
            public void success(String result) {
                showToast(getString(R.string.apply_platform));
                setIconStatus(LiveOneToMultiViewModel.RTC_APPLYING);
            }

            @Override
            public void failed(String failed) {
                errorTips(failed);
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void acceptOrInvite(boolean isAccept) {
        baseViewModel.respondInvite(isAccept, new Callback<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void failed(String failed) {
                showToast(failed);
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void showRtcCancleDialog() {
        new CircleDialog.Builder().setWidth(isIPad() ? getDimension(R.dimen.dp_170) : getDimension(R.dimen.dp_200)).setTitle(getResources().getString(R.string.tips)).setText(getResources().getString(R.string.cancle_platform_tip))/*.configText(new ConfigText() {
            @Override
            public void onConfig(TextParams params) {
                params.height = isIPad() ? getDimension(R.dimen.dp_60) : getDimension(R.dimen.dp_80);
            }
        })*/.setPositive(getResources().getString(R.string.confirm), v -> rtcCancle()).configPositive(params -> {
            params.height = dialogBtnHeight();
            params.textColor = getResources().getColor(R.color.red);
        }).setNegative(getResources().getString(R.string.cancel), null).configNegative(params -> params.height = dialogBtnHeight()).show(this.getSupportFragmentManager());
    }

    private void rtcCancle() {
        baseViewModel.rtcCancel(new Callback<String>() {
            @Override
            public void success(String result) {
                showToast(getString(R.string.cancle_platform));
                setIconStatus(LiveOneToMultiViewModel.RTC_CANCLE);
            }

            @Override
            public void failed(String failed) {
                errorTips(failed);
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void setVideoSwitch(boolean enable) {
        setVideoVisibility(View.VISIBLE);
        operatorVideoIV().setSelected(enable);
    }

    /**
     * ????????????????????????
     */
    private void setAudioSwitch(boolean enable) {
        setAudioVisibility(View.VISIBLE);
        operatorAudioIV().setSelected(enable);
    }

    /**
     * ????????????????????????
     */
    private void setVideoVisibility(int visibility) {
        operatorVideoIV().setVisibility(visibility);
    }

    /**
     * ????????????????????????
     */
    private void setAudioVisibility(int visibility) {
        operatorAudioIV().setVisibility(visibility);
    }


    /**
     * ??????????????????
     *
     * @param showSoft
     */
    private void showDialog(boolean showSoft) {
        initChatInputDialog();
        mChatInputDialog.showSoft(showSoft);
        mChatInputDialog.setMessageEdit(mDatabinding.bottomBar.bottomChatMessageTv.getText().toString());
        mChatInputDialog.show();
    }

    /**
     * ???????????????
     * ???????????????????????????
     */
    private void initChatInputDialog() {
        if (mChatInputDialog == null) {
            mChatInputDialog = new OTMChatInputDialog(this);
            mChatInputDialog.setOnSendMessageListener(this);
            mChatInputDialog.setCancelable(true);
            mChatInputDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /**
     * ??????
     *
     * @param msg
     */
    @Override
    public void onSendMsg(String msg) {
        baseViewModel.sendMessage(msg, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject result) {
                mDatabinding.bottomBar.bottomChatMessageTv.setText("");
                if (mChatInputDialog != null) {
                    mChatInputDialog.dismiss();
                }
            }

            @Override
            public void failed(String failed) {
                showToast(failed);
            }
        });
    }

    @Override
    public void onMsg(String msg) {
        mDatabinding.bottomBar.bottomChatMessageTv.setText(ExpressionUtil.getExpressionString(this, msg, "mipmap"));
    }


    //----------------------------------????????????????????????????????????----------------------------------------------------------------

    /**
     * ????????????
     *
     * @return
     */
    private ImageView operatorAudioIV() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.ivOperatorAudio : mDatabinding.leftOpratorRl.ivOperatorAudio;
    }

    /**
     * ????????????
     *
     * @return
     */
    private ImageView operatorVideoIV() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.ivOperatorVideo : mDatabinding.leftOpratorRl.ivOperatorVideo;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    private HoloView operatorRtcApplyIV() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.ivOrperatorRtcApply : mDatabinding.leftOpratorRl.ivOrperatorRtcApply;
    }

    /**
     * ???????????????
     *
     * @return
     */
    private HoloView operatorRtcDownIV() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.ivOrperatorRtcDown : mDatabinding.leftOpratorRl.ivOrperatorRtcDown;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    private FrameLayout operatorRtcCancelFL() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.flOrperatorRtcCancel : mDatabinding.leftOpratorRl.flOrperatorRtcCancel;
    }

    private RippleView operatorRtcCancelRippleView() {
        return isIPad() ? mDatabinding.ipadRightOpratorRl.rippleViewCancle : mDatabinding.leftOpratorRl.rippleViewCancle;
    }

    /**
     * ????????????
     *
     * @return
     */
    private ImageView drawIV() {
        return isIPad() ? mDatabinding.bottomBar.drawIv : mDatabinding.rightOpratorRl.drawIv;
    }

    /**
     * ???????????????
     *
     * @return
     */
    private ImageView eraserIV() {
        return isIPad() ? mDatabinding.bottomBar.eraserIv : mDatabinding.rightOpratorRl.eraserIv;
    }

    /**
     * ????????????
     *
     * @return
     */
    private ImageView voteIV() {
        return isIPad() ? mDatabinding.bottomBar.voteIv : mDatabinding.rightOpratorRl.voteIv;
    }

    /**
     * ????????????
     *
     * @return
     */
    private ImageView chatIV() {
        return isIPad() ? mDatabinding.bottomBar.chatIv : mDatabinding.rightOpratorRl.chatIv;
    }

    @Override
    public void onLiveTime(long time) {
        setLiveTime(time);
    }


    //------------------------?????????----------------------------------------------------------------------------------
    @Override
    public void onMultiMediaApplicate(int id, int docType, String title, int duration) {
        this.duration = duration;
        isMp4 = DocType.MP4 == docType;
        isApplicateMuitiMedia = true;
        setMultiMidiaViewWidth(false);
        initMultiMediaLayout(isMp4);
        mDatabinding.multimediaLayout.timeProgress.setMax(duration);
        setProgressBarTime(0, false);
        drawVisibility(false, false);
    }

    @Override
    public void onMultiMediaStatusChange(int status, int time, String msg) {
//        TalkFunLogger.i("status:"+status);
        switch (status) {
            case OnMultiMediaStatusChangeListener.STATUS_PLAY:
                setMultiMediaViewPauseVisiable(View.GONE);
                setProgressBarTime(time);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_SEEK:
                setProgressBarTime(time, false);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_PAUSE:
                setProgressBarTime(time);
                setMultiMediaViewPauseVisiable(View.VISIBLE);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_CLOSE:
                if (isMuitiMediaFullScreen) {
                    multiMediaViewFullScreen(false);
                }
                isApplicateMuitiMedia = false;
                setMultiMediaViewVisiable(View.GONE);
                drawVisibility(isDraw, false);
                break;
            case OnMultiMediaStatusChangeListener.STATUS_COMPLETE:
                setProgressBarTime(0, false);
                setMultiMediaViewPauseVisiable(View.VISIBLE);
                break;
        }
    }

    private void setMultiMidiaViewWidth(boolean isFullScreen) {
        if (isFullScreen) {
            mDatabinding.multimediaLayout.flMultiMediaParent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return;
        }
        int pptHeight = DimensionUtils.isPad(this) ? (int) (DimensionUtils.getScreenHeight(this) - this.getResources().getDimension((R.dimen.dp_107)))
                : (int) (DimensionUtils.getScreenHeight(this) - this.getResources().getDimension((R.dimen.dp_100)));
        int progressWidth = pptHeight * 16 / 9;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(progressWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mDatabinding.multimediaLayout.flMultiMediaParent.setLayoutParams(layoutParams);
    }

    @Override
    public void initMultiMediaLayout(boolean isMp4) {
        setMultiMediaViewVisiable(View.VISIBLE);
        mDatabinding.multimediaLayout.viewShade.setBackgroundColor(
                this.getResources().getColor(isMp4 ? R.color.transparency : R.color.multi_media_bg)
        );
        setMultiMediaViewPauseVisiable(View.VISIBLE);
        mDatabinding.multimediaLayout.ivMultiMediaDoctype.setVisibility(isMp4 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void setProgressBarTime(int time) {
        setProgressBarTime(time, true);
    }

    @Override
    public void setProgressBarTime(int time, boolean filter) {
        if (filter && mDatabinding.multimediaLayout.timeProgress.getProgress() > time) {
            return;
        }
        mDatabinding.multimediaLayout.timeProgress.setProgress(time);
        mDatabinding.multimediaLayout.tvTime.setText(TimeUtil.displayHHMMSS(time) + "/" + TimeUtil.displayHHMMSS(duration));
    }

    public void setMultiMediaViewVisiable(int visibility) {
        mDatabinding.multimediaLayout.flMultiMediaParent.setVisibility(visibility);
        rotate(false);
    }

    public void setMultiMediaViewPauseVisiable(int visibility) {
        lastVisibility = visibility;
        mDatabinding.multimediaLayout.tvPause.setVisibility(visibility);
        if (!isMp4) {
            rotate(visibility == View.GONE);
        }
    }

    @Override
    public void rotate(boolean isRotate) {
        if (lastRotate == isRotate) {
            return;
        }
        lastRotate = isRotate;
        if (isRotate) {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.startRotate();
        } else {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.stopRotate();
        }
    }

    @Override
    public void multiMediaViewFullScreen(boolean isFull) {
        mDatabinding.multimediaLayout.ivFullScreen.setSelected(isFull);
        isMuitiMediaFullScreen = isFull;
        setMultiMidiaViewWidth(isFull);
        if (isFull) {
            mDatabinding.flFullScreen.setVisibility(View.VISIBLE);
            if (mDatabinding.multimediaLayout.flMultiMediaParent.getParent() != null) {
                ((FrameLayout) mDatabinding.multimediaLayout.flMultiMediaParent.getParent()).removeView(mDatabinding.multimediaLayout.flMultiMediaParent);
            }
            mDatabinding.flFullScreen.addView(mDatabinding.multimediaLayout.flMultiMediaParent);
        } else {
            View view = mDatabinding.flFullScreen.getChildAt(0);
            if (view != null) {
                mDatabinding.flFullScreen.removeAllViews();
                mDatabinding.parentViewgroup.addView(view, 1);
            }
            mDatabinding.flFullScreen.setVisibility(View.GONE);
        }
        if (!isMp4 && lastRotate) {
            mDatabinding.multimediaLayout.ivMultiMediaDoctype.startRotate();
        }
    }

    @Override
    public void hideProgressBar() {
        mDatabinding.multimediaLayout.progressBarParent.setVisibility(View.GONE);
    }

    @Override
    public void showProgressBar() {
        mDatabinding.multimediaLayout.progressBarParent.setVisibility(View.VISIBLE);
    }


/*    @Override
    protected void onStart() {
        super.onStart();
        ScreenSwitchUtils.getInstance(this).start(this);
    }*/

}
