package com.android.weischool.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.weischool.R;
import com.android.weischool.consts.EventType;
import com.android.weischool.consts.MainConsts;
import com.android.weischool.dialog.SwitchLineDialogFragment;
import com.android.weischool.entity.Event;
import com.android.weischool.event.SimpleGestureLayoutListener;
import com.android.weischool.fragment.PlaybackSectionFragment;
import com.android.weischool.helper.NetChoiseDiologHelper;
import com.android.weischool.util.ACache;
import com.android.weischool.util.DanmakuFlameUtil;
import com.android.weischool.util.DimensionUtils;
import com.android.weischool.util.LogUtil;
import com.android.weischool.util.ScreenSwitchUtils;
import com.android.weischool.util.SeekBarHelper;
import com.android.weischool.util.SharedPreferencesUtil;
import com.android.weischool.util.StringUtils;
import com.android.weischool.util.TimeUtil;
import com.android.weischool.view.PlaybackMessageView;
import com.talkfun.sdk.HtSdk;
import com.talkfun.sdk.config.ADConfig;
import com.talkfun.sdk.consts.PlayerLoadState;
import com.talkfun.sdk.consts.TFMode;
import com.talkfun.sdk.consts.VideoStatus;
import com.talkfun.sdk.data.PlaybackDataManage;
import com.talkfun.sdk.event.ErrorEvent;
import com.talkfun.sdk.event.OnADVideoListener;
import com.talkfun.sdk.event.OnVideoChangeListener;
import com.talkfun.sdk.event.OnVideoStatusChangeListener;
import com.talkfun.sdk.event.PlaybackListener;
import com.talkfun.sdk.module.AlbumItemEntity;
import com.talkfun.sdk.module.ModuleConfigHelper;
import com.talkfun.sdk.module.PlaybackInfo;
import com.talkfun.sdk.module.User;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import master.flame.danmaku.ui.widget.DanmakuView;

public class PlaybackNativeActivity extends BasePlayActivity implements
        PlaybackListener, PlaybackSectionFragment.PlaybackSectionInterface, View.OnTouchListener
        , OnVideoStatusChangeListener, ErrorEvent.OnErrorListener, OnADVideoListener {

    //-------------------------------ui---------------------------------
    @BindView(R.id.operation_btn_container)
    LinearLayout operationContainer;
    @BindView(R.id.video_visibility_iv)
    ImageView videoVisibleIv;
    @BindView(R.id.seek_bar_layout)
    LinearLayout seekbarLayout;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.iv_play)
    ImageView playBtn;
    @BindView(R.id.total_duration)
    TextView totalDuration;
    @BindView(R.id.current_duration)
    TextView currentDuration;
    @BindView(R.id.iv_go_back)
    ImageView goBack;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.tab_container)
    PlaybackMessageView mPlaybackMessageView;
    //    @BindView(R.id.iv_start_download)
//    ImageView ivStartDownload;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;
    @BindView(R.id.iv_danmu_switch)
    ImageView ivDanmuSwitch;

    @BindView(R.id.fl_ad_parent)
    FrameLayout ADLayoutFrameLayoutParent;
    @BindView(R.id.fl_ad)
    FrameLayout ADLayoutFrameLayout;

    @BindView(R.id.iv_ad_full_screen)
    ImageView adFullScreenIV;

    @BindView(R.id.tv_count_down)
    TextView countDownTV;

    @BindView(R.id.tv_skip_ad)
    TextView skipADTV;
    //--------------------------------------------------------------------------------
    private static final String SKIP_AD = "PALYBACK_SKIP_AD";
    private SwitchLineDialogFragment switchLineDialogFragment;
    private boolean mIsPlaying = true;
    private String mId;
    private SeekBarHelper seekBarHelper;
    private ListPopupWindow playSpeedlpw;


    private static final String TAG = PlaybackNativeActivity.class.getName();
    private static final String[] playSpeedStrs = {"0.75X", "1.0X", "1.2X", "1.5X"};
    private static final float[] playSpeeds = {0.75f, 1.0f, 1.2f, 1.5f};
    private HtSdk mHtSdk;

    private NetChoiseDiologHelper mNetChoiseDiologHelper; //??????????????????

    DanmakuFlameUtil danmakuFlameUtil; //???????????????
    private int adVideoDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.playback_layout;
    }

    @Override
    protected void init() {
        super.init();
        mId = getIntent().getStringExtra("id");
        MainConsts.PlayBackID = mId;

    }


    /**
     * ???????????????
     */
    protected void initView() {
        super.initView();
//        ivStartDownload.setVisibility(View.VISIBLE);
        showVideoContainer(videoVisibleIv, false);
        operationContainer.bringToFront();  //????????????????????????
        seekbarLayout.bringToFront();
        goBack.bringToFront();

        hideTitleBar();  // ??????????????????????????????
        initDanmaku();
        updateLayout();
        seekBarHelper = new SeekBarHelper(this, seekBar);
        mHtSdk = HtSdk.getInstance();
        // mHtSdk.init(pptContainer, videoViewContainer, mToken, true);
        mHtSdk.init(this, pptContainer, videoViewContainer, mToken, TFMode.PLAYBACK_NORMAL);
        /**??????????????????/??????????????????
         * ???????????????????????????????????????????????????PPT??????????????????????????????????????????????????????????????????
         * */
        mHtSdk.setDesktopVideoContainer(desktopVideoContainer);
        mHtSdk.setADVideoContainer(ADLayoutFrameLayout);
        mHtSdk.setADVideoListener(this);
        mHtSdk.setFilterQuestionFlag(false);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View loadingView = layoutInflater.inflate(R.layout.loading_layout, null);
        View loadFailView = layoutInflater.inflate(R.layout.load_fail_layout, null);
        mHtSdk.setLoadingView(loadingView); //??????????????????????????????view
        mHtSdk.setLoadFailView(loadFailView);//??????????????????????????????view
        mHtSdk.setWhiteboardLoadFailDrawable(getResources().getDrawable(R.mipmap.image_broken));
        //????????????????????????????????????
        //?????????true,?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //?????????false,???????????????????????????????????????????????????????????????????????????????????????
        //?????????true
        mHtSdk.setIsPlayOffline(mId, true);
        initHelper();
    }

    private void initHelper() {
        mPlaybackMessageView.addTokenAndId(mToken, mId);
        mPlaybackMessageView.addSeekBarUtil(seekBarHelper);
    }

    /**
     * ??????????????????
     */

    private void initDanmaku() {
        ivDanmuSwitch.setSelected(false);
        danmakuFlameUtil = new DanmakuFlameUtil(danmakuView);
        danmakuFlameUtil.hide();
    }


    /**
     * ???????????????
     */
    private void initEvent() {
        videoViewContainer.setOnTouchListener(this);

        //??????????????????????????????
        mHtSdk.setPlaybackListener(this);


        //???????????????????????????????????????
        mHtSdk.setOnVideoStatusChangeListener(this);
        /**??????????????????????????????*/
        mHtSdk.setOnVideoChangeListener(new OnPlayVideoChangeLister());
        //????????????
        mHtSdk.setOnErrorListener(this);
        //???????????????????????????????????????
        //seekBarHelper.addTouchSlidSeekEvent(pptLayout);

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

        mHtSdk.setUpdatePlayTimeListener(time -> {
            if(seekBarHelper != null){
                seekBarHelper.updatePlayTime(time);
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
            public void onStartFastSeekOffset() {
                seekBarHelper.startFastSeek(pptLayout);
            }

            @Override
            public void onFastSeekOffset(float offsetPercentage) {
                seekBarHelper.fastSeekOffset(offsetPercentage);
            }

            @Override
            public void onStopFastSeekOffset() {
                seekBarHelper.stopFastSeek();
            }

            @Override
            public boolean onDown() {
                return true;
            }

            @Override
            public boolean onClick() {
                if (seekBarHelper.isShowPoped) {
                    seekBarHelper.isShowPoped = false;
                    return true;
                }
                if (isTitleBarShow) {
                    hideTitleBar();
                } else {
                    showTitleBar();
                }
                return true;
            }

            @Override
            public boolean onDoubleClick() {
                onFullScreenChange();
                return true;
            }
        });
    }

    private boolean isAdFullScreen;

    @OnClick({R.id.fullScreen_iv, R.id.video_visibility_iv, R.id.iv_play, R.id.iv_go_back, R.id.iv_ad_go_back,
            R.id.exchange, R.id.network_choice_iv, R.id.iv_refresh, R.id.tv_speed, R.id.iv_danmu_switch,
            R.id.tv_skip_ad, R.id.iv_ad_full_screen})
    void onClick(View v) {
        Log.d(getClass().getName(), v.getClass().getName());
        switch (v.getId()) {
            case R.id.fullScreen_iv:  //??????
                onFullScreenChange();
                break;
            case R.id.iv_ad_full_screen:
                onFullScreenChange();
                isAdFullScreen = !isAdFullScreen;
                adFullScreenIV.setSelected(isAdFullScreen);
                break;
            case R.id.video_visibility_iv:  //???????????????
                onVideoVisible(videoVisibleIv);
                break;
            case R.id.iv_play:  //???????????????
                if (mIsPlaying) {
                    pause();
                    mIsPlaying = false;
                } else {
                    play();
                    mIsPlaying = true;
                }
                break;
            case R.id.iv_go_back:  //??????
                gobackAction();
                break;
            case R.id.iv_ad_go_back:
                finish();
                break;
            case R.id.exchange:
                /**??????ppt??????????????????????????????*/
                if (!isVideoViewContainerVisiable()) {
                    return;
                }

                isExchangeViewContainer = !isExchangeViewContainer;
                mHtSdk.exchangeVideoAndWhiteboard();
                break;
            case R.id.network_choice_iv: //??????????????????
                //showSwitchLineDialog();
                if (mNetChoiseDiologHelper == null) {
                    mNetChoiseDiologHelper = new NetChoiseDiologHelper(PlaybackNativeActivity.this);
                }
                mNetChoiseDiologHelper.showNetworkChoiceDialog();
                break;
            case R.id.iv_refresh: //??????
                exchangeViewContainer();
                videoViewContainer.setVisibility(View.INVISIBLE);
                cachePlayTime();
                mHtSdk.reload();
                break;
            case R.id.tv_speed:
                showOrHideSpeedList(v);
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
            case R.id.tv_skip_ad:  //????????????
                mHtSdk.skipAD();
                break;
            default:
                break;
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param anchor
     */
    private void showOrHideSpeedList(View anchor) {
        stopDismissTitleBar();
        if (playSpeedlpw != null && playSpeedlpw.isShowing()) {
            playSpeedlpw.dismiss();
            playSpeedlpw = null;
            autoDismissTitleBar();
            return;
        }
        if (playSpeedlpw == null) {
            playSpeedlpw = new ListPopupWindow(this);
            playSpeedlpw.setAdapter(new ArrayAdapter<>(this, R.layout.speed_popup_window_item, R.id.list_pop_item, playSpeedStrs));
            playSpeedlpw.setOnItemClickListener((parent, view, position, id) -> {
                tvSpeed.setText(playSpeedStrs[position]);
                mHtSdk.setPlaybackPlaySpeed(playSpeeds[position]);
                playSpeedlpw.dismiss();
                playSpeedlpw = null;
                autoDismissTitleBar();
            });
            playSpeedlpw.setOnDismissListener(() -> autoDismissTitleBar());
            playSpeedlpw.setBackgroundDrawable(null);
        }

        int lpwWidth = DimensionUtils.dip2px(this, 100);
        int lpwHeight = DimensionUtils.dip2px(this, 150);
        if (anchor != null) {
            playSpeedlpw.setAnchorView(anchor);
            playSpeedlpw.setVerticalOffset(-lpwHeight);
            playSpeedlpw.setHorizontalOffset(-(lpwWidth - anchor.getWidth()) / 2);
        }

        playSpeedlpw.setModal(true);
        playSpeedlpw.setWidth(lpwWidth);
        playSpeedlpw.setHeight(lpwHeight);
        playSpeedlpw.show();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (playSpeedlpw != null && playSpeedlpw.isShowing()) {
            playSpeedlpw.dismiss();
            // playSpeedlpw.setAnchorView(null);
            playSpeedlpw = null;
            autoDismissTitleBar();
            return;
        }
        super.onConfigurationChanged(newConfig);
    }

    //TODO-------------------------------------------view changed------------------------------------

    /**
     * ??????????????????????????????
     */
    @Override
    void showController() {
        seekbarLayout.setVisibility(View.VISIBLE);
        operationContainer.setVisibility(View.VISIBLE);
        titleBar.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
    }

    /**
     * ??????????????????????????????
     */
    @Override
    void hideController() {
        if (seekbarLayout == null)
            return;
        seekbarLayout.setVisibility(View.INVISIBLE);
        operationContainer.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        playBtn.setVisibility(View.GONE);
    }


    /**
     * ???????????????
     */
    @Override
    public void layoutChanged() {
        super.layoutChanged();
        //?????????????????????
        boolean isPortrait = ScreenSwitchUtils.getInstance(this).isPortrait();
        boolean isSensorNotFullLandScreen = ScreenSwitchUtils.getInstance(this).isSensorNotFullLandScreen();
        totalDuration.setVisibility(isPortrait == false ? View.VISIBLE : View.GONE);
        //??????????????????
//        if ((mHtSdk != null && mHtSdk.isPlayLocal()) || isSensorNotFullLandScreen) {
//            mPlaybackMessageView.hideDownloadButton();
//        } else {
//            mPlaybackMessageView.showDownloadButton();
//        }
        // currentDuration.setVisibility(isPortrait == false ? View.VISIBLE : View.GONE);

    }

    //TODO-------------------------------------------?????????????????????????????????------------------------------
//    /**
//     * ??????Fragment
//     */
//    private PlaybackAlbumFragment albumFragment;

    /**
     * ???????????????????????????
     */

    @Override
    public void initSuccess() {
        showTitleBar();
//        showVideoContainer();
        userVideoShow = true;
//        showVideoContainer(videoVisibleIv, true);
        videoVisibleIv.setSelected(mHtSdk.isVideoShow());
        setSeekBar();
        if (mPlaybackMessageView != null) {
            /**
             * ????????????????????????viewpaper??????PlaybackAlbumFragment,???Tab???????????????Tab
             */
            mPlaybackMessageView.addAlbumFragment();
        }
        //??????????????????
//        if (mHtSdk.isPlayLocal()) {
//            mPlaybackMessageView.hideDownloadButton();
//        } else {
//            mPlaybackMessageView.showDownloadButton();
//        }

        ModuleConfigHelper moduleConfigHelper = mHtSdk.getModuleConfigHelper();
        if (moduleConfigHelper != null && moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_THEFTPROOF_PLAYBACK)) {
            User user = PlaybackInfo.getInstance().getUser();
            startShowWatermark(user != null ? user.getUid() : PlaybackInfo.getInstance().getLiveId());
        }

        //?????????????????????????????????????????????????????????
        if (moduleConfigHelper != null &&
                (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_PLAYBACK) || !moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_PLAYBACK_CHAT))
        ) {
            mPlaybackMessageView.hideChatFragment();
            showFullScreenInput(false);
        } else {
            mPlaybackMessageView.showChatFragment();
            updateLayout();
        }

        //?????????????????????????????????????????????????????????
        if (moduleConfigHelper != null &&
                (!moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_PLAYBACK) || !moduleConfigHelper.getModuleEnable(ModuleConfigHelper.KEY_MOD_TEXT_PLAYBACK_QA))
        ) {
            mPlaybackMessageView.hideQuestionFragment();
        } else {
            // mLiveMessageView.showQuestionFragment();
            mPlaybackMessageView.showQuestionFragment();
        }

        Long playTime = getCachePlayTime();
        if (playTime > 0) {
            mHtSdk.playbackSeekTo(playTime);
        }
/*        PlaybackDataManage.getInstance().setBroadcastListener(new PlaybackBroadcastListener() {
            @Override
            public void onBroadcast(JSONObject jsonObject) {
                TalkFunLogger.i("jsonObject"+jsonObject.toString());
            }

            @Override
            public void onBroadcastArray(JSONArray jsonArray) {
                TalkFunLogger.i("jsonArray"+jsonArray.toString());
            }
        });*/

    }

    private void setSeekBar() {
        seekBar.setClickable(true);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        long duration = PlaybackInfo.getInstance().getDurationLong();

        seekBar.setMax((int) duration);
        totalDuration.setText(TimeUtil.displayHHMMSS((int) duration));
    }

    /**
     * ???????????????
     *
     * @param msg
     */
    @Override
    public void onInitFail(String msg) {
        Log.d(TAG, "onInitFail:" + msg);
    }

    protected void registerNetWorkStateReceiver() {
        //???????????????????????????????????????????????????????????????
        if (mHtSdk != null && mHtSdk.isPlayLocal()) {
            return;
        }
        super.registerNetWorkStateReceiver();
    }

    //TODO-----------------------------------------activity life------------------------------------------------
    @Override
    public void onBackPressed() {
        gobackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHtSdk.onPause();
    }

    @Override
    protected void onDestroy() {
        cachePlayTime();
        super.onDestroy();
        onSeekBarChangeListener = null;
        if (mPlaybackMessageView != null) {
            mPlaybackMessageView.clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    //TODO--------------------------------------------??????????????????-----------------------------------------

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentDuration.setText(TimeUtil.displayHHMMSS(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            seekTo(progress);
        }
    };


    private void play() {
        setPlayingStatus();
        mHtSdk.playbackResume();
    }

    private void pause() {
        setPauseStatus();
        mHtSdk.playbackPause();

    }

    private void setPlayingStatus() {
        playBtn.setImageResource(R.mipmap.pause);
        mIsPlaying = true;
    }

    private void setPauseStatus() {
        playBtn.setImageResource(R.mipmap.play);
        mIsPlaying = false;
    }

    private void setStopStatus() {
        setPauseStatus();
        seekBarHelper.resetSeekBarProgress();
    }

    /**
     * ????????????????????????
     */
    @Override
    public void seekTo(int progress) {
        seekBarHelper.seekTo(progress);
    }

    /**
     * ??????????????????
     *
     * @param status ????????????
     * @param msg    ??????
     */
    @Override
    public void onVideoStatusChange(int status, String msg) {
        switch (status) {
            case VideoStatus.STATUS_PAUSE:
                setPauseStatus();
                break;
            case VideoStatus.STATUS_PLAYING:
                setPlayingStatus();
                break;
            case VideoStatus.STATUS_ERROR:
                StringUtils.tip(getApplicationContext(), msg);
                break;
            case VideoStatus.STATUS_IDLE:
                setStopStatus();
                break;
            case VideoStatus.STATUS_COMPLETED:
                Log.d(TAG, "completed");
                seekBarHelper.resetSeekBarProgress();
                // ???????????????????????????
                if (PlaybackInfo.getInstance().isAlbum()) {
                    int currentIndex = PlaybackInfo.getInstance().getCurrentAlbumIndex();
                    List<AlbumItemEntity> albumItemEntities = PlaybackDataManage.getInstance().getAlbumList();
                    if ((albumItemEntities.size() <= 1) || (currentIndex >= albumItemEntities.size() - 1)) {
                        currentIndex = 0;
                    } else {
                        currentIndex++;
                    }

                    mHtSdk.playAlbum(albumItemEntities.get(currentIndex));
                    return;
                }
                mHtSdk.replayVideo();
                break;
            case VideoStatus.STATUS_BUFFERING:
                Log.d(TAG,"STATUS_BUFFERING");
                break;
            case VideoStatus.STATUS_SEEKING:
                Log.d(TAG,"STATUS_SEEKING");
                break;
            case VideoStatus.STATUS_SEEKCOMPLETE:
                Log.d(TAG,"STATUS_SEEKCOMPLETE");
                break;
        }
    }

    @Override
    public void error(int code, String msg) {
        Toast.makeText(this, code + "------>>" + msg, Toast.LENGTH_SHORT).show();
        LogUtil.e("error:", code + "------>>" + msg);
    }

    @Override
    public void eventCallback(Event message) {
        if (message != null) {
            int type = message.getType();
            switch (type) {
                case EventType.ADDDANMAKU:
                    if (danmakuFlameUtil == null) return;
                    if (danmakuFlameUtil.isShown()) {   //???????????? view ???????????????????????????
                        danmakuFlameUtil.addDanmaku((SpannableString) message.getData(), false);
                    }
                    break;
            }
        }
    }


  /*  private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final int MAX_COUNT = Integer.MAX_VALUE; // ??????????????????????????????*/

    static final int CACHE_VOD_PLAY_TIME_MAX_COUNT = 100;
    static final int CACHE_VOD_PLAY_TIME_MAX_SIZE = 1000 * 1000 * 50; //50mb
    static final String CACHE_VOD_PLAY_TIME_FILE_NAME = "vod_cache_play_time";
    ACache aCache;

    /**
     * ????????????????????????
     *
     * @return
     */
    public long getCachePlayTime() {
        String id = mHtSdk.getPlaybackInfo().getLiveId();
        ensureACache();
        return StringUtils.getLong(aCache.getAsString(id), 0);
    }

    /**
     * ??????????????????
     */
    public void cachePlayTime() {
        ensureACache();
        String id = mHtSdk.getPlaybackInfo().getLiveId();
        long time = mHtSdk.getVideoCurrentTime();
        if (TextUtils.isEmpty(id))
            return;
        aCache.put(id, String.valueOf(time));
    }


    private void ensureACache() {
        if (aCache == null) {
            File f = new File(getCacheDir(), CACHE_VOD_PLAY_TIME_FILE_NAME);
            aCache = ACache.get(f, CACHE_VOD_PLAY_TIME_MAX_SIZE, CACHE_VOD_PLAY_TIME_MAX_COUNT);
        }
    }

    @Override
    public void onADPrepare(ADConfig ADConfig) {
        int duration = ADConfig.getDuration();
        ADLayoutFrameLayoutParent.setVisibility(View.VISIBLE);
        boolean isSkip = !SharedPreferencesUtil.getBoolean(this, SKIP_AD + mId) && ADConfig.isSkipAD();
        setCountDownTime(duration);
        String skipAd = isSkip ? " S | ????????????" : "S";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(skipAd);
        if (isSkip) {
            ForegroundColorSpan backgroundColorSpan = new ForegroundColorSpan(Color.parseColor("#80FFFFFF"));
            int index = skipAd.indexOf("|");
            spannableStringBuilder.setSpan(backgroundColorSpan, index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        skipADTV.setClickable(isSkip);
        skipADTV.setText(spannableStringBuilder.toString());
    }

    @Override
    public void onADVideoStatusChange(int status, String msg) {
        switch (status) {
/*            case OnVideoStatusChangeListener.STATUS_PLAYING:
                countDownTV.startCountDown(adVideoDuration);
                break;*/
            case OnVideoStatusChangeListener.STATUS_ERROR:
                StringUtils.tip(getApplicationContext(), msg);
                break;
/*            case OnVideoStatusChangeListener.STATUS_PAUSE:
                adVideoDuration = Integer.parseInt(msg);
                break;*/
            case OnVideoStatusChangeListener.STATUS_IDLE:
            case OnVideoStatusChangeListener.STATUS_COMPLETED:
                SharedPreferencesUtil.saveBoolean(this, SKIP_AD + mId, false);
                ADLayoutFrameLayoutParent.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void OnADCountDownTime(int time) {
        setCountDownTime(time);
    }

    private void setCountDownTime(int time) {
        String timeString = String.valueOf(time);
        if (timeString.length() == 1) {
            timeString = "0" + timeString;
        }
        countDownTV.setText(timeString);
    }

    /**
     * ??????????????????????????????
     */
    //TODO--------------------------------------------??????????????????????????????-------------------------------
    class OnPlayVideoChangeLister implements OnVideoChangeListener {
        /**
         * ??????????????????
         *
         * @param mode ????????????
         */
        @Override
        public void onVideoStart(int mode) {
        }

        /**
         * ??????????????????
         *
         * @param mode ????????????
         */
        @Override
        public void onVideoStop(int mode) {

        }

        /**
         * ??????????????????????????????
         *
         * @param beforeMode  ???????????????
         * @param currentMode ???????????????
         */
        @Override
        public void onVideoModeChanging(int beforeMode, int currentMode) {

        }

        /**
         * ??????????????????
         */
        @Override
        public void onVideoModeChanged() {

        }

        /**
         * ?????????????????????
         */
        @Override
        public void onCameraShow() {
            if (!userVideoShow) return;
            showVideoContainer(videoVisibleIv, true);
        }

        /**
         * ??????????????????
         */
        @Override
        public void onCameraHide() {
            exchangeViewContainer();
            showVideoContainer(videoVisibleIv, false);
        }
    }

}
