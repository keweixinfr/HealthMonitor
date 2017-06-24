package com.example.android.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
//    private Button mPrendreMedicamentButton;
//    private Button mRapporterEffetButton;
//    public static final String PRISE_DE_MEDICAMENT = "https://s4proj15.ddns.net/index.php/928927?lang=fr";
//    public static final String RAPPORTRE_UN_EFFET = "https://s4proj15.ddns.net/index.php/534181?lang=fr";
    private TextView mSearchResult;
    private EditText mSearchObject;
    private ImageButton mSearchButton;
    private ProgressBar mLoadingIndicator;
    String SEARCHURL = "https://s4proj15.ddns.net/request_surveyID.php?token=";
    String intentTokenPath = "com.example.android.healthmonitor.usertoken";
    private String token;

    @Override
    protected void onRestart() {
        super.onRestart();
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mSearchButton.setVisibility(View.VISIBLE);
        mSearchResult.setVisibility(View.VISIBLE);
        mSearchObject.setVisibility(View.VISIBLE);
        mSearchButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchResult =(TextView) findViewById(R.id.tv_hint);
        mSearchObject = (EditText) findViewById(R.id.et_token);
        mSearchButton = (ImageButton) findViewById(R.id.bt_confirm);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                token = mSearchObject.getText().toString();
                // check there is a token in the edittext
                if (mSearchObject.getText().length()!=0){
                    mSearchResult.setText("");
                    new RequestServer().execute(SEARCHURL + token);
                    //setEnabled is to make sure the patient won't click twice
                    mSearchButton.setEnabled(false);

                }
                else {
                    mSearchResult.setText("Merci de donner un token!");
                }
            }
        });





//        mPrendreMedicamentButton = (Button) findViewById(R.id.prise_button);
//        mRapporterEffetButton = (Button) findViewById(R.id.rapport_button);
//
//        mPrendreMedicamentButton.setOnClickListener( new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Context context = MainActivity.this;
//                Class destinationActivity = PrisedeMedicament.class;
//                Intent startChildActivityIntent = new Intent(context, destinationActivity);
//                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, PRISE_DE_MEDICAMENT);
//                startActivity(startChildActivityIntent);
//            }
//        });
//        mRapporterEffetButton.setOnClickListener( new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Context context = MainActivity.this;
//                Class destinationActivity = LoadWebsite.class;
//                Intent startChildActivityIntent = new Intent(context, destinationActivity);
//                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, RAPPORTRE_UN_EFFET);
//                startActivity(startChildActivityIntent);
//            }
//        });

    }



    private class RequestServer extends AsyncTask<String, Void, String> {
        private String requestURL;
        private String title;
        private int ID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.INVISIBLE);
            mSearchResult.setVisibility(View.INVISIBLE);
            mSearchObject.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            //Send the Http request
            requestURL = params[0];

            URL urlRequest = null;
            try {
                urlRequest = new URL(requestURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) urlRequest.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String jsonStr = s;

            //TODO: need deal with the case when Token is wrong, if wrong token, s == "[]", when we should do
            if (jsonStr.length() <=4){
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mSearchButton.setVisibility(View.VISIBLE);
                mSearchResult.setVisibility(View.VISIBLE);
                mSearchObject.setVisibility(View.VISIBLE);
                mSearchResult.setText("Votre jeton n'est pas correct, merci de donner un autre jeton");
                mSearchButton.setEnabled(true);
            }else{
                Context context = MainActivity.this;
                Class destinationActivity = ChoseSurvey.class;
                Intent startChildActivityIntent = new Intent(context, destinationActivity);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, s);
                startChildActivityIntent.putExtra(intentTokenPath,token);
                startActivity(startChildActivityIntent);

            }
        }
    }

}
