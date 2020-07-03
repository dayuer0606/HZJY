package com.android.jwjy.zkktproduct;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by KaelLi on 2018/11/22.
 */
public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_layout);
        pdfView = findViewById(R.id.pdfview);
        Intent intent = getIntent();
        String nameString = intent.getStringExtra("url"); // "https://download.brother.com/welcome/docp000648/cv_pt3600_schn_sig_lad962001.pdf"
        pdfView.loadOnlinePDF(nameString);

    }
}
