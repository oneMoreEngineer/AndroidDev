package edu.sjsu.android.threadeddownload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThreadedDownloadActivity extends Activity {

    ImageView image;
    EditText editText;
    ProgressDialog progressDialog;
    Bitmap myBitmat = null;

    Handler runHandler = new Handler();
    Runnable foregroundRunnable = new Runnable(){
        @Override
        public void run() {
            if (myBitmat != null){
                image.setImageBitmap(myBitmat);

            } else {
                Toast.makeText(getApplicationContext(), "Runnable download failed, wrong link",
                        Toast.LENGTH_SHORT).show();
            }
            myBitmat = null;
            progressDialog.dismiss();
        }
    };

    private Runnable background_task = new Runnable() {
        @Override
        public void run() {
            try{
                myBitmat = downloadBitmap(editText.getText().toString());
                //myBitmat = downloadBitmap("https://media.istockphoto.com/photos/red-apple-with-leaf-isolated-on-white-background-picture-id185262648?b=1&k=20&m=185262648&s=170667a&w=0&h=2ouM2rkF5oBplBmZdqs3hSOdBzA4mcGNCoF2P0KUMTM=");
            } catch(Exception e){
                e.printStackTrace();
            }
            runHandler.post(foregroundRunnable);
        }
    };

    Handler mesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (myBitmat != null){
                image.setImageBitmap(myBitmat);

            } else {
                Toast.makeText(getApplicationContext(), "Messages download failed, wrong link",
                        Toast.LENGTH_SHORT).show();
            }
            myBitmat = null;
            progressDialog.dismiss();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Download");
        editText = (EditText) findViewById(R.id.editTextURL);
    }

    Bitmap downloadBitmap (String url) throws IOException {
        URL real_url = new URL(url);
        HttpURLConnection urlC = (HttpURLConnection) real_url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlC.getInputStream());
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap;
        } finally {
            urlC.disconnect();
        }
    }

    public void runRunnable(View view) {
        // ... you fill in here
        progressDialog.setMessage("downloading via Runnable");
        progressDialog.show();

        Thread myThread1 = new Thread(background_task, "backAlias1");
        myThread1.start();


    }

    public void runMessages(View view) {
        // ... you fill in here
        progressDialog.setMessage("downloading via Messages");
        progressDialog.show();

        Thread backJob = new Thread (new Runnable (){
            @Override
            public void run() {
                try{
                    myBitmat = downloadBitmap(editText.getText().toString());
                    //myBitmat = downloadBitmap("https://media.istockphoto.com/photos/red-apple-with-leaf-isolated-on-white-background-picture-id185262648?b=1&k=20&m=185262648&s=170667a&w=0&h=2ouM2rkF5oBplBmZdqs3hSOdBzA4mcGNCoF2P0KUMTM=");
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message msg = mesHandler.obtainMessage(0, myBitmat);
                mesHandler.sendMessage(msg);
            }
        });
        backJob.start();
    }

    private class imageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        protected void onPreExecute() {
            progressDialog.setMessage("downloading via Async");
            progressDialog.show();
        }
        protected Bitmap doInBackground(final Void... args) {
            try{
                myBitmat = downloadBitmap(editText.getText().toString());
                //myBitmat = downloadBitmap("https://media.istockphoto.com/photos/red-apple-with-leaf-isolated-on-white-background-picture-id185262648?b=1&k=20&m=185262648&s=170667a&w=0&h=2ouM2rkF5oBplBmZdqs3hSOdBzA4mcGNCoF2P0KUMTM=");

            } catch(Exception e){
                e.printStackTrace();
            }
            return myBitmat;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                image.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Async download failed, wrong link",
                        Toast.LENGTH_SHORT).show();
            }
            myBitmat = null;
            progressDialog.dismiss();
        }
    }

    public void runAsyncTask(View view) {
        // ... you fill in here
        new imageAsyncTask().execute();
    }

    public void resetImage(View view) {

        image.setImageResource(R.drawable.apple);
    }
}