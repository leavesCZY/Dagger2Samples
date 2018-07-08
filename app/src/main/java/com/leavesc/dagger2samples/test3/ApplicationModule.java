package com.leavesc.dagger2samples.test3;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:25
 * 描述：
 */
@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

}