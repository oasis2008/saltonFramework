package com.salton123.saltonframeworkdemo;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.salton123.saltonframeworkdemo.video.OnStateChangeListener;
import com.salton123.saltonframeworkdemo.video.StateType;
import com.salton123.saltonframeworkdemo.video.VideoObj;
import com.salton123.util.RxUtils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: newSalton@outlook.com
 * Date: 2018/5/11 下午8:51
 * ModifyTime: 下午8:51
 * Description:
 */
public class SaltonVideoView extends FrameLayout
        implements MediaPlayer.OnErrorListener
        , MediaPlayer.OnInfoListener
        , MediaPlayer.OnPreparedListener
        , MediaPlayer.OnCompletionListener
        , MediaPlayer.OnBufferingUpdateListener, MediaController.MediaPlayerControl {
    private final String TAG = "SaltonVideoView";

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
    }

    private OnStateChangeListener mOnStateChangeListener;

    public SaltonVideoView(Context context) {
        super(context);
        initView();
    }

    public SaltonVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SaltonVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private VideoView mVideoView;
    private ImageView mCover;
    private FrameLayout mFlController;

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.salton_video_view, this, true);
        mVideoView = findViewById(R.id.videoView);
        mFlController = findViewById(R.id.flController);
        mCover = findViewById(R.id.cover);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Log.e(TAG, "[surfaceCreated] action=startUpdateProgressTimer ");
                // startUpdateProgressTimer();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e(TAG, "[surfaceDestroyed] action=unInit ");
                unInit();
            }
        });
    }


    public void setCover(int resId) {
        mFlController.setVisibility(View.VISIBLE);
        mCover.setImageResource(resId);
    }

    public void setVideoPath(String path) {
        mVideoView.setVideoURI(Uri.parse(path));
        mFlController.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setVideoURI(Uri uri) {
        mVideoView.setVideoURI(uri, null);
        mFlController.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mVideoView.setVideoURI(uri, headers);
        mFlController.setVisibility(View.VISIBLE);
    }

    public void restart() {
        // mVideoView.
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mOnStateChangeListener != null) {
            Message message = Message.obtain();
            message.what = StateType.STATE_ERROR;
            message.obj = new VideoObj(mp, what, extra);
            mOnStateChangeListener.onStateChange(message);
        }
        Log.e(TAG, "[onError] action=cancelUpdateProgressTimer ");
        cancelUpdateProgressTimer();
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (mOnStateChangeListener != null) {
            Message message = Message.obtain();
            message.what = StateType.STATE_INFO;
            message.obj = new VideoObj(mp, what, extra);
            mOnStateChangeListener.onStateChange(message);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // mVideoView.setBackgroundColor(Color.TRANSPARENT);
                    mFlController.setVisibility(View.GONE);
                    mCover.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }
        });
        if (mOnStateChangeListener != null) {
            Message message = Message.obtain();
            message.what = StateType.STATE_PREPARE;
            message.obj = new VideoObj(mp);
            mOnStateChangeListener.onStateChange(message);
        }
        startUpdateProgressTimer();
        Log.e(TAG, "[onPrepared] action=startUpdateProgressTimer ");

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mOnStateChangeListener != null) {
            Message message = Message.obtain();
            message.what = StateType.STATE_COMPLETE;
            message.obj = new VideoObj(mp);
            mOnStateChangeListener.onStateChange(message);
        }
        Log.e(TAG, "[onCompletion] action=cancelUpdateProgressTimer ");
        cancelUpdateProgressTimer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mOnStateChangeListener != null) {
            Message message = Message.obtain();
            message.what = StateType.STATE_BUFFERING;
            message.obj = new VideoObj(mp, percent);
            mOnStateChangeListener.onStateChange(message);
        }
    }

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = StateType.STATE_PROGRESSING;
                    int pos = mVideoView.getCurrentPosition();
                    int duration = mVideoView.getDuration();
                    message.obj = new VideoObj(pos, duration);
                    Log.e(TAG, "[startUpdateProgressTimer] update progress,pos=" + pos + ",duration=" + duration);
                    if (mOnStateChangeListener != null) {
                        mOnStateChangeListener.onStateChange(message);
                    }
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, getInterval(), getInterval());
    }

    int currentPos = 0;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        currentPos = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        seekTo(currentPos);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    private int interval = 500;

    private void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }


    private void unInit() {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener = null;
        }
        cancelUpdateProgressTimer();
    }


    @Override
    public void start() {
        mVideoView.start();
    }

    @Override
    public void pause() {
        mVideoView.pause();
    }

    @Override
    public int getDuration() {
        return mVideoView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mVideoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mVideoView.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return mVideoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return mVideoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return mVideoView.canSeekForward();
    }

    @Override
    public int getAudioSessionId() {
        return mVideoView.getAudioSessionId();
    }
}