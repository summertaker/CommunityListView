package com.summertaker.communitylistview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.communitylistview.common.BaseApplication;
import com.summertaker.communitylistview.data.ArticleData;
import com.summertaker.communitylistview.data.SiteData;
import com.summertaker.communitylistview.parser.Todayhumor;
import com.summertaker.communitylistview.util.EndlessScrollListener;
import com.summertaker.communitylistview.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleListFragment extends Fragment implements ArticleListInterface {

    private String mTag = "== " + this.getClass().getSimpleName();
    private String mVolleyTag = mTag;

    private ArticleListFragmentListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshMode = false;

    //private ProgressBar mPbLoading;
    //private LinearLayout mLoLoadMore;

    //private String mUserAgent;
    //private String mRequestUrl;

    private SiteData mSiteData;
    private int mCurrentPage = 1;
    private boolean mIsLoading = false;

    private ArrayList<ArticleData> mArticleList;
    private ListView mListView;
    protected ArticleListAdapter mAdapter;

    // Container Activity must implement this interface
    public interface ArticleListFragmentListener {
        public void onArticleListFragmentEvent(String event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mListener = (ArticleListFragmentListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    public ArticleListFragment() {
    }

    public static ArticleListFragment newInstance(int position) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        //args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.article_list_fragment, container, false);

        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        //mPbLoading = rootView.findViewById(R.id.pbLoading);
        //mLoLoadMore = rootView.findViewById(R.id.loLoadMore);

        mArticleList = new ArrayList<>();

        mListView = rootView.findViewById(R.id.listView);
        mAdapter = new ArticleListAdapter(getContext(), mArticleList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArticleData data = (ArticleData) adapterView.getItemAtPosition(i);

                String url = data.getUrl();

                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //startActivity(intent);

                //Intent intent = new Intent(getActivity(), ArticleViewActivity.class);
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("url", url);

                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!mIsRefreshMode) {
                    loadData();
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                } else {
                    return false;
                }
            }
        });

        int position = getArguments().getInt("position");

        mSiteData = BaseApplication.getInstance().getSiteList().get(position);
        //mUserAgent = mSiteData.getUserAgent();
        //mRequestUrl = mSiteData.getUrl();

        /*
        // MainActivity에서 미리 권한을 획득함
        String path = BaseApplication.getDataPath();
        File dir = new File(path);
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            //if (isSuccess) {
            //    Log.d(mTag, "created.");
            //} else {
            //    Log.d(mTag, "mkdir failed.");
            //}
            //} else {
            //Log.d(mTag, "exists.");
        }
        */

        loadData();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    /**
     * 데이터 로드하기
     */
    private void loadData() {
        if (mIsLoading) {
            if (mIsRefreshMode) {
                onRefreshFinished();
            }
            return;
        }

        mIsLoading = true;

        mListener.onArticleListFragmentEvent("onLoadDataStarted");

        requestData();
    }

    private void requestData() {
        String url = mSiteData.getUrl();

        if (mCurrentPage > 1) {
            url += mSiteData.getPageParam() + mCurrentPage;
            //mLoLoadMore.setVisibility(View.VISIBLE);
        }
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                headers.put("User-agent", mSiteData.getUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(response);
    }

    private void parseData(String response) {
        if (!response.isEmpty()) {
            if (mIsRefreshMode) {
                mArticleList.clear();
            }
            if (mSiteData.getUrl().contains("todayhumor")) {
                Todayhumor parser = new Todayhumor();
                parser.parseList(response, mArticleList);
            }
        }

        renderData();
    }

    private void renderData() {
        //Log.d(mTag, "mMemberList.size(): " + mMemberList.size());

        //if (mMemberList.size() == 0) {
        //    //alertNetworkErrorAndFinish(mErrorMessage);
        //} else {
        //    //gridView.setOnItemClickListener(itemClickListener);
        //}

        mAdapter.notifyDataSetChanged();

        if (mIsRefreshMode) {
            onRefreshFinished();
        } else {
            if (mCurrentPage == 1) {
                //mPbLoading.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        }

        if (mCurrentPage > 1) {
            //mLoLoadMore.setVisibility(View.GONE);
        }

        mCurrentPage++;
        mIsLoading = false;

        mListener.onArticleListFragmentEvent("onLoadDataFinished");
    }

    private void onRefreshFinished() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshMode = false;
    }

    public boolean goBack() {
        return false;
    }

    public void goTop() {
        //mListView.smoothScrollToPosition(0);
        //mListView.setSelection(0);
        mListView.setSelectionAfterHeaderView();
    }

    public void refresh() {
        mIsRefreshMode = true;
        mCurrentPage = 1;

        loadData();
    }

    public void openInNew() {
        //String url = mWebView.getUrl();
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //startActivity(intent);
    }

    public void share() {

    }

    @Override
    public void onPictureClick(int position, String imageUrl) {
        //Log.d(mTag, imageUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }

    @Override
    public void onNameClick(int position) {

    }

    @Override
    public void onCloseClick(int position) {
        mArticleList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.getInstance().cancelPendingRequests(mVolleyTag);
    }
}
