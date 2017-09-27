package com.summertaker.communitylistview.common;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.summertaker.communitylistview.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected String mTag = "== " + getClass().getSimpleName();
    protected String mVolleyTag = mTag;

    /**
     * 상태바 색상 설정하기
     */
    protected void setBaseStatusBar() {
        // https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }
}