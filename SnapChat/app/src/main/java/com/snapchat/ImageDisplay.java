package com.snapchat;

import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDisplay extends Activity {

    ImageView image;
    TextView text;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedisplay);

        String url = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }
        image = (ImageView) findViewById(R.id.display_image);
        text = (TextView) findViewById(R.id.text_Timer);
        text.setTextSize(35f);
        text.setTypeface(null, Typeface.BOLD);
        text.setVisibility(View.INVISIBLE);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading Image");
        DownloadImage task = new DownloadImage(dialog, ImageDisplay.this);
        task.execute(url);
    }

    public void Set_image(Bitmap bitmap) {
        text.setVisibility(View.VISIBLE);
        image.setImageBitmap(bitmap);

        Inbox_Activity.timer.start();
        timer = new CountDownTimer(10000, 100) {
            int secondsLeft = 0;

            public void onTick(long ms) {
                if (Math.round((float) ms / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) ms / 1000.0f);
                    text.setText("0 : " + secondsLeft);
                }
//		         Log.i("test","ms="+ms+" till finished="+secondsLeft );
            }

            public void onFinish() {
                text.setText("0 : 0");
                finish();
            }
        }.start();
    }

}

//DownloadImage AsyncTask
class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    ProgressDialog Pdialog;
    ImageDisplay Idisplay;

    DownloadImage(ProgressDialog dialog, ImageDisplay display) {
        Pdialog = dialog;
        Idisplay = display;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Pdialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Idisplay.Set_image(result);
        Pdialog.dismiss();
    }
}

