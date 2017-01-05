package com.san.os.ikjplayer.media;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.san.os.ikjplayer.R;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-01-04 09:41
 */

public class IJKVideoPlayView extends RelativeLayout {

    private IjkVideoView mVideoView;
    private IjkVideoViewControler mContronler;

    public IJKVideoPlayView(Context context) {
        super(context);
        initData();
        init(context);
    }

    public IJKVideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        init(context);
    }

    public IJKVideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        init(context);
    }

    private void initData() {

    }

    public void start(String path) {
        Uri uri = Uri.parse(path);

        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ikj_view_video, this, true);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        mContronler = (IjkVideoViewControler) findViewById(R.id.media_contoller);
        mVideoView.setMediaController(mContronler);

        mVideoView.setOnInfoListener( new IKJOnInfoListener());
    }

    class IKJOnInfoListener implements IMediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {


            return false;
        }
    }
}
