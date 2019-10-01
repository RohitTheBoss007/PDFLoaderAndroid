package com.example.android.pdfloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView=findViewById(R.id.pdf);
        File file = new File(getIntent().getStringExtra("file"));
        pdfView.fromFile(file).enableSwipe(true).swipeHorizontal(false)
                .enableAntialiasing(true)
                .spacing(0)
                .load();
    }
}
