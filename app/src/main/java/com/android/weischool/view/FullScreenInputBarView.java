package com.android.weischool.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.weischool.R;
import com.android.weischool.entity.ExpressionEntity;
import com.android.weischool.event.OnExpressionListener;
import com.android.weischool.event.OnSendMessageListener;
import com.android.weischool.util.ExpressionUtil;
import com.android.weischool.util.KeyBoardUtils;
import com.android.weischool.util.SoftKeyboardStateWatcher;


/**
 * Created by Wallace on 2016/12/29.
 */
public class FullScreenInputBarView extends LinearLayout implements OnExpressionListener,SoftKeyboardStateWatcher.SoftKeyboardStateListener {
    private EditText etFullScreenInput;
    private ImageView ivFullScreenExpression;
    private ImageView ivFullScreenSend;

    private long preDismissTime = 0L;
    private boolean canInput = true;
    private OnSendMessageListener sendMessageListener;
    private IFocusChangeListener mFocusChangeListener;

    private ExpressionLayout expressionLayout;

    SoftKeyboardStateWatcher softKeyboardStateWatcher;

    public FullScreenInputBarView(Context context) {
        this(context, null);
    }

    public FullScreenInputBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenInputBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initEvent();

    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.layout_fullscreen_edt, null);
        etFullScreenInput = (EditText) view.findViewById(R.id.et_fullScreen_input);
        ivFullScreenSend = (ImageView) view.findViewById(R.id.iv_send_fullScreen);
        ivFullScreenExpression = (ImageView) view.findViewById(R.id.iv_expression_fullScreen);
        expressionLayout = (ExpressionLayout) view.findViewById(R.id.layout_expression);
        softKeyboardStateWatcher = new SoftKeyboardStateWatcher(etFullScreenInput.getRootView());
        this.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    }


    private void initEvent() {

        etFullScreenInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFocusChangeListener != null) {
                    mFocusChangeListener.focusChange(hasFocus);
                }
            }
        });
        //????????????
        ivFullScreenExpression.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canInput) //????????????
                    return;
                if (System.currentTimeMillis() - preDismissTime > 100) {
                    showOrCloseExpressionPopupWindow();
                }
            }
        });
        //????????????
        ivFullScreenSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canInput) //????????????
                    return;
                String content = etFullScreenInput.getText().toString().trim();
                sendMessage(content);
            }
        });
        expressionLayout.setOnEmotionSelectedListener(this);

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility != VISIBLE){
            KeyBoardUtils.closeKeybord(etFullScreenInput,getContext());
            expressionLayout.setVisibility(GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        softKeyboardStateWatcher.addSoftKeyboardStateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        softKeyboardStateWatcher.removeSoftKeyboardStateListener(this);
    }



    public String getText() {
        return etFullScreenInput.getText().toString();
    }

    public void setText(String text) {
        etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), text, "mipmap"));
        etFullScreenInput.setSelection(text.length());
    }

    //???????????????????????? 1 ????????????0?????????
    public void setCanInput(boolean value) {
        canInput = value;
        if (!canInput) {
            etFullScreenInput.setHint(getResources().getString(R.string.shutUp_input_tip));
            etFullScreenInput.setMaxLines(1);

            etFullScreenInput.setEnabled(false);
            ivFullScreenSend.setVisibility(INVISIBLE);
            this.setAlpha(0.5f);
        } else {
            etFullScreenInput.setHint(getResources().getString(R.string.input_your_text));
            etFullScreenInput.setMaxLines(10);
            etFullScreenInput.setEnabled(true);
            ivFullScreenSend.setVisibility(VISIBLE);
            this.setAlpha(1.0f);
        }
    }

    /**
     * ??????
     */
    public void reset() {
        etFullScreenInput.setText("");
        expressionLayout.setVisibility(GONE);
    }

    //-----------------------------------------?????????????????????---------------------------------------

    /**
     * ??????????????????
     *
     * @param content
     */
    public void sendMessage(final String content) {
        if (!TextUtils.isEmpty(content)) {
            if (sendMessageListener != null) {
                sendMessageListener.onSendMessage(content);
            }
            etFullScreenInput.setText("");
            KeyBoardUtils.closeKeybord(etFullScreenInput,getContext());
        }
    }

    /**
     * ????????????????????????
     */
    private void showOrCloseExpressionPopupWindow() {
        if(expressionLayout.getVisibility() != VISIBLE){
            KeyBoardUtils.closeKeybord(etFullScreenInput,getContext());

            expressionLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    expressionLayout.setVisibility(VISIBLE);
                }
            },100);
        }else if(expressionLayout.getVisibility() != GONE){
            expressionLayout.setVisibility(GONE);
            if(etFullScreenInput.isFocused()){
                KeyBoardUtils.openKeybord(etFullScreenInput,getContext());
            }

        }
    }

    //????????????
    @Override
    public void OnExpressionSelected(ExpressionEntity entity) {
        if (entity == null) return;
        //????????????
        String content = etFullScreenInput.getText().toString();
        content += entity.character;
        etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
        etFullScreenInput.setSelection(content.length());
    }

    //????????????
    @Override
    public void OnExpressionRemove() {
        String content = etFullScreenInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            int selectionStart = etFullScreenInput.getSelectionStart();
            int count = ExpressionUtil.dealContent(getContext(), content, selectionStart);
            content = etFullScreenInput.getText().delete(selectionStart - count, selectionStart).toString();
            etFullScreenInput.setText(ExpressionUtil.getExpressionString(getContext(), content, "mipmap"));
            etFullScreenInput.setSelection(selectionStart - count);
        }
    }

    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.sendMessageListener = listener;
    }

    public void setOnFocusChangeListener(IFocusChangeListener mFocusChangeListener) {
        this.mFocusChangeListener = mFocusChangeListener;
    }

    public interface IFocusChangeListener {
        void focusChange(boolean isFocus);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        if(getVisibility() != VISIBLE)
            return;
        if(expressionLayout.getVisibility() == VISIBLE)
            expressionLayout.setVisibility(GONE);
    }

    @Override
    public void onSoftKeyboardClosed() {
        if(getVisibility() != VISIBLE)
            return;

    }
}
