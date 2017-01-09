package com.san.os.ikjplayer.media;

/**
 * @author luluc@yiche.com
 * @Description 视频播放器的控制器点击事件
 * @date 2017-01-05 09:53
 */

public interface IKJContronleronClickListener {
    void pauseByControler();
    void startByControler();
    void seekToByControler(int postion);
}
