package com.example.android.healthmonitor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class RapporterEffet extends AppCompatActivity implements DataApi.DataListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String URL2load;
    //Webview defined in the class because methode onKeyDown need this variable
    private String mHeartRate;
    private GoogleApiClient mGoogleApiClient;
    private boolean mWearableConnected = false;
    private boolean receivedRate = false;
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prisede_medicament);
        Intent intentCaught = getIntent();
        if (intentCaught.hasExtra(Intent.EXTRA_TEXT)) {
            //  If the Intent contains the correct extra, retrieve the text
            URL2load = intentCaught.getStringExtra(Intent.EXTRA_TEXT);
        }

        myWebView = (WebView) findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(URL2load);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    //creat menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);//这里的main指的是menu的xml文件的名字
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_copy) {
            Context context = RapporterEffet.this;
            checkNodeAPI();
            String textToShow;
            if (mWearableConnected) {
                if (receivedRate) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("HeartRate", mHeartRate);
                    clipboard.setPrimaryClip(clip);
                    textToShow = "Heart Rate Copied";
                } else {
                    textToShow = "Android Wear est en cours de synchronisation";
                }
            } else {
                textToShow = "Vous n'avez pas d'android wear.";
            }
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //check if there is an android wear
    private void checkNodeAPI() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult != null && getConnectedNodesResult.getNodes() != null) {
                    mWearableConnected = false;
                    for (Node node : getConnectedNodesResult.getNodes()) {
                        if (node.isNearby()) {
                            mWearableConnected = true;

                        }
                    }
                }
                Log.v("myTag", "mWearableConnected: " + mWearableConnected);
            }

        }, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        //mTextView.setText("START");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //当googleapi连接被建立时，调用此方法
        Log.d("myTag:", "Connection established");

        //mTextView.setText("Try Connection");
        Wearable.DataApi.addListener(mGoogleApiClient, this).setResultCallback(new ResultCallback<Status>() {
            //后半部分是为了判断创建情况的
            @Override
            public void onResult(Status status) {
                Log.d("myTag", String.valueOf(status));
                //mTextView.setText("Connected");
            }
        });

    }


    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        //mTextView.setText("Data changed");
        Log.d("myTag", "in on Data Changed");
        //mTextView.setText("Datachanged");
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                //相应的还有一种是type_deleted
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/heart-rate") == 0) {
                    receivedRate = true;
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    mHeartRate = dataMap.getString("heartrate");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            receivedRate = false;
            Log.d("myTag", "Connection stoped");
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            receivedRate = false;
            Log.d("myTag", "Connection stoped");
        }
        super.onPause();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //当googleapi连接终止时，调用此方法

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //当连接出现故障时，调用此方法

    }
}
