# IJKVideo 视频播放器
底层为[Bilibili/ijkplayer](https://github.com/Bilibili/ijkplayer)，封装视频View
Bilibili/ijkplayer原来项目缺失so文件，如果不用complie方式的话，需要手动生成。生成步骤原项目里也有说明，这里再说一下：

git clone https://github.com/Bilibili/ijkplayer.git ijkplayer-android
cd ijkplayer-android
git checkout -B latest k0.7.6

./init-android.sh

cd android/contrib
./compile-ffmpeg.sh clean
./compile-ffmpeg.sh all

cd ..
./compile-ijk.sh all
如有疑问可参考文献：
[小试ijkplayer编译](http://avenwu.net/ijkplayer/2015/05/07/hands_on_ijkplayer_preparation/)
[ windows下用cygwin编译android版ijkplayer]()