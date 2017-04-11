# Android TV直播电视节目

更多技术博客，项目，欢迎关注公众号：
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/qrcode.jpg)

Android TV开发交流群：135622564 

传统电视直播节目,在Android TV上起着越来越重要的作用,央视,各地卫视,满足观众日益增长的多元化需求
看下效果图:


![这里写图片描述](/images/device-2016-10-28-182918.jpg)

![这里写图片描述](/images/device-2016-10-28-182937.jpg)

![这里写图片描述](/images/device-2016-10-28-183002.jpg)

![这里写图片描述](/images/device-2016-10-28-183126.jpg)

![这里写图片描述](/images/device-2016-10-28-183149.jpg)

![这里写图片描述](/images/device-2016-10-28-183212.jpg)

![这里写图片描述](/images/device-2016-10-28-183234.jpg)

![这里写图片描述](/images/device-2016-10-28-183320.jpg)

![这里写图片描述](/images/device-2016-10-28-183344.jpg)

![这里写图片描述](/images/device-2016-10-28-184751.jpg)

![这里写图片描述](/images/device-2016-10-28-184845.jpg)

![这里写图片描述](/images/device-2016-10-28-185015.jpg)

![这里写图片描述](/images/device-2016-10-28-185131.jpg)

![这里写图片描述](/images/device-2016-10-28-185839.jpg)

![这里写图片描述](/images/device-2016-10-28-190258.jpg)

![这里写图片描述](/images/device-2016-10-28-190359.jpg)

代码实现思路:

- 1、通过RecycleView为对应的节目item,遥控器按键,可触发跳到对应的直播节目
- 2、用对IjkPlayer进行二次封装,并能用于播放视频源。
- 3、视频源m3u8,可能存在失效,目前获取了一个比较稳定的视频源

代码实现：

- 主页面：Recycleview对应adapater
- 直播节目源
- 播放器
- 播放页处理

主页面：
```
/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
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
public class MainActivity extends Activity {

    private MetroViewBorderImpl mMetroViewBorderImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMetroViewBorderImpl = new MetroViewBorderImpl(this);
        mMetroViewBorderImpl.setBackgroundResource(R.drawable.border_color);
        loadRecyclerViewMenuItem();
    }

    private void loadRecyclerViewMenuItem() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ry_menu_item);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        mMetroViewBorderImpl.attachTo(recyclerView);
        createOptionItemData(recyclerView, R.layout.detail_menu_item);
    }

    private void createOptionItemData(RecyclerView recyclerView, int id) {
        OptionItemAdapter adapter = new OptionItemAdapter(this, id);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }
}
```
播放页：
```
/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
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
public class LiveActivity extends Activity {

    private IjkVideoView mVideoView;
    private RelativeLayout mVideoViewLayout;
    private RelativeLayout mLoadingLayout;
    private TextView mLoadingText;
    private TextView mTextClock;
    private String mVideoUrl = "";
    private int mRetryTimes = 0;
    private static final int CONNECTION_TIMES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        mVideoUrl = getIntent().getStringExtra("url");
        mVideoView = (IjkVideoView) findViewById(R.id.videoview);
        mVideoViewLayout = (RelativeLayout) findViewById(R.id.fl_videoview);
        mLoadingLayout = (RelativeLayout) findViewById(R.id.rl_loading);
        mLoadingText = (TextView) findViewById(R.id.tv_load_msg);
        mTextClock = (TextView)findViewById(R.id.tv_time);
        mTextClock.setText(getDateFormate());
        mLoadingText.setText("节目加载中...");
        initVideo();
    }

    private String getDateFormate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public void initVideo() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mVideoView.setVideoURI(Uri.parse(mVideoUrl));
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mVideoView.start();
            }
        });

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mLoadingLayout.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mVideoView.stopPlayback();
                mVideoView.release(true);
                mVideoView.setVideoURI(Uri.parse(mVideoUrl));
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                if (mRetryTimes > CONNECTION_TIMES) {
                    new AlertDialog.Builder(LiveActivity.this)
                            .setMessage("节目暂时不能播放")
                            .setPositiveButton(R.string.VideoView_error_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            LiveActivity.this.finish();
                                        }
                                    })
                            .setCancelable(false)
                            .show();
                } else {
                    mVideoView.stopPlayback();
                    mVideoView.release(true);
                    mVideoView.setVideoURI(Uri.parse(mVideoUrl));
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    public static void activityStart(Context context, String url) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
```
播放器是用二次封装的ijkplayer，从主页面传url到播放页面，关才mediaplayer相关，之前专门写了专题分析，mediaplayer的状态可参考[《Android Multimedia框架总结（一）MediaPlayer介绍之状态图及生命周期》](http://blog.csdn.net/hejjunlin/article/details/52349221)
第三方播放器典型特点就是另起一个mediaplayerservice，注意这是另外一个进程，为什么是另一个进程，可参见我的文章：MediaPlayer的C/S模型。对于ijkplayer这个框架，因为做实例，才引入，不做评价，也不会去深究，满足基本播放需求就ok。市场上有很多第三方播放框架，ijkplayer,vitamio,百度云播放等。

再看下播放页的播放panel:
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#22000000"
    android:orientation="vertical">

    <RelativeLayout
        android:id="@+id/fl_videoview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/colorBlack">

        <com.hejunlin.liveplayback.ijkplayer.media.IjkVideoView
            android:id="@+id/videoview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_centerInParent="true"
            android:background="@color/colorBlack">
        </com.hejunlin.liveplayback.ijkplayer.media.IjkVideoView>

        <RelativeLayout
            android:id="@+id/rl_loading"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#de262a3b">

            <TextView
                android:id="@+id/tv_load_msg"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_below="@+id/pb_loading"
                android:layout_centerInParent="true"
                android:layout_marginTop="6dp"
                android:textColor="#ffffff"
                android:textSize="16sp" />

            <ProgressBar
                android:id="@+id/pb_loading"
                android:layout_width="60dp"
                android:layout_height="60dp"
                android:layout_centerInParent="true"
                android:layout_marginTop="60dp"
                android:indeterminate="false"                  android:indeterminateDrawable="@drawable/video_loading"
                android:padding="5dp" />
        </RelativeLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/player_panel_background_color">

            <TextView
                android:id="@+id/tv_title"
                android:layout_width="wrap_content"
                android:layout_height="60dp"
                android:textSize="24dp"
                android:text="Android TV开发总结（六）构建一个TV app的直播节目实例"
                android:layout_centerVertical="true"
                android:layout_marginTop="18dp"
                android:textColor="@color/white"/>

            <TextView
                android:id="@+id/tv_time"
                android:layout_width="wrap_content"
                android:layout_height="60dp"
                android:textSize="20dp"
                android:layout_toRightOf="@id/tv_title"
                android:layout_alignParentRight="true"
                android:layout_centerVertical="true"
                android:layout_marginLeft="60dp"
                android:layout_marginTop="20dp"
                android:textColor="@color/white"/>
        </LinearLayout>
    </RelativeLayout>
</RelativeLayout>
```
<font color ="#FF6347">这里有几个点要注意 </font>：

- 为演示，并未对层级进行使用FrameLayout,及viewstub,include等性能优化相关的，在实际商用项目中，建议写xml文件，尽可能遵循过少的层级，高级标签及FrameLayout等技巧。
- 所有的size切勿直接写死，用  android:layout_marginTop="@dimen/dimen_20dp"表示，string值统一写到string.xml中，这些基本的规范，会让你提高不少效率。


####欢迎关注我的个人公众号，android 技术干货，问题深度总结，FrameWork源码解析，插件化研究，最新开源项目推荐
![这里写图片描述](https://github.com/hejunlin2013/RedPackage/blob/master/image/qrcode.jpg)

License
--------
```
Copyright (C) 2016 hejunlin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```



