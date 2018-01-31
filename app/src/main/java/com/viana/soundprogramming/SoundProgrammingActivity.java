package com.viana.soundprogramming;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import topcodes.Scanner;
import topcodes.TopCode;

public class SoundProgrammingActivity extends AppCompatActivity {

    private Camera camera;
    private SurfaceView cameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_programming);
        this.cameraSurfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner();
                Bitmap bitmap = getBitmap();
                if (bitmap != null) {
                    List<TopCode> topCodes = scanner.scan(bitmap);
                    Log.i("TopCodes", String.valueOf(topCodes.size()));
                }
            }
        }).start();
    }

    public Bitmap getBitmap() {
        try {
            URL url = new URL("https://fivedots.coe.psu.ac.th/~ad/jg/nui065/codes.jpg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
