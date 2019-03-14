package com.xuganwen.mediaapplication;

import android.content.Context;
import android.view.TextureView;
import android.widget.ProgressBar;

import com.xuganwen.AudioRecorderHelper;
import com.xuganwen.VedioRecorderHelper;

import java.util.List;

/**
 * 文件描述：
 * 作者：徐干稳
 * 创建时间：2019/3/14
 * 更改时间：2019/3/14
 * 版本号：1.0
 */
public abstract class MediaHelperFactory implements IMediaHelper {

    private  TypeMedia typeMeida;
    private IMediaHelper helper;
    private IPermissionProxy permissionProxy;

    /**
     *  非开放无参构造
     * */
    private MediaHelperFactory() {

    }


    public MediaHelperFactory(Context context, TextureView textureView, RecorderStateListener listener, TypeMedia typeMedia) {

        requestPermission();
        this.typeMeida=typeMedia;
        if (typeMedia == TypeMedia.AUDIO) {
            helper = new AudioRecorderHelper(context, listener);
        } else {
            helper = new VedioRecorderHelper(context, textureView, listener);
        }
    }

    @Override
    public void initRecorder() {
        helper.initRecorder();
    }

    @Override
    public void startRecording() {
        helper.startRecording();

    }

    @Override
    public void stopRecording() {
        helper.stopRecording();
    }

    @Override
    public boolean isRecording() {
        return helper.isRecording();
    }

    @Override
    public List<String> getAbsoluteFilePath() {
        return helper.getAbsoluteFilePath();
    }

    @Override
    public String getLatestFileAbsoluteFilePath() {
        return helper.getLatestFileAbsoluteFilePath();
    }

    @Override
    public void destoryMediaRecorder() {

        helper.destoryMediaRecorder();
    }

    @Override
    public void destoryMediaPlayer() {

        helper.destoryMediaPlayer();
    }

    public enum TypeMedia {
        AUDIO,
        VEDIO;
    }


    /**
     *
     * 权限代理对象，考虑到有些应用在一开始就会请求所有的权限，所以这里不写成抽象方法
     * */
    public void setPermissionProxy(IPermissionProxy proxy){
        this.permissionProxy=proxy;
    }

    @Override
    public void playRecorder() {
        helper.playRecorder();
    }

    @Override
    public void setProgressBar(ProgressBar progressBar, int maxProgress){
        helper.setProgressBar(progressBar,maxProgress);
    }


    /**
     *  建议通过设置权限代理来执行，因为权限与功能业务无关,但是功能使用前必须先申请权限
     * */
    public abstract void requestPermission();

}
