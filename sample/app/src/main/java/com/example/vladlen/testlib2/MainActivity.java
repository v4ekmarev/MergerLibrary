package com.example.vladlen.testlib2;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.vladlen.mergerlibrary.Merger;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    File folder = Environment.getExternalStorageDirectory();
    final String path = folder.getAbsolutePath() + "/test";
    private Button button;
    private String mp4Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonShare);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                intent.setType("mp4/*")
//                intent.setData(Uri.parse(s));
                intent.setType("video/mp4");
                File file = new File(mp4Path);
                Uri uri = Uri.fromFile(file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
            }
        });
        Merger.initFFmpeg(getApplicationContext());


        RxPermissions.getInstance(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) { // will emit 2 Permission objects
                        if (permission.granted) {
                            // `permission.name` is granted !
                            Merger.mergeImgAndAudio(path + "/trololo.jpeg", path + "/sranina.png", path + "/armia_tmy_n_apodhode.mp3", 90, 175, getApplicationContext(), new Merger.Callback() {
                                @Override
                                public void onSucces(String s) {
                                    Log.d("TAG", s);
                                    button.setEnabled(true);
                                    mp4Path = s;
                                }
                            });
                        }
                    }
                });
//
//        Merger.mergeImgAndAudio(path + "/trololo.jpeg", path + "/sranina.png", path + "/armia_tmy_n_apodhode.mp3", 90, 175, getApplicationContext(), new Merger.Callback() {
//            @Override
//            public void onSucces(String s) {
//                Log.d("TAG", s);
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
////                intent.setType("mp4/*")
////                intent.setData(Uri.parse(s));
//                intent.setType("video/mp4");
//                File file = new File(s);
//                Uri uri = Uri.fromFile(file);
//                intent.putExtra(Intent.EXTRA_STREAM, uri);
//                startActivity(intent);
//            }
//        });
    }
}
