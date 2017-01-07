package com.san.os.ikjplayer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.san.os.ikjplayer.customview.IKJVideoEvnetListener;
import com.san.os.ikjplayer.customview.VideoPlayView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MainActivity extends Activity {

    private VideoPlayView mVideoView;
    private RelativeLayout mRoot;

    private String url = "http://flv.bitauto.com/2014/2016/12/27/e8ca4b8bcfe53b9e-sd.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mVideoView.start(url);
    }

    private void initView() {
        initIKJ();
        mVideoView = new VideoPlayView(this);
        mRoot = (RelativeLayout) findViewById(R.id.activity_main);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRoot.addView(mVideoView, rl);

        mVideoView.setVideoEventListener(new IKJEventlistener());
    }

    private void initIKJ() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoViewRelease();
    }

    private void videoViewRelease() {
        if (mVideoView != null) {
            mVideoView.stop();
            mVideoView.release();
            mVideoView.onDestroy();
            mVideoView = null;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVideoView.onChanged(newConfig);
    }


    class IKJEventlistener implements IKJVideoEvnetListener {

        @Override
        public void onError() {

        }

        @Override
        public void onVideoSizeChangedListener() {

        }

        @Override
        public void onSeekCompleteListener() {

        }

        @Override
        public void onCompletionListener() {
            videoViewRelease();
            finish();
        }

        @Override
        public void onPreparedListener() {

        }
    }
}
