package com.xuganwen.mediaapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ProgressBar;

import com.xuganwen.AudioRecorderHelper;
import com.xuganwen.VedioRecorderHelper;

/**
 * 文件描述：
 * 作者：徐干稳
 * 创建时间：2019/3/13
 * 更改时间：2019/3/13
 * 版本号：1.0
 */
public class SecondActivity extends AppCompatActivity implements RecorderStateListener {

    private Button btn;
    private TextureView textureview;
    private IMediaHelper audioRecorderHelper;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();

    }

    private void initView() {

        btn = findViewById(R.id.btn);
        textureview = findViewById(R.id.textureview);
        progressbar = findViewById(R.id.progressbar);
        audioRecorderHelper = new MediaHelperFactory(this, textureview, this, MediaHelperFactory.TypeMedia.VEDIO) {
            @Override
            public void requestPermission() {

            }
        };
         audioRecorderHelper.setProgressBar(progressbar, 20);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (audioRecorderHelper.isRecording()) {
                    audioRecorderHelper.stopRecording();
                } else {
                    audioRecorderHelper.startRecording();
                }
            }
        });
    }

    @Override
    public void startState() {
        btn.setText("停止录制视频");
    }

    @Override
    public void stopState() {
        btn.setText("开始录制视频");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecorderHelper.destoryMediaRecorder();
    }
}
