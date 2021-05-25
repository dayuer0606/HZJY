package com.android.school;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 加载webview格式
 */

public class OfficeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
        Intent intent = getIntent();
        String nameString = intent.getStringExtra("url");
        WebView webView = findViewById(R.id.web_view);
        //使用微软打开，在线预览
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        /**
         * 加载完成后才关闭加载框
         */
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    //todo    可以在这里关闭弹窗
                }
            }
        });
        webView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src=" + nameString );//使用微软在线浏览方式
    }
}
