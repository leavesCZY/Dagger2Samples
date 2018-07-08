package com.leavesc.dagger2samples;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leavesc.dagger2samples.test3.DaggerActivityComponent;

import javax.inject.Inject;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "Main3Activity";

    @Inject
    LocationManager locationManager;

    @Inject
    LocationManager locationManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerActivityComponent.builder().applicationComponent(RealApplication.applicationComponent).build().inject(this);
        Log.e(TAG, "locationManager: " + locationManager);
        Log.e(TAG, "locationManager: " + locationManager1);
    }

}