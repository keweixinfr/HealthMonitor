package com.example.android.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ChoseSurvey extends AppCompatActivity {
    private String jsonResponse;
    private ListView mSurveyList;
    List<String> surveyNames = new ArrayList<String>();
    List<Integer> surveyID = new ArrayList<Integer>();
    private String destinationURL;
    String intentTokenPath = "com.example.android.healthmonitor.usertoken";
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_survey);
        Intent intentCaught = getIntent();
        if (intentCaught.hasExtra(intentTokenPath)) {
            token=intentCaught.getStringExtra(intentTokenPath);
        }
        if (intentCaught.hasExtra(Intent.EXTRA_TEXT))
        {
            //  If the Intent contains the correct extra, retrieve the text
            jsonResponse = intentCaught.getStringExtra(Intent.EXTRA_TEXT);
            Gson gson = new Gson();
            List<Jsondataformat> surveyList = gson.fromJson(jsonResponse, new TypeToken<List<Jsondataformat>>() {
            }.getType());
            for (int i = 0; i < surveyList.size(); i++) {
                surveyNames.add(surveyList.get(i).title);
                surveyID.add(new Integer(surveyList.get(i).ID));
            }
            mSurveyList = (ListView) findViewById(R.id.lv_surveys);
            mSurveyList.setOnItemClickListener(mMessageClickedHandler);
            mSurveyList.setAdapter(new ArrayAdapter<String>(ChoseSurvey.this, android.R.layout.simple_expandable_list_item_1,surveyNames));

        }
    }
    private class Jsondataformat {
        int ID;
        String title;
    }
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            //Toast.makeText(ChoseSurvey.this,surveyNames.get((int) id), Toast.LENGTH_SHORT).show();
            Context context = ChoseSurvey.this;
            Class destinationActivity = LoadWebsite.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);
            destinationURL = "https://s4proj15.ddns.net/index.php/"+surveyID.get((int)id);
            startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, destinationURL);
            startChildActivityIntent.putExtra(intentTokenPath, token);
            startActivity(startChildActivityIntent);
        }
    };
}
