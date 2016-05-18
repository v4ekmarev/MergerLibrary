package com.example.vladlen.testlib2;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.vladlen.trololo_simple_two.Merger;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    File folder = Environment.getExternalStorageDirectory();
    final String path = folder.getAbsolutePath() + "/test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Merger.initFFmpeg(getApplicationContext());

        Merger.mergeImgAndAudio(path + "/trololo.jpeg", path + "/sranina.png", path + "/armia_tmy_n_apodhode.mp3", 90, 175, getApplicationContext(), new Merger.Callback() {
            @Override
            public void onSucces(String s) {
                Log.d("TAG", s);
            }
        });
    }
}
