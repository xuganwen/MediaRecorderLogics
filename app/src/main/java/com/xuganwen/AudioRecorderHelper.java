package com.xuganwen;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xuganwen.mediaapplication.IMediaHelper;
import com.xuganwen.mediaapplication.RecorderStateListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件描述：媒体播放类
 * 作者：徐干稳
 * 创建时间：2019/2/13
 * 更改时间：2019/2/13
 * 版本号：1.0
 */
public class AudioRecorderHelper implements IMediaHelper {

    private Context context;
    private String RECORDER_FILE_DIR_PATH;
    private MediaRecorder recorder;
    private boolean isRecording;
    private String fileName;
    private MediaPlayer player;
    private List<String> paths = new ArrayList<>();
    private RecorderStateListener listener;

    public AudioRecorderHelper(@NonNull Context context,@NonNull RecorderStateListener listener) {
        this.context = context;
        this.listener = listener;
        RECORDER_FILE_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "recorder" + File.separator + "audio";
        initMediaPlayer();
        recorder = new MediaRecorder();
    }


    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {

        player = new MediaPlayer();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (null != mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        });
        //播放器音量配置
        player.setVolume(1, 1);
        //是否循环播放
        player.setLooping(false);

    }

    /**
     * 初始化录音器
     */
    @Override
    public void initRecorder() {

        // 设置音频录入源
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制音频的输出格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置音频的编码格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                // 发生错误，停止录制
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;
                Toast.makeText(context, "录音发生错误", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 开始录音
     */
    @Override
    public void startRecording() {

        initRecorder();
        fileName = "record_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".amr";
        File dir = new File(RECORDER_FILE_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(RECORDER_FILE_DIR_PATH + File.separator + fileName);

        if (file.exists()) {
            file.delete();
        }
        recorder.setOutputFile(file.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            paths.add(file.getAbsolutePath());
            listener.startState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    @Override
    public void stopRecording() {
        if (isRecording) {
            recorder.stop();
            recorder.reset();
            isRecording = false;
            listener.stopState();
        }
    }


    /**
     * 播放录音
     */
    @Override
    public void playRecorder() {
        if (isRecording) {
            Toast.makeText(context, "当前正在录音", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null == fileName) {
            Toast.makeText(context, "请先录音", Toast.LENGTH_SHORT).show();
            return;
        }

        if (player.isPlaying()) {
            Toast.makeText(context, "正在播放录音，请稍后操作", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            player.setDataSource(RECORDER_FILE_DIR_PATH + File.separator + fileName);
            //准备及播放
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }

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

    @Override
    public void destoryMediaPlayer() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.setOnCompletionListener(null);
        player.setOnErrorListener(null);
        player.reset();
        player.release();
        player = null;
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
        //TODO
    }
}
