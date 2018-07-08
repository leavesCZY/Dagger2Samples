package com.leavesc.dagger2samples;

import android.app.Application;

import com.leavesc.dagger2samples.test3.ApplicationComponent;
import com.leavesc.dagger2samples.test3.ApplicationModule;
import com.leavesc.dagger2samples.test3.DaggerApplicationComponent;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:45
 * 描述：
 */
public class RealApplication extends Application {

    public static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }

}
