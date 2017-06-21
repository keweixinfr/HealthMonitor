package com.example.android.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button mPrendreMedicamentButton;
    private Button mRapporterEffetButton;
    public static final String PRISE_DE_MEDICAMENT = "https://s4proj15.ddns.net/index.php/928927?lang=fr";
    public static final String RAPPORTRE_UN_EFFET = "https://s4proj15.ddns.net/index.php/534181?lang=fr";
    private Button mConfirmButton;
    private String mJson;
    private EditText mToken;
    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrendreMedicamentButton = (Button) findViewById(R.id.prise_button);
        mRapporterEffetButton = (Button) findViewById(R.id.rapport_button);
        mToken =(EditText) findViewById(R.id.et_token) ;
        mResult = (TextView) findViewById(R.id.tv_result) ;
        mConfirmButton =(Button) findViewById(R.id.bt_token);
        mPrendreMedicamentButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                Class destinationActivity = PrisedeMedicament.class;
                Intent startChildActivityIntent = new Intent(context, destinationActivity);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, PRISE_DE_MEDICAMENT);
                startActivity(startChildActivityIntent);
            }
        });
        mRapporterEffetButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                Class destinationActivity = RapporterEffet.class;
                Intent startChildActivityIntent = new Intent(context, destinationActivity);
                startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, RAPPORTRE_UN_EFFET);
                startActivity(startChildActivityIntent);
            }
        });
        mConfirmButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mToken.getText() != null){
                    String token = mToken.getText().toString();
                    new BackgroundTask(token).execute();

                }
                else {
                    Context context = getApplicationContext();
                    String textToShow = "Merci de donner votre jeton";
                    Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class BackgroundTask extends AsyncTask<Void, Void, String> {

        String json_url;

        public BackgroundTask(String token) {
            super();
            json_url = "https://s4proj15.ddns.net/request_surveyID.php?token=" + token;

        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((mJson = bufferedReader.readLine())!=null) {

                    stringBuilder.append(mJson + "\n");

                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            mResult.setText(result);


        }
    }
}
