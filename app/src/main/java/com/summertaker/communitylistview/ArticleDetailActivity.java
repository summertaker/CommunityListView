package com.summertaker.communitylistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.communitylistview.common.BaseApplication;
import com.summertaker.communitylistview.parser.Todayhumor;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity {

    private String mTag = "== " + this.getClass().getSimpleName();
    private String mVolleyTag = mTag;

    private String mUrl;
    private String mContent = "";

    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mTvContent = findViewById(R.id.tvContent);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");

        requestData();
    }

    private void requestData() {
        Log.e(mTag, mUrl);

        StringRequest strReq = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseData("");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", BaseApplication.getInstance().getMobileUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void parseData(String response) {
        if (!response.isEmpty()) {
            if (mUrl.contains("todayhumor")) {
                Todayhumor parser = new Todayhumor();
                mContent = parser.parseDetail(response);
            }
        }

        renderData();
    }

    private void renderData() {
        Log.d(mTag, "==========================");
        Log.d(mTag, mContent);

        mTvContent.setText(Html.fromHtml(mContent));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
