package com.android.jwjy.zkktproduct;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class ModelHtmlUtils {
//    private static ModelHtmlUtils instance;
    private Activity activity;
    private TextView text;
    private Drawable pic;
    private String resource;
    public ModelHtmlUtils(Activity activity,TextView text){
        this.activity =activity;
        this.text = text;
    }
//    public static ModelHtmlUtils getInstance(Activity activity,TextView text){
//        if (instance == null){
//            instance = new ModelHtmlUtils(activity,text);
//        }
//        return instance;
//    }

    public void setHtmlWithPic(String resource){
        this.resource = resource;
        if (Build.VERSION.SDK_INT >= 24)
            text.setText(Html.fromHtml(resource,Html.FROM_HTML_MODE_COMPACT,imageGetter,null));
        else
            text.setText(Html.fromHtml(resource,imageGetter,null));

    }
    Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String s) {
            if (pic != null) {
                Log.d("TAG", "显示");
                return pic;
            }
            else {
                Log.d("TAG", "加载"+s);
                getPic(s);
            }
            return null;
        }
    };

    /**
     * 加载网络图片
     * @param s
     */
    private void getPic(final String s) {
        new Thread(() ->{
                try {
                    final Drawable drawable = Drawable.createFromStream(new URL(s).openStream(), "");
                    activity. runOnUiThread(() ->{
                            if (drawable != null) {
                                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                                DisplayMetrics outMetrics = new DisplayMetrics();
                                wm.getDefaultDisplay().getMetrics(outMetrics);
                                float picW = drawable.getIntrinsicWidth();
                                float picH = drawable.getIntrinsicHeight();
                                int width = outMetrics.widthPixels;
                                drawable.setBounds(0,0,(int)picW,(int)picH);
                                pic = drawable;
                                if (Build.VERSION.SDK_INT >= 24)
                                    text.setText(Html.fromHtml(resource,Html.FROM_HTML_MODE_COMPACT,imageGetter,null));
                                else
                                    text.setText(Html.fromHtml(resource,imageGetter,null));
                            }
                        });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
    }
}
