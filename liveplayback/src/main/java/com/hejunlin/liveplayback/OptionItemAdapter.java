/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 * 
 * Github:https://github.com/hejunlin2013/LivePlayback
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hejunlin.liveplayback;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hejunlin on 2015/10/28.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class OptionItemAdapter extends RecyclerView.Adapter<OptionItemAdapter.ViewHolder> {

    // 数据集
    private String[] mDataList = new String[] {
            "CCTV1综合","CCTV2财经","CCTV3综艺","CCTV4中文国际","CCTV5体育","CCTV6电影","CCTV7军事农业","CCTV8电视剧", "CCTV9纪录",
            "CCTV10科教","CCTV11戏曲","CCTV12社会与法","CCTV13新闻","CCTV14少儿","CCTV15音乐","第一剧场","湖南卫视"
    };

    private String [] mUrlList = new String[]{
            "http://58.135.196.138:8090/live/db3bd108e3364bf3888ccaf8377af077/index.m3u8",
            "http://58.135.196.138:8090/live/e31fa63612644555a545781ea32e66d4/index.m3u8",
            "http://58.135.196.138:8090/live/A68CE6833D654a9e932A657689463088/index.m3u8",
            "http://58.135.196.138:8090/live/56383AB184D54ac8B20478B6A43906DC/index.m3u8",
            "http://58.135.196.138:8090/live/6b9e3889ec6e2ab1a8c7bd0845e5368a/index.m3u8",
            "http://58.135.196.138:8090/live/6132e9cb136050bd94822db31d1401af/index.m3u8",
            "http://58.135.196.138:8090/live/a9a97bed07eca008a1c88c9d7c74965d/index.m3u8",
            "http://58.135.196.138:8090/live/6e38d69416f160bad4657788d6c06c01/index.m3u8",
            "http://58.135.196.138:8090/live/557a950d2cfcf2ee1aad5a260893c2b8/index.m3u8",
            "http://58.135.196.138:8090/live/a0effe8f31af0011b750e817c1b6e8c7/index.m3u8",
            "http://58.135.196.138:8090/live/2D5B4B7AB5A14d79A0D9A1D37540E2BF/index.m3u8",
            "http://58.135.196.138:8090/live/3720126fbea745f74c1c89df9797caac/index.m3u8",
            "http://58.135.196.138:8090/live/5e02ac2a0829f7ab10d6543fdd211d8e/index.m3u8",
            "http://58.135.196.138:8090/live/4A5DF0BA0C994081B02F8215491C4E48/index.m3u8",
            "http://58.135.196.138:8090/live/ADBD55B50F2D47bb970DCCBAF458E6C8/index.m3u8",
            "http://58.135.196.138:8090/live/13BCA1A2EFDB4db3B5DF6A8D343C3E39/index.m3u8",
            "http://117.144.248.49/HDhnws.m3u8?authCode=07110409322147352675&amp;stbId=006001FF0018120000060019F0D496A1&amp;Contentid=6837496099179515295&amp;mos=jbjhhzstsl&amp;livemode=1&amp;channel-id=wasusyt"
    };

    private Context mContext;
    private int id;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private OnBindListener onBindListener;
    private static final String TAG = OptionItemAdapter.class.getSimpleName();

    public interface OnBindListener {
        void onBind(View view, int i);
    }

    public OptionItemAdapter(Context context) {
        super();
        mContext = context;
    }

    public OptionItemAdapter(Context context, int id) {
        super();
        mContext = context;
        this.id = id;
    }

    public OptionItemAdapter(Context context, int id, View.OnFocusChangeListener onFocusChangeListener) {
        super();
        mContext = context;
        this.id = id;
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int resId = R.layout.detail_menu_item;
        if (this.id > 0) {
            resId = this.id;
        }
        View view = LayoutInflater.from(mContext).inflate(resId, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (mDataList.length == 0) {
            Log.d(TAG, "mDataset has no data!");
            return;
        }
        viewHolder.mTextView.setText(mDataList[i]);
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveActivity.activityStart(mContext, mUrlList[i]);
            }
        });
        if (onBindListener != null) {
            onBindListener.onBind(viewHolder.itemView, i);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_menu_title);
        }
    }

}
