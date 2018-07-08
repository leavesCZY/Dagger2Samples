package com.leavesc.dagger2samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leavesc.dagger2samples.test1.DaggerPersonComponent;
import com.leavesc.dagger2samples.test1.Person;

import javax.inject.Inject;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Inject
    Person person1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerPersonComponent.builder().build().inject(this);
        Log.e(TAG, "person1: " + person1);
        Log.e(TAG, "person1 name : " + person1.getName());
        startActivity(new Intent(this, Main2Activity.class));
    }

}
