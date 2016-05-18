package com.example.vladlen.mergerlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by vladlen on 17.05.16.
 */

public class Merger {

    private static final String TAG = "merger";
    private static File folder = Environment.getExternalStorageDirectory();
    private static final String outPath = folder.getAbsolutePath();

    public static void mergeImgAndAudio(final String botImgPath, final String topImgPath, final String audioPath, final float leftPadding, final float topPadding, final Context context, final Callback callback) {

        Runnable run = new Runnable() {
            @Override
            public void run() {
                String imgPath = mergeTwoImg(botImgPath, topImgPath, leftPadding, topPadding);
                try {
                    final String res = runProcess(context.getApplicationInfo().dataDir + "/ffmpeg", audioPath, imgPath);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSucces(res);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(run).start();
    }

    /**
     * @param botImgPath  a picture background
     * @param topImgPath  left and top padding move this img
     * @param leftPadding example 90
     * @param topPadding  example 175
     * @return
     */
    private static String mergeTwoImg(String botImgPath, String topImgPath, float leftPadding, float topPadding) {
        Bitmap bottomImage = BitmapFactory.decodeFile(botImgPath);
        Bitmap topImage = BitmapFactory.decodeFile(topImgPath);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(bottomImage.getWidth(), bottomImage.getHeight(), conf); // this creates a MUTABLE bitmap

        Canvas comboImage = new Canvas(bmp);

        comboImage.drawBitmap(bottomImage, 0f, 0f, null);
        comboImage.drawBitmap(topImage, leftPadding, topPadding, null);

        OutputStream os = null;
        try {
            os = new FileOutputStream(outPath + "/mergingImg.png");
            bmp.compress(Bitmap.CompressFormat.PNG, 50, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPath + "/mergingImg.png";
    }

    public static void initFFmpeg(Context context) {
        copyFileAndMakeExec("ffmpeg", context.getApplicationInfo().dataDir + "/ffmpeg", context.getApplicationContext());
    }

    private static void copyFileAndMakeExec(String assetPath, String ffmpegPath, Context context) {
        File f = new File(ffmpegPath);
        if (!f.exists()) {
            try {
                InputStream in = context.getAssets().open(assetPath);
                FileOutputStream out = new FileOutputStream(ffmpegPath);
                int read;
                byte[] buffer = new byte[4096];
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
                out.close();
                in.close();

                Boolean isOk = f.setExecutable(true);
                Log.d(TAG, isOk.toString());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String runProcess(String ffmpegPath, String audioPath, String imgPath) throws IOException, InterruptedException {
        Log.d(TAG, "merging started");
        String videoPath = outPath + "/mergerVideo.mp4";
        Process nativeApp = Runtime.getRuntime().exec(ffmpegPath + " -loop 1 -r 30 -i " + imgPath + " -i " + audioPath + " -shortest -c:v libx264 -pix_fmt yuvj420p -preset veryfast " + videoPath);
        StringBuffer output = new StringBuffer();

        InputStream stdin = nativeApp.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        final BufferedReader stdErr = new BufferedReader(new InputStreamReader(nativeApp.getErrorStream()));

        String line = null;
        System.out.println("<OUTPUT>");
        while ((line = br.readLine()) != null) {
            output.append(line);
            Log.d(TAG, line);
        }
        // System.out.println("</OUTPUT>");
        while ((line = stdErr.readLine()) != null) {
            output.append(line);
            Log.d(TAG, line);
        }

        // Waits for the command to finish.
        nativeApp.waitFor();

        // String nativeOutput = output.toString();

        Log.d(TAG, "merging ended");
        //return nativeOutput;
        return videoPath;
    }

    public interface Callback {
        void onSucces(String videoPath);
    }
}
