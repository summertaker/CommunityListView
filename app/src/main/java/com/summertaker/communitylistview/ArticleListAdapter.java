package com.summertaker.communitylistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summertaker.communitylistview.common.BaseDataAdapter;
import com.summertaker.communitylistview.data.ArticleData;

import java.util.ArrayList;

public class ArticleListAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ArticleData> mDataList = null;

    public ArticleListAdapter(Context context, ArrayList<ArticleData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ArticleData articleData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mLayoutInflater.inflate(R.layout.article_list_item, null);

            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvLike = convertView.findViewById(R.id.tvLike);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(articleData.getTitle());
        holder.tvLike.setText(articleData.getLike());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvLike;
    }
}
