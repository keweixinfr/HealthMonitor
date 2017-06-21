package com.example.android.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mPrendreMedicamentButton;
    private Button mRapporterEffetButton;
    public static final String PRISE_DE_MEDICAMENT = "https://s4proj15.ddns.net/index.php/928927?lang=fr";
    public static final String RAPPORTRE_UN_EFFET = "https://s4proj15.ddns.net/index.php/534181?lang=fr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrendreMedicamentButton = (Button) findViewById(R.id.prise_button);
        mRapporterEffetButton = (Button) findViewById(R.id.rapport_button);
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

    }
}
