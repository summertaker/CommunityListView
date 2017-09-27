package com.summertaker.communitylistview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.summertaker.communitylistview.common.BaseActivity;

public class WebViewActivity extends BaseActivity {

    String mUrl;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");

        mWebView = findViewById(R.id.webView);
        //if (url.contains("theqoo.net")) {
        //    WebSettings webSettings = mWebView.getSettings();
        //    webSettings.setJavaScriptEnabled(true);
        //}

        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.loadUrl(mUrl);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //mCallback.onWebFragmentEvent("onPageStarted", mWebView.getUrl(), mWebView.canGoBack());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //mCallback.onWebFragmentEvent("onPageFinished", mWebView.getUrl(), mWebView.canGoBack());
        }
    }

    public boolean goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public void goTop() {
        mWebView.scrollTo(0, 0);
    }

    public void refresh() {
        mWebView.reload();
    }

    public void openInNew() {
        String url = mWebView.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void share() {
        String title = mWebView.getTitle();
        String url = mWebView.getUrl();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, title));
    }
}
