package com.example.android.healthmonitor;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mHeartSensor;
   // private boolean UIfilled = false;
    private final int REQUEST_PERMISSION_BODY_SENSOR=1;
    private GoogleApiClient mGoogleApiClient;
    private String LOG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("On commence");
                //UIfilled=true;
                startMeasure();
            }
        });
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mHeartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        startMeasure();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMesure();
    }

    protected void startMeasure() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.BODY_SENSORS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //如果我们没有获得权限
            Log.d("Permission Status:", "permission NO ");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS)) {
                //如果如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
                //ActivityCompat.showExplanation("Permission Needed", "Rationale", Manifest.permission.BODY_SENSORS, REQUEST_PERMISSION_BODY_SENSOR);
            } else {
                //ActivityCompat.requestPermission(this,Manifest.permission.BODY_SENSORS, REQUEST_PERMISSION_BODY_SENSOR);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                        REQUEST_PERMISSION_BODY_SENSOR);
            }

        }

        //注册一个监听器，以SENSOR_DELAY_NORMAL频率进行监听
        boolean sensorRegistered = mSensorManager.registerListener(mSensorEventListener, mHeartSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("Sensor Status:", "Sensor registered: " + (sensorRegistered ? "yes" : "no"));
        mGoogleApiClient.connect();
    }

    //停止测量，注销侦听器
    protected void stopMesure() {
        mSensorManager.unregisterListener(mSensorEventListener);
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //当传感器精度发生变化时，一般不用改动
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            final SensorEvent event1=event;
            //当传感器感应的值发生变化时回调
            String heartRate = Float.toString(event.values[0]);
            mTextView.setText(heartRate);

            PutDataMapRequest putDataMapRequest =  PutDataMapRequest.create("/heart-rate");
            putDataMapRequest.getDataMap().putString("heartrate", heartRate);

            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>(){
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            // 这里的isSuccess只是检查是否正确的存在了本地，并不确保已经送达，在设备未连接时，会保存在本地，在未来配对后再上传
                            if(!dataItemResult.getStatus().isSuccess()){
                                Log.d(LOG,"dataitem send");
                            }else{
                                Log.d(LOG,"dataitem unsend");
                            }

                        }
                    });

        }
    };
    @Override
    public void onConnectionSuspended(int i) {
        //当googleapi连接终止时，调用此方法

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //当连接出现故障时，调用此方法

    }

}
