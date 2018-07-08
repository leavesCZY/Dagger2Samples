package com.leavesc.dagger2samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leavesc.dagger2samples.test2.DaggerUserComponent;
import com.leavesc.dagger2samples.test2.User;
import com.leavesc.dagger2samples.test2.UserModule;
import com.leavesc.dagger2samples.test4.UserWithParameter;
import com.leavesc.dagger2samples.test4.UserWithoutParameter;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    //@Named("no empty")
    @UserWithParameter
    User user1;

    @Inject
    //@Named("empty")
    @UserWithoutParameter
    User user2;

    @Inject
    Lazy<User> user3;

    @Inject
    Provider<User> user4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);
        Log.e(TAG, "user1: " + user1);
        Log.e(TAG, "user1 name : " + user1.getName());
        Log.e(TAG, "user2: " + user2);
        Log.e(TAG, "user2 name : " + user2.getName());

        User user = user3.get();
        Log.e(TAG, "user3-1: " + user);
        Log.e(TAG, "user3 name-1 : " + user.getName());
        user = user3.get();
        Log.e(TAG, "user3-2: " + user);
        Log.e(TAG, "user3 name-2 : " + user.getName());
        user = user3.get();
        Log.e(TAG, "user3-2: " + user);
        Log.e(TAG, "user3 name-2 : " + user.getName());

        user = user4.get();
        Log.e(TAG, "user4-1: " + user);
        Log.e(TAG, "user4 name-1 : " + user.getName());
        user = user4.get();
        Log.e(TAG, "user4-2: " + user);
        Log.e(TAG, "user4 name-2 : " + user.getName());
        user = user4.get();
        Log.e(TAG, "user4-2: " + user);
        Log.e(TAG, "user4 name-2 : " + user.getName());

        startActivity(new Intent(this, Main3Activity.class));
    }

}
