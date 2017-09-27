package com.summertaker.communitylistview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.summertaker.communitylistview.common.BaseActivity;
import com.summertaker.communitylistview.common.BaseApplication;
import com.summertaker.communitylistview.data.ArticleDetailData;
import com.summertaker.communitylistview.parser.TodayhumorParser;
import com.summertaker.communitylistview.util.ProportionalImageView;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailActivity extends BaseActivity {

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;

    private String mUrl;
    private ArticleDetailData mArticleDetailData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail_activity);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        mUrl = intent.getStringExtra("url");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        float density = getResources().getDisplayMetrics().density;
        int height = (int) (272 * density);
        int margin = (int) (10 * density);
        mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        mParams.setMargins(0, margin, 0, 0); // Ctrl + MouseOver
        mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

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
                TodayhumorParser todayhumorParser = new TodayhumorParser();
                mArticleDetailData = todayhumorParser.parseDetail(response);
            }
        }

        renderData();
    }

    private void renderData() {
        //Log.d(mTag, "==========================");
        //Log.d(mTag, mContent);

        if (mArticleDetailData.getThumbnails().size() > 0) {
            LinearLayout loPicture = findViewById(R.id.loPicture);
            //loPicture.removeAllViews();
            loPicture.setVisibility(View.VISIBLE);

            for (int i = 0; i < mArticleDetailData.getThumbnails().size(); i++) {
                //Log.e(TAG, "url[" + i + "]: " + imageArray[i]);

                final String thumbnail = mArticleDetailData.getThumbnails().get(i);
                final String image = mArticleDetailData.getImages().get(i);
                if (thumbnail.isEmpty()) {
                    continue;
                }

                final ProportionalImageView iv = new ProportionalImageView(this);
                //final ImageView iv = new ImageView(this);
                //if (i == imageArray.length - 1) {
                if (i == 0) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                //iv.setAdjustViewBounds(true);
                //iv.setBackgroundColor(getResources().getColor(R.color.card_background));
                //iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                loPicture.addView(iv);

                //int placeholder = R.drawable.placeholder_green;
                //if (thumbnailUrl.contains("nogizaka46")) {
                //    placeholder = R.drawable.placeholder_purple;
                //}

                Picasso.with(this).load(thumbnail).placeholder(R.drawable.placeholder).into(iv);

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.e(">>", imageUrl);
                        if (image != null && !image.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(image));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        TextView tvContent = findViewById(R.id.tvContent);
        tvContent.setText(mArticleDetailData.getContent());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
