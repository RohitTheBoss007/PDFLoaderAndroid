package com.example.android.pdfloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    SeekBar seekBar;
    PDFView pdfView;
    Button load;

    private final String PDF_LINK="http://ancestralauthor.com/download/sample.pdf";
    final String MY_PDF=md5(PDF_LINK);

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pdfView=findViewById(R.id.pdfView);
        textView=findViewById(R.id.textview);
        load=findViewById(R.id.btnLoad);
        initSeekbar();
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadPDF(MY_PDF);
            }
        });

//        new RetrievePDFStream().execute("http://ancestralauthor.com/download/sample.pdf");
    }

    @SuppressLint("StaticFieldLeak")
    private void LoadPDF(final String myfilename) {

        new AsyncTask<Void,Integer,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return downloadPDF();
            }

            Boolean downloadPDF()
            {
                try{
                    File file=getFileStreamPath(myfilename);
                    if(file.exists())
                        return true;

                    try {
                        FileOutputStream fileOutputStream = openFileOutput(myfilename, Context.MODE_PRIVATE);
                        URL u = new URL(PDF_LINK);
                        URLConnection con = u.openConnection();
                        int contentLength = con.getContentLength();
                        InputStream input = new BufferedInputStream(u.openStream());
                        byte data[] = new byte[contentLength];
                        long total = 0;
                        int count;
                        while ((count = input.read(data))!= -1) {
                            total += count;
                            publishProgress((int) ((total * 100) / contentLength));
                            fileOutputStream.write(data, 0, count);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        input.close();
                        return true;
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                        return false;
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                seekBar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean)
                {
                    openPDF(myfilename);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Unable to download this pdf",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    void openPDF(String filename)
    {
        try {
            File file=getFileStreamPath(filename);
            load.setText("Open");
            Intent intent=new Intent(MainActivity.this,PDFActivity.class);
            intent.putExtra("file", file.toString());
            startActivity(intent);
//            seekBar.setVisibility(View.GONE);
//            load.setVisibility(View.GONE);
//            pdfView.setVisibility(View.VISIBLE);
//            pdfView.fromFile(file).enableSwipe(true).swipeHorizontal(false)
//                    .enableAntialiasing(true)
//                    .spacing(0)
//                    .load();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initSeekbar() {
        seekBar=findViewById(R.id.seekbar);
        seekBar.getProgressDrawable().setColorFilter(Color.RED,PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.RED,PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val=(progress*(seekBar.getWidth()-3*seekBar.getThumbOffset()))/seekBar.getMax();
                textView.setText(""+progress);
                textView.setX(seekBar.getX()+val+seekBar.getThumbOffset()/2);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
//    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>
//    {
//
//        @Override
//        protected InputStream doInBackground(String... strings) {
//            InputStream inputStream=null;
//
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                if (urlConnection.getResponseCode() == 200) {
//                    File file;
//                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                }
//            }
//            catch (IOException e){
//                return null;
//            }
//
//            return inputStream;
//
//
//
//        }
//
//        @Override
//        protected void onPostExecute(InputStream inputStream) {
//            pdfView.fromStream(inputStream).load();
//        }
//    }
}
