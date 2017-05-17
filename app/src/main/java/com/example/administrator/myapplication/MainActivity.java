package com.example.administrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout parent = (LinearLayout) findViewById(R.id.content);
        parent.addView(mWebView);

        mWebView.loadUrl("file:///android_asset/docs/examples/full.html");
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            try {
                ViewParent parent = mWebView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mWebView);
                }
                mWebView.stopLoading();
                mWebView.getSettings().setJavaScriptEnabled(false);
                mWebView.clearHistory();
                mWebView.clearView();
                mWebView.removeAllViews();
                mWebView.destroy();
            } catch (Exception e) {
            }
        }
        super.onDestroy();
    }
}
