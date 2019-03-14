package com.xuganwen;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xuganwen.mediaapplication.IMediaHelper;
import com.xuganwen.mediaapplication.RecorderStateListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件描述：视频录制器代码
 * 作者：徐干稳
 * 创建时间：2019/3/13
 * 更改时间：2019/3/13
 * 版本号：1.0
 */
public class VedioRecorderHelper implements TextureView.SurfaceTextureListener,IMediaHelper{

    private MediaRecorder recorder;
    private Context context;
    private TextureView textureView;
    private String RECORDER_FILE_DIR_PATH;
    private SurfaceTexture surface;
    private Camera camera;
    private Camera.Parameters parameters;
    private boolean isRecording = false;
    private List<String> paths = new ArrayList<>();
    private ProgressBar progressBar;
    private RecorderStateListener listener;
    private long MAX_DURATION=20000;


    private CountDownTimer timer = new CountDownTimer(MAX_DURATION, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            invilidateProgress((int)(MAX_DURATION/1000) - (int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            stopRecording();
        }
    };

    public VedioRecorderHelper(@NonNull Context context, @NonNull TextureView textureView,@NonNull RecorderStateListener listener) {
        this.context = context;
        this.textureView = textureView;
        this.listener = listener;
        RECORDER_FILE_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "recorder" + File.separator + "vedio";
        recorder = new MediaRecorder();
        initRecorder();
    }


    @Override
    public void initRecorder() {
        if (null != textureView) {
            textureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = surface;
        initCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    /**
     * 初始化相机
     */
    private void initCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
        parameters = camera.getParameters();
        try {
            parameters.setPreviewSize(1920, 1080);
            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);//设置显示翻转，为0则是水平录像，90为竖屏
            camera.setPreviewTexture(surface);//将onSurfaceTextureAvailable监听中的surface传入进来，设置预览的控件
        } catch (IOException t) {
            Toast.makeText(context, "相机初始化失败", Toast.LENGTH_SHORT).show();
        }
        camera.startPreview();//开始预览
        textureView.setAlpha(1.0f);
    }


    /**
     * 录制视频
     */
    @Override
    public void startRecording() {
        if (isRecording) {
            Toast.makeText(context, "当前正在录制...", Toast.LENGTH_SHORT).show();
        }

        recorder = new MediaRecorder();
        camera.unlock();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)); //setProfile不能和后面的setOutputFormat等方法一起使用
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  // 设置视频的输出格式 为MP4

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 设置音频的编码格式
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // 设置视频的编码格式
        recorder.setVideoSize(context.getResources().getDisplayMetrics().heightPixels, context.getResources().getDisplayMetrics().widthPixels);  // 设置视频大小
        recorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        recorder.setVideoFrameRate(30); // 设置帧率

        recorder.setOrientationHint(90);


        File file = new File(RECORDER_FILE_DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        recorder.setOutputFile(file.getPath() + File.separator + "vedio_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4");
        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            paths.add(file.getAbsolutePath());
            timer.start();
            listener.startState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecording() {

        if (isRecording) {
            camera.lock();
            recorder.stop();
            recorder.reset();
            isRecording = false;
            initCamera();
            if (null != progressBar) {
                progressBar.setProgress(0);
                timer.cancel();
            }
            listener.stopState();
        }
    }


    /**
     * 判断是否在录制视频
     */
    @Override
    public boolean isRecording() {
        return isRecording;
    }


    /**
     * 获取所有录音文件完整路径
     */
    @Override
    public List<String> getAbsoluteFilePath() {

        return paths;
    }


    /**
     * 获取最后一条录音文件完整路径
     */
    @Override
    public String getLatestFileAbsoluteFilePath() {

        if (paths.size() != 0) {
            return paths.get(paths.size() - 1);
        }
        throw new IllegalStateException("暂未录制视频");
    }

    @Override
    public void setProgressBar(ProgressBar progressBar, int maxProgress) {
        this.progressBar = progressBar;
        this.MAX_DURATION=maxProgress*1000;
        progressBar.setMax(maxProgress);
    }

    public void invilidateProgress(int progress) {
        if (null != progressBar) {
            progressBar.setProgress(progress);
        }
    }


    /**
     * 销毁mediarecorder
     * */
    @Override
    public void destoryMediaRecorder() {
        if (isRecording) {
            recorder.stop();
        }
        recorder.reset();
        recorder.release();
        recorder = null;
        isRecording = false;
    }



    /**
     * 销毁视频播放器
     * */
    @Override
    public void destoryMediaPlayer() {
        //TODO
    }

    @Override
    public void playRecorder() {
        //TODO
    }
}
