# Android TV直播电视节目

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
1、通过RecycleView为对应的节目item,遥控器按键,可触发跳到对应的直播节目
2、用对IjkPlayer进行二次封装,并能用于播放视频源。
3、视频源m3u8,可能存在失效,目前获取了一个比较稳定的视频源
```
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
                            .setMessage("视频暂时不能播放")
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



