package com.tencent.videotwodemo.videoservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.tencent.videotwodemo.R
import com.tencent.videotwodemo.utils.FloatingWindowHelper
import com.tencent.videotwodemo.utils.dp2px
import com.tencent.videotwodemo.videoactivity.VideoActivity
import com.tencent.videotwodemo.videoutil.IVideo
import com.tencent.videotwodemo.videoutil.VideoError
import com.tencent.videotwodemo.videoutil.VideoManager
import java.util.*

class VideoService : Service(), IVideo {

    companion object {
        //标记服务是否开启
        var isStart = false
        //静音按钮
        var audioState = false
    }

    //视频管理者
    lateinit var videoManager: VideoManager

    //打气筒对象
    lateinit var layoutInflater: LayoutInflater

    //本地视频回调
    lateinit var mLocalSurfaceView: (SurfaceView?) -> Unit

    //远程视频回调
    lateinit var mRemoteSurfaceView: (LinkedList<Pair<Int, SurfaceView>>) -> Unit


    override fun onCreate() {
        super.onCreate()
        isStart = true
        //创建视频管理者
        videoManager = VideoManager(this)
        //获取打气筒对象
        layoutInflater = LayoutInflater.from(this@VideoService)
    }


    //本地视频渲染成功回调
    override fun LocalSurfaceView(mlocalSurfaceView: SurfaceView?) {
        mLocalSurfaceView.invoke(mlocalSurfaceView)
    }

    //远端视频渲染
    override fun onRemoteChanged(mRemoteSurfaceView: LinkedList<Pair<Int, SurfaceView>>) {
        this@VideoService.mRemoteSurfaceView.invoke(mRemoteSurfaceView)
    }

    //视频发生错误
    override fun onError(error: VideoError) {

    }


    //创建binder对象
    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }


    inner class MyBinder : Binder() {

        lateinit var mFloatingWindowHelper: FloatingWindowHelper

        lateinit var layout_float: FrameLayout

        //初始化
        fun initVideo(uid:Int,
            localSurfaceView: (SurfaceView?) -> Unit,
            mRemoteSurfaceView: (LinkedList<Pair<Int, SurfaceView>>) -> Unit
        ) {
            this@VideoService.mLocalSurfaceView = localSurfaceView
            this@VideoService.mRemoteSurfaceView = mRemoteSurfaceView
            videoManager.initVideo(uid)
        }

        //加入房间
        fun joinChannel(
            channelName: String,
            token: String? = null,
            optionalInfo: String = "",
            optionalUid: Int = 0
        ) {
            videoManager.joinChannel(channelName, token, optionalInfo, optionalUid)
        }

        //静音
        fun setAudioState(state: Boolean) {
            videoManager.setAudioState(state)
        }

        //切换摄像头
        fun switchCamera() {
            videoManager.switchCamera()
        }

        //切换大屏幕和小屏幕显示
        fun switchBigContainerShow(position: Int) {
            videoManager.switchBigContainerShow(position)
        }

        //开启悬浮窗
        fun showFloatWindow() {
            //创建悬浮窗帮助类
            mFloatingWindowHelper = FloatingWindowHelper(this@VideoService)
            //获取悬浮窗的父类
            val patient = (videoManager.localSurfaceView!!.second.parent as ViewGroup)
            //打入布局
            layout_float = layoutInflater.inflate(R.layout.float_layout, patient, false) as FrameLayout
            //移除原有的挂载
            patient.removeAllViews()
            //添加新的挂载
            val lp = FrameLayout.LayoutParams(dp2px(192F), dp2px(108F))
            lp.setMargins(10, 10, 10, 10)
            layout_float.addView(videoManager.localSurfaceView?.second, 0, lp)
            //展示悬浮窗
            mFloatingWindowHelper.addView(layout_float, true)

            //悬浮窗点击，去打开Activity
            layout_float.setOnClickListener {
                layout_float.removeAllViews()
                val eee = Intent(this@VideoService, VideoActivity::class.java)
                eee.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                eee.putExtra("from", "Service")
                startActivity(eee)
            }


        }
        //关闭悬浮窗

        fun dismassFloatWindow(
            localSurfaceView: (SurfaceView?) -> Unit,
            mRemoteSurfaceView: (LinkedList<Pair<Int, SurfaceView>>) -> Unit
        ) {
            this@VideoService.mLocalSurfaceView = localSurfaceView
            this@VideoService.mRemoteSurfaceView = mRemoteSurfaceView
            mFloatingWindowHelper.clear()
            mFloatingWindowHelper.destroy()
            videoManager.localSurfaceView!!.second.layoutParams =FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)
            this@VideoService. mLocalSurfaceView.invoke(videoManager.localSurfaceView!!.second)
            this@VideoService.mRemoteSurfaceView.invoke(videoManager.mSurfaceView)
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        isStart = false
        videoManager.leaveChannel()
    }


}