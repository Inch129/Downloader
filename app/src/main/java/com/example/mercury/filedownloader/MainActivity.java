package com.example.mercury.filedownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends Activity {

    private Button startBtn;
    private ProgressBar progressBar;
    private TextView mStatus;

    Bitmap bitmap;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        startBtn = (Button) findViewById(R.id.download);
        mStatus = (TextView) findViewById(R.id.statusText);
        img = (ImageView) findViewById(R.id.imageView);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
    }

    private void startDownload() {
        String url = getString(R.string.url_picture);
        new DownloadFileAsync().execute(url);
    }


    class DownloadFileAsync extends AsyncTask<String, String, Bitmap> {

        InputStream input;
        OutputStream output;

        @Override
        protected Bitmap doInBackground(String... params) {
            int count;
            try {

                URL url = new URL(params[0]);
                URLConnection connect = url.openConnection();
                connect.connect();

                int lengthOfFile = connect.getContentLength();
                input = new BufferedInputStream(url.openStream());
                output = new FileOutputStream("sdcard/downloaded_photo.jpg");


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    Thread.sleep(100);
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (output != null) output.close();
                    input.close();
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            bitmap = BitmapFactory.decodeFile("sdcard/downloaded_photo.jpg");

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mStatus.setText("Status:Downloaded.");
                progressBar.setVisibility(progressBar.INVISIBLE);
                img.setImageBitmap(result);
                startBtn.setEnabled(true);
            } else {
                Toast.makeText(MainActivity.this, "Неполадки с сетью, или картинка отсутствует.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(progressBar.VISIBLE);
            mStatus.setText("Status:Idle.");
            startBtn.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mStatus.setText("Status:Downloading...");
            progressBar.setProgress(Integer.parseInt(values[0]));
            startBtn.setEnabled(false);
        }

    }


}

