package com.san.os.ikjplayer.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.san.os.ikjplayer.R;
import com.san.os.ikjplayer.Utils.NetUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * @author luluc@yiche.com
 * @Description IKJ播放view
 * @date 2017-01-20 16:47
 */
public class VideoPlayView extends RelativeLayout implements IKJContronleronClickListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener {

    public static final String POWER_LOCK = "VideoPlayView";

    public static final String TAG = "lulu_local_video";

    private CustomMediaContoller mMediaController;
    private IKJVideoEvnetListener mVideoEventListener;
    private IjkVideoView mVideoView;
    private Handler mHandler = new Handler();
    private ProgressBar mLoading;
    private Context mContext;

    private PowerManager.WakeLock mWakeLock = null;
    private boolean portrait = true;
    private boolean mPage;//是否为当前页播放
    private int mWidth, mHeight;//播放器宽高
    private String mUrl;

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

    public void initSize(int width, int height) {
        mPage = true;
        mWidth = width;
        mHeight = height;
    }

    private void initActions() {
        mMediaController.setIKJContronleronClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
    }

    /**
     * 首次播放，供外部调用
     * @param path
     */
    public void start(String path) {
        mUrl = path;
        if (NetUtils.getNetWorkType(mContext) == 3) {
            initPlay();
        } else {
            showRemind();
        }
    }

    /**
     * 首次开始播放
     */
    private void initPlay() {
        if (TextUtils.isEmpty(mUrl)) {
            if (mVideoEventListener != null) {
                mVideoEventListener.onError();
            }
            return;
        }
        Uri uri = Uri.parse(mUrl);
        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.pause();
            mMediaController.pause(true);
        }

    }

    /**
     * 从暂停到开始播放
     */
    public void reStart() {
        if (mVideoView != null) {
            mVideoView.start();
            mMediaController.reStart(true);
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
     * 停止播放并释放资源
     */
    public void stop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
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
        if (mVideoView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        if (mPage) {
                            layoutParams.height = mHeight;
                            layoutParams.width = mWidth;
                        } else {
                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !portrait;
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



    public boolean isAvailable() {
        return mVideoView != null;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        portrait = (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT);
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 用户流量提醒
     */
    public void showRemind() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle(mContext.getString(R.string.video_start_remend_title));
        alertBuilder.setMessage(mContext.getString(R.string.video_start_remend_content));
        AlertDialog dialog = alertBuilder.create();
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.video_start_remend_cancel), new CancelDialogInterface());
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.video_start_remend_enter), new EnterDialogInterface());
        try {
            if (dialog != null && !dialog.isShowing() && mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CancelDialogInterface implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mVideoEventListener != null) {
                mVideoEventListener.onCompletionListener();
            }
        }
    }

    private class EnterDialogInterface implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            initPlay();
        }
    }

}
