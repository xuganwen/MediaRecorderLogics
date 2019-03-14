package com.xuganwen.mediaapplication;

import android.widget.ProgressBar;

import java.util.List;

/**
 * 文件描述：
 * 作者：徐干稳
 * 创建时间：2019/3/14
 * 更改时间：2019/3/14
 * 版本号：1.0
 */
public interface IMediaHelper {

    void initRecorder();

    void startRecording();

    void stopRecording();

    boolean isRecording();

    List<String> getAbsoluteFilePath();

    String getLatestFileAbsoluteFilePath();

    void destoryMediaRecorder();

    void destoryMediaPlayer();

    void playRecorder();

    void setProgressBar(ProgressBar progressBar, int maxProgress);
}
