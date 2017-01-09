package com.san.os.ikjplayer.media;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.san.os.ikjplayer.R;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Description IKJ播放view
 */
public class VideoPlayView extends RelativeLayout implements IKJContronleronClickListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener {

    public static final String POWER_LOCK = "VideoPlayView";

    public static final String TAG = "lulu_video";

    private CustomMediaContoller mMediaController;
    private IKJVideoEvnetListener mVideoEventListener;
    private IjkVideoView mVideoView;
    private Handler mHandler = new Handler();
    private ProgressBar mLoading;
    private Context mContext;

    private PowerManager.WakeLock mWakeLock = null;
    private boolean portrait;

    public VideoPlayView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
        initData();
        initViews();
        initActions();
    }


    private void initData() {

    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.view_video_item, this, true);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        mMediaController = (CustomMediaContoller) findViewById(R.id.media_contoller);
        mVideoView.setMediaController(mMediaController);

    }

    private void initActions() {
        mMediaController.setIKJContronleronClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
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
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
    }

    public boolean isPlaying() {
        return mVideoView == null ? false : mVideoView.isPlaying();
    }

    public int getCurrentPosition() {
        return mVideoView == null ? -1 : mVideoView.getCurrentPosition();
    }


    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }


    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        Log.e("handler", "400");
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        Log.e("handler", "height==" + heightPixels + "\nwidth==" + widthPixels);
                        setLayoutParams(layoutParams);
                    }
                }
            });
        }
    }


    private void setFullScreen(boolean fullScreen) {
        if (mContext != null && mContext instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    public void setVideoEventListener(IKJVideoEvnetListener listener) {
        mVideoEventListener = listener;
    }

    @Override
    public void pauseByControler() {
        mVideoView.pause();
    }

    @Override
    public void startByControler() {
        mVideoView.start();
    }

    @Override
    public void seekToByControler(int postion) {
        mVideoView.seekTo(postion);
    }


    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                //开始缓冲
                Log.i(TAG, "开始缓冲");
                mLoading.setVisibility(View.VISIBLE);
                mMediaController.hide();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                //开始播放
                Log.i(TAG, "缓冲结束,开始播放");
                mLoading.setVisibility(View.GONE);
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mLoading.setVisibility(View.GONE);
                break;

            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                mLoading.setVisibility(View.GONE);
                break;
            case IMediaPlayer.MEDIA_INFO_UNKNOWN:
                break;
        }
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        if (mVideoEventListener != null) {
            mVideoEventListener.onError();
        }
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        if (mVideoEventListener != null) {
            mVideoEventListener.onCompletionListener();
        }
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if (mVideoEventListener != null) {
            mVideoEventListener.onPreparedListener();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.pause();
        }

    }

    /**
     * 从暂停到开始播放
     */
    public void reStart() {
        if (mVideoView != null) {
            mVideoView.start();
        }
    }


    /**
     * 释放资源
     */
    public void release() {
        if (mVideoView != null) {
            mHandler.removeCallbacksAndMessages(null);
            mVideoView.release(true);
            mVideoView = null;
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    public boolean isAvailable() {
        return mVideoView != null;
    }
}
