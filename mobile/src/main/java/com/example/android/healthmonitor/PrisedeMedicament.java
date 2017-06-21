package com.example.android.healthmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrisedeMedicament extends AppCompatActivity {
    private  String URL2load;
    //Webview defined in the class because methode onKeyDown need this variable
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prisede_medicament);
        Intent intentCaught = getIntent();
        if (intentCaught.hasExtra(Intent.EXTRA_TEXT))
        {
            //  If the Intent contains the correct extra, retrieve the text
            URL2load = intentCaught.getStringExtra(Intent.EXTRA_TEXT);}

        myWebView = (WebView) findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(URL2load);
    }
}
