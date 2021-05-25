package com.android.school;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by dayuer on 19/7/2.
 * 公共通用对话框
 */
public class ModelCommonDialog extends Dialog {
//    /**
//     * 显示的图片
//     */
//    private ImageView imageIv ;

    /**
     * 显示的标题
     */
    private TextView titleTv ;

    /**
     * 显示的消息
     */
    private TextView messageTv ;

    private WebView dialog_content;

    private boolean isWebView = false;

    /**
     * 确认和取消按钮
     */
    private Button negtiveBn ,positiveBn;

    /**
     * 按钮之间的分割线
     */
    private View columnLineView ;
    public ModelCommonDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    /**
     * 都是内容数据
     */
    private String message;
    private String title;
    private String positive,negtive ;
    private int imageResId = -1 ;

    /**
     * 底部是否只有一个按钮
     */
    private boolean isSingle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_dialog_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener(v->{
            if ( onClickBottomListener!= null) {
                onClickBottomListener.onPositiveClick();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negtiveBn.setOnClickListener(v ->{
            if ( onClickBottomListener!= null) {
            onClickBottomListener.onNegtiveClick();
        }});
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void refreshView() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        }else {
            titleTv.setVisibility(View.GONE);
        }
        if (isWebView) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) messageTv.getLayoutParams();
            layoutParams.height = 0;
            messageTv.setLayoutParams(layoutParams);
            layoutParams = (LinearLayout.LayoutParams) dialog_content.getLayoutParams();
            layoutParams.height = (int) getContext().getResources().getDimension(R.dimen.dp_300);
            dialog_content.setLayoutParams(layoutParams);
            if (!TextUtils.isEmpty(message)) {
                try {
                    dialog_content.getSettings().setJavaScriptEnabled(true);
                    dialog_content.getSettings().setUseWideViewPort(true);
                    dialog_content.getSettings().setAllowFileAccessFromFileURLs(true);
                    dialog_content.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    dialog_content.loadUrl(message);
                } catch (Exception e) {
                    Log.e("TAG", "refreshView: " + e.getMessage());
                }
            }
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialog_content.getLayoutParams();
            layoutParams.height = 0;
            dialog_content.setLayoutParams(layoutParams);
            if (!TextUtils.isEmpty(message)) {
                messageTv.setText(message);
            }
        }
        //如果设置按钮的文字
        if (!TextUtils.isEmpty(positive)) {
            positiveBn.setText(positive);
        }else {
            positiveBn.setText("确定");
        }
        if (!TextUtils.isEmpty(negtive)) {
            negtiveBn.setText(negtive);
        }else {
            negtiveBn.setText("取消");
        }

//        if (imageResId!=-1){
//            imageIv.setImageResource(imageResId);
//            imageIv.setVisibility(View.VISIBLE);
//        }else {
//            imageIv.setVisibility(View.GONE);
//        }
        /**
         * 只显示一个按钮的时候隐藏取消按钮，回掉只执行确定的事件
         */
        if (isSingle){
            columnLineView.setVisibility(View.GONE);
            negtiveBn.setVisibility(View.GONE);
        }else {
            negtiveBn.setVisibility(View.VISIBLE);
            columnLineView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        negtiveBn = findViewById(R.id.negtive);
        positiveBn = findViewById(R.id.positive);
        titleTv = findViewById(R.id.title);
        messageTv = findViewById(R.id.message);
//        imageIv = (ImageView) findViewById(R.id.image);
        dialog_content = findViewById(R.id.dialog_content);
        columnLineView = findViewById(R.id.column_line);
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;
    public ModelCommonDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }
    public interface OnClickBottomListener{
        /**
         * 点击确定按钮事件
         */
        public void onPositiveClick();
        /**
         * 点击取消按钮事件
         */
        public void onNegtiveClick();
    }

    public String getMessage() {
        return message;
    }

    public ModelCommonDialog setMessage(String message) {
        isWebView = false;
        this.message = message;
        return this ;
    }

    public ModelCommonDialog setWebViewContent(String message) {
        isWebView = true;
        this.message = message;
        return this ;
    }
    public String getTitle() {
        return title;
    }

    public ModelCommonDialog setTitle(String title) {
        this.title = title;
        return this ;
    }

    public String getPositive() {
        return positive;
    }

    public ModelCommonDialog setPositive(String positive) {
        this.positive = positive;
        return this ;
    }

    public String getNegtive() {
        return negtive;
    }

    public ModelCommonDialog setNegtive(String negtive) {
        this.negtive = negtive;
        return this ;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public ModelCommonDialog setSingle(boolean single) {
        isSingle = single;
        return this ;
    }

    public ModelCommonDialog setImageResId(int imageResId) {
        this.imageResId = imageResId;
        return this ;
    }
}
