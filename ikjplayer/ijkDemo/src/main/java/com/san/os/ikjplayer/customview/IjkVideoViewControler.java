package com.san.os.ikjplayer.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.san.os.ikjplayer.R;
import com.san.os.ikjplayer.media.IMediaController;


/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-01-04 09:54
 */

public class IjkVideoViewControler extends RelativeLayout implements IMediaController {


    private static final int STATE_HIDE = 100;
    private static final int STATE_SHOW_PROGRESS = 200;
    private static final int VIWE_HIDE_TIME_OUT = 3000;

    private ProgressBar mLoading;
    private SeekBar mSeeBar;
    private TextView mTotalTime;
    private ImageView mFullScreen;
    private TextView mCurrentTime;
    private ImageView mPlay;
    private MediaController.MediaPlayerControl mIKJMediaPlayerControl;

    private boolean mIsShowContronlerView = false;


    public IjkVideoViewControler(Context context) {
        super(context);
        init(context);
    }

    public IjkVideoViewControler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public IjkVideoViewControler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.media_contoller, this, true);
        initView();
        initAction();
    }


    private void initView() {
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mSeeBar = (SeekBar) findViewById(R.id.seekbar);
        mTotalTime = (TextView) findViewById(R.id.all_time);
        mCurrentTime = (TextView) findViewById(R.id.time);
        mFullScreen = (ImageView) findViewById(R.id.full);
        mPlay = (ImageView) findViewById(R.id.player_btn);


    }

    private void initAction() {
        ContronlerClickListener clickListener = new ContronlerClickListener();
        setOnClickListener(clickListener);
        mFullScreen.setOnClickListener(clickListener);
        mPlay.setOnClickListener(clickListener);

        mSeeBar.setOnSeekBarChangeListener(new IJKOnSeekBarChangeListener());
    }


    /**
     * 处理UI显示
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATE_HIDE:
                    mIsShowContronlerView = false;
                    setVisibility(INVISIBLE);
                    break;
                case STATE_SHOW_PROGRESS:
                    setProgress();
                    if (mIsShowContronlerView) {
                        sendMessageDelayed(obtainMessage(STATE_SHOW_PROGRESS), 1000);
                    }
                    break;
            }
        }
    };

    /**
     * 视频缓冲
     */
    public void onLoading() {

    }

    /**
     * 开始播放
     */
    public void onStart() {
        setVisibility(INVISIBLE);
        mLoading.setVisibility(INVISIBLE);
    }

    /**
     * 暂定播放
     */
    public void pause() {
        if (mIKJMediaPlayerControl == null) {
            return;
        }
        mIKJMediaPlayerControl.pause();
        mPlay.setSelected(false);
        show(VIWE_HIDE_TIME_OUT);

    }

    /**
     * 由暂定到播放
     */
    public void start() {
        if (mIKJMediaPlayerControl == null) {
            return;
        }
        mIKJMediaPlayerControl.start();
        mPlay.setSelected(true);
        show(VIWE_HIDE_TIME_OUT);
    }

    /**
     * 播放结束
     */
    public void onComplete() {
        if (handler != null) {
            //TODO
        }
    }

    /**
     * 播放出错
     */
    public void onError() {
        if (handler != null) {
            //TODO
        }
    }

    /**
     * 显示播放控件
     */
    public void showView() {
        mIsShowContronlerView = true;
        setVisibility(VISIBLE);
        handler.sendEmptyMessage(STATE_SHOW_PROGRESS);
    }

    /**
     * 隐藏播放控件
     */
    public void hideView() {
        mIsShowContronlerView = false;
        setVisibility(INVISIBLE);
        handler.removeMessages(STATE_SHOW_PROGRESS);
        handler.sendEmptyMessage(STATE_HIDE);
    }


    public boolean isPlaying() {
        return mIKJMediaPlayerControl.isPlaying();
    }

    class ContronlerClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.show:
                    if (mIsShowContronlerView) {
                        hideView();
                    } else {
                        showView();
                    }
                    break;
                case R.id.full:
                    changeViewSize();
                    break;
                case R.id.player_btn:
                    if (mIKJMediaPlayerControl != null && mIKJMediaPlayerControl.isPlaying()) {
                        pause();
                    } else {
                        start();
                    }
                    break;
            }
        }
    }

    /**
     * 大小屏幕切换
     */
    public void changeViewSize() {

    }

    /**
     * 设置时间进度
     */
    public void setProgress() {
        if (!isShowing() || mIKJMediaPlayerControl == null) {
            return;
        }
        long position = mIKJMediaPlayerControl.getCurrentPosition();
        long duration = mIKJMediaPlayerControl.getDuration();
        int percent = mIKJMediaPlayerControl.getBufferPercentage();
        if (duration <= 0) {
            return;
        }
        mTotalTime.setText(generateTime(duration));
        long pos = 100L * position / duration;
        mSeeBar.setProgress((int) pos);
        mSeeBar.setSecondaryProgress(percent);
        mCurrentTime.setText(generateTime((long) (duration * position * 1.0f / 100)));
    }


    private String generateTime(long time) {
        if (time <= 0) {
            return "";
        }
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 时间进度的控制条
     */
    class IJKOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    @Override
    public void hide() {
        hideView();
    }

    @Override
    public boolean isShowing() {
        return mIsShowContronlerView;
    }

    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        mIKJMediaPlayerControl = player;
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(STATE_HIDE, timeout);
    }

    @Override
    public void show() {
        showView();
    }

    @Override
    public void showOnce(View view) {

    }
}
