package com.leavesc.dagger2samples.test3;

import android.content.Context;
import android.location.LocationManager;

import dagger.Module;
import dagger.Provides;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:27
 * 描述：
 */
@Module
public class ActivityModule {

    @Provides
    LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

}