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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
//    private Button mPrendreMedicamentButton;
//    private Button mRapporterEffetButton;
//    public static final String PRISE_DE_MEDICAMENT = "https://s4proj15.ddns.net/index.php/928927?lang=fr";
//    public static final String RAPPORTRE_UN_EFFET = "https://s4proj15.ddns.net/index.php/534181?lang=fr";
    private TextView mSearchResult;
    private EditText mSearchObject;
    private Button mSearchButton;
    String SEARCHURL = "https://s4proj15.ddns.net/request_surveyID.php?token=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchResult =(TextView) findViewById(R.id.tv_hint);
        mSearchObject = (EditText) findViewById(R.id.et_token);
        mSearchButton = (Button) findViewById(R.id.bt_confirm);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchObject = mSearchObject.getText().toString();
                // check there is a token in the edittext
                if (mSearchObject.getText().length()!=0){
                    String token = mSearchObject.getText().toString();
                    new RequestServer().execute(SEARCHURL + searchObject);
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
//                Class destinationActivity = RapporterEffet.class;
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
                mSearchResult.setText("Votre jeton n'est pas correct, merci de donner un autre jeton");
                mSearchButton.setEnabled(true);
            }else{
                Context context = MainActivity.this;
                Class destinationActivity = ChoseSurvey.class;
                Intent startChildActivityIntent = new Intent(context, destinationActivity);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, s);
                startActivity(startChildActivityIntent);

            }
        }
    }

}
