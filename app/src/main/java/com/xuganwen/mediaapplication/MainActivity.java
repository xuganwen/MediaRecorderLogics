package com.xuganwen.mediaapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements RecorderStateListener ,IPermissionProxy{

    private IMediaHelper mediaRecordHelper;
    private Button btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mediaRecordHelper=new MediaHelperFactory(this, null,this, MediaHelperFactory.TypeMedia.AUDIO) {
            @Override
            public void requestPermission() {
                   requstAppPermission();
            }
        };

        btn_record=findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAppPermission()){
                    requstAppPermission();
                }

                if(mediaRecordHelper.isRecording()){
                    mediaRecordHelper.stopRecording();
                    startState();
                }else {
                    mediaRecordHelper.startRecording();
                    stopState();
                }
            }
        });

       findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              mediaRecordHelper.playRecorder();
           }
       });

       findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(MainActivity.this,SecondActivity.class);
               startActivity(intent);

           }
       });


    }

    @Override
    public void requstAppPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA},2000);
        }
    }

    @Override
    public boolean checkAppPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onDestroy() {
        mediaRecordHelper.destoryMediaRecorder();
        mediaRecordHelper.destoryMediaPlayer();
        super.onDestroy();
    }

    @Override
    public void startState() {
        btn_record.setText("停止录音");
    }

    @Override
    public void stopState() {
        btn_record.setText("开始录音");
    }
}
